package br.com.gomesar.assembleia.configuration.mensageria

import com.github.sonus21.rqueue.config.SimpleRqueueListenerContainerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RqueueConfiguration {
    @Bean
    fun simpleRqueueConfiguration() = SimpleRqueueListenerContainerFactory()
}
