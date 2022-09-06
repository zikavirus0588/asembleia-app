package br.com.gomesar.assembleia.domain.entities

import br.com.gomesar.assembleia.domain.dto.VotoDto
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "voto")
data class Voto(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id", insertable = false, updatable = false, nullable = false)
    val id: UUID? = null,
    @Enumerated(value = EnumType.STRING)
    val respostaUsuario: ERespostaUsuario,
    val usuario: String,
    @ManyToOne(fetch = FetchType.LAZY)
    var pauta: Pauta? = null
) {
    constructor(votoControle: VotoControle, pauta: Pauta) : this(
        respostaUsuario = ERespostaUsuario.SIM.takeIf { it.resposta == votoControle.resposta } ?: ERespostaUsuario.NAO,
        usuario = votoControle.usuario,
        pauta = pauta
    )
    fun toDto() = VotoDto(this)
}

enum class ERespostaUsuario(val resposta: String) {
    SIM("SIM"),
    NAO("NÃO")
}
