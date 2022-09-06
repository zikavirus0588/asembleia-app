package br.com.gomesar.assembleia.application.services

import br.com.gomesar.assembleia.application.commons.ApiError
import br.com.gomesar.assembleia.application.commons.ApiErrorCode
import br.com.gomesar.assembleia.application.commons.ApiAssembleiaResponse
import br.com.gomesar.assembleia.application.commons.toSnakeCase
import br.com.gomesar.assembleia.application.controllers.v1.sessao.exceptions.CriaSessaoPautaNaoExistenteException
import br.com.gomesar.assembleia.application.controllers.v1.sessao.exceptions.DefaultSessaoException
import br.com.gomesar.assembleia.application.controllers.v1.sessao.exceptions.SessaoNaoEncontradaException
import br.com.gomesar.assembleia.application.integration.exceptions.DefaultUserException
import br.com.gomesar.assembleia.application.integration.exceptions.UsuarioNaoEncontradoException
import br.com.gomesar.assembleia.application.integration.exceptions.UsuarioServiceIndisponivelException
import br.com.gomesar.assembleia.application.services.pauta.exceptions.DefaultPautaException
import br.com.gomesar.assembleia.application.services.pauta.exceptions.PautaJaCadastradaException
import br.com.gomesar.assembleia.application.services.pauta.exceptions.PautaNaoEncontradaException
import br.com.gomesar.assembleia.application.services.sessao.exceptions.SessaoJaCadastradaException
import br.com.gomesar.assembleia.application.services.voto.exceptions.*
import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import javax.validation.ConstraintViolationException

@ControllerAdvice
class WebServiceExceptionHandler {

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleBadRequestException(e: Exception): ApiAssembleiaResponse<Any>? {
        return when(e) {
            is ConstraintViolationException -> ApiAssembleiaResponse(null, mutableListOf(getApiError(e)))
            is MethodArgumentNotValidException -> ApiAssembleiaResponse(null, e.fieldErrors.map { ApiError(it) }.toMutableList())
            is HttpMessageNotReadableException -> handleHttpMessageNotReadbleException(e.cause)
            else -> null
        }
    }

    private fun handleHttpMessageNotReadbleException(cause: Throwable?): ApiAssembleiaResponse<Any>? {
        val apiErro = when(cause) {
            is MissingKotlinParameterException -> {
                val fieldName = cause.path.last().fieldName
                val mensagem = REQUESTBODY_NAO_INFORMADO
                ApiError(ApiErrorCode.ERRO_REQUESTBODY_NULO.codigo, mensagem, fieldName)
            }
            is InvalidFormatException -> ApiError(ApiErrorCode.ERRO_REQUESTBODY_INVALIDFORMAT.codigo,
                "Valor informado (${cause.value}) não corresponde ao tipo esperado: ${cause.targetType.simpleName}",
                cause.path.last().fieldName.toSnakeCase()
            )
            is JsonParseException -> ApiError(ApiErrorCode.ERRO_REQUESTBODY_INVALIDFORMAT.codigo,
                "Json inválido: verifique o objeto enviado na requisição"
            )
            else -> ApiError("", "")
        }
        return ApiAssembleiaResponse(null, mutableListOf(apiErro))
    }

    @ExceptionHandler(
        PautaNaoEncontradaException::class,
        UsuarioNaoEncontradoException::class,
        SessaoNaoEncontradaException::class
    )
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNotFoundException(e: Exception): ApiAssembleiaResponse<Any>? {
        return when(e) {
            is DefaultPautaException -> getResponse(e.error.codigo, e.msg)
            is DefaultUserException -> getResponse(e.error.codigo, e.msg)
            is DefaultSessaoException -> getResponse(e.error.codigo, e.msg)
            else -> null
        }
    }

    @ExceptionHandler(
        PautaJaCadastradaException::class,
        CriaSessaoPautaNaoExistenteException::class,
        SessaoJaCadastradaException::class,
        SessaoNaoCadastradaException::class,
        SessaoVotoJaEncerradaException::class,
        UsuarioSemPermissaoPraVotarException::class,
        UsuarioVotoJaComputadoException::class
    )
    @ResponseBody
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    fun handleUnprocessableEntityException(e: Exception): ApiAssembleiaResponse<Any>? {
        return when (e) {
            is DefaultPautaException -> getResponse(e.error.codigo, e.msg)
            is DefaultSessaoException -> getResponse(e.error.codigo, e.msg)
            is DefaultVotoException -> getResponse(e.error.codigo, e.msg)
            is DefaultUserException -> getResponse(e.error.codigo, e.msg)
            else -> null
        }
    }

    @ExceptionHandler(
        UsuarioServiceIndisponivelException::class
    )
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleInternalServerErrorException(e: Exception): ApiAssembleiaResponse<Any>? {
        return when(e) {
            is DefaultUserException -> getResponse(e.error.codigo, e.msg)
            else -> null
        }
    }

    private fun getResponse(codigoErro: String, mensagem: String? = "") = ApiAssembleiaResponse(
        null,
        arrayListOf(ApiError(codigoErro, mensagem!!))
    )

    private fun getApiError(e: ConstraintViolationException): ApiError {
        return with(e.constraintViolations) {
            val propertyName = this.first().propertyPath.last().name
            val message = this.first().message
            ApiError(ApiErrorCode.ERRO_REQUESTPARAM_INVALIDO.codigo, "$REQUESTPARAM_INVALIDO $message: $propertyName")
        }
    }

    companion object {
        const val REQUESTPARAM_NAO_INFORMADO = "parâmetro obrigatório da requisição não informado"
        const val REQUESTPARAM_INVALIDO = "parâmetro obrigatório"
        const val REQUESTBODY_NAO_INFORMADO = "valor obrigatório não informado ou nulo"
    }
}