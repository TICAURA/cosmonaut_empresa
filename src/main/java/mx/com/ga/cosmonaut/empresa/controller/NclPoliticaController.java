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
import mx.com.ga.cosmonaut.common.dto.NclPoliticaDto;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.entity.cliente.BeneficioXpolitica;
import mx.com.ga.cosmonaut.common.entity.cliente.NclPolitica;
import mx.com.ga.cosmonaut.common.interceptor.BitacoraSistema;
import mx.com.ga.cosmonaut.common.interceptor.BitacoraUsuario;
import mx.com.ga.cosmonaut.common.util.Utilidades;
import mx.com.ga.cosmonaut.empresa.services.NclPoliticaService;


@Controller("/Politica")
public class NclPoliticaController {

    @Inject
    private NclPoliticaService nclPoliticaService;
    
     @Operation(summary = "${cosmonaut.controller.Politica.findAll.resumen}",
            description = "${cosmonaut.controller.Politica.findAll.descripcion}",
             operationId = "politica.findAll")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Politicas - Listar todos")
    @Get(value = "/listar/todos", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> findAll() {
        try {
            return HttpResponse.ok(nclPoliticaService.findAll());
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }
    
    @Operation(summary = "${cosmonaut.controller.Politica.findById.resumen}",
            description = "${cosmonaut.controller.Politica.findById.descripcion}",
            operationId = "politica.findById")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Politicas - Obtener politica por ID")
    @Get(value = "/obtener/politica/{id}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> consultaPoliticaId(@PathVariable Integer id){
        try {
            return HttpResponse.ok(nclPoliticaService.consultaPoliticaId(id));
        }catch (Exception e){
           return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }
    
    @Operation(summary = "${cosmonaut.controller.Politica.obtenerpoliticaempleado.resumen}",
            description = "${cosmonaut.controller.Politica.obtenerpoliticaempleado.descripcion}",
            operationId = "politica.obtenerpoliticaempleado")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Politicas - Obtener politica asociada a empleados por empresa ID / politica ID")
    @Get(value = "/obtener/politica/empresa/{id}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> consultaPoliticaXEmpPol(@PathVariable Integer id, Integer idCliente){
        try {
            return HttpResponse.ok(nclPoliticaService.consultaPoliticaXEmpPol(id, idCliente));
        }catch (Exception e){
           return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }
    
     @Operation(summary = "${cosmonaut.controller.Politica.obtenerpoliticasporempresa.resumen}",
            description = "${cosmonaut.controller.Politica.obtenerpoliticasporempresa.descripcion}",
             operationId = "politica.obtenerpoliticasporempresa")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Politicas - Obtener politicas por empresa ID")
    @Get(value = "/obtener/politica/idEmpresa/{id}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> consultaPoliticasXEmpresaId(@PathVariable Integer id){
        try {
            return HttpResponse.ok(nclPoliticaService.consultaPoliticasXEmpresaId(id));
        }catch (Exception e){
           return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }
    
    
    @Operation(summary = "${cosmonaut.controller.Politica.obtenerpoliticaempresa.resumen}",
            description = "${cosmonaut.controller.Politica.obtenerpoliticaempresa.descripcion}",
            operationId = "politica.obtenerpoliticaempresa")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Politicas - Obtener politicas por empresa ID / politica ID")
    @Get(value = "/obtener/politica/idCliente/{id}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> consultaPoliticaEmpresaId(@PathVariable Integer id, Integer idPolitica){
        try {
            return HttpResponse.ok(nclPoliticaService.consultaPoliticaEmpresaId(id, idPolitica));
        }catch (Exception e){
           return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }
    
    
    @Operation(summary = "${cosmonaut.controller.Politica.obtenerbeneficiospolitica.resumen}",
            description = "${cosmonaut.controller.Politica.obtenerbeneficiospolitica.descripcion}",
            operationId = "politica.obtenerbeneficiospolitica")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Politicas - Obtener beneficios por politica ID")
    @Get(value = "/obtener/beneficio/idCliente/{id}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> consultaBeneficiosPoliticaId(@PathVariable Integer id, Integer idCliente){
        try {
            return HttpResponse.ok(nclPoliticaService.consultaBeneficiosPoliticaId(id, idCliente));
        }catch (Exception e){
           return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @BitacoraUsuario
    @Operation(summary = "${cosmonaut.controller.Politica.guardar.resumen}",
            description = "${cosmonaut.controller.Politica.guardar.descripcion}",
             operationId = "politica.guardar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Politica - Guardar")
    @Put(value = "/guardar",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> guardar(@Header("datos-flujo") String datosFlujo,
                                                   @Header("datos-sesion") String datosSesion,
                                                   @Body NclPolitica nclPolitica){
        try {
            return HttpResponse.ok(nclPoliticaService.guardar(nclPolitica));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @Operation(summary = "${cosmonaut.controller.Politica.guardarEstandar.resumen}",
            description = "${cosmonaut.controller.Politica.guardarEstandar.descripcion}",
            operationId = "politica.guardarEstandar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Politica - Guardar Politica Estandar")
    @Put(value = "/guardarEstandar",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> guardaPoliticaEstandar(@Header("datos-flujo") String datosFlujo,
                                                                  @Header("datos-sesion") String datosSesion,
                                                                  @Body NclPolitica nclPolitica){
        try {
            return HttpResponse.ok(nclPoliticaService.guardaPoliticaEstandar(nclPolitica));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @Operation(summary = "${cosmonaut.controller.Politica.guardaBeneficiosEstandar.resumen}",
            description = "${cosmonaut.controller.Politica.guardaBeneficiosEstandar.descripcion}",
             operationId = "politica.guardaBeneficiosEstandar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Politica - Guardar Beneficios por Politica Estandar")
    @Put(value = "/guardarBeneficiosEstandar",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> guardaBeneficiosEstandar(@Header("datos-flujo") String datosFlujo,
                                                                    @Header("datos-sesion") String datosSesion,
                                                                    @Body List<BeneficioXpolitica> listabeneficioXpolitica){
        try {
            return HttpResponse.ok(nclPoliticaService.guardaBeneficiosEstandar(listabeneficioXpolitica));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @BitacoraUsuario
    @Operation(summary = "${cosmonaut.controller.Politica.modificar.resumen}",
            description = "${cosmonaut.controller.Politica.modificar.descripcion}",
            operationId = "politica.modificar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Politica - Modificar")
    @Post(value = "/modificar",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> modificar(@Header("datos-flujo") String datosFlujo,
                                                     @Header("datos-sesion") String datosSesion,
                                                     @Body NclPoliticaDto nclPolitica){
        try {
            return HttpResponse.ok(nclPoliticaService.modificar(nclPolitica));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @BitacoraUsuario
    @Operation(summary = "${cosmonaut.controller.Politica.eliminar.resumen}",
            description = "${cosmonaut.controller.Politica.eliminar.descripcion}",
            operationId = "politica.eliminar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Politica - Eliminar")
    @Post(value = "/eliminar",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> eliminar(@Header("datos-flujo") String datosFlujo,
                                                    @Header("datos-sesion") String datosSesion,
                                                    @Body NclPolitica nclPolitica){
        try {
            return HttpResponse.ok(nclPoliticaService.eliminar(nclPolitica));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.politica.findByActivo.resumen}",
            description = "${cosmonaut.controller.politica.findByActivo.descripcion}",
            operationId = "politica.findByActivo")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Politica - Obtener todos los activos/inactivos.")
    @Get(value = "/listar/todosActivo/{activo}", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> findByActivoPuesto(@PathVariable Boolean activo) {
        try {
            return HttpResponse.ok(nclPoliticaService.findByEsActivo(activo));
        } catch (Exception e) {
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }
}
