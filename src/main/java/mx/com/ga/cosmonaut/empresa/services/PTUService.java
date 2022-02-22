package mx.com.ga.cosmonaut.empresa.services;

import mx.com.ga.cosmonaut.common.dto.CargaMasivaDto;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.exception.ServiceException;

public interface PTUService {

    RespuestaGenerica cargaMasivaPTU(CargaMasivaDto cargaMasivaDto) throws ServiceException;

}
