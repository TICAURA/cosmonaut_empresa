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
import mx.com.ga.cosmonaut.empresa.services.ColaboradorGrupoNominaService;

import javax.inject.Inject;

@Controller("/colaboradorGrupoNomina")
public class ColaboradorGrupoNominaController {

    @Inject
    private ColaboradorGrupoNominaService colaboradorGrupoNominaService;

    @Operation(summary = "${cosmonaut.controller.colaboradorgruponomina.obtenerid.resumen}",
            description = "${cosmonaut.controller.colaboradorgruponomina.obtenerid.descripcion}",
            operationId = "colaboradorgruponomina.obtenerid")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Colaborador Grupo Nomina - Obtener id")
    @Get(value = "/lista/id/grupoNomina/{id}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> obtenerId(@PathVariable Long id){
        try {
            return HttpResponse.ok(colaboradorGrupoNominaService.listaIdGrupoNomina(id));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }
}
