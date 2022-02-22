package mx.com.ga.cosmonaut.empresa;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import mx.com.ga.cosmonaut.common.dto.*;
import mx.com.ga.cosmonaut.common.entity.administracion.NmaDomicilio;
import mx.com.ga.cosmonaut.common.entity.cliente.NclCentrocCliente;
import mx.com.ga.cosmonaut.common.entity.cliente.NclSede;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.common.util.Constantes;
import org.junit.jupiter.api.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
public class DomicilioTests {

    @Inject
    @Client("/domicilio")
    private RxHttpClient cliente;

    private static final Logger LOG = LoggerFactory.getLogger(CentroCostosClienteTests.class);

    @Test
    public void testGuardarDomicilio() throws ServiceException {
        NmaDomicilio domicilio = new NmaDomicilio();
        domicilio.setCalle("San juan");
        domicilio.setNumExterior("400");
        domicilio.setNumInterior("2");
        domicilio.setReferencias("Prueba");
        domicilio.setEsDomicilioFiscal(true);
        domicilio.setEsActivo(true);
        domicilio.setCentrocClienteId(new NclCentrocCliente());
        domicilio.getCentrocClienteId().setCentrocClienteId(1);
        domicilio.setAsentamientoId(584);
        domicilio.setEstado(9);
        domicilio.setCodigo("04230");
        domicilio.setMunicipio(3);

        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.PUT("/guardar",domicilio),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    public void testGuardarSedeDomicilio() throws ServiceException {
        NmaDomicilio domicilio = new NmaDomicilio();
        domicilio.setCalle("San juan");
        domicilio.setNumExterior("400");
        domicilio.setNumInterior("2");
        domicilio.setReferencias("Prueba");
        domicilio.setEsDomicilioFiscal(true);
        domicilio.setEsActivo(true);
        domicilio.setSedeId(new NclSede());
        domicilio.getSedeId().setDescripcion("Prueba Test");
        domicilio.getSedeId().setCentrocClienteId(new NclCentrocCliente());
        domicilio.getSedeId().getCentrocClienteId().setCentrocClienteId(1);
        domicilio.setCentrocClienteId(new NclCentrocCliente());
        domicilio.getCentrocClienteId().setCentrocClienteId(1);
        domicilio.setAsentamientoId(1316);
        domicilio.setEstado(1);
        domicilio.setCodigo("20240");
        domicilio.setMunicipio(1);

        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.PUT("/guardar/sede/domicilio",domicilio),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    public void testModificar() throws ServiceException {
        NmaDomicilio domicilio = new NmaDomicilio();
        domicilio.setDomicilioId(13);
        domicilio.setCalle("San juan");
        domicilio.setNumExterior("400");
        domicilio.setNumInterior("2");
        domicilio.setReferencias("Prueba");
        domicilio.setEsDomicilioFiscal(true);
        domicilio.setEsActivo(true);
        domicilio.setCentrocClienteId(new NclCentrocCliente());
        domicilio.getCentrocClienteId().setCentrocClienteId(1);
        domicilio.setAsentamientoId(1316);
        domicilio.setEstado(1);
        domicilio.setCodigo("20240");
        domicilio.setMunicipio(1);

        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.POST("/modificar",domicilio),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    public void testObtenerId() {
        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.GET("/obtener/id/" + 6),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    public void testObtenerIdPersona() {
        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.GET("/obtener/id/persona/" + 17),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    public void testObtenerIdEmpresa() {
        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.GET("/obtener/id/empresa/" + 184),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    public void testObtenerIdEmpresaDomicilio() {
        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.GET("/obtener/id/empresa/domicilio/" + 184),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

}
