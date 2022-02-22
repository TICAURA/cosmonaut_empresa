package mx.com.ga.cosmonaut.empresa.services;

import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.dto.imss.tectel.AfiliaRecepcionRequest;
import mx.com.ga.cosmonaut.common.exception.ServiceException;

public interface TectelService {

    RespuestaGenerica afiliaRecepcion(AfiliaRecepcionRequest request) throws ServiceException;
    RespuestaGenerica getAcuseRecibo(Integer idKardex) throws ServiceException;
    RespuestaGenerica getConstanciaPresentacion(Integer idKardex) throws ServiceException;

}
