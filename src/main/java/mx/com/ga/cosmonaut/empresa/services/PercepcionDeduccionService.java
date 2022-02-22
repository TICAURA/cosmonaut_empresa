package mx.com.ga.cosmonaut.empresa.services;

import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.dto.administracion.DeduccionEmpleadoArchivosDto;
import mx.com.ga.cosmonaut.common.entity.administracion.NmmConceptoDeduccion;
import mx.com.ga.cosmonaut.common.entity.administracion.NmmConceptoPercepcion;
import mx.com.ga.cosmonaut.common.entity.administracion.NmmConfiguraDeduccion;
import mx.com.ga.cosmonaut.common.entity.administracion.NmmConfiguraPercepcion;
import mx.com.ga.cosmonaut.common.exception.ServiceException;

public interface PercepcionDeduccionService {
    
    RespuestaGenerica guardarPercepcion(NmmConceptoPercepcion nmmConceptoPercepcion) throws ServiceException;
    
    RespuestaGenerica guardaPercepcionEmpleado(NmmConfiguraPercepcion nmmConfiguraPercepcion) throws ServiceException;
    
    RespuestaGenerica obtienePercepcionEmpleado(Integer personaId, Integer clienteId) throws ServiceException;

    RespuestaGenerica calculaMonto(Double montoTotal, Integer numeroPeriodos)throws ServiceException;

    RespuestaGenerica obtienePercepcionPolitica(Integer politicaId, Integer clienteId) throws ServiceException;

    RespuestaGenerica obtieneDeduccionPolitica(Integer politicaId, Integer clienteId) throws ServiceException;

    RespuestaGenerica guardaPercepcionesEstandar(Integer clienteId) throws ServiceException;

    RespuestaGenerica modificarPercepcion(NmmConceptoPercepcion nmmConceptoPercepcion) throws ServiceException;
    
    RespuestaGenerica eliminaPercepcion(NmmConceptoPercepcion nmmConceptoPercepcion) throws ServiceException;
    
    RespuestaGenerica eliminaPercepcionEmpleado(NmmConfiguraPercepcion nmmConfiguraPercepcion) throws ServiceException;

    RespuestaGenerica obtienePercepcionesEmpresa(Integer clienteId) throws ServiceException;

    RespuestaGenerica obtieneDeduccionesEmpresa(Integer clienteId) throws ServiceException;

    RespuestaGenerica obtieneDeduccionesEmpresaEstatus(Integer clienteId,boolean estatus) throws ServiceException;

    RespuestaGenerica obtieneDeduccionesPolitica(Integer clienteId) throws ServiceException;

    RespuestaGenerica obtieneDeduccionesPoliticaEstatus(Integer clienteId, boolean estatus) throws ServiceException;
    
    RespuestaGenerica guardaDeduccionEmpleado(NmmConfiguraDeduccion nmmConfiguraDeduccion,
                                              DeduccionEmpleadoArchivosDto archivos) throws ServiceException;

    RespuestaGenerica modificaDeduccionEmpleado(NmmConfiguraDeduccion nmmConfiguraDeduccion,
                                                DeduccionEmpleadoArchivosDto archivos) throws ServiceException;

    RespuestaGenerica obtieneDeduccionEmpleado(Integer personaId, Integer clienteId) throws ServiceException;

    RespuestaGenerica guardarDeduccion(NmmConceptoDeduccion nmmConceptoDeduccion) throws ServiceException;

    RespuestaGenerica guardaDeduccionesEstandar(Integer clienteId) throws ServiceException;
        
    RespuestaGenerica modificarDeduccion(NmmConceptoDeduccion nmmConceptoDeduccion) throws ServiceException;
    
    RespuestaGenerica eliminaDeduccion(NmmConceptoDeduccion nmmConceptoDeduccion) throws ServiceException;
    
    RespuestaGenerica eliminaDeduccionEmpleado(NmmConfiguraDeduccion nmmConfiguraDeduccion) throws ServiceException;

    RespuestaGenerica obtieneConceptoPercepcionEmpresa(Integer clienteId, String tipoPeriodicidad) throws ServiceException;

    RespuestaGenerica obtieneConceptoPercepcionPolitica(Integer clienteId, String tipoPeriodicidad) throws ServiceException;

    RespuestaGenerica modificaPercepcionEmpleado(NmmConfiguraPercepcion nmmConfiguraPercepcion) throws ServiceException;

    RespuestaGenerica modificarPercepcionPolitica(NmmConfiguraPercepcion nmmConfiguraPercepcion) throws ServiceException;

    RespuestaGenerica modificarDeduccionPolitica(NmmConfiguraDeduccion nmmConfiguraDeduccion) throws ServiceException;

    RespuestaGenerica guardaDeduccionPolitica(NmmConfiguraDeduccion nmmConfiguraDeduccion) throws ServiceException;

    RespuestaGenerica guardaPercepcionPolitica(NmmConfiguraPercepcion nmmConfiguraPercepcion) throws ServiceException;

    RespuestaGenerica eliminaPercepcionPolitica(NmmConfiguraPercepcion nmmConfiguraPercepcion) throws ServiceException;

    RespuestaGenerica eliminaDeduccionPolitica(NmmConfiguraDeduccion nmmConfiguraDeduccion) throws ServiceException;

}

