package mx.com.ga.cosmonaut.empresa;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import mx.com.ga.cosmonaut.common.dto.HorarioJornadaDto;
import mx.com.ga.cosmonaut.common.dto.JornadasDto;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.entity.catalogo.negocio.CatSumaHorasJornada;
import mx.com.ga.cosmonaut.common.entity.catalogo.sat.CsTipoJornada;
import mx.com.ga.cosmonaut.common.entity.cliente.NclCentrocCliente;
import mx.com.ga.cosmonaut.common.entity.cliente.NclJornada;
import mx.com.ga.cosmonaut.common.repository.catalogo.negocio.CatSumaHorasJornadaRepository;
import mx.com.ga.cosmonaut.common.repository.cliente.NclHorarioJornadaRepository;
import mx.com.ga.cosmonaut.common.repository.cliente.NclJornadaRepository;
import mx.com.ga.cosmonaut.common.repository.colaborador.NcoContratoColaboradorRepository;
import mx.com.ga.cosmonaut.common.repository.nativo.JornadasRepository;
import mx.com.ga.cosmonaut.common.util.Constantes;
import org.junit.jupiter.api.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
public class JornadasTests {

    private static final Logger LOG = LoggerFactory.getLogger(JornadasTests.class);

    @Inject
    @Client("/jornadas")
    protected RxHttpClient cliente;

    @Inject
    protected JornadasRepository jornadasRepository;

    @Inject
    protected NclJornadaRepository nclJornadaRepository;

    @Inject
    protected NclHorarioJornadaRepository nclHorarioJornadaRepository;

    @Inject
    protected NcoContratoColaboradorRepository ncoContratoColaboradorRepository;

    @Inject
    protected CatSumaHorasJornadaRepository catSumaHorasJornadaRepository;

    @Test
    public void testObtieneJornadasXEmpresa() {
        final RespuestaGenerica respuesta = cliente.toBlocking().retrieve(HttpRequest.GET("/listar/jornada/" + 1),
                RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    public void testConsultaEmpleadosJornadaEmpresa() {
        final RespuestaGenerica respuesta = cliente.toBlocking().retrieve(HttpRequest.GET("/listar/" + 1 + "/" + 1),
                RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    public void testConsultaJornadaXEmpresa() {
        final RespuestaGenerica respuesta = cliente.toBlocking().retrieve(HttpRequest.GET("/listar/jornada/" + 1 + "/" + 1),
                RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    public void testGuardarModificar() {
        JornadasDto jornada = new JornadasDto();
        List<HorarioJornadaDto> listaHorario = new ArrayList<>();
        HorarioJornadaDto horarios = new HorarioJornadaDto();
        horarios.setDia(1);
        horarios.setHorarioJornadaId(999);
        horarios.setHoraEntrada("00:00:00");
        horarios.setHoraInicioComida("00:00:00");
        horarios.setHoraFinComida("00:00:00");
        horarios.setHoraSalida("00:00:00");
        horarios.setNclJornada(new NclJornada());
        horarios.getNclJornada().setJornadaId(999);
        horarios.setTipoJornadaId(new CsTipoJornada());
        horarios.getTipoJornadaId().setTipoJornadaId("01");
        horarios.setEsActivo(true);
        listaHorario.add(horarios);
        jornada.setJornadaId(999);
        jornada.setNombre("TEST JORNADA");
        jornada.setMismoHorario(false);
        jornada.setHorarioComida(true);
        jornada.setHoraEntrada("00:00:00");
        jornada.setHoraInicioComida("00:00:00");
        jornada.setHoraFinComida("00:00:00");
        jornada.setHoraSalida("00:00:00");
        jornada.setEsActivo(true);
        jornada.setCentrocClienteId(new NclCentrocCliente());
        jornada.getCentrocClienteId().setCentrocClienteId(1);
        jornada.setTipoJornadaId(new CsTipoJornada());
        jornada.getTipoJornadaId().setTipoJornadaId("01");
        jornada.setSumaHorasJornadaId(new CatSumaHorasJornada());
        jornada.getSumaHorasJornadaId().setSumaHorasJornadaId(1);
        jornada.setNclHorarioJornada(listaHorario);
        final RespuestaGenerica respuestaGuardar = cliente.toBlocking().retrieve(HttpRequest.PUT("/guardar", jornada),
                RespuestaGenerica.class);
        if (respuestaGuardar.isResultado()) {
            jornada.setNombre("TEST MODIFICAR");
            final RespuestaGenerica respuesta = cliente.toBlocking().retrieve(HttpRequest.POST("/modificar", jornada),
                    RespuestaGenerica.class);
            if (respuesta.isResultado()) {
                nclJornadaRepository.deleteById(999);
                nclHorarioJornadaRepository.deleteById(999);
                assertTrue(respuesta.isResultado());
                assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
                LOG.info("Respuesta {}", respuesta.getDatos());

            }
        }
    }

    @Test
    public void testGuardarEliminar() {
        JornadasDto jornada = new JornadasDto();
        List<HorarioJornadaDto> listaHorario = new ArrayList<>();
        HorarioJornadaDto horarios = new HorarioJornadaDto();
        horarios.setDia(1);
        horarios.setHorarioJornadaId(998);
        horarios.setHoraEntrada("00:00:00");
        horarios.setHoraInicioComida("00:00:00");
        horarios.setHoraFinComida("00:00:00");
        horarios.setHoraSalida("00:00:00");
        horarios.setNclJornada(new NclJornada());
        horarios.getNclJornada().setJornadaId(998);
        horarios.setTipoJornadaId(new CsTipoJornada());
        horarios.getTipoJornadaId().setTipoJornadaId("01");
        horarios.setEsActivo(true);
        listaHorario.add(horarios);
        jornada.setJornadaId(998);
        jornada.setNombre("TEST ELIMINAR");
        jornada.setMismoHorario(false);
        jornada.setHorarioComida(true);
        jornada.setHoraEntrada("00:00:00");
        jornada.setHoraInicioComida("00:00:00");
        jornada.setHoraFinComida("00:00:00");
        jornada.setHoraSalida("00:00:00");
        jornada.setEsActivo(true);
        jornada.setCentrocClienteId(new NclCentrocCliente());
        jornada.getCentrocClienteId().setCentrocClienteId(1);
        jornada.setTipoJornadaId(new CsTipoJornada());
        jornada.getTipoJornadaId().setTipoJornadaId("01");
        jornada.setSumaHorasJornadaId(new CatSumaHorasJornada());
        jornada.getSumaHorasJornadaId().setSumaHorasJornadaId(1);
        jornada.setNclHorarioJornada(listaHorario);
        final RespuestaGenerica respuestaGuardar = cliente.toBlocking().retrieve(HttpRequest.PUT("/guardar", jornada),
                RespuestaGenerica.class);
        if (respuestaGuardar.isResultado()) {
            NclJornada nclJornada = new NclJornada();
            nclJornada.setJornadaId(998);
            nclJornada.setCentrocClienteId(new NclCentrocCliente());
            nclJornada.getCentrocClienteId().setCentrocClienteId(1);
            final RespuestaGenerica respuesta;
            respuesta = cliente.toBlocking().retrieve(HttpRequest.POST("/eliminar", nclJornada),
                    RespuestaGenerica.class);
            assertTrue(respuesta.isResultado());
            assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
            LOG.info("Respuesta {}", respuesta.getDatos());
        }
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
