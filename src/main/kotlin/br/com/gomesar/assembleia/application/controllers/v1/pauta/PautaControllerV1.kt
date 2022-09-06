package br.com.gomesar.assembleia.application.controllers.v1.pauta

import br.com.gomesar.assembleia.application.commons.ApiAssembleiaResponse
import br.com.gomesar.assembleia.application.controllers.v1.pauta.request.CriaPautaRequest
import br.com.gomesar.assembleia.application.controllers.v1.pauta.response.BuscaPautaResponse
import br.com.gomesar.assembleia.application.services.pauta.IPautaService
import br.com.gomesar.assembleia.application.services.pauta.exceptions.PautaJaCadastradaException
import br.com.gomesar.assembleia.application.services.pauta.exceptions.PautaNaoEncontradaException
import im.aop.loggers.Level
import im.aop.loggers.advice.around.LogAround
import io.swagger.annotations.*
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.util.UUID
import javax.validation.Valid
import javax.validation.constraints.Pattern

@RestController
@RequestMapping("/v1/pautas")
@Api(description = "API para manipulação de pautas da assembleia", tags = ["Pautas"])
@Validated
class PautaControllerV1(private val pautaService: IPautaService) {

    @GetMapping("", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ApiOperation(value = "Busca pautas cadastradas")
    @ApiResponses(
        ApiResponse(code = 200, message = "OK"),
        ApiResponse(code = 500, message = "Erro interno não mapeado")
    )
    @LogAround(declaringClass = PautaControllerV1::class, level = Level.INFO)
    fun buscaTodas(): ApiAssembleiaResponse<List<BuscaPautaResponse>> {
        return with(pautaService.buscaTodas()) {
            ApiAssembleiaResponse(this.map { BuscaPautaResponse(it) })
        }
    }

    @GetMapping("/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ApiOperation(value = "Busca pautas por id")
    @ApiResponses(
        ApiResponse(code = 200, message = "Sucesso"),
        ApiResponse(code = 400, message = "Requisição inválida"),
        ApiResponse(code = 404, message = "Recurso não encontrado"),
        ApiResponse(code = 422, message = "Não foi possível processar as instruções presentes"),
        ApiResponse(code = 500, message = "Erro interno não mapeado")
    )
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @LogAround(
        declaringClass = PautaControllerV1::class,
        ignoreExceptions = [PautaNaoEncontradaException::class],
        level = Level.INFO
    )
    fun busca(@PathVariable @Pattern(regexp = "^[0-9a-f]{8}(-[0-9a-f]{4}){3}-[0-9a-f]{12}$") @ApiParam(
        name = "id",
        value = "identificador único da pauta",
        example = "333b74d2-c1c2-4d4b-815c-bfcf839b8e13"
    ) id: String): ApiAssembleiaResponse<BuscaPautaResponse> {
        return with(pautaService.buscaPautaPorId(UUID.fromString(id))) {
            ApiAssembleiaResponse(BuscaPautaResponse(this))
        }
    }

    @PostMapping("")
    @ApiOperation(value = "Cria uma nova pauta")
    @ApiResponses(
        ApiResponse(code = 201, message = "Recurso criado"),
        ApiResponse(code = 400, message = "Requisição inválida"),
        ApiResponse(code = 422, message = "Não foi possível processar as instruções presentes"),
        ApiResponse(code = 500, message = "Erro interno não mapeado")
    )
    @ResponseStatus(HttpStatus.CREATED)
    @LogAround(
        declaringClass = PautaControllerV1::class,
        ignoreExceptions = [PautaJaCadastradaException::class],
        level = Level.INFO
    )
    fun criaPauta(@RequestBody @Valid request: CriaPautaRequest) {
        ApiAssembleiaResponse(pautaService.criaPauta(request.toDto()))
    }
}