package mx.com.ga.cosmonaut.empresa.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.entity.cliente.NclSede;
import mx.com.ga.cosmonaut.common.interceptor.BitacoraSistema;
import mx.com.ga.cosmonaut.common.util.Utilidades;
import mx.com.ga.cosmonaut.empresa.services.SedeServices;

import javax.inject.Inject;

@Controller("/sede")
public class SedeController {

    @Inject
    private SedeServices sedeServices;

    @BitacoraSistema
    @Operation(summary = "${cosmonaut.controller.sede.guardar.resumen}",
            description = "${cosmonaut.controller.sede.guardar.descripcion}",
            operationId = "sede.guardar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Sede - Guardar")
    @Put(value = "/guardar",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> guardar(@Header("datos-flujo") String datosFlujo,
                                                   @Header("datos-sesion") String datosSesion,
                                                   @Body NclSede sede){
        try {
            return HttpResponse.ok(sedeServices.guardar(sede));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @Operation(summary = "${cosmonaut.controller.sede.modificar.resumen}",
            description = "${cosmonaut.controller.sede.modificar.descripcion}",
            operationId = "sede.modificar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Sede - Modificar")
    @Post(value = "/modificar",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> modificar(@Header("datos-flujo") String datosFlujo,
                                                     @Header("datos-sesion") String datosSesion,
                                                     @Body NclSede sede){
        try {
            return HttpResponse.ok(sedeServices.modificar(sede));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.sede.eliminar.resumen}",
            description = "${cosmonaut.controller.sede.eliminar.descripcion}",
            operationId = "sede.eliminar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Sede - Eliminar")
    @Post(value = "/eliminar/{id}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> eliminar(@Header("datos-flujo") String datosFlujo,
                                                    @Header("datos-sesion") String datosSesion,
                                                    @PathVariable Integer id){
        try {
            return HttpResponse.ok(sedeServices.eliminar(id));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.sede.obtenerid.resumen}",
            description = "${cosmonaut.controller.sede.obtenerid.descripcion}",
            operationId = "sede.obtenerid")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Sede - Obtener Id")
    @Get(value = "/obtener/id/{id}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> obtenerId(@PathVariable Long id){
        try {
            return HttpResponse.ok(sedeServices.obtenerId(id));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.sede.obteneridcompania.resumen}",
            description = "${cosmonaut.controller.sede.obteneridcompania.descripcion}",
            operationId = "sede.obteneridcompania")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Sede - Obtener Id Persona")
    @Get(value = "/obtener/id/compania/{id}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> obtenerIdCompania(@PathVariable Long id){
        try {
            return HttpResponse.ok(sedeServices.obtenerIdCompania(id));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.sede.findByEsActivo.resumen}",
            description = "${cosmonaut.controller.sede.findByEsActivo.descripcion}",
            operationId = "sede.findByEsActivo")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Sede - Obtener todos activo/inactivo.")
    @Get(value = "/listar/todosActivo/{activo}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> findByEsActivo(@PathVariable Boolean activo){
        try {
            return HttpResponse.ok(sedeServices.findByEsActivo(activo));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

}
