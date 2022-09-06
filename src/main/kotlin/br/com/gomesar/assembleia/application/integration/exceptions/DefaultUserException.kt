package br.com.gomesar.assembleia.application.integration.exceptions

import br.com.gomesar.assembleia.application.commons.ApiErrorCode
import java.lang.Exception
import java.lang.RuntimeException

open class DefaultUserException(val msg: String, val error: ApiErrorCode, exception: Exception?) : RuntimeException(msg, exception) {
}