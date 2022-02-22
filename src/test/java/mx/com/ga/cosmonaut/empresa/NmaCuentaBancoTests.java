package mx.com.ga.cosmonaut.empresa;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import javax.inject.Inject;
import mx.com.ga.cosmonaut.common.dto.CsBancoDto;
import mx.com.ga.cosmonaut.common.dto.NclCentrocClienteDto;
import mx.com.ga.cosmonaut.common.dto.NmaCuentaBancoDto;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.entity.administracion.NmaCuentaBanco;
import mx.com.ga.cosmonaut.common.repository.administracion.NmaCuentaBancoRepository;
import mx.com.ga.cosmonaut.common.util.Constantes;
import org.junit.jupiter.api.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
public class NmaCuentaBancoTests {

    private static final Logger LOG = LoggerFactory.getLogger(NmaCuentaBancoTests.class);

    @Inject
    @Client("/cuentaBanco")
    protected RxHttpClient cliente;

    @Inject
    protected NmaCuentaBancoRepository repository;

    @Test
    public void testListarTodos() {
        final RespuestaGenerica respuesta
                = cliente.toBlocking().retrieve(HttpRequest.GET("/listar/todos"),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    public void testGuardar() {
        NmaCuentaBancoDto nmaCuentaBancoDto = new NmaCuentaBancoDto();
        NmaCuentaBanco nmaCuentaBanco = new NmaCuentaBanco();
        nmaCuentaBancoDto.setClabe("012555555555555556");
        nmaCuentaBancoDto.setNumeroCuenta("5555555556");
        nmaCuentaBancoDto.setNombreCuenta("TestGuardar");
        nmaCuentaBancoDto.setBancoId(new CsBancoDto());
        nmaCuentaBancoDto.getBancoId().setBancoId(Long.valueOf(4));
        nmaCuentaBancoDto.setNclCentrocCliente(new NclCentrocClienteDto());
        nmaCuentaBancoDto.getNclCentrocCliente().setCentrocClienteId(1);

        final RespuestaGenerica respuesta = cliente.toBlocking().retrieve(HttpRequest.PUT("/guardar", nmaCuentaBancoDto),
                RespuestaGenerica.class);
        if (respuesta.isResultado()) {
            NmaCuentaBanco cuenta = repository.findByNumeroCuenta(nmaCuentaBancoDto.getNumeroCuenta());
            repository.delete(cuenta);
            assertTrue(respuesta.isResultado());
            assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
            LOG.info("Respuesta {}", respuesta.getDatos());
        }
    }

    @Test
    public void testModificar() {

        NmaCuentaBancoDto nmaCuentaBancoDto = new NmaCuentaBancoDto();
        nmaCuentaBancoDto.setCuentaBancoId(57);
        nmaCuentaBancoDto.setClabe("012555555555555556");
        nmaCuentaBancoDto.setNumeroCuenta("5555555556");
        nmaCuentaBancoDto.setNombreCuenta("TestModificar");
        nmaCuentaBancoDto.setEsActivo(Constantes.ESTATUS_ACTIVO);
        nmaCuentaBancoDto.setBancoId(new CsBancoDto());
        nmaCuentaBancoDto.getBancoId().setBancoId(Long.valueOf(4));
        nmaCuentaBancoDto.setNclCentrocCliente(new NclCentrocClienteDto());
        nmaCuentaBancoDto.getNclCentrocCliente().setCentrocClienteId(1);
        final RespuestaGenerica respuesta
                = cliente.toBlocking().retrieve(HttpRequest.POST("/modificar", nmaCuentaBancoDto),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    public void testObtenerId() {
        final RespuestaGenerica respuesta
                = cliente.toBlocking().retrieve(HttpRequest.GET("/obtener/id/" + "5555555556"),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    public void testGuardarSTP() {
        NmaCuentaBancoDto nmaCuentaBancoDto = new NmaCuentaBancoDto();
        NmaCuentaBanco nmaCuentaBanco = new NmaCuentaBanco();
        nmaCuentaBancoDto.setUsaStp(Constantes.ESTATUS_ACTIVO);
        nmaCuentaBancoDto.setClabeStp("012555555555555556");
        nmaCuentaBancoDto.setCuentaStp("555555555555556");
        nmaCuentaBancoDto.setNombreCuenta("TestGuardarSTP");
        nmaCuentaBancoDto.setBancoId(new CsBancoDto());
        nmaCuentaBancoDto.getBancoId().setBancoId(Long.valueOf(4));
        nmaCuentaBancoDto.setNclCentrocCliente(new NclCentrocClienteDto());
        nmaCuentaBancoDto.getNclCentrocCliente().setCentrocClienteId(1);

        final RespuestaGenerica respuesta = cliente.toBlocking().retrieve(HttpRequest.PUT("/guardarSTP", nmaCuentaBancoDto),
                RespuestaGenerica.class);
        if (respuesta.isResultado()) {
            nmaCuentaBancoDto = (NmaCuentaBancoDto) respuesta.getDatos();
            NmaCuentaBanco cuenta = repository.findById(Long.valueOf(nmaCuentaBancoDto.getCuentaBancoId())).orElse(null);
            repository.deleteById(Long.valueOf(cuenta.getCuentaBancoId()));
            assertTrue(respuesta.isResultado());
            assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
            LOG.info("Respuesta {}", respuesta.getDatos());
        }
    }

    @Test
    public void testModificarSTP() {

        NmaCuentaBancoDto nmaCuentaBancoDto = new NmaCuentaBancoDto();
        NmaCuentaBanco nmaCuentaBanco = new NmaCuentaBanco();
        nmaCuentaBancoDto.setUsaStp(Constantes.ESTATUS_ACTIVO);
        nmaCuentaBancoDto.setEsActivo(Constantes.ESTATUS_ACTIVO);
        nmaCuentaBancoDto.setCuentaBancoId(11);
        nmaCuentaBancoDto.setClabeStp("123456123333123457");
        nmaCuentaBancoDto.setCuentaStp("999102123667");
        nmaCuentaBancoDto.setNombreCuenta("TestModificarSTP");
        nmaCuentaBancoDto.setBancoId(new CsBancoDto());
        nmaCuentaBancoDto.getBancoId().setBancoId(Long.valueOf(18));
        nmaCuentaBancoDto.setNclCentrocCliente(new NclCentrocClienteDto());
        nmaCuentaBancoDto.getNclCentrocCliente().setCentrocClienteId(1);

        final RespuestaGenerica respuesta
                = cliente.toBlocking().retrieve(HttpRequest.POST("/modificarSTP", nmaCuentaBancoDto),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    public void testObtenerPersonaId() {
        final RespuestaGenerica respuesta
                = cliente.toBlocking().retrieve(HttpRequest.GET("/obtener/persona/" + 455),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }
    
    @Test
    public void testEliminar() {
        final RespuestaGenerica respuesta
                = cliente.toBlocking().retrieve(HttpRequest.POST("/eliminar/"+1,""),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }
    
    @Test
    public void testObtenerCuentaCliente() {
        final RespuestaGenerica respuesta
                = cliente.toBlocking().retrieve(HttpRequest.GET("/obtener/cliente/" + 1),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }
    
    @Test
    public void testObtenerCuentaSTPCliente() {
        final RespuestaGenerica respuesta
                = cliente.toBlocking().retrieve(HttpRequest.GET("/obtener/STP/cliente/" + 1),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }
    
     @Test
    public void testObtieneBanco() {
        final RespuestaGenerica respuesta
                = cliente.toBlocking().retrieve(HttpRequest.GET("/obtieneBanco/" + "012326272789172872"),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    public void testFindByEsActivo() {
        final RespuestaGenerica respuesta
                = cliente.toBlocking().retrieve(HttpRequest.GET("/listar/todosActivo/" + Boolean.TRUE),
                RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }
}
