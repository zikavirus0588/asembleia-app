package br.com.gomesar.assembleia.application.services.pauta

import br.com.gomesar.assembleia.application.commons.ApiErrorCode
import br.com.gomesar.assembleia.application.services.bundle.IMessageBundleService
import br.com.gomesar.assembleia.application.services.pauta.exceptions.PautaJaCadastradaException
import br.com.gomesar.assembleia.application.services.pauta.exceptions.PautaNaoEncontradaException
import br.com.gomesar.assembleia.domain.dto.PautaDto
import br.com.gomesar.assembleia.domain.dto.VotoDto
import br.com.gomesar.assembleia.domain.entities.Pauta
import br.com.gomesar.assembleia.domain.repositories.IPautaRepository
import im.aop.loggers.Level
import im.aop.loggers.advice.after.throwing.LogAfterThrowing
import im.aop.loggers.advice.before.LogBefore
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class PautaService(
    private val pautaRepository: IPautaRepository,
    private val messageService: IMessageBundleService
) : IPautaService {

    @LogBefore(level = Level.INFO, declaringClass = PautaService::class)
    @LogAfterThrowing(declaringClass = PautaService::class)
    override fun criaPauta(dto: PautaDto) {
        with(dto) {
            if (pautaRepository.existsByNome(dto.nome!!)) {
                throw PautaJaCadastradaException(getMapError(ApiErrorCode.ERRO_PAUTA_JA_CADASTRADA, dto.nome!!))
            }
            pautaRepository.save(Pauta(this))
        }
    }

    @LogBefore(level = Level.INFO, declaringClass = PautaService::class)
    @LogAfterThrowing(declaringClass = PautaService::class)
    override fun buscaPautaPorId(id: UUID): PautaDto {
        return pautaRepository.getPautaById(id).takeIf { it != null }?.toDto()
            ?: throw PautaNaoEncontradaException(getMapError(ApiErrorCode.ERRO_PAUTA_NAO_ENCONTRADA, id.toString()))
    }

    @LogBefore(level = Level.INFO, declaringClass = PautaService::class)
    override fun buscaTodas(): List<PautaDto> {
        return pautaRepository.findAll().map { it.toDto() }
    }

    private fun getMapError(apiError: ApiErrorCode, vararg args: String): Map<ApiErrorCode, String> {
        return mapOf(apiError to messageService.getMessage(apiError.codigo, *args))
    }
}