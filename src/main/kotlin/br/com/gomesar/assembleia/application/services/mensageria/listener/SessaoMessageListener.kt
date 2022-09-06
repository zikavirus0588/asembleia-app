package br.com.gomesar.assembleia.application.services.mensageria.listener

import br.com.gomesar.assembleia.application.services.sessao.ISessaoService
import br.com.gomesar.assembleia.domain.entities.SessaoControle
import com.github.sonus21.rqueue.annotation.RqueueListener
import im.aop.loggers.Level
import im.aop.loggers.advice.before.LogBefore
import org.springframework.stereotype.Component

@Component
class SessaoMessageListener(private val sessaoService: ISessaoService) : ISessaoMessageListener {

    @RqueueListener(value = ["finaliza-sessao"], numRetries = "2")
    @LogBefore(declaringClass = SessaoMessageListener::class, level = Level.INFO)
    override fun finalizaSessao(sessaoControle: SessaoControle) {
        sessaoService.finalizaSessao(sessaoControle.sessaoId)
    }

}