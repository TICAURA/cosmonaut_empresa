package mx.com.ga.cosmonaut.empresa.services;

import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.exception.ServiceException;

public interface NclPuestoService {
    
    RespuestaGenerica findAll() throws ServiceException;
    
    RespuestaGenerica obtenerId(Integer idPuesto) throws ServiceException;
    
    RespuestaGenerica obtenerIdCentroCliente(Integer idCentrocCliente) throws ServiceException;
    
    RespuestaGenerica obtenerIdCentroClienteArea(Integer idCentrocCliente, Integer idArea) throws ServiceException;
    
    RespuestaGenerica obtenerPuestosXArea(Integer idArea) throws ServiceException;

    RespuestaGenerica findByEsActivo(Boolean activo) throws ServiceException;
       
}
