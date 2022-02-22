package mx.com.ga.cosmonaut.empresa.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import mx.com.ga.cosmonaut.common.dto.NclGrupoNominaDto;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.interceptor.BitacoraSistema;
import mx.com.ga.cosmonaut.common.interceptor.BitacoraUsuario;
import mx.com.ga.cosmonaut.common.util.Utilidades;
import mx.com.ga.cosmonaut.empresa.services.GrupoNominaService;

import javax.inject.Inject;

@Controller("/grupoNomina")
public class GrupoNominaController {

    @Inject
    private GrupoNominaService grupoNominaService;

    @BitacoraSistema
    @BitacoraUsuario
    @Operation(summary = "${cosmonaut.controller.gruponomina.guardar.resumen}",
            description = "${cosmonaut.controller.gruponomina.guardar.descripcion}",
            operationId = "gruponomina.guardar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Grupo Nomina - Guardar")
    @Put(value = "/guardar",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> guardar(@Header("datos-flujo") String datosFlujo,
                                                   @Header("datos-sesion") String datosSesion,
                                                   @Body NclGrupoNominaDto grupoNominaDto){
        try {
            return HttpResponse.ok(grupoNominaService.guardar(grupoNominaDto));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @BitacoraUsuario
    @Operation(summary = "${cosmonaut.controller.gruponomina.modificar.resumen}",
            description = "${cosmonaut.controller.gruponomina.modificar.descripcion}",
            operationId = "gruponomina.modificar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Grupo Nomina - Modificar")
    @Post(value = "/modificar",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> modificar(@Header("datos-flujo") String datosFlujo,
                                                     @Header("datos-sesion") String datosSesion,
                                                     @Body NclGrupoNominaDto grupoNominaDto){
        try {
            return HttpResponse.ok(grupoNominaService.modificar(grupoNominaDto));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraUsuario
    @Operation(summary = "${cosmonaut.controller.gruponomina.eliminar.resumen}",
            description = "${cosmonaut.controller.gruponomina.eliminar.descripcion}",
            operationId = "gruponomina.eliminar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Grupo Nomina - Eliminar")
    @Post(value = "/eliminar/id/{id}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> eliminar(@Header("datos-flujo") String datosFlujo,
                                                    @Header("datos-sesion") String datosSesion,
                                                    @PathVariable Long id){
        try {
            return HttpResponse.ok(grupoNominaService.eliminar(id));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.gruponomina.listaidcompania.resumen}",
            description = "${cosmonaut.controller.gruponomina.listaidcompania.descripcion}",
            operationId = "gruponomina.listaidcompania")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Grupo Nomina - Listar todos")
    @Get(value = "/lista/id/compania/{id}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listaTodos(@PathVariable Long id){
        try {
            return HttpResponse.ok(grupoNominaService.listarTodos(id));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.gruponomina.obtenerid.resumen}",
            description = "${cosmonaut.controller.gruponomina.obtenerid.descripcion}",
            operationId = "gruponomina.obtenerid")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Grupo Nomina - Obtener id")
    @Get(value = "/obtener/id/{id}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> obtenerId(@PathVariable Long id){
        try {
            return HttpResponse.ok(grupoNominaService.obtenerId(id));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.gruponomina.listardinamica.resumen}",
            description = "${cosmonaut.controller.gruponomina.listardinamica.descripcion}",
            operationId = "gruponomina.listadinamica")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Grupo Nomina - Lista dinámica")
    @Post(value = "/lista/dinamica",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listarDinamica(@Body NclGrupoNominaDto grupoNominaDto){
        try {
            return HttpResponse.ok(grupoNominaService.listaDinamica(grupoNominaDto));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.grupoNomina.findByEsActivo.resumen}",
            description = "${cosmonaut.controller.grupoNomina.findByEsActivo.descripcion}",
            operationId = "grupoNomina.findByEsActivo")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Grupo Nomina - Lista todos activo/inactivo.")
    @Get(value = "/listar/todosActivo/{activo}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> findByEsActivo(@PathVariable Boolean activo){
        try {
            return HttpResponse.ok(grupoNominaService.findByEsActivo(activo));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

}
