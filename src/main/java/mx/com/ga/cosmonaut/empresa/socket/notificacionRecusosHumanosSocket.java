package mx.com.ga.cosmonaut.empresa.socket;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.micronaut.websocket.WebSocketBroadcaster;
import io.micronaut.websocket.WebSocketSession;
import io.micronaut.websocket.annotation.OnClose;
import io.micronaut.websocket.annotation.OnMessage;
import io.micronaut.websocket.annotation.OnOpen;
import io.micronaut.websocket.annotation.ServerWebSocket;
import mx.com.ga.cosmonaut.common.dto.chat.Mensajes;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.empresa.services.ChatServices;
import org.reactivestreams.Publisher;
import org.w3c.dom.stylesheets.LinkStyle;

import javax.inject.Inject;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.function.Predicate;


@ServerWebSocket("/notificaciones/{clienteid}/usuario/{idusuario}")
public class notificacionRecusosHumanosSocket {


    @Inject
    private WebSocketBroadcaster locutor;

    @Inject
    private ChatServices chatServices;

    private final GsonBuilder gsonBuilder = new GsonBuilder();
    private final Gson gson = gsonBuilder.create();



    @OnOpen
    public Publisher<String> abierto(String clienteid, WebSocketSession session) throws ServiceException {
        return locutor.broadcast("CONNECT", valido(clienteid,session));
    }

    @OnMessage
    public Publisher<String> mensaje(String clienteid,String idusuario,String mensaje, WebSocketSession session)
             {
                 String mensajeModificado = "";
                 if(!mensaje.contains("ACCEPTMESSAGE") && !mensaje.contains("READONLY")){
                     final Gson gson = new Gson();
                     final Type tipoListaMenesajes = new TypeToken<List<Mensajes>>(){}.getType();
                     List<Mensajes> objMensajes = gson.fromJson(mensaje,tipoListaMenesajes);
                     objMensajes.get(objMensajes.size()-1).setFecha(new Date());
                     mensajeModificado = gson.toJson(objMensajes);
                     this.chatServices.guardarConversacion(Integer.parseInt(clienteid),Integer.parseInt(idusuario),mensajeModificado);
                 }else{
                     mensajeModificado = mensaje;
                 }
        return locutor.broadcast(mensajeModificado, valido(clienteid,session));
    }
    @OnClose
    public Publisher<String> cerrar(String clienteid, WebSocketSession session) throws ServiceException {
        return locutor.broadcast("CLOSE", valido(clienteid,session));
    }

    private Predicate<WebSocketSession> valido(String clienteid,WebSocketSession session) {
        return s ->  s != session &&  clienteid.equalsIgnoreCase(s.getUriVariables().get("clienteid", String.class, null));
    }


}
