package mx.com.ga.cosmonaut.empresa.services;

import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenericaGruop;
import mx.com.ga.cosmonaut.common.dto.imss.IMSSFiltradoRequest;
import mx.com.ga.cosmonaut.common.dto.imss.VariabilidadFiltradoRequest;
import mx.com.ga.cosmonaut.common.exception.ServiceException;

public interface IMSSService {

    RespuestaGenerica listarRegistrosPatronales(Integer id) throws ServiceException;

    RespuestaGenerica listarMovimientos() throws ServiceException;

    RespuestaGenericaGruop filtrar(IMSSFiltradoRequest request) throws ServiceException;

    RespuestaGenerica eliminar(Long id) throws ServiceException;

    RespuestaGenerica filtrarVariabilidad(VariabilidadFiltradoRequest request) throws ServiceException;

    RespuestaGenerica calcularDias(Integer bimestre) throws ServiceException;

}
