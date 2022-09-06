package br.com.gomesar.assembleia.domain.entities

import br.com.gomesar.assembleia.domain.dto.PautaDto
import br.com.gomesar.assembleia.domain.dto.VotoDto
import java.lang.StringBuilder
import java.util.UUID
import javax.persistence.*

@Entity
@Table(name = "pauta")
data class Pauta(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id", insertable = false, updatable = false, nullable = false)
    val id: UUID? = null,
    @Column(name = "nome")
    val nome: String,
    @OneToMany(
        mappedBy = "pauta",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    val votos: MutableList<Voto> = mutableListOf()

) {
    constructor(dto: PautaDto) : this(null, dto.nome!!)

    fun toDto() = PautaDto().apply {
        this.id = this@Pauta.id
        this.nome = this@Pauta.nome
        this.votos.addAll(this@Pauta.votos.map { VotoDto(it) })
    }

    fun adicionaVotos(listaVotos: List<Voto>) = votos.addAll(listaVotos)

    override fun toString(): String {
        return with(StringBuilder(this.javaClass.simpleName)) {
            append("(")
            append("nome=$nome")
            id?.let { append(", id=$it") }
            append(")")
        }.toString()
    }
}