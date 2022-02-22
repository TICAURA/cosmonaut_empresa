package mx.com.ga.cosmonaut.empresa;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.entity.catalogo.negocio.*;
import mx.com.ga.cosmonaut.common.entity.catalogo.sat.CsTipoContrato;
import mx.com.ga.cosmonaut.common.entity.catalogo.sat.CsTipoJornada;
import mx.com.ga.cosmonaut.common.entity.catalogo.sat.CsTipoRegimenContratacion;
import mx.com.ga.cosmonaut.common.entity.catalogo.ubicacion.CatAreaGeografica;
import mx.com.ga.cosmonaut.common.entity.catalogo.ubicacion.CatEstado;
import mx.com.ga.cosmonaut.common.entity.cliente.*;
import mx.com.ga.cosmonaut.common.entity.colaborador.NcoContratoColaborador;
import mx.com.ga.cosmonaut.common.entity.colaborador.NcoPersona;
import mx.com.ga.cosmonaut.common.util.Constantes;
import org.junit.jupiter.api.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
public class ContratoColaboradorTests {

    private static final Logger LOG = LoggerFactory.getLogger(PersonaTests.class);

    @Inject
    @Client("/contratoColaborador")
    private RxHttpClient cliente;

    @Test
    public void testGuardar() {
        NcoContratoColaborador contratoColaborador = new NcoContratoColaborador();
        contratoColaborador.setAreaId(new NclArea());
        contratoColaborador.getAreaId().setAreaId(2);
        contratoColaborador.setSedeId(new NclSede());
        contratoColaborador.getSedeId().setSedeId(1);
        contratoColaborador.setPuestoId(new NclPuesto());
        contratoColaborador.getPuestoId().setPuestoId(2);
        contratoColaborador.setPoliticaId(new NclPolitica());
        contratoColaborador.getPoliticaId().setPoliticaId(1);
        contratoColaborador.setNumEmpleado(generaNumeroAleatorio() + generaStringAleatorio() + generaNumeroAleatorio());
        contratoColaborador.setFechaAntiguedad(new Date());
        contratoColaborador.setTipoContratoId(new CsTipoContrato());
        contratoColaborador.getTipoContratoId().setTipoContratoId("1");
        contratoColaborador.setFechaContrato(new Date());
        contratoColaborador.setFechaFin(new Date());
        contratoColaborador.setAreaGeograficaId(new CatAreaGeografica());
        contratoColaborador.getAreaGeograficaId().setAreaGeograficaId(1L);
        contratoColaborador.setGrupoNominaId(new NclGrupoNomina());
        contratoColaborador.getGrupoNominaId().setGrupoNominaId(62);
        contratoColaborador.setTipoCompensacionId(new CatTipoCompensacion());
        contratoColaborador.getTipoCompensacionId().setTipoCompensacionId(1L);
        contratoColaborador.setTipoRegimenContratacionId(new CsTipoRegimenContratacion());
        contratoColaborador.getTipoRegimenContratacionId().setTipoRegimenContratacionId("2");
        contratoColaborador.setSueldoBrutoMensual(new BigDecimal(100));
        contratoColaborador.setSalarioDiario(new BigDecimal(100));
        contratoColaborador.setJornadaId(new NclJornada());
        contratoColaborador.getJornadaId().setJornadaId(7);
        contratoColaborador.getJornadaId().setTipoJornadaId(new CsTipoJornada());
        contratoColaborador.getJornadaId().getTipoJornadaId().setTipoJornadaId("04");
        contratoColaborador.setTipoJornadaId("01");
        contratoColaborador.setPersonaId(new NcoPersona());
        contratoColaborador.getPersonaId().setPersonaId(49);
        contratoColaborador.setCentrocClienteId(new NclCentrocCliente());
        contratoColaborador.getCentrocClienteId().setCentrocClienteId(1);
        contratoColaborador.setEstadoId(new CatEstado());
        contratoColaborador.getEstadoId().setEstadoId(1);
        contratoColaborador.setEsSubcontratado(false);
        contratoColaborador.setMetodoPagoId(new CatMetodoPago());
        contratoColaborador.getMetodoPagoId().setMetodoPagoId(1);
        contratoColaborador.setTipoJornadaId("04");

        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.PUT("/guardar",contratoColaborador),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    public void testModificar() throws ParseException {
        NcoContratoColaborador contratoColaborador = new NcoContratoColaborador();
        String sFecha="18/02/2021";
        Date fecha = new SimpleDateFormat("dd/MM/yyyy").parse(sFecha);
        contratoColaborador.setFechaContrato(fecha);
        contratoColaborador.setAreaId(new NclArea());
        contratoColaborador.getAreaId().setAreaId(2);
        contratoColaborador.setSedeId(new NclSede());
        contratoColaborador.getSedeId().setSedeId(1);
        contratoColaborador.setPuestoId(new NclPuesto());
        contratoColaborador.getPuestoId().setPuestoId(2);
        contratoColaborador.setPoliticaId(new NclPolitica());
        contratoColaborador.getPoliticaId().setPoliticaId(1);
        contratoColaborador.setNumEmpleado("12343546");
        contratoColaborador.setFechaAntiguedad(new Date());
        contratoColaborador.setTipoContratoId(new CsTipoContrato());
        contratoColaborador.getTipoContratoId().setTipoContratoId("1");
        contratoColaborador.setFechaInicio(new Timestamp(new Date().getTime()));
        contratoColaborador.setFechaFin(new Date());
        contratoColaborador.setAreaGeograficaId(new CatAreaGeografica());
        contratoColaborador.getAreaGeograficaId().setAreaGeograficaId(1L);
        contratoColaborador.setGrupoNominaId(new NclGrupoNomina());
        contratoColaborador.getGrupoNominaId().setGrupoNominaId(62);
        contratoColaborador.setTipoCompensacionId(new CatTipoCompensacion());
        contratoColaborador.getTipoCompensacionId().setTipoCompensacionId(1L);
        contratoColaborador.setTipoRegimenContratacionId(new CsTipoRegimenContratacion());
        contratoColaborador.getTipoRegimenContratacionId().setTipoRegimenContratacionId("01");
        contratoColaborador.setSueldoBrutoMensual(new BigDecimal(100));
        contratoColaborador.setSalarioDiario(new BigDecimal(100));
        contratoColaborador.setJornadaId(new NclJornada());
        contratoColaborador.getJornadaId().setJornadaId(7);
        contratoColaborador.getJornadaId().getTipoJornadaId().setTipoJornadaId("04");
        contratoColaborador.setPersonaId(new NcoPersona());
        contratoColaborador.getPersonaId().setPersonaId(17);
        contratoColaborador.setCentrocClienteId(new NclCentrocCliente());
        contratoColaborador.getCentrocClienteId().setCentrocClienteId(1);
        contratoColaborador.setEstadoId(new CatEstado());
        contratoColaborador.getEstadoId().setEstadoId(1);
        contratoColaborador.setEsSubcontratado(false);
        contratoColaborador.setTipoJornadaId("04");

        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.POST("/modificar",contratoColaborador),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    public void testModificarCompensacion() throws ParseException {
        NcoContratoColaborador contratoColaborador = new NcoContratoColaborador();
        String sFecha="18/02/2021";
        Date fecha = new SimpleDateFormat("dd/MM/yyyy").parse(sFecha);
        contratoColaborador.setFechaContrato(fecha);
        contratoColaborador.setGrupoNominaId(new NclGrupoNomina());
        contratoColaborador.getGrupoNominaId().setGrupoNominaId(62);
        contratoColaborador.setTipoCompensacionId(new CatTipoCompensacion());
        contratoColaborador.getTipoCompensacionId().setTipoCompensacionId(1L);
        contratoColaborador.setPersonaId(new NcoPersona());
        contratoColaborador.getPersonaId().setPersonaId(17);
        contratoColaborador.setCentrocClienteId(new NclCentrocCliente());
        contratoColaborador.getCentrocClienteId().setCentrocClienteId(1);
        contratoColaborador.setSueldoBrutoMensual(new BigDecimal(100));
        contratoColaborador.setSueldoNetoMensual(new BigDecimal(100));
        contratoColaborador.setSalarioDiario(new BigDecimal(100));
        contratoColaborador.setSalarioDiarioIntegrado(new BigDecimal(100));
        contratoColaborador.setSbc(new BigDecimal(100));

        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.POST("/modificar/compensacion",contratoColaborador),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    public void testObtenerIdPersona() {
        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.GET("/obtener/persona/id/" + 17),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    public void testObtenerIdEmpresa() {
        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.GET("/obtener/empresa/id/" + 1),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    public void testObtenerIdGrupoNomina() {
        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.GET("/obtener/grupoNomina/id/" + 75),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    public void obtenerIdPersonaNative() {
        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.GET("/obtener/persona/datos/id/" + 96),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    public void obtenerObtenerIdArea() {
        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.GET("/obtener/area/id/" + 2),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    public void testListaDinamica() {
        NcoContratoColaborador contratoColaborador = new NcoContratoColaborador();
        contratoColaborador.setCentrocClienteId(new NclCentrocCliente());
        contratoColaborador.getCentrocClienteId().setCentrocClienteId(1);
        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.POST("/lista/dinamica/",contratoColaborador),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    public void testGuardarBaja() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String sFechaContrato = "09/02/2021";
        Date fechaContrato = sdf.parse(sFechaContrato);
        String sUltimoDia = "05/03/2021";
        Date ultimoDia = sdf.parse(sUltimoDia);

        NcoContratoColaborador contratoColaborador = new NcoContratoColaborador();
        contratoColaborador.setFechaContrato(fechaContrato);
        contratoColaborador.setPersonaId(new NcoPersona());
        contratoColaborador.getPersonaId().setPersonaId(96);
        contratoColaborador.setCentrocClienteId(new NclCentrocCliente());
        contratoColaborador.getCentrocClienteId().setCentrocClienteId(38);
        contratoColaborador.setTipoBajaId(new CatTipoBaja());
        contratoColaborador.getTipoBajaId().setTipoBajaId(1);
        contratoColaborador.setMotivoBajaId(new CatMotivoBaja());
        contratoColaborador.getMotivoBajaId().setMotivoBajaId(1);
        contratoColaborador.setUltimoDia(ultimoDia);
        contratoColaborador.setFechaParaCalculo("A");
        contratoColaborador.setNotas("Prueba");
        contratoColaborador.setFechaFinUltimoPago(ultimoDia);

        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.POST("/guardar/baja",contratoColaborador),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }


    @Test
    public void testListaEmpleadoBaja() {
        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.GET("/lista/empleado/baja/" + 1 + "/" + "false"),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    private Integer generaNumeroAleatorio(){
        Random rand = new Random();
        return rand.nextInt(10);
    }

    private String generaStringAleatorio(){
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder(1);
        for (int i = 0; i < 1; i++) {
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());
            sb.append(AlphaNumericString
                    .charAt(index));
        }
        return sb.toString();
    }
}
