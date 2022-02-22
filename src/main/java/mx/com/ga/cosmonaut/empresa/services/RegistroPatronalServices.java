package mx.com.ga.cosmonaut.empresa.services;

import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.dto.cliente.CredencialesImssDto;
import mx.com.ga.cosmonaut.common.entity.cliente.NclRegistroPatronal;
import mx.com.ga.cosmonaut.common.exception.ServiceException;

public interface RegistroPatronalServices {

    // TECTEL
    RespuestaGenerica guardar(NclRegistroPatronal registroPatronal,
                              CredencialesImssDto credencialesImss) throws ServiceException;

    RespuestaGenerica modificar(NclRegistroPatronal registroPatronal,
                                CredencialesImssDto credencialesImss) throws ServiceException;

    RespuestaGenerica obtenerId(Long registroPatronalId) throws ServiceException;

    RespuestaGenerica obtenerEmpresaId(Long registroPatronalId) throws ServiceException;
}
