package mx.com.ga.cosmonaut.empresa.services;

import mx.com.ga.cosmonaut.common.entity.temporal.CargaMasivaPTU;
import mx.com.ga.cosmonaut.common.exception.ServiceException;

import java.util.List;

public interface CargaMasivaPTUService {

    List<CargaMasivaPTU> carga(byte[] archivo, Integer centroClienteId) throws ServiceException;

}
