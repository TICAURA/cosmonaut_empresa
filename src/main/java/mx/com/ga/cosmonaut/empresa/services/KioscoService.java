package mx.com.ga.cosmonaut.empresa.services;

import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.entity.calculo.NcrEmpleadoXnominaPK;
import mx.com.ga.cosmonaut.common.exception.ServiceException;

public interface KioscoService {
    RespuestaGenerica listaEmpleado(NcrEmpleadoXnominaPK ncrEmpleadoXnomina) throws ServiceException;
}
