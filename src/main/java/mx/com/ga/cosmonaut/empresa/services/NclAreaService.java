package mx.com.ga.cosmonaut.empresa.services;

import mx.com.ga.cosmonaut.common.dto.NclAreaDto;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.entity.cliente.NclEmpleadoXArea;
import mx.com.ga.cosmonaut.common.exception.ServiceException;


public interface NclAreaService {

    RespuestaGenerica findAll() throws ServiceException;

    RespuestaGenerica obtenerAreaXEmpresa(Integer idCentrocCliente) throws ServiceException;

    RespuestaGenerica obtenerIdArea(Integer idArea) throws ServiceException;

    RespuestaGenerica obtenerPuestoXEmpleado(Integer idCentrocCliente, Integer idArea) throws ServiceException;

    RespuestaGenerica guardar(NclAreaDto nclAreaDto) throws ServiceException;

    RespuestaGenerica modificar(NclAreaDto nclAreaDto) throws ServiceException;

    RespuestaGenerica obtenerAreas(Integer centrocClienteId) throws ServiceException;

    RespuestaGenerica eliminarArea(NclAreaDto nclAreaDto) throws ServiceException;

    RespuestaGenerica listarDinamica(NclEmpleadoXArea nclEmpleadoXArea) throws ServiceException;
    
    RespuestaGenerica agregarPuesto (NclAreaDto nclAreaDto) throws ServiceException;

    RespuestaGenerica findByEsActivo(Boolean activo) throws ServiceException;

}
