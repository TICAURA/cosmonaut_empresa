package mx.com.ga.cosmonaut.empresa.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import mx.com.ga.cosmonaut.common.dto.CargaMasivaDto;
import mx.com.ga.cosmonaut.common.dto.NcoPersonaDto;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.dto.ValidacionEmpleadoDto;
import mx.com.ga.cosmonaut.common.entity.colaborador.NcoPersona;
import mx.com.ga.cosmonaut.common.interceptor.BitacoraSistema;
import mx.com.ga.cosmonaut.common.interceptor.BitacoraUsuario;
import mx.com.ga.cosmonaut.common.util.Constantes;
import mx.com.ga.cosmonaut.common.util.Utilidades;
import mx.com.ga.cosmonaut.empresa.services.EmpleadoService;
import mx.com.ga.cosmonaut.empresa.services.PersonaService;
import mx.com.ga.cosmonaut.empresa.services.ValidacionServices;

import javax.inject.Inject;
import java.util.List;

@Controller("/persona")
public class PersonaController {

    @Inject
    private PersonaService personaService;

    @Inject
    private EmpleadoService empleadoService;

    @Inject
    private ValidacionServices validacionServices;

    @BitacoraSistema
    @BitacoraUsuario
    @Operation(summary = "${cosmonaut.controller.persona.guardar.representantelegal.resumen}",
            description = "${cosmonaut.controller.persona.guardar.representantelegal.descripcion}",
            operationId = "persona.guardar.representantelegal")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Persona - Guardar Representante Legal")
    @Put(value = "/guardar/representanteLegal",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> guardarRepresentanteLegal(@Header("datos-flujo") String datosFlujo,
                                                                     @Header("datos-sesion") String datosSesion,
                                                                     @Body NcoPersonaDto ncoPersonaDto){
        try {
            return HttpResponse.ok(personaService.guardarRepresentanteLegal(ncoPersonaDto));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @BitacoraUsuario
    @Operation(summary = "${cosmonaut.controller.persona.guardar.contactoinicial.resumen}",
            description = "${cosmonaut.controller.persona.guardar.contactoinicial.descripcion}",
            operationId = "persona.guardar.contactoinicial")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Persona - Guardar Contacto Inicial")
    @Put(value = "/guardar/contacto/inicial",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> guardarContactoInicial(@Header("datos-flujo") String datosFlujo,
                                                                  @Header("datos-sesion") String datosSesion,
                                                                  @Body NcoPersonaDto ncoPersonaDto){
        try {
            return HttpResponse.ok(personaService.guardarContactoInicial(ncoPersonaDto));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @BitacoraUsuario
    @Operation(summary = "${cosmonaut.controller.persona.guardar.recursoshumanos.resumen}",
            description = "${cosmonaut.controller.persona.guardar.recursoshumanos.descripcion}",
            operationId = "persona.guardar.recursoshumanos")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Persona - Guardar Contacto Recursos Humanos")
    @Put(value = "/guardar/contacto/recursosHumanos",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> guardarContactoRH(@Header("datos-flujo") String datosFlujo,
                                                             @Header("datos-sesion") String datosSesion,
                                                             @Body NcoPersonaDto ncoPersonaDto){
        try {
            return HttpResponse.ok(personaService.guardarContactoRH(ncoPersonaDto));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @Operation(summary = "${cosmonaut.controller.persona.guardar.empleado.resumen}",
            description = "${cosmonaut.controller.persona.guardar.empleado.descripcion}",
            operationId = "persona.guardar.empleado")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Persona - Guardar Empleado")
    @Put(value = "/guardar/empleado",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> guardarEmpleado(@Header("datos-flujo") String datosFlujo,
                                                           @Header("datos-sesion") String datosSesion,
                                                           @Body NcoPersonaDto ncoPersonaDto){
        try {
            return HttpResponse.ok(empleadoService.guardar(ncoPersonaDto));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @BitacoraUsuario
    @Operation(summary = "${cosmonaut.controller.persona.guardar.apoderadolegal.resumen}",
            description = "${cosmonaut.controller.persona.guardar.apoderadolegal.descripcion}",
            operationId = "persona.guardar.apoderadolegal")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Persona - Guardar Apoderado Legal")
    @Put(value = "/guardar/apoderadoLegal",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> guardarApoderadoLegal(@Header("datos-flujo") String datosFlujo,
                                                                 @Header("datos-sesion") String datosSesion,
                                                                 @Body NcoPersonaDto ncoPersonaDto){
        try {
            return HttpResponse.ok(personaService.guardarApoderadoLegal(ncoPersonaDto));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @BitacoraUsuario
    @Operation(summary = "${cosmonaut.controller.persona.modificar.representantelegal.resumen}",
            description = "${cosmonaut.controller.persona.modificar.representantelegal.descripcion}",
            operationId = "persona.modificar.representantelegal")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Persona - Modificar representante legal")
    @Post(value = "/modificar/representanteLegal",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> modificarRepresentanteLegal(@Header("datos-flujo") String datosFlujo,
                                                                       @Header("datos-sesion") String datosSesion,
                                                                       @Body NcoPersonaDto ncoPersonaDto){
        try {
            return HttpResponse.ok(personaService.modificarRepresentanteLegal(ncoPersonaDto));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @BitacoraUsuario
    @Operation(summary = "${cosmonaut.controller.persona.modificar.contactoinicial.resumen}",
            description = "${cosmonaut.controller.persona.modificar.contactoinicial.descripcion}",
            operationId = "persona.modificar.contactoinicial")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Persona - Modificar contacto inicial")
    @Post(value = "/modificar/contacto/inicial",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> modificarContactoInicial(@Header("datos-flujo") String datosFlujo,
                                                                    @Header("datos-sesion") String datosSesion,
                                                                    @Body NcoPersonaDto ncoPersonaDto){
        try {
            return HttpResponse.ok(personaService.modificarContactoInicial(ncoPersonaDto));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @BitacoraUsuario
    @Operation(summary = "${cosmonaut.controller.persona.modificar.contactorh.resumen}",
            description = "${cosmonaut.controller.persona.modificar.contactorh.descripcion}",
            operationId = "persona.modificar.contactorh")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Persona - Modificar contacto Recursos Humanos")
    @Post(value = "/modificar/contactoRH",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> modificarContactoRH(@Header("datos-flujo") String datosFlujo,
                                                               @Header("datos-sesion") String datosSesion,
                                                               @Body NcoPersonaDto ncoPersonaDto){
        try {
            return HttpResponse.ok(personaService.modificarContactoRH(ncoPersonaDto));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @BitacoraUsuario
    @Operation(summary = "${cosmonaut.controller.persona.modificar.apoderadolegal.resumen}",
            description = "${cosmonaut.controller.persona.modificar.apoderadolegal.descripcion}",
            operationId = "persona.modificar.apoderadolegal")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Persona - Modificar Aoderado Legal")
    @Post(value = "/modificar/apoderadoLegal",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> modificarApoderadoLegal(@Header("datos-flujo") String datosFlujo,
                                                                   @Header("datos-sesion") String datosSesion,
                                                                   @Body NcoPersonaDto ncoPersonaDto){
        try {
            return HttpResponse.ok(personaService.modificarApoderadoLegal(ncoPersonaDto));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @Operation(summary = "${cosmonaut.controller.persona.modificar.empleado.resumen}",
            description = "${cosmonaut.controller.persona.modificar.empleado.descripcion}",
            operationId = "persona.modificar.empleado")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Persona - Modificar Empleado")
    @Post(value = "/modificar/empleado",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> modificarEmpleado(@Header("datos-flujo") String datosFlujo,
                                                             @Header("datos-sesion") String datosSesion,
                                                             @Body NcoPersonaDto ncoPersonaDto){
        try {
            return HttpResponse.ok(empleadoService.modificar(ncoPersonaDto));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @Operation(summary = "${cosmonaut.controller.persona.listarmodificar.resumen}",
            description = "${cosmonaut.controller.persona.listarmodificar.descripcion}",
            operationId = "persona.listarmodificar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Persona - Modificar Lista")
    @Post(value = "/modificar/lista",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> modificarLista(@Header("datos-flujo") String datosFlujo,
                                                          @Header("datos-sesion") String datosSesion,
                                                          @Body List<NcoPersona> listaNcoPersonaDto){
        try {
            return HttpResponse.ok(personaService.modificarLista(listaNcoPersonaDto));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.persona.eliminar.resumen}",
            description = "${cosmonaut.controller.persona.eliminar.descripcion}",
            operationId = "persona.eliminar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Persona - Eliminar por ID")
    @Post(value = "/eliminar/id/{id}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> eliminar(@Header("datos-flujo") String datosFlujo,
                                                    @Header("datos-sesion") String datosSesion,
                                                    @PathVariable Long id){
        try {
            return HttpResponse.ok(personaService.eliminar(id));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.persona.obtenerid.resumen}",
            description = "${cosmonaut.controller.persona.obtenerid.descripcion}",
            operationId = "persona.obtenerid")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Persona - Obtener por ID")
    @Get(value = "/obtener/id/{id}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> obtenerId(@PathVariable Long id){
        try {
            return HttpResponse.ok(personaService.obtenerId(id));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.persona.obteneridcompania.resumen}",
            description = "${cosmonaut.controller.persona.obteneridcompania.descripcion}",
            operationId = "persona.obteneridcompania")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Persona - Obtener por ID compañia")
    @Get(value = "/obtener/id/compania/{id}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> obtenerIdCompania(@PathVariable Long id){
        try {
            return HttpResponse.ok(personaService.obtenerIdCompania(id));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.persona.obteneremailcorporativocentrocclienteId.resumen}",
            description = "${cosmonaut.controller.persona.obteneremailcorporativocentrocclienteId.descripcion}",
            operationId = "persona.obteneremailcorporativocentrocclienteId")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Persona - Obtener por correo y centro cliente")
    @Get(value = "/obtener/correo/centrocliente/{correo}/{centroclienteId}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> obtenerEmailCorporativoCentrocClienteId(@PathVariable String correo,
                                                                                   @PathVariable Long centroclienteId){
        try {
            return HttpResponse.ok(personaService.obtenerEmailCorporativoCentrocClienteId(correo,centroclienteId));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.persona.obtenerDetalleEventos.resumen}",
            description = "${cosmonaut.controller.persona.obtenerDetalleEventos.descripcion}",
            operationId = "persona.obtenerDetalleEventos")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Persona - Obtener por ID los detalles de los Eventos de la persona")
    @Get(value = "/obtener/detalle/eventos/{id}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> obtenerDetalleEventos(@PathVariable Long id){
        try {
            return HttpResponse.ok(personaService.obtenerDetalleEventos(id));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.persona.lista.resumen}",
            description = "${cosmonaut.controller.persona.lista.descripcion}",
            operationId = "persona.lista")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Persona - Listar todos")
    @Get(value = "/lista/todo/{tipoPersona}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listarTodos(@PathVariable Integer tipoPersona){
        try {
            return HttpResponse.ok(personaService.listarTodos(tipoPersona));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.persona.lista.compania.resumen}",
            description = "${cosmonaut.controller.persona.lista.compania.descripcion}",
            operationId = "persona.lista.compania")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Persona - Listar compañia por tipo de persona")
    @Post(value = "/lista/compania/tipoPersona",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listaCompaniaPersona(@Body NcoPersonaDto ncoPersonaDto) {
        try {
            return HttpResponse.ok(personaService.listarCompaniaPersona(ncoPersonaDto));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.persona.lista.empleadoincompleto.resumen}",
            description = "${cosmonaut.controller.persona.lista.empleadoincompleto.descripcion}",
            operationId = "persona.lista.empleadoincompleto")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Persona - Listar empleado incompleto")
    @Get(value = "/lista/empleado/incompleto/{id}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listaEmpleadoIncompleto(@PathVariable Long id) {
        try {
            return HttpResponse.ok(personaService.listaEmpleadoIncompleto(id));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.persona.listardinamica.resumen}",
            description = "${cosmonaut.controller.persona.listardinamica.descripcion}",
            operationId = "persona.listardinamica")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Persona - Lista dinámica")
    @Post(value = "/lista/dinamica",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listarDinamica(@Body NcoPersonaDto ncoPersonaDto){
        try {
            return HttpResponse.ok(personaService.listarDinamica(ncoPersonaDto));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.persona.validacionempleado.resumen}",
            description = "${cosmonaut.controller.persona.validacionempleado.descripcion}",
            operationId = "persona.validacionempleado")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Persona - Validar captura empleado")
    @Get(value = "/validacion/captura/empleado/{id}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<ValidacionEmpleadoDto> validacionEmpleado(@PathVariable Long id){
        try {
            return HttpResponse.ok(validacionServices.validarPantallaEmpleado(id));
        }catch (Exception e){
            ValidacionEmpleadoDto validacionEmpleado = new ValidacionEmpleadoDto();
            validacionEmpleado.setRespuesta(Constantes.RESULTADO_ERROR);
            validacionEmpleado.setMensaje(Constantes.ERROR);
            return HttpResponse.badRequest(validacionEmpleado);
        }
    }

    @Operation(summary = "${cosmonaut.controller.persona.lista.listacompaniapersonaestatus.resumen}",
            description = "${cosmonaut.controller.persona.lista.listacompaniapersonaestatus.descripcion}",
            operationId = "persona.lista.listacompaniapersonaestatus")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Persona - Listar compañia por tipo de persona y estatus")
    @Post(value = "/lista/compania/tipoPersona/estatus",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listaCompaniaPersonaEstatus(@Body NcoPersonaDto ncoPersonaDto) {
        try {
            return HttpResponse.ok(empleadoService.listaCompaniaPersonaEstatus(ncoPersonaDto));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @Operation(summary = "${cosmonaut.controller.persona.carga.masivaempleados.resumen}",
            description = "${cosmonaut.controller.persona.carga.masivaempleados.descripcion}",
            operationId = "persona.carga.masivaempleados")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Persona - Carga Masiva de empleados")
    @Post(value = "/carga/masiva/empleados",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> cargaMasivaEmpleados(@Header("datos-flujo") String datosFlujo,
                                                                @Header("datos-sesion") String datosSesion,
                                                                @Body CargaMasivaDto cargaMasivaDto) {
        try {
            return HttpResponse.ok(empleadoService.cargaMasivaXEmpleado(cargaMasivaDto));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.persona.lista.cargamasivaempleados.resumen}",
            description = "${cosmonaut.controller.persona.lista.cargamasivaempleados.descripcion}",
            operationId = "persona.lista.cargamasivaempleados")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Persona - Lista carga masiva de empleados")
    @Get(value = "/lista/carga/masiva/empleados/{centroClienteId}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listaCargaMasivaEmpleados(@PathVariable Integer centroClienteId) {
        try {
            return HttpResponse.ok(empleadoService.listaCargaMasivaEmpleados(centroClienteId));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.persona.lista.cargamasivaempleadosestatus.resumen}",
            description = "${cosmonaut.controller.persona.lista.cargamasivaempleadosestatus.descripcion}",
            operationId = "persona.lista.cargamasivaempleadosestatus")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Persona - Lista carga masiva de empleados")
    @Get(value = "/lista/carga/masiva/empleados/estatus/{centroClienteId}/{isEsCorrecto}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listaCargaMasivaEmpleadosEsCorrecto(@PathVariable Integer centroClienteId,Boolean isEsCorrecto) {
        try {
            return HttpResponse.ok(empleadoService.listaCargaMasivaEmpleadosEsCorrecto(centroClienteId, isEsCorrecto));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.persona.validarFechaFinPago.resumen}",
            description = "${cosmonaut.controller.persona.validarFechaFinPago.descripcion}",
            operationId = "persona.validarFechaFinPago")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Persona - Validar fecha fin ultimo pago")
    @Get(value = "/validarFechaFinPago/{personaId}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> validarFechaFinPago(@PathVariable Integer personaId) {
        try {
            return HttpResponse.ok(personaService.validarFechaFinPago(personaId));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

}
