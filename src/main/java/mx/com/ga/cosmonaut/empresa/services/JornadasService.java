package mx.com.ga.cosmonaut.empresa.services;

import mx.com.ga.cosmonaut.common.dto.JornadasDto;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.entity.cliente.NclJornada;
import mx.com.ga.cosmonaut.common.exception.ServiceException;

public interface JornadasService {
       
    RespuestaGenerica consultaJornadasEmpresa(Integer idCliente,Integer idJornada )throws ServiceException;
    
    RespuestaGenerica consultaEmpleadosJornadaEmpresa (Integer idCliente, Integer idJornada ) throws ServiceException;
    
    RespuestaGenerica guardar (JornadasDto jornadasDto) throws ServiceException;
    
    RespuestaGenerica duplicarJornada (Integer idJornada) throws ServiceException;
    
    RespuestaGenerica obtieneJornadasXEmpresa (Integer idJornada) throws ServiceException;
    
    RespuestaGenerica modificar(JornadasDto jornadasDto) throws ServiceException;
    
    RespuestaGenerica eliminar(NclJornada nclJornada) throws ServiceException;

    RespuestaGenerica findByActivo(Boolean activo) throws ServiceException;

}
