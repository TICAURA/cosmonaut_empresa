package mx.com.ga.cosmonaut.empresa.services.impl;

import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.entity.colaborador.NcoPreferencia;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.common.repository.colaborador.NcoPreferenciaRepository;
import mx.com.ga.cosmonaut.common.util.Constantes;
import mx.com.ga.cosmonaut.empresa.services.PreferenciasService;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PreferenciasServiceImpl implements PreferenciasService {

    @Inject
    private NcoPreferenciaRepository ncoPreferenciaRepository;

    @Override
    public RespuestaGenerica guardar(NcoPreferencia preferencia) throws ServiceException {
        try{
            RespuestaGenerica respuesta = validaCamposObligatorios(preferencia);
            if (respuesta.isResultado()){
                preferencia.setEsActivo(Constantes.ESTATUS_ACTIVO);
                respuesta.setDatos(ncoPreferenciaRepository.save(preferencia));
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
    public RespuestaGenerica modificar(NcoPreferencia preferencia) throws ServiceException {
        try{
            RespuestaGenerica respuesta =  new RespuestaGenerica();
            if (preferencia.getPreferenciaId() != null){
                respuesta = validaCamposObligatorios(preferencia);
                if (respuesta.isResultado()){
                    respuesta.setDatos(ncoPreferenciaRepository.update(preferencia));
                    respuesta.setResultado(Constantes.RESULTADO_EXITO);
                    respuesta.setMensaje(Constantes.EXITO);
                }
            }else{
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
                respuesta.setMensaje(Constantes.EXITO);
            }
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" modificar " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica obtenerId(Long idPreferencia) throws ServiceException {
        try{
            RespuestaGenerica respuesta = new RespuestaGenerica();
            respuesta.setDatos(
                    ncoPreferenciaRepository.findById(idPreferencia.intValue()).orElse(new NcoPreferencia()));
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            respuesta.setMensaje(Constantes.EXITO);
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" obtenerId " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica obtenerIdPersona(Long idPreferencia) throws ServiceException {
        try{
            RespuestaGenerica respuesta = new RespuestaGenerica();
            respuesta.setDatos(
                    ncoPreferenciaRepository.findByPersonaIdPersonaId(idPreferencia.intValue()));
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            respuesta.setMensaje(Constantes.EXITO);
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" obtenerId " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica eliminar(Long idPreferencia) throws ServiceException{
        try{
            if (idPreferencia != null){
                ncoPreferenciaRepository.update(idPreferencia.intValue(),Constantes.ESTATUS_INACTIVO);
                return new RespuestaGenerica(null,Constantes.RESULTADO_EXITO,Constantes.EXITO);
            }else {
                return new RespuestaGenerica(null,Constantes.RESULTADO_ERROR,Constantes.ID_NULO);
            }
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" eliminar " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private RespuestaGenerica validaCamposObligatorios(NcoPreferencia preferencia) throws ServiceException {
        try{
            RespuestaGenerica respuesta =  new RespuestaGenerica();
            if(preferencia.getPersonaId() == null
                    || preferencia.getPersonaId().getPersonaId() == null
                    || preferencia.getValor() == null
                    || preferencia.getValor().isEmpty()
                    || preferencia.getTipoPreferenciaId() == null
                    || preferencia.getTipoPreferenciaId().getTipoPreferenciaId() == null){
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
