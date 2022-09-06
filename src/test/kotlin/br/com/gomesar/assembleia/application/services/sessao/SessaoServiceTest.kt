package br.com.gomesar.assembleia.application.services.sessao

import br.com.gomesar.assembleia.application.controllers.v1.sessao.exceptions.CriaSessaoPautaNaoExistenteException
import br.com.gomesar.assembleia.application.controllers.v1.sessao.exceptions.SessaoNaoEncontradaException
import br.com.gomesar.assembleia.application.controllers.v1.sessao.request.CriaSessaoRequest
import br.com.gomesar.assembleia.application.services.bundle.IMessageBundleService
import br.com.gomesar.assembleia.application.services.mensageria.enqueuer.ISessaoMessageEnqueue
import br.com.gomesar.assembleia.application.services.sessao.exceptions.SessaoJaCadastradaException
import br.com.gomesar.assembleia.domain.dto.PautaDto
import br.com.gomesar.assembleia.domain.dto.SessaoDto
import br.com.gomesar.assembleia.domain.entities.*
import br.com.gomesar.assembleia.domain.repositories.IPautaRepository
import br.com.gomesar.assembleia.domain.repositories.ISessaoControleRepository
import br.com.gomesar.assembleia.domain.repositories.ISessaoRepository
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.UUID

@ExtendWith(MockKExtension::class)
internal class SessaoServiceTest {

    @MockK
    private lateinit var sessaoRepository: ISessaoRepository

    @MockK
    private lateinit var pautaRepository: IPautaRepository

    @MockK
    private lateinit var messageService: IMessageBundleService

    @MockK
    private lateinit var sessaoMessageEnqueue: ISessaoMessageEnqueue

    @MockK
    private lateinit var sessaoControleRepository: ISessaoControleRepository

    @InjectMockKs
    private lateinit var sessaoService: SessaoService

    private lateinit var sessaoDto: SessaoDto
    private lateinit var sessao: Sessao
    private lateinit var pautaId: UUID
    private lateinit var pauta: Pauta
    private lateinit var pautaDto: PautaDto
    private lateinit var sessaoControle: SessaoControle
    private lateinit var votoControle: VotoControle

    @BeforeEach
    fun setUp() {
        pautaId = UUID.randomUUID()
        pautaDto = seRequerPautaDto()
        pauta = seRequerPauta(uuid = pautaId, dto = pautaDto)
        sessaoDto = seRequerSessaoDto(pauta)
        sessao = seRequerSessao()
        sessaoControle = seRequerSessaoControle(sessaoDto)
        votoControle = seRequerVotoControle()
        every { messageService.getMessage(any(), *anyVararg()) } returns ""
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    @DisplayName("deve lançar CriaSessaoPautaNaoExistenteException, quando não existir uma pauta cadastrada")
    fun criaSessaoLancaCriaSessaoPautaNaoExistenteException() {
        every { pautaRepository.getPautaById(any()) } returns null
        assertThrows(
            CriaSessaoPautaNaoExistenteException::class.java,
            {
                sessaoService.criarSessao(dto = sessaoDto)
            },
            ""
        )
        verify(exactly = 1) { pautaRepository.getPautaById(ofType(UUID::class)) }
        verify(exactly = 0) { sessaoRepository.existsById(ofType(UUID::class)) }
        verify(exactly = 0) {  sessaoRepository.save(ofType(Sessao::class)) }
        verify(exactly = 0) {  sessaoMessageEnqueue.finalizaSessaoEnqueuer(ofType(SessaoControle::class)) }
        verify(exactly = 0) {  sessaoControleRepository.save(ofType(SessaoControle::class)) }

    }

    @Test
    @DisplayName("deve lançar SessaoJaCadastradaException, quando já existir uma sessão cadastrada para a pauta")
    fun criaSessaoLancaSessaoJaCadastradaExceptionException() {
        every { pautaRepository.getPautaById(any()) } returns pauta
        every { sessaoRepository.existsById(pautaId) } returns true

        assertThrows(
            SessaoJaCadastradaException::class.java,
            {
                sessaoService.criarSessao(dto = sessaoDto)
            },
            ""
        )
        verify(exactly = 1) { pautaRepository.getPautaById(ofType(UUID::class)) }
        verify(exactly = 1) { sessaoRepository.existsById(ofType(UUID::class)) }
        verify(exactly = 0) {  sessaoRepository.save(ofType(Sessao::class)) }
        verify(exactly = 0) {  sessaoMessageEnqueue.finalizaSessaoEnqueuer(ofType(SessaoControle::class)) }
        verify(exactly = 0) {  sessaoControleRepository.save(ofType(SessaoControle::class)) }

    }

    @Test
    @DisplayName("deve cadastrar uma nova sessão. quando requisição for válida")
    fun criaSessaoComSucesso() {
        every { pautaRepository.getPautaById(any()) } returns pauta
        every { sessaoRepository.existsById(any()) } returns false
        every { sessaoRepository.save(any()) } returns seRequerSessao()
        every { sessaoMessageEnqueue.finalizaSessaoEnqueuer(any()) } just runs
        every { sessaoControleRepository.save(any()) } returns sessaoControle

        sessaoService.criarSessao(sessaoDto)

        verify(exactly = 1) { pautaRepository.getPautaById(ofType(UUID::class)) }
        verify(exactly = 1) { sessaoRepository.existsById(ofType(UUID::class)) }
        verify(exactly = 1) {  sessaoRepository.save(ofType(Sessao::class)) }
        verify(exactly = 1) {  sessaoMessageEnqueue.finalizaSessaoEnqueuer(ofType(SessaoControle::class)) }
        verify(exactly = 1) {  sessaoControleRepository.save(ofType(SessaoControle::class)) }
    }

    @Test
    @DisplayName("não deve finalizar a sessão, se a mesma já estiver finalizada")
    fun testeNaoFinalizaSessaoSeAMesmaJaEstiverFinalizada() {
        sessao = seRequerSessao(finalizada = true)

        every { sessaoRepository.findByPautaId(any()) } returns sessao

        sessaoService.finalizaSessao(pautaId.toString())

        verify(exactly = 0) {  sessaoControleRepository.findBySessaoId(ofType(String::class)) }
        verify(exactly = 0) {  sessaoControleRepository.save(ofType(SessaoControle::class)) }
        verify(exactly = 0) {  pautaRepository.getPautaById(ofType(UUID::class)) }
        verify(exactly = 0) {  pautaRepository.save(ofType(Pauta::class)) }
        verify(exactly = 1) {  sessaoRepository.findByPautaId(ofType(UUID::class)) }
        verify(exactly = 0) {  sessaoRepository.save(ofType(Sessao::class)) }
    }

    @Test
    @DisplayName("deve finalizar a sessão após, após o tempo de duração chegar ao fim")
    fun testeFinalizaSessaoComSucesso() {
        val slotSessaoControle = slot<SessaoControle>()
        val slotPauta = slot<Pauta>()
        val votos =  mutableListOf(
            votoControle,
            votoControle.copy(resposta = "Não", usuario = "083.595.768-31"),
            votoControle.copy(resposta = "Sim", usuario = "022.724.048-04")
        )
        sessaoControle = seRequerSessaoControle(sessaoDto, votos)
        val sessaoControleAtualizada = sessaoControle.apply {
            statusVotacao = EStatusVotacao.FINALIZADA
            expiracao = 90L
        }
        val pautaAtualizada = atualizaPauta(votos, pauta)
        sessao = seRequerSessao(finalizada = false)
        val sessaoAtualizada = atualizaSessao(sessao, sessaoControleAtualizada)


        every { sessaoRepository.findByPautaId(any()) } returns sessao andThen sessaoAtualizada
        every { sessaoControleRepository.findBySessaoId(any()) } returns sessaoControle
        every { sessaoControleRepository.save(capture(slotSessaoControle)) } returns sessaoControleAtualizada
        every { pautaRepository.getPautaById(any()) } returns pauta
        every { pautaRepository.save(capture(slotPauta)) } returns pautaAtualizada
        every { sessaoRepository.findByPautaId(any()) } returns sessao
        every { sessaoRepository.save(any()) } returns sessaoAtualizada

        sessaoService.finalizaSessao(pautaId.toString())

        assertEquals(slotSessaoControle.captured.expiracao, sessaoControleAtualizada.expiracao)
        assertEquals(slotSessaoControle.captured.statusVotacao, sessaoControleAtualizada.statusVotacao)
        assertEquals(sessaoAtualizada.qtdVotos, sessaoControleAtualizada.calculaTotalDeVotos())
        assertEquals(sessaoAtualizada.votosValidos, sessaoControleAtualizada.calculaVotosValidos())
        assertEquals(sessaoAtualizada.resultado, sessaoControleAtualizada.obtemResultadoFinal())
        assertTrue(sessaoAtualizada.finalizada!!)

        verify(exactly = 1) {  sessaoControleRepository.findBySessaoId(ofType(String::class)) }
        verify(exactly = 1) {  sessaoControleRepository.save(ofType(SessaoControle::class)) }
        verify(exactly = 1) {  pautaRepository.getPautaById(ofType(UUID::class)) }
        verify(exactly = 1) {  pautaRepository.save(ofType(Pauta::class)) }
        verify(exactly = 2) {  sessaoRepository.findByPautaId(ofType(UUID::class)) }
        verify(exactly = 1) {  sessaoRepository.save(ofType(Sessao::class)) }
    }

    @Test
    @DisplayName("deve retornar os detalhes de uma sessão incompleta, quando a mesma ainda não for finalizada")
    fun testBuscaDetalheSessaoLancaSessaoNaoEncontradaException() {
        every { sessaoRepository.findByPautaId(any()) } returns null
        assertThrows(
            SessaoNaoEncontradaException::class.java,
            {
                sessaoService.buscaDetalheSessao(pautaId.toString())
            },
            ""
        )
    }

    @Test
    @DisplayName("deve retornar os detalhes de uma sessão incompleta, quando a mesma ainda não for finalizada")
    fun testBuscaDetalheSessaoNaoFinalizada() {
        every { sessaoRepository.findByPautaId(any()) } returns seRequerSessao(finalizada = false).copy(pauta = pauta)
        with(sessaoService.buscaDetalheSessao(pautaId.toString())) {
            assertTrue(this.resultado == EResultadoSessao.SEM_VOTOS_COMPUTADOS)
            assertFalse(this.finalizada!!)
            assertEquals(this.pauta?.nome, pauta?.nome)
        }
    }

    @Test
    @DisplayName("deve retornar os detalhes de uma sessão incompleta, quando a mesma ainda não for finalizada")
    fun testBuscaDetalheSessaoFinalizada() {
        every { sessaoRepository.findByPautaId(any()) } returns seRequerSessao(finalizada = true)
            .copy(pauta = pauta).apply {
                qtdVotos = 3
                votosValidos = 2
                resultado = EResultadoSessao.APROVADA
            }
        with(sessaoService.buscaDetalheSessao(pautaId.toString())) {
            assertTrue(this.resultado == EResultadoSessao.APROVADA)
            assertTrue(this.finalizada!!)
            assertEquals(this.qtdVotos, 3)
            assertEquals(this.votosValidos, 2)
        }
    }


    private fun atualizaSessao(sessao: Sessao, sessaoControle: SessaoControle) = sessao.copy(
        qtdVotos = sessaoControle.calculaTotalDeVotos(),
        votosValidos = sessaoControle.calculaVotosValidos(),
        resultado = sessaoControle.obtemResultadoFinal(),
        finalizada = true
    )

    private fun seRequerSessaoDto(pauta: Pauta) = SessaoDto(getCriaSessaoRequest()).apply { this.pauta = pauta }

    private fun getCriaSessaoRequest() = CriaSessaoRequest(pautaId.toString())

    private fun seRequerPautaDto() = PautaDto(nome = "pauta_1")

    private fun seRequerPauta(uuid: UUID, dto: PautaDto) = Pauta(
        id = uuid,
        nome = dto.nome!!,
        votos = mutableListOf()
    )

    private fun seRequerSessao(finalizada: Boolean? = null) = Sessao(
        id = pautaId,
        duracao = 1,
        null,
        null,
        null,
        null
    ).apply { finalizada?.let { this.finalizada = it } }

    private fun seRequerVotoControle() = VotoControle("Sim", "378.756.888-33")

    private fun seRequerSessaoControle(dto: SessaoDto, votos: MutableList<VotoControle>? = null) = SessaoControle(dto)
        .apply { votos?.let { this.votos.addAll(it) } }

    private fun atualizaPauta(votos: MutableList<VotoControle>, pauta: Pauta) = pauta.apply {
        adicionaVotos(votos.map { Voto(it, this) })
    }

}