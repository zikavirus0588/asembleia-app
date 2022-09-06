package br.com.gomesar.assembleia.application.services.voto.exceptions

import br.com.gomesar.assembleia.application.commons.ApiErrorCode
import java.lang.Exception
import java.lang.RuntimeException

open class DefaultVotoException(val msg: String, val error: ApiErrorCode, exception: Exception?) : RuntimeException(msg, exception) {
}