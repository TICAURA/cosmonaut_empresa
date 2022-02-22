package mx.com.ga.cosmonaut.empresa.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.dto.cliente.CredencialesImssDto;
import mx.com.ga.cosmonaut.common.entity.cliente.NclRegistroPatronal;
import mx.com.ga.cosmonaut.common.interceptor.BitacoraSistema;
import mx.com.ga.cosmonaut.common.util.Utilidades;
import mx.com.ga.cosmonaut.empresa.services.RegistroPatronalServices;

import javax.inject.Inject;

@Controller("/registroPatronal")
public class RegistroPatronalController {

    @Inject
    private RegistroPatronalServices registroPatronalServices;

    @BitacoraSistema
    @Operation(summary = "${cosmonaut.controller.registropatronal.guardar.resumen}",
            description = "${cosmonaut.controller.registropatronal.guardar.descripcion}",
            operationId = "registropatronal.guardar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Registro Patronal - Guardar")
    @Put(value = "/guardar",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> guardar(@Header("datos-flujo") String datosFlujo,
                                                   @Header("datos-sesion") String datosSesion,
                                                   @Body NclRegistroPatronal registroPatronal,
                                                   @Body CredencialesImssDto credencialesImss){
        try {
            // TECTEL
            return HttpResponse.ok(registroPatronalServices.guardar(registroPatronal, credencialesImss));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @Operation(summary = "${cosmonaut.controller.registropatronal.modificar.resumen}",
            description = "${cosmonaut.controller.registropatronal.modificar.descripcion}",
            operationId = "registropatronal.modificar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Registro Patronal - Modificar")
    @Post(value = "/modificar",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> modificar(@Header("datos-flujo") String datosFlujo,
                                                     @Header("datos-sesion") String datosSesion,
                                                     @Body NclRegistroPatronal registroPatronal,
                                                     @Body CredencialesImssDto credencialesImss){
        try {
            return HttpResponse.ok(registroPatronalServices.modificar(registroPatronal, credencialesImss));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.registropatronal.obtenerid.resumen}",
            description = "${cosmonaut.controller.registropatronal.obtenerid.descripcion}",
            operationId = "registropatronal.obtenerid")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Registro Patronal - Obtener Id")
    @Get(value = "/obtener/id/{id}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> obtenerId(@PathVariable Long id){
        try {
            return HttpResponse.ok(registroPatronalServices.obtenerId(id));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.registropatronal.obtenerempresaid.resumen}",
            description = "${cosmonaut.controller.registropatronal.obtenerempresaid.descripcion}",
            operationId = "registropatronal.obtenerempresaid")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Registro Patronal - Obtener Empresa id")
    @Get(value = "/obtener/empresa/id/{id}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> obtenerEmpresaId(@PathVariable Long id){
        try {
            return HttpResponse.ok(registroPatronalServices.obtenerEmpresaId(id));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

}
