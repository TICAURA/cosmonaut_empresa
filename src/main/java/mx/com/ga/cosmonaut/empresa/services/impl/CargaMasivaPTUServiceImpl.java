package mx.com.ga.cosmonaut.empresa.services.impl;

import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.entity.temporal.CargaMasivaPTU;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.common.util.Constantes;
import mx.com.ga.cosmonaut.common.util.ConstantesReportes;
import mx.com.ga.cosmonaut.common.util.ObjetoMapper;
import mx.com.ga.cosmonaut.common.util.UtilidadesReportes;
import mx.com.ga.cosmonaut.empresa.services.CargaMasivaPTUService;
import mx.com.ga.cosmonaut.empresa.services.ValidacionServices;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class CargaMasivaPTUServiceImpl implements CargaMasivaPTUService {

    @Inject
    private ValidacionServices validacionServices;

    @Override
    public List<CargaMasivaPTU> carga(byte[] archivo, Integer centroClienteId) throws ServiceException {
        try (InputStream fichero = new ByteArrayInputStream(archivo)) {

            XSSFWorkbook libro = new XSSFWorkbook(fichero);
            XSSFSheet hoja = libro.getSheetAt(0);
            List<CargaMasivaPTU> listaCargaMasivaPTU = new ArrayList<>();

            hoja.forEach(fila -> {
                if (fila.getRowNum() != 0){
                    CargaMasivaPTU ptu = new CargaMasivaPTU();
                    for (Cell celda : fila) {
                        ptu.setCentroClienteId(centroClienteId);
                        ptu = ObjetoMapper.map(cargaInformacion(ptu,celda),CargaMasivaPTU.class);
                    }
                    listaCargaMasivaPTU.add(validaCargaMasiva(ptu));
                }
            });
            return listaCargaMasivaPTU;

        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" carga " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private CargaMasivaPTU validaCargaMasiva(CargaMasivaPTU ptu){
        if (ptu.getErrores() == null || ptu.getErrores().isEmpty()){
            ptu.setEsCorrecto(Constantes.RESULTADO_EXITO);
        }else {
            ptu.setEsCorrecto(Constantes.RESULTADO_ERROR);
        }
        return ptu;
    }

    private CargaMasivaPTU cargaInformacion(CargaMasivaPTU ptu, Cell celda){
        RespuestaGenerica respuesta;
        switch (celda.getColumnIndex()) {
            case ConstantesReportes.PTU_NUMERO_EMPLEADO_ID:
                respuesta = validacionServices.validaTextoObligatorio(UtilidadesReportes.tipoCelda(celda));
                ptu.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.PTU_NUMERO_EMPLEADO,respuesta,ptu.getErrores()));
                ptu.setNumeroEmleado((String) respuesta.getDatos());
                break;
            case ConstantesReportes.PTU_NOMBRE_ID:
                respuesta = validacionServices.validaTextoObligatorio(UtilidadesReportes.tipoCelda(celda));
                ptu.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.PTU_NOMBRE,respuesta,ptu.getErrores()));
                ptu.setNombre((String) respuesta.getDatos());
                break;
            case ConstantesReportes.PTU_PRIMER_APELLIDO_ID:
                respuesta = validacionServices.validaTextoObligatorio(UtilidadesReportes.tipoCelda(celda));
                ptu.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.PTU_PRIMER_APELLIDO,respuesta,ptu.getErrores()));
                ptu.setPrimerApellido((String) respuesta.getDatos());
                break;
            case ConstantesReportes.PTU_SEGUNDO_APELLIDO_ID:
                respuesta = validacionServices.validaTextoNoRequerido(UtilidadesReportes.tipoCelda(celda));
                ptu.setSegundoApellido((String) respuesta.getDatos());
                break;
            case ConstantesReportes.PTU_DIAS_TRABAJADOS_ID:
                respuesta = validacionServices.validaNumeroObligatorio(UtilidadesReportes.tipoCelda(celda));
                ptu.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.PTU_DIAS_TRABAJADOS,respuesta,ptu.getErrores()));
                ptu.setDiasTrabajados((Long) respuesta.getDatos());
                break;
            case ConstantesReportes.PTU_TOTAL_BRUTO_ID:
                respuesta = validacionServices.validaMontoObligatorio(UtilidadesReportes.tipoCelda(celda));
                ptu.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.PTU_TOTAL_BRUTO,respuesta,ptu.getErrores()));
                ptu.setTotalBrutoPtu((Double) respuesta.getDatos());
                break;
            default:
                break;
        }

        return ptu;
    }

}
