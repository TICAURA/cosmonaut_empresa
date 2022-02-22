package mx.com.ga.cosmonaut.empresa.services;

import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.entity.colaborador.NcoChatColaborador;
import mx.com.ga.cosmonaut.common.exception.ServiceException;

public interface ChatServices {

    RespuestaGenerica obtenMensajeAbrirCanal(String empresaId, String usuarioId, String conversacionId) throws ServiceException;

    RespuestaGenerica modificar(NcoChatColaborador chatColaborador) throws ServiceException;

    String cerrar(String usuarioId) throws ServiceException;

    RespuestaGenerica listar(Integer idEmpresa,Integer usuarioId) throws ServiceException;

    RespuestaGenerica listarUsuario(Integer idEmpresa, Integer idUsuario) throws ServiceException;

    String enviarMensaje(String conversacionId, String mensajes) throws ServiceException;

    RespuestaGenerica eliminar(NcoChatColaborador usuarioId) throws ServiceException;

    void guardarConversacion(Integer clienteId,Integer usuarioId,String mensaje);

    RespuestaGenerica getMensahesChatEmpleado(Integer usuarioId) throws ServiceException;

    RespuestaGenerica eliminar(Long conversacionid) throws ServiceException;


}
