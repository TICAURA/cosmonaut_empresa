package mx.com.ga.cosmonaut.empresa;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import javax.inject.Inject;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.entity.catalogo.negocio.CatCalculoAntiguedad;
import mx.com.ga.cosmonaut.common.entity.cliente.NclCentrocCliente;
import mx.com.ga.cosmonaut.common.entity.cliente.NclPolitica;
import mx.com.ga.cosmonaut.common.repository.cliente.NclPoliticaRepository;
import mx.com.ga.cosmonaut.common.util.Constantes;
import org.junit.jupiter.api.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
public class PoliticasTests {

    private static final Logger LOG = LoggerFactory.getLogger(PoliticasTests.class);

    @Inject
    @Client("/Politica")
    protected RxHttpClient cliente;

    @Inject
    protected NclPoliticaRepository politicaRepository;

    @Test
    public void testListarTodos() {
        final RespuestaGenerica respuesta = cliente.toBlocking().retrieve(HttpRequest.GET("/listar/todos"),
                RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    public void testPoliticaPorId() {
        final RespuestaGenerica respuesta = cliente.toBlocking().retrieve(HttpRequest.GET("/obtener/politica/" + 1),
                RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    public void testPoliticaEmpleadoPorEmpresaIdPolId() {
        final RespuestaGenerica respuesta = cliente.toBlocking().retrieve(HttpRequest.GET("/obtener/politica/empresa/" + 1 + "?idCliente=1"),
                RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    public void testConsultaPoliticasXEmpresaId() {
        final RespuestaGenerica respuesta = cliente.toBlocking().retrieve(HttpRequest.GET("/obtener/politica/idEmpresa/" + 1),
                RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    public void testConsultaBeneficiosPoliticaIdEmpresaId() {
        final RespuestaGenerica respuesta = cliente.toBlocking().retrieve(HttpRequest.GET("/obtener/beneficio/idCliente/" + 1 + "?idCliente=1"),
                RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    public void testGuardar() {
        NclPolitica nclPolitica = new NclPolitica();
        nclPolitica.setPoliticaId(99999);
        nclPolitica.setNombre("test");
        nclPolitica.setCentrocClienteId(new NclCentrocCliente());
        nclPolitica.getCentrocClienteId().setCentrocClienteId(1);
        nclPolitica.setDiasEconomicos(12);
        nclPolitica.setNombreCorto("testing");
        /**
        nclPolitica.setCalculoAntiguedadId(new CatCalculoAntiguedad());
        nclPolitica.getCalculoAntiguedadId().setCalculoAntiguedadxId(Short.valueOf("1"));
        */
         final RespuestaGenerica respuesta = cliente.toBlocking().retrieve(HttpRequest.PUT("/guardar", nclPolitica),
                RespuestaGenerica.class);
        if (respuesta.isResultado()) {
            politicaRepository.deleteById(nclPolitica.getPoliticaId());
            assertTrue(respuesta.isResultado());
            assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
            LOG.info("Respuesta {}", respuesta.getDatos());
        }
    }

    @Test
    public void testGuardarEstandar() {
        NclPolitica nclPolitica = new NclPolitica();
        nclPolitica.setPoliticaId(99998);
        nclPolitica.setNombre("testEstandar");
        nclPolitica.setCentrocClienteId(new NclCentrocCliente());
        nclPolitica.getCentrocClienteId().setCentrocClienteId(1);
        nclPolitica.setDiasEconomicos(12);
        nclPolitica.setNombreCorto("testing");
        /**
        nclPolitica.setCalculoAntiguedadId(new CatCalculoAntiguedad());
        nclPolitica.getCalculoAntiguedadId().setCalculoAntiguedadxId(Short.valueOf("1"));
        */
         nclPolitica.setEsEstandar(Constantes.ESTATUS_ACTIVO);
        final RespuestaGenerica respuesta = cliente.toBlocking().retrieve(HttpRequest.PUT("/guardarEstandar", nclPolitica),
                RespuestaGenerica.class);
        if (respuesta.isResultado()) {
            politicaRepository.deleteById(nclPolitica.getPoliticaId());
            assertTrue(respuesta.isResultado());
            assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
            LOG.info("Respuesta {}", respuesta.getDatos());
        }
    }

    @Test
    public void testModificar() {
        NclPolitica nclPolitica = new NclPolitica();
        nclPolitica.setPoliticaId(99999);
        nclPolitica.setNombre("test");
        nclPolitica.setCentrocClienteId(new NclCentrocCliente());
        nclPolitica.getCentrocClienteId().setCentrocClienteId(1);
        nclPolitica.setDiasEconomicos(12);
        nclPolitica.setNombreCorto("testing");
        /**
        nclPolitica.setCalculoAntiguedadId(new CatCalculoAntiguedad());
        nclPolitica.getCalculoAntiguedadId().setCalculoAntiguedadxId(Short.valueOf("1"));
        */
         RespuestaGenerica respuestaGuardar = cliente.toBlocking().retrieve(HttpRequest.PUT("/guardar", nclPolitica),
                RespuestaGenerica.class);
        if (respuestaGuardar.isResultado()) {
            nclPolitica.setPoliticaId(99999);
            nclPolitica.setNombre("testModificar");
            nclPolitica.setCentrocClienteId(new NclCentrocCliente());
            nclPolitica.getCentrocClienteId().setCentrocClienteId(1);
            nclPolitica.setDiasEconomicos(12);
            nclPolitica.setNombreCorto("modificar");
            /**
            nclPolitica.setCalculoAntiguedadId(new CatCalculoAntiguedad());
            nclPolitica.getCalculoAntiguedadId().setCalculoAntiguedadxId(Short.valueOf("1"));
            */
             final RespuestaGenerica respuesta = cliente.toBlocking().retrieve(HttpRequest.POST("/modificar", nclPolitica),
                    RespuestaGenerica.class);
            if (respuesta.isResultado()) {
                politicaRepository.deleteById(nclPolitica.getPoliticaId());
                assertTrue(respuesta.isResultado());
                assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
                LOG.info("Respuesta {}", respuesta.getDatos());
            }
        }
    }

    @Test
    public void testEliminar() {
        NclPolitica nclPolitica = new NclPolitica();
        nclPolitica.setPoliticaId(157);
        nclPolitica.setCentrocClienteId(new NclCentrocCliente());
        nclPolitica.getCentrocClienteId().setCentrocClienteId(1);
        final RespuestaGenerica respuestaEliminar = cliente.toBlocking().retrieve(HttpRequest.POST("/eliminar", nclPolitica),
                RespuestaGenerica.class);
        politicaRepository.deleteById(nclPolitica.getPoliticaId());
        assertTrue(respuestaEliminar.isResultado());
        assertTrue(Constantes.EXITO.equals(respuestaEliminar.getMensaje()));
        LOG.info("Respuesta {}", respuestaEliminar.getDatos());
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
