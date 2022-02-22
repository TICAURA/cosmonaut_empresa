
package mx.com.ga.cosmonaut.empresa.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.inject.Inject;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.util.Utilidades;
import mx.com.ga.cosmonaut.empresa.services.NcoContratoColaboradorService;


@Controller("/contratoColaborador")
public class NcoContratoColaboradorController {

    @Inject
    private NcoContratoColaboradorService ncoContratoColaboradorService;

    @Operation(summary = "${cosmonaut.controller.contratoColaborador.obtenertodos.resumen}",
            description = "${cosmonaut.controller.contratoColaborador.obtenertodos.descripcion}",
            operationId = "contratoColaborador.obtenertodos")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Contrato Colaborador - Listar todos")
    @Get(value = "/listar/todos", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> obtenerTodos() {
        try {
            return HttpResponse.ok(ncoContratoColaboradorService.findAll());
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

}
