package br.com.gomesar.assembleia.application.services.pauta.exceptions

import br.com.gomesar.assembleia.application.commons.ApiErrorCode
import java.lang.Exception
import java.lang.RuntimeException

open class DefaultPautaException(val msg: String, val error: ApiErrorCode, exception: Exception?) : RuntimeException(msg, exception) {
}