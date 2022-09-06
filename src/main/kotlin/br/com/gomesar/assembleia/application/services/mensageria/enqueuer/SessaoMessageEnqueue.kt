package br.com.gomesar.assembleia.application.services.mensageria.enqueuer

import br.com.gomesar.assembleia.domain.entities.SessaoControle
import com.github.sonus21.rqueue.core.RqueueMessageEnqueuer
import im.aop.loggers.Level
import im.aop.loggers.advice.before.LogBefore
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class SessaoMessageEnqueue(
    private val enqueuer: RqueueMessageEnqueuer,
    @Value("\${assembleia.sessao.queueName}") val queueName: String
) : ISessaoMessageEnqueue {

    @LogBefore(declaringClass = SessaoMessageEnqueue::class, level = Level.INFO)
    override fun finalizaSessaoEnqueuer(sessaoControle: SessaoControle) {
        enqueuer.enqueueIn(
            queueName,
            sessaoControle.sessaoId,
            sessaoControle,
            Duration.ofMinutes(sessaoControle.duracao!!.toLong())
        )
    }

}