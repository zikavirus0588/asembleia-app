package br.com.gomesar.assembleia.application.integration.service

import br.com.gomesar.assembleia.application.commons.removeCaracteresEspeciais
import br.com.gomesar.assembleia.application.integration.exceptions.UsuarioNaoEncontradoException
import br.com.gomesar.assembleia.application.integration.exceptions.UsuarioServiceIndisponivelException
import br.com.gomesar.assembleia.application.integration.request.UserIntegrationRequest
import br.com.gomesar.assembleia.application.integration.response.EStatusUsuarioVotacao
import br.com.gomesar.assembleia.application.integration.response.UserIntegrationResponse
import br.com.gomesar.assembleia.application.services.bundle.IMessageBundleService
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import io.mockk.unmockkAll
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import java.net.URI

@ExtendWith(MockKExtension::class)
internal class UserIntegrationServiceTest {

    @MockK
    private lateinit var restTemplate: RestTemplate

    @MockK
    private lateinit var messageBundleService: IMessageBundleService

    private lateinit var host: String
    private lateinit var path: String
    private lateinit var userIntegrationService: IUserIntegrationService
    private lateinit var userIntegrationRequest: UserIntegrationRequest
    private lateinit var userIntegrationResponse: UserIntegrationResponse

    @BeforeEach
    fun setUp() {
        host = "user-info.herokuapp.com"
        path = "/users"
        userIntegrationService = UserIntegrationService(
            restTemplate,
            "http",
            host,
            path,
            messageBundleService
        )
        userIntegrationRequest = UserIntegrationRequest("111.222.333-44")
        userIntegrationResponse = UserIntegrationResponse(EStatusUsuarioVotacao.ABLE_TO_VOTE)
        every { messageBundleService.getMessage(any(),*anyVararg()) } returns ""
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun quandoCpfDoUsuarioForValido_entaoRespostaDeveSerAbleToVote() {
        val slotUri = slot<URI>()
        every { restTemplate.getForEntity(capture(slotUri), UserIntegrationResponse::class.java) } returns
                ResponseEntity.ok(userIntegrationResponse)
        with(userIntegrationService.getUserResponse(userIntegrationRequest)) {
            Assertions.assertThat(this.status).isEqualTo(EStatusUsuarioVotacao.ABLE_TO_VOTE)
            Assertions.assertThat(slotUri.captured.host).isEqualTo(host)
            Assertions.assertThat(slotUri.captured.path)
                .isEqualTo("$path/${userIntegrationRequest.cpf.removeCaracteresEspeciais()}")
        }
    }

    @Test
    fun quandoCpfDoUsuarioForValidoMasNaoPuderVotar_entaoRespostaDeveSerUnableToVote() {
        val slotUri = slot<URI>()
        every { restTemplate.getForEntity(capture(slotUri), UserIntegrationResponse::class.java) } returns
                ResponseEntity.ok(userIntegrationResponse.copy(status = EStatusUsuarioVotacao.UNABLE_TO_VOTE))
        with(userIntegrationService.getUserResponse(userIntegrationRequest)) {
            Assertions.assertThat(this.status).isEqualTo(EStatusUsuarioVotacao.UNABLE_TO_VOTE)
            Assertions.assertThat(slotUri.captured.host).isEqualTo(host)
            Assertions.assertThat(slotUri.captured.path)
                .isEqualTo("$path/${userIntegrationRequest.cpf.removeCaracteresEspeciais()}")
        }
    }

    @Test
    fun quandoCpfDoUsuarioForInvalido_entaoRespostaDeveSer404NotFound() {
        val slotUri = slot<URI>()
        every { restTemplate.getForEntity(capture(slotUri), UserIntegrationResponse::class.java) } throws
                HttpClientErrorException(HttpStatus.NOT_FOUND, "User not found")
        assertThrows(
            UsuarioNaoEncontradoException::class.java,
            { userIntegrationService.getUserResponse(userIntegrationRequest.copy("abc")) },
            ""
        )
    }

    @Test
    fun quandoUserServiceEstiverForaDoAr_entaoRespostaDeveSer500InternalServerError() {
        val slotUri = slot<URI>()
        every { restTemplate.getForEntity(capture(slotUri), UserIntegrationResponse::class.java) } throws
                HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request")
        assertThrows(
            UsuarioServiceIndisponivelException::class.java,
            { userIntegrationService.getUserResponse(userIntegrationRequest.copy("abc")) },
            ""
        )
    }



}