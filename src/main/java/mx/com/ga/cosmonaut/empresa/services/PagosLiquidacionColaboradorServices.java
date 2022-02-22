package mx.com.ga.cosmonaut.empresa.services;

import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.entity.PagosLiquidacionColaborador;
import mx.com.ga.cosmonaut.common.exception.ServiceException;

public interface PagosLiquidacionColaboradorServices {

    RespuestaGenerica guardar(PagosLiquidacionColaborador liquidacionColaborador) throws ServiceException;

}
