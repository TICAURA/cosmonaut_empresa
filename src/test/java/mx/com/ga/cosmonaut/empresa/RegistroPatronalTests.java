package mx.com.ga.cosmonaut.empresa;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.entity.cliente.NclCentrocCliente;
import mx.com.ga.cosmonaut.common.entity.cliente.NclRegistroPatronal;
import mx.com.ga.cosmonaut.common.util.Constantes;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
public class RegistroPatronalTests {

    private static final Logger LOG = LoggerFactory.getLogger(PersonaTests.class);

    @Inject
    @Client("/registroPatronal")
    private RxHttpClient cliente;

    @Test
    public void testGuardar() {
        NclRegistroPatronal registroPatronal = new NclRegistroPatronal();
        registroPatronal.setEmPrimaRiesgo(BigDecimal.valueOf(10.00));
        registroPatronal.setEmClaveDelegacionalImss(12345);
        registroPatronal.setEmEnviarMovsImss(true);
        registroPatronal.setEmImssObreroIntegradoApatronal(true);
        registroPatronal.setRegistroPatronal("Prueba");
        registroPatronal.setCentrocClienteId(new NclCentrocCliente());
        registroPatronal.getCentrocClienteId().setCentrocClienteId(1);

        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.PUT("/guardar",registroPatronal),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    public void testModificar() {
        NclRegistroPatronal registroPatronal = new NclRegistroPatronal();
        registroPatronal.setRegistroPatronalId(1);
        registroPatronal.setEmPrimaRiesgo(BigDecimal.valueOf(10.00));
        registroPatronal.setEmClaveDelegacionalImss(12345);
        registroPatronal.setEmEnviarMovsImss(true);
        registroPatronal.setEmImssObreroIntegradoApatronal(true);
        registroPatronal.setRegistroPatronal("Prueba");
        registroPatronal.setCentrocClienteId(new NclCentrocCliente());
        registroPatronal.getCentrocClienteId().setCentrocClienteId(1);

        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.POST("/modificar",registroPatronal),
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

}
