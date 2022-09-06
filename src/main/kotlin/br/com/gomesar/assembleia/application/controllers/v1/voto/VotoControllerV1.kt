package br.com.gomesar.assembleia.application.controllers.v1.voto

import br.com.gomesar.assembleia.application.controllers.v1.voto.request.CriaVotoRequest
import br.com.gomesar.assembleia.application.integration.exceptions.UsuarioNaoEncontradoException
import br.com.gomesar.assembleia.application.integration.exceptions.UsuarioServiceIndisponivelException
import br.com.gomesar.assembleia.application.services.voto.IVotoService
import br.com.gomesar.assembleia.application.services.voto.exceptions.SessaoNaoCadastradaException
import br.com.gomesar.assembleia.application.services.voto.exceptions.SessaoVotoJaEncerradaException
import br.com.gomesar.assembleia.application.services.voto.exceptions.UsuarioSemPermissaoPraVotarException
import br.com.gomesar.assembleia.application.services.voto.exceptions.UsuarioVotoJaComputadoException
import im.aop.loggers.Level
import im.aop.loggers.advice.around.LogAround
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@Api(description = "API para manipulação dos votos assembleia", tags = ["Votos"])
@RequestMapping("/v1/votos")
class VotoControllerV1(private val votoService: IVotoService) {

    @PostMapping("", consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ApiOperation(value = "Cadastra o voto do usuário em uma pauta durante a sessão")
    @ApiResponses(
        ApiResponse(code = 201, message = "Recurso criado"),
        ApiResponse(code = 400, message = "Requisição inválida"),
        ApiResponse(code = 422, message = "Não foi possível processar as instruções presentes"),
        ApiResponse(code = 500, message = "Erro interno não mapeado")
    )
    @ResponseStatus(HttpStatus.CREATED)
    @LogAround(declaringClass = VotoControllerV1::class, ignoreExceptions = [
        SessaoVotoJaEncerradaException::class,
        SessaoNaoCadastradaException::class,
        SessaoVotoJaEncerradaException::class,
        UsuarioSemPermissaoPraVotarException::class,
        UsuarioNaoEncontradoException::class,
        UsuarioServiceIndisponivelException::class,
        UsuarioVotoJaComputadoException::class,
    ], level = Level.INFO)
    fun criaVoto(@RequestBody @Valid request: CriaVotoRequest) {
        votoService.criaVoto(request.toDto(), request.pautaId)
    }
}