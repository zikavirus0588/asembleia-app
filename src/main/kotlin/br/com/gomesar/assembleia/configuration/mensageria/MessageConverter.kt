package br.com.gomesar.assembleia.configuration.mensageria

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.sonus21.rqueue.converter.MessageConverterProvider
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import org.springframework.messaging.converter.MessageConverter

class MessageConverter : MessageConverterProvider {
    override fun getConverter(): MessageConverter {
        return MappingJackson2MessageConverter().apply {
            val mapper = jacksonObjectMapper()
                .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
                .registerModule(JavaTimeModule())
            objectMapper = mapper
            serializedPayloadClass = String::class.java
        }
    }
}
