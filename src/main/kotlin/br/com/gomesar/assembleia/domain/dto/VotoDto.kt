package br.com.gomesar.assembleia.domain.dto

import br.com.gomesar.assembleia.application.commons.removeCaracteresEspeciais
import br.com.gomesar.assembleia.application.controllers.v1.voto.request.CriaVotoRequest
import br.com.gomesar.assembleia.domain.entities.ERespostaUsuario
import br.com.gomesar.assembleia.domain.entities.Voto
import br.com.gomesar.assembleia.domain.entities.VotoControle
import java.lang.StringBuilder
import java.util.UUID

data class VotoDto(
    val id: UUID? = null,
    val respostaUsuario: ERespostaUsuario? = null,
    val usuario: String? = null,
    var pauta: PautaDto? = null
) {
    constructor(request: CriaVotoRequest) : this(
        null,
        ERespostaUsuario.SIM.takeIf { it.resposta == request.resposta.uppercase()} ?: ERespostaUsuario.NAO,
        request.usuario.removeCaracteresEspeciais(),
    )
    constructor(entity: Voto) : this(entity.id, entity.respostaUsuario, entity.usuario)

    override fun toString(): String {
        return with(StringBuilder(this.javaClass.simpleName)) {
            append("(")
            append(respostaUsuario?.let {"respostaUsuario=$it, "})
            id?.let { append(", id=$it") }
            usuario?.let { append(", usuario=$it") }
            pauta?.let { append(", pauta=$it") }
            append(")")
        }.toString()
    }
}
