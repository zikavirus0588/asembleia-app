package br.com.gomesar.assembleia.application.controllers.v1.sessao.request

import br.com.gomesar.assembleia.domain.dto.SessaoDto
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.annotations.ApiModelProperty
import javax.validation.constraints.Pattern

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class CriaSessaoRequest(
    @JsonProperty("pauta_id")
    @ApiModelProperty(example = "333b74d2-c1c2-4d4b-815c-bfcf839b8e13", notes = "identificador da pauta", required = true)
    @field:Pattern(regexp = "^[0-9a-f]{8}(-[0-9a-f]{4}){3}-[0-9a-f]{12}$")
    val pautaId: String,
    @ApiModelProperty(example = "5", notes = "duração da sessão: default 1 (minuto)", required = false)
    @JsonProperty("duracao")
    val duracao: Int? = 1
) {
    fun toDto() = SessaoDto(this)
}
