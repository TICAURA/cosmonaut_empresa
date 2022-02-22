package mx.com.ga.cosmonaut.empresa.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import mx.com.ga.cosmonaut.common.dto.CargaMasivaDto;
import mx.com.ga.cosmonaut.common.dto.NscIncidenciaDto;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.dto.consultas.IncidenciasConsulta;
import mx.com.ga.cosmonaut.common.interceptor.BitacoraSistema;
import mx.com.ga.cosmonaut.common.interceptor.BitacoraUsuario;
import mx.com.ga.cosmonaut.common.util.Utilidades;
import mx.com.ga.cosmonaut.empresa.services.IncidenciasServices;

import javax.inject.Inject;
import java.util.List;

@Controller("/incidencias")
public class IncidenciasController {

    @Inject
    private IncidenciasServices incidenciasServices;

    @BitacoraSistema
    @BitacoraUsuario
    @Operation(summary = "${cosmonaut.controller.incidencias.guardar.resumen}",
            description = "${cosmonaut.controller.incidencias.guardar.descripcion}",
            operationId = "incidencias.guardar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Incidencias - Guardar")
    @Put(value = "/guardar",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> guardar(@Header("datos-flujo") String datosFlujo,
                                                   @Header("datos-sesion") String datosSesion,
                                                   @Body List<NscIncidenciaDto> listaIncidencia){
        try {
            return HttpResponse.ok(incidenciasServices.guardarLista(listaIncidencia));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @BitacoraUsuario
    @Operation(summary = "${cosmonaut.controller.incidencias.modificar.resumen}",
            description = "${cosmonaut.controller.incidencias.modificar.descripcion}",
            operationId = "incidencias.modificar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Incidencias - Modificar")
    @Post(value = "/modificar",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> modificar(@Header("datos-flujo") String datosFlujo,
                                                     @Header("datos-sesion") String datosSesion,
                                                     @Body NscIncidenciaDto incidencia){
        try {
            return HttpResponse.ok(incidenciasServices.modificar(incidencia));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @Operation(summary = "${cosmonaut.controller.incidencias.modificarestatus.resumen}",
            description = "${cosmonaut.controller.incidencias.modificarestatus.descripcion}",
            operationId = "incidencias.modificarestatus")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Incidencias - Modificar Estatus")
    @Post(value = "/modificar/estatus",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> modificarEstatus(@Header("datos-flujo") String datosFlujo,
                                                            @Header("datos-sesion") String datosSesion,
                                                            @Body NscIncidenciaDto incidencia){
        try {
            return HttpResponse.ok(incidenciasServices.modificarEstatus(incidencia));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.incidencias.eliminar.resumen}",
            description = "${cosmonaut.controller.incidencias.eliminar.descripcion}",
            operationId = "incidencias.eliminar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Incidencias - Eliminar")
    @Post(value = "/eliminar/{id}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> eliminar(@Header("datos-flujo") String datosFlujo,
                                                    @Header("datos-sesion") String datosSesion,
                                                    @PathVariable Long id){
        try {
            return HttpResponse.ok(incidenciasServices.eliminar(id));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.incidencias.obtenerid.resumen}",
            description = "${cosmonaut.controller.incidencias.obtenerid.descripcion}",
            operationId = "incidencias.obtenerid")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Incidencias - Obtener Id")
    @Get(value = "/obtener/id/{id}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> obtenerId(@PathVariable Long id){
        try {
            return HttpResponse.ok(incidenciasServices.obtenerId(id));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.incidencias.listaclienteid.resumen}",
            description = "${cosmonaut.controller.incidencias.listaclienteid.descripcion}",
            operationId = "incidencias.listaclienteid")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Incidencias - Listar incidencias por id empresa")
    @Get(value = "/lista/empresa/{id}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listaClienteId(@PathVariable Long id){
        try {
            return HttpResponse.ok(incidenciasServices.listaClienteId(id));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.incidencias.obtenerincidenciaid.resumen}",
            description = "${cosmonaut.controller.incidencias.obtenerincidenciaid.descripcion}",
            operationId = "incidencias.obtenerincidenciaid")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Incidencias - Obtener id incidencia")
    @Get(value = "/obtener/incidencia/{id}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> obtenerIncidenciaId(@PathVariable Long id){
        try {
            return HttpResponse.ok(incidenciasServices.obtenerIncidenciaId(id));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.incidencias.listaClienteIdFechaInicioFechaFin.resumen}",
            description = "${cosmonaut.controller.incidencias.listaClienteIdFechaInicioFechaFin.descripcion}",
            operationId = "incidencias.listaClienteIdFechaInicioFechaFin")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Incidencias - Listar incidencias por fechas")
    @Post(value = "/lista/empresa/fechas",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listaClienteIdFechaInicioFechaFin(@Body NscIncidenciaDto incidencia){
        try {
            return HttpResponse.ok(incidenciasServices.listaClienteIdFechaInicioFechaFin(incidencia));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.incidencias.listadinamica.resumen}",
            description = "${cosmonaut.controller.incidencias.listadinamica.descripcion}",
            operationId = "incidencias.listadinamica")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Incidencias - Lista Dinamica")
    @Post(value = "/lista/dinamica/",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listaDinamica(@Body IncidenciasConsulta incidencias){
        try {
            return HttpResponse.ok(incidenciasServices.listaDinamica(incidencias));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @Operation(summary = "${cosmonaut.controller.incidencias.lista.cargamasivaincidencias.resumen}",
            description = "${cosmonaut.controller.incidencias.lista.cargamasivaincidencias.descripcion}",
            operationId = "incidencias.lista.cargamasivaincidencias")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Incidencias - Carga Masiva de incidencias")
    @Post(value = "/carga/masiva/",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> cargaMasivaIncidencias(@Header("datos-flujo") String datosFlujo,
                                                                  @Header("datos-sesion") String datosSesion,
                                                                  @Body CargaMasivaDto cargaMasivaDto) {
        try {
            return HttpResponse.ok(incidenciasServices.cargaMasivaIncidencias(cargaMasivaDto));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.incidencias.lista.cargamasiva.resumen}",
            description = "${cosmonaut.controller.incidencias.lista.cargamasiva.descripcion}",
            operationId = "incidencias.lista.cargamasiva")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Incidencias - Lista carga masiva de empleados")
    @Get(value = "/lista/carga/masiva/{centroClienteId}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listaCargaMasiva(@PathVariable Integer centroClienteId) {
        try {
            return HttpResponse.ok(incidenciasServices.listaCargaMasiva(centroClienteId));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.incidencias.lista.cargamasivaestatus.resumen}",
            description = "${cosmonaut.controller.incidencias.lista.cargamasivaestatus.descripcion}",
            operationId = "incidencias.lista.cargamasivaestatus")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Incidencias - Lista carga masiva de empleados")
    @Get(value = "/lista/carga/masiva/estatus/{centroClienteId}/{isEsCorrecto}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listaCargaMasivaEsCorrecto(@PathVariable Integer centroClienteId,Boolean isEsCorrecto) {
        try {
            return HttpResponse.ok(incidenciasServices.listaCargaMasivaEsCorrecto(centroClienteId, isEsCorrecto));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.incidencias.listardinamicapaginado.resumen}",
            description = "${cosmonaut.controller.incidencias.listardinamicapaginado.descripcion}",
            operationId = "incidencias.listardinamicapaginado")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Incidencias - Lista Dinamica")
    @Post(value = "/lista/dinamica/paginado/{numeroRegistros}/{pagina}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listaDinamicaPaginado(@Body IncidenciasConsulta incidencias,
                                                         @PathVariable Integer numeroRegistros,
                                                         @PathVariable  Integer pagina){
        try {
            return HttpResponse.ok(incidenciasServices.listaDinamicaPaginado(incidencias,numeroRegistros,pagina));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

}
