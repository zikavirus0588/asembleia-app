package br.com.gomesar.assembleia.configuration

import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import java.time.Duration

@Configuration
class RestTemplateConfiguration {

    @Bean
    fun restTemplate(): RestTemplate {
        return RestTemplateBuilder().apply {
            setConnectTimeout(Duration.ofMillis(5000))
            setReadTimeout(Duration.ofMillis(5000))
        }.build()
    }
}