package br.com.gomesar.assembleia.application.services.bundle

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.unmockkAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyString
import org.springframework.context.MessageSource
import org.springframework.context.NoSuchMessageException
import java.util.*

@ExtendWith(MockKExtension::class)
internal class MessageBundleServiceTest {

    @MockK
    private lateinit var messageSource: MessageSource

    @InjectMockKs
    private lateinit var messageBundleService: MessageBundleService

    @BeforeEach
    fun setUp() {

    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun quandoExistirMensagemNoProperties_entaoRetornoDeveConterMensagemEspecifica() {
        every { messageSource.getMessage("001", arrayOf("tipo"), Locale.getDefault()) } returns "mensagem"
        with(messageBundleService.getMessage(key = "001",  args = arrayOf("tipo"))) {
            assertTrue(this == "mensagem")
        }
    }

    @Test
    fun quandoNaoExistirMensagemNoProperties_entaoRetornoDeveSerAChavePassadaComoParametro() {
        every { messageSource.getMessage("001", arrayOf("tipo"), Locale.getDefault()) } throws  NoSuchMessageException("001")
        with(messageBundleService.getMessage(key = "001",  args = arrayOf("tipo"))) {
            assertTrue(this == "001")
        }
    }

}