package mx.com.ga.cosmonaut.empresa;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.entity.cliente.NclCentrocCliente;
import mx.com.ga.cosmonaut.common.entity.cliente.NclSede;
import mx.com.ga.cosmonaut.common.util.Constantes;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
public class SedeTests {

    private static final Logger LOG = LoggerFactory.getLogger(PersonaTests.class);

    @Inject
    @Client("/sede")
    private RxHttpClient cliente;

    @Test
    public void testGuardar() {
        NclSede sede = new NclSede();
        sede.setDescripcion("Prueba");
        sede.setNombreCorto("P");
        sede.setCentrocClienteId(new NclCentrocCliente());
        sede.getCentrocClienteId().setCentrocClienteId(1);

        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.PUT("/guardar",sede),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    public void testModificar() {
        NclSede sede = new NclSede();
        sede.setSedeId(1);
        sede.setDescripcion("Prueba");
        sede.setNombreCorto("P");
        sede.setCentrocClienteId(new NclCentrocCliente());
        sede.getCentrocClienteId().setCentrocClienteId(1);

        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.POST("/modificar",sede),
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
    public void testObtenerIdCompania() {
        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.GET("/obtener/id/compania/" + 1),
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
