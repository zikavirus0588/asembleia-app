package br.com.gomesar.assembleia.application.controllers.v1.sessao

import br.com.gomesar.assembleia.application.commons.ApiAssembleiaResponse
import br.com.gomesar.assembleia.application.controllers.v1.sessao.exceptions.CriaSessaoPautaNaoExistenteException
import br.com.gomesar.assembleia.application.controllers.v1.sessao.exceptions.SessaoNaoEncontradaException
import br.com.gomesar.assembleia.application.controllers.v1.sessao.request.CriaSessaoRequest
import br.com.gomesar.assembleia.application.controllers.v1.sessao.response.DetalheSessaoResponse
import br.com.gomesar.assembleia.application.services.sessao.ISessaoService
import im.aop.loggers.Level
import im.aop.loggers.advice.around.LogAround
import io.swagger.annotations.*
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid
import javax.validation.constraints.Pattern

@RestController
@RequestMapping("/v1/sessoes")
@Api(description = "API para manipulação da sessão da assembleia", tags = ["Sessões"])
@Validated
class SessaoControllerV1(private val sessaoService: ISessaoService) {

    @PostMapping(
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ApiOperation(value = "Cria uma nova sessão")
    @ApiResponses(
        ApiResponse(code = 201, message = "Recurso criado"),
        ApiResponse(code = 400, message = "Requisição inválida"),
        ApiResponse(code = 422, message = "Não foi possível processar as instruções presentes"),
        ApiResponse(code = 500, message = "Erro interno não mapeado")
    )
    @ResponseStatus(HttpStatus.CREATED)
    @LogAround(
        declaringClass = SessaoControllerV1::class,
        ignoreExceptions = [CriaSessaoPautaNaoExistenteException::class],
        level = Level.INFO
    )
    fun criaSessao(@RequestBody @Valid request: CriaSessaoRequest) {
        return sessaoService.criarSessao(request.toDto())
    }

    @ApiOperation(value = "Busca detalhes da sessão")
    @ApiResponses(
        ApiResponse(code = 200, message = "Sucesso"),
        ApiResponse(code = 400, message = "Requisição inválida"),
        ApiResponse(code = 404, message = "Recurso não encontrado"),
        ApiResponse(code = 422, message = "Não foi possível processar as instruções presentes"),
        ApiResponse(code = 500, message = "Erro interno não mapeado")
    )
    @GetMapping("/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    @LogAround(
        declaringClass = SessaoControllerV1::class,
        ignoreExceptions = [SessaoNaoEncontradaException::class],
        level = Level.INFO
    )
    fun detalhaSessao(@PathVariable @Pattern(regexp = "^[0-9a-f]{8}(-[0-9a-f]{4}){3}-[0-9a-f]{12}$") @ApiParam(
        name = "id",
        value = "identificador único da sessão",
        example = "333b74d2-c1c2-4d4b-815c-bfcf839b8e13"
    ) id: String):
            ApiAssembleiaResponse<DetalheSessaoResponse> {
        return with(sessaoService.buscaDetalheSessao(id)) {
            ApiAssembleiaResponse(this.toDetalheSessaoResponse())
        }
    }
}