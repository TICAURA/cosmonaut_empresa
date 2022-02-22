package mx.com.ga.cosmonaut.empresa.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.entity.colaborador.NcoKardexColaborador;
import mx.com.ga.cosmonaut.common.service.KardexColaboradorService;
import mx.com.ga.cosmonaut.common.util.Utilidades;

import javax.inject.Inject;

@Controller("/kardex")
public class KardexController {

    @Inject
    private KardexColaboradorService kardexColaboradorService;

    @Operation(summary = "${cosmonaut.controller.kardex.listar.resumen}",
            description = "${cosmonaut.controller.kardex.listar.descripcion}",
            operationId = "kardex.listar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Kardex - Listar por Empresa, Persona y Tipo Movimiento")
    @Post(value = "/listar/", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listar(@Body NcoKardexColaborador kardexColaborador) {
        try {
            return HttpResponse.ok(kardexColaboradorService.listar(kardexColaborador));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.kardex.listartipomovimiento.resumen}",
            description = "${cosmonaut.controller.kardex.listartipomovimiento.descripcion}",
            operationId = "kardex.listartipomovimiento")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Kardex - Listar por Empresa, Persona y Tipo Movimiento")
    @Post(value = "/listar/tipo-movimiento", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listarTipoMovimiento(@Body NcoKardexColaborador kardexColaborador) {
        try {
            return HttpResponse.ok(kardexColaboradorService.listarTipoMovimiento(kardexColaborador));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }


}
