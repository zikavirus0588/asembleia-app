package br.com.gomesar.assembleia.application.controllers.v1.sessao.exceptions

import br.com.gomesar.assembleia.application.commons.ApiErrorCode
import java.lang.Exception
import java.lang.RuntimeException

open class DefaultSessaoException(val msg: String, val error: ApiErrorCode, exception: Exception?) : RuntimeException(msg, exception) {
}