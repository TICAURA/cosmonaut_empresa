
package mx.com.ga.cosmonaut.empresa.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.inject.Inject;
import mx.com.ga.cosmonaut.common.dto.NclAreaDto;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.entity.cliente.NclEmpleadoXArea;
import mx.com.ga.cosmonaut.common.interceptor.BitacoraSistema;
import mx.com.ga.cosmonaut.common.interceptor.BitacoraUsuario;
import mx.com.ga.cosmonaut.common.util.Utilidades;
import mx.com.ga.cosmonaut.empresa.services.NclAreaService;
import mx.com.ga.cosmonaut.empresa.services.NclPuestoService;

@Controller("/area")
public class NclAreaController {

    @Inject
    private NclAreaService nclAreaService;
    
    @Inject
    private NclPuestoService nclPuestoService;

    @Operation(summary = "${cosmonaut.controller.area.findAll.resumen}",
            description = "${cosmonaut.controller.area.findAll.descripcion}",
            operationId = "area.findAll")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Area - Listar todas las areas")
    @Get(value = "/listar/todos", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> findAll() {
        try {
            return HttpResponse.ok(nclAreaService.findAll());
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.area.obtenerAreas.resumen}",
            description = "${cosmonaut.controller.area.obtenerAreas.descripcion}",
            operationId = "area.obtenerAreas")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Area - Listar numero de empleados por Area")
    @Get(value = "/listar/areas/{id}", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> obtenerAreas(@PathVariable Integer id) {
        try {
            return HttpResponse.ok(nclAreaService.obtenerAreas(id));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.area.obtenercliente.resumen}",
            description = "${cosmonaut.controller.area.obtenercliente.descripcion}",
            operationId = "area.obtenercliente")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Area - Obtener Area por empresa ID")
    @Get(value = "/obtener/idCliente/{id}", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> obtenerAreaXEmpresa(@PathVariable Integer id) {
        try {
            return HttpResponse.ok(nclAreaService.obtenerAreaXEmpresa(id));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.area.obtenerPuestoXEmpleado.resumen}",
            description = "${cosmonaut.controller.area.obtenerPuestoXEmpleado.descripcion}",
            operationId = "area.obtenerPuestoXEmpleado")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Area - Obtener Empleado por empresa ID")
    @Get(value = "/obtener/empleado/{id}/{idArea}", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> obtenerPuestoXEmpleado(@PathVariable Integer id, Integer idArea) {
        try {
            return HttpResponse.ok(nclAreaService.obtenerPuestoXEmpleado(id, idArea));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.area.findById.resumen}",
            description = "${cosmonaut.controller.area.findById.descripcion}",
            operationId = "area.findById")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Area - Obtener Area por ID")
    @Get(value = "/obtener/idArea/{id}", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> obtenerIdArea(@PathVariable Integer id) {
        try {
            return HttpResponse.ok(nclAreaService.obtenerIdArea(id));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @BitacoraUsuario
    @Operation(summary = "${cosmonaut.controller.area.guardar.resumen}",
            description = "${cosmonaut.controller.area.guardar.descripcion}",
            operationId = "area.guardar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Area - Guardar")
    @Put(value = "/guardar", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> guardar(@Header("datos-flujo") String datosFlujo,
                                                   @Header("datos-sesion") String datosSesion,
                                                   @Body NclAreaDto nclAreaDto) {
        try {
            return HttpResponse.ok(nclAreaService.guardar(nclAreaDto));
        } catch (Exception e) {
            if (e.getMessage().equals("Area repetida"))
                return HttpResponse.badRequest(Utilidades.respuestaRepetida("El nombre de esta área ya existe"));
                else
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }
    

    @BitacoraSistema
    @Operation(summary = "${cosmonaut.controller.area.agregarPuesto.resumen}",
            description = "${cosmonaut.controller.area.agregarPuesto.descripcion}",
            operationId = "area.agregarPuesto")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Area - Agregar Puesto")
    @Put(value = "/agregarPuesto", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> agregarPuesto(@Header("datos-flujo") String datosFlujo,
                                                         @Header("datos-sesion") String datosSesion,
                                                         @Body NclAreaDto nclAreaDto) {
        try {
            return HttpResponse.ok(nclAreaService.agregarPuesto(nclAreaDto));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @BitacoraUsuario
    @Operation(summary = "${cosmonaut.controller.area.modificar.resumen}",
            description = "${cosmonaut.controller.area.modificar.descripcion}",
            operationId = "area.modificar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Area - Modificar Area")
    @Post(value = "/modificar", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> modificar(@Header("datos-flujo") String datosFlujo,
                                                     @Header("datos-sesion") String datosSesion,
                                                     @Body NclAreaDto nclAreaDto) {
        try {
            return HttpResponse.ok(nclAreaService.modificar(nclAreaDto));
        } catch (Exception e) {
            if (e.getMessage().equals("Area repetida"))
                return HttpResponse.badRequest(Utilidades.respuestaRepetida("El nombre de esta área ya existe"));
            else
                return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @Operation(summary = "${cosmonaut.controller.area.eliminar.resumen}",
            description = "${cosmonaut.controller.area.eliminar.descripcion}",
            operationId = "area.eliminar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Area - Eliminar por Area ID / Cliente ID")
    @Post(value = "/eliminar", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> eliminarArea(@Header("datos-flujo") String datosFlujo,
                                                        @Header("datos-sesion") String datosSesion,
                                                        @Body NclAreaDto nclAreaDto) {
        try {
            return HttpResponse.ok(nclAreaService.eliminarArea(nclAreaDto));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.area.listardinamica.resumen}",
            description = "${cosmonaut.controller.area.listardinamica.descripcion}",
            operationId = "area.listardinamica")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Area - Lista dinámica")
    @Post(value = "/lista/dinamica", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listarDinamica(@Body NclEmpleadoXArea nclEmpleadoXArea) {
        try {
            return HttpResponse.ok(nclAreaService.listarDinamica(nclEmpleadoXArea));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }
    
    @Operation(summary = "${cosmonaut.controller.puesto.listarPuetos.resumen}",
            description = "${cosmonaut.controller.puesto.listarPuetos.descripcion}",
            operationId = "puesto.listarPuetos")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Puesto - Listar todos")
    @Get(value = "/listar/puestos/todos", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listarPuetos() {
        try {
            return HttpResponse.ok(nclPuestoService.findAll());
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.puesto.findById.resumen}",
            description = "${cosmonaut.controller.puesto.findById.descripcion}",
            operationId = "puesto.findById")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Puesto - Obtener puesto por ID")
    @Get(value = "/obtener/id/{id}", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> obtenerId(@PathVariable Integer id) {
        try {
            return HttpResponse.ok(nclPuestoService.obtenerId(id));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.puesto.findByclienteId.resumen}",
            description = "${cosmonaut.controller.puesto.findByclienteId.descripcion}",
            operationId = "puesto.findByclienteId")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Puesto - Obtener puesto por empresa ID")
    @Get(value = "/obtener/cliente/id/{id}", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> obtenerIdCentroCliente(@PathVariable Integer id) {
        try {
            return HttpResponse.ok(nclPuestoService.obtenerIdCentroCliente(id));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.puesto.findByclienteIdArea.resumen}",
            description = "${cosmonaut.controller.puesto.findByclienteIdArea.descripcion}",
            operationId = "puesto.findByclienteIdArea")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Puesto - Obtener puesto por empresa/area ID")
    @Get(value = "/obtener/cliente/area/{id}/{idArea}", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> obtenerIdCentroClienteArea(@PathVariable Integer id, Integer idArea) {
        try {
            return HttpResponse.ok(nclPuestoService.obtenerIdCentroClienteArea(id, idArea));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }
    
     @Operation(summary = "${cosmonaut.controller.puesto.obtenerPuestosXArea.resumen}",
            description = "${cosmonaut.controller.puesto.obtenerPuestosXArea.descripcion}",
             operationId = "puesto.obtenerPuestosXArea")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Puesto - Obtener puesto por area ID")
    @Get(value = "/obtener/puestos/{id}", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> obtenerPuestosXArea(@PathVariable Integer id) {
        try {
            return HttpResponse.ok(nclPuestoService.obtenerPuestosXArea(id));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.area.findByActivo.resumen}",
            description = "${cosmonaut.controller.area.findByActivo.descripcion}",
            operationId = "area.findByActivo")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Area - Obtener todos los activos/inactivos.")
    @Get(value = "/listar/todosActivo/{activo}", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> findByActivo(@PathVariable Boolean activo) {
        try {
            return HttpResponse.ok(nclAreaService.findByEsActivo(activo));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

}
