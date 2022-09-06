package br.com.gomesar.assembleia.application.services.sessao

import br.com.gomesar.assembleia.domain.dto.SessaoDto

interface ISessaoService {
    fun criarSessao(dto: SessaoDto)
    fun finalizaSessao(sessaoId: String)
    fun buscaDetalheSessao(sessaoId: String): SessaoDto
}