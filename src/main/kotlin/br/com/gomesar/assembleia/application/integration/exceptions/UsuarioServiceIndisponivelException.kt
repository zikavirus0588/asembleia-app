package br.com.gomesar.assembleia.application.integration.exceptions

import br.com.gomesar.assembleia.application.commons.ApiErrorCode
import java.lang.Exception

data class UsuarioServiceIndisponivelException(val mapApiError: Map<ApiErrorCode, String>, val ex: Exception? = null) :
    DefaultUserException(mapApiError.values.first(), mapApiError.keys.first(), ex)