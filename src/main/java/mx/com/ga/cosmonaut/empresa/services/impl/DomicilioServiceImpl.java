package mx.com.ga.cosmonaut.empresa.services.impl;

import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.entity.administracion.NmaDomicilio;
import mx.com.ga.cosmonaut.common.entity.cliente.NclSede;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.common.repository.administracion.NmaDomicilioRepository;
import mx.com.ga.cosmonaut.common.repository.nativo.DomicilioRepository;
import mx.com.ga.cosmonaut.common.util.Constantes;
import mx.com.ga.cosmonaut.empresa.services.DomicilioService;
import mx.com.ga.cosmonaut.empresa.services.SedeServices;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DomicilioServiceImpl implements DomicilioService {

    @Inject
    private NmaDomicilioRepository nmaDomicilioRepository;

    @Inject
    private SedeServices sedeServices;

    @Inject
    private DomicilioRepository domicilioRepository;

    @Override
    public RespuestaGenerica guardar(NmaDomicilio domicilio) throws ServiceException {
        try{
            RespuestaGenerica respuesta = validaCamposObligatorios(domicilio);
            if (respuesta.isResultado()){
                respuesta.setDatos(nmaDomicilioRepository.save(domicilio));
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
    public RespuestaGenerica guardarSedeDomicilio(NmaDomicilio domicilio) throws ServiceException {
        try{
            RespuestaGenerica respuesta = sedeServices.validaCamposObligatorios(domicilio.getSedeId());
            if (respuesta.isResultado()){
                respuesta = validaCamposObligatorios(domicilio);
                if (respuesta.isResultado()){
                    RespuestaGenerica respuestaSede = sedeServices.guardar(domicilio.getSedeId());
                    domicilio.setSedeId((NclSede) respuestaSede.getDatos());
                    respuesta.setDatos(nmaDomicilioRepository.save(domicilio));
                    respuesta.setResultado(Constantes.RESULTADO_EXITO);
                    respuesta.setMensaje(Constantes.EXITO);
                }
            }
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" guardarSedeDomicilio " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica modificar(NmaDomicilio domicilio) throws ServiceException {
        try{
            if (domicilio.getDomicilioId() != null){
                RespuestaGenerica respuesta = validaCamposObligatorios(domicilio);
                if (respuesta.isResultado()){
                    respuesta.setDatos(nmaDomicilioRepository.update(domicilio));
                    respuesta.setResultado(Constantes.RESULTADO_EXITO);
                    respuesta.setMensaje(Constantes.EXITO);
                }
                return respuesta;
            }else{
                return new RespuestaGenerica(null, Constantes.RESULTADO_ERROR, Constantes.ID_NULO);
            }
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" modificar " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica obtenerId(Long idDomicilio) throws ServiceException {
        try{
            RespuestaGenerica respuesta = new RespuestaGenerica();
            respuesta.setDatos(
                    nmaDomicilioRepository.findById(idDomicilio.intValue()).orElse(new NmaDomicilio()));
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            respuesta.setMensaje(Constantes.EXITO);
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" obtenerId " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica obtenerIdEmpresaDomicilio(Long idEmpresa) throws ServiceException {
        try{
            RespuestaGenerica respuesta = new RespuestaGenerica();
            respuesta.setDatos(
                    nmaDomicilioRepository.findByCentrocClienteId(idEmpresa.intValue()));
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            respuesta.setMensaje(Constantes.EXITO);
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" obtenerIdEmpresaDomicilio " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica obtenerIdPersona(Long idPersona) throws ServiceException {
        try{
            RespuestaGenerica respuesta = new RespuestaGenerica();
            respuesta.setDatos(
                    nmaDomicilioRepository.findByPersonaIdPersonaId(idPersona.intValue()));
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            respuesta.setMensaje(Constantes.EXITO);
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" obtenerIdPersona " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica obtenerIdPersonaNativo(Long idPersona) throws ServiceException {
        try{
            RespuestaGenerica respuesta = new RespuestaGenerica();
            respuesta.setDatos(
                    domicilioRepository.consultaDomicilioEmleado(idPersona.intValue()));
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            respuesta.setMensaje(Constantes.EXITO);
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" obtenerIdPersonaNativo " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica obtenerIdEmpresa(Long idEmpresa) throws ServiceException {
        try{
            RespuestaGenerica respuesta = new RespuestaGenerica();
            respuesta.setDatos(
                    nmaDomicilioRepository.findByCentrocClienteIdCentrocClienteId(idEmpresa.intValue()));
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            respuesta.setMensaje(Constantes.EXITO);
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" obtenerIdEmpresa " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private RespuestaGenerica validaCamposObligatorios(NmaDomicilio domicilio) throws ServiceException {
        try{
            RespuestaGenerica respuesta =  new RespuestaGenerica();
            if(domicilio.getCodigo() == null
                    || domicilio.getMunicipio() == null
                    || domicilio.getAsentamientoId() == null
                    || domicilio.getEstado() == null
                    || domicilio.getCalle() == null
                    || domicilio.getCalle().isEmpty()
                    || domicilio.getNumExterior() == null
                    || domicilio.getNumExterior().isEmpty()){
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
