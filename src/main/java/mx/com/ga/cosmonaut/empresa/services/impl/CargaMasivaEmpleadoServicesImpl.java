package mx.com.ga.cosmonaut.empresa.services.impl;

import mx.com.ga.cosmonaut.common.dto.CsBancoDto;
import mx.com.ga.cosmonaut.common.dto.NclCentrocClienteDto;
import mx.com.ga.cosmonaut.common.dto.NmaCuentaBancoDto;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.dto.administracion.usuarios.AdminUsuarioCS;
import mx.com.ga.cosmonaut.common.entity.administracion.usuarios.AdmUsuarios;
import mx.com.ga.cosmonaut.common.entity.catalogo.negocio.CatMetodoPago;
import mx.com.ga.cosmonaut.common.entity.catalogo.negocio.CatTipoCompensacion;
import mx.com.ga.cosmonaut.common.entity.catalogo.negocio.CatTipoPersona;
import mx.com.ga.cosmonaut.common.entity.catalogo.sat.CsTipoContrato;
import mx.com.ga.cosmonaut.common.entity.catalogo.sat.CsTipoJornada;
import mx.com.ga.cosmonaut.common.entity.catalogo.sat.CsTipoRegimenContratacion;
import mx.com.ga.cosmonaut.common.entity.catalogo.ubicacion.CatAreaGeografica;
import mx.com.ga.cosmonaut.common.entity.catalogo.ubicacion.CatEstado;
import mx.com.ga.cosmonaut.common.entity.cliente.*;
import mx.com.ga.cosmonaut.common.entity.colaborador.NcoContratoColaborador;
import mx.com.ga.cosmonaut.common.entity.colaborador.NcoHistoricoCompensacion;
import mx.com.ga.cosmonaut.common.entity.colaborador.NcoKardexColaborador;
import mx.com.ga.cosmonaut.common.entity.colaborador.NcoPersona;
import mx.com.ga.cosmonaut.common.entity.temporal.CargaMasivaEmpleado;
import mx.com.ga.cosmonaut.common.enums.CatMovimientoImss;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.common.repository.administracion.NmaCuentaBancoRepository;
import mx.com.ga.cosmonaut.common.repository.colaborador.NcoContratoColaboradorRepository;
import mx.com.ga.cosmonaut.common.repository.colaborador.NcoPersonaRepository;
import mx.com.ga.cosmonaut.common.repository.temporal.CargaMasivaRepository;
import mx.com.ga.cosmonaut.common.service.AdmUsuariosService;
import mx.com.ga.cosmonaut.common.service.HistoricoCompensacionService;
import mx.com.ga.cosmonaut.common.service.KardexColaboradorService;
import mx.com.ga.cosmonaut.common.util.*;
import mx.com.ga.cosmonaut.empresa.services.CargaMasivaEmpleadoServices;
import mx.com.ga.cosmonaut.empresa.services.ValidacionServices;
import mx.com.ga.cosmonaut.orquestador.dto.CalculoSalario;
import mx.com.ga.cosmonaut.orquestador.dto.CalculoSalarioPPP;
import mx.com.ga.cosmonaut.orquestador.dto.peticion.CalculoSalarioImssCompletoPpp;
import mx.com.ga.cosmonaut.orquestador.dto.peticion.NominaCalculo;
import mx.com.ga.cosmonaut.orquestador.service.CalculadoraService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Singleton
public class CargaMasivaEmpleadoServicesImpl implements CargaMasivaEmpleadoServices {

    private static final Logger LOG = LoggerFactory.getLogger(CargaMasivaEmpleadoServicesImpl.class);

    @Inject
    private NcoPersonaRepository ncoPersonaRepository;

    @Inject
    private CargaMasivaRepository cargaMasivaRepository;

    @Inject
    private NcoContratoColaboradorRepository ncoContratoColaboradorRepository;

    @Inject
    private ValidacionServices validacionServices;

    @Inject
    private NmaCuentaBancoServiceImpl nmaCuentaBancoService;

    @Inject
    private NmaCuentaBancoRepository nmaCuentaBancoRepository;

    @Inject
    private CalculadoraService calculadoraService;

    @Inject
    private KardexColaboradorService kardexColaboradorService;

    @Inject
    private HistoricoCompensacionService historicoCompensacionService;

    @Inject
    private AdmUsuariosService admUsuariosServices;

    private ExecutorService executorService;

    public CargaMasivaEmpleadoServicesImpl() {
        this.executorService = Executors.newFixedThreadPool(30);
    }

    @Transactional
    @Override
    public void guardarCargaMasiva(Integer centrocClienteId, Integer tipoCargaId) throws ServiceException {
        List<CargaMasivaEmpleado> lista = cargaMasivaRepository.findByCentrocClienteIdAndEsCorrecto(centrocClienteId, Constantes.RESULTADO_EXITO);
        try {
            lista.parallelStream().forEach(empleado -> {
                NcoPersona persona = ObjetoMapper.map(empleado, NcoPersona.class);
                persona.setEsActivo(Constantes.ESTATUS_ACTIVO);
                persona.setTipoPersonaId(new CatTipoPersona());
                persona.getTipoPersonaId().setTipoPersonaId(Constantes.ID_EMPLEADO.intValue());
                String genero = "";
                if (empleado.getGenero() != null && !empleado.getGenero().isEmpty()){
                    genero = empleado.getGenero().equals("H") ? "M" : "F" ;
                }
                persona.setGenero(genero);
                persona.setCentrocClienteId(new NclCentrocCliente());
                persona.getCentrocClienteId().setCentrocClienteId(centrocClienteId);
                persona = ncoPersonaRepository.save(persona);

                AdmUsuarios usuarios = new AdmUsuarios();
                if(tipoCargaId.equals(1)){
                    AdminUsuarioCS usuarioSistemaCosmonaut = new AdminUsuarioCS();
                    usuarioSistemaCosmonaut.setClienteId(centrocClienteId);
                    usuarioSistemaCosmonaut.setPersonaId(persona.getPersonaId());
                    usuarioSistemaCosmonaut.setNombre(persona.getNombre());
                    usuarioSistemaCosmonaut.setApellidoPat(persona.getApellidoPaterno());
                    usuarioSistemaCosmonaut.setApellidoMat(persona.getApellidoMaterno());
                    usuarioSistemaCosmonaut.setEmail(persona.getEmailCorporativo());
                    RespuestaGenerica respuestaAdminUsuario = this.admUsuariosServices.agregarEmpleado(usuarioSistemaCosmonaut);
                    usuarios = (AdmUsuarios) respuestaAdminUsuario.getDatos();
                }

                NmaCuentaBancoDto nmaCuentaBancoDto = new NmaCuentaBancoDto();
                if (empleado.getMetodoPagoId() == 4){
                    nmaCuentaBancoDto.setBancoId(new CsBancoDto());
                    nmaCuentaBancoDto.getBancoId().setBancoId(empleado.getBancoId().longValue());
                    nmaCuentaBancoDto.setNclCentrocCliente(new NclCentrocClienteDto());
                    nmaCuentaBancoDto.getNclCentrocCliente().setCentrocClienteId(empleado.getCentrocClienteId());
                    nmaCuentaBancoDto.setClabe(empleado.getClabe());
                    nmaCuentaBancoDto.setNumeroCuenta(empleado.getNumeroCuenta());
                    nmaCuentaBancoDto.setNcoPersona(persona);

                    try {
                        RespuestaGenerica respuestaCuentaBanco = nmaCuentaBancoService.guardar(nmaCuentaBancoDto);
                        nmaCuentaBancoDto = (NmaCuentaBancoDto) respuestaCuentaBanco.getDatos();
                    } catch (Exception e) {
                        LOG.error(Constantes.EXCEPCION,e);
                    }
                }

                if (ncoContratoColaboradorRepository
                        .existsByFechaContratoAndPersonaIdPersonaIdAndCentrocClienteIdCentrocClienteId(
                                empleado.getFechaInicio(),persona.getPersonaId(), empleado.getCentrocClienteId())){
                    empleado.setEsCorrecto(false);
                    String mensajeAnterior = empleado.getErrores() == null || empleado.getErrores().isEmpty() ? "" : empleado.getErrores() + "/n ";
                    String mensaje = mensajeAnterior + "El empleado ya existe en la base de datos";
                    empleado.setErrores(mensaje);
                    cargaMasivaRepository.update(empleado);
                    try {
                        eliminarCarga(persona, nmaCuentaBancoDto,usuarios);
                    } catch (ServiceException e) {
                        LOG.error(Constantes.EXCEPCION,e);
                    }
                }

                if (ncoContratoColaboradorRepository
                        .existsByNumEmpleadoAndCentrocClienteIdCentrocClienteId(
                                empleado.getNumeroEmpleado(), empleado.getCentrocClienteId())){
                    empleado.setEsCorrecto(false);
                    String mensajeAnterior = empleado.getErrores() == null || empleado.getErrores().isEmpty() ? "" : empleado.getErrores() + "/n ";
                    String mensaje = mensajeAnterior + Constantes.ERROR_NUMERO_EMPLEADO;
                    empleado.setErrores(mensaje);
                    cargaMasivaRepository.update(empleado);
                    try {
                        eliminarCarga(persona, nmaCuentaBancoDto,usuarios);
                    } catch (ServiceException e) {
                        LOG.error(Constantes.EXCEPCION,e);
                    }
                }

                try {
                    NcoContratoColaborador colaborador = ObjetoMapper.map(empleado, NcoContratoColaborador.class);
                    colaborador.setFechaContrato(empleado.getFechaInicio());
                    colaborador.setCentrocClienteId(new NclCentrocCliente());
                    colaborador.getCentrocClienteId().setCentrocClienteId(centrocClienteId);
                    colaborador.setPersonaId(new NcoPersona());
                    colaborador.getPersonaId().setPersonaId(persona.getPersonaId());
                    colaborador.setAreaId(new NclArea());
                    colaborador.getAreaId().setAreaId(empleado.getAreaId());
                    colaborador.setPuestoId(new NclPuesto());
                    colaborador.getPuestoId().setPuestoId(empleado.getPuestoId());
                    colaborador.setTipoContratoId(new CsTipoContrato());
                    colaborador.getTipoContratoId().setTipoContratoId(empleado.getTipoContratoId().toString());
                    colaborador.setEstadoId(new CatEstado());
                    colaborador.getEstadoId().setEstadoId(empleado.getEstadoId());
                    colaborador.setTipoRegimenContratacionId(new CsTipoRegimenContratacion());
                    colaborador.getTipoRegimenContratacionId().setTipoRegimenContratacionId(empleado.getTipoRegimenContratacionId().toString());
                    colaborador.setPoliticaId(new NclPolitica());
                    colaborador.getPoliticaId().setPoliticaId(empleado.getPoliticaId());
                    colaborador.setAreaGeograficaId(new CatAreaGeografica());
                    colaborador.getAreaGeograficaId().setAreaGeograficaId(empleado.getAreaGeograficaId().longValue());
                    colaborador.setJornadaId(new NclJornada());
                    colaborador.getJornadaId().setJornadaId(empleado.getJornadaId());
                    colaborador.setTipoJornadaId("0" + empleado.getTipoJornadaId().toString());
                    colaborador.setTipoCompensacionId(new CatTipoCompensacion());
                    colaborador.getTipoCompensacionId().setTipoCompensacionId(empleado.getTipoCompensacionId().longValue());
                    colaborador.setGrupoNominaId(new NclGrupoNomina());
                    colaborador.getGrupoNominaId().setGrupoNominaId(empleado.getGrupoNominaId());
                    colaborador.setMetodoPagoId(new CatMetodoPago());
                    colaborador.getMetodoPagoId().setMetodoPagoId(empleado.getMetodoPagoId());
                    if (tipoCargaId == 2){
                        colaborador.setEsActivo(Constantes.ESTATUS_INACTIVO);
                    }else {
                        colaborador.setEsActivo(Constantes.ESTATUS_ACTIVO);
                    }
                    colaborador.setNumEmpleado(empleado.getNumeroEmpleado());

                    if (tipoCargaId == 3){
                        CalculoSalarioImssCompletoPpp calculoSalario = new CalculoSalarioImssCompletoPpp();
                        calculoSalario.setClienteId(colaborador.getCentrocClienteId().getCentrocClienteId());
                        calculoSalario.setFechaAntiguedad(colaborador.getFechaAntiguedad());
                        calculoSalario.setFechaContrato(new Date());
                        calculoSalario.setGrupoNomina(colaborador.getGrupoNominaId().getGrupoNominaId());
                        calculoSalario.setTipoCompensacion(colaborador.getTipoCompensacionId().getTipoCompensacionId().intValue());
                        calculoSalario.setPoliticaId(colaborador.getPoliticaId().getPoliticaId());
                        calculoSalario.setPagoNeto(empleado.getSueldoBrutoMensual());
                        calculoSalario.setSdImss(empleado.getSbc());
                        RespuestaGenerica respuestacalculoSalarioPPP = calculadoraService.calculoSalarioImssCompletoPpp(calculoSalario);

                        if (respuestacalculoSalarioPPP.isResultado()){
                            CalculoSalarioPPP calculoSalarioPPP = (CalculoSalarioPPP) respuestacalculoSalarioPPP.getDatos();
                            colaborador.setSalarioDiario(BigDecimal.valueOf(empleado.getSalarioDiario()));
                            colaborador.setPppSnm(BigDecimal.valueOf(empleado.getSueldoBrutoMensual()));
                            colaborador.setPppMontoComplementario(BigDecimal.valueOf(calculoSalarioPPP.getPppMontoComplementario()));
                            colaborador.setPppSalarioBaseMensual(BigDecimal.valueOf(calculoSalarioPPP.getSueldoBrutoMensual()));
                            colaborador.setSueldoNetoMensual(BigDecimal.valueOf(calculoSalarioPPP.getSueldoNetoMensual()));
                            colaborador.setSbc(BigDecimal.valueOf(calculoSalarioPPP.getSbc()));
                            NcoContratoColaborador colaboradorGuardado = ncoContratoColaboradorRepository.save(colaborador);
                            bitacoraKardex(colaboradorGuardado);
                        }else {
                            empleado.setEsCorrecto(false);
                            String mensajeAnterior = empleado.getErrores() == null || empleado.getErrores().isEmpty() ? "" : empleado.getErrores() + "/n ";
                            String mensaje = mensajeAnterior +respuestacalculoSalarioPPP.getMensaje();
                            empleado.setErrores(mensaje);
                            cargaMasivaRepository.update(empleado);
                            try {
                                eliminarCarga(persona, nmaCuentaBancoDto,usuarios);
                            } catch (ServiceException e) {
                                LOG.error(Constantes.EXCEPCION,e);
                            }
                        }

                    }else {
                        NominaCalculo nominaCalculo = new NominaCalculo();
                        nominaCalculo.setClienteId(empleado.getCentrocClienteId());
                        nominaCalculo.setGrupoNomina(empleado.getGrupoNominaId());
                        nominaCalculo.setFechaAntiguedad(empleado.getFechaAntiguedad());
                        nominaCalculo.setTipoCompensacion(empleado.getTipoCompensacionId());
                        nominaCalculo.setFecIniPeriodo(new Date());
                        nominaCalculo.setSbmImss(empleado.getSbc());
                        nominaCalculo.setPoliticaId(empleado.getPoliticaId());
                        RespuestaGenerica respuestaCalculo = calculadoraService.calculoSalarioBrutoNeto(nominaCalculo);

                        if (respuestaCalculo.isResultado()){
                            CalculoSalario calculoSalario = (CalculoSalario) respuestaCalculo.getDatos();
                            colaborador.setSueldoNetoMensual(BigDecimal.valueOf(calculoSalario.getSalarioNetoMensual()));
                            colaborador.setSalarioDiario(BigDecimal.valueOf(empleado.getSueldoBrutoMensual()/30));
                            colaborador.setSbc(BigDecimal.valueOf(empleado.getSbc()));
                            NcoContratoColaborador colaboradorGuardado = ncoContratoColaboradorRepository.save(colaborador);
                            bitacoraKardex(colaboradorGuardado);
                        }else {
                            empleado.setEsCorrecto(false);
                            String mensajeAnterior = empleado.getErrores() == null || empleado.getErrores().isEmpty() ? "" : empleado.getErrores() + "/n ";
                            String mensaje = mensajeAnterior + respuestaCalculo.getMensaje();
                            empleado.setErrores(mensaje);
                            cargaMasivaRepository.update(empleado);
                            try {
                                eliminarCarga(persona, nmaCuentaBancoDto,usuarios);
                            } catch (ServiceException e) {
                                LOG.error(Constantes.EXCEPCION,e);
                            }
                        }

                    }

                } catch (Exception e) {
                    LOG.error(Constantes.EXCEPCION,e);
                }
            });
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" carga " + Constantes.ERROR_EXCEPCION, e);
        }

    }

    @Transactional
    @Override
    public void guardarCargaMasivaXEmpleado(CargaMasivaEmpleado empleado, Integer centrocClienteId, Integer tipoCargaId) throws ServiceException {
        try {
            NcoPersona persona = ObjetoMapper.map(empleado, NcoPersona.class);
            persona.setEsActivo(Constantes.ESTATUS_ACTIVO);
            persona.setTipoPersonaId(new CatTipoPersona());
            persona.getTipoPersonaId().setTipoPersonaId(Constantes.ID_EMPLEADO.intValue());
            String genero = "";
            if (empleado.getGenero() != null && !empleado.getGenero().isEmpty()){
                genero = empleado.getGenero().equals("H") ? "M" : "F" ;
            }
            persona.setGenero(genero);
            persona.setCentrocClienteId(new NclCentrocCliente());
            persona.getCentrocClienteId().setCentrocClienteId(centrocClienteId);
            persona = ncoPersonaRepository.save(persona);

            NmaCuentaBancoDto nmaCuentaBancoDto = new NmaCuentaBancoDto();
            if (empleado.getMetodoPagoId() == 4){
                nmaCuentaBancoDto.setBancoId(new CsBancoDto());
                nmaCuentaBancoDto.getBancoId().setBancoId(empleado.getBancoId().longValue());
                nmaCuentaBancoDto.setNclCentrocCliente(new NclCentrocClienteDto());
                nmaCuentaBancoDto.getNclCentrocCliente().setCentrocClienteId(empleado.getCentrocClienteId());
                nmaCuentaBancoDto.setClabe(empleado.getClabe());
                nmaCuentaBancoDto.setNumeroCuenta(empleado.getNumeroCuenta());
                nmaCuentaBancoDto.setNcoPersona(persona);

                try {
                    RespuestaGenerica respuestaCuentaBanco = nmaCuentaBancoService.guardar(nmaCuentaBancoDto);
                    nmaCuentaBancoDto = (NmaCuentaBancoDto) respuestaCuentaBanco.getDatos();
                } catch (Exception e) {
                    LOG.error(Constantes.EXCEPCION,e);
                }
            }

            if (ncoContratoColaboradorRepository
                    .existsByFechaContratoAndPersonaIdPersonaIdAndCentrocClienteIdCentrocClienteId(
                            empleado.getFechaInicio(),persona.getPersonaId(), empleado.getCentrocClienteId())){
                empleado.setEsCorrecto(false);
                String mensajeAnterior = empleado.getErrores() == null || empleado.getErrores().isEmpty() ? "" : empleado.getErrores() + "/n ";
                String mensaje = mensajeAnterior + "El empleado ya existe en la base de datos";
                empleado.setErrores(mensaje);
                cargaMasivaRepository.update(empleado);
                try {
                    eliminarCarga(persona, nmaCuentaBancoDto,null);
                } catch (ServiceException e) {
                    LOG.error(Constantes.EXCEPCION,e);
                }
            }

            if (ncoContratoColaboradorRepository
                    .existsByNumEmpleadoAndCentrocClienteIdCentrocClienteId(
                            empleado.getNumeroEmpleado(), empleado.getCentrocClienteId())){
                empleado.setEsCorrecto(false);
                String mensajeAnterior = empleado.getErrores() == null || empleado.getErrores().isEmpty() ? "" : empleado.getErrores() + "/n ";
                String mensaje = mensajeAnterior + Constantes.ERROR_NUMERO_EMPLEADO;
                empleado.setErrores(mensaje);
                cargaMasivaRepository.update(empleado);
                try {
                    eliminarCarga(persona, nmaCuentaBancoDto,null);
                } catch (ServiceException e) {
                    LOG.error(Constantes.EXCEPCION,e);
                }
            }

            try {
                NcoContratoColaborador colaborador = ObjetoMapper.map(empleado, NcoContratoColaborador.class);
                colaborador.setFechaContrato(empleado.getFechaInicio());
                colaborador.setCentrocClienteId(new NclCentrocCliente());
                colaborador.getCentrocClienteId().setCentrocClienteId(centrocClienteId);
                colaborador.setPersonaId(new NcoPersona());
                colaborador.getPersonaId().setPersonaId(persona.getPersonaId());
                colaborador.setAreaId(new NclArea());
                colaborador.getAreaId().setAreaId(empleado.getAreaId());
                colaborador.setPuestoId(new NclPuesto());
                colaborador.getPuestoId().setPuestoId(empleado.getPuestoId());
                colaborador.setTipoContratoId(new CsTipoContrato());
                colaborador.getTipoContratoId().setTipoContratoId(empleado.getTipoContratoId().toString());
                colaborador.setEstadoId(new CatEstado());
                colaborador.getEstadoId().setEstadoId(empleado.getEstadoId());
                colaborador.setTipoRegimenContratacionId(new CsTipoRegimenContratacion());
                colaborador.getTipoRegimenContratacionId().setTipoRegimenContratacionId(empleado.getTipoRegimenContratacionId().toString());
                colaborador.setPoliticaId(new NclPolitica());
                colaborador.getPoliticaId().setPoliticaId(empleado.getPoliticaId());
                colaborador.setAreaGeograficaId(new CatAreaGeografica());
                colaborador.getAreaGeograficaId().setAreaGeograficaId(empleado.getAreaGeograficaId().longValue());
                colaborador.setJornadaId(new NclJornada());
                colaborador.getJornadaId().setJornadaId(empleado.getJornadaId());
                colaborador.setTipoJornadaId("0" + empleado.getTipoJornadaId().toString());
                colaborador.setTipoCompensacionId(new CatTipoCompensacion());
                colaborador.getTipoCompensacionId().setTipoCompensacionId(empleado.getTipoCompensacionId().longValue());
                colaborador.setGrupoNominaId(new NclGrupoNomina());
                colaborador.getGrupoNominaId().setGrupoNominaId(empleado.getGrupoNominaId());
                colaborador.setMetodoPagoId(new CatMetodoPago());
                colaborador.getMetodoPagoId().setMetodoPagoId(empleado.getMetodoPagoId());
                if (tipoCargaId == 2){
                    colaborador.setEsActivo(Constantes.ESTATUS_INACTIVO);
                }else {
                    colaborador.setEsActivo(Constantes.ESTATUS_ACTIVO);
                }
                colaborador.setNumEmpleado(empleado.getNumeroEmpleado());

                if (tipoCargaId == 3){
                    CalculoSalarioImssCompletoPpp calculoSalario = new CalculoSalarioImssCompletoPpp();
                    calculoSalario.setClienteId(colaborador.getCentrocClienteId().getCentrocClienteId());
                    calculoSalario.setFechaAntiguedad(colaborador.getFechaAntiguedad());
                    calculoSalario.setFechaContrato(new Date());
                    calculoSalario.setGrupoNomina(colaborador.getGrupoNominaId().getGrupoNominaId());
                    calculoSalario.setTipoCompensacion(colaborador.getTipoCompensacionId().getTipoCompensacionId().intValue());
                    calculoSalario.setPoliticaId(colaborador.getPoliticaId().getPoliticaId());
                    calculoSalario.setPagoNeto(empleado.getSueldoBrutoMensual());
                    calculoSalario.setSdImss(empleado.getSbc());
                    RespuestaGenerica respuestacalculoSalarioPPP = calculadoraService.calculoSalarioImssCompletoPpp(calculoSalario);

                    if (respuestacalculoSalarioPPP.isResultado()){
                        CalculoSalarioPPP calculoSalarioPPP = (CalculoSalarioPPP) respuestacalculoSalarioPPP.getDatos();
                        colaborador.setSalarioDiario(BigDecimal.valueOf(empleado.getSalarioDiario()));
                        colaborador.setPppSnm(BigDecimal.valueOf(empleado.getSueldoBrutoMensual()));
                        colaborador.setPppMontoComplementario(BigDecimal.valueOf(calculoSalarioPPP.getPppMontoComplementario()));
                        colaborador.setPppSalarioBaseMensual(BigDecimal.valueOf(calculoSalarioPPP.getSueldoBrutoMensual()));
                        colaborador.setSueldoNetoMensual(BigDecimal.valueOf(calculoSalarioPPP.getSueldoNetoMensual()));
                        colaborador.setSbc(BigDecimal.valueOf(empleado.getSbc()));
                        NcoContratoColaborador colaboradorGuardado = ncoContratoColaboradorRepository.save(colaborador);
                        bitacoraKardex(colaboradorGuardado);
                    }else {
                        empleado.setEsCorrecto(false);
                        String mensajeAnterior = empleado.getErrores() == null || empleado.getErrores().isEmpty() ? "" : empleado.getErrores() + "/n ";
                        String mensaje = mensajeAnterior +respuestacalculoSalarioPPP.getMensaje();
                        empleado.setErrores(mensaje);
                        cargaMasivaRepository.update(empleado);
                        try {
                            eliminarCarga(persona, nmaCuentaBancoDto,null);
                        } catch (ServiceException e) {
                            LOG.error(Constantes.EXCEPCION,e);
                        }
                    }

                }else {
                    NominaCalculo nominaCalculo = new NominaCalculo();
                    nominaCalculo.setClienteId(empleado.getCentrocClienteId());
                    nominaCalculo.setGrupoNomina(empleado.getGrupoNominaId());
                    nominaCalculo.setFechaAntiguedad(empleado.getFechaAntiguedad());
                    nominaCalculo.setTipoCompensacion(empleado.getTipoCompensacionId());
                    nominaCalculo.setFecIniPeriodo(new Date());
                    nominaCalculo.setSbmImss(empleado.getSueldoBrutoMensual());
                    nominaCalculo.setPoliticaId(empleado.getPoliticaId());
                    RespuestaGenerica respuestaCalculo = calculadoraService.calculoSalarioBrutoNeto(nominaCalculo);

                    if (respuestaCalculo.isResultado()){
                        CalculoSalario calculoSalario = (CalculoSalario) respuestaCalculo.getDatos();
                        colaborador.setSueldoNetoMensual(BigDecimal.valueOf(calculoSalario.getSalarioNetoMensual()));
                        colaborador.setSalarioDiario(BigDecimal.valueOf(empleado.getSueldoBrutoMensual()/30));
                        colaborador.setSbc(BigDecimal.valueOf(empleado.getSbc()));
                        NcoContratoColaborador colaboradorGuardado = ncoContratoColaboradorRepository.save(colaborador);
                        bitacoraKardex(colaboradorGuardado);
                        /**AdmUsuarios usuarios = new AdmUsuarios();*/
                        if(tipoCargaId.equals(1)){
                            AdminUsuarioCS usuarioSistemaCosmonaut = new AdminUsuarioCS();
                            usuarioSistemaCosmonaut.setClienteId(centrocClienteId);
                            usuarioSistemaCosmonaut.setPersonaId(persona.getPersonaId());
                            usuarioSistemaCosmonaut.setNombre(persona.getNombre());
                            usuarioSistemaCosmonaut.setApellidoPat(persona.getApellidoPaterno());
                            usuarioSistemaCosmonaut.setApellidoMat(persona.getApellidoMaterno());
                            usuarioSistemaCosmonaut.setEmail(persona.getEmailCorporativo());
                            this.admUsuariosServices.agregarEmpleado(usuarioSistemaCosmonaut);
                            //RespuestaGenerica respuestaAdminUsuario =
                            //usuarios = (AdmUsuarios) respuestaAdminUsuario.getDatos();
                        }
                    }else {
                        empleado.setEsCorrecto(false);
                        String mensajeAnterior = empleado.getErrores() == null || empleado.getErrores().isEmpty() ? "" : empleado.getErrores() + "/n ";
                        String mensaje = mensajeAnterior + respuestaCalculo.getMensaje();
                        empleado.setErrores(mensaje);
                        cargaMasivaRepository.update(empleado);
                        try {
                            eliminarCarga(persona, nmaCuentaBancoDto,null);
                        } catch (ServiceException e) {
                            LOG.error(Constantes.EXCEPCION,e);
                        }
                    }

                }

            } catch (Exception e) {
                LOG.error(Constantes.EXCEPCION,e);
            }
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" carga " + Constantes.ERROR_EXCEPCION, e);
        }

    }

    private void eliminarCarga(NcoPersona persona, NmaCuentaBancoDto cuentaBanco, AdmUsuarios usuarios) throws ServiceException {
        if(persona != null && persona.getPersonaId() != null){
            ncoPersonaRepository.delete(persona);
        }

        if(cuentaBanco != null && cuentaBanco.getCuentaBancoId() != null){
            nmaCuentaBancoService.eliminar(cuentaBanco.getCuentaBancoId());
        }

        if(usuarios != null && usuarios.getUsuarioId() != null){
            this.admUsuariosServices.eliminar(usuarios);
        }
    }

    private void bitacoraKardex(NcoContratoColaborador colaborador) throws ServiceException {
        try{
            NcoKardexColaborador kardexColaborador = new NcoKardexColaborador();
            kardexColaborador.setCentrocClienteId(colaborador.getCentrocClienteId());
            kardexColaborador.setPersonaId(colaborador.getPersonaId());
            kardexColaborador.setFechaContrato(colaborador.getFechaContrato());
            kardexColaborador.setMovimientoImssId(CatMovimientoImss.GUARDAR.getId());

            NcoHistoricoCompensacion historicoCompensacion = new NcoHistoricoCompensacion();
            historicoCompensacion.setCentrocClienteId(colaborador.getCentrocClienteId());
            historicoCompensacion.setPersonaId(colaborador.getPersonaId());
            historicoCompensacion.setFechaContrato(colaborador.getFechaContrato());
            historicoCompensacion.setGrupoNominaId(colaborador.getGrupoNominaId());
            historicoCompensacion.setPoliticaId(colaborador.getPoliticaId());
            historicoCompensacion.setSalarioDiario(colaborador.getSalarioDiario());
            historicoCompensacion.setSalarioDiarioIntegrado(colaborador.getSalarioDiarioIntegrado());
            historicoCompensacion.setSalarioBaseCotizacion(colaborador.getSbc());
            historicoCompensacion.setSalarioNetoMensual(colaborador.getSueldoNetoMensual());
            historicoCompensacion.setSalarioBrutoMensual(colaborador.getSueldoBrutoMensual());
            historicoCompensacion.setSalarioBrutoImss(colaborador.getPppSalarioBaseMensual());
            historicoCompensacion.setNumeroEmpleado(colaborador.getNumEmpleado());
            historicoCompensacion.setTipoCompensacionId(colaborador.getTipoCompensacionId());
            historicoCompensacion.setTipoJornadaId(new CsTipoJornada());
            historicoCompensacion.getTipoJornadaId().setTipoJornadaId(colaborador.getTipoJornadaId());

            RespuestaGenerica respuestaHistorico = historicoCompensacionService.guardar(historicoCompensacion);
            NcoHistoricoCompensacion historicoCompensacionGuardado = (NcoHistoricoCompensacion) respuestaHistorico.getDatos();
            kardexColaborador.setHistoricoCompensacionId(historicoCompensacionGuardado);
            kardexColaborador.setEsActivo(Constantes.ESTATUS_ACTIVO);
            kardexColaboradorService.guardar(kardexColaborador);
        }catch (ServiceException e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" bitacoraKardex " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Transactional
    @Override
    public List<CargaMasivaEmpleado> carga(byte[] archivo, Integer centroClienteId, Integer tipoCargaId) throws ServiceException {
        long profiler = System.currentTimeMillis();
        LOG.info("Inicio Carga {}", System.currentTimeMillis() - profiler);
        try (InputStream fichero = new ByteArrayInputStream(archivo)) {
            XSSFWorkbook libro = new XSSFWorkbook(fichero);
            XSSFSheet hoja = libro.getSheetAt(0);
            List<CargaMasivaEmpleado> listaCargaMasivaEmpleado = new ArrayList<>();
            hoja.forEach(fila -> {
                if (fila.getRowNum() != 0 && fila.getRowNum() != 1){
                    //listaCargaMasivaEmpleado.add(validaCargaMasiva(obtenEmpleadoCargaMasiva(fila,centroClienteId,tipoCargaId),tipoCargaId));
                    listaCargaMasivaEmpleado.add(obtenEmpleadoCargaMasiva(fila,centroClienteId,tipoCargaId));
                }
            });
            List<CargaMasivaEmpleado> listaCargaMasivaEmpleadoValido = new ArrayList<>();
            listaCargaMasivaEmpleado.parallelStream().forEach(v -> {
                listaCargaMasivaEmpleadoValido.add(validaCargaMasiva(v,tipoCargaId));
            });

            LOG.info("Fin Carga {}", System.currentTimeMillis() - profiler);
            return listaCargaMasivaEmpleadoValido;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" carga " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Transactional
    @Override
    public CargaMasivaEmpleado obtenEmpleadoCargaMasiva(Row fila, Integer centroClienteId, Integer tipoCargaId){
        CargaMasivaEmpleado empleado = new CargaMasivaEmpleado();
        for (Cell celda : fila) {
            empleado.setCentrocClienteId(centroClienteId);
            //empleado = ObjetoMapper.map(cargaInformacionBasica(empleado,celda,centroClienteId),CargaMasivaEmpleado.class);
            //empleado = ObjetoMapper.map(cargaEmpleo(empleado,celda),CargaMasivaEmpleado.class);
            cargaInformacionBasica(empleado,celda,centroClienteId);
            cargaEmpleo(empleado,celda);

            if(tipoCargaId == 1){
                //empleado = ObjetoMapper.map(cargaDetalleCompensacion(empleado,celda),CargaMasivaEmpleado.class);
                //empleado = ObjetoMapper.map(cargaDatosPago(empleado,celda),CargaMasivaEmpleado.class);
                cargaDetalleCompensacion(empleado,celda);
                cargaDatosPago(empleado,celda);
                if (empleado.getMetodoPagoId() != null && empleado.getMetodoPagoId() == 4){
                    //empleado = ObjetoMapper.map(cargaTranferencia(empleado,celda),CargaMasivaEmpleado.class);
                    cargaTranferencia(empleado,celda);
                }
            }else if (tipoCargaId == 2){
                //empleado = ObjetoMapper.map(cargaDetalleCompensacionExEmpleados(empleado,celda),CargaMasivaEmpleado.class);
                //empleado = ObjetoMapper.map(cargaDatosPagoExEmpleados(empleado,celda),CargaMasivaEmpleado.class);
                cargaDetalleCompensacionExEmpleados(empleado,celda);
                cargaDatosPagoExEmpleados(empleado,celda);
                if (empleado.getMetodoPagoId() != null && empleado.getMetodoPagoId() == 4){
                    //empleado = ObjetoMapper.map(cargaTranferenciaExEmpleados(empleado,celda),CargaMasivaEmpleado.class);
                    cargaTranferenciaExEmpleados(empleado,celda);
                }
            }else {
                //empleado = ObjetoMapper.map(cargaDetalleCompensacionPPP(empleado,celda),CargaMasivaEmpleado.class);
                //empleado = ObjetoMapper.map(cargaDatosPagoPPP(empleado,celda),CargaMasivaEmpleado.class);
                cargaDetalleCompensacionPPP(empleado,celda);
                cargaDatosPagoPPP(empleado,celda);
                if (empleado.getMetodoPagoId() != null && empleado.getMetodoPagoId() == 4){
                    //empleado = ObjetoMapper.map(cargaTranferenciaPPP(empleado,celda),CargaMasivaEmpleado.class);
                    cargaTranferenciaPPP(empleado,celda);
                }
            }

            if (empleado.getErrores() == null || empleado.getErrores().isEmpty()){
                empleado.setEsCorrecto(Constantes.RESULTADO_EXITO);
            }
            //empleado.setCentrocClienteId(centroClienteId);
        }
        return empleado;
    }

    @Transactional
    @Override
    public CargaMasivaEmpleado validaCargaMasiva(CargaMasivaEmpleado empleado,Integer tipoCargaId){
        long profiler = System.currentTimeMillis();
        LOG.info("Inicio Validaciones {}", System.currentTimeMillis() - profiler);
        empleado.setErrores(validaInformacionBasica(empleado, tipoCargaId));
        empleado.setErrores(validaEmpleado(empleado));
        empleado.setErrores(validaDetalleComensacion(empleado,tipoCargaId));
        empleado.setErrores(validaDatosPago(empleado));
        if (empleado.getErrores() == null || empleado.getErrores().isEmpty()){
            empleado.setEsCorrecto(Constantes.RESULTADO_EXITO);
        }else {
            empleado.setEsCorrecto(Constantes.RESULTADO_ERROR);
        }
        LOG.info("Fin Validaciones {}", System.currentTimeMillis() - profiler);
        return empleado;
    }

    private String validaInformacionBasica(CargaMasivaEmpleado empleado,Integer tipoCargaId){
        RespuestaGenerica respuesta;
        respuesta = validacionServices.validaTextoObligatorio(empleado.getNumeroEmpleado());
        empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_NUMERO,respuesta,empleado.getErrores()));
        respuesta = validacionServices.validaTextoObligatorio(empleado.getNombre());
        empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_NOMBRE,respuesta,empleado.getErrores()));
        respuesta = validacionServices.validaCaracteresEspeciales(empleado.getNombre());
        empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_NOMBRE,respuesta,empleado.getErrores()));
        respuesta = validacionServices.validaTextoObligatorio(empleado.getApellidoPaterno());
        empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_APPELLIDO_PATERNO,respuesta,empleado.getErrores()));
        respuesta = validacionServices.validaFechaObligatorio(empleado.getFechaNacimiento());
        empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_FECHA_NACIMIENTO,respuesta,empleado.getErrores()));
        /**respuesta = validacionServices.validaFechaNacimiento(empleado.getFechaNacimiento());
        empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_FECHA_NACIMIENTO,respuesta,empleado.getErrores()));*/
        respuesta = validacionServices.validaTextoObligatorio(empleado.getGenero());
        empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_GENERO,respuesta,empleado.getErrores()));
        respuesta = validacionServices.validaTextoObligatorio(empleado.getRfc());
        empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_RFC,respuesta,empleado.getErrores()));
        respuesta = validacionServices.validaRfc(empleado.getRfc());
        empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_RFC,respuesta,empleado.getErrores()));
        respuesta = validacionServices.validaRfcDuplicado(empleado.getRfc(),empleado.getCentrocClienteId());
        empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_RFC,respuesta,empleado.getErrores()));
        respuesta = validacionServices.validaTextoObligatorio(empleado.getCurp());
        empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_CURP,respuesta,empleado.getErrores()));
        respuesta = validacionServices.validaCurp(empleado.getCurp(), empleado.getGenero());
        empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_CURP,respuesta,empleado.getErrores()));
        respuesta = validacionServices.validaCurpFechaNacimiento(empleado.getCurp(), empleado.getFechaNacimiento());
        empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_CURP,respuesta,empleado.getErrores()));



        respuesta = validacionServices.validaCurpDuplicado(empleado.getCurp(), empleado.getCentrocClienteId());
        empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_CURP,respuesta,empleado.getErrores()));
        respuesta = validacionServices.validaNumeroSeguroSocial(empleado.getNss(), empleado.getFechaNacimiento());
        empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_NSS,respuesta,empleado.getErrores()));
        respuesta = validacionServices.validaNssDuplicado(empleado.getNss(), empleado.getCentrocClienteId());
        empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_NSS,respuesta,empleado.getErrores()));
        respuesta = validacionServices.validaTextoObligatorio(empleado.getEmailCorporativo());
        empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_CORREO_EMPRESARIAL,respuesta,empleado.getErrores()));
        respuesta = validacionServices.validaCurpRfc(empleado.getCurp(),empleado.getRfc());
        empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_RFC,respuesta,empleado.getErrores()));
        respuesta = validacionServices.validaCorreoEspacion(empleado.getEmailCorporativo());
        empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_CORREO_EMPRESARIAL,respuesta,empleado.getErrores()));
        respuesta = validacionServices.validaCorreoEmpresarial(empleado.getEmailCorporativo(),empleado.getCentrocClienteId());
        empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_CORREO_EMPRESARIAL,respuesta,empleado.getErrores()));
       if(tipoCargaId.equals(1) && empleado.getEmailCorporativo() != null){
           respuesta = validacionServices.validaCorreoEmpresarialUsuarioCosmonaut(empleado.getEmailCorporativo());
           empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_CORREO_EMPRESARIAL,respuesta,empleado.getErrores()));
       }
        respuesta = validacionServices.validaCorreoEspacion(empleado.getContactoInicialEmailPersonal());
        empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_CORREO_PERSONAL,respuesta,empleado.getErrores()));
        respuesta = validacionServices.validaCorreoPersonal(empleado.getContactoInicialEmailPersonal(),empleado.getCentrocClienteId());
        empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_CORREO_PERSONAL,respuesta,empleado.getErrores()));
        respuesta = validacionServices.validaApellidoMaterno(empleado.getApellidoMaterno());
        empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_APPELLIDO_MATERNO,respuesta,empleado.getErrores()));

        //respuesta = validacionServices.validaNumeroNoRequerido(empleado.getCelular());
        //empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_CELULAR,respuesta,empleado.getErrores()));

        respuesta = validacionServices.validaCelular(empleado.getCelular());
        empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_CELULAR,respuesta,empleado.getErrores()));

        respuesta = validacionServices.validaNumeroObligatorio(empleado.getDiasVacaciones());
        empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_SALDO_VACACIONES,respuesta,empleado.getErrores()));

        return empleado.getErrores();
    }

    private String validaEmpleado(CargaMasivaEmpleado empleado){
        RespuestaGenerica respuesta;
        respuesta = validacionServices.validaNumeroObligatorio(empleado.getAreaId());
        empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_AREA,respuesta,empleado.getErrores()));
        if (respuesta.isResultado()){
            respuesta = validacionServices.validaAreaCentroCliente(empleado.getAreaId(),empleado.getCentrocClienteId());
            empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_AREA,respuesta,empleado.getErrores()));
        }
        respuesta = validacionServices.validaNumeroObligatorio(empleado.getPuestoId());
        empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_PUESTO,respuesta,empleado.getErrores()));
        if (respuesta.isResultado()){
            respuesta = validacionServices.validaPuestoCentroCliente(empleado.getPuestoId(),empleado.getCentrocClienteId());
            empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_PUESTO,respuesta,empleado.getErrores()));
        }
        respuesta = validacionServices.validaNumeroObligatorio(empleado.getTipoContratoId());
        empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_TIPO_CONTRATO,respuesta,empleado.getErrores()));
        respuesta = validacionServices.validaFechaObligatorio(empleado.getFechaInicio());
        empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_FECHA_INICIO_CONTRATO,respuesta,empleado.getErrores()));
        respuesta = validacionServices.validaNumeroObligatorio(empleado.getEstadoId());
        empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_ENTIDAD,respuesta,empleado.getErrores()));
        respuesta = validacionServices.validaNumeroObligatorio(empleado.getTipoRegimenContratacionId());
        empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_TIPO_EMPLEADO,respuesta,empleado.getErrores()));
        respuesta = validacionServices.validaNumeroObligatorio(empleado.getPoliticaId());
        empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_POLITICA,respuesta,empleado.getErrores()));
        if (respuesta.isResultado()){
            respuesta = validacionServices.validaPoliticaCentroCliente(empleado.getPoliticaId(),empleado.getCentrocClienteId());
            empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_POLITICA,respuesta,empleado.getErrores()));
        }
        respuesta = validacionServices.validaNumeroObligatorio(empleado.getAreaGeograficaId());
        empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_AREA_GEOGRAFICA,respuesta,empleado.getErrores()));
        respuesta = validacionServices.validaNumeroObligatorio(empleado.getJornadaId());
        empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_JORNADA,respuesta,empleado.getErrores()));
        if (respuesta.isResultado()){
            respuesta = validacionServices.validaJornadaCentroCliente(empleado.getJornadaId(),empleado.getCentrocClienteId());
            empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_JORNADA,respuesta,empleado.getErrores()));
        }
        respuesta = validacionServices.validaAreaPuesto(empleado.getAreaId(), empleado.getPuestoId());
        empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_AREA,respuesta,empleado.getErrores()));
        respuesta = validacionServices.validaFechaInicioContrato(empleado.getFechaInicio(), empleado.getFechaAntiguedad());
        empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_FECHA_INICIO_CONTRATO,respuesta,empleado.getErrores()));
        respuesta = validacionServices.validaBoolean(empleado.getEsSindicalizado());
        empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_SINDICALIZADO,respuesta,empleado.getErrores()));
        if (empleado.getTipoContratoId() != null && empleado.getTipoContratoId() != 1 && empleado.getTipoContratoId() != 10){
            respuesta = validacionServices.validaFechaFinObligatorio(empleado.getFechaFin());
            empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_FECHA_FIN_CONTRATO,respuesta,empleado.getErrores()));
            respuesta = validacionServices.validaFechaFinContrato(empleado.getFechaFin(), empleado.getFechaInicio());
            empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_FECHA_FIN_CONTRATO,respuesta,empleado.getErrores()));
        }
        respuesta = validacionServices.validaFechaObligatorio(empleado.getFechaAntiguedad());
        empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_FECHA_ANTIGUEDAD,respuesta,empleado.getErrores()));

        return empleado.getErrores();
    }

    private String validaDatosPago(CargaMasivaEmpleado empleado){
        RespuestaGenerica respuesta;
        respuesta = validacionServices.validaNumeroObligatorio(empleado.getMetodoPagoId());
        empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_METODO_PAGO,respuesta,empleado.getErrores()));
        if (empleado.getMetodoPagoId() != null && empleado.getMetodoPagoId().equals(Constantes.METODO_PAGOS_TRANSFERENCIA_ID)){
            respuesta = validacionServices.validaNumeroObligatorio(empleado.getBancoId());
            empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_BANCO,respuesta,empleado.getErrores()));
            respuesta = validacionServices.validaTextoObligatorio(empleado.getNumeroCuenta());
            empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_NUMERO_CUENTA,respuesta,empleado.getErrores()));
            respuesta = validacionServices.validaNumeroCuenta(empleado.getNumeroCuenta(),empleado.getBancoId(),empleado.getClabe(),empleado.getCentrocClienteId());
            empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_NUMERO_CUENTA,respuesta,empleado.getErrores()));
            respuesta = validacionServices.validaTextoObligatorio(empleado.getClabe());
            empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_CLABE,respuesta,empleado.getErrores()));
            respuesta = validacionServices.validaClabe(empleado.getNumeroCuenta(),empleado.getBancoId(),empleado.getClabe(),empleado.getCentrocClienteId());
            empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_CLABE,respuesta,empleado.getErrores()));
            respuesta = validacionServices.validarCuentaClabe(empleado.getBancoId(),empleado.getClabe());
            empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_CLABE,respuesta,empleado.getErrores()));
        }
        return empleado.getErrores();
    }

    private String validaDetalleComensacion(CargaMasivaEmpleado empleado, Integer tipoCargaId){
        RespuestaGenerica respuesta;
        respuesta = validacionServices.validaNumeroObligatorio(empleado.getTipoCompensacionId());
        empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_TIPO_COMPENSACION,respuesta,empleado.getErrores()));
        respuesta = validacionServices.validaMontoObligatorio(empleado.getSueldoBrutoMensual());
        empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_SALARIO_BRUTO,respuesta,empleado.getErrores()));
        respuesta = validacionServices.validaMontoMayorCero(empleado.getSueldoBrutoMensual());
        empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_SALARIO_BRUTO,respuesta,empleado.getErrores()));
        if (tipoCargaId == 1){
            respuesta = validacionServices.validaMontoObligatorio(empleado.getSbc());
            empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_SALARIO_BASE_COTIZACION,respuesta,empleado.getErrores()));
            respuesta = validacionServices.validaMontoMayorCero(empleado.getSbc());
            empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_SALARIO_BASE_COTIZACION,respuesta,empleado.getErrores()));
        }else if (tipoCargaId == 2){
            respuesta = validacionServices.validaFechaBaja(empleado.getFechaBaja(),empleado.getFechaInicio());
            empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_FECHA_BAJA,respuesta,empleado.getErrores()));
            respuesta = validacionServices.validaFechaObligatorio(empleado.getFechaBaja());
            empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_FECHA_BAJA,respuesta,empleado.getErrores()));
            respuesta = validacionServices.validaMontoObligatorio(empleado.getSbc());
            empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_SALARIO_BASE_COTIZACION,respuesta,empleado.getErrores()));
            respuesta = validacionServices.validaMontoMayorCero(empleado.getSbc());
            empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_SALARIO_BASE_COTIZACION,respuesta,empleado.getErrores()));
        }else {
            respuesta = validacionServices.validaMontoObligatorio(empleado.getSalarioDiario());
            empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_SALARIO_DIARIO,respuesta,empleado.getErrores()));
            respuesta = validacionServices.validaMontoMayorCero(empleado.getSalarioDiario());
            empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_SALARIO_DIARIO,respuesta,empleado.getErrores()));
        }
        respuesta = validacionServices.validaNumeroObligatorio(empleado.getGrupoNominaId());
        empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_GRUPO_NOMINA,respuesta,empleado.getErrores()));
        if (respuesta.isResultado()){
            respuesta = validacionServices.validaGrupoNominaCentroCliente(empleado.getGrupoNominaId(),empleado.getCentrocClienteId());
            empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_GRUPO_NOMINA,respuesta,empleado.getErrores()));
        }
        return empleado.getErrores();
    }

    private CargaMasivaEmpleado cargaInformacionBasica(CargaMasivaEmpleado empleado, Cell celda, Integer centroClienteId){
        RespuestaGenerica respuesta;
        switch (celda.getColumnIndex()) {
            case ConstantesReportes.CARGA_MASIVA_NUMERO_ID:
                respuesta = validacionServices.validaNumeroEmpleado(UtilidadesReportes.tipoCelda(celda),centroClienteId);
                empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_NUMERO,respuesta,empleado.getErrores()));
                empleado.setNumeroEmpleado((String) respuesta.getDatos());
                break;
            case ConstantesReportes.CARGA_MASIVA_NOMBRE_ID:
                respuesta = validacionServices.validaTextoObligatorio(UtilidadesReportes.tipoCelda(celda));
                //empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_NOMBRE,respuesta,empleado.getErrores()));
                empleado.setNombre((String) respuesta.getDatos());
                break;
            case ConstantesReportes.CARGA_MASIVA_APPELLIDO_PATERNO_ID:
                respuesta = validacionServices.validaTextoObligatorio(UtilidadesReportes.tipoCelda(celda));
                //empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_APPELLIDO_PATERNO,respuesta,empleado.getErrores()));
                empleado.setApellidoPaterno((String) respuesta.getDatos());
                break;
            case ConstantesReportes.CARGA_MASIVA_APPELLIDO_MATERNO_ID:
                respuesta = validacionServices.validaTextoNoRequerido(UtilidadesReportes.tipoCelda(celda));
                empleado.setApellidoMaterno((String) respuesta.getDatos());
                break;
            case ConstantesReportes.CARGA_MASIVA_FECHA_NACIMIENTO_ID:
                respuesta = validacionServices.validaFechaObligatorio(UtilidadesReportes.tipoCelda(celda));
                //empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_FECHA_NACIMIENTO,respuesta,empleado.getErrores()));
                empleado.setFechaNacimiento((Date) respuesta.getDatos());
                break;
            case ConstantesReportes.CARGA_MASIVA_GENERO_ID:
                respuesta = validacionServices.obtenerComboObligatorioTexto(UtilidadesReportes.tipoCelda(celda));
                empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_GENERO,respuesta,empleado.getErrores()));
                empleado.setGenero((String) respuesta.getDatos());
                break;
            case ConstantesReportes.CARGA_MASIVA_RFC_ID:
                respuesta = validacionServices.validaTextoObligatorio(UtilidadesReportes.tipoCelda(celda));
                //empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_RFC,respuesta,empleado.getErrores()));
                empleado.setRfc((String) respuesta.getDatos());
                break;
            case ConstantesReportes.CARGA_MASIVA_CURP_ID:
                respuesta = validacionServices.validaTextoObligatorio(UtilidadesReportes.tipoCelda(celda));
                //empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_CURP,respuesta,empleado.getErrores()));
                empleado.setCurp((String) respuesta.getDatos());
                break;
            case ConstantesReportes.CARGA_MASIVA_NSS_ID:
                respuesta = validacionServices.validaTextoObligatorio(UtilidadesReportes.tipoCelda(celda));
                //empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_NSS,respuesta,empleado.getErrores()));
                empleado.setNss((String) respuesta.getDatos());
                break;
            case ConstantesReportes.CARGA_MASIVA_CORREO_PERSONAL_ID:
                respuesta = validacionServices.validaTextoNoRequerido(UtilidadesReportes.tipoCelda(celda));
                //empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_CORREO_PERSONAL,respuesta,empleado.getErrores()));
                empleado.setContactoInicialEmailPersonal((String) respuesta.getDatos());
                break;
            case ConstantesReportes.CARGA_MASIVA_CORREO_EMPRESARIAL_ID:
                respuesta = validacionServices.validaTextoObligatorio(UtilidadesReportes.tipoCelda(celda));
                //empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_CORREO_EMPRESARIAL,respuesta,empleado.getErrores()));
                empleado.setEmailCorporativo((String) respuesta.getDatos());
                break;
            case ConstantesReportes.CARGA_MASIVA_CELULAR_ID:
                respuesta = validacionServices.validaNumeroNoRequerido(UtilidadesReportes.tipoCelda(celda));
                //empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_CELULAR,respuesta,empleado.getErrores()));
                empleado.setCelular( respuesta.getDatos() != null ? BigInteger.valueOf ((Long) respuesta.getDatos()) : null );
                break;
            default:
                break;
        }
        return empleado;
    }

    private CargaMasivaEmpleado cargaEmpleo(CargaMasivaEmpleado empleado, Cell celda){
        RespuestaGenerica respuesta;
        switch (celda.getColumnIndex()) {
            case ConstantesReportes.CARGA_MASIVA_FECHA_ANTIGUEDAD_ID:
                respuesta = validacionServices.validaFechaObligatorio(UtilidadesReportes.tipoCelda(celda));
                //empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_FECHA_ANTIGUEDAD,respuesta,empleado.getErrores()));
                empleado.setFechaAntiguedad((Date) respuesta.getDatos());
                break;
            case ConstantesReportes.CARGA_MASIVA_AREA_ID:
                respuesta = validacionServices.validaComboObligatorio(UtilidadesReportes.tipoCelda(celda));
                empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_AREA,respuesta,empleado.getErrores()));
                empleado.setAreaId((Integer) respuesta.getDatos());
                break;
            case ConstantesReportes.CARGA_MASIVA_PUESTO_ID:
                respuesta = validacionServices.validaComboObligatorio(UtilidadesReportes.tipoCelda(celda));
                empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_PUESTO,respuesta,empleado.getErrores()));
                empleado.setPuestoId((Integer) respuesta.getDatos());
                break;
            case ConstantesReportes.CARGA_MASIVA_FECHA_INICIO_CONTRATO_ID:
                respuesta = validacionServices.validaFechaObligatorio(UtilidadesReportes.tipoCelda(celda));
                //empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_FECHA_INICIO_CONTRATO,respuesta,empleado.getErrores()));
                empleado.setFechaInicio((Date) respuesta.getDatos());
                break;
            case ConstantesReportes.CARGA_MASIVA_FECHA_FIN_CONTRATO_ID:
                respuesta = validacionServices.validaFechaNoRequerido(UtilidadesReportes.tipoCelda(celda));
                empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_FECHA_FIN_CONTRATO,respuesta,empleado.getErrores()));
                empleado.setFechaFin((Date) respuesta.getDatos());
                break;
            case ConstantesReportes.CARGA_MASIVA_ENTIDAD_ID:
                respuesta = validacionServices.validaComboObligatorio(UtilidadesReportes.tipoCelda(celda));
                empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_ENTIDAD,respuesta,empleado.getErrores()));
                empleado.setEstadoId((Integer) respuesta.getDatos());
                break;
            case ConstantesReportes.CARGA_MASIVA_TIPO_EMPLEADO_ID:
                respuesta = validacionServices.validaComboObligatorio(UtilidadesReportes.tipoCelda(celda));
                empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_TIPO_EMPLEADO,respuesta,empleado.getErrores()));
                empleado.setTipoRegimenContratacionId( ((Integer) respuesta.getDatos()));
                break;
            case ConstantesReportes.CARGA_MASIVA_TIPO_CONTRATO_ID:
                respuesta = validacionServices.validaComboObligatorio(UtilidadesReportes.tipoCelda(celda));
                empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_TIPO_CONTRATO,respuesta,empleado.getErrores()));
                empleado.setTipoContratoId(((Integer) respuesta.getDatos()));
                break;
            case ConstantesReportes.CARGA_MASIVA_POLITICA_ID:
                respuesta = validacionServices.validaComboObligatorio(UtilidadesReportes.tipoCelda(celda));
                empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_POLITICA,respuesta,empleado.getErrores()));
                empleado.setPoliticaId((Integer) respuesta.getDatos());
                break;
            case ConstantesReportes.CARGA_MASIVA_SINDICALIZADO_ID:
                Object sindicalizado = UtilidadesReportes.tipoCelda(celda);
                respuesta = validacionServices.validaSindicalizado(sindicalizado);
                empleado.setEsSindicalizado((Boolean) respuesta.getDatos());
                break;
            case ConstantesReportes.CARGA_MASIVA_AREA_GEOGRAFICA_ID:
                respuesta = validacionServices.validaComboObligatorio(UtilidadesReportes.tipoCelda(celda));
                empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_AREA_GEOGRAFICA,respuesta,empleado.getErrores()));
                empleado.setAreaGeograficaId( ((Integer) respuesta.getDatos()));
                break;
            case ConstantesReportes.CARGA_MASIVA_JORNADA_ID:
                respuesta = validacionServices.obtenerComboJornada(UtilidadesReportes.tipoCelda(celda));
                //empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_JORNADA,respuesta,empleado.getErrores()));
                if (respuesta.isResultado()){
                    String combojornada = (String) respuesta.getDatos();
                    int inicio = combojornada.indexOf('-');
                    if (inicio < 0){
                        respuesta.setResultado(Constantes.RESULTADO_ERROR);
                        respuesta.setMensaje(Constantes.ERROR_CAMPO_SELECCION);
                        respuesta.setDatos(null);
                        empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_JORNADA,respuesta,empleado.getErrores()));
                    }else {
                        String jornadaId = combojornada.substring(0,inicio);
                        empleado.setJornadaId(Integer.parseInt(jornadaId));
                        String comboTipoJornada = combojornada.substring(inicio + 1, combojornada.length());
                        int inicioTipoJornada = comboTipoJornada.indexOf('-');
                        if (inicioTipoJornada < 0){
                            empleado.setTipoJornadaId(Integer.parseInt(comboTipoJornada));
                        }else{
                            comboTipoJornada = comboTipoJornada.substring(0 , inicioTipoJornada);
                            empleado.setTipoJornadaId(Integer.parseInt(comboTipoJornada));
                        }
                    }
                }
                break;
            default:
                break;
        }
        return empleado;
    }

    private CargaMasivaEmpleado cargaDetalleCompensacion(CargaMasivaEmpleado empleado, Cell celda){
        RespuestaGenerica respuesta;
        switch (celda.getColumnIndex()) {
            case ConstantesReportes.CARGA_MASIVA_TIPO_COMPENSACION_ID:
                respuesta = validacionServices.validaComboObligatorio(UtilidadesReportes.tipoCelda(celda));
                empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_TIPO_COMPENSACION,respuesta,empleado.getErrores()));
                empleado.setTipoCompensacionId(((Integer) respuesta.getDatos()));
                break;
            case ConstantesReportes.CARGA_MASIVA_SALARIO_BRUTO_ID:
                respuesta = validacionServices.validaMontoObligatorio(UtilidadesReportes.tipoCelda(celda));
                //empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_SALARIO_BRUTO,respuesta,empleado.getErrores()));
                empleado.setSueldoBrutoMensual((Double) respuesta.getDatos());
                break;
            case ConstantesReportes.CARGA_MASIVA_SALARIO_BASE_COTIZACION_ID:
                respuesta = validacionServices.validaMontoObligatorio(UtilidadesReportes.tipoCelda(celda));
                //empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_SALARIO_BASE_COTIZACION,respuesta,empleado.getErrores()));
                empleado.setSbc((Double) respuesta.getDatos());
                break;
            case ConstantesReportes.CARGA_MASIVA_GRUPO_NOMINA_ID:
                respuesta = validacionServices.validaComboObligatorio(UtilidadesReportes.tipoCelda(celda));
                empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_GRUPO_NOMINA,respuesta,empleado.getErrores()));
                empleado.setGrupoNominaId((Integer) respuesta.getDatos());
                break;
            default:
                break;
        }
        return empleado;
    }

    private CargaMasivaEmpleado cargaDetalleCompensacionExEmpleados(CargaMasivaEmpleado empleado, Cell celda){
        RespuestaGenerica respuesta;
        switch (celda.getColumnIndex()) {
            case ConstantesReportes.CARGA_MASIVA_EXEMPLEADOS_TIPO_COMPENSACION_ID:
                respuesta = validacionServices.validaComboObligatorio(UtilidadesReportes.tipoCelda(celda));
                empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_TIPO_COMPENSACION,respuesta,empleado.getErrores()));
                empleado.setTipoCompensacionId(((Integer) respuesta.getDatos()));
                break;
            case ConstantesReportes.CARGA_MASIVA_EXEMPLEADOS_SUELDO_NETO_ID:
                respuesta = validacionServices.validaMontoObligatorio(UtilidadesReportes.tipoCelda(celda));
                //empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_SALARIO_BRUTO,respuesta,empleado.getErrores()));
                empleado.setSueldoBrutoMensual((Double) respuesta.getDatos());
                break;
            case ConstantesReportes.CARGA_MASIVA_EXEMPLEADOS_SALARIO_DIARIO_ID:
                respuesta = validacionServices.validaMontoObligatorio(UtilidadesReportes.tipoCelda(celda));
                empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_SALARIO_BASE_COTIZACION,respuesta,empleado.getErrores()));
                empleado.setSbc((Double) respuesta.getDatos());
                break;
            case ConstantesReportes.CARGA_MASIVA_EXEMPLEADOS_GRUPO_NOMINA_ID:
                respuesta = validacionServices.validaComboObligatorio(UtilidadesReportes.tipoCelda(celda));
                empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_GRUPO_NOMINA,respuesta,empleado.getErrores()));
                empleado.setGrupoNominaId((Integer) respuesta.getDatos());
                break;
            default:
                break;
        }
        return empleado;
    }


    private CargaMasivaEmpleado cargaDetalleCompensacionPPP(CargaMasivaEmpleado empleado, Cell celda){
        RespuestaGenerica respuesta;
        switch (celda.getColumnIndex()) {
            case ConstantesReportes.CARGA_MASIVA_PPP_TIPO_COMPENSACION_ID:
                respuesta = validacionServices.validaComboObligatorio(UtilidadesReportes.tipoCelda(celda));
                empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_TIPO_COMPENSACION,respuesta,empleado.getErrores()));
                empleado.setTipoCompensacionId(((Integer) respuesta.getDatos()));
                break;
            case ConstantesReportes.CARGA_MASIVA_PPP_SUELDO_NETO_ID:
                respuesta = validacionServices.validaMontoObligatorio(UtilidadesReportes.tipoCelda(celda));
                //empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_SALARIO_BRUTO,respuesta,empleado.getErrores()));
                empleado.setSueldoBrutoMensual((Double) respuesta.getDatos());
                break;
            case ConstantesReportes.CARGA_MASIVA_PPP_SBC_ID:
                respuesta = validacionServices.validaMontoObligatorio(UtilidadesReportes.tipoCelda(celda));
                //empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_SALARIO_DIARIO,respuesta,empleado.getErrores()));
                empleado.setSbc((Double) respuesta.getDatos());
                break;
            case ConstantesReportes.CARGA_MASIVA_PPP_SALARIO_DIARIO_ID:
                respuesta = validacionServices.validaMontoObligatorio(UtilidadesReportes.tipoCelda(celda));
                //empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_SALARIO_DIARIO,respuesta,empleado.getErrores()));
                empleado.setSalarioDiario((Double) respuesta.getDatos());
                break;
            case ConstantesReportes.CARGA_MASIVA_PPP_GRUPO_NOMINA_ID:
                respuesta = validacionServices.validaComboObligatorio(UtilidadesReportes.tipoCelda(celda));
                empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_GRUPO_NOMINA,respuesta,empleado.getErrores()));
                empleado.setGrupoNominaId((Integer) respuesta.getDatos());
                break;
            default:
                break;
        }
        return empleado;
    }

    private CargaMasivaEmpleado cargaDatosPago(CargaMasivaEmpleado empleado, Cell celda){
        RespuestaGenerica respuesta;
        switch (celda.getColumnIndex()) {
            case ConstantesReportes.CARGA_MASIVA_METODO_PAGO_ID:
                respuesta = validacionServices.validaComboObligatorio(UtilidadesReportes.tipoCelda(celda));
                empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_METODO_PAGO,respuesta,empleado.getErrores()));
                empleado.setMetodoPagoId((Integer) respuesta.getDatos());
                break;
            case ConstantesReportes.CARGA_MASIVA_NUEMRO_CLIENTE_ID:
                    respuesta = validacionServices.validaTextoNoRequerido(UtilidadesReportes.tipoCelda(celda));
                    empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_NUEMRO_CLIENTE,respuesta,empleado.getErrores()));
                    empleado.setNumeroInformacion((String) respuesta.getDatos());
                break;
            case ConstantesReportes.CARGA_MASIVA_SALDO_VACACIONES_ID:
                respuesta = validacionServices.validaNumeroObligatorio(UtilidadesReportes.tipoCelda(celda));
                //empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_SALDO_VACACIONES,respuesta,empleado.getErrores()));
                empleado.setDiasVacaciones(respuesta.getDatos() != null ? ((Long) respuesta.getDatos()).intValue() : null);
                break;
            default:
                break;
        }
        return empleado;
    }

    private CargaMasivaEmpleado cargaDatosPagoExEmpleados(CargaMasivaEmpleado empleado, Cell celda){
        RespuestaGenerica respuesta;
        switch (celda.getColumnIndex()) {
            case ConstantesReportes.CARGA_MASIVA_EXEMPLEADOS_METODO_PAGO_ID:
                respuesta = validacionServices.validaComboObligatorio(UtilidadesReportes.tipoCelda(celda));
                empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_METODO_PAGO,respuesta,empleado.getErrores()));
                empleado.setMetodoPagoId((Integer) respuesta.getDatos());
                break;
            case ConstantesReportes.CARGA_MASIVA_EXEMPLEADOS_NUEMRO_CLIENTE_ID:
                respuesta = validacionServices.validaTextoNoRequerido(UtilidadesReportes.tipoCelda(celda));
                empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_NUEMRO_CLIENTE,respuesta,empleado.getErrores()));
                empleado.setNumeroInformacion((String) respuesta.getDatos());
                break;
            case ConstantesReportes.CARGA_MASIVA_EXEMPLEADOS_SALDO_VACACIONES_ID:
                respuesta = validacionServices.validaNumeroObligatorio(UtilidadesReportes.tipoCelda(celda));
                //empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_SALDO_VACACIONES,respuesta,empleado.getErrores()));
                empleado.setDiasVacaciones(respuesta.getDatos() != null ? ((Long) respuesta.getDatos()).intValue() : null);
                break;
            case ConstantesReportes.CARGA_MASIVA_FECHA_BAJA_ID:
                respuesta = validacionServices.validaFechaNoRequerido(UtilidadesReportes.tipoCelda(celda));
                empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_FECHA_BAJA,respuesta,empleado.getErrores()));
                empleado.setFechaBaja((Date) respuesta.getDatos());
                break;
            default:
                break;
        }
        return empleado;
    }

    private CargaMasivaEmpleado cargaDatosPagoPPP(CargaMasivaEmpleado empleado, Cell celda){
        RespuestaGenerica respuesta;
        switch (celda.getColumnIndex()) {
            case ConstantesReportes.CARGA_MASIVA_PPP_METODO_PAGO_ID:
                respuesta = validacionServices.validaComboObligatorio(UtilidadesReportes.tipoCelda(celda));
                empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_METODO_PAGO,respuesta,empleado.getErrores()));
                empleado.setMetodoPagoId((Integer) respuesta.getDatos());
                break;
            case ConstantesReportes.CARGA_MASIVA_PPP_NUEMRO_CLIENTE_ID:
                respuesta = validacionServices.validaTextoNoRequerido(UtilidadesReportes.tipoCelda(celda));
                empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_NUEMRO_CLIENTE,respuesta,empleado.getErrores()));
                empleado.setNumeroInformacion((String) respuesta.getDatos());
                break;
            case ConstantesReportes.CARGA_MASIVA_PPP_SALDO_VACACIONES_ID:
                respuesta = validacionServices.validaNumeroObligatorio(UtilidadesReportes.tipoCelda(celda));
                //empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_SALDO_VACACIONES,respuesta,empleado.getErrores()));
                empleado.setDiasVacaciones(respuesta.getDatos() != null ? ((Long) respuesta.getDatos()).intValue() : null);
                break;
            default:
                break;
        }
        return empleado;
    }

    private CargaMasivaEmpleado cargaTranferencia(CargaMasivaEmpleado empleado, Cell celda){
        RespuestaGenerica respuesta;
        switch (celda.getColumnIndex()) {
            case ConstantesReportes.CARGA_MASIVA_BANCO_ID:
                respuesta = validacionServices.validaComboNoRequerido(UtilidadesReportes.tipoCelda(celda));
                empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_BANCO,respuesta,empleado.getErrores()));
                empleado.setBancoId(respuesta.getDatos() != null ? ((Integer) respuesta.getDatos()) : null);
                break;
            case ConstantesReportes.CARGA_MASIVA_NUMERO_CUENTA_ID:
                respuesta = validacionServices.validaTextoObligatorio(UtilidadesReportes.tipoCelda(celda));
                //empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_NUMERO_CUENTA,respuesta,empleado.getErrores()));
                empleado.setNumeroCuenta((String) respuesta.getDatos());
                break;
            case ConstantesReportes.CARGA_MASIVA_CLABE_ID:
                respuesta = validacionServices.validaTextoObligatorio(UtilidadesReportes.tipoCelda(celda));
                //empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_CLABE,respuesta,empleado.getErrores()));
                empleado.setClabe((String) respuesta.getDatos());
                break;
            default:
                break;
        }
        return empleado;
    }

    private CargaMasivaEmpleado cargaTranferenciaExEmpleados(CargaMasivaEmpleado empleado, Cell celda){
        RespuestaGenerica respuesta;
        switch (celda.getColumnIndex()) {
            case ConstantesReportes.CARGA_MASIVA_EXEMPLEADOS_BANCO_ID:
                respuesta = validacionServices.validaComboNoRequerido(UtilidadesReportes.tipoCelda(celda));
                empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_BANCO,respuesta,empleado.getErrores()));
                empleado.setBancoId(respuesta.getDatos() != null ? ((Integer) respuesta.getDatos()) : null);
                break;
            case ConstantesReportes.CARGA_MASIVA_EXEMPLEADOS_NUMERO_CUENTA_ID:
                respuesta = validacionServices.validaTextoObligatorio(UtilidadesReportes.tipoCelda(celda));
                //empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_NUMERO_CUENTA,respuesta,empleado.getErrores()));
                empleado.setNumeroCuenta((String) respuesta.getDatos());
                break;
            case ConstantesReportes.CARGA_MASIVA_EXEMPLEADOS_CLABE_ID:
                respuesta = validacionServices.validaTextoObligatorio(UtilidadesReportes.tipoCelda(celda));
                //empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_CLABE,respuesta,empleado.getErrores()));
                empleado.setClabe((String) respuesta.getDatos());
                break;
            default:
                break;
        }
        return empleado;
    }

    private CargaMasivaEmpleado cargaTranferenciaPPP(CargaMasivaEmpleado empleado, Cell celda){
        RespuestaGenerica respuesta;
        switch (celda.getColumnIndex()) {
            case ConstantesReportes.CARGA_MASIVA_PPP_BANCO_ID:
                respuesta = validacionServices.validaComboNoRequerido(UtilidadesReportes.tipoCelda(celda));
                empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_BANCO,respuesta,empleado.getErrores()));
                empleado.setBancoId(respuesta.getDatos() != null ? ((Integer) respuesta.getDatos()) : null);
                break;
            case ConstantesReportes.CARGA_MASIVA_PPP_NUMERO_CUENTA_ID:
                respuesta = validacionServices.validaTextoObligatorio(UtilidadesReportes.tipoCelda(celda));
                //empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_NUMERO_CUENTA,respuesta,empleado.getErrores()));
                empleado.setNumeroCuenta((String) respuesta.getDatos());
                break;
            case ConstantesReportes.CARGA_MASIVA_PPP_CLABE_ID:
                respuesta = validacionServices.validaTextoObligatorio(UtilidadesReportes.tipoCelda(celda));
                //empleado.setErrores(UtilidadesReportes.generaMensajeError(ConstantesReportes.CARGA_MASIVA_CLABE,respuesta,empleado.getErrores()));
                empleado.setClabe((String) respuesta.getDatos());
                break;
            default:
                break;
        }
        return empleado;
    }

}
