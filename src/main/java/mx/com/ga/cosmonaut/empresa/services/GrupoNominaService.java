package mx.com.ga.cosmonaut.empresa.services;

import mx.com.ga.cosmonaut.common.dto.NclGrupoNominaDto;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.exception.ServiceException;

public interface GrupoNominaService {

    RespuestaGenerica guardar(NclGrupoNominaDto grupoNominaDto) throws ServiceException;

    RespuestaGenerica modificar(NclGrupoNominaDto grupoNominaDto) throws ServiceException;
    
    RespuestaGenerica eliminar(Long idGrupoNomina) throws ServiceException;

    RespuestaGenerica listarTodos(Long idCompania) throws ServiceException;

    RespuestaGenerica obtenerId(Long idGrupoNomina) throws ServiceException;

    RespuestaGenerica listaDinamica(NclGrupoNominaDto grupoNominaDto) throws ServiceException;

    RespuestaGenerica findByEsActivo(Boolean activo) throws  ServiceException;
}
