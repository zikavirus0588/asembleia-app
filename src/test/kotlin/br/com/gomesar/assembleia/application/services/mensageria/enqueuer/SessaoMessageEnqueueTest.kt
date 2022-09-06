package br.com.gomesar.assembleia.application.services.mensageria.enqueuer

import br.com.gomesar.assembleia.domain.entities.SessaoControle
import com.github.sonus21.rqueue.core.RqueueMessageEnqueuer
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.Duration

@ExtendWith(MockKExtension::class)
internal class SessaoMessageEnqueueTest {

    @MockK
    private lateinit var enqueuer: RqueueMessageEnqueuer
    private lateinit var queueName: String

    private lateinit var sessaoMessageEnqueue: ISessaoMessageEnqueue

    @BeforeEach
    fun setUp() {
        queueName = "finaliza-sessao"
        sessaoMessageEnqueue = SessaoMessageEnqueue(enqueuer, queueName)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun quandoReceberObjetoSessaoControle_entaoDeveEnviarMensagemPraFilaFinalizaSessao() {
        every { enqueuer.enqueueIn(any(), any(), any(), ofType(Duration::class)) } returns true
        sessaoMessageEnqueue.finalizaSessaoEnqueuer(SessaoControle("id", sessaoId = "sessaoid", duracao = 1))
        verify(exactly = 1) { enqueuer.enqueueIn(
            ofType(String::class),
            ofType(String::class),
            ofType(SessaoControle::class),
            ofType(Duration::class)
        ) }
    }
}