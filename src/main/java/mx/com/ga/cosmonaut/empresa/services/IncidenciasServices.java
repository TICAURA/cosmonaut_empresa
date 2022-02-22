package mx.com.ga.cosmonaut.empresa.services;

import mx.com.ga.cosmonaut.common.dto.CargaMasivaDto;
import mx.com.ga.cosmonaut.common.dto.NscIncidenciaDto;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.dto.consultas.IncidenciasConsulta;
import mx.com.ga.cosmonaut.common.exception.ServiceException;

import java.util.List;

public interface IncidenciasServices {

    RespuestaGenerica guardarLista(List<NscIncidenciaDto> listIncidencia) throws ServiceException;

    RespuestaGenerica modificar(NscIncidenciaDto incidencia) throws ServiceException;

    RespuestaGenerica modificarEstatus(NscIncidenciaDto incidencia) throws ServiceException;

    RespuestaGenerica eliminar(Long incidenciaId) throws ServiceException;

    RespuestaGenerica obtenerId(Long incidenciaId) throws ServiceException;

    RespuestaGenerica listaClienteId(Long clienteId) throws ServiceException;

    RespuestaGenerica listaDinamica(IncidenciasConsulta incidencias) throws ServiceException;

    RespuestaGenerica obtenerIncidenciaId(Long clienteId) throws ServiceException;

    RespuestaGenerica listaClienteIdFechaInicioFechaFin(NscIncidenciaDto incidencia) throws ServiceException;

    RespuestaGenerica cargaMasivaIncidencias(CargaMasivaDto cargaMasivaDto) throws ServiceException;

    RespuestaGenerica listaCargaMasiva(Integer centroClienteId) throws ServiceException;

    RespuestaGenerica listaCargaMasivaEsCorrecto(Integer centroClienteId,Boolean isEsCorrecto) throws ServiceException;

    RespuestaGenerica listaDinamicaPaginado(IncidenciasConsulta incidencias,Integer numeroRegistros, Integer pagina) throws ServiceException;

}
