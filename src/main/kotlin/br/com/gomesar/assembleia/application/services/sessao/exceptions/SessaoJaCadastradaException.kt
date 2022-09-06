package br.com.gomesar.assembleia.application.services.sessao.exceptions

import br.com.gomesar.assembleia.application.commons.ApiErrorCode
import br.com.gomesar.assembleia.application.controllers.v1.sessao.exceptions.DefaultSessaoException
import java.lang.Exception

data class SessaoJaCadastradaException(val mapApiError: Map<ApiErrorCode, String>, val ex: Exception? = null) :
    DefaultSessaoException(mapApiError.values.first(), mapApiError.keys.first(), ex)