package mx.com.ga.cosmonaut.empresa.services.impl;

import mx.com.ga.cosmonaut.common.dto.CargaMasivaDto;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.entity.CargaPTU;
import mx.com.ga.cosmonaut.common.entity.colaborador.NcoContratoColaborador;
import mx.com.ga.cosmonaut.common.entity.temporal.CargaMasivaPTU;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.common.repository.CargaPTURepository;
import mx.com.ga.cosmonaut.common.repository.colaborador.NcoContratoColaboradorRepository;
import mx.com.ga.cosmonaut.common.util.Constantes;
import mx.com.ga.cosmonaut.common.util.Utilidades;
import mx.com.ga.cosmonaut.empresa.services.CargaMasivaPTUService;
import mx.com.ga.cosmonaut.empresa.services.PTUService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Singleton
public class PTUServiceImpl implements PTUService {

    private static final Logger LOG = LoggerFactory.getLogger(PTUServiceImpl.class);

    @Inject
    private CargaMasivaPTUService cargaMasivaPTUService;

    @Inject
    private CargaPTURepository cargaPTURepository;

    @Inject
    private NcoContratoColaboradorRepository ncoContratoColaboradorRepository;

    @Override
    public RespuestaGenerica cargaMasivaPTU(CargaMasivaDto cargaMasivaDto) throws ServiceException {
        try{
            cargaPTURepository.deleteByCentrocClienteId(cargaMasivaDto.getCentrocClienteId().longValue());
            List<CargaMasivaPTU> lista = cargaMasivaPTUService.carga(cargaMasivaDto.getArchivo(),
                    cargaMasivaDto.getCentrocClienteId());
            RespuestaGenerica respuesta = validaErrores(lista);

            List<CargaPTU> listaCargaPtu = new ArrayList<>();
            if (respuesta.isResultado()){
                lista.forEach(ptu -> {
                    try {
                        NcoContratoColaborador colaborador = ncoContratoColaboradorRepository.
                                findByCentrocClienteIdCentrocClienteIdAndNumEmpleadoAndNombreAndApellido(
                                        ptu.getCentroClienteId(),
                                        ptu.getNumeroEmleado(), ptu.getNombre(),ptu.getPrimerApellido())
                                .orElseThrow(() -> new ServiceException(Constantes.ERROR_OBTENER_EMPLEADO));
                        CargaPTU cargaPTU = new CargaPTU();
                        cargaPTU.setCentrocClienteId(ptu.getCentroClienteId().longValue());
                        cargaPTU.setDiasTrabajados(ptu.getDiasTrabajados());
                        cargaPTU.setPtu(ptu.getTotalBrutoPtu());
                        cargaPTU.setNumeroEmpleado(ptu.getNumeroEmleado());
                        cargaPTU.setNombre(ptu.getNombre());
                        cargaPTU.setApellidoPaterno(ptu.getPrimerApellido());
                        cargaPTU.setApellidoMaterno(ptu.getSegundoApellido());
                        cargaPTU.setFechaContrato(colaborador.getFechaContrato());
                        cargaPTU.setPersonaId(colaborador.getPersonaId().getPersonaId().longValue());
                        cargaPTU.setNominaPeriodoId(cargaMasivaDto.getNominaPeriodoId().longValue());
                        cargaPTU.setFechaInsercion(Utilidades.obtenerFechaSystema());
                        cargaPTURepository.save(cargaPTU);
                    } catch (ServiceException e) {
                        LOG.error(Constantes.ERROR, e);
                    }
                });

            }

            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" cargaMasivaPTU " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    private RespuestaGenerica validaErrores(List<CargaMasivaPTU> lista){
        RespuestaGenerica respuesta = new RespuestaGenerica(null, Constantes.RESULTADO_EXITO, Constantes.EXITO_CARGA_MASIVA_PTU);
        List<CargaMasivaPTU> listaErrores = lista.stream()
                .filter(ptu -> !ptu.isEsCorrecto())
                .collect(Collectors.toList());

        if (!listaErrores.isEmpty()){
            respuesta.setResultado(Constantes.RESULTADO_ERROR);
            respuesta.setMensaje(Constantes.ERROR_CARGA_MASIVA_PTU);
        }

        long empleadosDiasTrabajados = lista.stream()
                .filter(carga -> carga.getDiasTrabajados() != null && carga.getDiasTrabajados() > 366).count();

        if (empleadosDiasTrabajados > 0){
            respuesta.setResultado(Constantes.RESULTADO_ERROR);
            respuesta.setMensaje(Constantes.ERROR_CALCULA_PTU_DIAS_LABORADOS);
        }

        return respuesta;
    }


}
