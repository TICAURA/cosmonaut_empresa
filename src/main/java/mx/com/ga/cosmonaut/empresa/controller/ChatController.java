package mx.com.ga.cosmonaut.empresa.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.entity.colaborador.NcoChatColaborador;
import mx.com.ga.cosmonaut.common.util.Utilidades;
import mx.com.ga.cosmonaut.empresa.services.ChatServices;

import javax.inject.Inject;

@Controller("/chat")
public class ChatController {

    @Inject
    private ChatServices chatServices;

    @Operation(summary = "${cosmonaut.controller.chat.listar.resumen}",
            description = "${cosmonaut.controller.chat.listar.descripcion}",
            operationId = "chat.listar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Chat - Listar")
    @Get(value = "/listar/{idEmpresa}/usuario/{idUsuario}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listar(@PathVariable Integer idEmpresa,@PathVariable Integer idUsuario){
        try {
            return HttpResponse.ok(chatServices.listar(idEmpresa,idUsuario));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    
    @Operation(summary = "${cosmonaut.controller.chat.listarusuario.resumen}",
            description = "${cosmonaut.controller.chat.listarusuario.descripcion}",
            operationId = "chat.listarusuario")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Chat - Listar por usuario")
    @Get(value = "/listar/usuario/{idEmpresa}/{idUsuario}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listarUsuario(@PathVariable Integer idEmpresa, @PathVariable Integer idUsuario){
        try {
            return HttpResponse.ok(chatServices.listarUsuario(idEmpresa,idUsuario));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.chat.modificar.resumen}",
            description = "${cosmonaut.controller.chat.modificar.descripcion}",
            operationId = "chat.modificar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Chat - Modificar")
    @Post(value = "/modificar/",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> modificar(@Body NcoChatColaborador chatColaborador){
        try {
            return HttpResponse.ok(chatServices.modificar(chatColaborador));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.chat.eliminar.resumen}",
            description = "${cosmonaut.controller.chat.eliminar.descripcion}",
            operationId = "chat.eliminar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Chat - Eliminar")
    @Post(value = "/eliminar/",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> eliminar(@Body NcoChatColaborador chatColaborador){
        try {
            return HttpResponse.ok(chatServices.eliminar(chatColaborador));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.chat.empleadomensajes.resumen}",
            description = "${cosmonaut.controller.chat.empleadomensajes.descripcion}",
            operationId = "chat.empleadomensajes")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Get(value = "/empleado/mensajes/{idUsuario}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> getListaMensajesEmpleado(@PathVariable Integer idUsuario){
        try {
            return HttpResponse.ok(chatServices.getMensahesChatEmpleado(idUsuario));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.chat.terminar.resumen}",
            description = "${cosmonaut.controller.chat.terminar.descripcion}",
            operationId = "chat.terminar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Delete(value = "/terminar/{id}",consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> eliminarConversacion(@PathVariable Long id){
        try {
            return HttpResponse.ok(chatServices.eliminar(id));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

}
