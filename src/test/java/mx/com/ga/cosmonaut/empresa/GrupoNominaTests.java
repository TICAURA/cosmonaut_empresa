package mx.com.ga.cosmonaut.empresa;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import mx.com.ga.cosmonaut.common.dto.*;
import mx.com.ga.cosmonaut.common.entity.administracion.NmaCuentaBanco;
import mx.com.ga.cosmonaut.common.entity.catalogo.negocio.CatBasePeriodo;
import mx.com.ga.cosmonaut.common.entity.catalogo.negocio.CatEsquemaPago;
import mx.com.ga.cosmonaut.common.entity.catalogo.negocio.CatMoneda;
import mx.com.ga.cosmonaut.common.entity.catalogo.negocio.CatPeriodoAguinaldo;
import mx.com.ga.cosmonaut.common.entity.catalogo.sat.CsBanco;
import mx.com.ga.cosmonaut.common.entity.catalogo.sat.CsPeriodicidadPago;
import mx.com.ga.cosmonaut.common.entity.cliente.NclCentrocCliente;
import mx.com.ga.cosmonaut.common.util.Constantes;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
public class GrupoNominaTests {

    private static final Logger LOG = LoggerFactory.getLogger(PersonaTests.class);

    @Inject
    @Client("/grupoNomina")
    private RxHttpClient cliente;

    @Test
    public void testGuardar() {
        NclGrupoNominaDto grupoNominaDto = new NclGrupoNominaDto();
        grupoNominaDto.setNombre("Prueba");
        grupoNominaDto.setEsActivo(true);
        grupoNominaDto.setManeraCalcularSubsidio("P");
        grupoNominaDto.setBasePeriodoId(new CatBasePeriodo());
        grupoNominaDto.getBasePeriodoId().setBasePeriodoId(1);
        grupoNominaDto.setEsquemaPagoId(new CatEsquemaPago());
        grupoNominaDto.getEsquemaPagoId().setEsquemaPagoId(1L);
        grupoNominaDto.setMonedaId(new CatMoneda());
        grupoNominaDto.getMonedaId().setMonedaId(1L);
        grupoNominaDto.setPeriodoAguinaldoId(new CatPeriodoAguinaldo());
        grupoNominaDto.getPeriodoAguinaldoId().setPeriodoAguinaldoId("EXT");
        grupoNominaDto.setPeriodicidadPagoId(new CsPeriodicidadPago());
        grupoNominaDto.getPeriodicidadPagoId().setPeriodicidadPagoId("01");
        grupoNominaDto.setCentrocClienteId(new NclCentrocCliente());
        grupoNominaDto.getCentrocClienteId().setCentrocClienteId(1);
        grupoNominaDto.setClabe(new NmaCuentaBanco());
        grupoNominaDto.getClabe().setBancoId(new CsBanco());
        grupoNominaDto.getClabe().getBancoId().setBancoId(1L);
        grupoNominaDto.getClabe().setClabe("999999999999999999");

        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.PUT("/guardar",grupoNominaDto),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    public void testModificar() {
        NclGrupoNominaDto grupoNominaDto = new NclGrupoNominaDto();
        grupoNominaDto.setGrupoNominaId(13);
        grupoNominaDto.setNombre("Prueba-Modificar");
        grupoNominaDto.setEsActivo(true);
        grupoNominaDto.setManeraCalcularSubsidio("P");
        grupoNominaDto.setBasePeriodoId(new CatBasePeriodo());
        grupoNominaDto.getBasePeriodoId().setBasePeriodoId(1);
        grupoNominaDto.setEsquemaPagoId(new CatEsquemaPago());
        grupoNominaDto.getEsquemaPagoId().setEsquemaPagoId(1L);
        grupoNominaDto.setMonedaId(new CatMoneda());
        grupoNominaDto.getMonedaId().setMonedaId(1L);
        grupoNominaDto.setPeriodoAguinaldoId(new CatPeriodoAguinaldo());
        grupoNominaDto.getPeriodoAguinaldoId().setPeriodoAguinaldoId("EXT");
        grupoNominaDto.setPeriodicidadPagoId(new CsPeriodicidadPago());
        grupoNominaDto.getPeriodicidadPagoId().setPeriodicidadPagoId("01");
        grupoNominaDto.setCentrocClienteId(new NclCentrocCliente());
        grupoNominaDto.getCentrocClienteId().setCentrocClienteId(1);
        grupoNominaDto.setClabe(new NmaCuentaBanco());
        grupoNominaDto.getClabe().setBancoId(new CsBanco());
        grupoNominaDto.getClabe().getBancoId().setBancoId(1L);
        grupoNominaDto.getClabe().setClabe("999999999999999999");
        grupoNominaDto.getClabe().setCuentaBancoId(1);

        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.POST("/modificar",grupoNominaDto),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    public void testEliminar() {
        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.POST("/eliminar/id/" + 14,""),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    public void testListaIdCompania() {
        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.GET("/lista/id/compania/" + 1),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    public void testObtenerId() {
        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.GET("/obtener/id/" + 1),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    public void testListaDinamica() {
        NclGrupoNominaDto grupoNominaDto = new NclGrupoNominaDto();
        grupoNominaDto.setCentrocClienteId(new NclCentrocCliente());
        grupoNominaDto.getCentrocClienteId().setCentrocClienteId(112);
        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.POST("/lista/dinamica/",grupoNominaDto),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    public void testFindByEsActivo() {
        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.GET("/listar/todosActivo/" + Boolean.TRUE),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

}
