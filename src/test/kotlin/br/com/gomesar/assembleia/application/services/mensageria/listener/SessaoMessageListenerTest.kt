package br.com.gomesar.assembleia.application.services.mensageria.listener

import br.com.gomesar.assembleia.application.services.sessao.ISessaoService
import br.com.gomesar.assembleia.domain.entities.SessaoControle
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class SessaoMessageListenerTest {

    @MockK
    private lateinit var sessaoService: ISessaoService

    @InjectMockKs
    private lateinit var sessaoMessageListener: SessaoMessageListener

    @BeforeEach
    fun setUp() {
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun quandoReceberSessaoControle_entaoDeveFinalizarSessao() {
        every { sessaoService.finalizaSessao(any()) } just runs
        sessaoMessageListener.finalizaSessao(SessaoControle("id", sessaoId = "sessaoid", duracao = 1))
        verify(exactly = 1) { sessaoService.finalizaSessao(ofType(String::class)) }
    }
}