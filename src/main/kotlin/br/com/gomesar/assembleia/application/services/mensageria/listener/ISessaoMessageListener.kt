package br.com.gomesar.assembleia.application.services.mensageria.listener

import br.com.gomesar.assembleia.domain.entities.SessaoControle

interface ISessaoMessageListener {
    fun finalizaSessao(sessaoControle: SessaoControle)
}