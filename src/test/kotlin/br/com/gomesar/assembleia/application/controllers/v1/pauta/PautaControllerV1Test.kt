package br.com.gomesar.assembleia.application.controllers.v1.pauta

import br.com.gomesar.assembleia.application.commons.ApiAssembleiaResponse
import br.com.gomesar.assembleia.application.controllers.v1.pauta.request.CriaPautaRequest
import br.com.gomesar.assembleia.application.controllers.v1.pauta.response.BuscaPautaResponse
import br.com.gomesar.assembleia.application.services.pauta.IPautaService
import br.com.gomesar.assembleia.domain.dto.PautaDto
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.json.JSONArray
import org.json.JSONObject
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.util.UUID

@WebMvcTest(controllers = [PautaControllerV1::class])
internal class PautaControllerV1Test {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockkBean
    private lateinit var service: IPautaService

    private lateinit var uuid: UUID
    private lateinit var pautaDto: PautaDto
    private lateinit var buscaPautaResponse: BuscaPautaResponse
    private lateinit var criaPautaRequest: CriaPautaRequest


    @BeforeEach
    fun setUp() {
        uuid = UUID.randomUUID()
        criaPautaRequest = seRequerCriaPautaRequest()
        pautaDto = seRequerPautaDto(uuid, criaPautaRequest)
        buscaPautaResponse = seRequerBuscaPautaResponse(pautaDto)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    @DisplayName("Deve retonar 200 OK, quando buscar pautas no sistema")
    fun quandoRequisisaoValida_EntaoRetornoSera200Ok() {
        every { service.buscaTodas() } returns listOf(pautaDto)

        val respostaEsperada = ApiAssembleiaResponse(listOf(buscaPautaResponse), null)

        with(mockMvc.perform(MockMvcRequestBuilders.get("/v1/pautas")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            ).andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()) {
            assertThat(this.response.contentAsString).isEqualTo(objectMapper.writeValueAsString(respostaEsperada))
        }
        verify(exactly = 1) { service.buscaTodas() }
    }

    @Test
    @DisplayName("Deve retonar 400 Bad Request, quando requisição for inválida para criar pauta")
    fun quandoBuscaPautaPorIdComParametroInvalido_EntaoRetornoSera400BadRequest() {

        with(mockMvc.perform(MockMvcRequestBuilders.get("/v1/pautas/{id}", 1L)
            .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andReturn()) {
            val apiResponse = objectMapper.readValue(this.response.contentAsByteArray, ApiAssembleiaResponse::class.java)
            assertThat(apiResponse.payload).isNull()
            assertThat(apiResponse.errors?.size).isGreaterThan(0)
        }
        verify(exactly = 0) { service.buscaPautaPorId(ofType(UUID::class)) }
    }

    @Test
    @DisplayName("Deve retonar 200 OK, quando buscar pauta com requisição válida")
    fun quandoBuscaPautaPorIdComParametroValido_EntaoRetornoSera200Ok() {
        val slotUUID = slot<UUID>()
        val respostaEsperada = ApiAssembleiaResponse(buscaPautaResponse, null)
        every { service.buscaPautaPorId(capture(slotUUID)) } returns pautaDto

        with(mockMvc.perform(MockMvcRequestBuilders.get("/v1/pautas/{id}", uuid)
            .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()) {
            assertThat(this.response.contentAsString).isEqualTo(objectMapper.writeValueAsString(respostaEsperada))
            assertThat(slotUUID.captured).isEqualTo(uuid)
        }
        verify(exactly = 1) { service.buscaPautaPorId(ofType(UUID::class)) }
    }

    @Test
    @DisplayName("Deve retonar 400 BadRequest, quando criar pauta com nome em branco")
    fun quandoCriaPautaComNomeEmBranco_EntaoRerornoDeveSer400BadRequest() {
        with(mockMvc.perform(MockMvcRequestBuilders.post("/v1/pautas")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(criaPautaRequest.copy(nome = "")))
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andReturn()) {
            val apiResponse = objectMapper.readValue(this.response.contentAsByteArray, ApiAssembleiaResponse::class.java)
            assertThat(apiResponse.payload).isNull()
            assertThat(apiResponse.errors?.size).isGreaterThan(0)
        }
        verify(exactly = 0) { service.buscaPautaPorId(ofType(UUID::class)) }
    }

    @Test
    @DisplayName("Deve retonar 400 BadRequest, quando criar pauta com nome maior que 64 caracteres")
    fun quandoCriaPautaComNomeMaiorQue64Caracteres_EntaoRerornoDeveSer400BadRequest() {
        with(mockMvc.perform(MockMvcRequestBuilders.post("/v1/pautas")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(criaPautaRequest.copy(nome = "${UUID.randomUUID()}${UUID.randomUUID()}")))
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andReturn()) {
            val apiResponse = objectMapper.readValue(this.response.contentAsByteArray, ApiAssembleiaResponse::class.java)
            assertThat(apiResponse.payload).isNull()
            assertThat(apiResponse.errors?.size).isGreaterThan(0)
        }
        verify(exactly = 0) { service.buscaPautaPorId(ofType(UUID::class)) }
    }

    @Test
    @DisplayName("Deve retonar 400 BadRequest, quando criar pauta com nome nulo")
    fun quandoCriaPautaComNomeNulo_EntaoRerornoDeveSer400BadRequest() {
        with(mockMvc.perform(MockMvcRequestBuilders.post("/v1/pautas")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(null))
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andReturn()) {
            val apiResponse = objectMapper.readValue(this.response.contentAsByteArray, ApiAssembleiaResponse::class.java)
            assertThat(apiResponse.payload).isNull()
            assertThat(apiResponse.errors?.size).isGreaterThan(0)
        }
        verify(exactly = 0) { service.buscaPautaPorId(ofType(UUID::class)) }
    }

    @Test
    @DisplayName("Deve retonar 400 BadRequest, quando criar pauta com duração inválida")
    fun quandoCriaPautaComDuracaoInvalida_EntaoRerornoDeveSer400BadRequest() {
        val request = JSONObject().apply {
            this.put("nome", "pauta_1")
            this.put("zica", "duracao")
        }.toString()
        with(mockMvc.perform(MockMvcRequestBuilders.post("/v1/pautas")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(request))
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andReturn()) {
            val apiResponse = objectMapper.readValue(this.response.contentAsByteArray, ApiAssembleiaResponse::class.java)
            assertThat(apiResponse.payload).isNull()
            assertThat(apiResponse.errors?.size).isGreaterThan(0)
        }
        verify(exactly = 0) { service.buscaPautaPorId(ofType(UUID::class)) }
    }


    @Test
    @DisplayName("Deve retonar 201 Created, quando criar pauta com requisição válida")
    fun quandoCriarPautaComRequisicaoValida_EntaoRetornoSera200Ok() {
        val slotPautaDto = slot<PautaDto>()
        every { service.criaPauta(capture(slotPautaDto)) } just runs

        with(mockMvc.perform(MockMvcRequestBuilders.post("/v1/pautas")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(criaPautaRequest))
        ).andExpect(MockMvcResultMatchers.status().isCreated)
            .andReturn()) {
            assertThat(this.response.status).isEqualTo(HttpStatus.CREATED.value())
            assertThat(slotPautaDto.captured.id).isNull()
            assertThat(slotPautaDto.captured.nome).isEqualTo(pautaDto.nome)
        }
        verify(exactly = 1) { service.criaPauta(ofType(PautaDto::class)) }
    }



    private fun seRequerBuscaPautaResponse(dto: PautaDto) = BuscaPautaResponse(dto)
    private fun seRequerPautaDto(uuid: UUID, criaPautaRequest: CriaPautaRequest) = PautaDto(uuid,
        criaPautaRequest.nome)
    private fun seRequerCriaPautaRequest() = CriaPautaRequest("pauta_1")

}