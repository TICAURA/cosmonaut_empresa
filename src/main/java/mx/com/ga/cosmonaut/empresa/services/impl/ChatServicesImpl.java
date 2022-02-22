package mx.com.ga.cosmonaut.empresa.services.impl;

import mx.com.ga.cosmonaut.common.dto.NcoChatColaboradorDto;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.entity.administracion.usuarios.AdmUsuarios;
import mx.com.ga.cosmonaut.common.entity.cliente.NclCentrocCliente;
import mx.com.ga.cosmonaut.common.entity.colaborador.NcoChatColaborador;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.common.repository.administracion.usuarios.AdmUsuariosRepository;
import mx.com.ga.cosmonaut.common.repository.colaborador.NcoChatColaboradorRepository;
import mx.com.ga.cosmonaut.common.repository.nativo.NcoChatColaboradorCustom;
import mx.com.ga.cosmonaut.common.util.Constantes;
import mx.com.ga.cosmonaut.empresa.services.ChatServices;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Singleton
public class ChatServicesImpl implements ChatServices {

    @Inject
    private NcoChatColaboradorRepository ncoChatColaboradorRepository;

    @Inject
    private NcoChatColaboradorCustom ncoChatColaboradorCustom;

    @Inject
    private AdmUsuariosRepository admUsuariosRepository;

    @Override
    public RespuestaGenerica modificar(NcoChatColaborador chatColaborador) throws ServiceException {
        try{
            ncoChatColaboradorRepository.update(chatColaborador);
            return new RespuestaGenerica(null, Constantes.RESULTADO_EXITO, Constantes.EXITO);
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" modificar " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public String cerrar(String usuarioId) throws ServiceException {
        try{
            AdmUsuarios usuarios =
                    admUsuariosRepository.findById(Integer.valueOf(usuarioId))
                            .orElseThrow(() -> new ServiceException("No se encontro el usuario"));

            return "Se desconecto el usuario " + usuarios.getNombre() + " " + usuarios.getApellidoPat();
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" eliminar " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica listar(Integer idEmpresa,Integer usuarioId) throws ServiceException {
        try{

            RespuestaGenerica respuesta = new RespuestaGenerica();
            List<NcoChatColaboradorDto> lista = ncoChatColaboradorCustom.chatListado(idEmpresa);
            List<NcoChatColaboradorDto> listaGeneral = lista.stream().filter(o -> !o.isAtendido()).collect(Collectors.toList());
            List<NcoChatColaboradorDto> listaUsuario = lista.stream().filter(o -> o.getIdUsuarioRrh().equals(usuarioId)).collect(Collectors.toList());
            listaGeneral.addAll(listaUsuario);
            respuesta.setDatos(listaGeneral);
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            respuesta.setMensaje(Constantes.EXITO);

            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" listar " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica listarUsuario(Integer idEmpresa, Integer idUsuario) throws ServiceException {
        try{

            RespuestaGenerica respuesta = new RespuestaGenerica();
            List<NcoChatColaborador> lista = ncoChatColaboradorRepository.
                    findByCentrocClienteIdCentrocClienteIdAndUsuarioIdUsuarioId(idEmpresa,idUsuario);

            respuesta.setDatos(lista);
            respuesta.setMensaje(Constantes.EXITO);
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" listarUsuario " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica obtenMensajeAbrirCanal(String idEmpresa, String idUsuario, String conversacionId) throws ServiceException {
        try{
            if (!ncoChatColaboradorRepository.existsByUsuarioIdUsuarioId(Integer.valueOf(idUsuario))){
                guardar(idEmpresa, idUsuario, conversacionId);
            }

            AdmUsuarios usuarios =
                    admUsuariosRepository.findById(Integer.valueOf(idUsuario))
                    .orElseThrow(() -> new ServiceException("No se encontro el usuario"));

            return new RespuestaGenerica("Bienvenid@ " + usuarios.getNombre() + " " + usuarios.getApellidoPat(),
                    Constantes.RESULTADO_EXITO, Constantes.EXITO);
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" obtenMensajeAbrirCanal " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public String enviarMensaje(String conversacionId, String mensajes) throws ServiceException {
        try{
            ncoChatColaboradorRepository.updateByConversacionId(conversacionId,mensajes);
            return mensajes;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" enviarMensaje " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica eliminar(NcoChatColaborador usuarioId) throws ServiceException {
        try{
            ncoChatColaboradorRepository.update(usuarioId.getChatColaboradorId(),usuarioId.getMensajes(),usuarioId.isEsActual());
            return new RespuestaGenerica(null, Constantes.RESULTADO_EXITO, Constantes.EXITO);
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" eliminar " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public void guardarConversacion(Integer clienteId, Integer usuarioId, String mensaje) {
        NcoChatColaborador resultado = null;
        if (!ncoChatColaboradorRepository.existsByUsuarioIdUsuarioId(Integer.valueOf(usuarioId))){
            NcoChatColaborador chatColaborador = new NcoChatColaborador();
            chatColaborador.setUsuarioId(new AdmUsuarios());
            chatColaborador.getUsuarioId().setUsuarioId(Integer.valueOf(usuarioId));
            chatColaborador.setCentrocClienteId(new NclCentrocCliente());
            chatColaborador.getCentrocClienteId().setCentrocClienteId(Integer.valueOf(clienteId));
            chatColaborador.setEsActual(Constantes.ESTATUS_ACTIVO);
            chatColaborador.setConversacionId("");
            chatColaborador.setIdUsuarioRrh(0);
            chatColaborador.setMensajes(mensaje);
            chatColaborador.setFechaUltimoMensaje(new Timestamp(System.currentTimeMillis()));
            chatColaborador.setConversacionId(String.format("/notificaciones/%1$s/usuario/%2$s",clienteId.toString()+usuarioId.toString(),usuarioId));
            ncoChatColaboradorRepository.save(chatColaborador);
        }else{
            NcoChatColaborador chatActual = ncoChatColaboradorRepository.findByUsuarioIdUsuarioId(usuarioId);
            chatActual.setFechaUltimoMensaje(new Timestamp(System.currentTimeMillis()));
            chatActual.setMensajes(mensaje);
            resultado =  ncoChatColaboradorRepository.update(chatActual);
        }


    }

    @Override
    public RespuestaGenerica getMensahesChatEmpleado(Integer usuarioId) throws ServiceException {
        RespuestaGenerica respuesta = new RespuestaGenerica();
        if(this.ncoChatColaboradorRepository.existsByUsuarioIdUsuarioId(usuarioId)){
            respuesta.setDatos(this.ncoChatColaboradorRepository.findByUsuarioIdUsuarioId(usuarioId));
        }
        respuesta.setMensaje(Constantes.EXITO);
        respuesta.setResultado(Constantes.RESULTADO_EXITO);
        return respuesta;
    }

    @Override
    public RespuestaGenerica eliminar(Long conversacionid) throws ServiceException {
        RespuestaGenerica respuesta = new RespuestaGenerica();

        this.ncoChatColaboradorRepository.deleteById(conversacionid);
        respuesta.setResultado(Constantes.RESULTADO_EXITO);
        respuesta.setDatos(Constantes.EXITO);
        return respuesta;
    }

    private void guardar(String idEmpresa, String idUsuario, String conversacionId) throws ServiceException {
        try{
            RespuestaGenerica respuesta = validaCamposObligatorios(idEmpresa, idUsuario, conversacionId);
            if (respuesta.isResultado()){
                NcoChatColaborador chatColaborador = new NcoChatColaborador();
                chatColaborador.setUsuarioId(new AdmUsuarios());
                chatColaborador.getUsuarioId().setUsuarioId(Integer.valueOf(idUsuario));
                chatColaborador.setCentrocClienteId(new NclCentrocCliente());
                chatColaborador.getCentrocClienteId().setCentrocClienteId(Integer.valueOf(idEmpresa));
                chatColaborador.setEsActual(Constantes.ESTATUS_ACTIVO);
                chatColaborador.setConversacionId(conversacionId);
                respuesta.setDatos(ncoChatColaboradorRepository.save(chatColaborador));
            }
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" guardar " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private RespuestaGenerica validaCamposObligatorios(String idEmpresa, String idUsuario,String url) throws ServiceException {
        try{
            RespuestaGenerica respuesta = new RespuestaGenerica();
            if(idEmpresa == null || idEmpresa.isEmpty()
                    || idUsuario == null || idUsuario.isEmpty()
                    || url == null || url.isEmpty()){
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(Constantes.CAMPOS_REQUERIDOS);
            }else {
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
                respuesta.setMensaje(Constantes.EXITO);
            }
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" validaCamposObligatorios " + Constantes.ERROR_EXCEPCION, e);
        }
    }

}
