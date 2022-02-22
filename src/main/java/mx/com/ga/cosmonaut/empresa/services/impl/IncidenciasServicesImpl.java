package mx.com.ga.cosmonaut.empresa.services.impl;

import mx.com.ga.cosmonaut.common.dto.*;
import mx.com.ga.cosmonaut.common.dto.consultas.IncidenciasConsulta;
import mx.com.ga.cosmonaut.common.entity.calculo.NcrNominaXperiodo;
import mx.com.ga.cosmonaut.common.entity.catalogo.negocio.CatTipoIncapacidad;
import mx.com.ga.cosmonaut.common.entity.catalogo.negocio.CatTipoIncidencia;
import mx.com.ga.cosmonaut.common.entity.catalogo.negocio.CatUnidad;
import mx.com.ga.cosmonaut.common.entity.catalogo.ubicacion.CatEstadoIncidencia;
import mx.com.ga.cosmonaut.common.entity.cliente.NclBeneficioXpolitica;
import mx.com.ga.cosmonaut.common.entity.colaborador.NcoContratoColaborador;
import mx.com.ga.cosmonaut.common.entity.colaborador.NcoPersona;
import mx.com.ga.cosmonaut.common.entity.colaborador.servicios.NscIncidencia;
import mx.com.ga.cosmonaut.common.entity.temporal.CargaMasivaIncidencias;
import mx.com.ga.cosmonaut.common.enums.CatTipoIncidenciaEnum;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.common.repository.calculo.NcrNominaXperiodoRepository;
import mx.com.ga.cosmonaut.common.repository.cliente.NclBeneficioXpoliticaRepository;
import mx.com.ga.cosmonaut.common.repository.cliente.NclHorarioJornadaRepository;
import mx.com.ga.cosmonaut.common.repository.colaborador.NcoContratoColaboradorRepository;
import mx.com.ga.cosmonaut.common.repository.colaborador.NcoPersonaRepository;
import mx.com.ga.cosmonaut.common.repository.colaborador.servicios.NscIncidenciaRepository;
import mx.com.ga.cosmonaut.common.repository.nativo.IncidenciasRepository;
import mx.com.ga.cosmonaut.common.repository.temporal.CargaMasivaIncidenciasRepository;
import mx.com.ga.cosmonaut.common.service.DocumentosEmpleadoService;
import mx.com.ga.cosmonaut.common.util.*;
import mx.com.ga.cosmonaut.empresa.services.CargaMasivaIncidenciasServices;
import mx.com.ga.cosmonaut.empresa.services.IncidenciasServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Singleton
public class IncidenciasServicesImpl implements IncidenciasServices {

    private static final Logger LOG = LoggerFactory.getLogger(IncidenciasServicesImpl.class);

    @Inject
    private NscIncidenciaRepository nscIncidenciaRepository;

    @Inject
    private NcoContratoColaboradorRepository ncoContratoColaboradorRepository;

    @Inject
    private NcrNominaXperiodoRepository ncrNominaXperiodoRepository;

    @Inject
    private DocumentosEmpleadoService documentosEmpleadoService;

    @Inject
    private IncidenciasRepository incidenciasRepository;

    @Inject
    private CargaMasivaIncidenciasServices cargaMasivaIncidenciasServices;

    @Inject
    private CargaMasivaIncidenciasRepository cargaMasivaIncidenciasRepository;

    @Inject
    private NclHorarioJornadaRepository nclHorarioJornadaRepository;

    @Inject
    private NclBeneficioXpoliticaRepository nclBeneficioXpoliticaRepository;

    @Inject
    private NcoPersonaRepository ncoPersonaRepository;

    @Override
    public RespuestaGenerica guardarLista(List<NscIncidenciaDto> listIncidencia) throws ServiceException {



        try{
            RespuestaGenerica respuesta;
            List<NscIncidenciaDto> lista = new ArrayList<>();
            for (NscIncidenciaDto incidencia : listIncidencia) {
                boolean nsc;
                if (incidencia.getTipoIncidenciaId().getTipoIncidenciaId() == 3)
                    nsc= nscIncidenciaRepository.findByClienteIdAndFechaInicioincIdencia(incidencia.getClienteId(),incidencia.getPersonaId(),incidencia.getTipoIncidenciaId().getTipoIncidenciaId(),incidencia.getFechaInicio(),incidencia.getFechaFin()).isEmpty();
                else
                    nsc= nscIncidenciaRepository.findByIncidenciaDuplicado(incidencia.getClienteId(),incidencia.getPersonaId(),incidencia.getFechaInicio(),incidencia.getTipoIncidenciaId().getTipoIncidenciaId()).isEmpty();

                respuesta = validaCamposObligatorios(incidencia,listIncidencia.size(),0);
                if (respuesta.isResultado()){
                    respuesta = validaciones(incidencia);
                    if (respuesta.isResultado()){
                        if (nsc) {
                            if (incidencia.getTipoIncidenciaId().getTipoIncidenciaId() == 5 && incidencia.getMonto() != null)
                            {
                                incidencia.setDuracion(1);
                            }
                            lista.add(incidencia);
                        }
                    }else {
                        return respuesta;
                    }
                }else {
                    return respuesta;
                }
            }
            if (lista.size() == listIncidencia.size()) {
                return guardar(lista);
            }
            else{
                RespuestaGenerica respuestas = new RespuestaGenerica();
                respuestas.setResultado(Constantes.RESULTADO_ERROR);
                respuestas.setMensaje("Una o más fechas seleccionadas ya se encuentran registradas para este empleado. Verifica en la lista de eventos o en el calendario antes de continuar.");
                return respuestas;
            }
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" guardarLista " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private Integer repetidos (List<NscIncidenciaDto> listIncidencias, NscIncidenciaDto incidencias){
        int cont=0;
        for (NscIncidenciaDto incidencia : listIncidencias) {
                if ( incidencias.getPersonaId().equals(incidencia.getPersonaId()) && incidencias.getTipoIncidenciaId().getTipoIncidenciaId().equals(incidencia.getTipoIncidenciaId().getTipoIncidenciaId()) &&  incidencias.getFechaInicio().equals(incidencia.getFechaInicio()))
                {
                    cont = cont +1;
                }
        }
        return cont;
    }

    private Integer temporalEvento(List<NscIncidenciaDto> listaSave,NscIncidenciaDto incidencia){
        if (incidencia.getTipoIncidenciaId().getTipoIncidenciaId().equals(CatTipoIncidenciaEnum.VACACIONES.getId()) || incidencia.getTipoIncidenciaId().getTipoIncidenciaId().equals(CatTipoIncidenciaEnum.DIAS_ECONOMICOS.getId()) ) {
            int sum = 0;
            for (NscIncidenciaDto l : listaSave) {
                if (incidencia.getTipoIncidenciaId().getTipoIncidenciaId().equals(l.getTipoIncidenciaId().getTipoIncidenciaId()) && incidencia.getPersonaId().equals(l.getPersonaId())) {
                    if (l.getDuracion() != null)
                        sum = sum + l.getDuracion();
                }
            }
            return sum;
        }
        return 0;
    }


    private RespuestaGenerica guardarListaCargaMasiva(List<NscIncidenciaDto> listIncidencia, List<CargaMasivaIncidencias> listaCargaIncidencias) throws ServiceException {
        try{
            Integer tempDuracion=0;
            RespuestaGenerica respuesta;
            List<NscIncidenciaDto> lista = new ArrayList<>();
            int i = 0;
            for (NscIncidenciaDto incidencia : listIncidencia) {
                tempDuracion =temporalEvento(lista,incidencia);
                respuesta = validaCamposObligatorios(incidencia, null,tempDuracion);
                if (respuesta.isResultado()){
                    respuesta = validaciones(incidencia);
                    if (respuesta.isResultado()){
                        boolean nsc = true;
                        if (incidencia.getTipoIncidenciaId().getTipoIncidenciaId() == 3) {
                            nsc = nscIncidenciaRepository.findByClienteIdAndFechaInicioincIdencia(incidencia.getClienteId(), incidencia.getPersonaId(), incidencia.getTipoIncidenciaId().getTipoIncidenciaId(), incidencia.getFechaInicio(), incidencia.getFechaFin()).isEmpty();
                            if (nsc) {
                                if (repetidos(listIncidencia, incidencia) > 1) {
                                    nsc = false;
                                }
                            }
                        }
                        else {
                                nsc = nscIncidenciaRepository.findByIncidenciaDuplicado(incidencia.getClienteId(), incidencia.getPersonaId(), incidencia.getFechaInicio(), incidencia.getTipoIncidenciaId().getTipoIncidenciaId()).isEmpty();
                                if (nsc) {
                                    if (repetidos(listIncidencia, incidencia) > 1) {
                                        nsc = false;
                                    }
                                }
                            }
                        if (nsc) {
                            if (incidencia.getTipoIncidenciaId().getTipoIncidenciaId() == 5 && incidencia.getMonto() != null)
                            {
                                incidencia.setDuracion(1);
                            }
                            lista.add(incidencia);
                            listaCargaIncidencias.get(i).setEsCorrecto(true);
                        } else {
                            listaCargaIncidencias.get(i).setEsCorrecto(false);
                            listaCargaIncidencias.get(i).setErrores("Ya se encuentra dado de alta");
                        }
                    }else {
                        listaCargaIncidencias.get(i).setEsCorrecto(false);
                        listaCargaIncidencias.get(i).setErrores(respuesta.getMensaje());
                    }
                }else {
                    listaCargaIncidencias.get(i).setEsCorrecto(false);
                    listaCargaIncidencias.get(i).setErrores(respuesta.getMensaje());
                }
                i++;
            }

            long eventosErroneos = listaCargaIncidencias.stream()
                    .filter(incidencias -> !incidencias.isEsCorrecto()).count();

            if (eventosErroneos == 0){
                return guardar(lista);
            }else {
                guardar(lista);
                return new RespuestaGenerica(listaCargaIncidencias,Constantes.RESULTADO_ERROR,Constantes.ERROR);
            }

        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" guardarListaCargaMasiva " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private RespuestaGenerica guardar(List<NscIncidenciaDto> listaIncidencia) {
        RespuestaGenerica respuesta = new RespuestaGenerica();
        listaIncidencia.forEach(incidencia -> {
            if(incidencia.getArchivo() != null){
                try {
                    guardarDocumento(incidencia);
                } catch (ServiceException e) {
                    LOG.error(Constantes.ERROR, e);
                }
            }
            incidencia.setUltimoEstadoIncidenciaId(new CatEstadoIncidencia());
            // CAMBIO EL VALOR DE 1 a 2 SOL X NAYE
            incidencia.getUltimoEstadoIncidenciaId().setEstadoIncidenciaId(Constantes.ESTADO_INCIDENCIA_ID_AUTORIZADA);
            incidencia.setEsActivo(Constantes.ESTATUS_ACTIVO);
            incidencia.setUltimaActualizacion(Utilidades.obtenerFechaSystema());
            respuesta.setDatos(ObjetoMapper.map(
                    nscIncidenciaRepository.save(
                            ObjetoMapper.map(incidencia, NscIncidencia.class)), NscIncidencia.class));
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            respuesta.setMensaje(Constantes.EXITO);

        });

        return respuesta;
    }

    @Override
    public RespuestaGenerica modificar(NscIncidenciaDto incidencia) throws ServiceException {
        try{
            RespuestaGenerica respuesta =  new RespuestaGenerica();
            if (incidencia.getIncidenciaId() != null){
                respuesta = validaCamposObligatorios(incidencia, null,0);
                if (respuesta.isResultado()){
                    if(incidencia.getArchivo() != null){
                        guardarDocumento(incidencia);
                    }
                    respuesta.setDatos(ObjetoMapper.map(
                            nscIncidenciaRepository.update(
                                    ObjetoMapper.map(incidencia, NscIncidencia.class)),
                            NscIncidencia.class));
                    respuesta.setResultado(Constantes.RESULTADO_EXITO);
                    respuesta.setMensaje(Constantes.EXITO);
                }
            }else{
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(Constantes.ID_NULO);
            }
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" modificar " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica modificarEstatus(NscIncidenciaDto incidencia) throws ServiceException {
        try{
            RespuestaGenerica respuesta =  new RespuestaGenerica();
            if (incidencia.getIncidenciaId() != null
                    && incidencia.getUltimoEstadoIncidenciaId() != null
                    && incidencia.getUltimoEstadoIncidenciaId().getEstadoIncidenciaId() != null){
                nscIncidenciaRepository.update(incidencia.getIncidenciaId(),incidencia.getUltimoEstadoIncidenciaId());
                respuesta.setDatos(obtenerId(incidencia.getIncidenciaId().longValue()));
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
                respuesta.setMensaje(Constantes.EXITO);
            }else{
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(Constantes.ID_NULO);
            }
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" modificarEstatus " + Constantes.ERROR_EXCEPCION, e);
        }
    }


    @Override
    public RespuestaGenerica eliminar(Long incidenciaId) throws ServiceException {
        try{
            RespuestaGenerica respuesta = new RespuestaGenerica();
            if (validaEventoNomina(incidenciaId)){
                nscIncidenciaRepository.update(incidenciaId.intValue(), Constantes.ESTATUS_INACTIVO);
                respuesta.setDatos(obtenerId(incidenciaId));
                respuesta.setMensaje(Constantes.EXITO);
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
            }else {
                respuesta.setMensaje(Constantes.ERROR_EVENTO_NOMINA);
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
            }
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" eliminar " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica obtenerId(Long incidenciaId) throws ServiceException {
        try{
            RespuestaGenerica respuesta = new RespuestaGenerica();
            respuesta.setDatos(
                    nscIncidenciaRepository.findById(incidenciaId.intValue()).orElse(new NscIncidencia()));
            respuesta.setMensaje(Constantes.EXITO);
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" obtenerId " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica listaClienteId(Long clienteId) throws ServiceException {
        try{
            RespuestaGenerica respuesta = new RespuestaGenerica();
            respuesta.setDatos(
                    incidenciasRepository.consultaClienteId(clienteId.intValue()));
            respuesta.setMensaje(Constantes.EXITO);
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" listaClienteId " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica obtenerIncidenciaId(Long incidenciaId) throws ServiceException {
        try{
            RespuestaGenerica respuesta = new RespuestaGenerica();
            respuesta.setDatos(
                    incidenciasRepository.consultaIncidenciaId(incidenciaId.intValue()));
            respuesta.setMensaje(Constantes.EXITO);
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" obtenerIncidenciaId " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica listaClienteIdFechaInicioFechaFin(NscIncidenciaDto incidencia) throws ServiceException {
        try{
            RespuestaGenerica respuesta = new RespuestaGenerica();
            respuesta.setDatos(
                    incidenciasRepository.consultalistaClienteIdFechaInicioFechaFin(
                            incidencia.getClienteId().longValue(),
                            incidencia.getFechaInicio(),
                            incidencia.getFechaFin()));
            respuesta.setMensaje(Constantes.EXITO);
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" listaClienteIdFechaInicioFechaFin " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica listaDinamica(IncidenciasConsulta incidencias) throws ServiceException {
        try{
            RespuestaGenerica respuesta = new RespuestaGenerica();
            respuesta.setDatos(
                    incidenciasRepository.consultaDimanicaIncidencia(incidencias));
            respuesta.setMensaje(Constantes.EXITO);
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" listaDinamica " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica cargaMasivaIncidencias(CargaMasivaDto cargaMasivaDto) throws ServiceException {
        try{
            cargaMasivaIncidenciasRepository.deleteByCentrocClienteId(cargaMasivaDto.getCentrocClienteId());
            List<CargaMasivaIncidencias> lista = cargaMasivaIncidenciasServices.carga(cargaMasivaDto.getArchivo(),
                    cargaMasivaDto.getCentrocClienteId());
            /** lista.forEach(incidencias -> {
                 Regla de registro de incidencias 1 y 2 (Sol x Kat/Naye)
                if (incidencias.getTipoEventoId().equals(CatTipoIncidenciaEnum.DIA_DESCANSO_LABORADO.getId())
                        || incidencias.getTipoEventoId().equals(CatTipoIncidenciaEnum.DIAS_FESTIVOS_LABORADOS.getId())) {
                    incidencias.setMonto(null);
                }
                cargaMasivaIncidenciasRepository.save(incidencias);
            });*/
            cargaMasivaIncidenciasRepository.saveAll(lista);
            guardarCargaMasiva(cargaMasivaDto);
            return generaRespuestaCargaMasiva(cargaMasivaDto.getCentrocClienteId());
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" cargaMasivaIncidencias " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica listaCargaMasiva(Integer centroClienteId) throws ServiceException {
        try{
            return new RespuestaGenerica(
                    listaCargaMasivaIncidenciasDto(centroClienteId),
                    Constantes.RESULTADO_EXITO, Constantes.EXITO);
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" listaCargaMasiva " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica listaCargaMasivaEsCorrecto(Integer centroClienteId,Boolean isEsCorrecto) throws ServiceException {
        try{
            return new RespuestaGenerica(
                    listaCargaMasivaIncidenciasDto(centroClienteId)
                            .stream()
                            .filter(cargaMasivaIncidenciasDto -> isEsCorrecto.equals(cargaMasivaIncidenciasDto.isEsCorrecto()))
                            .collect(Collectors.toList()),
                    Constantes.RESULTADO_EXITO, Constantes.EXITO);
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" listaCargaMasivaEsCorrecto " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica listaDinamicaPaginado(IncidenciasConsulta incidencias,Integer numeroRegistros, Integer pagina) throws ServiceException {
        try{
            Map<String, Object> repuesta = new HashMap<>();
            List<IncidenciasConsulta> lista = incidenciasRepository.consultaDimanicaIncidenciaPaginado(incidencias,numeroRegistros,pagina);
            List<IncidenciasConsulta> listaIncidencia = incidenciasRepository.consultaDimanicaIncidencia(incidencias);
            repuesta.put("lista",lista);
            repuesta.put("totalResgistros",listaIncidencia.size());
            return new RespuestaGenerica(repuesta,Constantes.RESULTADO_EXITO,Constantes.EXITO);
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" listaDinamica " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private RespuestaGenerica generaRespuestaCargaMasiva(Integer centroClienteId){
        if (cargaMasivaIncidenciasRepository.existsByCentrocClienteIdAndEsCorrecto(centroClienteId,false)){
            return new RespuestaGenerica(listaCargaMasivaIncidenciasDto(centroClienteId), Constantes.RESULTADO_ERROR, Constantes.ERROR_CARGA_MASIVA_INCIDENCIAS);
        }else{
            return new RespuestaGenerica(listaCargaMasivaIncidenciasDto(centroClienteId), Constantes.RESULTADO_EXITO, Constantes.EXITO_CARGA_MASIVA_PTU);
        }
    }

    private List<CargaMasivaIncidenciasDto> listaCargaMasivaIncidenciasDto(Integer centroClienteId){
        List<CargaMasivaIncidenciasDto> lista = new ArrayList<>();
        List<CargaMasivaIncidencias> listaIncidencias = cargaMasivaIncidenciasRepository.findByCentrocClienteId(centroClienteId);

        listaIncidencias.forEach(incidencias -> {
            CargaMasivaIncidenciasDto carga = ObjetoMapper.map(incidencias, CargaMasivaIncidenciasDto.class);
            NcoContratoColaborador colaborador = ncoContratoColaboradorRepository
                        .findByCentrocClienteIdCentrocClienteIdAndNumEmpleado(centroClienteId, incidencias.getNumeroEmpleado())
                        .orElse(null);
            
            if (colaborador != null){
                carga.setApellidoPaterno(colaborador.getPersonaId().getApellidoPaterno());
                carga.setNombre(colaborador.getPersonaId().getNombre());
            }

            lista.add(carga);
        });

        return lista;
    }

    private void guardarCargaMasiva(CargaMasivaDto cargaMasivaDto) throws ServiceException {
        try{
            List<CargaMasivaIncidencias> lista = cargaMasivaIncidenciasRepository.
                    findByCentrocClienteIdAndEsCorrecto(cargaMasivaDto.getCentrocClienteId(),
                            Constantes.RESULTADO_EXITO);
            List<CargaMasivaIncidencias> listaCargaIncidencias = new ArrayList<>();
            List<NscIncidenciaDto> listaIncidencias = new ArrayList<>();
            lista.forEach(incidencias -> {
                NscIncidenciaDto incidenciaDto = new NscIncidenciaDto();

                NcoContratoColaborador colaborador = null;
                try {
                    colaborador = ncoContratoColaboradorRepository.
                            findByCentrocClienteIdCentrocClienteIdAndNumEmpleado(
                                    incidencias.getCentrocClienteId(),incidencias.getNumeroEmpleado())
                            .orElseThrow(() -> new ServiceException(Constantes.ERROR_OBTENER_EMPLEADO));
                } catch (ServiceException e) {
                    LOG.error(Constantes.ERROR, e);
                }

                incidenciaDto.setPersonaId(colaborador.getPersonaId().getPersonaId());
                incidenciaDto.setFechaContrato(colaborador.getFechaContrato());
                incidenciaDto.setClienteId(colaborador.getCentrocClienteId().getCentrocClienteId());

                incidenciaDto.setTipoIncidenciaId(new CatTipoIncidencia());
                incidenciaDto.getTipoIncidenciaId().setTipoIncidenciaId(incidencias.getTipoEventoId());

                incidenciaDto.setTipoIncapacidadId(new CatTipoIncapacidad());
                incidenciaDto.getTipoIncapacidadId().setTipoIncapacidadId(incidencias.getTipoIncapacidadId());
                incidenciaDto.setMonto(incidencias.getMonto() != null ? BigDecimal.valueOf(incidencias.getMonto()) : null);

                incidenciaDto.setUnidadMedidaId(new CatUnidad());
                incidenciaDto.getUnidadMedidaId().setUnidadMedidaId(incidencias.getUnidadMedidaId());

                if (incidencias.getMonto() != null) {
                    if (incidencias.getMonto() > 0 && incidencias.getFechaInicio() != null) {
                        incidenciaDto.setDuracion(1);
                    } else {
                        incidenciaDto.setDuracion(0);
                    }
                } else {
                    incidenciaDto.setDuracion(0);
                }
                if (incidencias.getTipoEventoId() ==5 )
                {
                    incidenciaDto.setDuracion(incidencias.getNumeroDias().intValue());
                    incidenciaDto.setHeTiempo(0);
                }

                if (incidencias.getTipoEventoId() ==11 || incidencias.getTipoEventoId() == 2 || incidencias.getTipoEventoId() ==16 || incidencias.getTipoEventoId() ==3 || incidencias.getTipoEventoId() ==9 || incidencias.getTipoEventoId() ==1 )
                {
                    incidenciaDto.setDuracion(incidencias.getNumeroDias().intValue());
                    incidenciaDto.setHeTiempo(0);
                }

              //  incidenciaDto.setDuracion(incidencias.getNumeroHorasExtras() != null ? incidencias.getNumeroHorasExtras() : 0);
                incidenciaDto.setHeTiempo(incidencias.getNumeroDias() != null ? incidencias.getNumeroDias().intValue() : 0);
                incidenciaDto.setFechaInicio(incidencias.getFechaInicio() != null ? new Timestamp(incidencias.getFechaInicio().getTime()) : null);
                incidenciaDto.setFechaAplicacion(incidencias.getFechaAplicacion() != null ? new Timestamp(incidencias.getFechaAplicacion().getTime()) : null);
                listaCargaIncidencias.add(incidencias);
                listaIncidencias.add(incidenciaDto);
            });
            if (!listaCargaIncidencias.isEmpty() && !listaIncidencias.isEmpty()){
                guardaCargaMasivaIncidencias(listaCargaIncidencias, listaIncidencias);
            }

        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" listaCargaMasivaEsCorrecto " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private void guardaCargaMasivaIncidencias(List<CargaMasivaIncidencias> listaCargaIncidencias, List<NscIncidenciaDto> listaIncidencia) throws ServiceException {
        RespuestaGenerica respuesta = guardarListaCargaMasiva(listaIncidencia,listaCargaIncidencias);
        if (!respuesta.isResultado()){
            listaCargaIncidencias = (List<CargaMasivaIncidencias>) respuesta.getDatos();
            listaCargaIncidencias.forEach(incidencias -> cargaMasivaIncidenciasRepository.update(incidencias));
        }
    }

    private RespuestaGenerica validaCamposObligatorios(NscIncidenciaDto incidencia,Integer numero,Integer tempDuracion) throws ServiceException {
        try{
            RespuestaGenerica respuesta =  new RespuestaGenerica();
            if(incidencia.getTipoIncidenciaId() == null
                    || incidencia.getTipoIncidenciaId().getTipoIncidenciaId() == null
                    || incidencia.getPersonaId() == null
                    || incidencia.getClienteId() == null
                    || incidencia.getFechaContrato() == null
                    || incidencia.getFechaInicio() == null){
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(Constantes.CAMPOS_REQUERIDOS);
                return respuesta;
            }

            if (incidencia.getFechaFin() == null){
                incidencia.setFechaFin(incidencia.getFechaInicio());
            }

            if (incidencia.getTipoIncidenciaId().getTipoIncidenciaId().equals(CatTipoIncidenciaEnum.VACACIONES.getId())
                    || incidencia.getTipoIncidenciaId().getTipoIncidenciaId().equals(CatTipoIncidenciaEnum.DIAS_ECONOMICOS.getId())
                    || incidencia.getTipoIncidenciaId().getTipoIncidenciaId().equals(CatTipoIncidenciaEnum.FALTAS.getId())){
                respuesta = validaVacacionesDiasEconomicosFaltas(incidencia,numero,tempDuracion);
                if (!respuesta.isResultado())
                    return respuesta;
            }

            if (incidencia.getTipoIncidenciaId().getTipoIncidenciaId().equals(CatTipoIncidenciaEnum.INCAPACIDADES.getId())){
                respuesta = validaIncapacidades(incidencia);
                if (!respuesta.isResultado())
                    return respuesta;
            }

            if (incidencia.getTipoIncidenciaId().getTipoIncidenciaId().equals(CatTipoIncidenciaEnum.HORAS_EXTRAS_DOBLES.getId())
                    || incidencia.getTipoIncidenciaId().getTipoIncidenciaId().equals(CatTipoIncidenciaEnum.HORAS_EXTRAS_TRIPLES.getId())){
                respuesta = validaHorasExtras(incidencia);
            }

            if (incidencia.getTipoIncidenciaId().getTipoIncidenciaId().equals(CatTipoIncidenciaEnum.RETARDO.getId())){
                respuesta = validaRetardos(incidencia);
                if (!respuesta.isResultado())
                    return respuesta;
            }

            if (incidencia.getTipoIncidenciaId().getTipoIncidenciaId().equals(CatTipoIncidenciaEnum.DIA_DESCANSO_LABORADO.getId())
                    || incidencia.getTipoIncidenciaId().getTipoIncidenciaId().equals(CatTipoIncidenciaEnum.DIAS_FESTIVOS_LABORADOS.getId())
                || incidencia.getTipoIncidenciaId().getTipoIncidenciaId().equals(CatTipoIncidenciaEnum.PRIMA_DOMINICAL.getId())){
                respuesta = validaPrimaDominicalDiaFestivoDescanzo(incidencia);
                if (!respuesta.isResultado())
                    return respuesta;
            }

            // Regla de registro de incidencias 1 y 2 (Sol x Kat/Naye)
            if (incidencia.getTipoIncidenciaId().getTipoIncidenciaId().equals(CatTipoIncidenciaEnum.DIA_DESCANSO_LABORADO.getId())
                    || incidencia.getTipoIncidenciaId().getTipoIncidenciaId().equals(CatTipoIncidenciaEnum.DIAS_FESTIVOS_LABORADOS.getId())) {
                incidencia.setMonto(null);
            }

            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" validaCamposObligatorios " + Constantes.ERROR_EXCEPCION, e);
        }
    }


    private boolean validaPrimaDominical (Date fecha){
        Calendar cal = Calendar.getInstance();
        cal.setTime(fecha);
        return cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
}



    private RespuestaGenerica validaVacacionesDiasEconomicosFaltas(NscIncidenciaDto incidencia,Integer numero , Integer tempDuracion) throws ServiceException {
        RespuestaGenerica respuesta =  new RespuestaGenerica(null, Constantes.RESULTADO_EXITO,Constantes.EXITO);
        if(incidencia.getFechaInicio() == null
            || incidencia.getFechaFin() == null
            || incidencia.getFechaAplicacion() == null){
            respuesta.setResultado(Constantes.RESULTADO_ERROR);
            respuesta.setMensaje(Constantes.CAMPOS_REQUERIDOS);
            return respuesta;
        }


      /*  if (incidencia.getTipoIncidenciaId().getTipoIncidenciaId().equals(CatTipoIncidenciaEnum.PRIMA_DOMINICAL.getId())) {
            if (validaPrimaDominical(incidencia.getFechaInicio())) {
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(Constantes.ERROR_INCIDENCIAS_DIAS_INSUFICIENTES_VACACIONES);
                return respuesta;
            }
        }*/


        NcoContratoColaborador colaborador = obtenEmpleado(incidencia);

        if (validaJornada(incidencia, colaborador)){
            Calendar fechaAntiguedad = Calendar.getInstance();
            fechaAntiguedad.setTime(colaborador.getFechaAntiguedad());

            Calendar fechaActual = Calendar.getInstance();
            fechaActual.setTime(incidencia.getFechaInicio());

            fechaAntiguedad.set(Calendar.YEAR,fechaActual.get(Calendar.YEAR));
            int anio = fechaActual.get(Calendar.YEAR);

            if(fechaAntiguedad.getTime().getTime() > fechaActual.getTime().getTime()){
                fechaAntiguedad.set(Calendar.YEAR,anio - 1);
            }

            Calendar cFechaUltimoDiaAnio = Calendar.getInstance();
            cFechaUltimoDiaAnio.setTime(fechaAntiguedad.getTime());
            cFechaUltimoDiaAnio.add(Calendar.YEAR,1);
            cFechaUltimoDiaAnio.add(Calendar.DAY_OF_MONTH,-1);

            Date fechaUltimoDiaAnio = cFechaUltimoDiaAnio.getTime();
            Date fechaInicioAnio = fechaAntiguedad.getTime();

            int antiguedad = obtenAntiguedad(incidencia.getFechaInicio(), colaborador.getFechaAntiguedad());
            int vacaciones = 0;
            if (antiguedad == 0){
                antiguedad = 1;
            }
            int diasVacaciones=0;
            if (colaborador.getDiasVacaciones()!=null)
                diasVacaciones= colaborador.getDiasVacaciones();
            Integer vacacionesDerecho = diasVacaciones +
                    nclBeneficioXpoliticaRepository.consultaDiasVacaciones(colaborador.getPoliticaId().getPoliticaId(),
                            obtenerAniosAntiguedad(nclBeneficioXpoliticaRepository.
                                    findByPoliticaIdAndAniosAntiguedad(colaborador.getPoliticaId().getPoliticaId(), antiguedad),
                                    antiguedad));

            Optional<Long> vacacionesTomadas = nscIncidenciaRepository.sumDuracionIncidencia(incidencia.getTipoIncidenciaId().getTipoIncidenciaId(),
                    2, fechaInicioAnio,fechaUltimoDiaAnio,incidencia.getPersonaId());

            Integer tomadas = vacacionesTomadas.isPresent() ? vacacionesTomadas.get().intValue() : 0;
            vacaciones = vacacionesDerecho - (tomadas + tempDuracion);

            Integer duracion;
            if (numero != null){
                duracion = numero;
            }else{
                duracion = incidencia.getDuracion();
            }

            if (incidencia.getTipoIncidenciaId().getTipoIncidenciaId().equals(CatTipoIncidenciaEnum.VACACIONES.getId())
                    && vacaciones < duracion){
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(Constantes.ERROR_INCIDENCIAS_DIAS_INSUFICIENTES_VACACIONES);
                return respuesta;
            }


            if(incidencia.getTipoIncidenciaId().getTipoIncidenciaId().equals(CatTipoIncidenciaEnum.DIAS_ECONOMICOS.getId())){
                Integer diasEconomicosDerecho = colaborador.getPoliticaId().getDiasEconomicos();
                Optional<Long> diasEconomicosTomados = nscIncidenciaRepository.sumDuracionIncidenciaUltimoEstado(incidencia.getTipoIncidenciaId().getTipoIncidenciaId(),
                         fechaInicioAnio,fechaUltimoDiaAnio,incidencia.getPersonaId());

                Integer diasTomadas = diasEconomicosTomados.isPresent() ? diasEconomicosTomados.get().intValue() : 0;

                Integer diasEconomicos = diasEconomicosDerecho - (diasTomadas + tempDuracion);

                if (diasEconomicos < duracion){
                    respuesta.setResultado(Constantes.RESULTADO_ERROR);
                    respuesta.setMensaje(Constantes.ERROR_INCIDENCIAS_DIAS_INSUFICIENTES);
                }

                return respuesta;
            }
        }else {
            respuesta.setResultado(Constantes.RESULTADO_ERROR);
            respuesta.setMensaje(Constantes.ERROR_FECHA_FUERA_JORNADA);
        }

        return respuesta;
    }

    private RespuestaGenerica validaIncapacidades(NscIncidenciaDto incidencia){
        RespuestaGenerica respuesta =  new RespuestaGenerica(null, Constantes.RESULTADO_EXITO,Constantes.EXITO);
        if(incidencia.getDuracion() == null
            || incidencia.getFechaInicio() == null
            || incidencia.getFechaFin() == null
            || incidencia.getFechaAplicacion() == null
            || incidencia.getTipoIncapacidadId() == null
            || incidencia.getTipoIncapacidadId().getTipoIncapacidadId() == null
            /**|| incidencia.getNumeroFolio() == null se quita la validacion del back*/
        ){
            respuesta.setResultado(Constantes.RESULTADO_ERROR);
            respuesta.setMensaje(Constantes.CAMPOS_REQUERIDOS);
            return respuesta;
        }

        NcoPersona persona = ncoPersonaRepository.findByPersonaId(incidencia.getPersonaId());

        if (incidencia.getTipoIncapacidadId().getTipoIncapacidadId().equals(3)){
            if (persona.getGenero().equals("F")){
                if (incidencia.getDuracion() > Constantes.DIAS_MATERNIDAD){
                    respuesta.setResultado(Constantes.RESULTADO_ERROR);
                    respuesta.setMensaje(Constantes.ERROR_INCAPACIDAD_MATERNIDAD);
                }
            }else {
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(Constantes.ERROR_INCIDENCIA_MATERNIDAD_GENERO);
            }
        }

        if (incidencia.getNumeroFolio() != null && !incidencia.getNumeroFolio().isEmpty()){
            if (!Validar.numeroFolio(incidencia.getNumeroFolio())){
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(Constantes.ERROR_FORMATO_NUMERO_FOLIO);
            }
        }

        return respuesta;
    }

    private RespuestaGenerica validaRetardos(NscIncidenciaDto incidencia) throws ServiceException {
        RespuestaGenerica respuesta =  validaRetardosCampos(incidencia);
        if (!validaJornada(incidencia, obtenEmpleado(incidencia))){
            respuesta.setResultado(Constantes.RESULTADO_ERROR);
            respuesta.setMensaje(Constantes.ERROR_FECHA_FUERA_JORNADA);
        }

        return respuesta;
    }

    private RespuestaGenerica validaRetardosCampos(NscIncidenciaDto incidencia){
        RespuestaGenerica respuesta =  new RespuestaGenerica(null, Constantes.RESULTADO_EXITO,Constantes.EXITO);
        if(incidencia.getFechaInicio() == null
                || incidencia.getFechaAplicacion() == null
                || incidencia.getMonto() == null){
            respuesta.setResultado(Constantes.RESULTADO_ERROR);
            respuesta.setMensaje(Constantes.CAMPOS_REQUERIDOS);
            return respuesta;
        }
        return respuesta;
    }

    private RespuestaGenerica validaHorasExtras(NscIncidenciaDto incidencia) {
        RespuestaGenerica respuesta =  validaHorasExtrasCampos(incidencia);
        if (respuesta.isResultado()){
            respuesta =  validaHorasExtrasUnidadMedida(incidencia);
            /**if (respuesta.isResultado()){
                if (!validaJornada(incidencia, obtenEmpleado(incidencia))){
                    respuesta.setResultado(Constantes.RESULTADO_ERROR);
                    respuesta.setMensaje(Constantes.ERROR_FECHA_DENTRO_JORNADA);
                }
            }*/
        }

        return respuesta;
    }

    private RespuestaGenerica validaHorasExtrasUnidadMedida(NscIncidenciaDto incidencia){
        RespuestaGenerica respuesta =  new RespuestaGenerica(null, Constantes.RESULTADO_EXITO,Constantes.EXITO);
        if (incidencia.getUnidadMedidaId() == null || incidencia.getUnidadMedidaId().getUnidadMedidaId() == null){
            respuesta.setResultado(Constantes.RESULTADO_ERROR);
            respuesta.setMensaje(Constantes.ERROR_INCIDENCIA_HORAS_EXTRAS_UNIDAD);
        }
        return respuesta;
    }

    private RespuestaGenerica validaHorasExtrasCampos(NscIncidenciaDto incidencia){
        // Naye: Cuando es horas extras la incidencia puede aplicar por tiempo y monto puede ser null
        RespuestaGenerica respuesta =  new RespuestaGenerica(null, Constantes.RESULTADO_EXITO,Constantes.EXITO);
        if(incidencia.getFechaInicio() == null
                || incidencia.getFechaAplicacion() == null){
            respuesta.setResultado(Constantes.RESULTADO_ERROR);
            respuesta.setMensaje(Constantes.CAMPOS_REQUERIDOS);
            return respuesta;
        }
        return respuesta;
    }

    private RespuestaGenerica validaPrimaDominicalDiaFestivoDescanzo(NscIncidenciaDto incidencia) throws ServiceException {
        RespuestaGenerica respuesta =  new RespuestaGenerica(null, Constantes.RESULTADO_EXITO,Constantes.EXITO);
        if(incidencia.getDuracion() == null
            || incidencia.getFechaInicio() == null
            || incidencia.getFechaAplicacion() == null){
            respuesta.setResultado(Constantes.RESULTADO_ERROR);
            respuesta.setMensaje(Constantes.CAMPOS_REQUERIDOS);
            return respuesta;
        }

        if (incidencia.getTipoIncidenciaId().getTipoIncidenciaId().equals(CatTipoIncidenciaEnum.PRIMA_DOMINICAL.getId())){
            Calendar fechaInicio = Calendar.getInstance();
            fechaInicio.setTime(incidencia.getFechaInicio());
            Integer dia = fechaInicio.get(Calendar.DAY_OF_WEEK);
            if (!dia.equals(1)){
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(Constantes.ERROR_PRIMA_DOMINICAL);
            }
        }

        if (incidencia.getTipoIncidenciaId().getTipoIncidenciaId().equals(CatTipoIncidenciaEnum.DIA_DESCANSO_LABORADO.getId())
                && validaJornada(incidencia, obtenEmpleado(incidencia))){
            respuesta.setResultado(Constantes.RESULTADO_ERROR);
            respuesta.setMensaje(Constantes.ERROR_FECHA_DENTRO_JORNADA);
        }

        if (incidencia.getTipoIncidenciaId().getTipoIncidenciaId().equals(CatTipoIncidenciaEnum.DIAS_FESTIVOS_LABORADOS.getId())
                && !validaJornada(incidencia, obtenEmpleado(incidencia))){
            respuesta.setResultado(Constantes.RESULTADO_ERROR);
            respuesta.setMensaje(Constantes.ERROR_FECHA_FUERA_JORNADA);
        }

        return respuesta;
    }

    private RespuestaGenerica validaciones(NscIncidenciaDto incidencia) throws ServiceException {
        RespuestaGenerica respuesta =  new RespuestaGenerica(null, Constantes.RESULTADO_EXITO,Constantes.EXITO);

        NcoContratoColaborador colaborador = this.obtenEmpleado(incidencia);
        if (incidencia.getFechaAplicacion().before(colaborador.getFechaContrato())){
            respuesta.setResultado(Constantes.RESULTADO_ERROR);
            respuesta.setMensaje(Constantes.ERROR_INCIDENCIAS_FECHA_CONTRATO);
            return respuesta;
        }


        if (incidencia.getFechaInicio().before(colaborador.getFechaContrato())){
            respuesta.setResultado(Constantes.RESULTADO_ERROR);
            respuesta.setMensaje(Constantes.ERROR_INCIDENCIAS_FECHA_CONTRATO);
            return respuesta;
        }

        /**
         if (!(incidencia.getTipoIncidenciaId().getTipoIncidenciaId().equals(CatTipoIncidenciaEnum.HORAS_EXTRAS_DOBLES.getId())
                || incidencia.getTipoIncidenciaId().getTipoIncidenciaId().equals(CatTipoIncidenciaEnum.HORAS_EXTRAS_TRIPLES.getId()))){

            if (esErrorValidacionRetardo(incidencia) || esErrorValidacionRetardoViceversa(incidencia)) {
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(Constantes.ERROR_INCIDENCIAS_SOLICITUD_FECHAS);
                return respuesta;
            }
        }


         * 2030 - No permite agregar eventos
          if(ncrNominaXperiodoRepository.
                countFechaInicioAndFechaFinAndEstadoNominaIdActualAndPersonaId(incidencia.getFechaAplicacion(),
                        incidencia.getPersonaId()) > 0){
            respuesta.setResultado(Constantes.RESULTADO_ERROR);
            respuesta.setMensaje(Constantes.ERROR_INCIDENCIAS_EVENTOS_FECHA);
        }
         */

     /*    List<NcrNominaXperiodo> ncr= ncrNominaXperiodoRepository.findByFechaInicioAndFechaFinAndEstadoNominaIdActualAndPersonaId(
                        incidencia.getFechaAplicacion(),
                        incidencia.getFechaAplicacion(),
                        Arrays.asList(Constantes.ID_ESTATUS_NOMINA_PAGADA,
                                Constantes.ID_ESTATUS_NOMINA_TIMBRADA,
                                Constantes.ID_ESTATUS_NOMINA_COMPLETA),
                        incidencia.getPersonaId());
        if (!ncr.isEmpty()){
            respuesta.setResultado(Constantes.RESULTADO_ERROR);
            respuesta.setMensaje(Constantes.ERROR_INCIDENCIAS_EVENTOS_FECHA);
            return respuesta;
        }*/
        return respuesta;
    }

    // Regla de registro de incidencias 3 (Sol x Kat/Naye)
    private boolean esErrorValidacionRetardo(NscIncidenciaDto incidencia) {
        List<NscIncidencia> list = nscIncidenciaRepository.findByPersonaIdAndFechaInicioAndEsActivo(
                incidencia.getPersonaId(), incidencia.getFechaInicio(), incidencia.getFechaFin(),true);

        if (incidencia.getTipoIncidenciaId().getTipoIncidenciaId().equals(CatTipoIncidenciaEnum.RETARDO.getId())) {
            for (NscIncidencia element : list) {
                Integer tipoIncidenciaId = element.getTipoIncidenciaId().getTipoIncidenciaId();
                if (!tipoIncidenciaId.equals(CatTipoIncidenciaEnum.PRIMA_DOMINICAL.getId())
                        && !tipoIncidenciaId.equals(CatTipoIncidenciaEnum.HORAS_EXTRAS_DOBLES.getId())
                        && !tipoIncidenciaId.equals(CatTipoIncidenciaEnum.HORAS_EXTRAS_TRIPLES.getId())) {
                    return true;
                }
            }
        } else {
            return !list.isEmpty();
        }
        return false;
    }

    // ó viceversa
    private boolean esErrorValidacionRetardoViceversa(NscIncidenciaDto incidencia) {
        List<NscIncidencia> list = nscIncidenciaRepository.findByPersonaIdAndFechaInicioAndEsActivo(
                incidencia.getPersonaId(), incidencia.getFechaInicio(), incidencia.getFechaFin(),true);

        Integer tipoIncidenciaId = incidencia.getTipoIncidenciaId().getTipoIncidenciaId();
        if (tipoIncidenciaId.equals(CatTipoIncidenciaEnum.PRIMA_DOMINICAL.getId())
                || tipoIncidenciaId.equals(CatTipoIncidenciaEnum.HORAS_EXTRAS_DOBLES.getId())
                || tipoIncidenciaId.equals(CatTipoIncidenciaEnum.HORAS_EXTRAS_TRIPLES.getId())) {
            for (NscIncidencia element : list) {
                if (!element.getTipoIncidenciaId().getTipoIncidenciaId().equals(CatTipoIncidenciaEnum.RETARDO.getId())) {
                    return true;
                }
            }
        } else {
            return !list.isEmpty();
        }
        return false;
    }

    private boolean validaJornada(NscIncidenciaDto incidencia, NcoContratoColaborador colaborador){
        Calendar fechaInicio = Calendar.getInstance();
        fechaInicio.setTime(incidencia.getFechaInicio());
        int dia = fechaInicio.get(Calendar.DAY_OF_WEEK) - 1;
        dia = dia != 0 ? dia : 7;
        return nclHorarioJornadaRepository.
                existsByNclJornadaJornadaIdAndDiaAndEsActivo(colaborador.getJornadaId().getJornadaId(),
                        dia, Constantes.ESTATUS_ACTIVO);
    }

    private NcoContratoColaborador obtenEmpleado(NscIncidenciaDto incidencia) throws ServiceException {
        return ncoContratoColaboradorRepository.findByFechaContratoAndPersonaIdPersonaIdAndCentrocClienteIdCentrocClienteId(
                        incidencia.getFechaContrato(),
                        incidencia.getPersonaId(),
                        incidencia.getClienteId()).orElseThrow(() -> new ServiceException(Constantes.ERROR_OBTENER_EMPLEADO));
    }

    private boolean validaEventoNomina(Long incidenciaId){
        Optional<NscIncidencia> incidencia = nscIncidenciaRepository.findByIncidenciaIdEstadoId
                (incidenciaId.intValue(), 4);
        return !incidencia.isPresent();
    }

    private void guardarDocumento(NscIncidenciaDto incidencia) throws ServiceException {
        DateFormat df = new SimpleDateFormat("ddMMyyyy");

        String fechaAplicacion = df.format(incidencia.getFechaAplicacion());
        String fechaInicio = df.format(incidencia.getFechaInicio());
        DocumentosEmpleadoDto documentos = new DocumentosEmpleadoDto();
        documentos.setCentrocClienteId(incidencia.getClienteId());
        documentos.setPersonaId(incidencia.getPersonaId());
        documentos.setTipoDocumentoId(11);
        documentos.setUsuarioId(1);
        documentos.setNombreArchivo(fechaAplicacion + "-" + fechaInicio + "-" + incidencia.getNombreArchivo());
        documentos.setDocumento(incidencia.getArchivo());
        documentosEmpleadoService.guardar(documentos);
    }

    private Integer obtenerAniosAntiguedad(List<NclBeneficioXpolitica> beneficioXpoliticas, int antiguedad) {
        int cercano = 0;
        int diferencia = 73;
        for (NclBeneficioXpolitica beneficioXpolitica : beneficioXpoliticas) {
            if (beneficioXpolitica.getAniosAntiguedad() == antiguedad) {
                cercano = beneficioXpolitica.getAniosAntiguedad();
            } else {
                if (Math.abs(beneficioXpolitica.getAniosAntiguedad() - antiguedad) < diferencia) {
                    cercano = beneficioXpolitica.getAniosAntiguedad();
                    diferencia = Math.abs(beneficioXpolitica.getAniosAntiguedad() - antiguedad);
                }
            }
        }
        return cercano;
    }

    private Integer obtenAntiguedad (Date fechaFinalPago, Date fechaAntiguedad) {
        Calendar fechaDesde = Calendar.getInstance();
        Calendar fechaHasta = Calendar.getInstance();
        fechaDesde.setTime(fechaAntiguedad);
        fechaHasta.setTime(fechaFinalPago);
        int incremento = 0;

        if (fechaDesde.get(Calendar.DAY_OF_MONTH) > fechaHasta.get(Calendar.DAY_OF_MONTH)) {
            incremento = fechaDesde.getActualMaximum(Calendar.DAY_OF_MONTH);
        }

        if (incremento != 0) {
            incremento = 1;
        }

        if ((fechaDesde.get(Calendar.MONTH) + incremento) > fechaHasta.get(Calendar.MONTH)) {
            incremento = 1;
        } else {
            incremento = 0;
        }

        return fechaHasta.get(Calendar.YEAR) - (fechaDesde.get(Calendar.YEAR) + incremento);
    }


}
