package mx.com.ga.cosmonaut.empresa.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import mx.com.ga.cosmonaut.common.dto.NmaCuentaBancoDto;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.entity.administracion.NmaCuentaBanco;
import mx.com.ga.cosmonaut.common.interceptor.BitacoraSistema;
import mx.com.ga.cosmonaut.common.util.Utilidades;
import mx.com.ga.cosmonaut.empresa.services.NmaCuentaBancoService;


@Controller("/cuentaBanco")
public class NmaCuentaBancoController {

    @Inject
    private NmaCuentaBancoService nmaCuentaBancoService;

    @BitacoraSistema
    @Operation(summary = "${cosmonaut.controller.cuentaBanco.guardar.resumen}",
            description = "${cosmonaut.controller.cuentaBanco.guardar.descripcion}",
            operationId = "cuentaBanco.guardar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Cuenta Bancaria - Guardar")
    @Put(value = "/guardar",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> guardar(@Header("datos-flujo") String datosFlujo,
                                                   @Header("datos-sesion") String datosSesion,
                                                   @Body NmaCuentaBancoDto nmaCuentaBancoDto){
        try {
            return HttpResponse.ok(nmaCuentaBancoService.guardar(nmaCuentaBancoDto));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @Operation(summary = "${cosmonaut.controller.cuentaBanco.guardarSTP.resumen}",
            description = "${cosmonaut.controller.cuentaBanco.guardarSTP.descripcion}",
            operationId = "cuentaBanco.guardarSTP")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Cuenta Bancaria - Guardar STP")
    @Put(value = "/guardarSTP",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> guardarSTP(@Header("datos-flujo") String datosFlujo,
                                                      @Header("datos-sesion") String datosSesion,
                                                      @Body NmaCuentaBancoDto nmaCuentaBancoDto){
        try {




            return HttpResponse.ok(nmaCuentaBancoService.guardarSTP(nmaCuentaBancoDto));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @Operation(summary = "${cosmonaut.controller.cuentaBanco.guardarLista.resumen}",
            description = "${cosmonaut.controller.cuentaBanco.guardarLista.descripcion}",
            operationId = "cuentaBanco.guardarLista")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Cuenta Bancaria - Guardar lista de cuentas bancarias")
    @Put(value = "/lista/guardar",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> guardaLista(@Header("datos-flujo") String datosFlujo,
                                                       @Header("datos-sesion") String datosSesion,
                                                       @Body List<NmaCuentaBancoDto> nmaCuentaBancoDto){
        try {
            return HttpResponse.ok(nmaCuentaBancoService.guardaLista(nmaCuentaBancoDto));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }
    

    @Operation(summary = "${cosmonaut.controller.cuentaBanco.findAll.resumen}",
            description = "${cosmonaut.controller.cuentaBanco.findAll.descripcion}",
            operationId = "cuentaBanco.findAll")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Cuenta Bancaria - Listar todos")
    @Get(value = "/listar/todos", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> findAll() {
        try {
            return HttpResponse.ok(nmaCuentaBancoService.findAll());
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }
    
     @Operation(summary = "${cosmonaut.controller.cuentaBanco.obtieneBanco.resumen}",
            description = "${cosmonaut.controller.cuentaBanco.obtieneBanco.descripcion}",
             operationId = "cuentaBanco.obtieneBanco")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Cuenta Bancaria - Obtiene detalle de banco por codigo ID")
    @Get(value = "/obtieneBanco/{codigo}", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> obtieneBanco(@PathVariable @NotNull String codigo) {
        try {
            return HttpResponse.ok(nmaCuentaBancoService.obtieneBanco(codigo));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }
    
    @Operation(summary = "${cosmonaut.controller.cuentaBanco.obtenerCuentaCliente.resumen}",
            description = "${cosmonaut.controller.cuentaBanco.obtenerCuentaCliente.descripcion}",
            operationId = "cuentaBanco.obtenerCuentaCliente")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Cuenta Bancaria - Obtener por empresa ID")
    @Get(value = "/obtener/cliente/{id}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> obtenerCuentaCliente(@PathVariable Integer id){
        try {
            return HttpResponse.ok(nmaCuentaBancoService.obtenerCuentaCliente(id));
        }catch (Exception e){
           return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }
    
    @Operation(summary = "${cosmonaut.controller.cuentaBanco.obtenerCuentaClienteFuncion.resumen}",
            description = "${cosmonaut.controller.cuentaBanco.obtenerCuentaClienteFuncion.descripcion}",
            operationId = "cuentaBanco.obtenerCuentaClienteFuncion")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Cuenta Bancaria - Obtener cuentas funcion ID por empresa ID")
    @Get(value = "/obtener/cuentaFuncion/cliente/{id}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> obtenerCuentaClienteFuncion(@PathVariable Integer id){
        try {
            return HttpResponse.ok(nmaCuentaBancoService.obtenerCuentaClienteFuncion(id));
        }catch (Exception e){
           return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }
    
    @Operation(summary = "${cosmonaut.controller.cuentaBanco.obtenerCuentaSTPCliente.resumen}",
            description = "${cosmonaut.controller.cuentaBanco.obtenerCuentaSTPCliente.descripcion}",
            operationId = "cuentaBanco.obtenerCuentaSTPCliente")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Cuenta Bancaria - Obtener Cuenta STP por empresa ID")
    @Get(value = "/obtener/STP/cliente/{id}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> obtenerCuentaSTPCliente(@PathVariable Integer id){
        try {
            return HttpResponse.ok(nmaCuentaBancoService.obtenerCuentaSTPCliente(id));
        }catch (Exception e){
           return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }
    
    @Operation(summary = "${cosmonaut.controller.cuentaBanco.findByNumeroCuenta.resumen}",
            description = "${cosmonaut.controller.cuentaBanco.findByNumeroCuenta.descripcion}",
            operationId = "cuentaBanco.findByNumeroCuenta")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Cuenta Bancaria - Obtener por Numero de Cuenta")
    @Get(value = "/obtener/id/{id}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> findByNumeroCuenta(@PathVariable String id){
        try {
            return HttpResponse.ok(nmaCuentaBancoService.findByNumeroCuenta(id));
        }catch (Exception e){
           return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }
    
    @Operation(summary = "${cosmonaut.controller.cuentaBanco.obtenerpersonaid.resumen}",
            description = "${cosmonaut.controller.cuentaBanco.obtenerpersonaid.descripcion}",
            operationId = "cuentaBanco.obtenerpersonaid")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Cuenta Bancaria - Obtener cuenta por persona ID")
    @Get(value = "/obtener/persona/{personaId}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> findByPersonaId(@PathVariable Integer personaId){
        try {
            return HttpResponse.ok(nmaCuentaBancoService.obtieneCuentaBancariaPersonaId(personaId));
        }catch (Exception e){
           return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @Operation(summary = "${cosmonaut.controller.cuentaBanco.modificar.resumen}",
            description = "${cosmonaut.controller.cuentaBanco.modificar.descripcion}",
            operationId = "cuentaBanco.modificar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Cuenta Bancaria - Modificar cuenta bancaria")
    @Post(value = "/modificar",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> modificar(@Header("datos-flujo") String datosFlujo,
                                                     @Header("datos-sesion") String datosSesion,
                                                     @Body NmaCuentaBancoDto nmaCuentaBancoDto){
        try {
            return HttpResponse.ok(nmaCuentaBancoService.modificar(nmaCuentaBancoDto));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @Operation(summary = "${cosmonaut.controller.cuentaBanco.modificarSTP.resumen}",
            description = "${cosmonaut.controller.cuentaBanco.modificarSTP.descripcion}",
            operationId = "cuentaBanco.modificarSTP")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Cuenta Bancaria - Modificar STP")
    @Post(value = "/modificarSTP",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> modificarSTP(@Header("datos-flujo") String datosFlujo,
                                                        @Header("datos-sesion") String datosSesion,
                                                        @Body NmaCuentaBancoDto nmaCuentaBancoDto){
        try {
            return HttpResponse.ok(nmaCuentaBancoService.modificarSTP(nmaCuentaBancoDto));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @Operation(summary = "${cosmonaut.controller.cuentaBanco.modificarLista.resumen}",
            description = "${cosmonaut.controller.cuentaBanco.modificarLista.descripcion}",
            operationId = "cuentaBanco.modificarLista")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Cuenta Bancaria - Modificar Lista de Cuentas Bancarias")
    @Post(value = "/lista/modificar",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> modificarLista(@Header("datos-flujo") String datosFlujo,
                                                          @Header("datos-sesion") String datosSesion,
                                                          @Body List<NmaCuentaBancoDto> listaNmaCuentaBancoDto){
        try {
            return HttpResponse.ok(nmaCuentaBancoService.modificarLista(listaNmaCuentaBancoDto));
        }catch (Exception e){
           return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }
    
    @Operation(summary = "${cosmonaut.controller.cuentaBanco.eliminar.resumen}",
            description = "${cosmonaut.controller.cuentaBanco.eliminar.descripcion}",
            operationId = "cuentaBanco.eliminar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Cuenta Bancaria - Elimina Cuenta Bancaria")
    @Post(value = "/eliminar/{cuentaBancoId}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> eliminar(@Header("datos-flujo") String datosFlujo,
                                                    @Header("datos-sesion") String datosSesion,
                                                    @PathVariable Integer cuentaBancoId){
        try {
            return HttpResponse.ok(nmaCuentaBancoService.eliminar(cuentaBancoId));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.cuentaBanco.findByEsActivo.resumen}",
            description = "${cosmonaut.controller.cuentaBanco.findByEsActivo.descripcion}",
            operationId = "cuentaBanco.findByEsActivo")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Cuenta Bancaria - lista todos activo/inactivo.")
    @Get(value = "/listar/todosActivo/{activo}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> findByEsActivo(@PathVariable Boolean activo){
        try {
            return HttpResponse.ok(nmaCuentaBancoService.findByEsActivo(activo));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.cuentaBanco.modificar.resumen}",
            description = "${cosmonaut.controller.cuentaBanco.modificar.descripcion}",
            operationId = "cuentaBanco.modificar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Cuenta Bancaria - Modificar cuenta bancaria")
    @Post(value = "/listar/cuenta/cliente/dinamica",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listarCuentaClienteDinamico(@Body NmaCuentaBanco cuentaBanco){
        try {
            return HttpResponse.ok(nmaCuentaBancoService.listarCuentaClienteDinamico(cuentaBanco));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }
}
