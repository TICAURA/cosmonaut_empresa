package mx.com.ga.cosmonaut.empresa.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import mx.com.ga.cosmonaut.common.dto.NclCentrocClienteDto;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.dto.ValidacionEmpresaDto;
import mx.com.ga.cosmonaut.common.interceptor.BitacoraSistema;
import mx.com.ga.cosmonaut.common.interceptor.BitacoraUsuario;
import mx.com.ga.cosmonaut.common.util.Constantes;
import mx.com.ga.cosmonaut.common.util.Utilidades;
import mx.com.ga.cosmonaut.empresa.services.CentroCostosClienteService;
import mx.com.ga.cosmonaut.empresa.services.ValidacionServices;

import javax.inject.Inject;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Controller("/centroCostosCliente")
public class CentroCostosClienteController {

    @Inject
    private CentroCostosClienteService centroCostosClienteService;

    @Inject
    private ValidacionServices validacionServices;

    @BitacoraSistema
    @Operation(summary = "${cosmonaut.controller.centrocliente.guardarcompania.resumen}",
            description = "${cosmonaut.controller.centrocliente.guardarcompania.descripcion}",
            operationId = "centrocliente.guardarcompania")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Centro de Costos/Cliente - Guardar Compañia")
    @Put(value = "/guardar/compania",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> guardar(@Header("datos-flujo") String datosFlujo,
                                                   @Header("datos-sesion") String datosSesion,
                                                   @Body NclCentrocClienteDto centroCostosClienteDto){
        try {
            return HttpResponse.ok(centroCostosClienteService.guardarCompania(centroCostosClienteDto));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }


    @BitacoraSistema
    @BitacoraUsuario
    @Operation(summary = "${cosmonaut.controller.centrocliente.guardarempresa.resumen}",
            description = "${cosmonaut.controller.centrocliente.guardarempresa.descripcion}",
            operationId = "centrocliente.guardarempresa")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Centro de Costos/Cliente - Guardar Empresa")
    @Put(value = "/guardar/empresa",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> guardarEmpresa(@Header("datos-flujo") String datosFlujo,
                                                          @Header("datos-sesion") String datosSesion,
                                                          @Body NclCentrocClienteDto centroCostosClienteDto){
        try {
            return HttpResponse.ok(centroCostosClienteService.guardarEmpresa(centroCostosClienteDto));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @Operation(summary = "${cosmonaut.controller.centrocliente.modificarcompania.resumen}",
            description = "${cosmonaut.controller.centrocliente.modificarcompania.descripcion}",
            operationId = "centrocliente.modificarcompania")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Centro de Costos/Cliente - Modificar compañia")
    @Post(value = "/modificar/compania",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> modificarCompania(@Header("datos-flujo") String datosFlujo,
                                                             @Header("datos-sesion") String datosSesion,
                                                             @Body NclCentrocClienteDto centroCostosClienteDto){
        try {
            return HttpResponse.ok(centroCostosClienteService.modificarCompania(centroCostosClienteDto));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @BitacoraUsuario
    @Operation(summary = "${cosmonaut.controller.centrocliente.modificarempresa.resumen}",
            description = "${cosmonaut.controller.centrocliente.modificarempresa.descripcion}",
            operationId = "centrocliente.modificarempresa")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Centro de Costos/Cliente - Modificar empresa")
    @Post(value = "/modificar/empresa",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> modificarEmpresa(@Header("datos-flujo") String datosFlujo,
                                                            @Header("datos-sesion") String datosSesion,
                                                            @Body NclCentrocClienteDto centroCostosClienteDto){
        try {
            return HttpResponse.ok(centroCostosClienteService.modificarEmpresa(centroCostosClienteDto));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @Operation(summary = "${cosmonaut.controller.centrocliente.modificarlista.resumen}",
            description = "${cosmonaut.controller.centrocliente.modificarlista.descripcion}",
            operationId = "centrocliente.modificarlista")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Centro de Costos/Cliente - Modificacion masiva")
    @Post(value = "/modificar/lista",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> modificarLista(@Header("datos-flujo") String datosFlujo,
                                                          @Header("datos-sesion") String datosSesion,
                                                          @Body List<NclCentrocClienteDto> listaCentroCostosClienteDto){
        try {
            return HttpResponse.ok(centroCostosClienteService.modificarLista(listaCentroCostosClienteDto));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.centrocliente.eliminar.resumen}",
            description = "${cosmonaut.controller.centrocliente.eliminar.descripcion}",
            operationId = "centrocliente.eliminar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Centro de Costos/Cliente - Eliminar por id")
    @Post(value = "/eliminar/id/{id}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> eliminarId(@Header("datos-flujo") String datosFlujo,
                                                      @Header("datos-sesion") String datosSesion,
                                                      @PathVariable Long id){
        try {
            return HttpResponse.ok(centroCostosClienteService.eliminarId(id));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.centrocliente.eliminarempresa.resumen}",
            description = "${cosmonaut.controller.centrocliente.eliminarempresa.descripcion}",
            operationId = "centrocliente.eliminarempresa")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Centro de Costos/Cliente - Eliminar por id de empresa")
    @Post(value = "/eliminar/empresa/id/{id}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> eliminarEmpresa(@Header("datos-flujo") String datosFlujo,
                                                           @Header("datos-sesion") String datosSesion,
                                                           @PathVariable Long id){
        try {
            return HttpResponse.ok(centroCostosClienteService.eliminarEmpresa(id));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.centrocliente.listarcompania.resumen}",
            description = "${cosmonaut.controller.centrocliente.listarcompania.descripcion}",
            operationId = "centrocliente.listarcompania")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Centro de Costos/Cliente - Listar centro de costos")
    @Get(value = "/lista/compania",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listaCompania(){
        try {
            return HttpResponse.ok(centroCostosClienteService.listarCompania());
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.centrocliente.listarcompaniasimple.resumen}",
            description = "${cosmonaut.controller.centrocliente.listarcompaniasimple.descripcion}",
            operationId = "centrocliente.listarcompaniasimple")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Centro de Costos/Cliente - Listar centro de costos de forma simple")
    @Get(value = "/lista/compania/simple",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listaCompaniaSimple(){
        try {
            return HttpResponse.ok(centroCostosClienteService.listarCompaniaSimple());
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.centrocliente.listarcompaniaempresa.resumen}",
            description = "${cosmonaut.controller.centrocliente.listarcompaniaempresa.descripcion}",
            operationId = "centrocliente.listarcompaniaempresa")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Centro de Costos/Cliente - Listar cliente por centro de costos")
    @Get(value = "/lista/compania/empresa/{id}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listaCompaniaEmpresa(@PathVariable Long id){
        try {
            return HttpResponse.ok(centroCostosClienteService.listaCompaniaEmpresa(id));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.centrocliente.listarempresasimple.resumen}",
            description = "${cosmonaut.controller.centrocliente.listarempresasimple.descripcion}",
            operationId = "centrocliente.listarempresasimple")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Centro de Costos/Cliente - Listar cliente por centro de costos")
    @Get(value = "/lista/empresa/simple",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listaEmpresaSimple(){
        try {
            return HttpResponse.ok(centroCostosClienteService.listaEmpresaSimple());
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.centrocliente.obtenerid.resumen}",
            description = "${cosmonaut.controller.centrocliente.obtenerid.descripcion}",
            operationId = "centrocliente.obtenerid")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Centro de Costos/Cliente - Obtener por ID")
    @Get(value = "/obtener/id/{id}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> obtenerId(@NotBlank @PathVariable Long id){
        try {
            return HttpResponse.ok(centroCostosClienteService.obtenerId(id));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.centrocliente.listadinamica.resumen}",
            description = "${cosmonaut.controller.centrocliente.listadinamica.descripcion}",
            operationId = "centrocliente.listadinamica")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Centro de Costos/Cliente - Lista dinamica")
    @Post(value = "/lista/dinamica",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listaDinamica(@Body NclCentrocClienteDto centroCostosClienteDto){
        try {
            return HttpResponse.ok(centroCostosClienteService.listaDinamica(centroCostosClienteDto));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.centrocliente.validacionempresa.resumen}",
            description = "${cosmonaut.controller.centrocliente.validacionempresa.descripcion}",
            operationId = "centrocliente.validacionempresa")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Centro de Costos/Cliente - Validar captura de empresa")
    @Get(value = "/validacion/captura/empresa/{id}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<ValidacionEmpresaDto> validacionEmpresa(@PathVariable Long id){
        try {
            return HttpResponse.ok(validacionServices.validarPantallaEmpresa(id));
        }catch (Exception e){
            ValidacionEmpresaDto validacionDto = new ValidacionEmpresaDto();
            validacionDto.setRespuesta(Constantes.RESULTADO_ERROR);
            validacionDto.setMensaje(Constantes.ERROR);
            return HttpResponse.badRequest(validacionDto);
        }
    }

    @Operation(summary = "${cosmonaut.controller.centrocliente.login.resumen}",
            description = "${cosmonaut.controller.centrocliente.login.descripcion}",
            operationId = "centrocliente.login")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Centro de Costos/Cliente - Login")
    @Get(value = "/login/{correo}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> login(@PathVariable String correo){
        try {
            return HttpResponse.ok(centroCostosClienteService.login(correo));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.centrocliente.listarcompania.resumen}",
            description = "${cosmonaut.controller.centrocliente.listarcompaniapaginado.descripcion}",
            operationId = "centrocliente.listarcompania")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Centro de Costos/Cliente - Listar centro de costos")
    @Get(value = "/lista/compania/paginado/{numeroRegistros}/{pagina}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listaCompaniaPaginado(@PathVariable Integer numeroRegistros,
                                                                 @PathVariable  Integer pagina){
        try {
            return HttpResponse.ok(centroCostosClienteService.listarCompaniaPaginado(numeroRegistros,pagina));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.centrocliente.listadinamicapaginado.resumen}",
            description = "${cosmonaut.controller.centrocliente.listadinamicapaginado.descripcion}",
            operationId = "centrocliente.listadinamicapaginado")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Centro de Costos/Cliente - Lista dinamica")
    @Post(value = "/lista/dinamica/paginado/{numeroRegistros}/{pagina}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listaDinamicaPaginado(@Body NclCentrocClienteDto centroCostosClienteDto,@PathVariable Integer numeroRegistros,
                                                                 @PathVariable  Integer pagina){
        try {
            return HttpResponse.ok(centroCostosClienteService.listaDinamicaPaginado(centroCostosClienteDto,numeroRegistros,pagina));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.centrocliente.listarcompaniacombo.resumen}",
            description = "${cosmonaut.controller.centrocliente.listarcompaniacombo.descripcion}",
            operationId = "centrocliente.listarcompaniacombo")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Centro de Costos/Cliente - Listar centro de costos")
    @Get(value = "/lista/compania/combo",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listaCompaniaCombo(){
        try {
            return HttpResponse.ok(centroCostosClienteService.listarCompaniaCombo());
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }
}
