package mx.com.ga.cosmonaut.empresa.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.entity.calculo.NcrEmpleadoXnominaPK;
import mx.com.ga.cosmonaut.common.util.Utilidades;
import mx.com.ga.cosmonaut.empresa.services.KioscoService;

import javax.inject.Inject;

@Controller("/kiosco")
public class KioscoController {

    @Inject
    private KioscoService kioscoService;

    @Operation(summary = "${cosmonaut.controller.kiosco.listarempleados.resumen}",
            description = "${cosmonaut.controller.kiosco.listarempleados.descripcion}",
            operationId = "kiosco.listarempleados")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Kiosco - Listar empleados")
    @Post(value = "/listar/empleados",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listaEmpleado(NcrEmpleadoXnominaPK ncrEmpleadoXnomina){
        try {
            return HttpResponse.ok(kioscoService.listaEmpleado(ncrEmpleadoXnomina));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }


}
