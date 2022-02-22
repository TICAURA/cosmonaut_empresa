package mx.com.ga.cosmonaut.empresa.services.impl;

import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.entity.PagosLiquidacionColaborador;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.common.repository.PagosLiquidacionColaboradorRepository;
import mx.com.ga.cosmonaut.common.util.Constantes;
import mx.com.ga.cosmonaut.empresa.services.PagosLiquidacionColaboradorServices;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PagosLiquidacionColaboradorServicesImpl implements PagosLiquidacionColaboradorServices {

    @Inject
    private PagosLiquidacionColaboradorRepository pagosLiquidacionColaboradorRepository;

    @Override
    public RespuestaGenerica guardar(PagosLiquidacionColaborador liquidacionColaborador) throws ServiceException {
        try{
            RespuestaGenerica respuesta = validaCamposObligatorios(liquidacionColaborador);
            if (respuesta.isResultado()){
                respuesta.setDatos(pagosLiquidacionColaboradorRepository.save(liquidacionColaborador));
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
                respuesta.setMensaje(Constantes.EXITO);
            }
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" guardar " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private RespuestaGenerica validaCamposObligatorios(PagosLiquidacionColaborador liquidacionColaborador) throws ServiceException {
        try{
            RespuestaGenerica respuesta =  new RespuestaGenerica(null, Constantes.RESULTADO_EXITO, Constantes.EXITO);
            if(liquidacionColaborador.getFechaContrato() == null
                    || liquidacionColaborador.getCentrocClienteId() == null
                    || liquidacionColaborador.getCentrocClienteId().getCentrocClienteId() == null
                    || liquidacionColaborador.getPersonaId() == null
                    || liquidacionColaborador.getPersonaId().getPersonaId() == null
                    || liquidacionColaborador.getPagosLiquidacionId() == null
                    || liquidacionColaborador.getPagosLiquidacionId().getPagosLiquidacionId() == null){
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(Constantes.CAMPOS_REQUERIDOS);
            }
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" validaCamposObligatorios " + Constantes.ERROR_EXCEPCION, e);
        }
    }

}
