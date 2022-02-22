package mx.com.ga.cosmonaut.empresa.services.impl;

import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.entity.administracion.NmaMedioContacto;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.common.repository.administracion.NmaMedioContactoRepository;
import mx.com.ga.cosmonaut.common.util.Constantes;
import mx.com.ga.cosmonaut.empresa.services.MedioContactoServices;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MedioContactoServicesImpl implements MedioContactoServices {

    @Inject
    private NmaMedioContactoRepository nmaMedioContactoRepository;

    @Override
    public RespuestaGenerica guardar(NmaMedioContacto medioContacto) throws ServiceException {
        try{
            RespuestaGenerica respuesta = validaCamposObligatorios(medioContacto);
            if (respuesta.isResultado()){
                medioContacto.setEsActivo(Constantes.ESTATUS_ACTIVO);
                respuesta.setDatos(nmaMedioContactoRepository.save(medioContacto));
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
                respuesta.setMensaje(Constantes.EXITO);
            }
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" guardar " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica modificar(NmaMedioContacto medioContacto) throws ServiceException {
        try{
            RespuestaGenerica respuesta =  new RespuestaGenerica();
            if (medioContacto.getNumeroOcuenta() != null && !medioContacto.getNumeroOcuenta().isEmpty()){
                respuesta = validaCamposObligatorios(medioContacto);
                if (respuesta.isResultado()){
                    respuesta.setDatos(nmaMedioContactoRepository.update(medioContacto));
                    respuesta.setResultado(Constantes.RESULTADO_EXITO);
                    respuesta.setMensaje(Constantes.EXITO);
                }
            }else{
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(Constantes.ID_NULO);
            }
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" modificar " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica obtenerId(String numeroCuenta) throws ServiceException {
        try{
            RespuestaGenerica respuesta = new RespuestaGenerica();
            respuesta.setDatos(
                    nmaMedioContactoRepository.findById(numeroCuenta).orElse(new NmaMedioContacto()));
            respuesta.setMensaje(Constantes.EXITO);
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" obtenerId " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private RespuestaGenerica validaCamposObligatorios(NmaMedioContacto medioContacto) throws ServiceException {
        try{
            RespuestaGenerica respuesta =  new RespuestaGenerica();
            if(medioContacto.getNumeroOcuenta() == null
                    || medioContacto.getNumeroOcuenta().isEmpty()
                    || medioContacto.getCentrocClienteId() == null
                    || medioContacto.getCentrocClienteId().getCentrocClienteId() == null
                    || medioContacto.getPersonaId() == null
                    || medioContacto.getPersonaId().getPersonaId() == null
                    || medioContacto.getTipoContactoId() == null
                    || medioContacto.getTipoContactoId().getTipoContactoId() == null){
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(Constantes.CAMPOS_REQUERIDOS);
            }else{
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
