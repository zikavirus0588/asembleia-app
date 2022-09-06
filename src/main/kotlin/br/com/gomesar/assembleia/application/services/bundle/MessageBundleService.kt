package br.com.gomesar.assembleia.application.services.bundle

import org.springframework.context.MessageSource
import org.springframework.context.NoSuchMessageException
import org.springframework.stereotype.Service
import java.util.*

@Service
class MessageBundleService(private val messageSource: MessageSource): IMessageBundleService {

    override fun getMessage(key: String, vararg args: String): String {
        return try {
            messageSource.getMessage(key, args, Locale.getDefault())
        } catch (ex: NoSuchMessageException) {
            key
        }
    }

}