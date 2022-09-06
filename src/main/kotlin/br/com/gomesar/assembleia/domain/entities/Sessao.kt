package br.com.gomesar.assembleia.domain.entities

import br.com.gomesar.assembleia.domain.dto.SessaoDto
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "sessao")
data class Sessao(
    @Id
    val id: UUID? = null,
    @Column(name = "duracao")
    val duracao: Int,
    @Column(name = "qtd_votos")
    var qtdVotos: Int?,
    @Column(name = "votos_validos")
    var votosValidos: Int?,
    @Column(name = "resultado")
    @Enumerated(EnumType.STRING)
    var resultado: EResultadoSessao?,
    @Column(name = "finalizada")
    var finalizada: Boolean?,
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    var pauta: Pauta? = null

) {
    constructor(dto: SessaoDto) : this(
        null,
        dto.duracao,
        null,
        null,
        null,
        false,
        dto.pauta
    )

    fun toDto() = SessaoDto(this)
}

enum class EResultadoSessao {
    SEM_VOTOS_COMPUTADOS,
    APROVADA,
    EMPATE,
    REPROVADA
}