package br.com.gomesar.assembleia.application.services.voto

import br.com.gomesar.assembleia.application.commons.ApiErrorCode
import br.com.gomesar.assembleia.application.integration.exceptions.UsuarioNaoEncontradoException
import br.com.gomesar.assembleia.application.integration.exceptions.UsuarioServiceIndisponivelException
import br.com.gomesar.assembleia.application.integration.request.UserIntegrationRequest
import br.com.gomesar.assembleia.application.integration.response.EStatusUsuarioVotacao
import br.com.gomesar.assembleia.application.integration.service.IUserIntegrationService
import br.com.gomesar.assembleia.application.services.bundle.IMessageBundleService
import br.com.gomesar.assembleia.application.services.sessao.SessaoService
import br.com.gomesar.assembleia.application.services.voto.exceptions.SessaoNaoCadastradaException
import br.com.gomesar.assembleia.application.services.voto.exceptions.SessaoVotoJaEncerradaException
import br.com.gomesar.assembleia.application.services.voto.exceptions.UsuarioSemPermissaoPraVotarException
import br.com.gomesar.assembleia.application.services.voto.exceptions.UsuarioVotoJaComputadoException
import br.com.gomesar.assembleia.domain.dto.VotoDto
import br.com.gomesar.assembleia.domain.entities.EStatusVotacao
import br.com.gomesar.assembleia.domain.entities.SessaoControle
import br.com.gomesar.assembleia.domain.entities.VotoControle
import br.com.gomesar.assembleia.domain.repositories.ISessaoRepository
import br.com.gomesar.assembleia.domain.repositories.ISessaoControleRepository
import im.aop.loggers.Level
import im.aop.loggers.advice.after.throwing.LogAfterThrowing
import im.aop.loggers.advice.before.LogBefore
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class VotoService(
    private val userIntegrationService: IUserIntegrationService,
    private val sessaoControleRepository: ISessaoControleRepository,
    private val sessaoRepository: ISessaoRepository,
    private val bundleService: IMessageBundleService
) : IVotoService {

    @LogBefore(level = Level.INFO, declaringClass = SessaoService::class)
    @LogAfterThrowing(declaringClass = VotoService::class, ignoreExceptions = [
        UsuarioNaoEncontradoException::class,
        UsuarioServiceIndisponivelException::class
    ])
    override fun criaVoto(dto: VotoDto, pautaId: String) {
        with(dto) {
            val sessaoControle = sessaoControleRepository.findBySessaoId(pautaId)

            validaSessaoExistenteOuFinalizada(pautaId, sessaoControle)
            validaUsuarioComVotoComputado(this.usuario!!, sessaoControle!!)
            validaPermissaoDeVotoDoUsuario(this.usuario)
            sessaoControle.votos.add(VotoControle(this.respostaUsuario!!.resposta, this.usuario))
            sessaoControleRepository.save(sessaoControle)
        }
    }

    private fun validaSessaoExistenteOuFinalizada(sessaoId: String, sessaoControle: SessaoControle?) {

        if (sessaoRepository.findByPautaId(UUID.fromString(sessaoId))?.finalizada == true) {
            throw SessaoVotoJaEncerradaException(
                mapOf(
                    ApiErrorCode.ERRO_VOTO_SESSAO_JA_ENCERRADA to bundleService.getMessage(
                        ApiErrorCode.ERRO_VOTO_SESSAO_JA_ENCERRADA.codigo,
                        sessaoId
                    )
                )
            )
        }

        with(sessaoControle) {
            if (this == null || (this.statusVotacao == EStatusVotacao.FINALIZADA)) {
                if (this == null) {
                    throw SessaoNaoCadastradaException(
                        mapOf(
                            ApiErrorCode.ERRO_VOTO_SESSAO_NAO_CADASTRADA to bundleService.getMessage(
                                ApiErrorCode.ERRO_VOTO_SESSAO_NAO_CADASTRADA.codigo,
                                sessaoId
                            )
                        )
                    )
                }
                throw SessaoVotoJaEncerradaException(
                    mapOf(
                        ApiErrorCode.ERRO_VOTO_SESSAO_JA_ENCERRADA to bundleService.getMessage(
                            ApiErrorCode.ERRO_VOTO_SESSAO_JA_ENCERRADA.codigo,
                            this.id!!
                        )
                    )
                )
            }
        }
    }

    private fun validaUsuarioComVotoComputado(cpf: String, sessaoControle: SessaoControle) {
        if (sessaoControle.votos.any { it.usuario == cpf }) throw UsuarioVotoJaComputadoException(
                mapOf(
                    ApiErrorCode.ERRO_VOTO_USUARIO_JA_COMPUTADO to bundleService.getMessage(
                        ApiErrorCode.ERRO_VOTO_USUARIO_JA_COMPUTADO.codigo,
                        cpf
                    )
                )
        )
    }

    private fun validaPermissaoDeVotoDoUsuario(cpf: String) {
        with(userIntegrationService.getUserResponse(UserIntegrationRequest(cpf))) {
            if (this.status == EStatusUsuarioVotacao.UNABLE_TO_VOTE) throw UsuarioSemPermissaoPraVotarException(
                mapOf(
                    ApiErrorCode.ERRO_VOTO_USUARIO_SEM_PERMISSAO to bundleService.getMessage(
                        ApiErrorCode.ERRO_VOTO_USUARIO_SEM_PERMISSAO.codigo,
                        cpf
                    )
                )
            )
        }
    }
}