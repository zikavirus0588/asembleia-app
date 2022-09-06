package br.com.gomesar.assembleia.domain.dto

import br.com.gomesar.assembleia.application.controllers.v1.sessao.request.CriaSessaoRequest
import br.com.gomesar.assembleia.application.controllers.v1.sessao.response.DetalheSessaoResponse
import br.com.gomesar.assembleia.domain.entities.EResultadoSessao
import br.com.gomesar.assembleia.domain.entities.Pauta
import br.com.gomesar.assembleia.domain.entities.Sessao
import java.lang.StringBuilder
import java.util.UUID

data class SessaoDto(
    var id: UUID? = null,
    var duracao: Int,
    var qtdVotos: Int? = null,
    var votosValidos: Int? = null,
    var resultado: EResultadoSessao? = null,
    var finalizada: Boolean? = null,
    var pauta: Pauta? = null
) {
    constructor(entity: Sessao) : this(
        entity.id,
        entity.duracao,
        entity.qtdVotos,
        entity.votosValidos,
        entity.resultado,
        entity.finalizada,
        entity.pauta
    )

    constructor(request: CriaSessaoRequest) : this(UUID.fromString(request.pautaId), request.duracao ?: 1)

    fun toEntity() = Sessao(this)

    fun toDetalheSessaoResponse() = DetalheSessaoResponse().apply {
        this.quantidadeVotos = this@SessaoDto.qtdVotos
        this.votosValidos = this@SessaoDto.votosValidos
        this.resultado = this@SessaoDto.resultado
        this.finalizada = this@SessaoDto.finalizada
        this.pauta = this@SessaoDto.pauta?.nome
    }

    override fun toString(): String {
        return with(StringBuilder(this.javaClass.simpleName)) {
            append("(")
            append({"duracao= $duracao"})
            id?.let { ", id=$it" }
            qtdVotos?.let { ", qtdVotos=$it" }
            votosValidos?.let { ", votosValidos=$it" }
            resultado?.let { ", resultado=$it" }
            finalizada?.let { ", finalizada=$it" }
            append(")")
        }.toString()
    }
}