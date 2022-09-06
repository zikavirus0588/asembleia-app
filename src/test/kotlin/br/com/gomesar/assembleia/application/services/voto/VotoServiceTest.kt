package br.com.gomesar.assembleia.application.services.voto

import br.com.gomesar.assembleia.application.integration.response.EStatusUsuarioVotacao
import br.com.gomesar.assembleia.application.integration.response.UserIntegrationResponse
import br.com.gomesar.assembleia.application.integration.service.IUserIntegrationService
import br.com.gomesar.assembleia.application.services.bundle.IMessageBundleService
import br.com.gomesar.assembleia.application.services.voto.exceptions.SessaoNaoCadastradaException
import br.com.gomesar.assembleia.application.services.voto.exceptions.SessaoVotoJaEncerradaException
import br.com.gomesar.assembleia.application.services.voto.exceptions.UsuarioSemPermissaoPraVotarException
import br.com.gomesar.assembleia.application.services.voto.exceptions.UsuarioVotoJaComputadoException
import br.com.gomesar.assembleia.domain.dto.PautaDto
import br.com.gomesar.assembleia.domain.dto.VotoDto
import br.com.gomesar.assembleia.domain.entities.*
import br.com.gomesar.assembleia.domain.repositories.ISessaoControleRepository
import br.com.gomesar.assembleia.domain.repositories.ISessaoRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import io.mockk.unmockkAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.UUID

@ExtendWith(MockKExtension::class)
internal class VotoServiceTest {

    @MockK
    private lateinit var userIntegrationService: IUserIntegrationService

    @MockK
    private lateinit var sessaoControleRepository: ISessaoControleRepository

    @MockK
    private lateinit var sessaoRepository: ISessaoRepository

    @MockK
    private lateinit var bundleService: IMessageBundleService

    @InjectMockKs
    private lateinit var votoService: VotoService


    private lateinit var votoDto: VotoDto
    private lateinit var pautaDto: PautaDto
    private lateinit var sessao: Sessao
    private lateinit var sessaoControle: SessaoControle
    private lateinit var votoControle: VotoControle


    @BeforeEach
    fun setUp() {
        pautaDto = PautaDto().apply { id = UUID.randomUUID() }
        votoDto = VotoDto(
            respostaUsuario = ERespostaUsuario.SIM,
            usuario = "378.756.888-33",
            pauta = pautaDto
        )
        sessao = Sessao(
            pautaDto.id,
            1,
            5,
            4,
            EResultadoSessao.APROVADA,
            true
        )
        votoControle = VotoControle("Sim", "378.756.888-33")

        sessaoControle = SessaoControle(
            id = UUID.randomUUID().toString(),
            votos = mutableListOf(
                votoControle,
                votoControle.copy(resposta = "Não", usuario = "083.595.768-31"),
                votoControle.copy(resposta = "Sim", usuario = "022.724.048-04")
            ),
            statusVotacao = EStatusVotacao.FINALIZADA,
            duracao = 1,
            expiracao = null,
            sessaoId = votoDto.pauta?.id.toString()
        )
        every { bundleService.getMessage(any(), *anyVararg()) } returns ""
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    @DisplayName("deve lançar SessaoVotoJaEncerradaException, quando sessao já estiver finalizada")
    fun testeCriaVotoLancaSessaoVotoJaEncerradaException() {

        every { sessaoControleRepository.findBySessaoId(any()) } returns sessaoControle
        every { sessaoRepository.findByPautaId(any()) } returns sessao

        assertThrows(
            SessaoVotoJaEncerradaException::class.java,
            {
                votoService.criaVoto(votoDto, pautaDto.id.toString())
            },
            ""
        )
    }

    @Test
    @DisplayName("deve lançar SessaoNaoCadastradaException, quando não existir o controle de sessão")
    fun testeCriaVotoLancaSessaoNaoCadastradaException() {

        every { sessaoRepository.findByPautaId(any()) } returns sessao.copy(
            finalizada = false
        )
        every { sessaoControleRepository.findBySessaoId(any()) } returns null

        assertThrows(
            SessaoNaoCadastradaException::class.java,
            {
                votoService.criaVoto(votoDto, pautaDto.id.toString())
            },
            ""
        )
    }

    @Test
    @DisplayName("deve lançar SessaoVotoJaEncerradaException, quando sessao já estiver sido finalizada")
    fun testeCriaVotoSessaoVotoJaEncerradaException() {

        every { sessaoRepository.findByPautaId(any()) } returns sessao.copy(
            finalizada = false
        )

        every { sessaoControleRepository.findBySessaoId(any()) } returns sessaoControle


        assertThrows(
            SessaoVotoJaEncerradaException::class.java,
            {
                votoService.criaVoto(votoDto, pautaDto.id.toString())
            },
            ""
        )
    }

    @Test
    @DisplayName("deve lançar UsuarioVotoJaComputadoException, quando usuário já tiver votado")
    fun testeCriaVotoLancaUsuarioVotoJaComputadoException() {

        every { sessaoRepository.findByPautaId(any()) } returns sessao.copy(
            finalizada = false
        )

        every { sessaoControleRepository.findBySessaoId(any()) } returns sessaoControle
            .copy(statusVotacao = EStatusVotacao.EM_ANDAMENTO)

        every { userIntegrationService.getUserResponse(any()) } returns UserIntegrationResponse(EStatusUsuarioVotacao.ABLE_TO_VOTE)

        assertThrows(
            UsuarioVotoJaComputadoException::class.java,
            {
                votoService.criaVoto(votoDto, pautaDto.id.toString())
            },
            ""
        )
    }

    @Test
    @DisplayName("deve lançar UsuarioSemPermissaoPraVotarException, quando usuário não tiver permissão pra votar")
    fun testeCriaVotoLancaUsuarioSemPermissaoPraVotarException() {

        every { sessaoRepository.findByPautaId(any()) } returns sessao.copy(
            finalizada = false
        )

        every { sessaoControleRepository.findBySessaoId(any()) } returns sessaoControle
            .copy(statusVotacao = EStatusVotacao.EM_ANDAMENTO)

        every { userIntegrationService.getUserResponse(any()) } returns
                UserIntegrationResponse(EStatusUsuarioVotacao.UNABLE_TO_VOTE)

        assertThrows(
            UsuarioSemPermissaoPraVotarException::class.java,
            {
                votoService.criaVoto(votoDto.copy(usuario = "111.222.333-44"), pautaDto.id.toString())
            },
            ""
        )
    }

    @Test
    @DisplayName("deve computar o voto do usuário, quando requisição for válida")
    fun testeCriaVotoComSucesso() {

        val sessaoInicial = SessaoControle(statusVotacao = EStatusVotacao.EM_ANDAMENTO,
            sessaoId = pautaDto.id.toString())

        val sessaoControleAtualizada = sessaoControle.copy(votos = mutableListOf(VotoControle(ERespostaUsuario.SIM.resposta, "111.222.333-44")))

        every { sessaoRepository.findByPautaId(any()) } returns sessao.copy(
            finalizada = false
        )

        every { sessaoControleRepository.findBySessaoId(any()) } returns sessaoInicial


        every { userIntegrationService.getUserResponse(any()) } returns
                UserIntegrationResponse(EStatusUsuarioVotacao.ABLE_TO_VOTE)

        every { sessaoControleRepository.save(any()) } returns sessaoControleAtualizada

        votoService.criaVoto(votoDto.copy(usuario = "111.222.333-44"), pautaDto.id.toString())

        assertTrue(sessaoInicial.votos.isNotEmpty())
    }
}