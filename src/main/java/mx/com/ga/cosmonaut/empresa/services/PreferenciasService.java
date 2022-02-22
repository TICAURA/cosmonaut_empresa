package mx.com.ga.cosmonaut.empresa.services;

import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.entity.colaborador.NcoPreferencia;
import mx.com.ga.cosmonaut.common.exception.ServiceException;

public interface PreferenciasService {

    RespuestaGenerica guardar(NcoPreferencia preferencia) throws ServiceException;

    RespuestaGenerica obtenerId(Long idPreferencia) throws ServiceException;

    RespuestaGenerica obtenerIdPersona(Long idPreferencia) throws ServiceException;

    RespuestaGenerica modificar(NcoPreferencia preferencia) throws ServiceException;

    RespuestaGenerica eliminar(Long idPreferencia) throws ServiceException;
}
