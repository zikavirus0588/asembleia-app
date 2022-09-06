package br.com.gomesar.assembleia.application.controllers.v1.sessao

import br.com.gomesar.assembleia.application.commons.ApiAssembleiaResponse
import br.com.gomesar.assembleia.application.controllers.v1.sessao.request.CriaSessaoRequest
import br.com.gomesar.assembleia.application.services.sessao.ISessaoService
import br.com.gomesar.assembleia.domain.dto.SessaoDto
import br.com.gomesar.assembleia.domain.entities.EResultadoSessao
import br.com.gomesar.assembleia.domain.entities.Pauta
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.web.util.UriBuilder
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI
import java.util.UUID

@WebMvcTest(controllers = [SessaoControllerV1::class])
internal class SessaoControllerV1Test {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockkBean
    private lateinit var service: ISessaoService

    private lateinit var uuid: UUID
    private lateinit var criaSessaoRequest: CriaSessaoRequest
    private lateinit var sessaoDto: SessaoDto

    @BeforeEach
    fun setUp() {
        uuid = UUID.randomUUID()
        criaSessaoRequest = CriaSessaoRequest(uuid.toString())
        sessaoDto = SessaoDto(criaSessaoRequest)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun quandoCriaSessaoComRequisicaoInvalida_EntaoRetornoDeveSer400BadRequest() {
        with(
            mockMvc.perform(
                MockMvcRequestBuilders.post("/v1/sessoes")
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(null))
            ).andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andReturn()
        ) {
            val apiResponse =
                objectMapper.readValue(this.response.contentAsByteArray, ApiAssembleiaResponse::class.java)
            Assertions.assertThat(apiResponse.payload).isNull()
            Assertions.assertThat(apiResponse.errors?.size).isGreaterThan(0)
        }
        verify(exactly = 0) { service.criarSessao(ofType(SessaoDto::class)) }
    }

    @Test
    fun quandoCriaSessaoComPautaIdInvalida_EntaoRetornoDeveSer400BadRequest() {
        with(
            mockMvc.perform(
                MockMvcRequestBuilders.post("/v1/sessoes")
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(criaSessaoRequest.copy(pautaId = "pautaId")))
            ).andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andReturn()
        ) {
            val apiResponse =
                objectMapper.readValue(this.response.contentAsByteArray, ApiAssembleiaResponse::class.java)
            Assertions.assertThat(apiResponse.payload).isNull()
            Assertions.assertThat(apiResponse.errors?.size).isGreaterThan(0)
        }
        verify(exactly = 0) { service.criarSessao(ofType(SessaoDto::class)) }
    }

    @Test
    fun quandoCriaSessaoComPautaJsonInvalido_EntaoRetornoDeveSer400BadRequest() {
        val request = """
             {
                "pauta_id": "333b74d2-c1c2-4d4b-815c-bfcf839b8e13",
             }
        """.trimIndent()
        with(
            mockMvc.perform(
                MockMvcRequestBuilders.post("/v1/sessoes")
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(request)
            ).andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andReturn()
        ) {
            val apiResponse =
                objectMapper.readValue(this.response.contentAsByteArray, ApiAssembleiaResponse::class.java)
            Assertions.assertThat(apiResponse.payload).isNull()
            Assertions.assertThat(apiResponse.errors?.size).isGreaterThan(0)
        }
        verify(exactly = 0) { service.criarSessao(ofType(SessaoDto::class)) }
    }

    @Test
    fun quandoCriaSessaoComPautaArgumentoInvalido_EntaoRetornoDeveSer400BadRequest() {
        val request = """
             {
                "pauta_id": "333b74d2-c1c2-4d4b-815c-bfcf839b8e13",
                "duracao": "duracao"
             }
        """.trimIndent()
        with(
            mockMvc.perform(
                MockMvcRequestBuilders.post("/v1/sessoes")
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(request)
            ).andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andReturn()
        ) {
            val apiResponse =
                objectMapper.readValue(this.response.contentAsByteArray, ApiAssembleiaResponse::class.java)
            Assertions.assertThat(apiResponse.payload).isNull()
            Assertions.assertThat(apiResponse.errors?.size).isGreaterThan(0)
        }
        verify(exactly = 0) { service.criarSessao(ofType(SessaoDto::class)) }
    }

    @Test
    fun quandoCriaSessaoComParametroObrigatorioNaoEnviado_EntaoRetornoDeveSer400BadRequest() {
        val request = """
             {
                "duracao": 3
             }
        """.trimIndent()
        with(
            mockMvc.perform(
                MockMvcRequestBuilders.post("/v1/sessoes")
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(request)
            ).andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andReturn()
        ) {
            val apiResponse =
                objectMapper.readValue(this.response.contentAsByteArray, ApiAssembleiaResponse::class.java)
            Assertions.assertThat(apiResponse.payload).isNull()
            Assertions.assertThat(apiResponse.errors?.size).isGreaterThan(0)
        }
        verify(exactly = 0) { service.criarSessao(ofType(SessaoDto::class)) }
    }


    @Test
    fun quandoCriaSessaoComRequestValida_EntaoRetornoDeveSer201Ok() {
        val slotSessaoDto = slot<SessaoDto>()
        every { service.criarSessao(capture(slotSessaoDto)) } just runs

        with(
            mockMvc.perform(
                MockMvcRequestBuilders.post("/v1/sessoes")
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(criaSessaoRequest))
            ).andExpect(MockMvcResultMatchers.status().isCreated)
                .andReturn()
        ) {
            Assertions.assertThat(this.response.status).isEqualTo(HttpStatus.CREATED.value())
            Assertions.assertThat(slotSessaoDto.captured.id.toString()).isEqualTo(criaSessaoRequest.pautaId)
            Assertions.assertThat(slotSessaoDto.captured.duracao).isEqualTo(criaSessaoRequest.duracao)
        }
        verify(exactly = 1) { service.criarSessao(ofType(SessaoDto::class)) }
    }

    @Test
    fun quandoBuscaDetalhesDaSessaoComSessaoIdInvalido_EntaoRetornoDeveSer400BadRequest() {
        with(
            mockMvc.perform(
                MockMvcRequestBuilders.get("/v1/sessoes/{id}", 1L)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
            ).andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andReturn()
        ) {
            val apiResponse =
                objectMapper.readValue(this.response.contentAsByteArray, ApiAssembleiaResponse::class.java)
            Assertions.assertThat(apiResponse.payload).isNull()
            Assertions.assertThat(apiResponse.errors?.size).isGreaterThan(0)
        }
        verify(exactly = 0) {
            service.buscaDetalheSessao(ofType(String::class))
        }
    }

    @Test
    fun quandoBuscaDetalhesDaSessaoComSessaoIdValido_EntaoRetornoDeveSer200Ok() {
        sessaoDto = sessaoDto.copy(
            qtdVotos = 2,
            votosValidos = 2,
            resultado = EResultadoSessao.APROVADA,
            finalizada = true,
            pauta = Pauta(nome = "pauta_1")
        )
        val respostaEsperada = ApiAssembleiaResponse(sessaoDto.toDetalheSessaoResponse(), null)

        every { service.buscaDetalheSessao(any()) } returns sessaoDto

        with(
            mockMvc.perform(
                MockMvcRequestBuilders.get("/v1/sessoes/{id}", uuid)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
            ).andExpect(MockMvcResultMatchers.status().isOk)
                .andReturn()
        ) {
            Assertions.assertThat(this.response.contentAsString)
                .isEqualTo(objectMapper.writeValueAsString(respostaEsperada))
        }
        verify(exactly = 1) {
            service.buscaDetalheSessao(ofType(String::class))
        }
    }
}