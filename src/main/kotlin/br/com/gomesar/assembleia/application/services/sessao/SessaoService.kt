package br.com.gomesar.assembleia.application.services.sessao

import br.com.gomesar.assembleia.application.commons.ApiErrorCode
import br.com.gomesar.assembleia.application.controllers.v1.sessao.exceptions.CriaSessaoPautaNaoExistenteException
import br.com.gomesar.assembleia.application.controllers.v1.sessao.exceptions.SessaoNaoEncontradaException
import br.com.gomesar.assembleia.application.services.bundle.IMessageBundleService
import br.com.gomesar.assembleia.application.services.mensageria.enqueuer.ISessaoMessageEnqueue
import br.com.gomesar.assembleia.application.services.pauta.PautaService
import br.com.gomesar.assembleia.application.services.sessao.exceptions.SessaoJaCadastradaException
import br.com.gomesar.assembleia.domain.dto.SessaoDto
import br.com.gomesar.assembleia.domain.entities.EStatusVotacao
import br.com.gomesar.assembleia.domain.entities.EResultadoSessao
import br.com.gomesar.assembleia.domain.entities.Pauta
import br.com.gomesar.assembleia.domain.entities.Sessao
import br.com.gomesar.assembleia.domain.entities.SessaoControle
import br.com.gomesar.assembleia.domain.entities.Voto
import br.com.gomesar.assembleia.domain.repositories.IPautaRepository
import br.com.gomesar.assembleia.domain.repositories.ISessaoRepository
import br.com.gomesar.assembleia.domain.repositories.ISessaoControleRepository
import im.aop.loggers.Level
import im.aop.loggers.advice.after.throwing.LogAfterThrowing
import im.aop.loggers.advice.before.LogBefore
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class SessaoService(
    private val sessaoRepository: ISessaoRepository,
    private val pautaRepository: IPautaRepository,
    private val messageService: IMessageBundleService,
    private val sessaoMessageEnqueue: ISessaoMessageEnqueue,
    private val sessaoControleRepository: ISessaoControleRepository
    ) : ISessaoService {

    @LogBefore(level = Level.INFO, declaringClass = SessaoService::class)
    @LogAfterThrowing(declaringClass = SessaoService::class)
    override fun criarSessao(dto: SessaoDto) {
        val pauta = pautaRepository.getPautaById(dto.id!!) ?: throw CriaSessaoPautaNaoExistenteException(getMapError(
            ApiErrorCode.ERRO_SESSAO_CRIAR_PAUTA_NAO_EXISTENTE,
            dto.id.toString()
        ))

        validaSessaoDto(dto)

        sessaoRepository.save(dto.apply { this.pauta = pauta }.toEntity()).also {
            sessaoMessageEnqueue.finalizaSessaoEnqueuer(sessaoControleRepository.save(SessaoControle(it.toDto())))
        }
    }

    @LogBefore(level = Level.INFO, declaringClass = SessaoService::class)
    @LogAfterThrowing(declaringClass = SessaoService::class)
    override fun finalizaSessao(sessaoId: String) {
        if (!isSessaoFinalizada(sessaoId)) {
            sessaoControleRepository.findBySessaoId(sessaoId)!!.apply {
                statusVotacao = EStatusVotacao.FINALIZADA
            }.also {
                salvaResultadoFinalSessao(
                    sessaoControle = it,
                    pauta = pautaRepository.getPautaById(UUID.fromString(it.sessaoId))
                )
            }
        }
    }

    @LogBefore(level = Level.INFO, declaringClass = SessaoService::class)
    @LogAfterThrowing(declaringClass = SessaoService::class)
    override fun buscaDetalheSessao(sessaoId: String): SessaoDto {
        return with(sessaoRepository.findByPautaId(UUID.fromString(sessaoId))) {
            if (this == null) {
                throw SessaoNaoEncontradaException(getMapError(ApiErrorCode.ERRO_SESSAO_NAO_EXISTENTE, sessaoId))
            }
            montaSessaoDto(this)
        }
    }

    private fun getMapError(apiError: ApiErrorCode, vararg args: String): Map<ApiErrorCode, String> {
        return mapOf(apiError to messageService.getMessage(apiError.codigo, *args))
    }

    private fun validaSessaoDto(dto: SessaoDto) {
        if (sessaoRepository.existsById(dto.id!!)) {
            throw SessaoJaCadastradaException(getMapError(ApiErrorCode.ERRO_SESSAO_JA_CADASTRADA, dto.id.toString()))
        }
    }

    private fun isSessaoFinalizada(sessaoId: String) = sessaoRepository
        .findByPautaId(UUID.fromString(sessaoId))?.finalizada == true

    private fun salvaResultadoFinalSessao(sessaoControle: SessaoControle, pauta: Pauta?) {
        with(sessaoControle) {
            adicionaESalvaVotosNaPauta(pauta = pauta, sessaoControle = this)
            atualizaDadosDaSessaoESalvaNoBanco(this)
            this.apply { expiracao = 90L }.also { sessaoControleRepository.save(it) }
        }
    }

    private fun adicionaESalvaVotosNaPauta(pauta: Pauta?, sessaoControle: SessaoControle) {
        pauta?.let { p ->
            p.adicionaVotos(sessaoControle.votos.map { Voto(it, p) })
            pautaRepository.save(p)
        }
    }

    private fun atualizaDadosDaSessaoESalvaNoBanco(sessaoControle: SessaoControle) {
        sessaoRepository.findByPautaId(UUID.fromString(sessaoControle.sessaoId))?.apply {
            this.qtdVotos = sessaoControle.calculaTotalDeVotos()
            this.votosValidos = sessaoControle.calculaVotosValidos()
            this.resultado = sessaoControle.obtemResultadoFinal()
            this.finalizada = true
        }?.also { sessaoRepository.save(it) }
    }

    private fun montaSessaoDto(sessao: Sessao) = sessao.toDto().copy(
        id = null,
        duracao = sessao.duracao,
        qtdVotos = null,
        votosValidos = null,
        resultado = EResultadoSessao.SEM_VOTOS_COMPUTADOS,
        finalizada = sessao.finalizada,
        pauta = sessao.pauta
    ).takeUnless { sessao.finalizada!! } ?: sessao.toDto()

}