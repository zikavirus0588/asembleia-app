package br.com.gomesar.assembleia.application.controllers.v1.sessao.exceptions

import br.com.gomesar.assembleia.application.commons.ApiErrorCode
import java.lang.Exception

data class CriaSessaoPautaNaoExistenteException(val mapApiError: Map<ApiErrorCode, String>, val ex: Exception? = null) :
    DefaultSessaoException(mapApiError.values.first(), mapApiError.keys.first(), ex)