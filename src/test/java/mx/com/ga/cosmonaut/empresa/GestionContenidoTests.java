package mx.com.ga.cosmonaut.empresa;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.dto.cms.*;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.common.service.GestionContenidoService;
import mx.com.ga.cosmonaut.common.util.Utilidades;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
public class GestionContenidoTests {

    private static final Logger LOG = LoggerFactory.getLogger(CentroCostosClienteTests.class);

    @Inject
    private GestionContenidoService gestionContenidoService;

    @Test
    public void testObtenerCatalogoMultimedios() throws ServiceException {
        RespuestaGenerica respuestaGenerica = gestionContenidoService.obtenerCatalogoMultimedios();
        LOG.info("Respuesta {}", respuestaGenerica.getDatos());
        assertTrue(respuestaGenerica.isResultado());
    }

    @Test
    public void testObtenerCatalogoTipoDocumento() throws ServiceException {
        RespuestaGenerica respuestaGenerica = gestionContenidoService.obtenerCatalogoTipoDocumento();
        LOG.info("Respuesta {}", respuestaGenerica.getDatos());
        assertTrue(respuestaGenerica.isResultado());
    }

    @Test
    public void testGuardarCatalogoTipoDocumento() throws ServiceException {
        Entradas entradas = new Entradas();
        Datos datos = new Datos();
        TipoDocumento tipoDocumento = new TipoDocumento();
        tipoDocumento.setAcronimo("RFC");
        tipoDocumento.setDescripcion("RFC");
        tipoDocumento.setNombre("RFC");
        datos.setData(tipoDocumento);
        entradas.setEntrada(datos);
        RespuestaGenerica respuestaGenerica = gestionContenidoService.guardarCatalogoTipoDocumento(entradas);
        LOG.info("Respuesta {}", respuestaGenerica.getDatos());
        assertTrue(respuestaGenerica.isResultado());
    }

    @Test
    public void testEliminarCatalogoTipoDocumento() throws ServiceException {
        Entradas entradas = new Entradas();
        Datos datos = new Datos();
        TipoDocumento tipoDocumento = new TipoDocumento();
        tipoDocumento.setId(5);
        datos.setData(tipoDocumento);
        entradas.setEntrada(datos);
        RespuestaGenerica respuestaGenerica = gestionContenidoService.eliminarCatalogoTipoDocumento(entradas);
        LOG.info("Respuesta {}", respuestaGenerica.getDatos());
        assertTrue(respuestaGenerica.isResultado());
    }

    @Test
    public void testGuardarExpediente() throws ServiceException {
        Datos datos = new Datos();
        Expediente expediente = new Expediente();
        expediente.setClave("1-448-SOOA890306GG2");
        datos.setData(expediente);
        RespuestaGenerica respuestaGenerica =  gestionContenidoService.guardarExpediente(datos);
        LOG.info("Respuesta {}", respuestaGenerica.getDatos());
        assertTrue(respuestaGenerica.isResultado());
    }

    @Test
    public void testObtenerExpediente() throws ServiceException {
        RespuestaGenerica respuestaGenerica =  gestionContenidoService.obtenerExpediente("112-10-AX");
        LOG.info("Respuesta {}", respuestaGenerica.getDatos());
        assertTrue(respuestaGenerica.isResultado());
    }

    @Test
    public void testGuardarDocumento() throws ServiceException, IOException {
        Entradas entradas = new Entradas();
        Archivos archivos = new Archivos();
        Documentos documentos = new Documentos();
        InputStream imagen = this.getClass().getResourceAsStream("/imagen.jpg");
        documentos.setContenido(Utilidades.encodeContent(new byte[imagen.available()]));
        documentos.setNombre("ImagenPrueba.jpg");
        documentos.setTipoDocto(new TipoDocumento());
        documentos.getTipoDocto().setAcronimo("I");
        documentos.setTipoMM(new TipoMultimedia());
        documentos.getTipoMM().setId(3);
        archivos.setArchivo(documentos);
        archivos.setExpediente(new Expediente());
        archivos.getExpediente().setClave("1-448-SOOA890306GG2");
        entradas.setEntrada(archivos);
        RespuestaGenerica respuestaGenerica = gestionContenidoService.guardarDocumento(entradas);
        LOG.info("Respuesta {}", respuestaGenerica.getDatos());
        assertTrue(respuestaGenerica.isResultado());
    }

    @Test
    public void testVersionarDocumento() throws ServiceException, IOException {
        Entradas entradas = new Entradas();
        Archivos archivos = new Archivos();
        Documentos documentos = new Documentos();
        InputStream imagen = this.getClass().getResourceAsStream("/imagen.jpg");
        documentos.setNombre("ImagenPrueba.jpg");
        documentos.setComentario("Prueba");
        documentos.setContenido(Utilidades.encodeContent(new byte[imagen.available()]));
        documentos.setTipoDocto(new TipoDocumento());
        documentos.getTipoDocto().setAcronimo("VAP01");
        documentos.setTipoMM(new TipoMultimedia());
        documentos.getTipoMM().setId(3);
        archivos.setArchivo(documentos);
        archivos.setOriginal(new Origen());
        archivos.getOriginal().setId(8);
        entradas.setEntrada(archivos);
        RespuestaGenerica respuestaGenerica = gestionContenidoService.versionarDocumento(entradas);
        LOG.info("Respuesta {}", respuestaGenerica.getDatos());
        assertTrue(respuestaGenerica.isResultado());
    }

    @Test
    public void testRemplazarDocumento() throws ServiceException, IOException {
        Entradas entradas = new Entradas();
        Archivos archivos = new Archivos();
        Documentos documentos = new Documentos();
        InputStream imagen = this.getClass().getResourceAsStream("/imagen.jpg");
        documentos.setNombre("ImagenPrueba.jpg");
        documentos.setComentario("Prueba");
        documentos.setContenido(Utilidades.encodeContent(new byte[imagen.available()]));
        documentos.setTipoDocto(new TipoDocumento());
        documentos.getTipoDocto().setAcronimo("VAP01");
        documentos.setTipoMM(new TipoMultimedia());
        documentos.getTipoMM().setId(3);
        archivos.setArchivo(documentos);
        archivos.setOriginal(new Origen());
        archivos.getOriginal().setId(9);
        entradas.setEntrada(archivos);
        RespuestaGenerica respuestaGenerica = gestionContenidoService.remplazarDocumento(entradas);
        LOG.info("Respuesta {}", respuestaGenerica.getDatos());
        assertTrue(respuestaGenerica.isResultado());
    }

    @Test
    public void testObtenerDocumento() throws ServiceException {
        RespuestaGenerica respuestaGenerica = gestionContenidoService.obtenerDocumento(4);
        LOG.info("Respuesta {}", respuestaGenerica.getDatos());
        assertTrue(respuestaGenerica.isResultado());
    }

}
