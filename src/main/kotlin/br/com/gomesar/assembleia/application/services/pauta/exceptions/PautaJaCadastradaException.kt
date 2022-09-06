package br.com.gomesar.assembleia.application.services.pauta.exceptions

import br.com.gomesar.assembleia.application.commons.ApiErrorCode
import java.lang.Exception

data class PautaJaCadastradaException(val mapApiError: Map<ApiErrorCode, String>, val ex: Exception? = null) :
    DefaultPautaException(mapApiError.values.first(), mapApiError.keys.first(), ex)