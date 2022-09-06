package br.com.gomesar.assembleia.application.services.voto.exceptions

import br.com.gomesar.assembleia.application.commons.ApiErrorCode
import java.lang.Exception

data class SessaoNaoCadastradaException(val mapApiError: Map<ApiErrorCode, String>, val ex: Exception? = null) :
    DefaultVotoException(mapApiError.values.first(), mapApiError.keys.first(), ex)