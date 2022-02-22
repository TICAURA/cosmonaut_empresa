package mx.com.ga.cosmonaut.empresa;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import javax.inject.Inject;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.entity.administracion.NmmConceptoDeduccion;
import mx.com.ga.cosmonaut.common.entity.administracion.NmmConceptoPercepcion;
import mx.com.ga.cosmonaut.common.entity.catalogo.sat.CsTipoDeduccion;
import mx.com.ga.cosmonaut.common.entity.catalogo.sat.CsTipoPercepcion;
import mx.com.ga.cosmonaut.common.entity.cliente.NclCentrocCliente;
import mx.com.ga.cosmonaut.common.repository.administracion.NmmConceptoDeduccionRepository;
import mx.com.ga.cosmonaut.common.repository.administracion.NmmConceptoPercepcionRepository;
import mx.com.ga.cosmonaut.common.repository.administracion.NmmConfiguraDeduccionRepository;
import mx.com.ga.cosmonaut.common.repository.administracion.NmmConfiguraPercepcionRepository;
import mx.com.ga.cosmonaut.common.repository.catalogo.sat.CsTipoDeduccionRepository;
import mx.com.ga.cosmonaut.common.repository.catalogo.sat.CsTipoPercepcionRepository;
import mx.com.ga.cosmonaut.common.util.Constantes;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@MicronautTest
public class PercepcionesDeduccionesTests {

    private static final Logger LOG = LoggerFactory.getLogger(PercepcionesDeduccionesTests.class);

    @Inject
    @Client("/percepcionDeduccion")
    protected RxHttpClient cliente;

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

    @Test
    public void testObtienePercepcionesEmpresa() {
        final RespuestaGenerica respuesta = cliente.toBlocking().retrieve(HttpRequest.GET("/obtener/percepcion/" + 1),
                RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    public void testObtieneDeduccionesEmpresa() {
        final RespuestaGenerica respuesta = cliente.toBlocking().retrieve(HttpRequest.GET("/obtener/deduccion/" + 1),
                RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    public void testGuardaModificaPercepcion() {
        NmmConceptoPercepcion nmmConceptoPercepcion = new NmmConceptoPercepcion();
        nmmConceptoPercepcion.setConceptoPercepcionId(999);
        nmmConceptoPercepcion.setCentrocClienteId(new NclCentrocCliente());
        nmmConceptoPercepcion.getCentrocClienteId().setCentrocClienteId(1);
        nmmConceptoPercepcion.setNombre("TESTPERCEPCIONGUARDAR");
        nmmConceptoPercepcion.setTipoPercepcionId(new CsTipoPercepcion());
        nmmConceptoPercepcion.getTipoPercepcionId().setTipoPercepcionId("053");
        nmmConceptoPercepcion.setTipoPeriodicidad("O");
        final RespuestaGenerica respuesta = cliente.toBlocking().retrieve(HttpRequest.PUT("/guardarPercepcion", nmmConceptoPercepcion),
                RespuestaGenerica.class);
        if (respuesta.isResultado()) {
            nmmConceptoPercepcion.setNombre("TEST MODIFICAR");
            final RespuestaGenerica respuestaModificar = cliente.toBlocking().retrieve(HttpRequest.POST("/modificarPercepcion", nmmConceptoPercepcion),
                    RespuestaGenerica.class);
            if (respuestaModificar.isResultado()) {
                nmmConceptoPercepcionRepository.delete(nmmConceptoPercepcion);
                assertTrue(respuesta.isResultado());
                assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
                LOG.info("Respuesta {}", respuesta.getDatos());

            }

        }

    }

    @Test
    public void testGuardaModificaDeduccion() {
        NmmConceptoDeduccion nmmConceptoDeduccion = new NmmConceptoDeduccion();
        nmmConceptoDeduccion.setConceptoDeduccionId(999);
        nmmConceptoDeduccion.setCentrocClienteId(new NclCentrocCliente());
        nmmConceptoDeduccion.getCentrocClienteId().setCentrocClienteId(1);
        nmmConceptoDeduccion.setNombre("TESTDEDUCCIONGUARDAR");
        nmmConceptoDeduccion.setTipoDeduccionId(new CsTipoDeduccion());
        nmmConceptoDeduccion.getTipoDeduccionId().setTipoDeduccionId("052");
        final RespuestaGenerica respuesta = cliente.toBlocking().retrieve(HttpRequest.PUT("/guardarDeduccion", nmmConceptoDeduccion),
                RespuestaGenerica.class);
        if (respuesta.isResultado()) {
            nmmConceptoDeduccion.setNombre("TEST MODIFICAR DEDUC");
            final RespuestaGenerica respuestaModificar = cliente.toBlocking().retrieve(HttpRequest.POST("/modificarDeduccion", nmmConceptoDeduccion),
                    RespuestaGenerica.class);
            if (respuestaModificar.isResultado()) {
                nmmConceptoDeduccionRepository.delete(nmmConceptoDeduccion);
                assertTrue(respuesta.isResultado());
                assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
                LOG.info("Respuesta {}", respuesta.getDatos());

            }

        }

    }

    @Test
    public void testEliminarDeduccion() {
        NmmConceptoDeduccion nmmConceptoDeduccion = new NmmConceptoDeduccion();
        nmmConceptoDeduccion.setConceptoDeduccionId(999);
        nmmConceptoDeduccion.setCentrocClienteId(new NclCentrocCliente());
        nmmConceptoDeduccion.getCentrocClienteId().setCentrocClienteId(1);
        nmmConceptoDeduccion.setTipoDeduccionId(new CsTipoDeduccion());
        nmmConceptoDeduccion.getTipoDeduccionId().setTipoDeduccionId("052");
        final RespuestaGenerica respuesta = cliente.toBlocking().retrieve(HttpRequest.POST("/eliminarDeduccion", nmmConceptoDeduccion),
                RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());

    }

    @Test
    public void testEliminarPercepcion() {
        NmmConceptoPercepcion nmmConceptoPercepcion = new NmmConceptoPercepcion();
        nmmConceptoPercepcion.setConceptoPercepcionId(999);
        nmmConceptoPercepcion.setCentrocClienteId(new NclCentrocCliente());
        nmmConceptoPercepcion.getCentrocClienteId().setCentrocClienteId(1);
        nmmConceptoPercepcion.setTipoPercepcionId(new CsTipoPercepcion());
        nmmConceptoPercepcion.getTipoPercepcionId().setTipoPercepcionId("053");
        final RespuestaGenerica respuesta = cliente.toBlocking().retrieve(HttpRequest.POST("/eliminarPercepcion", nmmConceptoPercepcion),
                RespuestaGenerica.class);

        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());

    }
    
}
