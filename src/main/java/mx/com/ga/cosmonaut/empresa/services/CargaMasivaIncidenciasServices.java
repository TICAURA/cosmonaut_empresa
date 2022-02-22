package mx.com.ga.cosmonaut.empresa.services;

import mx.com.ga.cosmonaut.common.entity.temporal.CargaMasivaIncidencias;
import mx.com.ga.cosmonaut.common.exception.ServiceException;

import java.util.List;

public interface CargaMasivaIncidenciasServices {

    List<CargaMasivaIncidencias> carga(byte[] archivo, Integer centroClienteId) throws ServiceException;

}
