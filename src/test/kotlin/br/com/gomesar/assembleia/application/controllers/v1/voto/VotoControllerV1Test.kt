package br.com.gomesar.assembleia.application.controllers.v1.voto

import br.com.gomesar.assembleia.application.commons.ApiAssembleiaResponse
import br.com.gomesar.assembleia.application.commons.removeCaracteresEspeciais
import br.com.gomesar.assembleia.application.controllers.v1.voto.request.CriaVotoRequest
import br.com.gomesar.assembleia.application.services.voto.IVotoService
import br.com.gomesar.assembleia.domain.dto.VotoDto
import br.com.gomesar.assembleia.domain.entities.ERespostaUsuario
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.*
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
import java.util.UUID

@WebMvcTest(controllers = [VotoControllerV1::class])
internal class VotoControllerV1Test {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockkBean
    private lateinit var service: IVotoService


    @BeforeEach
    fun setUp() {
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun quandoCriaVotoComRequisicaoInvalida_EntaoRetornoDeveSer400BadRequest() {
        with(
            mockMvc.perform(
                MockMvcRequestBuilders.post("/v1/votos")
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
        verify(exactly = 0) { service.criaVoto(ofType(VotoDto::class), ofType(String::class)) }
    }

    @Test
    fun quandoCriaVotoComRequisicaoContendoPropertieInvalida_EntaoRetornoDeveSer400BadRequest() {
        with(
            mockMvc.perform(
                MockMvcRequestBuilders.post("/v1/votos")
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(CriaVotoRequest(
                        usuario = "", resposta = "Talvez", pautaId = "pautaId"
                    )))
            ).andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andReturn()
        ) {
            val apiResponse =
                objectMapper.readValue(this.response.contentAsByteArray, ApiAssembleiaResponse::class.java)
            Assertions.assertThat(apiResponse.payload).isNull()
            Assertions.assertThat(apiResponse.errors?.size).isEqualTo(3)
        }
        verify(exactly = 0) { service.criaVoto(ofType(VotoDto::class), ofType(String::class)) }
    }

    @Test
    fun quandoCriaVotoComRequisicaoValida_EntaoRetornoDeveSer200Ok() {
        val slotVotoDto = slot<VotoDto>()
        val request = CriaVotoRequest(
            usuario = "111.222.333-44", resposta = "Sim", pautaId = "${UUID.randomUUID()}"
        )
        every { service.criaVoto(capture(slotVotoDto), any()) } just runs
        with(
            mockMvc.perform(
                MockMvcRequestBuilders.post("/v1/votos")
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(MockMvcResultMatchers.status().isCreated)
                .andReturn()
        ) {
            Assertions.assertThat(this.response.status).isEqualTo(HttpStatus.CREATED.value())
            Assertions.assertThat(slotVotoDto.captured.id).isNull()
            Assertions.assertThat(slotVotoDto.captured.respostaUsuario).isEqualTo(ERespostaUsuario.SIM)
            Assertions.assertThat(slotVotoDto.captured.usuario).isEqualTo(request.usuario.removeCaracteresEspeciais())
        }
        verify(exactly = 1) { service.criaVoto(ofType(VotoDto::class), ofType(String::class)) }
    }
}