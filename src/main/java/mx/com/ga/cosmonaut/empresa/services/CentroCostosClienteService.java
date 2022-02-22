package mx.com.ga.cosmonaut.empresa.services;

import mx.com.ga.cosmonaut.common.dto.NclCentrocClienteDto;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.exception.ServiceException;

import java.util.List;

public interface CentroCostosClienteService {

    RespuestaGenerica guardarCompania(NclCentrocClienteDto centroCostosClienteDto) throws ServiceException;

    RespuestaGenerica guardarEmpresa(NclCentrocClienteDto centroCostosClienteDto) throws ServiceException;

    RespuestaGenerica modificarCompania(NclCentrocClienteDto centroCostosClienteDto) throws ServiceException;

    RespuestaGenerica modificarEmpresa(NclCentrocClienteDto centroCostosClienteDto) throws ServiceException;

    RespuestaGenerica modificarLista(List<NclCentrocClienteDto> listCentroCostosClienteDto) throws ServiceException;

    RespuestaGenerica listarCompania() throws ServiceException;

    RespuestaGenerica listarCompaniaSimple() throws ServiceException;

    RespuestaGenerica listaCompaniaEmpresa(Long idCentroCostosCliente) throws ServiceException;

    RespuestaGenerica listaEmpresaSimple() throws ServiceException;

    RespuestaGenerica obtenerId(Long idCentroCostosCliente) throws ServiceException;

    RespuestaGenerica eliminarId(Long idCentroCostosCliente) throws ServiceException;

    RespuestaGenerica eliminarEmpresa(Long idCentroCostosCliente) throws ServiceException;

    RespuestaGenerica listaDinamica(NclCentrocClienteDto centroCostosClienteDto) throws ServiceException;

    RespuestaGenerica login(String correo) throws ServiceException;

    RespuestaGenerica listarCompaniaPaginado(Integer numeroRegistros, Integer pagina) throws ServiceException;

    RespuestaGenerica listaDinamicaPaginado(NclCentrocClienteDto centroCostosClienteDto,Integer numeroRegistros, Integer pagina) throws ServiceException;

    RespuestaGenerica listarCompaniaCombo() throws ServiceException;
}
