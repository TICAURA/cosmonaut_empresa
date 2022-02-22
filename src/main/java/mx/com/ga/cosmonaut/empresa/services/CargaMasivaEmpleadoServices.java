package mx.com.ga.cosmonaut.empresa.services;

import mx.com.ga.cosmonaut.common.entity.temporal.CargaMasivaEmpleado;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import org.apache.poi.ss.usermodel.Row;

import java.util.List;

public interface CargaMasivaEmpleadoServices {

    List<CargaMasivaEmpleado> carga(byte[] archivo, Integer centroClienteId, Integer tipoCargaId) throws ServiceException;

    void guardarCargaMasiva(Integer centrocClienteId, Integer tipoCargaId) throws ServiceException;

    CargaMasivaEmpleado obtenEmpleadoCargaMasiva(Row fila, Integer centroClienteId, Integer tipoCargaId);

    CargaMasivaEmpleado validaCargaMasiva(CargaMasivaEmpleado empleado,Integer tipoCargaId);

    void guardarCargaMasivaXEmpleado(CargaMasivaEmpleado empleado, Integer centrocClienteId, Integer tipoCargaId) throws ServiceException;

}
