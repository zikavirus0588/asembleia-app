package br.com.gomesar.assembleia.application.controllers.v1.voto.request

import br.com.gomesar.assembleia.domain.dto.VotoDto
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.annotations.ApiModelProperty
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern

@JsonIgnoreProperties(ignoreUnknown = true)
data class CriaVotoRequest(
    @JsonProperty("usuario")
    @ApiModelProperty(example = "111.222.333-44", notes = "cpf do usuário", required = true)
    @field:NotBlank
    val usuario: String,
    @JsonProperty("resposta")
    @ApiModelProperty(example = "Sim", notes = "resposta do usuário", required = true, allowableValues = "Sim, Não")
    @field:Pattern(regexp = "(?i)(?<= |^)sim|não(?= |\$)")
    val resposta: String,
    @JsonProperty("pauta_id")
    @ApiModelProperty(example = "333b74d2-c1c2-4d4b-815c-bfcf839b8e13", notes = "pauta que vai receber o voto", required = true)
    @field:Pattern(regexp = "^[0-9a-f]{8}(-[0-9a-f]{4}){3}-[0-9a-f]{12}$")
    val pautaId: String) { fun toDto() = VotoDto(this) }
