package mx.com.ga.cosmonaut.empresa.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenericaGruop;
import mx.com.ga.cosmonaut.common.dto.imss.IMSSFiltradoRequest;
import mx.com.ga.cosmonaut.common.dto.imss.VariabilidadFiltradoRequest;
import mx.com.ga.cosmonaut.common.util.Utilidades;
import mx.com.ga.cosmonaut.empresa.services.IMSSService;

import javax.inject.Inject;
import javax.validation.Valid;

@Controller("/imss")
public class IMSSController {

    @Inject
    private IMSSService imssService;

    @Operation(summary = "${cosmonaut.controller.imss.registropatronal.resumen}",
            description = "${cosmonaut.controller.imss.registropatronal.descripcion}",
            operationId = "imss.registropatronal")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "IMSS - Listar los registros patronales asignados a un cliente")
    @Get(value = "/cliente/{id}/listar/registrospatronales", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listarRegistrosPatronales(@PathVariable Integer id) {
        try {
            return HttpResponse.ok(imssService.listarRegistrosPatronales(id));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.imss.listarmovimientos.resumen}",
            description = "${cosmonaut.controller.imss.listarmovimientos.descripcion}",
            operationId = "imss.listarmovimientos")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "IMSS - Listar tipos de movimientos del IMSS")
    @Get(value = "/listar/movimientos", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listarMovimientos() {
        try {
            return HttpResponse.ok(imssService.listarMovimientos());
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.imss.filtrar.resumen}",
            description = "${cosmonaut.controller.imss.filtrar.descripcion}",
            operationId = "imss.filtrar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "IMSS - Listar por Empresa, Persona y Tipo Movimiento")
    @Post(value = "/filtrar", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenericaGruop> filtrar(@Body @Valid IMSSFiltradoRequest request) {
        try {
            return HttpResponse.ok(imssService.filtrar(request));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaRepetidaGroup());
        }
    }

    @Operation(summary = "${cosmonaut.controller.imss.eliminar.resumen}",
            description = "${cosmonaut.controller.imss.eliminar.descripcion}",
            operationId = "imss.eliminar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "IMSS - Borrar registro de Kardex")
    @Delete(value = "/eliminar/{id}", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> eliminar(@Header("datos-flujo") String datosFlujo,
                                                    @Header("datos-sesion") String datosSesion,
                                                    @PathVariable Long id) {
        try {
            return HttpResponse.ok(imssService.eliminar(id));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.imss.variabilidadfiltrar.resumen}",
            description = "${cosmonaut.controller.imss.variabilidadfiltrar.descripcion}",
            operationId = "imss.variabilidadfiltrar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "IMSS - Borrar registro de Kardex")
    @Post(value = "/variabilidad/filtrar", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> filtrarVariabilidad(@Body @Valid VariabilidadFiltradoRequest request) {
        try {
            return HttpResponse.ok(imssService.filtrarVariabilidad(request));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.variabilidad.calcularDias.resumen}",
            description = "${cosmonaut.controller.variabilidad.calcularDias.descripcion}",
            operationId = "variabilidad.calcularDias")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Variabilidad - Calcular dias en un bimestre")
    @Get(value = "/variabilidad/calcularDias/{bimestre}", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> calcularDias(@PathVariable Integer bimestre) {
        try {
            return HttpResponse.ok(imssService.calcularDias(bimestre));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

}
