package mx.com.ga.cosmonaut.empresa.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.dto.imss.tectel.AfiliaRecepcionRequest;
import mx.com.ga.cosmonaut.common.util.Utilidades;
import mx.com.ga.cosmonaut.empresa.services.TectelService;

import javax.inject.Inject;

@Controller("/tectel")
public class TectelController {

    @Inject
    private TectelService tectelService;

    @Operation(summary = "${cosmonaut.controller.tectel.afiliaRecepcion.resumen}",
            description = "${cosmonaut.controller.tectel.afiliaRecepcion.descripcion}",
            operationId = "tectel.afiliaRecepcion")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "tectel - AfiliaRecepcion")
    @Post(value = "/afiliaRecepcion",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> afiliaRecepcion(@Body AfiliaRecepcionRequest request){
        try {
            return HttpResponse.ok(tectelService.afiliaRecepcion(request));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Get(value = "/comprobante/acuse/{idKardexColaborador}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> getAcuseRecibo(@PathVariable("idKardexColaborador") Integer idKardex){
        try {
            return HttpResponse.ok(tectelService.getAcuseRecibo(idKardex));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Get(value = "/comprobante/constancia/{idKardexColaborador}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> getComprobante(@PathVariable("idKardexColaborador") Integer idKardex){
        try {
            return HttpResponse.ok(tectelService.getConstanciaPresentacion(idKardex));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

}
