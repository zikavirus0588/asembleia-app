package br.com.gomesar.assembleia.application.controllers.v1.pauta.request

import br.com.gomesar.assembleia.domain.dto.PautaDto
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.annotations.ApiModelProperty
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@JsonIgnoreProperties(ignoreUnknown = true)
data class CriaPautaRequest(
    @JsonProperty("nome")
    @ApiModelProperty(example = "Pauta_1", notes = "Nome da pauta", required = true)
    @field:NotBlank
    @field:Size(min = 1, max = 64)
    val nome: String) { fun toDto() = PautaDto(this) }
