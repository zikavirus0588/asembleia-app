package br.com.gomesar.assembleia.domain.entities

import br.com.gomesar.assembleia.domain.dto.SessaoDto
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.TimeToLive
import org.springframework.data.redis.core.index.Indexed
import javax.persistence.Id

@RedisHash("controle-sessao")
data class SessaoControle(
    @Id
    val id: String? = null,
    @Indexed val sessaoId: String,
    val votos: MutableList<VotoControle> = mutableListOf(),
    var statusVotacao: EStatusVotacao? = null,
    var duracao: Int? = null,
    @TimeToLive
    var expiracao: Long? = null
) {
    constructor(dto: SessaoDto) : this(
        sessaoId = dto.id.toString(),
        statusVotacao = EStatusVotacao.EM_ANDAMENTO,
        duracao = dto.duracao
    )

    fun calculaTotalDeVotos() = this.votos.size

    fun calculaVotosValidos() = this.votos.filter { it.resposta == ERespostaUsuario.SIM.resposta }.size

    private fun calculaVotosInvalidos() = calculaTotalDeVotos() - calculaVotosValidos()

    fun obtemResultadoFinal() = when {
        calculaVotosValidos() > calculaVotosInvalidos() -> EResultadoSessao.APROVADA
        calculaVotosValidos() < calculaVotosInvalidos() -> EResultadoSessao.REPROVADA
        calculaTotalDeVotos() > 0 && calculaVotosValidos() == calculaVotosInvalidos() -> EResultadoSessao.EMPATE
        else -> EResultadoSessao.SEM_VOTOS_COMPUTADOS
    }

    override fun toString() = with(StringBuilder(this.javaClass.simpleName)) {
        append("(")
        append("duracao= $duracao, ")
        append("sessaoId=$sessaoId")
        votos.takeIf { it.isNotEmpty() }?.let { append(", votos=$it") }
        statusVotacao?.let { append(", statusVotacao=$it") }
        expiracao?.let { append(", expiracao=$it") }
        append(")")
    }.toString()
}

data class VotoControle(val resposta: String, val usuario: String)

enum class EStatusVotacao {
    EM_ANDAMENTO,
    FINALIZADA
}
