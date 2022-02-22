
package mx.com.ga.cosmonaut.empresa.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.dto.administracion.DeduccionEmpleadoArchivosDto;
import mx.com.ga.cosmonaut.common.entity.administracion.NmmConceptoDeduccion;
import mx.com.ga.cosmonaut.common.entity.administracion.NmmConceptoPercepcion;
import mx.com.ga.cosmonaut.common.entity.administracion.NmmConfiguraDeduccion;
import mx.com.ga.cosmonaut.common.entity.administracion.NmmConfiguraPercepcion;
import mx.com.ga.cosmonaut.common.interceptor.BitacoraSistema;
import mx.com.ga.cosmonaut.common.util.Utilidades;
import mx.com.ga.cosmonaut.empresa.services.PercepcionDeduccionService;

import javax.inject.Inject;
import javax.validation.constraints.NotBlank;


@Controller("/percepcionDeduccion")
public class PercepcionDeduccionController {
    
    @Inject
    private PercepcionDeduccionService percepcionDeduccionService;

    @BitacoraSistema
    @Operation(summary = "${cosmonaut.controller.percepcion.guardarPercepcion.resumen}",
            description = "${cosmonaut.controller.percepcion.guardarPercepcion.descripcion}",
            operationId = "percepcion.guardarPercepcion")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Percepciones - Guardar")
    @Put(value = "/guardarPercepcion", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> guardarPercepcion(@Header("datos-flujo") String datosFlujo,
                                                             @Header("datos-sesion") String datosSesion,
                                                             @Body NmmConceptoPercepcion nmmConceptoPercepcion) {
        try {
            return HttpResponse.ok(percepcionDeduccionService.guardarPercepcion(nmmConceptoPercepcion));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @Operation(summary = "${cosmonaut.controller.percepcion.guardaPercepcionEmpleado.resumen}",
            description = "${cosmonaut.controller.percepcion.guardaPercepcionEmpleado.descripcion}",
            operationId = "percepcion.guardaPercepcionEmpleado")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Percepciones - Guardar percepción a empleado")
    @Put(value = "/guardaPercepcionEmpleado", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> guardaPercepcionEmpleado(@Header("datos-flujo") String datosFlujo,
                                                                    @Header("datos-sesion") String datosSesion,
                                                                    @Body NmmConfiguraPercepcion nmmConfiguraPercepcion) {
        try {
            return HttpResponse.ok(percepcionDeduccionService.guardaPercepcionEmpleado(nmmConfiguraPercepcion));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @Operation(summary = "${cosmonaut.controller.percepcion.guardaPercepcionPolitica.resumen}",
            description = "${cosmonaut.controller.percepcion.guardaPercepcionPolitica.descripcion}",
            operationId = "percepcion.guardaPercepcionPolitica")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Percepciones - Guardar percepción a politica")
    @Put(value = "/guardaPercepcionPolitica", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> guardaPercepcionPolitica(@Header("datos-flujo") String datosFlujo,
                                                                    @Header("datos-sesion") String datosSesion,
                                                                    @Body NmmConfiguraPercepcion nmmConfiguraPercepcion) {
        try {
            return HttpResponse.ok(percepcionDeduccionService.guardaPercepcionPolitica(nmmConfiguraPercepcion));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @Operation(summary = "${cosmonaut.controller.percepcion.modificaPercepcionEmpleado.resumen}",
            description = "${cosmonaut.controller.percepcion.modificaPercepcionEmpleado.descripcion}",
            operationId = "percepcion.modificaPercepcionEmpleado")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Percepciones - Modifíca percepción a empleado")
    @Post(value = "/modificaPercepcionEmpleado", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> modificaPercepcionEmpleado(@Header("datos-flujo") String datosFlujo,
                                                                      @Header("datos-sesion") String datosSesion,
                                                                      @Body NmmConfiguraPercepcion nmmConfiguraPercepcion) {
        try {
            return HttpResponse.ok(percepcionDeduccionService.modificaPercepcionEmpleado(nmmConfiguraPercepcion));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @Operation(summary = "${cosmonaut.controller.percepcion.modificarPercepcionPolitica.resumen}",
            description = "${cosmonaut.controller.percepcion.modificarPercepcionPolitica.descripcion}",
            operationId = "percepcion.modificarPercepcionPolitica")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Percepciones - Modifíca percepción a politica")
    @Post(value = "/modificarPercepcionPolitica", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> modificarPercepcionPolitica(@Header("datos-flujo") String datosFlujo,
                                                                       @Header("datos-sesion") String datosSesion,
                                                                       @Body NmmConfiguraPercepcion nmmConfiguraPercepcion) {
        try {
            return HttpResponse.ok(percepcionDeduccionService.modificarPercepcionPolitica(nmmConfiguraPercepcion));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @Operation(summary = "${cosmonaut.controller.percepcion.guardaDeduccionEmpleado.resumen}",
            description = "${cosmonaut.controller.percepcion.guardaDeduccionEmpleado.descripcion}",
            operationId = "percepcion.guardaDeduccionEmpleado")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Deducciones - Guardar deducción a empleado")
    @Put(value = "/guardaDeduccionEmpleado", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> guardaDeduccionEmpleado(@Header("datos-flujo") String datosFlujo,
                                                                   @Header("datos-sesion") String datosSesion,
                                                                   @Body NmmConfiguraDeduccion nmmConfiguraDeduccion,
                                                                   @Body DeduccionEmpleadoArchivosDto archivos) {
        try {
            return HttpResponse.ok(percepcionDeduccionService.guardaDeduccionEmpleado(nmmConfiguraDeduccion, archivos));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @Operation(summary = "${cosmonaut.controller.percepcion.modificaDeduccionEmpleado.resumen}",
            description = "${cosmonaut.controller.percepcion.modificaDeduccionEmpleado.descripcion}",
            operationId = "percepcion.modificaDeduccionEmpleado")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Deducciones - Modifíca deducción a empleado")
    @Post(value = "/modificaDeduccionEmpleado", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> modificaDeduccionEmpleado(@Header("datos-flujo") String datosFlujo,
                                                                     @Header("datos-sesion") String datosSesion,
                                                                     @Body NmmConfiguraDeduccion nmmConfiguraDeduccion,
                                                                     @Body DeduccionEmpleadoArchivosDto archivos) {
        try {
            return HttpResponse.ok(percepcionDeduccionService.modificaDeduccionEmpleado(nmmConfiguraDeduccion, archivos));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @Operation(summary = "${cosmonaut.controller.percepcion.guardaDeduccionPolitica.resumen}",
            description = "${cosmonaut.controller.percepcion.guardaDeduccionPolitica.descripcion}",
            operationId = "percepcion.guardaDeduccionPolitica")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Deducciones - Guardar deducción a politica")
    @Put(value = "/guardaDeduccionPolitica", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> guardaDeduccionPolitia(@Header("datos-flujo") String datosFlujo,
                                                                  @Header("datos-sesion") String datosSesion,
                                                                  @Body NmmConfiguraDeduccion nmmConfiguraDeduccion) {
        try {
            return HttpResponse.ok(percepcionDeduccionService.guardaDeduccionPolitica(nmmConfiguraDeduccion));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @Operation(summary = "${cosmonaut.controller.percepcion.modificarDeduccionPolitica.resumen}",
            description = "${cosmonaut.controller.percepcion.modificarDeduccionPolitica.descripcion}",
            operationId = "percepcion.modificarDeduccionPolitica")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Deducciones - Modifíca deducción por politica")
    @Post(value = "/modificarDeduccionPolitica", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> modificarDeduccionPolitica(@Header("datos-flujo") String datosFlujo,
                                                                      @Header("datos-sesion") String datosSesion,
                                                                      @Body NmmConfiguraDeduccion nmmConfiguraDeduccion) {
        try {
            return HttpResponse.ok(percepcionDeduccionService.modificarDeduccionPolitica(nmmConfiguraDeduccion));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }
    
    @Operation(summary = "${cosmonaut.controller.percepcion.percepcionEstandar.resumen}",
            description = "${cosmonaut.controller.percepcion.percepcionEstandar.descripcion}",
            operationId = "percepcion.percepcionEstandar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Percepciones - Configura percepciones estándar por empresa ID")
    @Put(value = "/percepcionesEstandar/{clienteId}", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> guardaPercepcionesEstandar(@PathVariable @NotBlank Integer clienteId) {
        try {
            return HttpResponse.ok(percepcionDeduccionService.guardaPercepcionesEstandar(clienteId));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @Operation(summary = "${cosmonaut.controller.percepcion.modificarPercepcion.resumen}",
            description = "${cosmonaut.controller.percepcion.modificarPercepcion.descripcion}",
            operationId = "percepcion.modificarPercepcion")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Percepciones - Modificar")
    @Post(value = "/modificarPercepcion", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> modificarPercepcion(@Header("datos-flujo") String datosFlujo,
                                                               @Header("datos-sesion") String datosSesion,
                                                               @Body NmmConceptoPercepcion nmmConceptoPercepcion) {
        try {
            return HttpResponse.ok(percepcionDeduccionService.modificarPercepcion(nmmConceptoPercepcion));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @Operation(summary = "${cosmonaut.controller.percepcion.eliminarPercepcion.resumen}",
            description = "${cosmonaut.controller.percepcion.eliminarPercepcion.descripcion}",
             operationId = "percepcion.eliminarPercepcion")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Percepciones - Eliminar")
    @Post(value = "/eliminarPercepcion", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> eliminaPercepcion(@Header("datos-flujo") String datosFlujo,
                                                             @Header("datos-sesion") String datosSesion,
                                                             @Body NmmConceptoPercepcion nmmConceptoPercepcion) {
        try {
            return HttpResponse.ok(percepcionDeduccionService.eliminaPercepcion(nmmConceptoPercepcion));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @Operation(summary = "${cosmonaut.controller.percepcion.eliminaPercepcionEmpleado.resumen}",
            description = "${cosmonaut.controller.percepcion.eliminaPercepcionEmpleado.descripcion}",
              operationId = "percepcion.eliminaPercepcionEmpleado")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Percepciones Empleado - Eliminar percepciones al empleado")
    @Post(value = "/eliminarPercepcionEmpleado", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> eliminaPercepcionEmpleado(@Header("datos-flujo") String datosFlujo,
                                                                     @Header("datos-sesion") String datosSesion,
                                                                     @Body NmmConfiguraPercepcion nmmConfiguraPercepcion){
         try {
            return HttpResponse.ok(percepcionDeduccionService.eliminaPercepcionEmpleado(nmmConfiguraPercepcion));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }        
    }

    @BitacoraSistema
    @Operation(summary = "${cosmonaut.controller.percepcion.eliminarDeduccionEmpleado.resumen}",
            description = "${cosmonaut.controller.percepcion.eliminarDeduccionEmpleado.descripcion}",
             operationId = "percepcion.eliminarDeduccionEmpleado")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Deducciones Empleado - Eliminar deducciones al empleado")
    @Post(value = "/eliminarDeduccionEmpleado", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> eliminaDeduccionEmpleado(@Header("datos-flujo") String datosFlujo,
                                                                    @Header("datos-sesion") String datosSesion,
                                                                    @Body NmmConfiguraDeduccion nmmConfiguraDeduccion){
         try {
            return HttpResponse.ok(percepcionDeduccionService.eliminaDeduccionEmpleado(nmmConfiguraDeduccion));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }        
    }
    
    
    @Operation(summary = "${cosmonaut.controller.percepcion.consultaPercepcion.resumen}",
            description = "${cosmonaut.controller.percepcion.consultaPercepcion.descripcion}",
            operationId = "percepcion.consultaPercepcion")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Percepciones - Obtiene perceciones por empresa ID")
    @Get(value = "/obtener/percepcion/{clienteId}", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> obtienePercepcionesEmpresa(@PathVariable @NotBlank Integer clienteId) {
        try {
            return HttpResponse.ok(percepcionDeduccionService.obtienePercepcionesEmpresa(clienteId));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }
    
    @Operation(summary = "${cosmonaut.controller.percepcion.obtieneConceptoPercepcionEmpresa.resumen}",
            description = "${cosmonaut.controller.percepcion.obtieneConceptoPercepcionEmpresa.descripcion}",
            operationId = "percepcion.obtieneConceptoPercepcionEmpresa")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Percepciones - Obtiene percepciones por empresa ID / periodicidad")
    @Get(value = "/obtener/percepcion/periodicidad/{clienteId}/{tipoPeriodicidad}", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> obtieneConceptoPercepcionEmpresa(@PathVariable @NotBlank Integer clienteId, String tipoPeriodicidad) {
        try {
            return HttpResponse.ok(percepcionDeduccionService.obtieneConceptoPercepcionEmpresa(clienteId, tipoPeriodicidad));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.percepcion.obtieneConceptoPercepcionPolitica.resumen}",
            description = "${cosmonaut.controller.percepcion.obtieneConceptoPercepcionPolitica.descripcion}",
            operationId = "percepcion.obtieneConceptoPercepcionPolitica")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Percepciones - Obtiene percepciones por empresa ID / periodicidad para politica")
    @Get(value = "/obtener/percepcion/politica/periodicidad/{clienteId}/{tipoPeriodicidad}", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> obtieneConceptoPercepcionPolitica(@PathVariable @NotBlank Integer clienteId, String tipoPeriodicidad) {
        try {
            return HttpResponse.ok(percepcionDeduccionService.obtieneConceptoPercepcionPolitica(clienteId, tipoPeriodicidad));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }
    
    @Operation(summary = "${cosmonaut.controller.percepcion.calculaMonto.resumen}",
            description = "${cosmonaut.controller.percepcion.calculaMonto.descripcion}",
            operationId = "percepcion.calculaMonto")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Percepciones - Obtiene monto")
    @Get(value = "/obtener/monto/percepcion/{montoTotal}/{numeroPeriodos}", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> calculaMonto(@PathVariable @NotBlank Double montoTotal, Integer numeroPeriodos) {
        try {
            return HttpResponse.ok(percepcionDeduccionService.calculaMonto(montoTotal, numeroPeriodos));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }
    
    
    @Operation(summary = "${cosmonaut.controller.percepcion.obtienePercepcionEmpleado.resumen}",
            description = "${cosmonaut.controller.percepcion.obtienePercepcionEmpleado.descripcion}",
            operationId = "percepcion.obtienePercepcionEmpleado")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Percepciones Empleado - Percepciones asignadas al empleado")
    @Get(value = "/obtienePercepcionEmpleado/{personaId}/{clienteId}", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> obtienePercepcionEmpleado(@PathVariable @NotBlank Integer personaId, Integer clienteId){
         try {
            return HttpResponse.ok(percepcionDeduccionService.obtienePercepcionEmpleado(personaId, clienteId));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }        
    }
    
    @Operation(summary = "${cosmonaut.controller.percepcion.obtieneDeduccionEmpleado.resumen}",
            description = "${cosmonaut.controller.percepcion.obtieneDeduccionEmpleado.descripcion}",
            operationId = "percepcion.obtieneDeduccionEmpleado")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Deducciones Empleado - Deducciones asignadas al empleado")
    @Get(value = "/obtieneDeduccionEmpleado/{personaId}/{clienteId}", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> obtieneDeduccionEmpleado(@PathVariable @NotBlank Integer personaId, Integer clienteId){
         try {
            return HttpResponse.ok(percepcionDeduccionService.obtieneDeduccionEmpleado(personaId, clienteId));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }        
    }
    
    @Operation(summary = "${cosmonaut.controller.percepcion.obtienePercepcionPolitica.resumen}",
            description = "${cosmonaut.controller.percepcion.obtienePercepcionPolitica.descripcion}",
            operationId = "percepcion.obtienePercepcionPolitica")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Percepciones Politica - Percepciones asignadas a política")
    @Get(value = "/obtienePercepcionPolitica/{politicaId}/{clienteId}", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> obtienePercepcionPolitica(@PathVariable @NotBlank Integer politicaId, Integer clienteId){
         try {
            return HttpResponse.ok(percepcionDeduccionService.obtienePercepcionPolitica(politicaId, clienteId));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }        
    }

    @Operation(summary = "${cosmonaut.controller.percepcion.obtieneDeduccionPolitica.resumen}",
            description = "${cosmonaut.controller.percepcion.obtieneDeduccionPolitica.descripcion}",
            operationId = "percepcion.obtieneDeduccionPolitica")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Deducciones Politica - Percepciones asignadas a política")
    @Get(value = "/obtieneDeduccionPolitica/{politicaId}/{clienteId}", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> obtieneDeduccionPolitica(@PathVariable @NotBlank Integer politicaId, Integer clienteId){
        try {
            return HttpResponse.ok(percepcionDeduccionService.obtieneDeduccionPolitica(politicaId, clienteId));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }
    
    @Operation(summary = "${cosmonaut.controller.percepcion.obtenerdeduccion.resumen}",
            description = "${cosmonaut.controller.percepcion.obtenerdeduccion.descripcion}",
            operationId = "percepcion.obtenerdeduccion")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Deducciones - Obtiene deducciones por empresa ID")
    @Get(value = "/obtener/deduccion/{clienteId}", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> obtieneDeduccionesEmpresa(@PathVariable @NotBlank Integer clienteId) {
        try {
            return HttpResponse.ok(percepcionDeduccionService.obtieneDeduccionesEmpresa(clienteId));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.percepcion.obtenerdeduccionempresaestatus.resumen}",
            description = "${cosmonaut.controller.percepcion.obtenerdeduccionempresaestatus.descripcion}",
            operationId = "percepcion.obtenerdeduccionempresaestatus")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Deducciones - Obtiene deducciones por empresa ID")
    @Get(value = "/obtener/deduccion/empresa/estatus/{clienteId}/{estatus}", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> obtieneDeduccionesEmpresaEstatus(@PathVariable @NotBlank Integer clienteId,@PathVariable @NotBlank boolean estatus) {
        try {
            return HttpResponse.ok(percepcionDeduccionService.obtieneDeduccionesEmpresaEstatus(clienteId,estatus));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.percepcion.obtenerdeduccionpolitica.resumen}",
            description = "${cosmonaut.controller.percepcion.obtenerdeduccionpolitica.descripcion}",
            operationId = "percepcion.obtenerdeduccionpolitica")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Deducciones - Obtiene deducciones por empresa ID para politica")
    @Get(value = "/obtener/deduccion/politica/{clienteId}", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> obtieneDeduccionesPolitica(@PathVariable @NotBlank Integer clienteId) {
        try {
            return HttpResponse.ok(percepcionDeduccionService.obtieneDeduccionesPolitica(clienteId));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.percepcion.obtenerdeduccionpoliticaestatus.resumen}",
            description = "${cosmonaut.controller.percepcion.obtenerdeduccionpoliticaestatus.descripcion}",
            operationId = "percepcion.obtenerdeduccionpoliticaestatus")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Deducciones - Obtiene deducciones por empresa ID para politica")
    @Get(value = "/obtener/deduccion/politica/estatus/{clienteId}/{estatus}", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> obtieneDeduccionesPoliticaEstatus(@PathVariable @NotBlank Integer clienteId, @PathVariable @NotBlank  boolean estatus) {
        try {
            return HttpResponse.ok(percepcionDeduccionService.obtieneDeduccionesPoliticaEstatus(clienteId,estatus));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @Operation(summary = "${cosmonaut.controller.percepcion.guardarpercepcion.resumen}",
            description = "${cosmonaut.controller.percepcion.guardarpercepcion.descripcion}",
             operationId = "percepcion.guardarpercepcion")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Deducciones - Guardar")
    @Put(value = "/guardarDeduccion", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> guardarDeduccion(@Header("datos-flujo") String datosFlujo,
                                                            @Header("datos-sesion") String datosSesion,
                                                            @Body NmmConceptoDeduccion nmmConceptoDeduccion) {
        try {
            return HttpResponse.ok(percepcionDeduccionService.guardarDeduccion(nmmConceptoDeduccion));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }
    
    @Operation(summary = "${cosmonaut.controller.percepcion.deduccionEstandar.resumen}",
            description = "${cosmonaut.controller.percepcion.deduccionEstandar.descripcion}",
            operationId = "percepcion.deduccionEstandar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Deducciones - Configura deducciones estándar por empresa ID")
    @Put(value = "/deduccionesEstandar/{clienteId}", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> guardaDeduccionesEstandar(@Header("datos-flujo") String datosFlujo,
                                                                     @Header("datos-sesion") String datosSesion,
                                                                     @PathVariable @NotBlank Integer clienteId) {
        try {
            return HttpResponse.ok(percepcionDeduccionService.guardaDeduccionesEstandar(clienteId));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @Operation(summary = "${cosmonaut.controller.percepcion.modificarpercepcion.resumen}",
            description = "${cosmonaut.controller.percepcion.modificarpercepcion.descripcion}",
            operationId = "percepcion.modificarpercepcion")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Deducciones - Modificar")
    @Post(value = "/modificarDeduccion", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> modificarDeduccion(@Header("datos-flujo") String datosFlujo,
                                                              @Header("datos-sesion") String datosSesion,
                                                              @Body NmmConceptoDeduccion nmmConceptoDeduccion) {
        try {
            return HttpResponse.ok(percepcionDeduccionService.modificarDeduccion(nmmConceptoDeduccion));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @Operation(summary = "${cosmonaut.controller.percepcion.eliminarpercepcion.resumen}",
            description = "${cosmonaut.controller.percepcion.eliminarpercepcion.descripcion}",
            operationId = "percepcion.eliminarpercepcion")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Deducciones - Eliminar")
    @Post(value = "/eliminarDeduccion", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> eliminaDeduccion(@Header("datos-flujo") String datosFlujo,
                                                            @Header("datos-sesion") String datosSesion,
                                                            @Body NmmConceptoDeduccion nmmConceptoDeduccion) {
        try {
            return HttpResponse.ok(percepcionDeduccionService.eliminaDeduccion(nmmConceptoDeduccion));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @Operation(summary = "${cosmonaut.controller.percepcion.eliminaPercepcionPolitica.resumen}",
            description = "${cosmonaut.controller.percepcion.eliminaPercepcionPolitica.descripcion}",
            operationId = "percepcion.eliminaPercepcionPolitica")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Percepciones politicas - Eliminar")
    @Post(value = "/eliminaPercepcionPolitica", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> eliminaPercepcionPolitica(@Header("datos-flujo") String datosFlujo,
                                                                     @Header("datos-sesion") String datosSesion,
                                                                     @Body NmmConfiguraPercepcion nmmConfiguraPercepcion) {
        try {
            return HttpResponse.ok(percepcionDeduccionService.eliminaPercepcionPolitica(nmmConfiguraPercepcion));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @Operation(summary = "${cosmonaut.controller.percepcion.eliminaDeduccionPolitica.resumen}",
            description = "${cosmonaut.controller.percepcion.eliminaDeduccionPolitica.descripcion}",
            operationId = "percepcion.eliminaDeduccionPolitica")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Deducciones politicas - Eliminar")
    @Post(value = "/eliminaDeduccionPolitica", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> eliminaDeduccionPolitica(@Header("datos-flujo") String datosFlujo,
                                                                    @Header("datos-sesion") String datosSesion,
                                                                    @Body NmmConfiguraDeduccion nmmConfiguraDeduccion) {
        try {
            return HttpResponse.ok(percepcionDeduccionService.eliminaDeduccionPolitica(nmmConfiguraDeduccion));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

}
