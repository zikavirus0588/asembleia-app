package br.com.gomesar.assembleia.application.controllers.v1.pauta.response

import br.com.gomesar.assembleia.domain.dto.PautaDto
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.annotations.ApiModelProperty

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class BuscaPautaResponse(
    @JsonProperty("pauta_id")
    @ApiModelProperty(example = "333b74d2-c1c2-4d4b-815c-bfcf839b8e13", notes = "identificador único da pauta", required = true)
    val id: String,
    @JsonProperty("nome")
    @ApiModelProperty(example = "pauta_1", notes = "nome da pauta", required = true)
    val nome: String
) {
    constructor(dto: PautaDto) : this(dto.id.toString(), dto.nome!!)
}