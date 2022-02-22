package mx.com.ga.cosmonaut.empresa.services.impl;

import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.dto.ValidacionEmpleadoDto;
import mx.com.ga.cosmonaut.common.dto.ValidacionEmpresaDto;
import mx.com.ga.cosmonaut.common.entity.catalogo.sat.CsBanco;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.common.repository.administracion.NmaCuentaBancoRepository;
import mx.com.ga.cosmonaut.common.repository.administracion.NmaDomicilioRepository;
import mx.com.ga.cosmonaut.common.repository.catalogo.sat.CsBancoRepository;
import mx.com.ga.cosmonaut.common.repository.cliente.*;
import mx.com.ga.cosmonaut.common.repository.colaborador.NcoContratoColaboradorRepository;
import mx.com.ga.cosmonaut.common.repository.colaborador.NcoPersonaRepository;
import mx.com.ga.cosmonaut.common.service.AdmUsuariosService;
import mx.com.ga.cosmonaut.common.util.Constantes;
import mx.com.ga.cosmonaut.common.util.Utilidades;
import mx.com.ga.cosmonaut.common.util.Validar;
import mx.com.ga.cosmonaut.empresa.services.ValidacionServices;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Singleton
public class ValidacionServicesImpl implements ValidacionServices {

    @Inject
    private NmaDomicilioRepository nmaDomicilioRepository;

    @Inject
    private NmaCuentaBancoRepository nmaCuentaBancoRepository;

    @Inject
    private NclRegistroPatronalRepository nclRegistroPatronalRepository;

    @Inject
    private NclCentrocClienteRepository nclCentrocClienteRepository;

    @Inject
    private NcoPersonaRepository ncoPersonaRepository;

    @Inject
    private NcoContratoColaboradorRepository ncoContratoColaboradorRepository;

    @Inject
    private NclPuestoXareaRepository nclPuestoXareaRepository;

    @Inject
    private CsBancoRepository csBancoRepository;

    @Inject
    private NclAreaRepository nclAreaRepository;

    @Inject
    private NclPoliticaRepository nclPoliticaRepository;

    @Inject
    private NclGrupoNominaRepository nclGrupoNominaRepository;

    @Inject
    private NclJornadaRepository nclJornadaRepository;

    @Inject
    private NclPuestoRepository nclPuestoRepository;

    @Inject
    private AdmUsuariosService  admUsuariosServices;

    @Override
    public ValidacionEmpresaDto validarPantallaEmpresa(Long idEmpresa) throws ServiceException {
        try{
            ValidacionEmpresaDto validacion = new ValidacionEmpresaDto();
            validacion.setBanco(nmaCuentaBancoRepository.existsByNclCentrocClienteCentrocClienteId(idEmpresa.intValue()));
            validacion.setDomicilio(nmaDomicilioRepository.existsByCentrocClienteIdCentrocClienteId(idEmpresa.intValue()));
            validacion.setImss(nclRegistroPatronalRepository.existsByCentrocClienteIdCentrocClienteId(idEmpresa.intValue()));
            validacion.setEmpresa(nclCentrocClienteRepository.existsByCentrocClienteId(idEmpresa.intValue()));
            validacion.setRespuesta(Constantes.RESULTADO_EXITO);
            validacion.setMensaje(Constantes.EXITO);
            return validacion;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" validarPantallaEmpresa " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public ValidacionEmpleadoDto validarPantallaEmpleado(Long idEmpleado) throws ServiceException {
        try{
            ValidacionEmpleadoDto validacion = new ValidacionEmpleadoDto();

            String metodoPago = ncoPersonaRepository.getMetodoPagoByPersona(idEmpleado);
            boolean esMetodoPago = metodoPago.equalsIgnoreCase("Transferencia");

            validacion.setInformacionBasica(ncoPersonaRepository.existsByPersonaId(idEmpleado.intValue()));
            validacion.setInformacionEmpleo(ncoContratoColaboradorRepository.existsByPersonaIdPersonaId(idEmpleado.intValue()));
            validacion.setDetalleCuenta(esMetodoPago?nmaCuentaBancoRepository.existsByNcoPersonaPersonaId(idEmpleado.intValue()):true);
            validacion.setDomicilio(nmaDomicilioRepository.existsByPersonaIdPersonaId(idEmpleado.intValue()));
            validacion.setPorcentaje(calculaPorcentaje(validacion));
            validacion.setRespuesta(Constantes.RESULTADO_EXITO);
            validacion.setMensaje(Constantes.EXITO);
            return validacion;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" validarPantallaEmpresa " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private Integer calculaPorcentaje (ValidacionEmpleadoDto validacionEmpleado){
        Integer porcentajeInformacionBasica = validacionEmpleado.isInformacionBasica() ? Constantes.PORCENTAJE_INFORMACION_BASICA : 0;
        Integer porcentajeInformacionEmpeado = validacionEmpleado.isInformacionEmpleo() ? Constantes.PORCENTAJE_INFORMACION_EMPLEO : 0;
        Integer porcentajeDetalleCuenta = validacionEmpleado.isDetalleCuenta() ? Constantes.PORCENTAJE_DETALLE_CUENTA : 0;
        Integer porcentajeDomicilio = validacionEmpleado.isDomicilio() ? Constantes.PORCENTAJE_DOMICILIO : 0;
        return porcentajeInformacionBasica + porcentajeInformacionEmpeado + porcentajeDetalleCuenta + porcentajeDomicilio;
    }

    @Override
    public RespuestaGenerica validaTextoObligatorio(Object valor) {
        RespuestaGenerica respuesta = new RespuestaGenerica();
        if (valor instanceof String && !((String) valor).isEmpty()) {
            respuesta.setResultado(Utilidades.validaTexto((String) valor));
            respuesta.setDatos(Utilidades.validaString((String) valor));
            if (respuesta.isResultado()) {
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
            } else {
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(Constantes.ERROR_CAMPO_TEXTO);
            }
        } else if (valor instanceof Double){
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            Long entero = ((Double) valor).longValue();
            respuesta.setDatos(entero.toString());
        }else{
            respuesta.setResultado(Constantes.RESULTADO_ERROR);
            respuesta.setMensaje(Constantes.ERROR_CAMPO_REQUERIDO);
        }
        return respuesta;
    }

    @Override
    public RespuestaGenerica validaComboObligatorio(Object valor){
        RespuestaGenerica respuesta = new RespuestaGenerica();
        if (valor instanceof String){
            obtenerCombo((String) valor, respuesta);
        }else {
            respuesta.setDatos(null);
            respuesta.setResultado(Constantes.RESULTADO_ERROR);
            respuesta.setMensaje(Constantes.ERROR_CAMPO_REQUERIDO);
        }
        return respuesta;
    }

    @Override
    public RespuestaGenerica validaFechaObligatorio(Object valor){
        RespuestaGenerica respuesta = new RespuestaGenerica(null,Constantes.RESULTADO_EXITO,Constantes.EXITO);
        if (valor != null){
            if (valor instanceof Date){
                Date fechaValor = (Date) valor;
                Date fechaActual = new Date();
                respuesta.setDatos(valor);
                if (fechaActual.before(fechaValor)){
                    respuesta = new RespuestaGenerica(valor,Constantes.RESULTADO_ERROR,Constantes.ERROR_FECHA_POSTERIOR_FECHA_ACTUAL);
                }
            }else {
                respuesta = new RespuestaGenerica(null,Constantes.RESULTADO_ERROR,Constantes.ERROR_FORMATO_FECHA);
            }
        }else{
            respuesta.setResultado(Constantes.RESULTADO_ERROR);
            respuesta.setMensaje(Constantes.ERROR_CAMPO_REQUERIDO);
        }
        return respuesta;
    }

    @Override
    public RespuestaGenerica validaFechaFinObligatorio(Object valor){
        RespuestaGenerica respuesta = new RespuestaGenerica(null,Constantes.RESULTADO_EXITO,Constantes.EXITO);
        if (valor != null){
            if (valor instanceof Date){
                respuesta.setDatos(valor);
            }else {
                respuesta = new RespuestaGenerica(null,Constantes.RESULTADO_ERROR,Constantes.ERROR_FORMATO_FECHA);
            }
        }else{
            respuesta.setResultado(Constantes.RESULTADO_ERROR);
            respuesta.setMensaje(Constantes.ERROR_CAMPO_REQUERIDO);
        }
        return respuesta;
    }

    @Override
    public RespuestaGenerica validaFechaNacimiento(Date valor){
        RespuestaGenerica respuesta = new RespuestaGenerica(null,Constantes.RESULTADO_EXITO,Constantes.EXITO);
        if (valor != null){
            respuesta = fechaNacimientofechaActual(valor);
        }
        return respuesta;
    }

    private RespuestaGenerica fechaNacimientofechaActual(Date valor){
        if (!valor.before(new Date())){
            return new RespuestaGenerica(null,Constantes.RESULTADO_ERROR,Constantes.ERROR_CAMPO_FECHA_NACIMIENTO);
        }else {
            return new RespuestaGenerica(null,Constantes.RESULTADO_EXITO,Constantes.EXITO);
        }
    }

    @Override
    public RespuestaGenerica validaNumeroObligatorio(Object valor){
        RespuestaGenerica respuesta = new RespuestaGenerica();
        if (valor instanceof String){
            respuesta.setDatos(null);
            respuesta.setResultado(Constantes.RESULTADO_ERROR);
            if (((String) valor).isEmpty()){
                respuesta.setMensaje(Constantes.ERROR_CAMPO_REQUERIDO);
            }else {
                respuesta.setMensaje(Constantes.ERROR_CAMPO_NUMERO);
            }
        }else if(valor instanceof BigInteger){
            Long lValor = ((BigInteger) valor).longValue();
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            respuesta.setDatos(lValor);
        }else if(valor instanceof Double){
            Long lValor = ((Double) valor).longValue();
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            respuesta.setDatos(lValor);
        }else if(valor instanceof Integer){
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            respuesta.setDatos(valor);
        }else {
            respuesta.setDatos(null);
            respuesta.setResultado(Constantes.RESULTADO_ERROR);
            respuesta.setMensaje(Constantes.ERROR_CAMPO_REQUERIDO);
        }
        return respuesta;
    }

    @Override
    public RespuestaGenerica validaMontoObligatorio(Object valor){
        RespuestaGenerica respuesta = new RespuestaGenerica();
        if(valor != null){
            if(valor instanceof Double || valor instanceof BigDecimal){
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
                respuesta.setDatos(valor);
            }else if (valor instanceof String){
                respuesta.setDatos(null);
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                if (((String) valor).isEmpty()){
                    respuesta.setMensaje(Constantes.ERROR_CAMPO_REQUERIDO);
                }else {
                    respuesta.setMensaje(Constantes.ERROR_CAMPO_NUMERO);
                }
            }else {
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(Constantes.ERROR_CAMPO_NUMERO);
            }
        }else {
            respuesta.setResultado(Constantes.RESULTADO_ERROR);
            respuesta.setMensaje(Constantes.ERROR_CAMPO_REQUERIDO);
        }
        return respuesta;
    }

    @Override
    public RespuestaGenerica validaComboNoRequerido(Object valor){
        RespuestaGenerica respuesta = new RespuestaGenerica(null,Constantes.RESULTADO_EXITO,Constantes.EXITO);
        if (valor instanceof String){
            int inicio = ((String) valor).indexOf('-');
            if (inicio > 0){
                String sub = ((String) valor).substring(0,inicio);
                Integer iValor = Integer.parseInt(sub);
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
                respuesta.setDatos(iValor);
            }
        }
        return respuesta;
    }

    @Override
    public RespuestaGenerica validaNumeroNoRequerido(Object valor){
        RespuestaGenerica respuesta = new RespuestaGenerica(null,Constantes.RESULTADO_EXITO,Constantes.EXITO);
        if (valor != null){
            respuesta = validaNumeroObligatorio(valor);
        }
        return respuesta;
    }

    @Override
    public RespuestaGenerica validaMontoNoRequerido(Object valor){
        RespuestaGenerica respuesta = new RespuestaGenerica(null,Constantes.RESULTADO_EXITO,Constantes.EXITO);
        if (valor instanceof Double){
            respuesta.setDatos(valor);
        }
        return respuesta;
    }

    @Override
    public RespuestaGenerica validaFechaNoRequerido(Object valor){
        RespuestaGenerica respuesta = new RespuestaGenerica(null,Constantes.RESULTADO_EXITO,Constantes.EXITO);
        if (valor != null){
            if (valor instanceof Date){
                Date fechaValor = (Date) valor;
                Date fechaActual = new Date();
                respuesta.setDatos(valor);
                if (fechaValor.before(fechaActual)){
                    respuesta = new RespuestaGenerica(valor,Constantes.RESULTADO_EXITO,Constantes.ERROR_FECHA_POSTERIOR_FECHA_ACTUAL);
                }
            }else {
                respuesta = new RespuestaGenerica(null,Constantes.RESULTADO_EXITO,Constantes.ERROR_FORMATO_FECHA);
            }
        }
        return respuesta;
    }

    @Override
    public RespuestaGenerica validaTextoNoRequerido(Object valor) {
        RespuestaGenerica respuesta = new RespuestaGenerica(null,Constantes.RESULTADO_EXITO,Constantes.EXITO);
        if (valor instanceof String){
            respuesta.setDatos(valor);
        }else if (valor instanceof Double){
            Integer entero = ((Double) valor).intValue();
            respuesta.setDatos(entero.toString());
        }
        return respuesta;
    }

    private void obtenerCombo(String valor, RespuestaGenerica respuesta) {
        int inicio = valor.indexOf('-');
        if (inicio < 0){
            respuesta.setResultado(Constantes.RESULTADO_ERROR);
            respuesta.setMensaje(Constantes.ERROR_CAMPO_SELECCION);
            respuesta.setDatos(null);
        }else {
            String sub = valor.substring(0,inicio);
            Integer iValor = Integer.parseInt(sub);
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            respuesta.setDatos(iValor);
        }
    }

    @Override
    public RespuestaGenerica obtenerComboObligatorioTexto(Object valor) {
        RespuestaGenerica respuesta = new RespuestaGenerica(null,Constantes.RESULTADO_ERROR,Constantes.ERROR_CAMPO_SELECCION);
        if (valor instanceof String){
            int inicio = ((String) valor).indexOf('-');
            if (inicio > 0){
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
                respuesta.setDatos(((String) valor).substring(0,inicio));
            }
        }
        return respuesta;
    }

    @Override
    public RespuestaGenerica validaNumeroEmpleado(Object valor, Integer centroClienteId){
        RespuestaGenerica respuesta = validaTextoObligatorio(valor);
        String numeroEmpleado;
        if (valor instanceof Double){
            long numero = ((Double) valor).longValue();
            numeroEmpleado = Long.toString(numero);
        }else if (valor instanceof String){
            numeroEmpleado = (String) valor;
        }else {
            respuesta.setMensaje(Constantes.ERROR_CAMPO_REQUERIDO);
            respuesta.setResultado(Constantes.RESULTADO_ERROR);
            return respuesta;
        }
        if (ncoContratoColaboradorRepository.
                existsByNumEmpleadoAndCentrocClienteIdCentrocClienteId(numeroEmpleado,centroClienteId)){
            respuesta.setMensaje(Constantes.ERROR_NUMERO_EMPLEADO);
            respuesta.setResultado(Constantes.RESULTADO_ERROR);
            return respuesta;
        }
        respuesta.setMensaje(Constantes.EXITO);
        respuesta.setResultado(Constantes.RESULTADO_EXITO);
        respuesta.setDatos(numeroEmpleado);
        return respuesta;
    }

    @Override
    public RespuestaGenerica validaNumeroEmpleadoDadoAlta(Object valor, Integer centroClienteId){
        RespuestaGenerica respuesta = validaTextoObligatorio(valor);
        String numeroEmpleado;
        if (valor instanceof Double){
            long numero = ((Double) valor).longValue();
            numeroEmpleado = Long.toString(numero);
        }else if (valor instanceof String){
            numeroEmpleado = (String) valor;
        }else {
            respuesta.setMensaje(Constantes.ERROR_CAMPO_REQUERIDO);
            respuesta.setResultado(Constantes.RESULTADO_ERROR);
            return respuesta;
        }
        if (!ncoContratoColaboradorRepository.
                existsByNumEmpleadoAndCentrocClienteIdCentrocClienteId(numeroEmpleado,centroClienteId)){
            respuesta.setMensaje("Error empleado no existe en el centro cliente");
            respuesta.setResultado(Constantes.RESULTADO_ERROR);
            return respuesta;
        }
        respuesta.setMensaje(Constantes.EXITO);
        respuesta.setResultado(Constantes.RESULTADO_EXITO);
        respuesta.setDatos(numeroEmpleado);
        return respuesta;
    }

    @Override
    public RespuestaGenerica validaSindicalizado(Object valor){
        RespuestaGenerica respuesta =  new RespuestaGenerica(null, Constantes.RESULTADO_ERROR, Constantes.ERROR_CAMPO_REQUERIDO);
        if (valor instanceof String && !((String) valor).isEmpty()){
            int inicio = ((String) valor).indexOf('-');
            if (inicio > -1){
                String sub = ((String) valor).substring(0,inicio);
                int sindicalizado = Integer.parseInt(sub);
                Boolean esSindicalizado = sindicalizado == 1;
                respuesta.setDatos(esSindicalizado);
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
                respuesta.setMensaje(Constantes.EXITO);
            }else {
                respuesta.setDatos(null);
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje("Error en el combo de sindicalizado");
            }
        }
        return respuesta;
    }

    @Override
    public boolean validaTipoContrato(String tipoContratoId) {
        boolean es = false;
        if (tipoContratoId.equals(Constantes.ID_CONTRATO_INDETERMINADO.toString())
                || tipoContratoId.equals(Constantes.ID_CONTRATO_JUBILACION.toString())){
            es = true;
        }
        return es;
    }

    @Override
    public RespuestaGenerica validaCurp(String curp, String genero) {
        RespuestaGenerica respuesta = new RespuestaGenerica(curp,Constantes.RESULTADO_EXITO, Constantes.EXITO);
        if (Validar.validaTexto(curp)){
            if (!Validar.curp(curp)){
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(Constantes.CURP_NO_VALIDO);
            }
            String curpGenero = String.valueOf(!curp.isEmpty() ? curp.charAt(10) : null);
            if (genero != null){
                if (!genero.equals(curpGenero)){
                    respuesta.setResultado(Constantes.RESULTADO_ERROR);
                    respuesta.setMensaje(Constantes.ERROR_GENERO_CURP);
                }
            }else {
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(Constantes.ERROR_CURP_FALTA_GENERO);
            }
        }
        return respuesta;
    }

    @Override
    public RespuestaGenerica validaCurpFechaNacimiento(String curp, Date fecha) {
        RespuestaGenerica respuesta = new RespuestaGenerica(curp,Constantes.RESULTADO_EXITO, Constantes.EXITO);
        if (curp != null && fecha != null){
            String sCurp = curp.substring(4,10);
            DateFormat dateFormat = new SimpleDateFormat("yyMMdd");
            String sFecha = dateFormat.format(fecha);
            if (!sCurp.equals(sFecha)){
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(Constantes.ERROR_CURP_FECHA_NACIMIENTO);
            }
        }
        return respuesta;
    }

    @Override
    public RespuestaGenerica validaRfc(String rfc) {
        RespuestaGenerica respuesta = new RespuestaGenerica(rfc,Constantes.RESULTADO_EXITO, Constantes.EXITO);
        if (Validar.validaTexto(rfc) && !Validar.rfc(rfc)){
            respuesta.setResultado(Constantes.RESULTADO_ERROR);
            respuesta.setMensaje(Constantes.RFC_NO_VALIDO);
        }
        return respuesta;
    }

    @Override
    public RespuestaGenerica validaNumeroSeguroSocial(String numeroSeguroSocial, Date fechaNacimiento) {
        RespuestaGenerica respuesta = new RespuestaGenerica(numeroSeguroSocial,Constantes.RESULTADO_EXITO, Constantes.EXITO);
        if(numeroSeguroSocial != null && fechaNacimiento != null){
            if (Validar.validaTexto(numeroSeguroSocial)){
                respuesta = Validar.numeroSerguroSocial(numeroSeguroSocial, fechaNacimiento);
            }
        }else {
            respuesta.setResultado(Constantes.RESULTADO_ERROR);
            respuesta.setMensaje(Constantes.ERROR_NUMERO_SEGURO_SOCIAL_FECHA);
        }

        return respuesta;
    }

    @Override
    public RespuestaGenerica validaCorreoEmpresarial(String correoEmpresarial, Integer centroClienteId) {
        RespuestaGenerica respuesta = new RespuestaGenerica(null, Constantes.RESULTADO_EXITO, Constantes.EXITO);
        if (correoEmpresarial != null && !correoEmpresarial.isEmpty() && centroClienteId != null){
            if (Validar.correo(correoEmpresarial)){
                if (!ncoPersonaRepository.existsByEmailCorporativoAndTipoPersonaIdTipoPersonaIdAndCentrocClienteIdCentrocClienteId(
                        correoEmpresarial, Constantes.ID_EMPLEADO.intValue(), centroClienteId)){
                    respuesta.setResultado(Constantes.RESULTADO_EXITO);
                    respuesta.setMensaje(Constantes.EXITO);
                }else{
                    respuesta.setMensaje(Constantes.CORREO_DUPLICADO);
                    respuesta.setResultado(Constantes.RESULTADO_ERROR);
                }
            }else{
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(Constantes.CORREO_NO_VALIDO);
                return respuesta;
            }
        }
        return respuesta;
    }

    @Override
    public RespuestaGenerica validaCorreoEmpresarialUsuarioCosmonaut(String correoEmpresarial) {
        RespuestaGenerica respuesta = new RespuestaGenerica();
        if(!this.admUsuariosServices.existeEmpleadoCorreo(correoEmpresarial)){
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            respuesta.setMensaje(Constantes.EXITO);
        }else{
            respuesta.setMensaje(Constantes.CORREO_DUPLICADO_COSMONAUT);
            respuesta.setResultado(Constantes.RESULTADO_ERROR);
        }

        return respuesta;
    }

    @Override
    public RespuestaGenerica validaCorreoPersonal(String correoPersonal, Integer centroClienteId) {
        RespuestaGenerica respuesta = new RespuestaGenerica(null, Constantes.RESULTADO_EXITO, Constantes.EXITO);
        if (correoPersonal != null && !correoPersonal.isEmpty()){
            if (Validar.correo(correoPersonal)){
                if (!ncoPersonaRepository.
                        existsByContactoInicialEmailPersonalAndTipoPersonaIdTipoPersonaIdAndCentrocClienteIdcentrocClienteId(
                        correoPersonal, Constantes.ID_EMPLEADO.intValue(), centroClienteId)){
                    respuesta.setResultado(Constantes.RESULTADO_EXITO);
                    respuesta.setMensaje(Constantes.EXITO);
                }else{
                    respuesta.setMensaje(Constantes.CORREO_DUPLICADO);
                    respuesta.setResultado(Constantes.RESULTADO_ERROR);
                }
            }else{
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(Constantes.CORREO_NO_VALIDO);
                return respuesta;
            }
        }
        return respuesta;
    }

    @Override
    public RespuestaGenerica validaCelular(BigInteger celular) {
        RespuestaGenerica respuesta = new RespuestaGenerica(null, Constantes.RESULTADO_EXITO, Constantes.EXITO);
        if(celular != null) {
            if (!Validar.telefono(celular.longValue())) {
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(Constantes.ERROR_NUMERO_CUENTA_DIITOS);
            }
        }
        return respuesta;
    }

    @Override
    public RespuestaGenerica validaAreaPuesto(Integer areaId, Integer puestoId) {
        RespuestaGenerica respuesta = new RespuestaGenerica(null, Constantes.RESULTADO_EXITO, Constantes.EXITO);
        if (areaId != null && puestoId != null && !nclPuestoXareaRepository.existsByAreaIdAndPuestoId(areaId,puestoId)){
            respuesta.setResultado(Constantes.RESULTADO_ERROR);
            respuesta.setMensaje(Constantes.ERROR_AREA_PUESTO);
        }
        return respuesta;
    }

    @Override
    public RespuestaGenerica validaFechaInicioContrato(Date fechaInicio, Date fechaAntiguedad) {
        RespuestaGenerica respuesta = new RespuestaGenerica(null, Constantes.RESULTADO_EXITO, Constantes.EXITO);
        if (fechaInicio != null && fechaAntiguedad != null){
            respuesta = fechaInicioContrato(fechaInicio,fechaAntiguedad);
        }
        return respuesta;
    }

    @Override
    public RespuestaGenerica validaFechaFinContrato(Date fechaFin, Date fechaInicio) {
        RespuestaGenerica respuesta = new RespuestaGenerica(null, Constantes.RESULTADO_EXITO, Constantes.EXITO);
        if (fechaFin != null && fechaInicio != null){
            respuesta = fechaFinContrato(fechaFin,fechaInicio);
        }
        return respuesta;
    }

    private RespuestaGenerica fechaInicioContrato(Date fechaInicio, Date fechaAntiguedad){
        if (fechaInicio.before(fechaAntiguedad)){
            return new RespuestaGenerica(null, Constantes.RESULTADO_ERROR, Constantes.ERROR_FECHA_INICIO_CONTRATO_ANTIGUEDAD);
        }else {
            return new RespuestaGenerica(null, Constantes.RESULTADO_EXITO, Constantes.EXITO);
        }
    }

    private RespuestaGenerica fechaFinContrato(Date fechaFin, Date fechaInicio){
        if (fechaFin.before(fechaInicio)){
            return new RespuestaGenerica(null, Constantes.RESULTADO_ERROR, "Fecha fin de contrato no puede ser menor a fecha de ingreso");
        }else {
            return new RespuestaGenerica(null, Constantes.RESULTADO_EXITO, Constantes.EXITO);
        }
    }

    @Override
    public RespuestaGenerica validaNumeroCuenta(String numeroCuenta, Integer bancoId, String clabe,Integer centrocClienteId) {
        RespuestaGenerica respuesta = new RespuestaGenerica(null, Constantes.RESULTADO_EXITO, Constantes.EXITO);
        if (numeroCuenta != null && bancoId != null && clabe != null && centrocClienteId != null){
            respuesta = numeroCuenta(numeroCuenta, bancoId,centrocClienteId);
        }
        return respuesta;
    }

    private RespuestaGenerica numeroCuenta(String numeroCuenta, Integer bancoId,Integer centrocClienteId){
        if (nmaCuentaBancoRepository.
                existsByNumeroCuentaAndBancoIdBancoIdAndNclCentrocClienteCentrocClienteId(numeroCuenta, bancoId,centrocClienteId)) {
            return new RespuestaGenerica(null, Constantes.RESULTADO_ERROR, Constantes.CUENTA_BANCARIA_DUPLICADA);
        }else {
            if (numeroCuenta.length() <= 10){
                return new RespuestaGenerica(null, Constantes.RESULTADO_EXITO, Constantes.EXITO);
            }else {
                return new RespuestaGenerica(null, Constantes.RESULTADO_ERROR,Constantes.ERROR_NUMERO_CUENTA_DIITOS);
            }
        }
    }

    @Override
    public RespuestaGenerica validaClabe(String numeroCuenta, Integer bancoId, String clabe,Integer centroClienteId) {
        RespuestaGenerica respuesta = new RespuestaGenerica(null, Constantes.RESULTADO_EXITO, Constantes.EXITO);
        if (numeroCuenta != null && bancoId != null && clabe != null && centroClienteId != null){
            respuesta = clave(clabe,centroClienteId);
        }
        return respuesta;
    }

    private RespuestaGenerica clave(String clabe, Integer centroClienteId){
        if (nmaCuentaBancoRepository.existsByClabeAndNclCentrocClienteCentrocClienteId(clabe,centroClienteId)) {
            return new RespuestaGenerica(null, Constantes.RESULTADO_ERROR, Constantes.CUENTA_BANCARIA_DUPLICADA);
        }else {
            if (clabe.length() == 18){
                return new RespuestaGenerica(null, Constantes.RESULTADO_EXITO, Constantes.EXITO);
            }else {
                return new RespuestaGenerica(null, Constantes.RESULTADO_ERROR,Constantes.ERROR_CLABE_DIGITOS);
            }
        }
    }

    @Override
    public RespuestaGenerica validarCuentaClabe(Integer bancoId, String clabe) {
        RespuestaGenerica respuesta = new RespuestaGenerica(null, Constantes.RESULTADO_EXITO, Constantes.EXITO);
        if (bancoId != null && clabe != null && !clabe.isEmpty()){
            Optional<CsBanco> banco = csBancoRepository.findById(bancoId.longValue());
            if (banco.isPresent()) {
                if (!clabe.startsWith(banco.get().getCodBanco())) {
                    respuesta.setMensaje(Constantes.CUENTA_BANCARIA_BANCO);
                    respuesta.setResultado(Constantes.RESULTADO_ERROR);
                }
            } else {
                respuesta.setMensaje(Constantes.CUENTA_BANCO_NO_EXISTE);
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
            }
        }
        return respuesta;
    }

    @Override
    public RespuestaGenerica obtenerComboJornada(Object objecto) {
        RespuestaGenerica respuesta =  new RespuestaGenerica();
        if (objecto instanceof String){
            String valor = (String) objecto;
            if (!valor.isEmpty()){
                int inicio = valor.lastIndexOf('-');
                if (inicio > 0){
                    String sub = valor.substring(0,inicio);
                    respuesta.setResultado(Constantes.RESULTADO_EXITO);
                    respuesta.setMensaje(Constantes.EXITO);
                    respuesta.setDatos(sub);
                }else {
                    respuesta.setResultado(Constantes.RESULTADO_ERROR);
                    respuesta.setMensaje(Constantes.ERROR_CAMPO_REQUERIDO);
                }
            }else {
                respuesta.setDatos(null);
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(Constantes.ERROR_CAMPO_REQUERIDO);
            }
        }else {
            respuesta.setDatos(null);
            respuesta.setResultado(Constantes.RESULTADO_ERROR);
            respuesta.setMensaje(Constantes.ERROR_CAMPO_REQUERIDO);
        }
        return respuesta;
    }

    @Override
    public RespuestaGenerica validaBoolean(Object objecto) {
        RespuestaGenerica respuesta =  new RespuestaGenerica();
        if (objecto instanceof Boolean){
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            respuesta.setMensaje(Constantes.EXITO);
        }else {
            respuesta.setResultado(Constantes.RESULTADO_ERROR);
            respuesta.setMensaje(Constantes.ERROR_CAMPO_REQUERIDO);
        }
        return respuesta;
    }

    @Override
    public RespuestaGenerica validaApellidoMaterno(String apellidoMaterno) {
        RespuestaGenerica respuesta = new RespuestaGenerica(null, Constantes.RESULTADO_EXITO, Constantes.EXITO);
        if (apellidoMaterno != null && !apellidoMaterno.isEmpty()){
            return validaTextoObligatorio(apellidoMaterno);
        }
        return respuesta;
    }

    @Override
    public  RespuestaGenerica validaFechaBaja(Date fechaBaja, Date fechaInicio){
        RespuestaGenerica respuesta = new RespuestaGenerica(null, Constantes.RESULTADO_EXITO, Constantes.EXITO);
        if (fechaBaja != null && fechaInicio != null){
            if (fechaBaja.before(fechaInicio)){
                respuesta.setMensaje(Constantes.ERROR_FECHA_BAJA);
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
            }
        }
        return respuesta;
    }

    @Override
    public RespuestaGenerica validaCaracteresEspeciales(String texto) {
        RespuestaGenerica respuesta = new RespuestaGenerica(null, Constantes.RESULTADO_EXITO, Constantes.EXITO);
        String regexCurp= "^[^<>%$@/]*$";
        if (texto != null){
            if (!Pattern.matches(regexCurp, texto)){
                return new RespuestaGenerica(null, Constantes.RESULTADO_ERROR,"No se admiten caracteres especiales");
            }
        }
        return respuesta;
    }

    @Override
    public RespuestaGenerica validaAreaCentroCliente(Integer areaId, Integer centroClienteId){
        RespuestaGenerica respuesta = new RespuestaGenerica(null, Constantes.RESULTADO_EXITO, Constantes.EXITO);
        if(!nclAreaRepository.existsByAreaIdAndCentrocClienteId(areaId,centroClienteId)){
            respuesta.setResultado(Constantes.RESULTADO_ERROR);
            respuesta.setMensaje(Constantes.ERROR_AREA_CENTRO_CLIENTE);
        }
        return respuesta;
    }

    @Override
    public RespuestaGenerica validaPuestoCentroCliente(Integer puestoId, Integer centroClienteId){
        RespuestaGenerica respuesta = new RespuestaGenerica(null, Constantes.RESULTADO_EXITO, Constantes.EXITO);
        if(!nclPuestoRepository.existsByPuestoIdAndCentrocClienteIdCentrocClienteId(puestoId,centroClienteId)){
            respuesta.setResultado(Constantes.RESULTADO_ERROR);
            respuesta.setMensaje(Constantes.ERROR_PUESTO_CENTRO_CLIENTE);
        }
        return respuesta;
    }

    @Override
    public RespuestaGenerica validaPoliticaCentroCliente(Integer politicaId, Integer centroClienteId){
        RespuestaGenerica respuesta = new RespuestaGenerica(null, Constantes.RESULTADO_EXITO, Constantes.EXITO);
        if(!nclPoliticaRepository.existsByPoliticaIdAndCentrocClienteIdCentrocClienteId(politicaId,centroClienteId)){
            respuesta.setResultado(Constantes.RESULTADO_ERROR);
            respuesta.setMensaje(Constantes.ERROR_POLITICA_CENTRO_CLIENTE);
        }
        return respuesta;
    }

    @Override
    public RespuestaGenerica validaJornadaCentroCliente(Integer jornadaId, Integer centroClienteId){
        RespuestaGenerica respuesta = new RespuestaGenerica(null, Constantes.RESULTADO_EXITO, Constantes.EXITO);
        if(!nclJornadaRepository.existsByJornadaIdAndCentrocClienteIdCentrocClienteId(jornadaId,centroClienteId)){
            respuesta.setResultado(Constantes.RESULTADO_ERROR);
            respuesta.setMensaje(Constantes.ERROR_JORNADA_CENTRO_CLIENTE);
        }
        return respuesta;
    }

    @Override
    public RespuestaGenerica validaGrupoNominaCentroCliente(Integer grupoNominaId, Integer centroClienteId){
        RespuestaGenerica respuesta = new RespuestaGenerica(null, Constantes.RESULTADO_EXITO, Constantes.EXITO);
        if(!nclGrupoNominaRepository.existsByGrupoNominaIdAndCentrocClienteIdCentrocClienteId(grupoNominaId,centroClienteId)){
            respuesta.setResultado(Constantes.RESULTADO_ERROR);
            respuesta.setMensaje(Constantes.ERROR_GRUPO_NOMINA_CENTRO_CLIENTE);
        }
        return respuesta;
    }

    @Override
    public RespuestaGenerica validaMontoMayorCero(Double monto){
        RespuestaGenerica respuesta = new RespuestaGenerica(null, Constantes.RESULTADO_EXITO, Constantes.EXITO);
        if(monto != null){
            if(monto <= 0){
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(Constantes.ERROR_MONTO_MAYO_CERO);
            }
        }
        return respuesta;
    }

    @Override
    public RespuestaGenerica validaRfcDuplicado(String rfc, Integer centroClienteId) {
        RespuestaGenerica respuesta = new RespuestaGenerica(rfc,Constantes.RESULTADO_EXITO, Constantes.EXITO);
        if (centroClienteId != null && rfc != null){
            if (ncoPersonaRepository.existsByRfcAndCentrocClienteIdCentrocClienteId(rfc,centroClienteId)){
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(Constantes.RFC_DUPLICADO);
            }
        }
        return respuesta;
    }

    @Override
    public RespuestaGenerica validaNssDuplicado(String nss, Integer centroClienteId) {
        RespuestaGenerica respuesta = new RespuestaGenerica(nss,Constantes.RESULTADO_EXITO, Constantes.EXITO);
        if (centroClienteId != null && nss != null){
            if (ncoPersonaRepository.existsByNssAndCentrocClienteIdCentrocClienteId(nss,centroClienteId)){
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(Constantes.NSS_DUPLICADO);
            }
        }
        return respuesta;
    }

    @Override
    public RespuestaGenerica validaCurpDuplicado(String curp, Integer centroClienteId) {
        RespuestaGenerica respuesta = new RespuestaGenerica(curp,Constantes.RESULTADO_EXITO, Constantes.EXITO);
        if (centroClienteId != null && curp != null){
            if (ncoPersonaRepository.existsByCurpAndCentrocClienteIdCentrocClienteId(curp,centroClienteId)){
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(Constantes.CURP_DUPLICADO);
            }
        }
        return respuesta;
    }

    @Override
    public RespuestaGenerica validaCorreoEspacion(String correo) {
        RespuestaGenerica respuesta = new RespuestaGenerica(correo,Constantes.RESULTADO_EXITO, Constantes.EXITO);
        if (correo != null){
            Pattern pattern = Pattern.compile("\\s");
            Matcher matcher = pattern.matcher(correo);
            if (matcher.find()){
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(Constantes.ERROR_CORREO_ESPACIOS);
            }
        }
        return respuesta;
    }

    @Override
    public RespuestaGenerica validaCurpRfc(String curp, String rfc) {
        RespuestaGenerica respuesta = new RespuestaGenerica(null,Constantes.RESULTADO_EXITO, Constantes.EXITO);
        if (curp != null && rfc != null){
            String curpSub = curp.substring(0,10);
            String rfcSub = rfc.substring(0,10);
            if (!curpSub.equals(rfcSub)){
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(Constantes.ERROR_CURP_RFC);
            }
        }
        return respuesta;
    }

}
