package mx.com.ga.cosmonaut.empresa.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.Post;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import mx.com.ga.cosmonaut.common.dto.CargaMasivaDto;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.interceptor.BitacoraSistema;
import mx.com.ga.cosmonaut.common.util.Utilidades;
import mx.com.ga.cosmonaut.empresa.services.PTUService;

import javax.inject.Inject;

@Controller("/ptu")
public class PTUController {

    @Inject
    private PTUService ptuService;

    @BitacoraSistema
    @Operation(summary = "${cosmonaut.controller.ptu.cargamasiva.resumen}",
            description = "${cosmonaut.controller.ptu.cargamasiva.descripcion}",
            operationId = "ptu.cargamasiva")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "PTU - Carga Masiva de incidencias")
    @Post(value = "/carga/masiva/",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> cargaMasivaPTU(@Header("datos-flujo") String datosFlujo,
                                                          @Header("datos-sesion") String datosSesion,
                                                          @Body CargaMasivaDto cargaMasivaDto) {
        try {
            return HttpResponse.ok(ptuService.cargaMasivaPTU(cargaMasivaDto));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

}
