package mx.com.ga.cosmonaut.empresa.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import mx.com.ga.cosmonaut.common.dto.DocumentosEmpleadoDto;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.interceptor.BitacoraSistema;
import mx.com.ga.cosmonaut.common.service.DocumentosEmpleadoService;
import mx.com.ga.cosmonaut.common.util.Utilidades;
import mx.com.ga.cosmonaut.empresa.services.DocumentosService;

import javax.inject.Inject;

@Controller("/documentos")
public class DocumentosEmpleadoController {

    @Inject
    private DocumentosService documentosService;

    @Inject
    private DocumentosEmpleadoService documentosEmpleadoService;

    @BitacoraSistema
    @Operation(summary = "${cosmonaut.controller.cargadocumentos.guardar.resumen}",
            description = "${cosmonaut.controller.cargadocumentos.guardar.descripcion}",
            operationId = "cargadocumentos.guardar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Carga de Documentos - Guardar documentos")
    @Put(value = "/guardar",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> guardar(@Header("datos-flujo") String datosFlujo,
                                                   @Header("datos-sesion") String datosSesion,
                                                   @Body DocumentosEmpleadoDto documentosEmpleadoDto){
        try {
            return HttpResponse.ok(documentosEmpleadoService.guardar(documentosEmpleadoDto));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.cargadocumentos.descargar.resumen}",
            description = "${cosmonaut.controller.cargadocumentos.descargar.descripcion}",
            operationId = "cargadocumentos.descargar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Carga de Documentos - Descargar documentos")
    @Get(value = "/descargar/{cmsArchivoId}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> descargar(@PathVariable Integer cmsArchivoId){
        try {
            return HttpResponse.ok(documentosEmpleadoService.descargar(cmsArchivoId));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @Operation(summary = "${cosmonaut.controller.cargadocumentos.remplazar.resumen}",
            description = "${cosmonaut.controller.cargadocumentos.remplazar.descripcion}",
            operationId = "cargadocumentos.remplazar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Carga de Documentos - Remplazar documentos")
    @Post(value = "/remplazar",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> remplazar(@Header("datos-flujo") String datosFlujo,
                                                     @Header("datos-sesion") String datosSesion,
                                                     @Body DocumentosEmpleadoDto documentosEmpleadoDto){
        try {
            return HttpResponse.ok(documentosEmpleadoService.remplazar(documentosEmpleadoDto));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.cargadocumentos.lista.resumen}",
            description = "${cosmonaut.controller.cargadocumentos.lista.descripcion}",
            operationId = "cargadocumentos.lista")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Carga de Documentos - Lista documentos")
    @Get(value = "/lista/{centroClienteId}/{personaId}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> lista(@PathVariable Integer centroClienteId,@PathVariable Integer personaId){
        try {
            return HttpResponse.ok(documentosEmpleadoService.lista(centroClienteId,personaId));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.cargadocumentos.listatipodocumento.resumen}",
            description = "${cosmonaut.controller.cargadocumentos.listatipodocumento.descripcion}",
            operationId = "cargadocumentos.listatipodocumento")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Carga de Documentos - Lista documentos")
    @Get(value = "/lista/tipo-documento/{centroClienteId}/{personaId}/{tipoDocumentoId}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listaTipoDocumento(@PathVariable Integer centroClienteId,
                                                 @PathVariable Integer personaId,
                                                 @PathVariable Integer tipoDocumentoId){
        try {
            return HttpResponse.ok(documentosEmpleadoService.listaTipoDocumento(centroClienteId,personaId,tipoDocumentoId));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.cargadocumentos.obtenertipodocumentos.resumen}",
            description = "${cosmonaut.controller.cargadocumentos.obtenertipodocumentos.descripcion}",
            operationId = "cargadocumentos.obtenertipodocumentos")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Carga de Documentos - Obtener Tipo Documentos")
    @Get(value = "/obtener/tipo/documentos",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> obtenerTipoDocumentos(){
        try {
            return HttpResponse.ok(documentosEmpleadoService.obtenerTipoDocumentos());
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.cargadocumentos.eliminardocumentos.resumen}",
            description = "${cosmonaut.controller.cargadocumentos.eliminardocumentos.descripcion}",
            operationId = "cargadocumentos.eliminardocumentos")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Eliminar Documento - Eliminar documento")
    @Delete(value = "/eliminar/documentos/{documentoId}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> eliminar(@Header("datos-flujo") String datosFlujo,
                                                    @Header("datos-sesion") String datosSesion,
                                                    @PathVariable Integer documentoId){
        try {
            return HttpResponse.ok(documentosService.eliminar(documentoId));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

}
