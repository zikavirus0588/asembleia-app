package br.com.gomesar.assembleia.configuration

import com.google.common.collect.Sets
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.info.BuildProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.Contact
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger.web.OperationsSorter
import springfox.documentation.swagger.web.UiConfiguration
import springfox.documentation.swagger.web.UiConfigurationBuilder

@Configuration
class SpringFoxConfiguration(
    private val buildProperties: BuildProperties,
    @Value("\${swagger.protocolo}") private val protocolo: String,
    @Value("\${swagger.base.url}") private val baseUrl: String,
    @Value("\${assembleia.description}") private val descricao: String
) {

    @Bean
    fun customImplementation(): Docket {
        return Docket(DocumentationType.SWAGGER_2).protocols(Sets.newHashSet(protocolo))
            .useDefaultResponseMessages(false)
            .select()
            .apis(RequestHandlerSelectors.basePackage("br.com.gomesar.assembleia.application.controllers"))
            .build()
            .host(baseUrl)
            .apiInfo(apiInfo())
    }

    @Bean
    fun uiConfig(): UiConfiguration? {
        return UiConfigurationBuilder
            .builder()
            .operationsSorter(OperationsSorter.METHOD)
            .build()
    }

    private fun apiInfo(): ApiInfo {
        return ApiInfoBuilder().title(buildProperties.name.uppercase())
            .description(descricao)
            .termsOfServiceUrl("")
            .version(buildProperties.version)
            .contact(Contact("André Rodrigues Gomes", "", "andre88.rg@gmail.com"))
            .build()
    }
}
