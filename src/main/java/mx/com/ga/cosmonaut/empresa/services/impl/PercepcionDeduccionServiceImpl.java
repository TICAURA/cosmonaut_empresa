package mx.com.ga.cosmonaut.empresa.services.impl;

import java.util.*;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import mx.com.ga.cosmonaut.common.dto.DocumentosEmpleadoDto;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.dto.administracion.DeduccionEmpleadoArchivosDto;
import mx.com.ga.cosmonaut.common.entity.DocumentosEmpleado;
import mx.com.ga.cosmonaut.common.entity.administracion.*;
import mx.com.ga.cosmonaut.common.entity.catalogo.sat.CsTipoDeduccion;
import mx.com.ga.cosmonaut.common.entity.catalogo.sat.CsTipoPercepcion;
import mx.com.ga.cosmonaut.common.entity.cliente.NclCentrocCliente;
import mx.com.ga.cosmonaut.common.entity.cliente.NclPolitica;
import mx.com.ga.cosmonaut.common.entity.colaborador.NcoContratoColaborador;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.common.repository.administracion.*;
import mx.com.ga.cosmonaut.common.repository.calculo.NcrDeduccionXnominaRepository;
import mx.com.ga.cosmonaut.common.repository.calculo.NcrPercepcionXnominaRepository;
import mx.com.ga.cosmonaut.common.repository.catalogo.sat.CsTipoDeduccionRepository;
import mx.com.ga.cosmonaut.common.repository.catalogo.sat.CsTipoPercepcionRepository;
import mx.com.ga.cosmonaut.common.repository.cliente.NclPoliticaRepository;
import mx.com.ga.cosmonaut.common.repository.colaborador.NcoContratoColaboradorRepository;
import mx.com.ga.cosmonaut.common.service.DocumentosEmpleadoService;
import mx.com.ga.cosmonaut.common.util.Constantes;
import mx.com.ga.cosmonaut.common.util.ObjetoMapper;
import mx.com.ga.cosmonaut.common.util.Utilidades;
import mx.com.ga.cosmonaut.empresa.services.PercepcionDeduccionService;
import mx.com.ga.cosmonaut.orquestador.repository.DeduccionesRepository;

@Singleton
public class PercepcionDeduccionServiceImpl implements PercepcionDeduccionService {

    @Inject
    private NmmConceptoPercepcionRepository nmmConceptoPercepcionRepository;

    @Inject
    private NmmConfiguraPercepcionRepository nmmConfiguraPercepcionRepository;

    @Inject
    private NmmConfiguraDeduccionRepository nmmConfiguraDeduccionRepository;

    @Inject
    private NmmConceptoDeduccionRepository nmmConceptoDeduccionRepository;

    @Inject
    private CsTipoPercepcionRepository csTipoPercepcionRepository;

    @Inject
    private CsTipoDeduccionRepository csTipoDeduccionRepository;

    @Inject
    private NcrPercepcionXnominaRepository ncrPercepcionXnominaRepository;

    @Inject
    private NcrDeduccionXnominaRepository ncrDeduccionXnominaRepository;
    @Inject
    private NcoContratoColaboradorRepository ncoContratoColaboradorRepository;

    @Inject
    private NclPoliticaRepository nclPoliticaRepository;

    @Inject
    private DocumentosEmpleadoService documentosEmpleadoService;

    @Inject
    private NmmConfiguraDeduccionXdocumentoRepository nmmConfiguraDeduccionXdocumentoRepository;

    @Inject
    private DeduccionesRepository deduccionesRepository;

    public RespuestaGenerica guardarPercepcion(NmmConceptoPercepcion nmmConceptoPercepcion) throws ServiceException {
        RespuestaGenerica respuesta;
        try {
            // CONFIRMACION DE REGLAS POSTERIORES
            respuesta = validarCamposObligatoriosPercepcion(nmmConceptoPercepcion);
            if (respuesta.isResultado()) {
                respuesta = validarEstructuraPercepcion(nmmConceptoPercepcion);
                if (respuesta.isResultado()) {
                    Optional<CsTipoPercepcion> tipoPercepcion = csTipoPercepcionRepository
                            .findByTipoPercepcionIdAndEspecializacion(
                                    nmmConceptoPercepcion.getTipoPercepcionId().getTipoPercepcionId(),
                                    nmmConceptoPercepcion.getTipoPercepcionId().getEspecializacion());
                    if (tipoPercepcion.isPresent()) {
                        // REGLAS DE HERENCIA DE CAMPOS CON RESPECTO A CsTipoPercepcion
                        seteaCamposPadre(nmmConceptoPercepcion, tipoPercepcion.get());

                        nmmConceptoPercepcion.setEsActivo(Constantes.ESTATUS_ACTIVO);
                        respuesta.setDatos(nmmConceptoPercepcionRepository.save(nmmConceptoPercepcion));
                        respuesta.setMensaje(Constantes.EXITO);
                        respuesta.setResultado(Constantes.RESULTADO_EXITO);
                    } else {
                        respuesta.setDatos(null);
                        respuesta.setMensaje(Constantes.PERCEPCION_NO_EXISTE);
                        respuesta.setResultado(Constantes.RESULTADO_ERROR);
                    }
                }
            }
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " guardarPercepcion " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    public RespuestaGenerica guardaPercepcionesEstandar(Integer clienteId) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            if (nmmConceptoPercepcionRepository.findByCentrocClienteIdCentrocClienteId(clienteId).isEmpty()) {
                List<CsTipoPercepcion> estandar = csTipoPercepcionRepository.findByPorDefectoAndEsActivo(Constantes.ESTATUS_ACTIVO,Constantes.ESTATUS_ACTIVO);
                if (!estandar.isEmpty()) {
                    respuesta = guardarPercepcionEstandar(clienteId, estandar);
                } else {
                    respuesta.setMensaje(Constantes.ERROR);
                    respuesta.setResultado(Constantes.RESULTADO_ERROR);
                }
            } else {
                respuesta.setMensaje(Constantes.PERCEPCIONES_EXISTE);
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
            }

            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " guardaPercepcionesEstandar " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    public RespuestaGenerica guardarPercepcionEstandar(Integer clienteId, List<CsTipoPercepcion> estandar) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            for (CsTipoPercepcion percepcionEstandar : estandar) {
                NmmConceptoPercepcion percepcion = new NmmConceptoPercepcion();
                percepcion.setCentrocClienteId(new NclCentrocCliente());
                percepcion.setTipoPercepcionId(new CsTipoPercepcion());
                percepcion.setNombre(percepcionEstandar.getDescripcion());
                percepcion.setTipoPercepcionId(percepcionEstandar);
                percepcion.setTipoPeriodicidad("E");
                percepcion.getCentrocClienteId().setCentrocClienteId(clienteId);
                percepcion.setEsActivo(Constantes.ESTATUS_ACTIVO);

                percepcion.setGravaIsn(percepcionEstandar.getIntegraIsn().equals("S"));
                percepcion.setGravaIsr(percepcionEstandar.getIntegraIsr());
                percepcion.setIntegraImss(percepcionEstandar.getIntegraSdi());



                percepcion.setTipoConcepto(percepcionEstandar.getTipoConcepto().equals("A")?"E":percepcionEstandar.getTipoConcepto());
                percepcion.setTipoOtroPago(percepcionEstandar.getTipoOtroPago());
                percepcion.setEspecializacion(percepcionEstandar.getEspecializacion());
                if (percepcionEstandar.getDescripcion().contains(Constantes.SUELDOS)) {
                    percepcion.setNombre(Constantes.SUELDOS);
                    percepcion.setTipoConcepto(Constantes.PERCEPCION_CONCEPTO_ORDINARIO);
                }
                if (percepcionEstandar.getDescripcion().contains(Constantes.PTU)) {
                    percepcion.setNombre(Constantes.PTU);
                    //percepcion.setIntegraImss(Constantes.INTEGRA_IMSS_SI);
                    percepcion.setPeriodicidadPagoId(Constantes.PERIODICIDAD_PAGO_ID_MESUAL);
                }
                if (percepcionEstandar.getDescripcion().contains(Constantes.FONDO_AHORRO)) {
                    percepcion.setTipoConcepto(Constantes.PERCEPCION_CONCEPTO_ORDINARIO);
                }
                if (percepcionEstandar.getDescripcion().contains(Constantes.AGUINALDO)) {
                    percepcion.setPeriodicidadPagoId(Constantes.PERIODICIDAD_PAGO_ID_MESUAL);
                }
                nmmConceptoPercepcionRepository.save(percepcion);
                respuesta.setMensaje(Constantes.EXITO);
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
            }
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " guardarPercepcionEstandar " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    public RespuestaGenerica modificarPercepcion(NmmConceptoPercepcion nmmConceptoPercepcion) throws ServiceException {
        RespuestaGenerica respuesta;
        try {
            respuesta = validarCamposObligatoriosPercepcion(nmmConceptoPercepcion);
            if (respuesta.isResultado()) {
                if (nmmConceptoPercepcion.getConceptoPercepcionId() != null) {
                    NmmConceptoPercepcion percepcion = nmmConceptoPercepcionRepository.findById(nmmConceptoPercepcion.getConceptoPercepcionId()).orElse(null);

                    if (percepcion != null && !esErrorTipoPercepcion(nmmConceptoPercepcion)) {
                        Optional<CsTipoPercepcion> tipoPercepcion = csTipoPercepcionRepository
                                .findByTipoPercepcionIdAndEspecializacion(
                                        nmmConceptoPercepcion.getTipoPercepcionId().getTipoPercepcionId(),
                                        nmmConceptoPercepcion.getTipoPercepcionId().getEspecializacion());
                        if (tipoPercepcion.isPresent()) {
                            // REGLAS DEDE HERENCIA  CAMPOS CON RESPECTO A CsTipoPercepcion
                            seteaCamposPadre(nmmConceptoPercepcion, tipoPercepcion.get());
                            if (tipoPercepcion.get().getPorDefecto() == null || tipoPercepcion.get().getPorDefecto().equals(Constantes.ESTATUS_INACTIVO)) {

                                Optional<NmmConceptoPercepcion> conceptoPercepcion =
                                        nmmConceptoPercepcionRepository.findById(nmmConceptoPercepcion.getConceptoPercepcionId());

                                if (nmmConfiguraPercepcionRepository.
                                        existsByTipoPercepcionIdTipoPercepcionIdAndEspecializacionAndConceptoPercepcionIdConceptoPercepcionId(
                                                conceptoPercepcion.get().getTipoPercepcionId().getTipoPercepcionId(),conceptoPercepcion.get().getEspecializacion(),
                                                conceptoPercepcion.get().getConceptoPercepcionId())){
                                    respuesta.setMensaje(Constantes.ERROR_PERCEPCION_UTILIZADA);
                                    respuesta.setResultado(Constantes.RESULTADO_ERROR);
                                }else{
                                    respuesta.setDatos(nmmConceptoPercepcionRepository.update(nmmConceptoPercepcion));
                                    respuesta.setMensaje(Constantes.EXITO);
                                    respuesta.setResultado(Constantes.RESULTADO_EXITO);
                                }

                            } else {
                                if(tipoPercepcion.get().getTipoConcepto().equals("A")){
                                    percepcion.setTipoConcepto(nmmConceptoPercepcion.getTipoConcepto());
                                }

                                if(tipoPercepcion.get().getTipoPeriodicidad().equals("A")){
                                    percepcion.setTipoPeriodicidad(nmmConceptoPercepcion.getTipoPeriodicidad());
                                }
                                respuesta = modificaPercepcionesEstandar(nmmConceptoPercepcion, percepcion);
                            }
                        } else {
                            respuesta.setMensaje(Constantes.TIPO_PERCEPCION_NO_EXISTE);
                            respuesta.setResultado(Constantes.RESULTADO_ERROR);
                        }
                    } else {
                        respuesta.setMensaje(Constantes.PERCEPCION_NO_EXISTE);
                        respuesta.setResultado(Constantes.RESULTADO_ERROR);
                    }
                } else {
                    respuesta.setMensaje(Constantes.PERCEPCION_ID_REQ);
                    respuesta.setResultado(Constantes.RESULTADO_ERROR);
                }
            }
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " modificarPercepcion " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    public RespuestaGenerica modificaPercepcionesEstandar(NmmConceptoPercepcion nmmConceptoPercepcion, NmmConceptoPercepcion percepcion) throws ServiceException {
        RespuestaGenerica respuesta = new RespuestaGenerica();
        try {
            if (nmmConceptoPercepcion.getEsActivo() != null ) {
                percepcion.setEsActivo(nmmConceptoPercepcion.getEsActivo());
                percepcion.setNombre(nmmConceptoPercepcion.getNombre());
                percepcion.setCuentaContable(nmmConceptoPercepcion.getCuentaContable());
                percepcion.setGravaIsn(nmmConceptoPercepcion.getGravaIsn());
                percepcion.setGravaIsr(nmmConceptoPercepcion.getGravaIsr());
                percepcion.setTipoPercepcionId(nmmConceptoPercepcion.getTipoPercepcionId());
                percepcion.setEspecializacion(nmmConceptoPercepcion.getEspecializacion());
                if (!percepcion.getNombre().contains(Constantes.FONDO_AHORRO)) {
                    percepcion.setIntegraImss(nmmConceptoPercepcion.getIntegraImss());
                }
                if (percepcion.getNombre().contains(Constantes.FONDO_AHORRO) || percepcion.getNombre().contains(Constantes.PERCEPCION_PUNTUALIDAD)
                        || percepcion.getNombre().contains(Constantes.HORAS_EXTRA) || percepcion.getNombre().contains(Constantes.PRIMA_DOMINICAL)) {
                    percepcion.setTipoConcepto(nmmConceptoPercepcion.getTipoConcepto());
                }
                respuesta.setDatos(nmmConceptoPercepcionRepository.update(percepcion));
                respuesta.setMensaje(Constantes.MODIFICA_PERCEPCION_ESTANDAR);
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
            } else {
                respuesta.setMensaje(Constantes.DESACTIVAR_PERCEPCION_ESTANDAR);
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
            }
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " modificaPercepcionesEstandar " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    public RespuestaGenerica guardarDeduccion(NmmConceptoDeduccion nmmConceptoDeduccion) throws ServiceException {
        RespuestaGenerica respuesta;
        try {
            respuesta = validarCamposObligatoriosDeduccion(nmmConceptoDeduccion);
            if (respuesta.isResultado()) {
                respuesta = validarEstructuraDeduccion(nmmConceptoDeduccion);
                if (respuesta.isResultado()) {
                    Optional<CsTipoDeduccion> deduccion = csTipoDeduccionRepository
                            .findByTipoDeduccionIdAndEspecializacion(
                                    nmmConceptoDeduccion.getTipoDeduccionId().getTipoDeduccionId(),
                                    nmmConceptoDeduccion.getTipoDeduccionId().getEspecializacion());
                    if (deduccion.isPresent()) {
                        nmmConceptoDeduccion.setTipoDeduccionId(deduccion.get());
                        nmmConceptoDeduccion.setEspecializacion(deduccion.get().getEspecializacion());
                        nmmConceptoDeduccion.setEsActivo(Constantes.ESTATUS_ACTIVO);
                        respuesta.setDatos(nmmConceptoDeduccionRepository.save(nmmConceptoDeduccion));
                        respuesta.setMensaje(Constantes.EXITO);
                        respuesta.setResultado(Constantes.RESULTADO_EXITO);
                    } else {
                        respuesta.setDatos(null);
                        respuesta.setMensaje(Constantes.PERCEPCION_NO_EXISTE);
                        respuesta.setResultado(Constantes.RESULTADO_ERROR);
                    }
                }
            }
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " guardarDeduccion " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    public RespuestaGenerica guardaDeduccionesEstandar(Integer clienteId) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            if (nmmConceptoDeduccionRepository.findByCentrocClienteIdCentrocClienteId(clienteId).isEmpty()) {
                List<CsTipoDeduccion> estandar = csTipoDeduccionRepository.findByPorDefecto(Constantes.ESTATUS_ACTIVO);
                if (!estandar.isEmpty()) {
                    respuesta = guardaDeduccionEstandar(clienteId, estandar);
                } else {
                    respuesta.setMensaje(Constantes.ERROR);
                    respuesta.setResultado(Constantes.RESULTADO_ERROR);
                }
            } else {
                respuesta.setMensaje(Constantes.DEDUCCIONES_EXISTE);
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
            }
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " guardaDeduccionesEstandar " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    public RespuestaGenerica guardaDeduccionEstandar(Integer clienteId, List<CsTipoDeduccion> estandar) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            for (CsTipoDeduccion deduccionEstandar : estandar) {
                NmmConceptoDeduccion deduccion = new NmmConceptoDeduccion();
                deduccion.setCentrocClienteId(new NclCentrocCliente());
                deduccion.setTipoDeduccionId(new CsTipoDeduccion());
                deduccion.setNombre(deduccionEstandar.getDescripcion());
                deduccion.setCuentaContable(Constantes.ESPACIO);
                deduccion.setEsActivo(Constantes.ESTATUS_ACTIVO);
                deduccion.setTipoDeduccionId(deduccionEstandar);
                deduccion.getCentrocClienteId().setCentrocClienteId(clienteId);
                deduccion.setEspecializacion(deduccionEstandar.getEspecializacion());
                if (deduccionEstandar.getDescripcion().contains(Constantes.DEDUCCION_VIVIENDA)) {
                    deduccion.setNombre(Constantes.DEDUCCION_INFONAVIT);
                }
                if (deduccionEstandar.getDescripcion().contains(Constantes.DEDUCCION_INFONACOT)) {
                    deduccion.setNombre(Constantes.DEDUCCION_INFONACOT);
                }
                if (deduccionEstandar.getDescripcion().contains(Constantes.DEDUCCION_AUSENCIA)) {
                    deduccion.setNombre(Constantes.DEDUCCION_AUSENCIA);
                }
                if (deduccionEstandar.getDescripcion().contains(Constantes.DEDUCCION_OBRERO)) {
                    deduccion.setNombre(Constantes.DEDUCCION_IMSS);
                }
                if (deduccionEstandar.getDescripcion().contains(Constantes.DEDUCCION_SUBSIDIO_CAUSADO)) {
                    deduccion.setNombre(Constantes.DEDUCCION_SUBSIDIO_EMPLEO);
                }
                if (deduccionEstandar.getDescripcion().contains(Constantes.DEDUCCION_SUBSIDIO_CAUSADO)) {
                    deduccion.setNombre(Constantes.DEDUCCION_SUBSIDIO_EMPLEO);
                }
                if (deduccionEstandar.getDescripcion().contains(Constantes.OTROS)) {
                    deduccion.setNombre(Constantes.PRESTAMO_PERSONAL);
                }
                nmmConceptoDeduccionRepository.save(deduccion);
                respuesta.setMensaje(Constantes.EXITO);
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
            }
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " guardaDeduccionEstandar " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    public RespuestaGenerica modificarDeduccion(NmmConceptoDeduccion nmmConceptoDeduccion) throws ServiceException {
        RespuestaGenerica respuesta = new RespuestaGenerica();
        try {
            if (nmmConceptoDeduccion.getConceptoDeduccionId() != null) {
                NmmConceptoDeduccion deduccion = nmmConceptoDeduccionRepository.findById(nmmConceptoDeduccion.getConceptoDeduccionId()).orElse(null);
             
                if (deduccion != null && !esErrorTipoDeduccion(nmmConceptoDeduccion)) {
                    Optional<CsTipoDeduccion> tipoDeduccion = csTipoDeduccionRepository
                            .findByTipoDeduccionIdAndEspecializacion(
                                    nmmConceptoDeduccion.getTipoDeduccionId().getTipoDeduccionId(),
                                    nmmConceptoDeduccion.getTipoDeduccionId().getEspecializacion());
                   /** if (tipoDeduccion.isPresent() && (tipoDeduccion.get().getPorDefecto() == null || tipoDeduccion.get().getPorDefecto().equals(Constantes.ESTATUS_INACTIVO))) {*/
                        nmmConceptoDeduccion.setTipoDeduccionId(tipoDeduccion.get());
                        nmmConceptoDeduccion.setEspecializacion(tipoDeduccion.get().getEspecializacion());
                        respuesta = validarCamposObligatoriosDeduccion(nmmConceptoDeduccion);
                        Optional<NmmConceptoDeduccion> conceptoDeduccion =
                                nmmConceptoDeduccionRepository.findById(nmmConceptoDeduccion.getConceptoDeduccionId());

                        if (nmmConfiguraDeduccionRepository.
                                existsByTipoDeduccionIdTipoDeduccionIdAndEspecializacionAndConceptoDeduccionIdConceptoDeduccionId(
                                        conceptoDeduccion.get().getTipoDeduccionId().getTipoDeduccionId(),conceptoDeduccion.get().getEspecializacion(),
                                        conceptoDeduccion.get().getConceptoDeduccionId())){
                            respuesta.setMensaje(Constantes.ERROR_DEDUCCION_UTILIZADA);
                            respuesta.setResultado(Constantes.RESULTADO_ERROR);
                        }else {
                            respuesta.setDatos(nmmConceptoDeduccionRepository.update(nmmConceptoDeduccion));
                            respuesta.setMensaje(Constantes.EXITO);
                            respuesta.setResultado(Constantes.RESULTADO_EXITO);
                        }
                   /** } else {
                        respuesta = modificarDeduccionEstandar(nmmConceptoDeduccion, deduccion);
                    }*/
                } else {
                    respuesta.setMensaje(Constantes.DEDUCCION_NO_EXISTE);
                    respuesta.setResultado(Constantes.RESULTADO_ERROR);
                }
            } else {
                respuesta.setMensaje(Constantes.DEDUCCION_ID_REQ);
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
            }

            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " modificarDeduccion " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    public RespuestaGenerica modificarDeduccionEstandar(NmmConceptoDeduccion nmmConceptoDeduccion, NmmConceptoDeduccion deduccion) throws ServiceException {

        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            if (nmmConceptoDeduccion.getEsActivo() != null) {
                deduccion.setEsActivo(nmmConceptoDeduccion.getEsActivo());
                deduccion.setNombre(nmmConceptoDeduccion.getNombre());
                deduccion.setCuentaContable(nmmConceptoDeduccion.getCuentaContable());
                respuesta.setDatos(nmmConceptoDeduccionRepository.update(deduccion));
                respuesta.setMensaje(Constantes.MODIFICA_DEDUCCION_ESTANDAR);
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
            } else {
                respuesta.setMensaje(Constantes.DESACTIVAR_DEDUCCION_ESTANDAR);
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
            }
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " modificarDeduccionEstandar " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    public RespuestaGenerica calculaMonto(Double montoTotal, Integer numeroPeriodos) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            if (numeroPeriodos >= Constantes.NUMERO_PERIODOS_MIN) {

                respuesta.setDatos(montoTotal / Double.valueOf(numeroPeriodos));
                respuesta.setMensaje(Constantes.EXITO);
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
            } else {
                respuesta.setMensaje(Constantes.NUMERO_PERIODOS_ERROR);
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
            }
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " calculaMonto " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    public RespuestaGenerica obtienePercepcionesEmpresa(Integer clienteId) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            respuesta.setDatos(nmmConceptoPercepcionRepository
                    .findByCentrocClienteIdCentrocClienteIdJoinByIdAndEspecializacion(clienteId));
            respuesta.setMensaje(Constantes.EXITO);
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " obtienePercepcionesEmpresa " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica obtieneDeduccionesEmpresaEstatus(Integer clienteId,boolean estatus) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
           List<NmmConceptoDeduccion> listado=  nmmConceptoDeduccionRepository.findByCentrocClienteIdCentrocClienteIdEspecialidad(clienteId);
           listado.removeIf(remove -> remove.getEspecializacion().equals("001"));
            listado.removeIf(remove -> remove.getEspecializacion().equals("002"));
            listado.removeIf(remove -> remove.getEspecializacion().equals("006"));
            listado.removeIf(remove -> remove.getEspecializacion().equals("021"));
            listado.removeIf(remove -> remove.getEspecializacion().equals("020"));
            listado.removeIf(remove -> remove.getEspecializacion().equals("RET"));

            respuesta.setDatos(listado);
            respuesta.setMensaje(Constantes.EXITO);
            respuesta.setResultado(Constantes.RESULTADO_EXITO);

            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " obtieneDeduccionesEmpresaEstatus " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    public RespuestaGenerica obtieneDeduccionesEmpresa(Integer clienteId) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            List<NmmConceptoDeduccion> nmmConceptoDeduccion= nmmConceptoDeduccionRepository.findByCentrocClienteIdCentrocClienteIdEspecialidad(clienteId);
            respuesta.setDatos(nmmConceptoDeduccion);
            respuesta.setMensaje(Constantes.EXITO);
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " obtieneDeduccionesEmpresa " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    public RespuestaGenerica obtieneDeduccionesPolitica(Integer clienteId) throws ServiceException {
        try {
            // SE AGREGA 021 IMSS
            List<String> especializacionesNoValidas = Arrays.asList("001", "002", "006", "007", "010", "011", "021", "022", "107");
            RespuestaGenerica respuesta = new RespuestaGenerica();

            List<NmmConceptoDeduccion> deduccions = nmmConceptoDeduccionRepository
                    .findByCentrocClienteIdCentrocClienteId(clienteId);
            deduccions.removeIf(deduccion -> especializacionesNoValidas.contains(deduccion.getEspecializacion()));

            respuesta.setDatos(deduccions);
            respuesta.setMensaje(Constantes.EXITO);
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " obtieneDeduccionesEmpresa " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica obtieneDeduccionesPoliticaEstatus(Integer clienteId, boolean estatus) throws ServiceException {
        try {
            // SE AGREGA 021 IMSS
            List<String> especializacionesNoValidas = Arrays.asList("001", "002", "006", "007", "010", "011", "021", "022", "107");
            RespuestaGenerica respuesta = new RespuestaGenerica();

            List<NmmConceptoDeduccion> deduccions = nmmConceptoDeduccionRepository
                    .findByCentrocClienteIdCentrocClienteIdEspecialidad(clienteId);
            deduccions.removeIf(deduccion -> especializacionesNoValidas.contains(deduccion.getEspecializacion()));
            respuesta.setDatos(deduccions);
            respuesta.setMensaje(Constantes.EXITO);
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " obtieneDeduccionesEmpresa " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    public RespuestaGenerica eliminaPercepcion(NmmConceptoPercepcion nmmConceptoPercepcion) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            if (nmmConceptoPercepcion != null && nmmConceptoPercepcion.getConceptoPercepcionId() != null
                    && nmmConceptoPercepcion.getCentrocClienteId() != null
                    && nmmConceptoPercepcion.getCentrocClienteId().getCentrocClienteId() != null
                    && nmmConceptoPercepcion.getTipoPercepcionId() != null
                    && (nmmConceptoPercepcion.getTipoPercepcionId().getTipoPercepcionId() != null
                    || !nmmConceptoPercepcion.getTipoPercepcionId().getTipoPercepcionId().isEmpty())) {
                CsTipoPercepcion tipoPercepcion = csTipoPercepcionRepository.findById(nmmConceptoPercepcion.getTipoPercepcionId().getTipoPercepcionId()).orElse(null);
                if (tipoPercepcion != null && (tipoPercepcion.getPorDefecto() == null || tipoPercepcion.getPorDefecto().equals(Constantes.ESTATUS_INACTIVO))) {
                    if (validaConfiguracionPercepcion(nmmConceptoPercepcion.getConceptoPercepcionId())) {
                        nmmConceptoPercepcionRepository.update(nmmConceptoPercepcion.getConceptoPercepcionId(), Constantes.ESTATUS_INACTIVO);
                        respuesta.setResultado(Constantes.RESULTADO_EXITO);
                        respuesta.setMensaje(Constantes.EXITO);
                    } else {
                        respuesta.setMensaje(Constantes.PERCEPCION_ELIMINAR + nmmConceptoPercepcion.getNombre() + Constantes.PERCEPCION_UTILIZADA);
                        respuesta.setResultado(Constantes.RESULTADO_ERROR);
                    }
                } else {
                    respuesta.setMensaje(Constantes.PERCEPCION_ELIMINAR_ESTANDAR);
                    respuesta.setResultado(Constantes.RESULTADO_ERROR);
                }
            }
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " eliminaPercepcion " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    public RespuestaGenerica eliminaPercepcionEmpleado(NmmConfiguraPercepcion nmmConfiguraPercepcion) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            if (nmmConfiguraPercepcion != null && nmmConfiguraPercepcion.getConfiguraPercepcionId() != null) {
                if (validaPercepcionActiva(nmmConfiguraPercepcion)) {
                    nmmConfiguraPercepcionRepository.update(nmmConfiguraPercepcion.getConfiguraPercepcionId(), Constantes.ESTATUS_INACTIVO);
                    respuesta.setResultado(Constantes.RESULTADO_EXITO);
                    respuesta.setMensaje(Constantes.EXITO);
                } else {
                    respuesta.setMensaje(Constantes.PERCEPCION_EMPLEADO_ELIMINAR);
                    respuesta.setResultado(Constantes.RESULTADO_ERROR);
                }
            }
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " eliminaPercepcionEmpleado " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    public RespuestaGenerica eliminaPercepcionPolitica(NmmConfiguraPercepcion nmmConfiguraPercepcion) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            if (nmmConfiguraPercepcion != null && nmmConfiguraPercepcion.getConfiguraPercepcionId() != null) {
                if (validaPercepcionActiva(nmmConfiguraPercepcion)) {
                    nmmConfiguraPercepcionRepository.update(nmmConfiguraPercepcion.getConfiguraPercepcionId(), Constantes.ESTATUS_INACTIVO);
                    respuesta.setResultado(Constantes.RESULTADO_EXITO);
                    respuesta.setMensaje(Constantes.EXITO);
                } else {
                    respuesta.setMensaje(Constantes.PERCEPCION_EMPLEADO_ELIMINAR);
                    respuesta.setResultado(Constantes.RESULTADO_ERROR);
                }
            }
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " eliminaPercepcionPolitica " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private boolean validaPercepcionActiva(NmmConfiguraPercepcion nmmConfiguraPercepcion) throws ServiceException {
        try {
            return ncrPercepcionXnominaRepository.findByNmmConfiguraPercepcionConfiguraPercepcionId(nmmConfiguraPercepcion.getConfiguraPercepcionId()).isEmpty();
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " validaPercepcionActiva " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    public RespuestaGenerica eliminaDeduccion(NmmConceptoDeduccion nmmConceptoDeduccion) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            if (nmmConceptoDeduccion != null && nmmConceptoDeduccion.getConceptoDeduccionId() != null
                    && nmmConceptoDeduccion.getCentrocClienteId() != null
                    && nmmConceptoDeduccion.getCentrocClienteId().getCentrocClienteId() != null
                    && nmmConceptoDeduccion.getTipoDeduccionId() != null
                    && (nmmConceptoDeduccion.getTipoDeduccionId().getTipoDeduccionId() != null
                    || !nmmConceptoDeduccion.getTipoDeduccionId().getTipoDeduccionId().isEmpty())) {

                CsTipoDeduccion tipoDeduccion = csTipoDeduccionRepository.findById(nmmConceptoDeduccion.getTipoDeduccionId().getTipoDeduccionId()).orElse(null);
                if (tipoDeduccion != null && (tipoDeduccion.getPorDefecto() == null || tipoDeduccion.getPorDefecto().equals(Constantes.ESTATUS_INACTIVO))) {
                    if (validaConfiguracionDeduccion(nmmConceptoDeduccion.getConceptoDeduccionId())) {
                        nmmConceptoDeduccionRepository.update(nmmConceptoDeduccion.getConceptoDeduccionId(), Constantes.ESTATUS_INACTIVO);
                        respuesta.setResultado(Constantes.RESULTADO_EXITO);
                        respuesta.setMensaje(Constantes.EXITO);
                    } else {
                        respuesta.setMensaje(Constantes.DEDUCCION_ELIMINAR + nmmConceptoDeduccion.getNombre() + Constantes.DEDUCCION_UTILIZADA);
                        respuesta.setResultado(Constantes.RESULTADO_ERROR);
                    }
                } else {
                    respuesta.setMensaje(Constantes.DEDUCCION_ELIMINAR_ESTANDAR);
                    respuesta.setResultado(Constantes.RESULTADO_ERROR);
                }
            }
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " eliminaDeduccion " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    public RespuestaGenerica eliminaDeduccionEmpleado(NmmConfiguraDeduccion nmmConfiguraDeduccion) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            if (nmmConfiguraDeduccion != null && nmmConfiguraDeduccion.getConfiguraDeduccionId() != null) {
                if (validaDeduccionActiva(nmmConfiguraDeduccion)) {
                    nmmConceptoDeduccionRepository.update(nmmConfiguraDeduccion.getConfiguraDeduccionId(), Constantes.ESTATUS_INACTIVO);
                    respuesta.setResultado(Constantes.RESULTADO_EXITO);
                    respuesta.setMensaje(Constantes.EXITO);
                } else {
                    respuesta.setMensaje(Constantes.DEDUCCION_EMPLEADO_ELIMINAR);
                    respuesta.setResultado(Constantes.RESULTADO_ERROR);
                }
            }
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " eliminaDeduccionEmpleado " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    public RespuestaGenerica eliminaDeduccionPolitica(NmmConfiguraDeduccion nmmConfiguraDeduccion) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            if (nmmConfiguraDeduccion != null && nmmConfiguraDeduccion.getConfiguraDeduccionId() != null) {
                if (validaDeduccionActiva(nmmConfiguraDeduccion)) {
                    nmmConceptoDeduccionRepository.update(nmmConfiguraDeduccion.getConfiguraDeduccionId(), Constantes.ESTATUS_INACTIVO);
                    respuesta.setResultado(Constantes.RESULTADO_EXITO);
                    respuesta.setMensaje(Constantes.EXITO);
                } else {
                    respuesta.setMensaje(Constantes.DEDUCCION_EMPLEADO_ELIMINAR);
                    respuesta.setResultado(Constantes.RESULTADO_ERROR);
                }
            }
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " eliminaDeduccionPolitica " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private boolean validaDeduccionActiva(NmmConfiguraDeduccion nmmConfiguraDeduccion) throws ServiceException {
        try {
            return ncrDeduccionXnominaRepository.findByNmmConfiguraDeduccionConfiguraDeduccionId(nmmConfiguraDeduccion.getConfiguraDeduccionId()).isEmpty();
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " validaDeduccionActiva " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private RespuestaGenerica validarEstructuraPercepcion(NmmConceptoPercepcion nmmConceptoPercepcion) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            if (validaPercepcionDuplicada(nmmConceptoPercepcion.getNombre(), nmmConceptoPercepcion.getCentrocClienteId().getCentrocClienteId())) {
                respuesta.setMensaje(Constantes.EXITO);
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
            } else {
                respuesta.setMensaje(Constantes.PERCEPCION_DUPLICADA);
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
            }
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " validarEstructuraPercepcion " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private boolean noDuplicaPercepcionPolitica(NmmConfiguraPercepcion nmmConfiguraPercepcion) throws ServiceException {
        try {
            List<NmmConfiguraPercepcion> percepciones = nmmConfiguraPercepcionRepository
                    .findByConceptoPercepcionIdConceptoPercepcionIdAndPoliticaIdPoliticaId(
                            nmmConfiguraPercepcion.getConceptoPercepcionId().getConceptoPercepcionId(),
                            nmmConfiguraPercepcion.getPoliticaId().getPoliticaId());
            percepciones.removeIf(percepcion ->
                    percepcion.getConfiguraPercepcionId().equals(nmmConfiguraPercepcion.getConfiguraPercepcionId()));
            return percepciones.isEmpty();
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " noDuplicaPercepcionPolitica " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private boolean noDuplicaDeduccionPolitica(NmmConfiguraDeduccion nmmConfiguraDeduccion) throws ServiceException {
        try {
            List<NmmConfiguraDeduccion> deducciones = nmmConfiguraDeduccionRepository
                    .findByConceptoDeduccionIdConceptoDeduccionIdAndPoliticaIdPoliticaId(
                            nmmConfiguraDeduccion.getConceptoDeduccionId().getConceptoDeduccionId(),
                            nmmConfiguraDeduccion.getPoliticaId().getPoliticaId());
            deducciones.removeIf(deduccion ->
                    deduccion.getConfiguraDeduccionId().equals(nmmConfiguraDeduccion.getConfiguraDeduccionId()));
            return deducciones.isEmpty();
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " noDuplicaDeduccionPolitica " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private RespuestaGenerica validarEstructuraDeduccion(NmmConceptoDeduccion nmmConceptoDeduccion) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            if (validaDeduccionDuplicada(nmmConceptoDeduccion.getNombre(), nmmConceptoDeduccion.getCentrocClienteId().getCentrocClienteId())) {
                respuesta.setMensaje(Constantes.EXITO);
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
            } else {
                respuesta.setMensaje(Constantes.DEDUCCION_DUPLICADA);
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
            }
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " validarEstructuraDeduccion " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private boolean validaPercepcionDuplicada(String nombre, Integer clienteId) throws ServiceException {
        try {
            return nmmConceptoPercepcionRepository.findByNombreAndCentrocClienteIdCentrocClienteId(nombre, clienteId).isEmpty();
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " validaPercepcionDuplicada " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    protected boolean validaDeduccionDuplicada(String nombre, Integer clienteId) throws ServiceException {
        try {
            return nmmConceptoDeduccionRepository.findByNombreAndCentrocClienteIdCentrocClienteId(nombre, clienteId).isEmpty();
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " validaDeduccionDuplicada " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    protected boolean validaTipoPercepcionDuplicada(String tipoPercepcionId, Integer clienteId) throws ServiceException {
        try {
            return nmmConceptoPercepcionRepository.findByTipoPercepcionIdTipoPercepcionIdAndCentrocClienteIdCentrocClienteId(tipoPercepcionId, clienteId).isEmpty();
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " validaTipoPercepcionDuplicada " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    protected boolean validaTipoPercepcionDuplicadaEmpleado(String tipoPercepcionId, Integer personaId) throws ServiceException {
        try {
            return nmmConfiguraPercepcionRepository.findByTipoPercepcionIdTipoPercepcionIdAndPersonaIdPersonaId(tipoPercepcionId, personaId).isEmpty();
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " validaTipoPercepcionDuplicadaEmpleado " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    protected boolean validaTipoDeduccionDuplicadaEmpleado(String tipoDeduccionId, Integer personaId) throws ServiceException {
        try {
            return nmmConfiguraDeduccionRepository.findByTipoDeduccionIdTipoDeduccionIdAndPersonaIdPersonaId(tipoDeduccionId, personaId).isEmpty();
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " validaTipoDeduccionDuplicadaEmpleado " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    protected boolean validaTipoDeduccionDuplicada(String tipoDeduccionId, Integer clienteId) throws ServiceException {
        try {
            return nmmConceptoDeduccionRepository.findByTipoDeduccionIdTipoDeduccionIdAndCentrocClienteIdCentrocClienteId(tipoDeduccionId, clienteId).isEmpty();
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " validaTipoPercepcionDuplicada " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private boolean validaConfiguracionPercepcion(Integer conceptoPercepcionId) throws ServiceException {
        try {
            return nmmConfiguraPercepcionRepository.findByConceptoPercepcionIdConceptoPercepcionId(conceptoPercepcionId).isEmpty();
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " validaConfiguracionPercepcion " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private boolean validaConfiguracionDeduccion(Integer conceptoDeduccionId) throws ServiceException {
        try {
            return nmmConfiguraDeduccionRepository.findByConceptoDeduccionIdConceptoDeduccionId(conceptoDeduccionId).isEmpty();
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " validaConfiguracionDeduccion " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private RespuestaGenerica validarCamposObligatoriosPercepcion(NmmConceptoPercepcion nmmConceptoPercepcion) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            respuesta.setMensaje(Constantes.EXITO);
            respuesta.setResultado(Constantes.RESULTADO_EXITO);

            if (nmmConceptoPercepcion.getCentrocClienteId() == null
                    || nmmConceptoPercepcion.getCentrocClienteId().getCentrocClienteId() == null
                    || nmmConceptoPercepcion.getNombre().isEmpty()
                    || nmmConceptoPercepcion.getIntegraImss() == null
                    || nmmConceptoPercepcion.getTipoPercepcionId() == null
                    || nmmConceptoPercepcion.getTipoPercepcionId().getTipoPercepcionId() == null
                    || nmmConceptoPercepcion.getTipoPercepcionId().getTipoPercepcionId().isBlank()
                    || nmmConceptoPercepcion.getTipoPercepcionId().getEspecializacion() == null
                    || nmmConceptoPercepcion.getTipoPercepcionId().getEspecializacion().isBlank()) {
                respuesta.setMensaje(Constantes.CAMPOS_REQUERIDOS);
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
            }

            if (!nmmConceptoPercepcion.getTipoPeriodicidad().equals(Constantes.PERIODICIDAD_ESTANDAR)
                    && !nmmConceptoPercepcion.getTipoPeriodicidad().equals(Constantes.PERIODICIDAD_PERIODICA)) {
                respuesta.setMensaje(Constantes.PERCEPCION_PERIODICIDAD_VALIDA);
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
            }

            if (!nmmConceptoPercepcion.getTipoConcepto().equals(Constantes.PERCEPCION_CONCEPTO_ORDINARIO)
                    && !nmmConceptoPercepcion.getTipoConcepto().equals(Constantes.PERCEPCION_CONCEPTO_EXTRAORDINARIO)
                    && !nmmConceptoPercepcion.getTipoConcepto().equals(Constantes.PERCEPCION_CONCEPTO_NA)) {
                respuesta.setMensaje(Constantes.PERCEPCION_PERIODICIDAD_VALIDA);
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
            }

            if (nmmConceptoPercepcion.getGravaIsn() == null) {
                respuesta.setMensaje(Constantes.PERCEPCION_INTEGRA_VALIDA);
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
            }

            if (!nmmConceptoPercepcion.getGravaIsr().equals(Constantes.INTEGRA_IMSS_NO)
                    && !nmmConceptoPercepcion.getGravaIsr().equals(Constantes.INTEGRA_IMSS_SI)) {
                respuesta.setMensaje(Constantes.PERCEPCION_INTEGRA_VALIDA);
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
            }

            if (!nmmConceptoPercepcion.getIntegraImss().equals(Constantes.INTEGRA_IMSS_NO)
                    && !nmmConceptoPercepcion.getIntegraImss().equals(Constantes.INTEGRA_IMSS_SI)) {
                respuesta.setMensaje(Constantes.PERCEPCION_INTEGRA_VALIDA);
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
            }

            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " validarCamposObligatoriosPercepcion " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private RespuestaGenerica validarCamposObligatoriosDeduccion(NmmConceptoDeduccion nmmConceptoDeduccion) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            if (nmmConceptoDeduccion.getCentrocClienteId() == null
                    || nmmConceptoDeduccion.getCentrocClienteId().getCentrocClienteId() == null
                    || nmmConceptoDeduccion.getNombre() == null
                    || nmmConceptoDeduccion.getNombre().isEmpty()
                    || nmmConceptoDeduccion.getTipoDeduccionId() == null
                    || nmmConceptoDeduccion.getTipoDeduccionId().getTipoDeduccionId() == null
                    || nmmConceptoDeduccion.getTipoDeduccionId().getTipoDeduccionId().isBlank()
                    || nmmConceptoDeduccion.getTipoDeduccionId().getEspecializacion() == null
                    || nmmConceptoDeduccion.getTipoDeduccionId().getEspecializacion().isBlank()) {
                respuesta.setMensaje(Constantes.CAMPOS_REQUERIDOS);
                respuesta.setResultado(Constantes.RESULTADO_ERROR);

            } else {
                respuesta.setMensaje(Constantes.EXITO);
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
            }
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " validarCamposObligatoriosDeduccion " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    public RespuestaGenerica guardaPercepcionEmpleado(NmmConfiguraPercepcion nmmConfiguraPercepcion) throws ServiceException {
        try {
            RespuestaGenerica respuesta = validarCamposObligatoriosPercepcionEmpleado(nmmConfiguraPercepcion);
            if (respuesta.isResultado()) {
                Optional<NmmConceptoPercepcion> conceptoPercepcion = nmmConceptoPercepcionRepository
                        .findById(nmmConfiguraPercepcion.getConceptoPercepcionId().getConceptoPercepcionId());
                if (conceptoPercepcion.isPresent()) {
                    nmmConfiguraPercepcion.setEspecializacion(conceptoPercepcion.get().getEspecializacion());
                    nmmConfiguraPercepcion.setEsActivo(Constantes.ESTATUS_ACTIVO);
                    respuesta.setDatos(nmmConfiguraPercepcionRepository.save(nmmConfiguraPercepcion));
                    respuesta.setMensaje(Constantes.EXITO);
                    respuesta.setResultado(Constantes.RESULTADO_EXITO);
                } else {
                    respuesta.setDatos(null);
                    respuesta.setMensaje(Constantes.PERCEPCION_NO_EXISTE);
                    respuesta.setResultado(Constantes.RESULTADO_ERROR);
                }
            }
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " guardaPercepcionEmpleado " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    public RespuestaGenerica guardaPercepcionPolitica(NmmConfiguraPercepcion nmmConfiguraPercepcion) throws ServiceException {
        try {
            RespuestaGenerica respuesta = validarCamposObligatoriosPercepcionPolitica(nmmConfiguraPercepcion);
            if (respuesta.isResultado() && noDuplicaPercepcionPolitica(nmmConfiguraPercepcion)) {
                Optional<NmmConceptoPercepcion> conceptoPercepcion = nmmConceptoPercepcionRepository
                        .findById(nmmConfiguraPercepcion.getConceptoPercepcionId().getConceptoPercepcionId());
                if (conceptoPercepcion.isPresent()) {
                    nmmConfiguraPercepcion.setEspecializacion(conceptoPercepcion.get().getEspecializacion());
                    nmmConfiguraPercepcion.setEsActivo(Constantes.ESTATUS_ACTIVO);
                    respuesta.setDatos(nmmConfiguraPercepcionRepository.save(nmmConfiguraPercepcion));
                    respuesta.setMensaje(Constantes.EXITO);
                    respuesta.setResultado(Constantes.RESULTADO_EXITO);
                } else {
                    respuesta.setDatos(null);
                    respuesta.setMensaje(Constantes.PERCEPCION_NO_EXISTE);
                    respuesta.setResultado(Constantes.RESULTADO_ERROR);
                }
            } else {
                respuesta.setDatos(null);
                respuesta.setMensaje(Constantes.PERCEPCION_DUPLICADA_POLITICA);
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
            }
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " guardaPercepcionPolitica " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    public RespuestaGenerica modificaPercepcionEmpleado(NmmConfiguraPercepcion nmmConfiguraPercepcion) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            if (nmmConfiguraPercepcion.getConfiguraPercepcionId() != null) {
                NmmConfiguraPercepcion percepcion = nmmConfiguraPercepcionRepository
                        .findById(nmmConfiguraPercepcion.getConfiguraPercepcionId()).orElse(null);
                NmmConceptoPercepcion conceptoPercepcion = nmmConceptoPercepcionRepository
                        .findById(nmmConfiguraPercepcion.getConceptoPercepcionId().getConceptoPercepcionId()).orElse(null);
                NcoContratoColaborador contrato = ncoContratoColaboradorRepository.findByPersonaIdPersonaId(nmmConfiguraPercepcion.getPersonaId().getPersonaId());
                if (percepcion != null && contrato != null) {
                    if (contrato.getFechaContrato() != null) {
                        nmmConfiguraPercepcion.setTipoPercepcionId(conceptoPercepcion.getTipoPercepcionId());
                        nmmConfiguraPercepcion.setEspecializacion(conceptoPercepcion.getEspecializacion());
                        nmmConfiguraPercepcion.setFechaContrato(contrato.getFechaContrato());

                        ObjetoMapper.map(nmmConfiguraPercepcion, percepcion);
                        respuesta.setDatos(nmmConfiguraPercepcionRepository.update(percepcion));
                        respuesta.setMensaje(Constantes.EXITO);
                        respuesta.setResultado(Constantes.RESULTADO_EXITO);
                    }
                } else {
                    respuesta.setMensaje(Constantes.ERROR_INCIDENCIA_UPDATE);
                    respuesta.setResultado(Constantes.RESULTADO_ERROR);
                }
            } else {
                respuesta.setMensaje(Constantes.CONFIGURA_PERCEPCION_ID);
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
            }
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " modificaPercepcionEmpleado " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    public RespuestaGenerica modificarPercepcionPolitica(NmmConfiguraPercepcion nmmConfiguraPercepcion) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            if (nmmConfiguraPercepcion.getConfiguraPercepcionId() != null) {
                NmmConfiguraPercepcion percepcion = nmmConfiguraPercepcionRepository
                        .findById(nmmConfiguraPercepcion.getConfiguraPercepcionId()).orElse(null);
                NmmConceptoPercepcion conceptoPercepcion = nmmConceptoPercepcionRepository
                        .findById(nmmConfiguraPercepcion.getConceptoPercepcionId().getConceptoPercepcionId()).orElse(null);
                Optional<NclPolitica> politica = nclPoliticaRepository
                        .findById(nmmConfiguraPercepcion.getPoliticaId().getPoliticaId());
                if (percepcion != null && conceptoPercepcion != null && politica.isPresent()) {
                    if (noDuplicaPercepcionPolitica(nmmConfiguraPercepcion)) {
                        ObjetoMapper.map(nmmConfiguraPercepcion, percepcion);
                        percepcion.setTipoPercepcionId(conceptoPercepcion.getTipoPercepcionId());
                        percepcion.setEspecializacion(conceptoPercepcion.getEspecializacion());

                        respuesta.setDatos(nmmConfiguraPercepcionRepository.update(percepcion));
                        respuesta.setMensaje(Constantes.EXITO);
                        respuesta.setResultado(Constantes.RESULTADO_EXITO);
                    } else {
                        respuesta.setMensaje(Constantes.ERROR_INCIDENCIA_DUPLICATED);
                        respuesta.setResultado(Constantes.RESULTADO_ERROR);
                    }
                } else {
                    respuesta.setMensaje(Constantes.ERROR_INCIDENCIA_UPDATE);
                    respuesta.setResultado(Constantes.RESULTADO_ERROR);
                }
            } else {
                respuesta.setMensaje(Constantes.CONFIGURA_PERCEPCION_ID);
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
            }
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " modificarPercepcionPolitica " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    public RespuestaGenerica modificarDeduccionPolitica(NmmConfiguraDeduccion nmmConfiguraDeduccion) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            if (nmmConfiguraDeduccion.getConfiguraDeduccionId() != null) {
                NmmConfiguraDeduccion deduccion = nmmConfiguraDeduccionRepository.
                        findById(nmmConfiguraDeduccion.getConfiguraDeduccionId()).orElse(null);
                NmmConceptoDeduccion conceptoDeduccion = nmmConceptoDeduccionRepository
                        .findById(nmmConfiguraDeduccion.getConceptoDeduccionId().getConceptoDeduccionId()).orElse(null);
                Optional<NclPolitica> politica = nclPoliticaRepository
                        .findById(nmmConfiguraDeduccion.getPoliticaId().getPoliticaId());
                if (deduccion != null && conceptoDeduccion != null && politica.isPresent()) {
                    if (noDuplicaDeduccionPolitica(nmmConfiguraDeduccion)) {
                        ObjetoMapper.map(nmmConfiguraDeduccion, deduccion);
                        deduccion.setTipoDeduccionId(conceptoDeduccion.getTipoDeduccionId());
                        deduccion.setEspecializacion(conceptoDeduccion.getEspecializacion());

                        respuesta.setDatos(nmmConfiguraDeduccionRepository.update(deduccion));
                        respuesta.setMensaje(Constantes.EXITO);
                        respuesta.setResultado(Constantes.RESULTADO_EXITO);
                    } else {
                        respuesta.setMensaje(Constantes.ERROR_INCIDENCIA_DUPLICATED);
                        respuesta.setResultado(Constantes.RESULTADO_ERROR);
                    }
                } else {
                    respuesta.setMensaje(Constantes.ERROR_INCIDENCIA_UPDATE);
                    respuesta.setResultado(Constantes.RESULTADO_ERROR);
                }
            } else {
                respuesta.setMensaje(Constantes.CONFIGURA_DEDUCCION_ID);
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
            }
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " modificarDeduccionPolitica " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    public RespuestaGenerica guardaDeduccionEmpleado(NmmConfiguraDeduccion nmmConfiguraDeduccion,
                                                     DeduccionEmpleadoArchivosDto archivos) throws ServiceException {
        try {
            RespuestaGenerica respuesta = validarCamposObligatoriosDeduccionEmpleado(nmmConfiguraDeduccion);
            if (respuesta.isResultado()) {
                Optional<NmmConceptoDeduccion> conceptoDeduccion = nmmConceptoDeduccionRepository
                        .findById(nmmConfiguraDeduccion.getConceptoDeduccionId().getConceptoDeduccionId());
                NcoContratoColaborador contratoPersonaId= ncoContratoColaboradorRepository.findByPersonaIdPersonaId(nmmConfiguraDeduccion.getPersonaId().getPersonaId());
                nmmConfiguraDeduccion.setFechaContrato(contratoPersonaId.getFechaContrato());
                if (conceptoDeduccion.isPresent()) {
                    nmmConfiguraDeduccion.setEspecializacion(conceptoDeduccion.get().getEspecializacion());

                    nmmConfiguraDeduccion.setEsActivo(Constantes.ESTATUS_ACTIVO);
                    nmmConfiguraDeduccion = nmmConfiguraDeduccionRepository.save(nmmConfiguraDeduccion);

                    // LOGICA DE GUARDADO DE ARCHIVOS
                    actualizarGuardarArchivo(nmmConfiguraDeduccion, archivos);

                    respuesta.setDatos(nmmConfiguraDeduccion);
                    respuesta.setMensaje(Constantes.EXITO);
                    respuesta.setResultado(Constantes.RESULTADO_EXITO);
                } else {
                    respuesta.setDatos(null);
                    respuesta.setMensaje(Constantes.DEDUCCION_NO_EXISTE);
                    respuesta.setResultado(Constantes.RESULTADO_ERROR);
                }
            }
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " guardaDeduccionEmpleado " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    public RespuestaGenerica modificaDeduccionEmpleado(NmmConfiguraDeduccion nmmConfiguraDeduccion,
                                                       DeduccionEmpleadoArchivosDto archivos) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            if (nmmConfiguraDeduccion.getConfiguraDeduccionId() != null) {
                NmmConfiguraDeduccion deduccion = nmmConfiguraDeduccionRepository
                        .findById(nmmConfiguraDeduccion.getConfiguraDeduccionId()).orElse(null);
                NmmConceptoDeduccion conceptoDeduccion = nmmConceptoDeduccionRepository
                        .findById(nmmConfiguraDeduccion.getConceptoDeduccionId().getConceptoDeduccionId()).orElse(null);
                NcoContratoColaborador contrato = ncoContratoColaboradorRepository.findByPersonaIdPersonaId(nmmConfiguraDeduccion.getPersonaId().getPersonaId());
                if (deduccion != null && conceptoDeduccion != null && contrato != null) {
                    if (contrato.getFechaContrato() != null) {
                        nmmConfiguraDeduccion.setFechaContrato(contrato.getFechaContrato());
                        nmmConfiguraDeduccion.setTipoDeduccionId(conceptoDeduccion.getTipoDeduccionId());
                        nmmConfiguraDeduccion.setEspecializacion(conceptoDeduccion.getEspecializacion());
                         ObjetoMapper.map(nmmConfiguraDeduccion, deduccion);
                        deduccion = nmmConfiguraDeduccionRepository.update(deduccion);

                        // LOGICA DE GUARDADO DE ARCHIVOS
                        actualizarGuardarArchivo(nmmConfiguraDeduccion, archivos);

                        respuesta.setDatos(deduccion);
                        respuesta.setMensaje(Constantes.EXITO);
                        respuesta.setResultado(Constantes.RESULTADO_EXITO);
                    }
                } else {
                    respuesta.setMensaje(Constantes.ERROR_INCIDENCIA_UPDATE);
                    respuesta.setResultado(Constantes.RESULTADO_ERROR);
                }
            } else {
                respuesta.setMensaje(Constantes.CONFIGURA_DEDUCCION_ID);
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
            }
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " modificaDeduccionEmpleado " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private void actualizarGuardarArchivo(NmmConfiguraDeduccion nmmConfiguraDeduccion,
                                          DeduccionEmpleadoArchivosDto archivos) throws ServiceException {
        Optional<NmmConceptoDeduccion> nmmConceptoDeduccion = nmmConceptoDeduccionRepository
                .findById(nmmConfiguraDeduccion.getConceptoDeduccionId().getConceptoDeduccionId());
        if (nmmConceptoDeduccion.isPresent()
                && nmmConceptoDeduccion.get().getNombre().equalsIgnoreCase(Constantes.DEDUCCION_INFONAVIT)) {
            if (archivos.getDocsupension() != null && archivos.getNombresuspension() != null) {
                actualizarGuardarArchivo(nmmConfiguraDeduccion,
                        archivos.getNombresuspension(), archivos.getDocsupension(), true);
            }
            if (archivos.getDocretencion() != null && !archivos.getDocretencion().isEmpty()
                    && archivos.getNombreretencion() != null && !archivos.getNombreretencion().isEmpty()) {
                actualizarGuardarArchivo(nmmConfiguraDeduccion,
                        archivos.getNombreretencion(), archivos.getDocretencion(), false);
            }
        }
    }

    private void actualizarGuardarArchivo(NmmConfiguraDeduccion nmmConfiguraDeduccion, String nombre, String file64,
                                   boolean isSuspencion) throws ServiceException {
        try {
            NmmConfiguraDeduccionXdocumento deduccionXDoc = nmmConfiguraDeduccionXdocumentoRepository
                    .findByConfiguraDeduccionIdConfiguraDeduccionId(nmmConfiguraDeduccion.getConfiguraDeduccionId())
                    .orElse(new NmmConfiguraDeduccionXdocumento(nmmConfiguraDeduccion));

            /**if (deduccionXDoc.getDocumentoSuspensionId() != null && isSuspencion) {
                documentosEmpleadoService.eliminar(deduccionXDoc.getDocumentoSuspensionId().getDocumentosEmpleadoId());
            } else if (deduccionXDoc.getDocumentoRetencionId() != null) {
                documentosEmpleadoService.eliminar(deduccionXDoc.getDocumentoRetencionId().getDocumentosEmpleadoId());
            }*/

            DocumentosEmpleadoDto documentosEmpleadoDto = new DocumentosEmpleadoDto();
            documentosEmpleadoDto.setPersonaId(nmmConfiguraDeduccion.getPersonaId().getPersonaId());
            documentosEmpleadoDto.setCentrocClienteId(nmmConfiguraDeduccion.getCentrocClienteId().getCentrocClienteId());
            documentosEmpleadoDto.setTipoDocumentoId(9);
            documentosEmpleadoDto.setNombreArchivo(nombre);
            documentosEmpleadoDto.setUsuarioId(1);
            documentosEmpleadoDto.setDocumento(Utilidades.decodeContent(file64));

            RespuestaGenerica respuestaDoc = documentosEmpleadoService.guardar(documentosEmpleadoDto);
            if (respuestaDoc.isResultado()) {
                ObjectMapper m = new ObjectMapper();
                DocumentosEmpleado doc = m.convertValue(respuestaDoc.getDatos(), DocumentosEmpleado.class);
                if (doc.getDocumentosEmpleadoId() != null && isSuspencion) {
                    deduccionXDoc.setDocumentoSuspensionId(doc);
                    if (deduccionXDoc.getConfiguraDeduccionXdocumentoId() != null) {
                        nmmConfiguraDeduccionXdocumentoRepository.update(deduccionXDoc);
                    } else {
                        nmmConfiguraDeduccionXdocumentoRepository.save(deduccionXDoc);
                    }
                } else if (doc.getDocumentosEmpleadoId() != null) {
                    deduccionXDoc.setDocumentoRetencionId(doc);
                    if (deduccionXDoc.getConfiguraDeduccionXdocumentoId() != null) {
                        nmmConfiguraDeduccionXdocumentoRepository.update(deduccionXDoc);
                    } else {
                        nmmConfiguraDeduccionXdocumentoRepository.save(deduccionXDoc);
                    }
                }
            }
        } catch (ServiceException e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " actualizarGuardarArchivo " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    public RespuestaGenerica guardaDeduccionPolitica(NmmConfiguraDeduccion nmmConfiguraDeduccion) throws ServiceException {
        try {
            RespuestaGenerica respuesta = validarCamposObligatoriosDeduccionPolitica(nmmConfiguraDeduccion);
            if (respuesta.isResultado() && noDuplicaDeduccionPolitica(nmmConfiguraDeduccion)) {
                Optional<NmmConceptoDeduccion> conceptoDeduccion = nmmConceptoDeduccionRepository
                        .findById(nmmConfiguraDeduccion.getConceptoDeduccionId().getConceptoDeduccionId());
                if (conceptoDeduccion.isPresent()) {
                    nmmConfiguraDeduccion.setEspecializacion(conceptoDeduccion.get().getEspecializacion());
                    nmmConfiguraDeduccion.setEsActivo(Constantes.ESTATUS_ACTIVO);
                    respuesta.setDatos(nmmConfiguraDeduccionRepository.save(nmmConfiguraDeduccion));
                    respuesta.setMensaje(Constantes.EXITO);
                    respuesta.setResultado(Constantes.RESULTADO_EXITO);
                } else {
                    respuesta.setDatos(null);
                    respuesta.setMensaje(Constantes.DEDUCCION_NO_EXISTE);
                    respuesta.setResultado(Constantes.RESULTADO_ERROR);
                }
            } else {
                respuesta.setDatos(null);
                respuesta.setMensaje(Constantes.DEDUCCION_DUPLICADA_POLITICA);
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
            }
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " guardaDeduccionPolitica " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    public RespuestaGenerica obtienePercepcionEmpleado(Integer personaId, Integer clienteId) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            // SOLUCION RAPIDA DE DUPLICIDAD EN JOIN DE TIPOPERCEPCIONID
            List<NmmConfiguraPercepcion> configuraPercepcionList = nmmConfiguraPercepcionRepository
                    .findByPersonaIdPersonaIdAndCentrocClienteIdCentrocClienteId(personaId, clienteId);
            configuraPercepcionList.removeIf(configuraPercepcion -> !configuraPercepcion.getEspecializacion().equals(configuraPercepcion.getTipoPercepcionId().getEspecializacion()));
            respuesta.setDatos(configuraPercepcionList);
            respuesta.setMensaje(Constantes.EXITO);
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " obtienePercepcionEmpleado " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    public RespuestaGenerica obtieneConceptoPercepcionEmpresa(Integer clienteId, String tipoPeriodicidad) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();

            List<NmmConceptoPercepcion> percepcions = nmmConceptoPercepcionRepository
                    .findByCentrocClienteIdCentrocClienteIdAndTipoPeriodicidadAndEsActivo(clienteId, tipoPeriodicidad,true);
            // EVITA LA DUPLICIDAD POR EL JOIN A TIPO PERCEPCION Y QUITA LO QUE NO ES CONFIGURABLE PARA EL EMPLEADO
            percepcions.removeIf(percepcion -> percepcion.getTipoPercepcionId().getEsConfigurablex() == null);
            percepcions.removeIf(percepcion -> !percepcion.getTipoPercepcionId().getEspecializacion().equals(percepcion.getEspecializacion())
                    || percepcion.getTipoPercepcionId().getEsConfigurablex().equals("N")
                    || percepcion.getTipoPercepcionId().getEsConfigurablex().equals("P"));

            respuesta.setDatos(percepcions);
            respuesta.setMensaje(Constantes.EXITO);
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " obtieneConceptoPercepcionEmpresa " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    public RespuestaGenerica obtieneConceptoPercepcionPolitica(Integer clienteId, String tipoPeriodicidad) throws ServiceException {
        try {
            List<String> especializacionesNoValidas = Arrays.asList("001", "002", "003", "025", "I20", "I90");
            RespuestaGenerica respuesta = new RespuestaGenerica();

            List<NmmConceptoPercepcion> percepcions = nmmConceptoPercepcionRepository
                    .findByCentrocClienteIdCentrocClienteIdAndTipoPeriodicidadAndEsActivo(clienteId, tipoPeriodicidad,true);
            // EVITA LA DUPLICIDAD POR EL JOIN A TIPO PERCEPCION Y QUITA LO QUE NO ES CONFIGURABLE PARA LA POLITICA
          /*  percepcions.stream()
                    .filter(c -> c.getTipoPercepcionId().getEsConfigurablex() != null)
                    .filter( c -> !c.getTipoPercepcionId().getEspecializacion().equals(c.getEspecializacion())
                                    || especializacionesNoValidas.contains(c.getEspecializacion())
                                    || c.getTipoPercepcionId().getEsConfigurablex().equals("N")
                                    || c.getTipoPercepcionId().getEsConfigurablex().equals("C"))
                            .collect(Collectors.toList());*/

            percepcions.removeIf(percepcion -> percepcion.getTipoPercepcionId().getEsConfigurablex() == null);

            percepcions.removeIf(percepcion -> !percepcion.getTipoPercepcionId().getEspecializacion().equals(percepcion.getEspecializacion())
                    || especializacionesNoValidas.contains(percepcion.getEspecializacion())
                    || percepcion.getTipoPercepcionId().getEsConfigurablex().equals("N")
                    || percepcion.getTipoPercepcionId().getEsConfigurablex().equals("C"));

            respuesta.setDatos(percepcions);
            respuesta.setMensaje(Constantes.EXITO);
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " obtieneConceptoPercepcionPolitica " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    public RespuestaGenerica obtieneDeduccionEmpleado(Integer personaId, Integer clienteId) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            // SOLUCION RAPIDA DE DUPLICIDAD EN JOIN DE TIPOPERCEPCIONID
            List<NmmConfiguraDeduccion> configuraDeduccionList = nmmConfiguraDeduccionRepository
                    .findByPersonaIdPersonaIdAndCentrocClienteIdCentrocClienteId(personaId, clienteId);
            configuraDeduccionList.removeIf(configuraDeduccion -> !configuraDeduccion.getEspecializacion().equals(configuraDeduccion.getTipoDeduccionId().getEspecializacion()));

            // SOLUCION RAPIDA TARJETA 556
            List<Map<String, Object>> data = configuraDeduccionList.stream().map(configuraDeduccion -> {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> dato = mapper.convertValue(configuraDeduccion, new TypeReference<>(){});
                if (configuraDeduccion.getConceptoDeduccionId().getNombre().equalsIgnoreCase(Constantes.DEDUCCION_INFONAVIT)) {
                    Optional<NmmConfiguraDeduccionXdocumento> ncdxdOp = nmmConfiguraDeduccionXdocumentoRepository
                            .findByConfiguraDeduccionIdConfiguraDeduccionId(configuraDeduccion.getConfiguraDeduccionId());
                    dato.put("suspensionCargada", ncdxdOp.isPresent() && ncdxdOp.get().getDocumentoSuspensionId() != null);
                    dato.put("retencionCargada", ncdxdOp.isPresent() && ncdxdOp.get().getDocumentoRetencionId() != null);
                } else {
                    dato.put("suspensionCargada", false);
                    dato.put("retencionCargada", false);
                }
                return dato;
            }).collect(Collectors.toList());

            respuesta.setDatos(data);
            respuesta.setMensaje(Constantes.EXITO);
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " obtieneDeduccionEmpleado " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    public RespuestaGenerica obtienePercepcionPolitica(Integer politicaId, Integer clienteId) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            // SOLUCION RAPIDA DE DUPLICIDAD EN JOIN DE TIPOPERCEPCIONID
            List<NmmConfiguraPercepcion> configuraPercepcionList = nmmConfiguraPercepcionRepository
                    .findByPoliticaIdPoliticaIdAndCentrocClienteIdCentrocClienteId(politicaId, clienteId);
            configuraPercepcionList.removeIf(configuraPercepcion -> !configuraPercepcion.getEspecializacion().equals(configuraPercepcion.getTipoPercepcionId().getEspecializacion()));
            respuesta.setDatos(configuraPercepcionList);
            respuesta.setMensaje(Constantes.EXITO);
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " obtienePercepcionPolitica " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    public RespuestaGenerica obtieneDeduccionPolitica(Integer politicaId, Integer clienteId) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            // SOLUCION RAPIDA DE DUPLICIDAD EN JOIN DE TIPODEDUCCIONID
            List<NmmConfiguraDeduccion> configuraDeduccionList = nmmConfiguraDeduccionRepository
                    .findByPoliticaIdPoliticaIdAndCentrocClienteIdCentrocClienteId(politicaId, clienteId);
            configuraDeduccionList.removeIf(configuraDeduccion -> !configuraDeduccion.getEspecializacion().equals(configuraDeduccion.getTipoDeduccionId().getEspecializacion()));
            respuesta.setDatos(configuraDeduccionList);
            respuesta.setMensaje(Constantes.EXITO);
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " obtieneDeduccionPolitica " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private RespuestaGenerica validarCamposObligatoriosPercepcionEmpleado(NmmConfiguraPercepcion nmmConfiguraPercepcion) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            if (nmmConfiguraPercepcion.getCentrocClienteId() == null
                    || nmmConfiguraPercepcion.getCentrocClienteId().getCentrocClienteId() == null
                    || nmmConfiguraPercepcion.getConceptoPercepcionId() == null
                    || nmmConfiguraPercepcion.getConceptoPercepcionId().getConceptoPercepcionId() == null
                    || nmmConfiguraPercepcion.getPersonaId() == null
                    || nmmConfiguraPercepcion.getPersonaId().getPersonaId() == null
                    || nmmConfiguraPercepcion.getTipoPercepcionId() == null
                    || nmmConfiguraPercepcion.getTipoPercepcionId().getTipoPercepcionId().isEmpty()) {
                respuesta.setMensaje(Constantes.CAMPOS_REQUERIDOS);
                respuesta.setResultado(Constantes.RESULTADO_ERROR);

            } else {
                respuesta.setMensaje(Constantes.EXITO);
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
            }
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " validarCamposObligatoriosPercepcionEmpleado " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private RespuestaGenerica validarCamposObligatoriosDeduccionEmpleado(NmmConfiguraDeduccion nmmConfiguraDeduccion) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            if (nmmConfiguraDeduccion.getCentrocClienteId() == null
                    || nmmConfiguraDeduccion.getCentrocClienteId().getCentrocClienteId() == null
                    || nmmConfiguraDeduccion.getConceptoDeduccionId() == null
                    || nmmConfiguraDeduccion.getConceptoDeduccionId().getConceptoDeduccionId() == null
                    || nmmConfiguraDeduccion.getPersonaId() == null
                    || nmmConfiguraDeduccion.getPersonaId().getPersonaId() == null
                    || nmmConfiguraDeduccion.getTipoDeduccionId() == null
                    || nmmConfiguraDeduccion.getTipoDeduccionId().getTipoDeduccionId().isEmpty()) {
                respuesta.setMensaje(Constantes.CAMPOS_REQUERIDOS);
                respuesta.setResultado(Constantes.RESULTADO_ERROR);

            } else {
                respuesta.setMensaje(Constantes.EXITO);
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
            }
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " validarCamposObligatoriosDeduccionEmpleado " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private RespuestaGenerica validarCamposObligatoriosPercepcionPolitica(NmmConfiguraPercepcion nmmConfiguraPercepcion) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            if (nmmConfiguraPercepcion.getCentrocClienteId() == null
                    || nmmConfiguraPercepcion.getCentrocClienteId().getCentrocClienteId() == null
                    || nmmConfiguraPercepcion.getConceptoPercepcionId() == null
                    || nmmConfiguraPercepcion.getConceptoPercepcionId().getConceptoPercepcionId() == null
                    || nmmConfiguraPercepcion.getPoliticaId() == null
                    || nmmConfiguraPercepcion.getPoliticaId().getPoliticaId() == null
                    || nmmConfiguraPercepcion.getTipoPercepcionId() == null
                    || nmmConfiguraPercepcion.getTipoPercepcionId().getTipoPercepcionId().isEmpty()) {
                respuesta.setMensaje(Constantes.CAMPOS_REQUERIDOS);
                respuesta.setResultado(Constantes.RESULTADO_ERROR);

            } else {
                respuesta.setMensaje(Constantes.EXITO);
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
            }
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " validarCamposObligatoriosPercepcionPolitica " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private RespuestaGenerica validarCamposObligatoriosDeduccionPolitica(NmmConfiguraDeduccion nmmConfiguraDeduccion) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            if (nmmConfiguraDeduccion.getCentrocClienteId() == null
                    || nmmConfiguraDeduccion.getCentrocClienteId().getCentrocClienteId() == null
                    || nmmConfiguraDeduccion.getConceptoDeduccionId() == null
                    || nmmConfiguraDeduccion.getConceptoDeduccionId().getConceptoDeduccionId() == null
                    || nmmConfiguraDeduccion.getPoliticaId() == null
                    || nmmConfiguraDeduccion.getPoliticaId().getPoliticaId() == null
                    || nmmConfiguraDeduccion.getTipoDeduccionId() == null
                    || nmmConfiguraDeduccion.getTipoDeduccionId().getTipoDeduccionId().isEmpty()) {
                respuesta.setMensaje(Constantes.CAMPOS_REQUERIDOS);
                respuesta.setResultado(Constantes.RESULTADO_ERROR);

            } else {
                respuesta.setMensaje(Constantes.EXITO);
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
            }
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " validarCamposObligatoriosDeduccionEmpleado " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private boolean esErrorTipoPercepcion(NmmConceptoPercepcion nmmConceptoPercepcion) {
        return nmmConceptoPercepcion.getTipoPercepcionId() == null
                || nmmConceptoPercepcion.getTipoPercepcionId().getTipoPercepcionId() == null
                || nmmConceptoPercepcion.getTipoPercepcionId().getTipoPercepcionId().isBlank()
                || nmmConceptoPercepcion.getTipoPercepcionId().getEspecializacion() == null
                || nmmConceptoPercepcion.getTipoPercepcionId().getEspecializacion().isBlank();
    }

    private boolean esErrorTipoDeduccion(NmmConceptoDeduccion nmmConceptoDeduccion) {
        return nmmConceptoDeduccion.getTipoDeduccionId() == null
                || nmmConceptoDeduccion.getTipoDeduccionId().getTipoDeduccionId() == null
                || nmmConceptoDeduccion.getTipoDeduccionId().getTipoDeduccionId().isBlank()
                || nmmConceptoDeduccion.getTipoDeduccionId().getEspecializacion() == null
                || nmmConceptoDeduccion.getTipoDeduccionId().getEspecializacion().isBlank();
    }

    private void seteaCamposPadre(NmmConceptoPercepcion nmmConceptoPercepcion, CsTipoPercepcion tipoPercepcion) {
        // HEREDA DEL PADRE
        nmmConceptoPercepcion.setTipoPercepcionId(tipoPercepcion);
        nmmConceptoPercepcion.setEspecializacion(tipoPercepcion.getEspecializacion());
        if (tipoPercepcion.getTipoOtroPago() != null) {
            nmmConceptoPercepcion.setTipoOtroPago(tipoPercepcion.getTipoOtroPago());
        }

        // REGLA SI NO ES AMBAS VIENE DEL PADRE, SI ES AMBAS ES MANDADO POR EL USUARIO
        if (!tipoPercepcion.getTipoPeriodicidad().equals(Constantes.PERIODICIDAD_AMBAS)) {
            nmmConceptoPercepcion.setTipoPeriodicidad(tipoPercepcion.getTipoPeriodicidad());
        }
        // REGLA SI NO ES AMBAS VIENE DEL PADRE, SI ES AMBAS ES MANDADO POR EL USUARIO
        if (!tipoPercepcion.getTipoConcepto().equals(Constantes.PERCEPCION_CONCEPTO_AMBAS)) {
            nmmConceptoPercepcion.setTipoConcepto(tipoPercepcion.getTipoConcepto());
        }
        // REGLA SI NO ES AMBAS O NO ES CONDICIONADO VIENE DEL PADRE, SI ES AMBAS O CONDICIONADO ES MANDADO POR EL USUARIO
        if (!tipoPercepcion.getIntegraIsn().equals(Constantes.INTEGRA_IMSS_CONDICIONADO)) {
            nmmConceptoPercepcion.setGravaIsn(conversorTextoABoolIntegra(tipoPercepcion.getIntegraIsn()));
        }
        // REGLA SI NO ES AMBAS O NO ES CONDICIONADO VIENE DEL PADRE, SI ES AMBAS O CONDICIONADO ES MANDADO POR EL USUARIO
        if (!tipoPercepcion.getIntegraIsr().equals(Constantes.INTEGRA_IMSS_CONDICIONADO)) {
            System.out.println("si entra?");
            nmmConceptoPercepcion.setGravaIsr(tipoPercepcion.getIntegraIsr());
        }
        // REGLA SI NO ES AMBAS O NO ES CONDICIONADO VIENE DEL PADRE, SI ES AMBAS O CONDICIONADO ES MANDADO POR EL USUARIO
        if (!tipoPercepcion.getIntegraSdi().equals(Constantes.INTEGRA_IMSS_CONDICIONADO)) {
            nmmConceptoPercepcion.setIntegraImss(tipoPercepcion.getIntegraSdi());
        }
    }

    private boolean conversorTextoABoolIntegra(String integra) {
        boolean bandera;
        switch (integra) {
            default:
            case Constantes.INTEGRA_IMSS_SI:
                bandera = true;
                break;
            case Constantes.INTEGRA_IMSS_NO:
                bandera = false;
                break;
        }
        return bandera;
    }

}
