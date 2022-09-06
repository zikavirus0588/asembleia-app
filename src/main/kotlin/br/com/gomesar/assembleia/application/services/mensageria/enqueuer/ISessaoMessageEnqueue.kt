package br.com.gomesar.assembleia.application.services.mensageria.enqueuer

import br.com.gomesar.assembleia.domain.entities.SessaoControle

interface ISessaoMessageEnqueue {
    fun finalizaSessaoEnqueuer(sessaoControle: SessaoControle)
}