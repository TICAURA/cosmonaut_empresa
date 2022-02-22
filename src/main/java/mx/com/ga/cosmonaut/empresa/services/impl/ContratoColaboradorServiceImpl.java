package mx.com.ga.cosmonaut.empresa.services.impl;

import mx.com.ga.cosmonaut.common.dto.NcoContratoColaboradorDto;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.dto.RespuestaGoogleStorage;
import mx.com.ga.cosmonaut.common.dto.consultas.ContratoColaboradorConsulta;
import mx.com.ga.cosmonaut.common.entity.calculo.NcrNominaXperiodo;
import mx.com.ga.cosmonaut.common.entity.colaborador.ContratoColaborador;
import mx.com.ga.cosmonaut.common.entity.colaborador.ContratoColaboradorPK;
import mx.com.ga.cosmonaut.common.entity.colaborador.NcoContratoColaborador;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.common.repository.PagosLiquidacionColaboradorRepository;
import mx.com.ga.cosmonaut.common.repository.calculo.NcrEmpleadoXnominaRepository;
import mx.com.ga.cosmonaut.common.repository.colaborador.ContratoColaboradorPKRepository;
import mx.com.ga.cosmonaut.common.repository.colaborador.NcoContratoColaboradorRepository;
import mx.com.ga.cosmonaut.common.repository.colaborador.NcoPersonaRepository;
import mx.com.ga.cosmonaut.common.repository.nativo.ContratoColaboradorRepository;
import mx.com.ga.cosmonaut.common.service.GoogleStorageService;
import mx.com.ga.cosmonaut.common.util.Constantes;
import mx.com.ga.cosmonaut.common.util.ObjetoMapper;
import mx.com.ga.cosmonaut.common.util.Utilidades;
import mx.com.ga.cosmonaut.empresa.services.ContratoColaboradorService;
import mx.com.ga.cosmonaut.empresa.services.PagosLiquidacionColaboradorServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton
public class ContratoColaboradorServiceImpl implements ContratoColaboradorService {

    private static final Logger LOG = LoggerFactory.getLogger(ContratoColaboradorServiceImpl.class);

    @Inject
    private NcoContratoColaboradorRepository ncoContratoColaboradorRepository;

    @Inject
    private ContratoColaboradorRepository contratoColaboradorRepository;

    @Inject
    private NcrEmpleadoXnominaRepository ncrEmpleadoXnominaRepository;

    @Inject
    private NcoPersonaRepository ncoPersonaRepository;

    @Inject
    private PagosLiquidacionColaboradorServices pagosLiquidacionColaboradorServices;

    @Inject
    private PagosLiquidacionColaboradorRepository pagosLiquidacionColaboradorRepository;

    @Inject
    private GoogleStorageService googleStorageService;

    @Inject
    private ContratoColaboradorPKRepository colaboradorPKRepository;

    @Override
    public RespuestaGenerica guardar(NcoContratoColaborador contratoColaborador) throws ServiceException {
        try{
            RespuestaGenerica respuesta = validaCamposObligatorios(contratoColaborador);
            if (respuesta.isResultado()){
                if (!validaNumeroEmpleado(contratoColaborador.getNumEmpleado(),
                        contratoColaborador.getCentrocClienteId().getCentrocClienteId())){
                    if (!ncoContratoColaboradorRepository.existsByFechaContratoAndPersonaIdPersonaIdAndCentrocClienteIdCentrocClienteId(
                            contratoColaborador.getFechaContrato(),
                            contratoColaborador.getPersonaId().getPersonaId(),
                            contratoColaborador.getCentrocClienteId().getCentrocClienteId())){
                        contratoColaborador.setEsActivo(Constantes.ESTATUS_ACTIVO);
                        respuesta.setDatos(ncoContratoColaboradorRepository.save(contratoColaborador));
                    }else {
                        return new RespuestaGenerica(null,Constantes.RESULTADO_ERROR, Constantes.ERROR_COLABORADOR_EXISTE);
                    }
                }else{
                    return new RespuestaGenerica(null,Constantes.RESULTADO_ERROR, Constantes.ERROR_NUMERO_EMPLEADO);
                }
            }
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" guardar " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica guardarReactivar(NcoContratoColaborador contratoColaborador) throws ServiceException {
        try{
            RespuestaGenerica respuesta = validaCamposObligatorios(contratoColaborador);
            contratoColaborador = reactivaEmpleado(contratoColaborador);
            if (respuesta.isResultado()){
                if (!validaNumeroEmpleado(contratoColaborador.getNumEmpleado(),
                        contratoColaborador.getCentrocClienteId().getCentrocClienteId())){
                    if (!ncoContratoColaboradorRepository.existsByFechaContratoAndPersonaIdPersonaIdAndCentrocClienteIdCentrocClienteId(
                            contratoColaborador.getFechaContrato(),
                            contratoColaborador.getPersonaId().getPersonaId(),
                            contratoColaborador.getCentrocClienteId().getCentrocClienteId())){
                        contratoColaborador.setEsActivo(Constantes.ESTATUS_ACTIVO);
                        respuesta.setDatos(ncoContratoColaboradorRepository.save(contratoColaborador));
                    }else {
                        return new RespuestaGenerica(null,Constantes.RESULTADO_ERROR, Constantes.ERROR_REACTIVAR_FECHA_CONTRATO);
                    }
                }else{
                    return new RespuestaGenerica(null,Constantes.RESULTADO_ERROR, Constantes.ERROR_REACTIVAR_NUMERO_EMPLEADO);
                }
            }
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" guardar " + Constantes.ERROR_EXCEPCION, e);
        }
    }


    @Override
    public RespuestaGenerica modificar(NcoContratoColaborador contratoColaborador) throws ServiceException {
        try{
            RespuestaGenerica respuesta =  validaEmpleado(contratoColaborador);
            if (respuesta.isResultado()){
                ContratoColaborador colaborador = ObjetoMapper.map(contratoColaborador, ContratoColaborador.class);
                colaborador.setContratoColaboradorPK(new ContratoColaboradorPK());
                colaborador.getContratoColaboradorPK().setCentrocClienteId(contratoColaborador.getCentrocClienteId().getCentrocClienteId());
                colaborador.getContratoColaboradorPK().setFechaContrato(contratoColaborador.getFechaContrato());
                colaborador.getContratoColaboradorPK().setPersonaId(contratoColaborador.getPersonaId().getPersonaId());
                colaboradorPKRepository.update(colaborador);
                //ncoContratoColaboradorRepository.update(contratoColaborador);
                respuesta.setDatos(ncoContratoColaboradorRepository.findByFechaContratoAndPersonaIdPersonaIdAndCentrocClienteIdCentrocClienteId(
                        contratoColaborador.getFechaContrato(),
                        contratoColaborador.getPersonaId().getPersonaId(),
                        contratoColaborador.getCentrocClienteId().getCentrocClienteId()
                ).orElse(new NcoContratoColaborador()));
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
                respuesta.setMensaje(Constantes.EXITO);
            }else {
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(Constantes.ERROR_OBTENER_EMPLEADO);
            }
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" modificar " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private RespuestaGenerica modificarBaja(NcoContratoColaborador contratoColaborador) throws ServiceException {
        try{
            RespuestaGenerica respuesta =  validaEmpleado(contratoColaborador);
            if (respuesta.isResultado()){
                contratoColaboradorRepository.update(contratoColaborador);
                respuesta.setDatos(ncoContratoColaboradorRepository.findByFechaContratoAndPersonaIdPersonaIdAndCentrocClienteIdCentrocClienteId(
                        contratoColaborador.getFechaContrato(),
                        contratoColaborador.getPersonaId().getPersonaId(),
                        contratoColaborador.getCentrocClienteId().getCentrocClienteId()
                ).orElse(new NcoContratoColaborador()));
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
                respuesta.setMensaje(Constantes.EXITO);
            }else {
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(Constantes.ERROR_OBTENER_EMPLEADO);
            }
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" modificar " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica modificarCompensacion(NcoContratoColaborador contratoColaborador) throws ServiceException {
        try{
            RespuestaGenerica respuesta =  validaEmpleado(contratoColaborador);
            if (respuesta.isResultado()){
                ContratoColaborador colaborador = ObjetoMapper.map(contratoColaborador, ContratoColaborador.class);
                colaborador.setContratoColaboradorPK(new ContratoColaboradorPK());
                colaborador.getContratoColaboradorPK().setCentrocClienteId(contratoColaborador.getCentrocClienteId().getCentrocClienteId());
                colaborador.getContratoColaboradorPK().setFechaContrato(contratoColaborador.getFechaContrato());
                colaborador.getContratoColaboradorPK().setPersonaId(contratoColaborador.getPersonaId().getPersonaId());
                colaboradorPKRepository.update(colaborador);
                respuesta.setDatos(ncoContratoColaboradorRepository.
                        findByFechaContratoAndPersonaIdPersonaIdAndCentrocClienteIdCentrocClienteId(
                                contratoColaborador.getFechaContrato(),
                                contratoColaborador.getPersonaId().getPersonaId(),
                                contratoColaborador.getCentrocClienteId().getCentrocClienteId()
                        ).orElse(new NcoContratoColaborador()));
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
                respuesta.setMensaje(Constantes.EXITO);
            }else {
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(Constantes.ERROR_OBTENER_EMPLEADO);
            }
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" modificarCompensacion " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica obtenerIdPersona(Long idPersona) throws ServiceException {
        try{
            RespuestaGenerica respuesta = new RespuestaGenerica();
            RespuestaGoogleStorage respuestaGoogle;
            NcoContratoColaborador colaborador = ncoContratoColaboradorRepository.findByPersonaIdPersonaId(idPersona.intValue());

            if (colaborador.getJefeInmediatoId() != null && colaborador.getJefeInmediatoId().getPersonaId() != null){
                colaborador.setJefeInmediatoId(
                        ncoPersonaRepository.findByPersonaId(colaborador.getJefeInmediatoId().getPersonaId()));
            }
            NcoContratoColaboradorDto contratoColaboradorDto = ObjetoMapper.map(colaborador,NcoContratoColaboradorDto.class);

            if (contratoColaboradorDto.getPersonaId().getUrlImagen() != null
                    && !contratoColaboradorDto.getPersonaId().getUrlImagen().isEmpty()){
                respuestaGoogle = googleStorageService.obtenerArchivo(contratoColaboradorDto.getPersonaId().getUrlImagen());
                contratoColaboradorDto.setUrl(respuestaGoogle.getUrl());
            }

            respuesta.setDatos(contratoColaboradorDto);
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            respuesta.setMensaje(Constantes.EXITO);
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" obtenerIdPersona " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica obtenerIdEmpresa(Long idEmpresa) throws ServiceException {
        try{
            RespuestaGenerica respuesta = new RespuestaGenerica();
            respuesta.setDatos(
                    ncoContratoColaboradorRepository.findByCentrocClienteIdCentrocClienteId(idEmpresa.intValue()));
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            respuesta.setMensaje(Constantes.EXITO);
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" obtenerIdEmpresa " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica obtenerIdGrupoNomina(Long idGrupoNomina) throws ServiceException {
        try{
            RespuestaGenerica respuesta = new RespuestaGenerica();
            respuesta.setDatos(

                    ncoContratoColaboradorRepository.findByGrupoNominaIdGrupoNominaId(idGrupoNomina.intValue()));
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            respuesta.setMensaje(Constantes.EXITO);
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" obtenerIdGrupoNomina " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica obtenerIdPersonaNative(Long idPersona) throws ServiceException {
        try{
            RespuestaGenerica respuesta = new RespuestaGenerica();
            respuesta.setDatos(
                    contratoColaboradorRepository.consultaListaEmpleado(idPersona.intValue()));
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            respuesta.setMensaje(Constantes.EXITO);
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" obtenerIdPersona " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica obtenerIdArea(Long idArea) throws ServiceException {
        try{
            RespuestaGenerica respuesta = new RespuestaGenerica();
            respuesta.setDatos(
                    ncoContratoColaboradorRepository.findByAreaIdAndEsActivo(idArea.intValue()));
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            respuesta.setMensaje(Constantes.EXITO);
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" obtenerIdEmpresa " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica obtenerListaDinamica(NcoContratoColaborador contratoColaborador) throws ServiceException {
        try{
            RespuestaGenerica respuesta = new RespuestaGenerica();
            RespuestaGoogleStorage respuestaGoogle;
            List<ContratoColaboradorConsulta> lista = new ArrayList<>();
            List<ContratoColaboradorConsulta> listaColaborador = contratoColaboradorRepository.consultaDimanica(contratoColaborador);
            for (ContratoColaboradorConsulta colaborador : listaColaborador) {
                if (colaborador.getUrlImagen() != null && !colaborador.getUrlImagen().isEmpty()){
                    respuestaGoogle = googleStorageService.obtenerArchivo(colaborador.getUrlImagen());
                    colaborador.setUrl(respuestaGoogle.getUrl());
                }
                lista.add(colaborador);
            }
            respuesta.setDatos(lista);
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            respuesta.setMensaje(Constantes.EXITO);
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" obtenerListaDinamica " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica listaEmpleadoBaja(Long id, Boolean activo) throws ServiceException {
        try{
            RespuestaGenerica respuesta = new RespuestaGenerica();
           List<NcoContratoColaborador> ncoContratoColaborador= ncoContratoColaboradorRepository
                    .findByCentrocClienteIdCentrocClienteIdAndEsActivo(id.intValue() ,activo);
            respuesta.setDatos(ncoContratoColaborador);

            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            respuesta.setMensaje(Constantes.EXITO);
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" obtenerListaDinamica " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica guardarBaja(NcoContratoColaboradorDto colaboradorBaja) throws ServiceException {
        try{
            RespuestaGenerica respuesta = validaCamposObligatoriosBaja(colaboradorBaja);
            if (respuesta.isResultado()){
                colaboradorBaja.setEsActivo(Constantes.ESTATUS_INACTIVO);
                colaboradorBaja.getPersonaId().setEsActivo(Constantes.ESTATUS_INACTIVO);
                colaboradorBaja.setEstatusBajaId(1);

                // Regla agregada para evitar la duplicidad de obtener el colaborador
                Optional<NcoContratoColaborador> colabValid = ncoContratoColaboradorRepository
                        .findByFechaContratoAndPersonaIdPersonaIdAndCentrocClienteIdCentrocClienteId(
                                colaboradorBaja.getFechaContrato(),
                                colaboradorBaja.getPersonaId().getPersonaId(),
                                colaboradorBaja.getCentrocClienteId().getCentrocClienteId());
                if (colabValid.isPresent()) {
                    // Regla para no permitir que la fecha de baja sea menor a la de antiguedad
                    if (colabValid.get().getFechaAntiguedad().after(colaboradorBaja.getUltimoDia())
                            || colabValid.get().getFechaAntiguedad().equals(colaboradorBaja.getUltimoDia())) {
                        respuesta.setDatos(null);
                        respuesta.setMensaje(Constantes.ERROR_ULTIMO_DIA2);
                        respuesta.setResultado(Constantes.RESULTADO_ERROR);
                        return respuesta;
                    }

                    // Reglas especiales, no permitir baja de un empleado con nomina en proceso
                    if (esErrorBaja(colaboradorBaja.getPersonaId().getPersonaId(),
                            colaboradorBaja.getCentrocClienteId().getCentrocClienteId())) {
                        respuesta.setDatos(null);
                        respuesta.setMensaje(Constantes.ERROR_BAJA);
                        respuesta.setResultado(Constantes.RESULTADO_ERROR);
                        return respuesta;
                    }

                    // Correcion de incidencia para no permitir baja el mismo dia de la nomina
                    if (esErrorUltimoDia(colaboradorBaja)) {
                        respuesta.setDatos(null);
                        respuesta.setMensaje(Constantes.ERROR_ULTIMO_DIA);
                        respuesta.setResultado(Constantes.RESULTADO_ERROR);
                        return respuesta;
                    }
                } else {
                    respuesta.setDatos(null);
                    respuesta.setMensaje(Constantes.ERROR_CONTRATO_NO_EXISTE);
                    respuesta.setResultado(Constantes.RESULTADO_ERROR);
                    return respuesta;
                }

                respuesta = modificarBaja(ObjetoMapper.map(colaboradorBaja,NcoContratoColaborador.class));
                if (respuesta.isResultado()){
                    colaboradorBaja.getPagosLiquidacionColaborador().forEach(liquidacionColaborador -> {
                        try {
                            pagosLiquidacionColaboradorServices.guardar(liquidacionColaborador);
                        } catch (ServiceException e) {
                            LOG.error(Constantes.ERROR, e);
                        }
                    });
                    NcoContratoColaboradorDto colaborador = ObjetoMapper.map(respuesta.getDatos(), NcoContratoColaboradorDto.class);
                    colaborador.setPagosLiquidacionColaborador(pagosLiquidacionColaboradorRepository.
                            findByFechaContratoAndPersonaIdPersonaIdAndCentrocClienteIdCentrocClienteId(colaborador.getFechaContrato(),
                                    colaborador.getPersonaId().getPersonaId(),
                                    colaborador.getCentrocClienteId().getCentrocClienteId()));
                    respuesta.setDatos(colaborador);
                }
            }
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" guardarBaja " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    // Reglas especiales, no permitir baja de un empleado con nomina en proceso
    private boolean esErrorBaja(Integer personaId, Integer clienteId) {
        return !ncrEmpleadoXnominaRepository.findAllNominasEnProceso(personaId, clienteId).isEmpty();
    }

    // Correcion de incidencia para no permitir baja el mimo dia de la nomina
    private boolean esErrorUltimoDia(NcoContratoColaboradorDto colaboradorBaja) {
        Optional<NcrNominaXperiodo> ncrNominaXperiodo = ncrEmpleadoXnominaRepository
                .findUltimaNominaProcesada(colaboradorBaja.getPersonaId().getPersonaId(), colaboradorBaja.getCentrocClienteId().getCentrocClienteId());
        if (ncrNominaXperiodo.isPresent()) {
            return ncrNominaXperiodo.get().getFechaFin().after(colaboradorBaja.getUltimoDia())
                    || ncrNominaXperiodo.get().getFechaFin().equals(colaboradorBaja.getUltimoDia());
        }
        return false;
    }

    @Override
    public RespuestaGenerica listaEmpleadoFiniquito(Integer centroClienteId) throws ServiceException {
        try{
            return new RespuestaGenerica(ncoContratoColaboradorRepository
                    .obtenColaboradoresNominaFiniquito(centroClienteId),Constantes.RESULTADO_EXITO, Constantes.EXITO);
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" listaEmpleadoFiniquito " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica listaEmpleadoAguinaldo(Integer centroClienteId) throws ServiceException {
        try{
            Integer anioLey = Utilidades.obtenAnio(new Date());

            Date fechaInicioPeriodo = Utilidades.obtenPrimerDiaAnio(anioLey);
            Date fechaFinPeriodo = Utilidades.obtenUltimoDiaAnio(anioLey);

            return new RespuestaGenerica(ncoContratoColaboradorRepository
                    .obtenColaboradoresNominaExtraordinaria(centroClienteId,fechaInicioPeriodo, fechaFinPeriodo),
                    Constantes.RESULTADO_EXITO, Constantes.EXITO);
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" listaEmpleadoAguinaldo " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica obtenerListaDinamicaPaginado(NcoContratoColaborador contratoColaborador,Integer numeroRegistros, Integer pagina) throws ServiceException {
        try{
            Map<String, Object> repuesta = new HashMap<>();
            RespuestaGoogleStorage respuestaGoogle;
            List<ContratoColaboradorConsulta> lista = new ArrayList<>();
            List<ContratoColaboradorConsulta> listaColaborador = contratoColaboradorRepository.
                    consultaDimanicaPaginado(contratoColaborador,numeroRegistros,pagina);
            List<ContratoColaboradorConsulta> listaColaboradorSum = contratoColaboradorRepository.consultaDimanica(contratoColaborador);
            for (ContratoColaboradorConsulta colaborador : listaColaborador) {
                if (colaborador.getUrlImagen() != null && !colaborador.getUrlImagen().isEmpty()){
                    respuestaGoogle = googleStorageService.obtenerArchivo(colaborador.getUrlImagen());
                    colaborador.setUrl(respuestaGoogle.getUrl());
                }
                lista.add(colaborador);
            }
            repuesta.put("lista",lista);
            repuesta.put("totalResgistros",listaColaboradorSum.size());
            return new RespuestaGenerica(repuesta,Constantes.RESULTADO_EXITO,Constantes.EXITO);
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" obtenerListaDinamicaPaginado " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private RespuestaGenerica validaCamposObligatorios(NcoContratoColaborador contratoColaborador) throws ServiceException {
        try{
            RespuestaGenerica respuesta =  new RespuestaGenerica();
            if(contratoColaborador.getAreaId() == null
                    || contratoColaborador.getAreaId().getAreaId() == null
                    || contratoColaborador.getPuestoId() == null
                    || contratoColaborador.getPuestoId().getPuestoId() == null
                    || contratoColaborador.getPoliticaId() == null
                    || contratoColaborador.getPoliticaId().getPoliticaId() == null
                    || contratoColaborador.getNumEmpleado() == null
                    || contratoColaborador.getNumEmpleado().isEmpty()
                    || contratoColaborador.getFechaAntiguedad() == null
                    || contratoColaborador.getTipoContratoId() == null
                    || contratoColaborador.getTipoContratoId().getTipoContratoId() == null
                    || contratoColaborador.getFechaContrato() == null
                    || contratoColaborador.getAreaGeograficaId() == null
                    || contratoColaborador.getAreaGeograficaId().getAreaGeograficaId() == null
                    || contratoColaborador.getGrupoNominaId() == null
                    || contratoColaborador.getGrupoNominaId().getGrupoNominaId() == null
                    || contratoColaborador.getTipoCompensacionId() == null
                    || contratoColaborador.getTipoCompensacionId().getTipoCompensacionId() == null
                    || contratoColaborador.getTipoRegimenContratacionId() == null
                    || contratoColaborador.getTipoRegimenContratacionId().getTipoRegimenContratacionId() == null
                    || contratoColaborador.getSueldoBrutoMensual() == null
                    || contratoColaborador.getSbc() == null
                    || contratoColaborador.getSalarioDiario() == null){
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(Constantes.CAMPOS_REQUERIDOS);
            }else{
                if (contratoColaborador.isEsSubcontratado()){
                    if (contratoColaborador.getSubcontratistaId() == null
                            || contratoColaborador.getSubcontratistaId().getSubcontratistaId() == null
                            || contratoColaborador.getPorcentaje() == null){
                        respuesta.setResultado(Constantes.RESULTADO_ERROR);
                        respuesta.setMensaje(Constantes.CAMPOS_REQUERIDOS);
                    }
                }else{
                    respuesta.setResultado(Constantes.RESULTADO_EXITO);
                    respuesta.setMensaje(Constantes.EXITO);
                }
            }
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" validaCamposObligatorios " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private RespuestaGenerica validaCamposObligatoriosBaja(NcoContratoColaboradorDto contratoColaborador) throws ServiceException {
        try{
            RespuestaGenerica respuesta =  new RespuestaGenerica(null, Constantes.RESULTADO_EXITO, Constantes.EXITO);
            if(contratoColaborador.getFechaContrato() == null
                    || contratoColaborador.getCentrocClienteId() == null
                    || contratoColaborador.getCentrocClienteId().getCentrocClienteId() == null
                    || contratoColaborador.getPersonaId() == null
                    || contratoColaborador.getPersonaId().getPersonaId() == null
                    || contratoColaborador.getTipoBajaId() == null
                    || contratoColaborador.getTipoBajaId().getTipoBajaId() == null
                    || contratoColaborador.getMotivoBajaId() == null
                    || contratoColaborador.getMotivoBajaId().getMotivoBajaId() == null
                    || contratoColaborador.getUltimoDia() == null
                    || contratoColaborador.getFechaParaCalculo() == null
                    || contratoColaborador.getFechaParaCalculo().isEmpty()){
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(Constantes.CAMPOS_REQUERIDOS);
            }
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" validaCamposObligatoriosBaja " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private boolean validaNumeroEmpleado(String numeroEmpleado, Integer idEmpresa) throws ServiceException {
        try{
            return ncoContratoColaboradorRepository.existsByNumEmpleadoAndCentrocClienteIdCentrocClienteIdAndEsActivo(numeroEmpleado, idEmpresa,true);
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" validaNumeroEmpleado " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private RespuestaGenerica validaEmpleado(NcoContratoColaborador colaborador){
        RespuestaGenerica respuesta = new RespuestaGenerica();
        if (colaborador.getFechaContrato() != null
                && colaborador.getCentrocClienteId() != null
                && colaborador.getCentrocClienteId().getCentrocClienteId() != null
                && colaborador.getPersonaId() != null
                && colaborador.getPersonaId().getPersonaId() != null){
            if(ncoContratoColaboradorRepository.existsByFechaContratoAndPersonaIdPersonaIdAndCentrocClienteIdCentrocClienteId(
                    colaborador.getFechaContrato(),
                    colaborador.getPersonaId().getPersonaId(),
                    colaborador.getCentrocClienteId().getCentrocClienteId())){
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
                respuesta.setMensaje(Constantes.EXITO);
            }else {
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(Constantes.ERROR_OBTENER_EMPLEADO);
            }
        }else{
            respuesta.setResultado(Constantes.RESULTADO_ERROR);
            respuesta.setMensaje(Constantes.ID_NULO);
        }
        return respuesta;
    }

    private NcoContratoColaborador reactivaEmpleado(NcoContratoColaborador colaborador){
        colaborador.setFechaFinUltimoPago(null);
        colaborador.setEsActivo(true);
        colaborador.setTipoBajaId(null);
        colaborador.setUltimoDia(null);
        colaborador.setFechaParaCalculo(null);
        colaborador.setMotivoBajaId(null);
        colaborador.setNotas(null);
        colaborador.setEstatusBajaId(null);
        return colaborador;
    }

    @Override
    public RespuestaGenerica validaReactivar(Integer centroClienteId,Integer personaId) throws ServiceException {
        try{
            if (ncrEmpleadoXnominaRepository.findByNominasEnProcesoFiniquito(personaId, centroClienteId).isEmpty()){
                return new RespuestaGenerica(null,Constantes.RESULTADO_EXITO,Constantes.EXITO);
            }else {
                return new RespuestaGenerica(null,Constantes.RESULTADO_ERROR,Constantes.ERROR_VALIDA_NOMINA_REACTIVAR);
            }
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" validaNumeroEmpleado " + Constantes.ERROR_EXCEPCION, e);
        }
    }

}
