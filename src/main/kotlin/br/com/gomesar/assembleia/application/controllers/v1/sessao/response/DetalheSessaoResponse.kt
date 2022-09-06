package br.com.gomesar.assembleia.application.controllers.v1.sessao.response

import br.com.gomesar.assembleia.domain.entities.EResultadoSessao
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.annotations.ApiModelProperty

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class DetalheSessaoResponse(
    @JsonProperty("quantidade_votos")
    @ApiModelProperty(example = "3", notes = "votos que a pauta recebeu", required = false)
    var quantidadeVotos: Int? = null,
    @JsonProperty("votos_validos")
    @ApiModelProperty(example = "2", notes = "votos a favor da pauta", required = false)
    var votosValidos: Int? = null,
    @JsonProperty("resultado_sessao")
    @ApiModelProperty(example = "APROVADA", notes = "resultado da sessão", required = true)
    var resultado: EResultadoSessao? = null,
    @JsonProperty("sessao_finalizada")
    @ApiModelProperty(example = "true", notes = "flag que indica se a sessão foi finalizada", required = true)
    var finalizada: Boolean? = null,
    @JsonProperty("pauta")
    @ApiModelProperty(example = "pauta_1", notes = "pauta discutida na sessão", required = true)
    var pauta: String? = null
)
