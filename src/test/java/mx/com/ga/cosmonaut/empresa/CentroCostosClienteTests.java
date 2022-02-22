package mx.com.ga.cosmonaut.empresa;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import mx.com.ga.cosmonaut.common.dto.NclCentrocClienteDto;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.dto.ValidacionEmpresaDto;
import mx.com.ga.cosmonaut.common.dto.cms.RespuestaGestionContenido;
import mx.com.ga.cosmonaut.common.dto.csd.CertificadoSelloDigitalRespuestaDto;
import mx.com.ga.cosmonaut.common.entity.administracion.NmaDomicilio;
import mx.com.ga.cosmonaut.common.entity.catalogo.negocio.CatBasePeriodo;
import mx.com.ga.cosmonaut.common.entity.catalogo.sat.CsRegimenFiscal;
import mx.com.ga.cosmonaut.common.entity.cliente.NclCentrocCliente;
import mx.com.ga.cosmonaut.common.entity.cliente.NclSede;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.common.repository.cliente.NclCentrocClienteRepository;
import mx.com.ga.cosmonaut.common.util.Constantes;
import mx.com.ga.cosmonaut.common.util.ObjetoMapper;
import mx.com.ga.cosmonaut.common.util.Utilidades;
import mx.com.ga.cosmonaut.common.util.Validar;
import okhttp3.*;
import org.json.JSONObject;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CentroCostosClienteTests {

    private static final Logger LOG = LoggerFactory.getLogger(CentroCostosClienteTests.class);

    @Inject
    private NclCentrocClienteRepository nclCentrocClienteRepository;

    @Inject
    @Client("/centroCostosCliente")
    private RxHttpClient cliente;

    private Integer centroClienteCompaniaId;

    private Integer centroClienteEmpresaId;

    @Test
    @Order(0)
    public void testGuardarCompania() {
        NclCentrocClienteDto centrocClienteDTO = new NclCentrocClienteDto();
        centrocClienteDTO.setNombre("Prueba-Test");
        centrocClienteDTO.setRazonSocial("Prueba-Test");
        centrocClienteDTO.setRfc(generaRfc());
        centrocClienteDTO.setRegimenfiscalId(new CsRegimenFiscal());
        centrocClienteDTO.getRegimenfiscalId().setRegimenfiscalId("601");
        centrocClienteDTO.setBasePeriodoId(new CatBasePeriodo());
        centrocClienteDTO.getBasePeriodoId().setBasePeriodoId(1);
        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.PUT("/guardar/compania",centrocClienteDTO),
                        RespuestaGenerica.class);
        if (respuesta.isResultado()){
            NclCentrocCliente centrocClienteDto = ObjetoMapper.map(respuesta.getDatos(), NclCentrocCliente.class);
            this.centroClienteCompaniaId = centrocClienteDto.getCentrocClienteId();
        }
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    @Order(1)
    public void testGuardarEmpresa() {
        NclCentrocClienteDto centrocClienteDTO = new NclCentrocClienteDto();
        centrocClienteDTO.setEsActivo(true);
        centrocClienteDTO.setNombre("PruebaTest");
        centrocClienteDTO.setRazonSocial("PruebaTest");
        centrocClienteDTO.setRfc(generaRfc());
        centrocClienteDTO.setRegimenfiscalId(new CsRegimenFiscal());
        centrocClienteDTO.getRegimenfiscalId().setRegimenfiscalId("601");
        centrocClienteDTO.setCurp("AAAA000000AAAAAA00");
        centrocClienteDTO.setPrimaRiesgo(BigDecimal.ONE);
        centrocClienteDTO.setClaveDelegacionalImss(1);
        centrocClienteDTO.setCuentaStp("12345");
        centrocClienteDTO.setCuentaClabeStp(12345);
        centrocClienteDTO.setRegimenfiscalId(new CsRegimenFiscal());
        centrocClienteDTO.getRegimenfiscalId().setRegimenfiscalId("601");
        centrocClienteDTO.setCentroCostosCentrocClienteId(new NclCentrocCliente());
        centrocClienteDTO.getCentroCostosCentrocClienteId().setCentrocClienteId(1);
        centrocClienteDTO.setDomicilio(new NmaDomicilio());
        centrocClienteDTO.getDomicilio().setCalle("Prueba");
        centrocClienteDTO.getDomicilio().setNumExterior("400");
        centrocClienteDTO.getDomicilio().setNumInterior("2");
        centrocClienteDTO.getDomicilio().setReferencias("Prueba");
        centrocClienteDTO.getDomicilio().setEsDomicilioFiscal(true);
        centrocClienteDTO.getDomicilio().setEsActivo(true);
        centrocClienteDTO.getDomicilio().setSedeId(new NclSede());
        centrocClienteDTO.getDomicilio().getSedeId().setSedeId(1);
        centrocClienteDTO.getDomicilio().setCentrocClienteId(new NclCentrocCliente());
        centrocClienteDTO.getDomicilio().getCentrocClienteId().setCentrocClienteId(this.centroClienteCompaniaId);
        centrocClienteDTO.getDomicilio().setAsentamientoId(1316);
        centrocClienteDTO.getDomicilio().setEstado(1);
        centrocClienteDTO.getDomicilio().setCodigo("20240");
        centrocClienteDTO.getDomicilio().setMunicipio(1);
        centrocClienteDTO.setCer(Utilidades.decodeContent("MIIFuzCCA6OgAwIBAgIUMzAwMDEwMDAwMDA0MDAwMDI0MzQwDQYJKoZIhvcNAQELBQAwggErMQ8wDQYDVQQDDAZBQyBVQVQxLjAsBgNVBAoMJVNFUlZJQ0lPIERFIEFETUlOSVNUUkFDSU9OIFRSSUJVVEFSSUExGjAYBgNVBAsMEVNBVC1JRVMgQXV0aG9yaXR5MSgwJgYJKoZIhvcNAQkBFhlvc2Nhci5tYXJ0aW5lekBzYXQuZ29iLm14MR0wGwYDVQQJDBQzcmEgY2VycmFkYSBkZSBjYWRpejEOMAwGA1UEEQwFMDYzNzAxCzAJBgNVBAYTAk1YMRkwFwYDVQQIDBBDSVVEQUQgREUgTUVYSUNPMREwDwYDVQQHDAhDT1lPQUNBTjERMA8GA1UELRMIMi41LjQuNDUxJTAjBgkqhkiG9w0BCQITFnJlc3BvbnNhYmxlOiBBQ0RNQS1TQVQwHhcNMTkwNjE3MTk0NDE0WhcNMjMwNjE3MTk0NDE0WjCB4jEnMCUGA1UEAxMeRVNDVUVMQSBLRU1QRVIgVVJHQVRFIFNBIERFIENWMScwJQYDVQQpEx5FU0NVRUxBIEtFTVBFUiBVUkdBVEUgU0EgREUgQ1YxJzAlBgNVBAoTHkVTQ1VFTEEgS0VNUEVSIFVSR0FURSBTQSBERSBDVjElMCMGA1UELRMcRUtVOTAwMzE3M0M5IC8gWElRQjg5MTExNlFFNDEeMBwGA1UEBRMVIC8gWElRQjg5MTExNk1HUk1aUjA1MR4wHAYDVQQLExVFc2N1ZWxhIEtlbXBlciBVcmdhdGUwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCN0peKpgfOL75iYRv1fqq+oVYsLPVUR/GibYmGKc9InHFy5lYF6OTYjnIIvmkOdRobbGlCUxORX/tLsl8Ya9gm6Yo7hHnODRBIDup3GISFzB/96R9K/MzYQOcscMIoBDARaycnLvy7FlMvO7/rlVnsSARxZRO8Kz8Zkksj2zpeYpjZIya/369+oGqQk1cTRkHo59JvJ4Tfbk/3iIyf4H/Ini9nBe9cYWo0MnKob7DDt/vsdi5tA8mMtA953LapNyCZIDCRQQlUGNgDqY9/8F5mUvVgkcczsIgGdvf9vMQPSf3jjCiKj7j6ucxl1+FwJWmbvgNmiaUR/0q4m2rm78lFAgMBAAGjHTAbMAwGA1UdEwEB/wQCMAAwCwYDVR0PBAQDAgbAMA0GCSqGSIb3DQEBCwUAA4ICAQBcpj1TjT4jiinIujIdAlFzE6kRwYJCnDG08zSp4kSnShjxADGEXH2chehKMV0FY7c4njA5eDGdA/G2OCTPvF5rpeCZP5Dw504RZkYDl2suRz+wa1sNBVpbnBJEK0fQcN3IftBwsgNFdFhUtCyw3lus1SSJbPxjLHS6FcZZ51YSeIfcNXOAuTqdimusaXq15GrSrCOkM6n2jfj2sMJYM2HXaXJ6rGTEgYmhYdwxWtil6RfZB+fGQ/H9I9WLnl4KTZUS6C9+NLHh4FPDhSk19fpS2S/56aqgFoGAkXAYt9Fy5ECaPcULIfJ1DEbsXKyRdCv3JY89+0MNkOdaDnsemS2o5Gl08zI4iYtt3L40gAZ60NPh31kVLnYNsmvfNxYyKp+AeJtDHyW9w7ftM0Hoi+BuRmcAQSKFV3pk8j51la+jrRBrAUv8blbRcQ5BiZUwJzHFEKIwTsRGoRyEx96sNnB03n6GTwjIGz92SmLdNl95r9rkvp+2m4S6q1lPuXaFg7DGBrXWC8iyqeWE2iobdwIIuXPTMVqQb12m1dAkJVRO5NdHnP/MpqOvOgLqoZBNHGyBg4Gqm4sCJHCxA1c8Elfa2RQTCk0tAzllL4vOnI1GHkGJn65xokGsaU4B4D36xh7eWrfj4/pgWHmtoDAYa8wzSwo2GVCZOs+mtEgOQB91/g=="));
        centrocClienteDTO.setKey(Utilidades.decodeContent("MIIFDjBABgkqhkiG9w0BBQ0wMzAbBgkqhkiG9w0BBQwwDgQIAgEAAoIBAQACAggAMBQGCCqGSIb3DQMHBAgwggS8AgEAMASCBMh4EHl7aNSCaMDA1VlRoXCZ5UUmqErAbucRFLOMmsAaFNkyWR0dXIAh0CMjE6NpQIMZhQ0HH/4tHgmwh4kCawGjIwERoG6/IH3mCt7u19J5+m6gUEGOJdEMXj976E5lKCd/EG6t6lCq66GE3rgux/nFmeQZvsjLlzPyhe2j+X81LrGudITTjDdgLI0EdbdV9CUJwWbibzrVxjuAVShRh07XPL/DiEw3Wk2+kdy4cfWmMvh0U55p0RKZopNkWuVVSvr3ai7ZNCwHZWDVqkUDpwDDGdyt0kYQ7qoKanIxv/A9wv6ekq0LQ/yLlOcelkxQeb8Glu4RXe+krRvrASw1eBAQ3mvNKpngwF8vtlyoil41PjHUOKALMJtNpywckRRYOk4703ylWIzTfdBlrZ6VmDBjdC5723G1HAx3R/x+o+08++RNiFaN06Ly5QbZZvjnealDfSKz1VKRHWeXggaW87rl4n0SOOWnvabKs4ZWRXTS0dhWK+KD/yYYQypTslDSXQrmyMkpc1Zcb4p9RTjodXxGCWdsR5i5+Ro/RiJvxWwwaO3YW6eaSavV0ROqANQ+A+GizMlxsVjl6G5Ooh6ORdA7jTNWmK44Icgyz6QFNh+J3NibxVK2GZxsQRi+N3HXeKYtq5SDXARA0BsaJQzYfDotA9LFgmFKg9jVhtcc1V3rtpaJ5sab8tdBTPPyN/XT8fA0GxlIX+hjLd3E9wB7qzNR6PZ84UKDxhCGWrLuIoSzuCbr+TD9UCJprsfTu8kr8Pur4rrxm7Zu1MsJRR9U5Ut+O9FZfw4SqGykyTGGh0v1gDG8esKpTW5MKNk9dRwDNHEmIF6tE6NeXDlzovf8VW6z9JA6AVUkgiFjDvLUY5MgyTqPB9RJNMSAZBzrkZgXyHlmFz2rvPqQGFbAtukjeRNS+nkVayLqfQnqpgthBvsgDUgFn03z0U2Svb094Q5XHMeQ4KM/nMWTEUC+8cybYhwVklJU7FBl9nzs66wkMZpViIrVWwSB2k9R1r/ZQcmeL+LR+WwgCtRs4It1rNVkxXwYHjsFM2Ce46TWhbVMF/h7Ap4lOTS15EHC8RvIBBcR2w1iJ+3pXiMeihArTELVnQsS31X3kxbBp3dGvLvW7PxDlwwdUQOXnMoimUCI/h0uPdSRULPAQHgSp9+TwqI0Uswb7cEiXnN8PySN5Tk109CYJjKqCxtuXu+oOeQV2I/0knQLd2zol+yIzNLj5a/HvyN+kOhIGi6TrFThuiVbbtnTtRM1CzKtFGuw5lYrwskkkvenoSLNY0N85QCU8ugjc3Bw4JZ9jNrDUaJ1Vb5/+1GQx/q/Dbxnl+FK6wMLjXy5JdFDeQyjBEBqndQxrs9cM5xBnl6AYs2Xymydafm2qK0cEDzwOPMpVcKU8sXS/AHvtgsn+rjMzW0wrQblWE0Ht/74GgfCj4diCDtzxQ0ggi6yJD+yhLZtVVqmKS3Gwnj9RxPLNfpgzPP01eYyBBi/W0RWTzcTb8iMxWX52MTU0oX9//4I7CAPXn0ZhpWAAIvUmkfjwfEModH7iwwaNtZFlT2rlzeshbP++UCEtqbwvveDRhmr5sMYkl+duEOca5156fcRy4tQ8Y3moNcKFKzHGMenShEIHz+W5KE="));
        centrocClienteDTO.setContrasenia("contrasena");
        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.PUT("/guardar/empresa",centrocClienteDTO),
                        RespuestaGenerica.class);
        if (respuesta.isResultado()){
            NclCentrocCliente centrocClienteDto = ObjetoMapper.map(respuesta.getDatos(), NclCentrocCliente.class);
            this.centroClienteEmpresaId = centrocClienteDto.getCentrocClienteId();
        }
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    @Order(2)
    public void testModificarCompania() {
        NclCentrocClienteDto centrocClienteDTO = new NclCentrocClienteDto();
        centrocClienteDTO.setCentrocClienteId(this.centroClienteCompaniaId);
        centrocClienteDTO.setNombre("PruebaTestModificar");
        centrocClienteDTO.setRazonSocial("PruebaTestModificar");
        centrocClienteDTO.setRfc(generaRfc());
        centrocClienteDTO.setFechaAlta(new Timestamp(new Date().getTime()));
        centrocClienteDTO.setRegimenfiscalId(new CsRegimenFiscal());
        centrocClienteDTO.getRegimenfiscalId().setRegimenfiscalId("601");
        centrocClienteDTO.setBasePeriodoId(new CatBasePeriodo());
        centrocClienteDTO.getBasePeriodoId().setBasePeriodoId(1);
        centrocClienteDTO.setEsActivo(true);
        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.POST("/modificar/compania",centrocClienteDTO),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    @Order(3)
    public void testModificarEmpresa() {
        NclCentrocClienteDto centrocClienteDTO = new NclCentrocClienteDto();
        centrocClienteDTO.setCentrocClienteId(this.centroClienteEmpresaId);
        centrocClienteDTO.setNombre("ASG");
        centrocClienteDTO.setRazonSocial("ASG");
        centrocClienteDTO.setRfc(generaRfc());
        centrocClienteDTO.setRegimenfiscalId(new CsRegimenFiscal());
        centrocClienteDTO.getRegimenfiscalId().setRegimenfiscalId("601");
        centrocClienteDTO.setCurp("AAAA000000AAAAAA00");
        centrocClienteDTO.setPrimaRiesgo(BigDecimal.ONE);
        centrocClienteDTO.setClaveDelegacionalImss(1);
        centrocClienteDTO.setCuentaStp("12345");
        centrocClienteDTO.setCuentaClabeStp(12345);
        centrocClienteDTO.setRegimenfiscalId(new CsRegimenFiscal());
        centrocClienteDTO.getRegimenfiscalId().setRegimenfiscalId("601");
        centrocClienteDTO.setCentroCostosCentrocClienteId(new NclCentrocCliente());
        centrocClienteDTO.getCentroCostosCentrocClienteId().setCentrocClienteId(1);
        centrocClienteDTO.setDomicilio(new NmaDomicilio());
        centrocClienteDTO.getDomicilio().setCalle("Prueba");
        centrocClienteDTO.getDomicilio().setNumExterior("400");
        centrocClienteDTO.getDomicilio().setNumInterior("2");
        centrocClienteDTO.getDomicilio().setReferencias("Prueba");
        centrocClienteDTO.getDomicilio().setEsDomicilioFiscal(true);
        centrocClienteDTO.getDomicilio().setEsActivo(true);
        centrocClienteDTO.getDomicilio().setSedeId(new NclSede());
        centrocClienteDTO.getDomicilio().getSedeId().setSedeId(1);
        centrocClienteDTO.getDomicilio().setCentrocClienteId(new NclCentrocCliente());
        centrocClienteDTO.getDomicilio().getCentrocClienteId().setCentrocClienteId(1);
        centrocClienteDTO.getDomicilio().setAsentamientoId(1316);
        centrocClienteDTO.getDomicilio().setEstado(1);
        centrocClienteDTO.getDomicilio().setCodigo("20240");
        centrocClienteDTO.getDomicilio().setMunicipio(1);
        centrocClienteDTO.setFechaAlta(new Timestamp(new Date().getTime()));
        centrocClienteDTO.setBasePeriodoId(new CatBasePeriodo());
        centrocClienteDTO.getBasePeriodoId().setBasePeriodoId(1);
        centrocClienteDTO.setEsActivo(true);
        centrocClienteDTO.setCer(Utilidades.decodeContent("MIIFuzCCA6OgAwIBAgIUMzAwMDEwMDAwMDA0MDAwMDI0MzQwDQYJKoZIhvcNAQELBQAwggErMQ8wDQYDVQQDDAZBQyBVQVQxLjAsBgNVBAoMJVNFUlZJQ0lPIERFIEFETUlOSVNUUkFDSU9OIFRSSUJVVEFSSUExGjAYBgNVBAsMEVNBVC1JRVMgQXV0aG9yaXR5MSgwJgYJKoZIhvcNAQkBFhlvc2Nhci5tYXJ0aW5lekBzYXQuZ29iLm14MR0wGwYDVQQJDBQzcmEgY2VycmFkYSBkZSBjYWRpejEOMAwGA1UEEQwFMDYzNzAxCzAJBgNVBAYTAk1YMRkwFwYDVQQIDBBDSVVEQUQgREUgTUVYSUNPMREwDwYDVQQHDAhDT1lPQUNBTjERMA8GA1UELRMIMi41LjQuNDUxJTAjBgkqhkiG9w0BCQITFnJlc3BvbnNhYmxlOiBBQ0RNQS1TQVQwHhcNMTkwNjE3MTk0NDE0WhcNMjMwNjE3MTk0NDE0WjCB4jEnMCUGA1UEAxMeRVNDVUVMQSBLRU1QRVIgVVJHQVRFIFNBIERFIENWMScwJQYDVQQpEx5FU0NVRUxBIEtFTVBFUiBVUkdBVEUgU0EgREUgQ1YxJzAlBgNVBAoTHkVTQ1VFTEEgS0VNUEVSIFVSR0FURSBTQSBERSBDVjElMCMGA1UELRMcRUtVOTAwMzE3M0M5IC8gWElRQjg5MTExNlFFNDEeMBwGA1UEBRMVIC8gWElRQjg5MTExNk1HUk1aUjA1MR4wHAYDVQQLExVFc2N1ZWxhIEtlbXBlciBVcmdhdGUwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCN0peKpgfOL75iYRv1fqq+oVYsLPVUR/GibYmGKc9InHFy5lYF6OTYjnIIvmkOdRobbGlCUxORX/tLsl8Ya9gm6Yo7hHnODRBIDup3GISFzB/96R9K/MzYQOcscMIoBDARaycnLvy7FlMvO7/rlVnsSARxZRO8Kz8Zkksj2zpeYpjZIya/369+oGqQk1cTRkHo59JvJ4Tfbk/3iIyf4H/Ini9nBe9cYWo0MnKob7DDt/vsdi5tA8mMtA953LapNyCZIDCRQQlUGNgDqY9/8F5mUvVgkcczsIgGdvf9vMQPSf3jjCiKj7j6ucxl1+FwJWmbvgNmiaUR/0q4m2rm78lFAgMBAAGjHTAbMAwGA1UdEwEB/wQCMAAwCwYDVR0PBAQDAgbAMA0GCSqGSIb3DQEBCwUAA4ICAQBcpj1TjT4jiinIujIdAlFzE6kRwYJCnDG08zSp4kSnShjxADGEXH2chehKMV0FY7c4njA5eDGdA/G2OCTPvF5rpeCZP5Dw504RZkYDl2suRz+wa1sNBVpbnBJEK0fQcN3IftBwsgNFdFhUtCyw3lus1SSJbPxjLHS6FcZZ51YSeIfcNXOAuTqdimusaXq15GrSrCOkM6n2jfj2sMJYM2HXaXJ6rGTEgYmhYdwxWtil6RfZB+fGQ/H9I9WLnl4KTZUS6C9+NLHh4FPDhSk19fpS2S/56aqgFoGAkXAYt9Fy5ECaPcULIfJ1DEbsXKyRdCv3JY89+0MNkOdaDnsemS2o5Gl08zI4iYtt3L40gAZ60NPh31kVLnYNsmvfNxYyKp+AeJtDHyW9w7ftM0Hoi+BuRmcAQSKFV3pk8j51la+jrRBrAUv8blbRcQ5BiZUwJzHFEKIwTsRGoRyEx96sNnB03n6GTwjIGz92SmLdNl95r9rkvp+2m4S6q1lPuXaFg7DGBrXWC8iyqeWE2iobdwIIuXPTMVqQb12m1dAkJVRO5NdHnP/MpqOvOgLqoZBNHGyBg4Gqm4sCJHCxA1c8Elfa2RQTCk0tAzllL4vOnI1GHkGJn65xokGsaU4B4D36xh7eWrfj4/pgWHmtoDAYa8wzSwo2GVCZOs+mtEgOQB91/g=="));
        centrocClienteDTO.setKey(Utilidades.decodeContent("MIIFDjBABgkqhkiG9w0BBQ0wMzAbBgkqhkiG9w0BBQwwDgQIAgEAAoIBAQACAggAMBQGCCqGSIb3DQMHBAgwggS8AgEAMASCBMh4EHl7aNSCaMDA1VlRoXCZ5UUmqErAbucRFLOMmsAaFNkyWR0dXIAh0CMjE6NpQIMZhQ0HH/4tHgmwh4kCawGjIwERoG6/IH3mCt7u19J5+m6gUEGOJdEMXj976E5lKCd/EG6t6lCq66GE3rgux/nFmeQZvsjLlzPyhe2j+X81LrGudITTjDdgLI0EdbdV9CUJwWbibzrVxjuAVShRh07XPL/DiEw3Wk2+kdy4cfWmMvh0U55p0RKZopNkWuVVSvr3ai7ZNCwHZWDVqkUDpwDDGdyt0kYQ7qoKanIxv/A9wv6ekq0LQ/yLlOcelkxQeb8Glu4RXe+krRvrASw1eBAQ3mvNKpngwF8vtlyoil41PjHUOKALMJtNpywckRRYOk4703ylWIzTfdBlrZ6VmDBjdC5723G1HAx3R/x+o+08++RNiFaN06Ly5QbZZvjnealDfSKz1VKRHWeXggaW87rl4n0SOOWnvabKs4ZWRXTS0dhWK+KD/yYYQypTslDSXQrmyMkpc1Zcb4p9RTjodXxGCWdsR5i5+Ro/RiJvxWwwaO3YW6eaSavV0ROqANQ+A+GizMlxsVjl6G5Ooh6ORdA7jTNWmK44Icgyz6QFNh+J3NibxVK2GZxsQRi+N3HXeKYtq5SDXARA0BsaJQzYfDotA9LFgmFKg9jVhtcc1V3rtpaJ5sab8tdBTPPyN/XT8fA0GxlIX+hjLd3E9wB7qzNR6PZ84UKDxhCGWrLuIoSzuCbr+TD9UCJprsfTu8kr8Pur4rrxm7Zu1MsJRR9U5Ut+O9FZfw4SqGykyTGGh0v1gDG8esKpTW5MKNk9dRwDNHEmIF6tE6NeXDlzovf8VW6z9JA6AVUkgiFjDvLUY5MgyTqPB9RJNMSAZBzrkZgXyHlmFz2rvPqQGFbAtukjeRNS+nkVayLqfQnqpgthBvsgDUgFn03z0U2Svb094Q5XHMeQ4KM/nMWTEUC+8cybYhwVklJU7FBl9nzs66wkMZpViIrVWwSB2k9R1r/ZQcmeL+LR+WwgCtRs4It1rNVkxXwYHjsFM2Ce46TWhbVMF/h7Ap4lOTS15EHC8RvIBBcR2w1iJ+3pXiMeihArTELVnQsS31X3kxbBp3dGvLvW7PxDlwwdUQOXnMoimUCI/h0uPdSRULPAQHgSp9+TwqI0Uswb7cEiXnN8PySN5Tk109CYJjKqCxtuXu+oOeQV2I/0knQLd2zol+yIzNLj5a/HvyN+kOhIGi6TrFThuiVbbtnTtRM1CzKtFGuw5lYrwskkkvenoSLNY0N85QCU8ugjc3Bw4JZ9jNrDUaJ1Vb5/+1GQx/q/Dbxnl+FK6wMLjXy5JdFDeQyjBEBqndQxrs9cM5xBnl6AYs2Xymydafm2qK0cEDzwOPMpVcKU8sXS/AHvtgsn+rjMzW0wrQblWE0Ht/74GgfCj4diCDtzxQ0ggi6yJD+yhLZtVVqmKS3Gwnj9RxPLNfpgzPP01eYyBBi/W0RWTzcTb8iMxWX52MTU0oX9//4I7CAPXn0ZhpWAAIvUmkfjwfEModH7iwwaNtZFlT2rlzeshbP++UCEtqbwvveDRhmr5sMYkl+duEOca5156fcRy4tQ8Y3moNcKFKzHGMenShEIHz+W5KE="));
        centrocClienteDTO.setContrasenia("contrasena");
        centrocClienteDTO.setCerKeyConstrasenia(false);
        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.POST("/modificar/empresa",centrocClienteDTO),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    @Order(4)
    public void testModificarLista() {
        List<NclCentrocClienteDto> listaNclCentrocClienteDTO =  new ArrayList<>();
        NclCentrocClienteDto centrocClienteDTO = new NclCentrocClienteDto();
        centrocClienteDTO.setCentrocClienteId(this.centroClienteCompaniaId);
        centrocClienteDTO.setNombre("PruebaModificarLista");
        centrocClienteDTO.setRazonSocial("PruebaModificarLista");
        centrocClienteDTO.setRfc(generaRfc());
        centrocClienteDTO.setEsActivo(true);
        centrocClienteDTO.setFechaAlta(new Timestamp(new Date().getTime()));
        listaNclCentrocClienteDTO.add(centrocClienteDTO);

        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.POST("/modificar/lista",listaNclCentrocClienteDTO),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    @Order(4)
    public void testEliminarId() {
        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.POST("/eliminar/id/" + this.centroClienteCompaniaId,""),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    @Order(5)
    public void testEliminarEmpresa() {
        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.POST("/eliminar/empresa/id/" + this.centroClienteEmpresaId,""),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    @Order(6)
    public void testListaCompania() {
        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.GET("/lista/compania"),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    @Order(7)
    public void testListaCompaniaEmpresa() {
        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.GET("/lista/compania/empresa/" + this.centroClienteEmpresaId),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    @Order(8)
    public void testObtenerId() {
        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.GET("/obtener/id/" + this.centroClienteEmpresaId),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    @Order(9)
    public void testListaDinamica() {
        NclCentrocClienteDto centrocClienteDTO = new NclCentrocClienteDto();
        centrocClienteDTO.setCentrocClienteId(this.centroClienteCompaniaId);
        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.POST("/lista/dinamica",centrocClienteDTO),
                        RespuestaGenerica.class);
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getDatos());
    }

    @Test
    @Order(10)
    public void testValidacionEmpresa() {
        final ValidacionEmpresaDto respuesta =
                cliente.toBlocking().retrieve(HttpRequest.GET("/validacion/captura/empresa/" + this.centroClienteEmpresaId),
                        ValidacionEmpresaDto.class);
        assertTrue(respuesta.isRespuesta());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
        LOG.info("Respuesta {}", respuesta.getMensaje());
        nclCentrocClienteRepository.deleteById(this.centroClienteEmpresaId);
        nclCentrocClienteRepository.deleteById(this.centroClienteCompaniaId);
    }

    @Test
    public void testPrueba() throws IOException {
        try {
            String respuesta = "{\"codigo_resultado\":\"000\",\"contenido\":[{\"csd_id\":\"86bcdf29-bd34-4205-a6fe-ce2b96e02da8\",\"fecha_final\":\"2023-06-20T20:58:17\",\"fecha_inicio\":\"2019-06-20T20:58:17\",\"no_certificado\":\"30001000000400002464\",\"rfc\":\"O\\u00d1O120726RX3\",\"status_certificado\":\"OK\"}],\"exito\":true,\"mensaje\":\"Certificado almacenado\",\"resultado_servicio\":[{\"identificador_entrada\":\"NA\",\"identificador_operacion\":\"86bcdf29-bd34-4205-a6fe-ce2b96e02da8\",\"mensaje_servicio\":\"Solicitud ejecutada correctamente\",\"servicio\":\"interno\",\"status_servicio\":\"OK\"}],\"tipo\":\"alta_csd\"}\n";
            ObjectMapper objectMapper = new ObjectMapper();
            CertificadoSelloDigitalRespuestaDto respuestaDto = objectMapper.readValue(respuesta,
                    CertificadoSelloDigitalRespuestaDto.class);
            System.out.println(respuestaDto);
        }catch (Exception e){
            System.out.println(e);
        }
    }


    @Test
    public void test() throws IOException {


        OkHttpClient client = new OkHttpClient();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("csd_cer", "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");
        jsonObject.put("csd_key", "MIIFDjBABgkqhkiG9w0BBQ0wMzAbBgkqhkiG9w0BBQwwDgQIAgEAAoIBAQACAggAMBQGCCqGSIb3DQMHBAgwggS9AgEAMASCBMh4EHl7aNSCaMDA1VlRoXCZ5UUmqErAbucRBAKNQXH8t30iyHVlZlDEzX06ewbtBpI9FUmIs/ZrMqxsa3Wu2khmXzX9rXKmIv867FLTA8OGCeIY3AtGuIM5QuREj+Gz6VR1spFknZzf1miJc/t11P7y6nzZiUoSshKhJ39ADk2s7G61KdqMyr7tUsM5j/PgP0a2vTYvp59rCr+HURr8x2P/3sG8KoXCQ2ZHzIo7TzDIndwBDa98g+vZ5yfHvKm7V8J+R9CzNIjFkaZ90PsWuDeLaHiv2cpUe1/E9Cndm8j1v0Rli0c/VKMI8//VHMDmzZJ5FIip9xigO7ph6SdnIbWSDE3a4McR/a05nye8seqdxKG3x2MfINlI5jzZElGEok9nwFava8HmTjXbSZ9j4u3ql3nRXm0flYb439QzT5YFGi+zzoaT7w7HvwKciEUT+ftOcCgo5JliAlaw/+Qe7oIgrCQimamv5fkbb1b0Sr9NZcTREAqvaKbYUNf+ceP5vyR17knGwMYoBRhbs22E/57zytkblD/26rf1imkVNFn1yT8vLNRjYf+0WY9NboYbaqLNfF7tRRj5PRvaTjGqn8IJPJ1YcuDWJnFE6RKQmKNNSjtzhWoDWF7A0WPvWdXE8P80yyirCSDLAi6C7MErVJ2x+oCS4dNfI0dM4GTVZHExR6BkjJndlScrSc7g2KBji/+ph79Xu+xsT1k5JToXxTfuyuVeBq5gKAUqiK31oXhCNk9KbXJxWs4khVLCfyt70z7MV5H7Pj+BKud9tm3FvX3FhOKwD8/F2x3r9/TD9QOJKYCFsR4zdAP+qa9HYQxx6wvZtA+YBV4JNyVRYMr/HxtLzo4bXR765BKrxUj/LwyvUxYbTmqH8lp66ZQVTpSUqxdVopVyPT11XPFaw622GjWrtU0FtFmj/rfxQ2uz4ui94jjV17avC3IMWAsfMR6zlQXIua8dbJ8x/yvcqiKn2I2Hjzcd/QtLGXl0Y4qzdsHfAVNLNpz7THw7CvlBU78gLZD2dkpa3YzL4oTdD1pIZayXWc/rw6jDM9tiWtcGH3nCUMdpLfJVqlKv6Ytpl1CuvnB57HaEVnkQF82XTamq0XQsHwcV8rwHSPHyTpr0B9KSIiLwoukHpmoxPQnISU1YY3zJGCCaJu89ubsBYcD0iJRkrrltktW5105yJy74oIjrJYGzVvVrvdfBl8AL67FLAUOMa1iK5XiDDu/FU82JJ0Z4RmR9yAOvu5JHeTbVVkItljP6ix40vWvFcjaX9m4VipOxxcF3g1C+FaETNcuen5YX+0x5ahlgst7Y0Bm35F+VO0oDR0h7Ef43o7fUmjk9IqMjqggNTPA757aPZx3erbqTEFwcI39qCxyC3S6ZbIXp6Qt0c3zQ9ukMsXkswwzYzyHGrJQX/l202YBxC6mB9gVYHsvsbsJ4N21q/G5onc6p/zBYeFm+1BPJpo29ASf6/Z3UTEQn0WqEJt/5e6v4oKTa0FaWAKJnOPyPDgqFTLCnX7AnNrWef0UqkzkGzC30i109ne6uQ/8O4qMJEklrr+hLL5CNAUZ3ynVD+uBUJqENGuLLJ/SUafGcyLRY7l7aJYOyUVIS8y9eG0oIsrboOM9puNNtUrmImKo=");
        jsonObject.put("csd_pass","contrasena");

        RequestBody formBody = RequestBody.create(jsonObject.toString(),null);

        Request request = new Request.Builder()
                .url("https://us-central1-cosmonaut-299500.cloudfunctions.net/cosmonaut-csd")
                .addHeader("Content-Type","application/json")
                .put(formBody)
                .build();

        Call call = client.newCall(request);
        Response response = call.execute();
        if (response.isSuccessful()){
            ObjectMapper objectMapper = new ObjectMapper();
            CertificadoSelloDigitalRespuestaDto respuestaDto = objectMapper.readValue(response.body().string(),
                    CertificadoSelloDigitalRespuestaDto.class);
            System.out.println(respuestaDto.getContenido()[0].getCsd_id());
        }

    }

    @Test
    public void testNss() throws IOException, ServiceException, ParseException {
        String sDate1="23/08/1963";
        Date fecha = new SimpleDateFormat("dd/MM/yyyy").parse(sDate1);
        RespuestaGenerica respuesta = Validar.numeroSerguroSocial("04826303010",fecha);
        LOG.info("Respuesta {}", respuesta.getMensaje());
        assertTrue(respuesta.isResultado());
        assertTrue(Constantes.EXITO.equals(respuesta.getMensaje()));
    }

    @Test
    public void testValidRfc() {
        String rfc = "SOOJ890306GR9";
        RespuestaGenerica respuesta = new RespuestaGenerica(rfc,Constantes.RESULTADO_EXITO, Constantes.EXITO);
        if (Validar.validaTexto(rfc) && !Validar.rfc(rfc)){
            respuesta.setResultado(Constantes.RESULTADO_ERROR);
            respuesta.setMensaje(Constantes.RFC_NO_VALIDO);
        }
        LOG.info("Respuesta {}", respuesta.getMensaje());
        assertTrue(respuesta.isResultado());
    }

    private String generaRfc(){
        String rfc = "S" + generaStringAleatorio() + generaStringAleatorio() +"J890306GG" + generaNumeroAleatorio();
        return rfc;
    }

    private Integer generaNumeroAleatorio(){
        Random rand = new Random();
        return rand.nextInt(10);
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

}
