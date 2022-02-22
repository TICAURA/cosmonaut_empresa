
package mx.com.ga.cosmonaut.empresa.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.inject.Inject;
import javax.validation.constraints.NotBlank;
import mx.com.ga.cosmonaut.common.dto.JornadasDto;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.entity.cliente.NclJornada;
import mx.com.ga.cosmonaut.common.interceptor.BitacoraSistema;
import mx.com.ga.cosmonaut.common.interceptor.BitacoraUsuario;
import mx.com.ga.cosmonaut.common.util.Utilidades;
import mx.com.ga.cosmonaut.empresa.services.JornadasService;

@Controller("/jornadas")
public class JornadasController {
    
    @Inject
    private JornadasService jornadasService;

    @BitacoraSistema
    @BitacoraUsuario
    @Operation(summary = "${cosmonaut.controller.jornadas.guardar.resumen}",
            description = "${cosmonaut.controller.jornadas.guardar.descripcion}",
            operationId = "jornadas.guardar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Jornadas - Guardar")
    @Put(value = "/guardar", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> guardar(@Header("datos-flujo") String datosFlujo,
                                                   @Header("datos-sesion") String datosSesion,
                                                   @Body JornadasDto jornadasDto) {
        try {
            return HttpResponse.ok(jornadasService.guardar(jornadasDto));
        } catch (Exception e) {
            if (e.getMessage().equals("Jornada duplicada"))
                return HttpResponse.badRequest(Utilidades.respuestaRepetida("El nombre de esta jornada ya existe"));
            else
                return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @BitacoraUsuario
    @Operation(summary = "${cosmonaut.controller.jornadas.modificar.resumen}",
            description = "${cosmonaut.controller.jornadas.modificar.descripcion}",
            operationId = "jornadas.modificar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Jornadas - Modificar")
    @Post(value = "/modificar", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> modificar(@Header("datos-flujo") String datosFlujo,
                                                     @Header("datos-sesion") String datosSesion,
                                                     @Body JornadasDto jornadasDto) {
        try {
            return HttpResponse.ok(jornadasService.modificar(jornadasDto));
        } catch (Exception e) {
            if (e.getMessage().equals("Jornada duplicada"))
                return HttpResponse.badRequest(Utilidades.respuestaRepetida("El nombre de esta jornada ya existe"));
            else
                return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }
    
     @Operation(summary = "${cosmonaut.controller.jornadas.duplicar.resumen}",
            description = "${cosmonaut.controller.jornadas.duplicar.descripcion}",
             operationId = "jornadas.duplicar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Jornadas - Duplicar")
    @Put(value = "/duplicar/{id}", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> duplicar(@PathVariable @NotBlank Integer id) {
        try {
            return HttpResponse.ok(jornadasService.duplicarJornada(id));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @BitacoraUsuario
    @Operation(summary = "${cosmonaut.controller.jornadas.eliminar.resumen}",
            description = "${cosmonaut.controller.jornadas.eliminar.descripcion}",
            operationId = "jornadas.eliminar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Jornadas - Eliminar")
    @Post(value = "/eliminar", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> eliminar(@Header("datos-flujo") String datosFlujo,
                                                    @Header("datos-sesion") String datosSesion,
                                                    @Body NclJornada nclJornada) {
        try {
            return HttpResponse.ok(jornadasService.eliminar(nclJornada));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }
        
    @Operation(summary = "${cosmonaut.controller.jornadas.empleadosJornada.resumen}",
            description = "${cosmonaut.controller.jornadas.empleadosJornada.descripcion}",
            operationId = "jornadas.empleadosJornada")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Jornadas  - Obtiene Empleados Asociados a Jornada")
    @Get(value = "/listar/{idEmpresa}/{idJornada}", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> consultaEmpleadosJornadaEmpresa(@PathVariable Integer idEmpresa, Integer idJornada) {
        try {
            return HttpResponse.ok(jornadasService.consultaEmpleadosJornadaEmpresa(idEmpresa,idJornada));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }
    
    @Operation(summary = "${cosmonaut.controller.jornadas.jornadaEmpresa.resumen}",
            description = "${cosmonaut.controller.jornadas.jornadaEmpresa.descripcion}",
            operationId = "jornadas.jornadaEmpresa")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Jornadas  - Obtiene Jornada por Empresa ID / Jornada ID")
    @Get(value = "/listar/jornada/{idEmpresa}/{idJornada}", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> consultaJornadaXEmpresa(@PathVariable Integer idEmpresa, Integer idJornada) {
        try {
            return HttpResponse.ok(jornadasService.consultaJornadasEmpresa(idEmpresa,idJornada));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }
    
    @Operation(summary = "${cosmonaut.controller.jornadas.obtieneJornadasEmpresa.resumen}",
            description = "${cosmonaut.controller.jornadas.obtieneJornadasEmpresa.descripcion}",
            operationId = "jornadas.obtieneJornadasEmpresa")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Jornadas  - Obtiene Jornadas por Empresa ID")
    @Get(value = "/listar/jornada/{idEmpresa}", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> obtieneJornadasXEmpresa(@PathVariable Integer idEmpresa) {
        try {
            return HttpResponse.ok(jornadasService.obtieneJornadasXEmpresa(idEmpresa));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.jornadas.findByEsActivo.resumen}",
            description = "${cosmonaut.controller.jornadas.findByEsActivo.descripcion}",
            operationId = "jornadas.findByEsActivo")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Jornadas  - Lista todos activo/inactivo.")
    @Get(value = "/listar/todosActivo/{activo}", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> findByEsActivo(@PathVariable Boolean activo) {
        try {
            return HttpResponse.ok(jornadasService.findByActivo(activo));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }
    
}
