package mx.com.ga.cosmonaut.empresa.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import mx.com.ga.cosmonaut.common.dto.NcoContratoColaboradorDto;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.entity.colaborador.NcoContratoColaborador;
import mx.com.ga.cosmonaut.common.interceptor.BitacoraKardex;
import mx.com.ga.cosmonaut.common.interceptor.BitacoraSistema;
import mx.com.ga.cosmonaut.common.util.Utilidades;
import mx.com.ga.cosmonaut.empresa.services.ContratoColaboradorService;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;

@Controller("/contratoColaborador")
public class ContratoColaboradorController {

    @Inject
    private ContratoColaboradorService contratoColaboradorService;

    @BitacoraSistema
    @BitacoraKardex
    @Operation(summary = "${cosmonaut.controller.contratocolaborador.guardar.resumen}",
            description = "${cosmonaut.controller.contratocolaborador.guardar.descripcion}",
            operationId = "contratocolaborador.guardar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Contrato Colaborador - Guardar")
    @Put(value = "/guardar",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> guardar(@Header("datos-flujo") String datosFlujo,
                                                   @Header("datos-sesion") String datosSesion,
                                                   @Body NcoContratoColaborador contratoColaborador){
        try {
            return HttpResponse.ok(contratoColaboradorService.guardar(contratoColaborador));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @Operation(summary = "${cosmonaut.controller.contratocolaborador.modificar.resumen}",
            description = "${cosmonaut.controller.contratocolaborador.modificar.descripcion}",
            operationId = "contratocolaborador.modificar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Contrato Colaborador - Modificar")
    @Post(value = "/modificar",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> modificar(@Header("datos-flujo") String datosFlujo,
                                                     @Header("datos-sesion") String datosSesion,
                                                     @Body NcoContratoColaborador contratoColaborador){
        try {
            return HttpResponse.ok(contratoColaboradorService.modificar(contratoColaborador));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraKardex
    @Operation(summary = "${cosmonaut.controller.contratocolaborador.modificarcompensacion.resumen}",
            description = "${cosmonaut.controller.contratocolaborador.modificarcompensacion.descripcion}",
            operationId = "contratocolaborador.modificarcompensacion")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Contrato Colaborador - Modificar Compensacion")
    @Post(value = "/modificar/compensacion",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> modificarCompensacion(@Header("datos-flujo") String datosFlujo,
                                                                 @Header("datos-sesion") String datosSesion,
                                                                 @Body NcoContratoColaborador contratoColaborador){
        try {
            return HttpResponse.ok(contratoColaboradorService.modificarCompensacion(contratoColaborador));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.contratocolaborador.obteneridpersona.resumen}",
            description = "${cosmonaut.controller.contratocolaborador.obteneridpersona.descripcion}",
            operationId = "contratocolaborador.obteneridpersona")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Contrato Colaborador - Obtener por id persona")
    @Get(value = "/obtener/persona/id/{id}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> obtenerIdPersona(@PathVariable Long id){
        try {
            return HttpResponse.ok(contratoColaboradorService.obtenerIdPersona(id));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.contratocolaborador.obteneridempresa.resumen}",
            description = "${cosmonaut.controller.contratocolaborador.obteneridempresa.descripcion}",
            operationId = "contratocolaborador.obteneridempresa")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Contrato Colaborador - Obtener por id empresa")
    @Get(value = "/obtener/empresa/id/{id}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> obtenerIdEmpresa(@PathVariable Long id){
        try {
            return HttpResponse.ok(contratoColaboradorService.obtenerIdEmpresa(id));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.contratocolaborador.obteneridgruponomina.resumen}",
            description = "${cosmonaut.controller.contratocolaborador.obteneridgruponomina.descripcion}",
            operationId = "contratocolaborador.obteneridgruponomina")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Contrato Colaborador - Obtener por id grupo nomina")
    @Get(value = "/obtener/grupoNomina/id/{id}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> obtenerIdGrupoNomina(@PathVariable Long id){
        try {
            return HttpResponse.ok(contratoColaboradorService.obtenerIdGrupoNomina(id));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.contratocolaborador.obteneridpersonadatos.resumen}",
            description = "${cosmonaut.controller.contratocolaborador.obteneridpersonadatos.descripcion}",
            operationId = "contratocolaborador.obteneridpersonadatos")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Contrato Colaborador - Obtener por id persona datos")
    @Get(value = "/obtener/persona/datos/id/{id}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> obtenerIdPersonaNative(@PathVariable Long id){
        try {
            return HttpResponse.ok(contratoColaboradorService.obtenerIdPersonaNative(id));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.contratocolaborador.obteneridarea.resumen}",
            description = "${cosmonaut.controller.contratocolaborador.obteneridarea.descripcion}",
            operationId = "contratocolaborador.obteneridarea")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Contrato Colaborador - Obtener por id area")
    @Get(value = "/obtener/area/id/{id}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> obtenerIdArea(@PathVariable Long id){
        try {
            return HttpResponse.ok(contratoColaboradorService.obtenerIdArea(id));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.contratocolaborador.obtenerlistadinamica.resumen}",
            description = "${cosmonaut.controller.contratocolaborador.obtenerlistadinamica.descripcion}",
            operationId = "contratocolaborador.obtenerlistadinamica")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Contrato Colaborador - Lista Dinamica")
    @Post(value = "/lista/dinamica/",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> obtenerListaDinamica(@Body NcoContratoColaborador contratoColaborador){
        try {
            return HttpResponse.ok(contratoColaboradorService.obtenerListaDinamica(contratoColaborador));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @BitacoraKardex
    @Operation(summary = "${cosmonaut.controller.contratocolaborador.guardarbaja.resumen}",
            description = "${cosmonaut.controller.contratocolaborador.guardarbaja.descripcion}",
            operationId = "contratocolaborador.guardarbaja")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Contrato Colaborador - Guardar baja empleado")
    @Post(value = "/guardar/baja",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> guardarBaja(@Header("datos-flujo") String datosFlujo,
                                                        @Header("datos-sesion") String datosSesion,
                                                        @Body NcoContratoColaboradorDto contratoColaborador){
        try {
            return HttpResponse.ok(contratoColaboradorService.guardarBaja(contratoColaborador));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.contratocolaborador.listaempleadobaja.resumen}",
            description = "${cosmonaut.controller.contratocolaborador.listaempleadobaja.descripcion}",
            operationId = "contratocolaborador.listaempleadobaja")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Contrato Colaborador - Lista empleado baja")
    @Get(value = "/lista/empleado/baja/{id}/{activo}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listaEmpleadoBaja(@NotNull @PathVariable Long id, @NotNull @PathVariable Boolean activo){
        try {
            return HttpResponse.ok(contratoColaboradorService.listaEmpleadoBaja(id, activo));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.contratocolaborador.listaempleadofiniquito.resumen}",
            description = "${cosmonaut.controller.contratocolaborador.listaempleadofiniquito.descripcion}",
            operationId = "contratocolaborador.listaempleadofiniquito")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Contrato Colaborador - Lista empleado baja")
    @Get(value = "/lista/empleado/finiquito/{centroClienteid}/",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listaEmpleadoFiniquito(@NotNull @PathVariable Integer centroClienteid){
        try {
            return HttpResponse.ok(contratoColaboradorService.listaEmpleadoFiniquito(centroClienteid));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.contratocolaborador.listaempleadoaguinaldo.resumen}",
            description = "${cosmonaut.controller.contratocolaborador.listaempleadoaguinaldo.descripcion}",
            operationId = "contratocolaborador.listaempleadoaguinaldo")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Contrato Colaborador - Lista empleado baja")
    @Get(value = "/lista/empleado/aguinaldo/{centroClienteid}/",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listaEmpleadoAguinaldo(@NotNull @PathVariable Integer centroClienteid){
        try {
            return HttpResponse.ok(contratoColaboradorService.listaEmpleadoAguinaldo(centroClienteid));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.contratocolaborador.listadinamicapaginado.resumen}",
            description = "${cosmonaut.controller.contratocolaborador.listadinamicapaginado.descripcion}",
            operationId = "contratocolaborador.listadinamicapaginado")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Contrato Colaborador - Lista Dinamica")
    @Post(value = "/lista/dinamica/paginado/{numeroRegistros}/{pagina}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> obtenerListaDinamicaPaginado(@Body NcoContratoColaborador contratoColaborador,
                                                                        @PathVariable Integer numeroRegistros,
                                                                        @PathVariable  Integer pagina){
        try {
            return HttpResponse.ok(contratoColaboradorService.obtenerListaDinamicaPaginado(contratoColaborador, numeroRegistros,pagina));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @BitacoraKardex
    @Operation(summary = "${cosmonaut.controller.contratocolaborador.guardar.resumen}",
            description = "${cosmonaut.controller.contratocolaborador.guardar.descripcion}",
            operationId = "contratocolaborador.guardar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Contrato Colaborador - Guardar")
    @Put(value = "/guardar/reactivar",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> guardarReactivar(@Header("datos-flujo") String datosFlujo,
                                                           @Header("datos-sesion") String datosSesion,
                                                           @Body NcoContratoColaborador contratoColaborador){
        try {
            return HttpResponse.ok(contratoColaboradorService.guardarReactivar(contratoColaborador));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.contratocolaborador.validareactivar.resumen}",
            description = "${cosmonaut.controller.contratocolaborador.validareactivar.descripcion}",
            operationId = "contratocolaborador.validareactivar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Contrato Colaborador - Valida Reactivar")
    @Get(value = "/valida/reactivar/{centroClienteId}/{personaId}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> validaReactivar(@PathVariable Integer centroClienteId, @PathVariable Integer personaId){
        try {
            return HttpResponse.ok(contratoColaboradorService.validaReactivar(centroClienteId,personaId));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

}
