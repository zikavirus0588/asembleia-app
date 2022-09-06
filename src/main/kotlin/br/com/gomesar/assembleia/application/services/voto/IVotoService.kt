package br.com.gomesar.assembleia.application.services.voto

import br.com.gomesar.assembleia.domain.dto.VotoDto

interface IVotoService {
    fun criaVoto(dto: VotoDto, pautaId: String)
}