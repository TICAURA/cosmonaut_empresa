package mx.com.ga.cosmonaut.empresa.services;

import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.dto.ValidacionEmpleadoDto;
import mx.com.ga.cosmonaut.common.dto.ValidacionEmpresaDto;
import mx.com.ga.cosmonaut.common.exception.ServiceException;

import java.math.BigInteger;
import java.util.Date;

public interface ValidacionServices {

    ValidacionEmpresaDto validarPantallaEmpresa(Long idEmpresa) throws ServiceException;

    ValidacionEmpleadoDto validarPantallaEmpleado(Long idEmpleado) throws ServiceException;

    RespuestaGenerica validaTextoObligatorio(Object valor);

    RespuestaGenerica validaComboObligatorio(Object valor);

    RespuestaGenerica obtenerComboJornada(Object objecto);

    RespuestaGenerica obtenerComboObligatorioTexto(Object valor);

    RespuestaGenerica validaComboNoRequerido(Object valor);

    RespuestaGenerica validaNumeroNoRequerido(Object valor);

    RespuestaGenerica validaMontoNoRequerido(Object valor);

    RespuestaGenerica validaFechaNoRequerido(Object valor);

    RespuestaGenerica validaTextoNoRequerido(Object valor);

    RespuestaGenerica validaNumeroObligatorio(Object valor);

    RespuestaGenerica validaNumeroEmpleado(Object valor, Integer centroClienteId);

    RespuestaGenerica validaMontoObligatorio(Object valor);

    RespuestaGenerica validaSindicalizado(Object valor);

    RespuestaGenerica validaFechaObligatorio(Object valor);

    RespuestaGenerica validaFechaFinObligatorio(Object valor);

    RespuestaGenerica validaFechaNacimiento(Date valor);

    boolean validaTipoContrato(String tipoContratoId);

    RespuestaGenerica validaCurp(String curp, String genero);

    RespuestaGenerica validaRfc(String rfc);

    RespuestaGenerica validaNumeroSeguroSocial(String numeroSeguroSocial, Date fechaNacimiento);

    RespuestaGenerica validaCorreoEmpresarial(String correoEmpresarial,Integer centroClienteId);

    RespuestaGenerica validaCorreoEmpresarialUsuarioCosmonaut (String correoEmpresarial);

    RespuestaGenerica validaCorreoPersonal(String correoPersonal,Integer centroClienteId);

    RespuestaGenerica validaCelular(BigInteger celular);

    RespuestaGenerica validaAreaPuesto(Integer areaId, Integer puestoId);

    RespuestaGenerica validaFechaInicioContrato(Date fechaInicio, Date fechaAntiguedad);

    RespuestaGenerica validaNumeroCuenta(String numeroCuenta, Integer bancoId, String clabe,Integer centrocClienteId);

    RespuestaGenerica validaClabe(String numeroCuenta, Integer bancoId, String clabe,Integer centroClienteId);

    RespuestaGenerica validarCuentaClabe(Integer bancoId, String clabe);

    RespuestaGenerica validaNumeroEmpleadoDadoAlta(Object valor, Integer centroClienteId);

    RespuestaGenerica validaBoolean(Object objecto);

    RespuestaGenerica validaFechaFinContrato(Date fechaFin, Date fechaInicio);

    RespuestaGenerica validaApellidoMaterno(String apellidoMaterno);

    RespuestaGenerica validaCaracteresEspeciales(String valor);

    RespuestaGenerica validaFechaBaja(Date fechaBaja, Date fechaInicio);

    RespuestaGenerica validaAreaCentroCliente(Integer areaId, Integer centroClienteId);

    RespuestaGenerica validaPuestoCentroCliente(Integer puestoId, Integer centroClienteId);

    RespuestaGenerica validaPoliticaCentroCliente(Integer politicaId, Integer centroClienteId);

    RespuestaGenerica validaJornadaCentroCliente(Integer jornadaId, Integer centroClienteId);

    RespuestaGenerica validaGrupoNominaCentroCliente(Integer grupoNominaId, Integer centroClienteId);

    RespuestaGenerica validaMontoMayorCero(Double monto);

    RespuestaGenerica validaRfcDuplicado(String rfc, Integer centroClienteId);

    RespuestaGenerica validaNssDuplicado(String nss, Integer centroClienteId);

    RespuestaGenerica validaCurpDuplicado(String curp, Integer centroClienteId);

    RespuestaGenerica validaCorreoEspacion(String correo);

    RespuestaGenerica validaCurpRfc(String curp, String rfc);

    RespuestaGenerica validaCurpFechaNacimiento(String curp, Date fecha);
}
