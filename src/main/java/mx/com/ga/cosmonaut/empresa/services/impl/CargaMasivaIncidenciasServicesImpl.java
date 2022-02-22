package mx.com.ga.cosmonaut.empresa.services.impl;

import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.entity.temporal.CargaMasivaIncidencias;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.common.util.*;
import mx.com.ga.cosmonaut.empresa.services.ValidacionServices;
import mx.com.ga.cosmonaut.empresa.services.CargaMasivaIncidenciasServices;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Singleton
public class CargaMasivaIncidenciasServicesImpl implements CargaMasivaIncidenciasServices {

    @Inject
    private ValidacionServices validacionServices;

    @Override
    public List<CargaMasivaIncidencias> carga(byte[] archivo, Integer centroClienteId) throws ServiceException {
        try (InputStream fichero = new ByteArrayInputStream(archivo)) {

            XSSFWorkbook libro = new XSSFWorkbook(fichero);
            XSSFSheet hoja = libro.getSheetAt(0);
            List<CargaMasivaIncidencias> listaCargaMasivaIncidencias = new ArrayList<>();

            hoja.forEach(fila -> {
                if (fila.getRowNum() != 0){
                    CargaMasivaIncidencias incidencias = new CargaMasivaIncidencias();
                    for (Cell celda : fila) {
                        incidencias.setCentrocClienteId(centroClienteId);
                        incidencias = ObjetoMapper.map(cargaInformaciongeneral(incidencias,celda,centroClienteId),CargaMasivaIncidencias.class);
                        if (incidencias.getErrores() == null || incidencias.getErrores().isEmpty()){
                            incidencias.setEsCorrecto(Constantes.RESULTADO_EXITO);
                        }
                    }
                    if (incidencias.getNumeroEmpleado() != null && !incidencias.getNumeroEmpleado().isEmpty()){
                        listaCargaMasivaIncidencias.add(validaCargaMasiva(incidencias));
                    }
                }
            });
            return listaCargaMasivaIncidencias;

        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" carga " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private CargaMasivaIncidencias cargaInformaciongeneral(CargaMasivaIncidencias incidencias, Cell celda, Integer centroClienteId){
        RespuestaGenerica respuesta;
        switch (celda.getColumnIndex()) {
            case ConstantesReportes.INCIDENCIAS_NUMERO_EMPLEADO_ID:
                respuesta = validacionServices.validaNumeroEmpleadoDadoAlta(UtilidadesReportes.tipoCelda(celda),centroClienteId);
                incidencias.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.INCIDENCIAS_NUMERO_EMPLEADO,respuesta,incidencias.getErrores()));
                incidencias.setNumeroEmpleado((String) respuesta.getDatos());
                break;
            case ConstantesReportes.INCIDENCIAS_NOMBRE_EMPLEADO_ID:
                respuesta = validacionServices.validaTextoObligatorio(UtilidadesReportes.tipoCelda(celda));
                incidencias.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.INCIDENCIAS_NOMBRE_EMPLEADO,respuesta,incidencias.getErrores()));
                break;
            case ConstantesReportes.INCIDENCIAS_TIPO_EVENTO_ID:
                respuesta = validacionServices.validaComboObligatorio(UtilidadesReportes.tipoCelda(celda));
                incidencias.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.INCIDENCIAS_TIPO_EVENTO,respuesta,incidencias.getErrores()));
                incidencias.setTipoEventoId((Integer) respuesta.getDatos());
                break;
            case ConstantesReportes.INCIDENCIAS_UNIDAD_MEDIDA_ID:
                respuesta = validacionServices.validaComboObligatorio(UtilidadesReportes.tipoCelda(celda));
                incidencias.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.INCIDENCIAS_UNIDAD_MEDIDA,respuesta,incidencias.getErrores()));
                incidencias.setUnidadMedidaId((Integer) respuesta.getDatos());
                break;
            case ConstantesReportes.INCIDENCIAS_FECHA_APLICACION_ID:
                respuesta = validacionServices.validaFechaFinObligatorio(UtilidadesReportes.tipoCelda(celda));
                incidencias.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.INCIDENCIAS_FECHA_APLICACION,respuesta,incidencias.getErrores()));
                incidencias.setFechaAplicacion((Date) respuesta.getDatos());
                break;
            case ConstantesReportes.INCIDENCIAS_FECHA_INICIO_ID:
                respuesta = validacionServices.validaFechaFinObligatorio(UtilidadesReportes.tipoCelda(celda));
                incidencias.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.INCIDENCIAS_FECHA_INICIO,respuesta,incidencias.getErrores()));
                incidencias.setFechaInicio((Date) respuesta.getDatos());
                break;
            case ConstantesReportes.INCIDENCIAS_TIPO_INCAPACIDAD_ID:
                respuesta = validacionServices.validaComboNoRequerido(UtilidadesReportes.tipoCelda(celda));
                incidencias.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.INCIDENCIAS_TIPO_INCAPACIDAD,respuesta,incidencias.getErrores()));
                incidencias.setTipoIncapacidadId((Integer) respuesta.getDatos());
                break;
            case ConstantesReportes.INCIDENCIAS_NUMERO_DIAS_ID:
                if(incidencias.getTipoEventoId() != null){
                    switch (incidencias.getTipoEventoId()){
                        case 5:
                            if(incidencias.getUnidadMedidaId() != null){
                                switch (incidencias.getUnidadMedidaId()){
                                    case 1:
                                        incidencias.setNumeroDias(0L);
                                        break;
                                    case 2:
                                        respuesta = validacionServices.validaNumeroObligatorio(UtilidadesReportes.tipoCelda(celda));
                                        incidencias.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.INCIDENCIAS_NUMERO_DIAS,respuesta,incidencias.getErrores()));
                                        incidencias.setNumeroDias((Long) respuesta.getDatos());
                                        break;
                                    default:
                                        incidencias.setNumeroDias(0L);
                                        break;
                                }
                            }
                            break;
                        case 8:
                        case 13:
                        case 14:
                            incidencias.setNumeroDias(0L);
                            break;
                        default:
                            respuesta = validacionServices.validaNumeroObligatorio(UtilidadesReportes.tipoCelda(celda));
                            incidencias.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.INCIDENCIAS_NUMERO_DIAS,respuesta,incidencias.getErrores()));
                            incidencias.setNumeroDias((Long) respuesta.getDatos());
                            break;
                    }
                }
                break;
            case ConstantesReportes.INCIDENCIAS_NUMERO_HORAS_EXTRAS_ID:
                if(incidencias.getTipoEventoId() != null){
                    switch (incidencias.getTipoEventoId()){
                        case 13:
                        case 14:
                            respuesta = validacionServices.validaNumeroNoRequerido(UtilidadesReportes.tipoCelda(celda));
                            incidencias.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.INCIDENCIAS_NUMERO_HORAS_EXTRAS,respuesta,incidencias.getErrores()));
                            int numero = 0;
                            if (respuesta.getDatos() != null && respuesta.getDatos() instanceof Long){
                                numero = ((Long) respuesta.getDatos()).intValue();
                            }
                            incidencias.setNumeroHorasExtras(numero);
                            break;
                        default:
                            incidencias.setNumeroHorasExtras(0);
                            break;
                    }
                }
                break;
            case ConstantesReportes.INCIDENCIAS_MONTO_ID:
                if(incidencias.getTipoEventoId() != null){
                    switch (incidencias.getTipoEventoId()){
                        case 5:
                        case 8:
                        case 13:
                        case 14:
                            if(incidencias.getUnidadMedidaId() != null){
                                switch (incidencias.getUnidadMedidaId()){
                                    case 1:
                                        incidencias.setMonto(0.0);
                                        break;
                                    case 2:
                                        incidencias.setMonto(0.0);
                                        break;
                                    default:
                                        respuesta = validacionServices.validaMontoObligatorio(UtilidadesReportes.tipoCelda(celda));
                                        incidencias.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.INCIDENCIAS_MONTO,respuesta,incidencias.getErrores()));
                                        incidencias.setMonto((Double) respuesta.getDatos());
                                        break;
                                }
                            }
                            break;
                        default:
                            incidencias.setMonto(0.0);
                            break;
                    }
                }
                break;
            default:
                break;
        }

        return incidencias;
    }

    private CargaMasivaIncidencias validaCargaMasiva(CargaMasivaIncidencias incidencias){
        incidencias.setErrores(validaInformacion(incidencias));
        if (incidencias.getErrores() == null || incidencias.getErrores().isEmpty()){
            incidencias.setEsCorrecto(Constantes.RESULTADO_EXITO);
        }else {
            incidencias.setEsCorrecto(Constantes.RESULTADO_ERROR);
        }
        return incidencias;
    }

    private String validaInformacion(CargaMasivaIncidencias incidencias){
        RespuestaGenerica respuesta;
        respuesta = validacionServices.validaTextoObligatorio(incidencias.getNumeroEmpleado());
        incidencias.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.INCIDENCIAS_NUMERO_EMPLEADO,respuesta,incidencias.getErrores()));
        respuesta = validacionServices.validaNumeroObligatorio(incidencias.getTipoEventoId());
        incidencias.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.INCIDENCIAS_TIPO_EVENTO,respuesta,incidencias.getErrores()));
        respuesta = validacionServices.validaFechaFinObligatorio(incidencias.getFechaAplicacion());
        incidencias.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.INCIDENCIAS_FECHA_APLICACION,respuesta,incidencias.getErrores()));
        respuesta = validacionServices.validaFechaFinObligatorio(incidencias.getFechaInicio());
        incidencias.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.INCIDENCIAS_FECHA_INICIO,respuesta,incidencias.getErrores()));
        if (incidencias.getTipoEventoId() != null){
            if (incidencias.getTipoEventoId() == 13 || incidencias.getTipoEventoId() == 14){
                respuesta = validacionServices.validaNumeroObligatorio(incidencias.getUnidadMedidaId());
                incidencias.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.INCIDENCIAS_UNIDAD_MEDIDA,respuesta,incidencias.getErrores()));

            }
        }

        if (incidencias.getTipoEventoId() != null  && incidencias.getUnidadMedidaId() != null){
            if (incidencias.getTipoEventoId() == 5 && incidencias.getUnidadMedidaId() == 1){
                respuesta = new RespuestaGenerica(null,Constantes.RESULTADO_ERROR,Constantes.ERROR_UNIDAD_MEDIDA);
                incidencias.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.INCIDENCIAS_UNIDAD_MEDIDA,respuesta,incidencias.getErrores()));
            }

            if (incidencias.getTipoEventoId() == 13 || incidencias.getTipoEventoId() == 14 ){
                if(incidencias.getUnidadMedidaId() == 2){
                    respuesta = new RespuestaGenerica(null,Constantes.RESULTADO_ERROR,Constantes.ERROR_UNIDAD_MEDIDA);
                    incidencias.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.INCIDENCIAS_UNIDAD_MEDIDA,respuesta,incidencias.getErrores()));
                }
            }

            if (incidencias.getTipoEventoId() == 8  && incidencias.getUnidadMedidaId() != 3){
                respuesta = new RespuestaGenerica(null,Constantes.RESULTADO_ERROR,Constantes.ERROR_UNIDAD_MEDIDA);
                incidencias.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.INCIDENCIAS_UNIDAD_MEDIDA,respuesta,incidencias.getErrores()));
            }
        }

        return incidencias.getErrores();
    }

    private void a(){


    }


}
