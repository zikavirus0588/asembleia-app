package br.com.gomesar.assembleia.domain.dto

import br.com.gomesar.assembleia.application.controllers.v1.pauta.request.CriaPautaRequest
import java.lang.StringBuilder
import java.util.UUID


data class PautaDto(
    var id: UUID? = null,
    var nome: String? = null,
    var votos: MutableList<VotoDto> = mutableListOf()
) {
    constructor(request: CriaPautaRequest) : this(nome = request.nome)

    override fun toString(): String {
        return with(StringBuilder(this.javaClass.simpleName)) {
            append("(")
            nome?.let { append("nome=$it") }
            id?.let { append(", id=$it") }
            append(")")
        }.toString()
    }
}