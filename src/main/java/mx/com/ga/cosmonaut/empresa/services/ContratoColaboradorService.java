package mx.com.ga.cosmonaut.empresa.services;

import io.micronaut.http.annotation.PathVariable;
import mx.com.ga.cosmonaut.common.dto.NcoContratoColaboradorDto;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.entity.colaborador.NcoContratoColaborador;
import mx.com.ga.cosmonaut.common.exception.ServiceException;

public interface ContratoColaboradorService {

    RespuestaGenerica guardar(NcoContratoColaborador contratoColaborador) throws ServiceException;

    RespuestaGenerica modificar(NcoContratoColaborador contratoColaborador) throws ServiceException;

    RespuestaGenerica modificarCompensacion(NcoContratoColaborador contratoColaborador) throws ServiceException;

    RespuestaGenerica obtenerIdPersona(Long idPersona) throws ServiceException;

    RespuestaGenerica obtenerIdEmpresa(Long idEmpresa) throws ServiceException;

    RespuestaGenerica obtenerIdArea(Long idArea) throws ServiceException;

    RespuestaGenerica obtenerIdGrupoNomina(Long idGrupoNomina) throws ServiceException;

    RespuestaGenerica obtenerIdPersonaNative(Long idPersona) throws ServiceException;

    RespuestaGenerica obtenerListaDinamica(NcoContratoColaborador contratoColaborador) throws ServiceException;

    RespuestaGenerica listaEmpleadoBaja(Long id, Boolean activo) throws ServiceException;

    RespuestaGenerica guardarBaja(NcoContratoColaboradorDto contratoColaborador) throws ServiceException;

    RespuestaGenerica listaEmpleadoFiniquito(Integer centroClienteId) throws ServiceException;

    RespuestaGenerica listaEmpleadoAguinaldo(Integer centroClienteId) throws ServiceException;

    RespuestaGenerica obtenerListaDinamicaPaginado(NcoContratoColaborador contratoColaborador,Integer numeroRegistros, Integer pagina) throws ServiceException;

    RespuestaGenerica guardarReactivar(NcoContratoColaborador contratoColaborador) throws ServiceException;

    RespuestaGenerica validaReactivar(Integer centroClienteId,Integer personaId) throws ServiceException;
}
