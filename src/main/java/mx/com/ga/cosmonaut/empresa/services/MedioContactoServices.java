package mx.com.ga.cosmonaut.empresa.services;

import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.entity.administracion.NmaMedioContacto;
import mx.com.ga.cosmonaut.common.exception.ServiceException;

public interface MedioContactoServices {

    RespuestaGenerica guardar(NmaMedioContacto medioContacto) throws ServiceException;

    RespuestaGenerica obtenerId(String numeroCuenta) throws ServiceException;

    RespuestaGenerica modificar(NmaMedioContacto medioContacto) throws ServiceException;

}
