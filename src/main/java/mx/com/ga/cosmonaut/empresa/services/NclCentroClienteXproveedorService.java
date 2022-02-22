package mx.com.ga.cosmonaut.empresa.services;

import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.dto.cliente.FiltrarRequest;
import mx.com.ga.cosmonaut.common.dto.cliente.GuardarRequest;
import mx.com.ga.cosmonaut.common.dto.cliente.ModificarRequest;
import mx.com.ga.cosmonaut.common.exception.ServiceException;

public interface NclCentroClienteXproveedorService {

    RespuestaGenerica guardar(GuardarRequest request) throws ServiceException;

    RespuestaGenerica modificar(ModificarRequest request) throws ServiceException;

    RespuestaGenerica listar() throws ServiceException;

    RespuestaGenerica obtener(Integer id) throws ServiceException;

    RespuestaGenerica filtrar(FiltrarRequest request) throws ServiceException;

    RespuestaGenerica filtrarPaginado(FiltrarRequest request, Integer numeroRegistros, Integer pagina) throws ServiceException;

}
