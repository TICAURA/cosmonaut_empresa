package mx.com.ga.cosmonaut.empresa.controller;

import io.micronaut.context.annotation.Value;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.dto.nomina.Request;
import mx.com.ga.cosmonaut.common.util.Constantes;
import mx.com.ga.cosmonaut.common.util.Utilidades;

import javax.inject.Inject;
import java.math.BigDecimal;

@Controller("/nomina")
public class NominaController {

    @Inject
    @Client("${servicio.nomina.host}")
    private RxHttpClient cliente;

    @Value("${servicio.nomina.path}")
    private String ruta;

    @Operation(summary = "${cosmonaut.controller.persona.nomina.obtenernomina.resumen}",
            description = "${cosmonaut.controller.persona.nomina.obtenernomina.descripcion}",
            operationId = "nomina.obtenernomina")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nomina - Obtener Nomina")
    @Get(value = "/obtener",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> guardarRepresentanteLegal(){
        try {

            String sFechaContrato = "2018-11-01";
            String sFechaIniciPeriodo = "2021-01-01";

            Request peticion = new Request();
            peticion.setClienteId(1);
            peticion.setFechaContrato(sFechaContrato);
            peticion.setPersonaId(312);
            peticion.setPoliticaId(1);
            peticion.setGrupoNominaId(4);
            peticion.setTipoCompensacionId(1);
            peticion.setSalarioBaseMensual(BigDecimal.valueOf(8250));
            peticion.setFechaInicioPeriodo(sFechaIniciPeriodo);
            String respuesta = cliente.retrieve(HttpRequest.POST(ruta,peticion), String.class).blockingFirst();

            return HttpResponse.ok(new RespuestaGenerica(respuesta, Constantes.RESULTADO_EXITO, Constantes.EXITO));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

}
