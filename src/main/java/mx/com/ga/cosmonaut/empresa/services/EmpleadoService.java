package mx.com.ga.cosmonaut.empresa.services;

import mx.com.ga.cosmonaut.common.dto.CargaMasivaDto;
import mx.com.ga.cosmonaut.common.dto.NcoPersonaDto;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.exception.ServiceException;

public interface EmpleadoService {

    RespuestaGenerica guardar(NcoPersonaDto personaDto) throws ServiceException;

    RespuestaGenerica modificar(NcoPersonaDto personaDto) throws ServiceException;

    RespuestaGenerica listaCompaniaPersonaEstatus(NcoPersonaDto ncoPersonaDto) throws ServiceException;

    RespuestaGenerica cargaMasivaEmpleados(CargaMasivaDto cargaMasivaDto) throws ServiceException;

    RespuestaGenerica listaCargaMasivaEmpleados(Integer centroClienteId) throws ServiceException;

    RespuestaGenerica listaCargaMasivaEmpleadosEsCorrecto(Integer centroClienteId, Boolean isEsCorrecto) throws ServiceException;

    RespuestaGenerica cargaMasivaXEmpleado(CargaMasivaDto cargaMasivaDto) throws ServiceException;
}
