package mx.com.ga.cosmonaut.empresa;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.entity.administracion.NmaMedioContacto;
import mx.com.ga.cosmonaut.common.entity.catalogo.negocio.*;
import mx.com.ga.cosmonaut.common.entity.cliente.NclCentrocCliente;
import mx.com.ga.cosmonaut.common.entity.colaborador.NcoPersona;
import mx.com.ga.cosmonaut.common.util.Constantes;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
public class MedioContactoTests {

    private static final Logger LOG = LoggerFactory.getLogger(PersonaTests.class);

    @Inject
    @Client("/medioContacto")
    private RxHttpClient cliente;

    @Test
    public void testGuardar() {
        NmaMedioContacto medioContacto = new NmaMedioContacto();
        medioContacto.setNumeroOcuenta(generaStringAleatorio() + generaStringAleatorio() + generaNumeroAleatorio());
        medioContacto.setCentrocClienteId(new NclCentrocCliente());
        medioContacto.getCentrocClienteId().setCentrocClienteId(1);
        medioContacto.setPersonaId(new NcoPersona());
        medioContacto.getPersonaId().setPersonaId(152);
        medioContacto.setTipoContactoId(new CatTipoContacto());
        medioContacto.getTipoContactoId().setTipoContactoId(1L);

        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.PUT("/guardar",medioContacto),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    public void testModificar() {
        NmaMedioContacto medioContacto = new NmaMedioContacto();
        medioContacto.setNumeroOcuenta("556677889977");
        medioContacto.setCentrocClienteId(new NclCentrocCliente());
        medioContacto.getCentrocClienteId().setCentrocClienteId(1);
        medioContacto.setPersonaId(new NcoPersona());
        medioContacto.getPersonaId().setPersonaId(152);
        medioContacto.setTipoContactoId(new CatTipoContacto());
        medioContacto.getTipoContactoId().setTipoContactoId(1L);

        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.POST("/modificar",medioContacto),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    public void testObtenerId() {
        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.GET("/obtener/id/" + "556677889977"),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
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

    private Integer generaNumeroAleatorio(){
        Random rand = new Random();
        return rand.nextInt(10);
    }

}
