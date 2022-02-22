package mx.com.ga.cosmonaut.empresa.services;

import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.entity.administracion.NmaDomicilio;
import mx.com.ga.cosmonaut.common.exception.ServiceException;

public interface DomicilioService {

    RespuestaGenerica guardar(NmaDomicilio domicilioDto) throws ServiceException;

    RespuestaGenerica guardarSedeDomicilio(NmaDomicilio domicilioDto) throws ServiceException;

    RespuestaGenerica modificar(NmaDomicilio domicilioDto) throws ServiceException;

    RespuestaGenerica obtenerId(Long idDomicilio) throws ServiceException;

    RespuestaGenerica obtenerIdPersona(Long idPersona) throws ServiceException;

    RespuestaGenerica obtenerIdPersonaNativo(Long idPersona) throws ServiceException;

    RespuestaGenerica obtenerIdEmpresa(Long idEmpresa) throws ServiceException;

    RespuestaGenerica obtenerIdEmpresaDomicilio(Long idEmpresa) throws ServiceException;

}
