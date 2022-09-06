package br.com.gomesar.assembleia.application.services.pauta

import br.com.gomesar.assembleia.domain.dto.PautaDto
import java.util.*

interface IPautaService {
    fun criaPauta(dto: PautaDto)
    fun buscaPautaPorId(id: UUID): PautaDto
    fun buscaTodas(): List<PautaDto>
}