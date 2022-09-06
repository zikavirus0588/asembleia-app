package br.com.gomesar.assembleia.application.commons

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.validation.FieldError

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiAssembleiaResponse<out T>(val payload: T? = null, val errors: MutableList<ApiError>? = null)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiError(val codigo: String, val mensagem: String, val campo: String? = null) {
    constructor(fe: FieldError) : this(
        ApiErrorCode.ERRO_REQUESTBODY_INVALIDO.codigo,
        fe.defaultMessage ?: "",
        fe.field.toSnakeCase()
    )
}