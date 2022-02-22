package mx.com.ga.cosmonaut.empresa.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.dto.cliente.FiltrarRequest;
import mx.com.ga.cosmonaut.common.dto.cliente.GuardarRequest;
import mx.com.ga.cosmonaut.common.dto.cliente.ModificarRequest;
import mx.com.ga.cosmonaut.common.interceptor.BitacoraSistema;
import mx.com.ga.cosmonaut.common.util.Utilidades;
import mx.com.ga.cosmonaut.empresa.services.NclCentroClienteXproveedorService;

import javax.inject.Inject;
import javax.validation.Valid;

@Controller("/proveedores")
public class NclCentroClienteXproveedorController {

    @Inject
    private NclCentroClienteXproveedorService nclCentroClienteXproveedorService;

    @BitacoraSistema
    @Operation(summary = "${cosmonaut.controller.proveedores.guardar.resumen}",
            description = "${cosmonaut.controller.proveedores.guardar.descripcion}",
            operationId = "proveedores.guardar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Proveedores - Asigna un proveedor de dispersion y de timbrado a un cliente")
    @Put(value = "/guardar",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> guardar(@Header("datos-flujo") String datosFlujo,
                                                   @Header("datos-sesion") String datosSesion,
                                                   @Body @Valid GuardarRequest request){
        try {
            return HttpResponse.ok(nclCentroClienteXproveedorService.guardar(request));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @Operation(summary = "${cosmonaut.controller.proveedores.modificar.resumen}",
            description = "${cosmonaut.controller.proveedores.modificar.descripcion}",
            operationId = "proveedores.modificar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Proveedores - Modifica el proveedor de dispersion y de timbrado a un cliente")
    @Post(value = "/modificar",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> modificar(@Header("datos-flujo") String datosFlujo,
                                                     @Header("datos-sesion") String datosSesion,
                                                     @Body @Valid ModificarRequest request){
        try {
            return HttpResponse.ok(nclCentroClienteXproveedorService.modificar(request));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.proveedores.listar.resumen}",
            description = "${cosmonaut.controller.proveedores.listar.descripcion}",
            operationId = "proveedores.listar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Proveedores - Lista los proveedores de dispersion y de timbrado")
    @Get(value = "/listar",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listar(){
        try {
            return HttpResponse.ok(nclCentroClienteXproveedorService.listar());
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.proveedores.obtener.resumen}",
            description = "${cosmonaut.controller.proveedores.obtener.descripcion}",
            operationId = "proveedores.obtener")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Proveedores - Obtiene un registro de los proveedores de dispersion y de timbrado")
    @Get(value = "/obtener/{id}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> obtener(Integer id){
        try {
            return HttpResponse.ok(nclCentroClienteXproveedorService.obtener(id));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.proveedores.filtrar.resumen}",
            description = "${cosmonaut.controller.proveedores.filtrar.descripcion}",
            operationId = "proveedores.filtrar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Proveedores - Lista los proveedores de dispersion y de timbrado por cliente/empresa")
    @Post(value = "/filtrar",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> filtrar(@Body @Valid FiltrarRequest request){
        try {
            return HttpResponse.ok(nclCentroClienteXproveedorService.filtrar(request));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.proveedores.filtrarpaginado.resumen}",
            description = "${cosmonaut.controller.proveedores.filtrarpaginado.descripcion}",
            operationId = "proveedores.filtrar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Proveedores - Lista los proveedores de dispersion y de timbrado por cliente/empresa")
    @Post(value = "/filtrar/paginado/{numeroRegistros}/{pagina}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> filtrarPaginado(@Body @Valid FiltrarRequest request,
                                                   @PathVariable Integer numeroRegistros,
                                                   @PathVariable  Integer pagina){
        try {
            return HttpResponse.ok(nclCentroClienteXproveedorService.filtrarPaginado(request,numeroRegistros,pagina));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

}
