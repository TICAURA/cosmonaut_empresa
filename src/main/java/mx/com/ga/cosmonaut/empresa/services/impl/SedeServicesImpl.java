package mx.com.ga.cosmonaut.empresa.services.impl;

import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.entity.cliente.NclSede;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.common.repository.cliente.NclSedeRepository;
import mx.com.ga.cosmonaut.common.repository.nativo.DomicilioRepository;
import mx.com.ga.cosmonaut.common.util.Constantes;
import mx.com.ga.cosmonaut.empresa.services.SedeServices;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SedeServicesImpl implements SedeServices {

    @Inject
    private NclSedeRepository nclSedeRepository;

    @Inject
    private DomicilioRepository domicilioRepository;

    @Override
    public RespuestaGenerica guardar(NclSede sede) throws ServiceException {
        try{
            RespuestaGenerica respuesta = validaCamposObligatorios(sede);
            if (respuesta.isResultado()){
                sede.setEsActivo(Constantes.ESTATUS_ACTIVO);
                respuesta.setDatos(nclSedeRepository.save(sede));
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
    public RespuestaGenerica modificar(NclSede sede) throws ServiceException {
        try{
            RespuestaGenerica respuesta =  new RespuestaGenerica();
            if (sede.getSedeId() != null){
                respuesta = validaCamposObligatorios(sede);
                if (respuesta.isResultado()){
                    respuesta.setDatos(nclSedeRepository.update(sede));
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
    public RespuestaGenerica eliminar(Integer sedeId) throws ServiceException {
        try{
            RespuestaGenerica respuesta =  new RespuestaGenerica();
            if (sedeId != null){
                nclSedeRepository.update(sedeId,Constantes.ESTATUS_INACTIVO);
                respuesta.setDatos(null);
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
                respuesta.setMensaje(Constantes.EXITO);
            }else{
                respuesta = new RespuestaGenerica(null, Constantes.RESULTADO_ERROR, Constantes.ID_NULO);
            }
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" modificar " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica obtenerId(Long idSede) throws ServiceException {
        try{
            RespuestaGenerica respuesta = new RespuestaGenerica();
            respuesta.setDatos(
                    domicilioRepository.consultaDomicilioSedeId(idSede.intValue()));
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            respuesta.setMensaje(Constantes.EXITO);
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" obtenerId " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica obtenerIdCompania(Long idCompania) throws ServiceException {
        try{
            RespuestaGenerica respuesta = new RespuestaGenerica();
            respuesta.setDatos(
                    domicilioRepository.consultaDomicilioEmpresaId(idCompania.intValue()));
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            respuesta.setMensaje(Constantes.EXITO);
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" obtenerIdCompania " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica validaCamposObligatorios(NclSede sede) throws ServiceException {
        try{
            RespuestaGenerica respuesta =  new RespuestaGenerica();
            if(sede.getDescripcion() == null
                    || sede.getDescripcion().isEmpty()
                    || sede.getCentrocClienteId() == null
                    || sede.getCentrocClienteId().getCentrocClienteId() == null){
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

    @Override
    public RespuestaGenerica findByEsActivo(Boolean activo) throws ServiceException {
        try {
            return new RespuestaGenerica(
                    nclSedeRepository.findByEsActivoOrderByDescripcion(activo),
                    Constantes.RESULTADO_EXITO,
                    Constantes.EXITO);
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" findByEsActivo" + Constantes.ERROR_EXCEPCION, e);
        }
    }


}
