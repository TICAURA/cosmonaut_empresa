package mx.com.ga.cosmonaut.empresa;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import mx.com.ga.cosmonaut.common.dto.NclAreaDto;
import mx.com.ga.cosmonaut.common.dto.NclPuestoDto;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.entity.cliente.NclCentrocCliente;
import mx.com.ga.cosmonaut.common.entity.cliente.NclEmpleadoXArea;
import mx.com.ga.cosmonaut.common.repository.cliente.NclAreaRepository;
import mx.com.ga.cosmonaut.common.repository.cliente.NclPuestoRepository;
import mx.com.ga.cosmonaut.common.repository.cliente.NclPuestoXareaRepository;
import mx.com.ga.cosmonaut.common.util.Constantes;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@MicronautTest
public class AreasPuestosTests {


    private static final Logger LOG = LoggerFactory.getLogger(AreasPuestosTests.class);
    
    @Inject
    @Client("/area")
    protected RxHttpClient cliente;

    @Inject
    protected NclAreaRepository areaRepository;
    @Inject
    protected NclPuestoRepository puestoRepository;
    @Inject
    protected NclPuestoXareaRepository  nclPuestoXareaRepository;
    
    @Test
    public void testGuardar(){
        NclPuestoDto puesto = new NclPuestoDto();
        NclAreaDto area = new NclAreaDto();
        area.setAreaId(999999);
        area.setDescripcion("area-Test");
        area.setCentrocClienteId(1);
        area.setNombreCorto("area-Test");
        List<NclPuestoDto> lista = new ArrayList<>();
        puesto.setPuestoId(999999);
        puesto.setDescripcion("puestoTest");
        puesto.setNombreCorto("puestoTest");
        puesto.setCentrocClienteId(1);
        lista.add(puesto);
        area.setNclPuestoDto(lista);
        final RespuestaGenerica respuesta
                = cliente.toBlocking().retrieve(HttpRequest.PUT("/guardar", area),
                        RespuestaGenerica.class);
        if (respuesta.isResultado()) {
            nclPuestoXareaRepository.deleteById(area.getAreaId());
            puestoRepository.deleteById(puesto.getPuestoId());
            areaRepository.deleteById(area.getAreaId());
            assertTrue(respuesta.isResultado());
            assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
            LOG.info("Respuesta {}", respuesta.getDatos());
        }
    }

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
    public void testListarPuestosTodos() {
        final RespuestaGenerica respuesta
                = cliente.toBlocking().retrieve(HttpRequest.GET("/listar/puestos/todos"),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }
    
    @Test
    public void testObtenerPuestoPorId() {
        final RespuestaGenerica respuesta
                = cliente.toBlocking().retrieve(HttpRequest.GET("/obtener/id/" + 1),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }
    
    @Test
    public void testObtenerPuestoPorIdEmpresa() {
        final RespuestaGenerica respuesta
                = cliente.toBlocking().retrieve(HttpRequest.GET("/obtener/cliente/id/" + 1),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }
    
    @Test
    public void testObtenerPuestoPorIdEmpresaIdArea() {
        final RespuestaGenerica respuesta
                = cliente.toBlocking().retrieve(HttpRequest.GET("/obtener/cliente/area/"  + 1 + "/" + 1),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }
    
    @Test
    public void testObtenerPuestoPorIdArea() {
        final RespuestaGenerica respuesta
                = cliente.toBlocking().retrieve(HttpRequest.GET("/obtener/puestos/" + 1),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }
    
    
    @Test
    public void testObtenerEmpleadosArea() {
        final RespuestaGenerica respuesta
                = cliente.toBlocking().retrieve(HttpRequest.GET("/listar/areas/" + 1),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }
    
    @Test
    public void testObtenerAreasPorEmpresa() {
        final RespuestaGenerica respuesta
                = cliente.toBlocking().retrieve(HttpRequest.GET("/obtener/idCliente/" + 1),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }
    
    @Test
    public void testObtenerPuestoPorEmpleado() {
        final RespuestaGenerica respuesta
                = cliente.toBlocking().retrieve(HttpRequest.GET("/obtener/empleado/" + 1 + "/" + 1),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }
    
    @Test
    public void testObtenerAreaPorId() {
        final RespuestaGenerica respuesta
                = cliente.toBlocking().retrieve(HttpRequest.GET("/obtener/idArea/" + 1),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }
    
    
    @Test
    public void testListaDinamicaEmpresa() {
        NclEmpleadoXArea nclEmpleadoXArea = new NclEmpleadoXArea();
        nclEmpleadoXArea.setAreaId(1);
        nclEmpleadoXArea.setNclCentrocCliente(new NclCentrocCliente());
        nclEmpleadoXArea.getNclCentrocCliente().setCentrocClienteId(1);
        nclEmpleadoXArea.setEsActivo(true);
        nclEmpleadoXArea.setCount(1);
        nclEmpleadoXArea.setNombreCorto("alta");
        final RespuestaGenerica respuesta
                = cliente.toBlocking().retrieve(HttpRequest.POST("/lista/dinamica" , nclEmpleadoXArea),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }
    
    @Test
    public void testModificar(){
        List<NclPuestoDto> listaNclPuestoDto = new ArrayList();
        NclPuestoDto nclPuestoDto = new NclPuestoDto();
        nclPuestoDto.setPuestoId(1);
        nclPuestoDto.setDescripcion("testModificar");
        nclPuestoDto.setCentrocClienteId(1);
        listaNclPuestoDto.add(nclPuestoDto);
        NclAreaDto nclAreaDto = new NclAreaDto();
        nclAreaDto.setAreaId(1);
        nclAreaDto.setDescripcion("testModificar");
        nclAreaDto.setCentrocClienteId(1);
        nclAreaDto.setNclPuestoDto(listaNclPuestoDto);
         final RespuestaGenerica respuesta
                = cliente.toBlocking().retrieve(HttpRequest.POST("/modificar" , nclAreaDto),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
        
    }
    
    @Test
    public void testEliminar(){
        List<NclPuestoDto> listaNclPuestoDto = new ArrayList();
        NclPuestoDto nclPuestoDto = new NclPuestoDto();
        nclPuestoDto.setPuestoId(1);
        nclPuestoDto.setCentrocClienteId(1);
        listaNclPuestoDto.add(nclPuestoDto);
        NclAreaDto nclAreaDto = new NclAreaDto();
        nclAreaDto.setAreaId(1);
        nclAreaDto.setCentrocClienteId(1);
         final RespuestaGenerica respuesta
                = cliente.toBlocking().retrieve(HttpRequest.POST("/eliminar" , nclAreaDto),
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
