package mx.com.ga.cosmonaut.empresa.socket;

import io.micronaut.websocket.WebSocketBroadcaster;
import io.micronaut.websocket.WebSocketSession;
import io.micronaut.websocket.annotation.OnClose;
import io.micronaut.websocket.annotation.OnMessage;
import io.micronaut.websocket.annotation.OnOpen;
import io.micronaut.websocket.annotation.ServerWebSocket;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.empresa.services.ChatServices;
import org.reactivestreams.Publisher;

import javax.inject.Inject;
import java.util.function.Predicate;

@ServerWebSocket("/websocket/chat/{canal}/{idEmpresa}/{idUsuario}")
public class ChatWebSocket {




    @Inject
    private WebSocketBroadcaster locutor;

    @Inject
    private ChatServices chatServices;

    @OnOpen
    public Publisher<String> abierto(String canal, String idEmpresa, String idUsuario, WebSocketSession session) throws ServiceException {
        RespuestaGenerica respuestaGenerica = chatServices.obtenMensajeAbrirCanal(idEmpresa,idUsuario,session.getRequestURI().toString());
        String msg = (String) respuestaGenerica.getDatos();
        return locutor.broadcast(msg, valido(canal));
    }

    @OnMessage
    public Publisher<String> mensaje(String canal, String idEmpresa, String idUsuario, String mensajes, WebSocketSession session)
            throws ServiceException {
        return locutor.broadcast(chatServices.enviarMensaje(session.getRequestURI().toString(),mensajes), valido(canal));
    }

    @OnClose
    public Publisher<String> cerrar(String canal, String idEmpresa, String idUsuario, WebSocketSession session) throws ServiceException {
        return locutor.broadcast(chatServices.cerrar(idUsuario), valido(canal));
    }

    private Predicate<WebSocketSession> valido(String canal) {
        return s -> canal.equalsIgnoreCase(s.getUriVariables().get("canal", String.class, null));
    }

}
