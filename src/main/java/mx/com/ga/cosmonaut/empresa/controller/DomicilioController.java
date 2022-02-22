package mx.com.ga.cosmonaut.empresa.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.entity.administracion.NmaDomicilio;
import mx.com.ga.cosmonaut.common.interceptor.BitacoraSistema;
import mx.com.ga.cosmonaut.common.util.Utilidades;
import mx.com.ga.cosmonaut.empresa.services.DomicilioService;

import javax.inject.Inject;

@Controller("/domicilio")
public class DomicilioController {

    @Inject
    private DomicilioService domicilioService;

    @BitacoraSistema
    @Operation(summary = "${cosmonaut.controller.domicilio.guardar.resumen}",
            description = "${cosmonaut.controller.domicilio.guardar.descripcion}",
            operationId = "domicilio.guardar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Domicilio - Guardar")
    @Put(value = "/guardar",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> guardar(@Header("datos-flujo") String datosFlujo,
                                                   @Header("datos-sesion") String datosSesion,
                                                   @Body NmaDomicilio domicilio){
        try {
            return HttpResponse.ok(domicilioService.guardar(domicilio));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @Operation(summary = "${cosmonaut.controller.domicilio.guardarsede.resumen}",
            description = "${cosmonaut.controller.domicilio.guardarsede.descripcion}",
            operationId = "domicilio.guardarsede")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Domicilio - Guardar Sede")
    @Put(value = "/guardar/sede/domicilio",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> guardarSedeDomicilio(@Header("datos-flujo") String datosFlujo,
                                                                @Header("datos-sesion") String datosSesion,
                                                                @Body NmaDomicilio domicilio){
        try {
            return HttpResponse.ok(domicilioService.guardarSedeDomicilio(domicilio));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @Operation(summary = "${cosmonaut.controller.domicilio.modificar.resumen}",
            description = "${cosmonaut.controller.domicilio.modificar.descripcion}",
            operationId = "domicilio.modificar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Domicilio - Modificar")
    @Post(value = "/modificar",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> modificar(@Header("datos-flujo") String datosFlujo,
                                                     @Header("datos-sesion") String datosSesion,
                                                     @Body NmaDomicilio domicilio){
        try {
            return HttpResponse.ok(domicilioService.modificar(domicilio));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.domicilio.obtenerid.resumen}",
            description = "${cosmonaut.controller.domicilio.obtenerid.descripcion}",
            operationId = "domicilio.obtenerid")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Domicilio - Obtener Id")
    @Get(value = "/obtener/id/{id}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> obtenerId(@PathVariable Long id){
        try {
            return HttpResponse.ok(domicilioService.obtenerId(id));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.domicilio.obteneridpersona.resumen}",
            description = "${cosmonaut.controller.domicilio.obteneridpersona.descripcion}",
            operationId = "domicilio.obteneridpersona")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Domicilio - Obtener id persona")
    @Get(value = "/obtener/id/persona/{id}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> obtenerIdPersona(@PathVariable Long id){
        try {
            return HttpResponse.ok(domicilioService.obtenerIdPersona(id));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.domicilio.obteneridpersonanativo.resumen}",
            description = "${cosmonaut.controller.domicilio.obteneridpersonanativo.descripcion}",
            operationId = "domicilio.obteneridpersonanativo")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Domicilio - Obtener id persona nativo")
    @Get(value = "/obtener/id/persona/nativo/{id}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> obtenerIdPersonaNativo(@PathVariable Long id){
        try {
            return HttpResponse.ok(domicilioService.obtenerIdPersonaNativo(id));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.domicilio.obteneridempresa.resumen}",
            description = "${cosmonaut.controller.domicilio.obteneridempresa.descripcion}",
            operationId = "domicilio.obteneridempresa")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Domicilio - Obtener Id Empresa")
    @Get(value = "/obtener/id/empresa/{id}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> obtenerIdEmpresa(@PathVariable Long id){
        try {
            return HttpResponse.ok(domicilioService.obtenerIdEmpresa(id));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.domicilio.obtenerdomicilioid.resumen}",
            description = "${cosmonaut.controller.domicilio.obtenerdomicilioid.descripcion}",
            operationId = "domicilio.obtenerdomicilioid")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Domicilio - Obtener Domicilio")
    @Get(value = "/obtener/id/empresa/domicilio/{id}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> obtenerIdEmpresaDomicilio(@PathVariable Long id){
        try {
            return HttpResponse.ok(domicilioService.obtenerIdEmpresaDomicilio(id));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

}
