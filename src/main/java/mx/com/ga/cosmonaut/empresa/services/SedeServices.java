package mx.com.ga.cosmonaut.empresa.services;

import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.entity.cliente.NclSede;
import mx.com.ga.cosmonaut.common.exception.ServiceException;

public interface SedeServices {

    RespuestaGenerica guardar(NclSede sede) throws ServiceException;

    RespuestaGenerica modificar(NclSede sede) throws ServiceException;

    RespuestaGenerica eliminar(Integer sedeId) throws ServiceException;

    RespuestaGenerica obtenerId(Long idSede) throws ServiceException;

    RespuestaGenerica obtenerIdCompania(Long idSede) throws ServiceException;

    RespuestaGenerica validaCamposObligatorios(NclSede sede) throws ServiceException;

    RespuestaGenerica findByEsActivo(Boolean activo) throws ServiceException;

}
