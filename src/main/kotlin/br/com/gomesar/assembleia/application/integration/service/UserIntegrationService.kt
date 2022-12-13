package br.com.gomesar.assembleia.application.integration.service

import br.com.gomesar.assembleia.application.commons.ApiErrorCode
import br.com.gomesar.assembleia.application.commons.removeCaracteresEspeciais
import br.com.gomesar.assembleia.application.integration.exceptions.UsuarioNaoEncontradoException
import br.com.gomesar.assembleia.application.integration.exceptions.UsuarioServiceIndisponivelException
import br.com.gomesar.assembleia.application.integration.request.UserIntegrationRequest
import br.com.gomesar.assembleia.application.integration.response.EStatusUsuarioVotacao
import br.com.gomesar.assembleia.application.integration.response.UserIntegrationResponse
import br.com.gomesar.assembleia.application.services.bundle.IMessageBundleService
import br.com.gomesar.assembleia.application.services.bundle.MessageBundleService
import br.com.gomesar.assembleia.application.services.pauta.PautaService
import im.aop.loggers.Level
import im.aop.loggers.advice.after.throwing.LogAfterThrowing
import im.aop.loggers.advice.before.LogBefore
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpStatusCodeException
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

@Service
class UserIntegrationService(
    private val restTemplate: RestTemplate,
    @Value("\${assembleia.user-service.scheme}") private val scheme: String,
    @Value("\${assembleia.user-service.host}") private val host: String,
    @Value("\${assembleia.user-service.path}") private val path: String,
    @Value("\${assembleia.mock-user-service}") private val mock: Boolean,
    private val messageBundleService: IMessageBundleService
) : IUserIntegrationService {

    @LogBefore(level = Level.INFO, declaringClass = UserIntegrationService::class)
    @LogAfterThrowing(declaringClass = UserIntegrationService::class)
    override fun getUserResponse(request: UserIntegrationRequest): UserIntegrationResponse {
        if (mock) {
            return UserIntegrationResponse(EStatusUsuarioVotacao.randomStatus())
        }
        val uri = UriComponentsBuilder.newInstance().scheme(scheme).host(host)
            .path("$path/${request.cpf.removeCaracteresEspeciais()}")
            .build().toUri()
        return try {
            restTemplate.getForEntity(uri, UserIntegrationResponse::class.java).body!!
        } catch (exc: HttpClientErrorException) {
            handleException(exc, request.cpf)
        }
    }

    private fun handleException(exc: HttpStatusCodeException, cpf: String): UserIntegrationResponse {
        when (exc.statusCode) {
            HttpStatus.NOT_FOUND -> throw UsuarioNaoEncontradoException(mapOf(
                ApiErrorCode.ERRO_USUARIOSERVICE_NAO_ENCONTRADO to
                        messageBundleService.getMessage(ApiErrorCode.ERRO_USUARIOSERVICE_NAO_ENCONTRADO.codigo, cpf)
            ))
            else -> throw UsuarioServiceIndisponivelException(mapOf(
                ApiErrorCode.ERRO_USUARIOSERVICE_INDISPONIVEL to
                        messageBundleService.getMessage(ApiErrorCode.ERRO_USUARIOSERVICE_INDISPONIVEL.codigo)
            ))
        }
    }

}