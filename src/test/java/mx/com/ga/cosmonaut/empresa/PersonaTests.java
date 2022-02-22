package mx.com.ga.cosmonaut.empresa;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import mx.com.ga.cosmonaut.common.dto.*;
import mx.com.ga.cosmonaut.common.entity.catalogo.negocio.CatTipoPersona;
import mx.com.ga.cosmonaut.common.entity.catalogo.negocio.CatTipoRepresentante;
import mx.com.ga.cosmonaut.common.entity.catalogo.ubicacion.CatNacionalidad;
import mx.com.ga.cosmonaut.common.entity.cliente.NclCentrocCliente;
import mx.com.ga.cosmonaut.common.util.Constantes;
import org.junit.jupiter.api.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
public class PersonaTests {

    private static final Logger LOG = LoggerFactory.getLogger(PersonaTests.class);

    @Inject
    @Client("/persona")
    private RxHttpClient cliente;

    @Test
    public void testGuardarContactoInicial() {
        NcoPersonaDto personaDTO = new NcoPersonaDto();
        personaDTO.setNombre("Karen Victoria");
        personaDTO.setApellidoPaterno("Noguez");
        personaDTO.setApellidoMaterno("Vazquez");
        personaDTO.setCurp("SOOJ890306HDFLRL00");
        personaDTO.setEmailCorporativo(generaCorreo());
        personaDTO.setContactoInicialEmailPersonal(generaCorreo());
        personaDTO.setContactoInicialTelefono(5566778899L);
        personaDTO.setCentrocClienteId(new NclCentrocCliente());
        personaDTO.getCentrocClienteId().setCentrocClienteId(1);
        personaDTO.setEstadoCivil(null);
        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.PUT("/guardar/contacto/inicial",personaDTO),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    public void testGuardarRepresentanteLegal() {
        NcoPersonaDto personaDTO = new NcoPersonaDto();
        personaDTO.setNombre("Karen");
        personaDTO.setApellidoPaterno("Noguez");
        personaDTO.setApellidoMaterno("Vazquez");
        personaDTO.setCurp("SOOJ890306HDFLRL00");
        personaDTO.setEmailCorporativo(generaCorreo());
        personaDTO.setContactoInicialEmailPersonal(generaCorreo());
        personaDTO.setContactoInicialTelefono(5566778899L);
        personaDTO.setCentrocClienteId(new NclCentrocCliente());
        personaDTO.getCentrocClienteId().setCentrocClienteId(1);
        personaDTO.setNacionalidadId(new CatNacionalidad());
        personaDTO.getNacionalidadId().setNacionalidadId(1L);
        personaDTO.setTipoRepresentanteId(new CatTipoRepresentante());
        personaDTO.getTipoRepresentanteId().setTipoRepresentanteId((short)1);
        

        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.PUT("/guardar/representanteLegal",personaDTO),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    public void testGuardarUsuario() {
        NcoPersonaDto personaDTO = new NcoPersonaDto();
        personaDTO.setNombre("Karen");
        personaDTO.setApellidoPaterno("Noguez");
        personaDTO.setApellidoMaterno("Vazquez");
        personaDTO.setCurp("SOOJ890306HDFLRL00");
        personaDTO.setEmailCorporativo(generaCorreo());
        personaDTO.setContactoInicialEmailPersonal(generaCorreo());
        personaDTO.setContactoInicialTelefono(5564778899L);
        personaDTO.setTipoPersonaId(new CatTipoPersona());
        personaDTO.getTipoPersonaId().setTipoPersonaId(3);
        personaDTO.setCentrocClienteId(new NclCentrocCliente());
        personaDTO.getCentrocClienteId().setCentrocClienteId(1);
        

        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.PUT("/guardar/usuario",personaDTO),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    public void testGuardarContactoRH() {
        NcoPersonaDto personaDTO = new NcoPersonaDto();
        personaDTO.setNombre("Karen");
        personaDTO.setApellidoPaterno("Noguez");
        personaDTO.setApellidoMaterno("Vazquez");
        personaDTO.setCurp("SOOJ890306HDFLRL00");
        personaDTO.setEmailCorporativo(generaCorreo());
        personaDTO.setContactoInicialEmailPersonal(generaCorreo());
        personaDTO.setContactoInicialTelefono(5566778899L);
        personaDTO.setTipoPersonaId(new CatTipoPersona());
        personaDTO.getTipoPersonaId().setTipoPersonaId(4);
        personaDTO.setCentrocClienteId(new NclCentrocCliente());
        personaDTO.getCentrocClienteId().setCentrocClienteId(1);
        

        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.PUT("/guardar/contacto/recursosHumanos",personaDTO),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }
    
    @Test
    public void testModificarContactoInicial() {
        NcoPersonaDto personaDTO = new NcoPersonaDto();
        personaDTO.setPersonaId(14);
        personaDTO.setNombre("Karen");
        personaDTO.setApellidoPaterno("Noguez");
        personaDTO.setApellidoMaterno("Vazquez");
        personaDTO.setCurp("SOOJ890306HDFLRL00");
        personaDTO.setEmailCorporativo("karenvic@advisoryservicesg.tech");
        personaDTO.setContactoInicialEmailPersonal("karenvic@gmail.com");
        personaDTO.setContactoInicialTelefono(5566778899L);
        personaDTO.setCentrocClienteId(new NclCentrocCliente());
        personaDTO.getCentrocClienteId().setCentrocClienteId(1);
        
        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.POST("/modificar/contacto/inicial",personaDTO),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    public void testModificarRepresentanteLegal() {
        NcoPersonaDto personaDTO = new NcoPersonaDto();
        personaDTO.setPersonaId(14);
        personaDTO.setNombre("Karen");
        personaDTO.setApellidoPaterno("Noguez");
        personaDTO.setApellidoMaterno("Vazquez");
        personaDTO.setCurp("SOOJ890306HDFLRL00");
        personaDTO.setEmailCorporativo("karenvic@advisoryservicesg.tech");
        personaDTO.setContactoInicialEmailPersonal("karenvic@gmail.com");
        personaDTO.setContactoInicialTelefono(5566778899L);
        personaDTO.setCentrocClienteId(new NclCentrocCliente());
        personaDTO.getCentrocClienteId().setCentrocClienteId(1);
        personaDTO.setNacionalidadId(new CatNacionalidad());
        personaDTO.getNacionalidadId().setNacionalidadId(1L);
        personaDTO.setTipoRepresentanteId(new CatTipoRepresentante());
        personaDTO.getTipoRepresentanteId().setTipoRepresentanteId((short)1);
        

        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.POST("/modificar/representanteLegal",personaDTO),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    public void testModificarUsuario() {
        NcoPersonaDto personaDTO = new NcoPersonaDto();
        personaDTO.setPersonaId(14);
        personaDTO.setNombre("Karen");
        personaDTO.setApellidoPaterno("Noguez");
        personaDTO.setApellidoMaterno("Vazquez");
        personaDTO.setCurp("SOOJ890306HDFLRL00");
        personaDTO.setEmailCorporativo("karenvic@advisoryservicesg.tech");
        personaDTO.setContactoInicialEmailPersonal("karenvic@gmail.com");
        personaDTO.setContactoInicialTelefono(5566778899L);
        personaDTO.setTipoPersonaId(new CatTipoPersona());
        personaDTO.getTipoPersonaId().setTipoPersonaId(3);
        personaDTO.setCentrocClienteId(new NclCentrocCliente());
        personaDTO.getCentrocClienteId().setCentrocClienteId(1);
        

        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.POST("/modificar/usuario",personaDTO),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    public void testModificarContactoRH() {
        NcoPersonaDto personaDTO = new NcoPersonaDto();
        personaDTO.setPersonaId(14);
        personaDTO.setNombre("Karen");
        personaDTO.setApellidoPaterno("Noguez");
        personaDTO.setApellidoMaterno("Vazquez");
        personaDTO.setCurp("SOOJ890306HDFLRL00");
        personaDTO.setEmailCorporativo("karenvic@advisoryservicesg.tech");
        personaDTO.setContactoInicialEmailPersonal("karenvic@gmail.com");
        personaDTO.setContactoInicialTelefono(5566778899L);
        personaDTO.setTipoPersonaId(new CatTipoPersona());
        personaDTO.getTipoPersonaId().setTipoPersonaId(4);
        personaDTO.setCentrocClienteId(new NclCentrocCliente());
        personaDTO.getCentrocClienteId().setCentrocClienteId(1);
        

        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.POST("/modificar/contactoRH",personaDTO),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    public void testEliminarId() {
        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.POST("/eliminar/id/"+1,""),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    public void testObtenerId() {
        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.GET("/obtener/id/" + 447),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    public void testObtenerIdCompania() {
        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.GET("/obtener/id/compania/" + 2),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    public void testListarTodos() {
        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.GET("/lista/todo/" + 3),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    public void testListaCompaniaPersona() {
        NcoPersonaDto personaDTO = new NcoPersonaDto();
        personaDTO.setTipoPersonaId(new CatTipoPersona());
        personaDTO.getTipoPersonaId().setTipoPersonaId(3);
        personaDTO.setCentrocClienteId(new NclCentrocCliente());
        personaDTO.getCentrocClienteId().setCentrocClienteId(2);

        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.POST("/lista/compania/tipoPersona",personaDTO),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    public void testListaDinamica() {
        NcoPersonaDto personaDTO = new NcoPersonaDto();
        personaDTO.setNombre("Julio");
        personaDTO.setTipoPersonaId(new CatTipoPersona());
        personaDTO.getTipoPersonaId().setTipoPersonaId(4);

        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.POST("/lista/dinamica/",personaDTO),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    private String generaCorreo(){
        return generaNumeroAleatorio() + generaStringAleatorio() + generaNumeroAleatorio() + "@gmail.com";
    }

    private Integer generaNumeroAleatorio(){
        Random rand = new Random();
        return rand.nextInt(99);
    }

    private String generaStringAleatorio(){
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder(30);
        for (int i = 0; i < 1; i++) {
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());
            sb.append(AlphaNumericString
                    .charAt(index));
        }
        return sb.toString();
    }

    @Test
    public void validaFechaNss() throws ParseException {
        String sFecha="18/02/1984";
        Date fecha = new SimpleDateFormat("dd/MM/yyyy").parse(sFecha);
        String nss = "93998484492";
        RespuestaGenerica respuesta = new RespuestaGenerica(null,Constantes.RESULTADO_EXITO,Constantes.EXITO);
        Calendar fechaNacimiento = Calendar.getInstance();
        fechaNacimiento.setTime(fecha);
        Integer anioNacimiento = fechaNacimiento.get(Calendar.YEAR);
        String sAnioNacimiento = anioNacimiento.toString().substring(2,4);
        anioNacimiento = Integer.valueOf(sAnioNacimiento);
        String sAnioNss =  nss.substring(4,6);
        Integer anioNss= Integer.valueOf(sAnioNss);
        if (anioNacimiento != anioNss){
            respuesta.setResultado(Constantes.RESULTADO_ERROR);
            respuesta.setMensaje("Error Fecha.");
        }
        assertTrue(respuesta.isResultado());
    }

    @Test
    public void validaDigitoValidadorNss() {

        String nss = "34189076432";

        RespuestaGenerica respuesta = new RespuestaGenerica(null,Constantes.RESULTADO_EXITO,Constantes.EXITO);
        String sDigitoValidador = nss.substring(nss.length() - 1, nss.length());
        nss = nss.substring(0, nss.length()-1);

        Integer digitoValidador = Integer.valueOf(sDigitoValidador);
        String sNumero = "";
        Integer digito = 0;
        for (int i = 0; i < nss.length(); i++){
            sNumero = String.valueOf(nss.charAt(i));
            Integer numero = Integer.valueOf(sNumero);
            if (i % 2 == 0){
                numero = numero * 1;
            }else {
                numero = numero * 2;
            }
            sNumero = numero.toString();
            if (sNumero.length() != 1){
                String sNumeroUno = String.valueOf(sNumero.charAt(0));
                Integer numUno = Integer.valueOf(sNumeroUno);
                String sNumeroDos = String.valueOf(sNumero.charAt(1));
                Integer numDos = Integer.valueOf(sNumeroDos);
                numero = numUno + numDos;
            }
            digito = digito + numero;
        }
        String sDigito = digito.toString();
        sNumero = String.valueOf(sDigito.charAt(1));
        digito = Integer.valueOf(sNumero);
        digito = 10 - digito;

        if (digitoValidador != digito){
            respuesta.setResultado(Constantes.RESULTADO_ERROR);
            respuesta.setMensaje("Error Digito.");
        }

        assertTrue(respuesta.isResultado());
    }
}
