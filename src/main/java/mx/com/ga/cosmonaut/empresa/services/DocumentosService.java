package mx.com.ga.cosmonaut.empresa.services;

import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;

public interface DocumentosService {

    RespuestaGenerica eliminar(Integer documentoId) throws Exception;

}
