package br.com.gomesar.assembleia.application.services.pauta

import br.com.gomesar.assembleia.application.services.bundle.IMessageBundleService
import br.com.gomesar.assembleia.application.services.pauta.exceptions.PautaJaCadastradaException
import br.com.gomesar.assembleia.application.services.pauta.exceptions.PautaNaoEncontradaException
import br.com.gomesar.assembleia.domain.dto.PautaDto
import br.com.gomesar.assembleia.domain.entities.Pauta
import br.com.gomesar.assembleia.domain.repositories.IPautaRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.extension.ExtendWith
import java.util.UUID

@ExtendWith(MockKExtension::class)
internal class PautaServiceTest {

    @MockK
    private lateinit var pautaRepository: IPautaRepository

    @MockK
    private lateinit var messageService: IMessageBundleService

    @InjectMockKs
    private lateinit var pautaService: PautaService

    private lateinit var dto: PautaDto
    private lateinit var pauta: Pauta
    private lateinit var uuid: UUID

    @BeforeEach
    fun setUp() {
        uuid = UUID.randomUUID()
        dto = seRequerPautaDto()
        pauta = seRequerPauta(uuid, dto)
        every { messageService.getMessage(any(), *anyVararg()) } returns ""
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    @DisplayName("deve lançar PautaJaCadastradaException, quando tentar criar uma pauta já existente")
    fun testeCriaPautaLancaPautaJaCadastradaException() {
        mockkPautaService(existsByNome = true)
        assertThrows(
            PautaJaCadastradaException::class.java,
            { pautaService.criaPauta(dto)},
            ""
        )
        verify(exactly = 0) { pautaRepository.save(ofType(Pauta::class)) }
    }

    @Test
    @DisplayName("deve criar pauta no sistema, quando receber uma requisição válida")
    fun testeCriaPautaComSucesso() {
        mockkPautaService(existsByNome = false, pauta = pauta, save = true)
        pautaService.criaPauta(dto)
        verify(exactly = 1) { pautaRepository.existsByNome(ofType(String::class)) }
        verify(exactly = 1) { pautaRepository.save(ofType(Pauta::class)) }
    }

    @Test
    @DisplayName("deve lançar PautaNaoEncontradaException, quando buscar pauta por id não existente no sistema")
    fun testeCriaPautaLancaPautaNaoEncontradaException() {
        mockkPautaService(getById = true)
        every { pautaRepository.getPautaById(any()) } returns null
        assertThrows(
            PautaNaoEncontradaException::class.java,
            { pautaService.buscaPautaPorId(uuid)},
            ""
        )
        verify(exactly = 1) { pautaRepository.getPautaById(ofType(UUID::class)) }
    }

    @Test
    @DisplayName("deve buscar pauta por id, quando receber um id válido")
    fun testeBuscaPautaPorIdComSucesso() {
        mockkPautaService(pauta = pauta, getById = true)
        with(pautaService.buscaPautaPorId(uuid)) {
            assertEquals(this.id, pauta.toDto().id)
            assertEquals(this.nome, pauta.toDto().nome)
        }
        verify(exactly = 1) { pautaRepository.getPautaById(ofType(UUID::class)) }
    }

    @Test
    @DisplayName("deve buscar todas as pautas cadastradas no sistema")
    fun testeBuscaTodasComSucesso() {
        mockkPautaService(pauta = pauta, buscaTodas = true)
        with(pautaService.buscaTodas()) {
            assertTrue(this.isNotEmpty())
            assertEquals(this.first().id,pauta.toDto().id)
        }
        verify(exactly = 1) { pautaRepository.findAll() }
    }


    private fun seRequerPautaDto() = PautaDto(nome = "pauta_1")

    private fun seRequerPauta(uuid: UUID, dto: PautaDto) = Pauta(
        id = uuid,
        nome = dto.nome!!,
        votos = mutableListOf()
    )

    private fun mockkPautaService(
        existsByNome: Boolean? = null,
        pauta: Pauta? = null,
        save: Boolean? = null,
        getById: Boolean? = null,
        buscaTodas: Boolean? = null
    ) {
        existsByNome?.let {
            every { pautaRepository.existsByNome(any()) } returns it
        }
        pauta?.let {
            if (save == true) every { pautaRepository.save(any()) } returns it
            if (getById == true) every { pautaRepository.getPautaById(any()) } returns it
        }
        buscaTodas?.let {
            every { pautaRepository.findAll() } returns listOf(pauta)
        }
    }


}