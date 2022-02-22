package mx.com.ga.cosmonaut.empresa.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.util.Utilidades;
import mx.com.ga.cosmonaut.empresa.services.NclPuestoService;

import javax.inject.Inject;

@Controller("/puesto")
public class NclPuestoController {

    @Inject
    private NclPuestoService nclPuestoService;

    @Operation(summary = "${cosmonaut.controller.puesto.findByEsActivo.resumen}",
            description = "${cosmonaut.controller.puesto.findByEsActivo.descripcion}",
            operationId = "puesto.findByEsActivo")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Puesto - Lista todos los activo/inactivo.")
    @Get(value = "/listar/todosActivo/{activo}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> findByEsActivo(@PathVariable Boolean activo){
        try {
            return HttpResponse.ok(nclPuestoService.findByEsActivo(activo));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

}
