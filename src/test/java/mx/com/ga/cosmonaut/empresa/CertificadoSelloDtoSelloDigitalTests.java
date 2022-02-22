package mx.com.ga.cosmonaut.empresa;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.dto.cms.RespuestaEstatus;
import mx.com.ga.cosmonaut.common.dto.cms.RespuestaGestionContenido;
import mx.com.ga.cosmonaut.common.dto.csd.CertificadoSelloDigitalPeticionDto;
import mx.com.ga.cosmonaut.common.dto.csd.CertificadoSelloDigitalRespuestaDto;
import mx.com.ga.cosmonaut.common.dto.imss.GuardarCsdImssResponseDto;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.common.util.Cliente;
import mx.com.ga.cosmonaut.common.util.Constantes;
import mx.com.ga.cosmonaut.common.util.ObjetoMapper;
import mx.com.ga.cosmonaut.common.util.Validar;
import okhttp3.*;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
public class CertificadoSelloDtoSelloDigitalTests {

    private static final Logger LOG = LoggerFactory.getLogger(CentroCostosClienteTests.class);

    @Test
    public void testGuardarCertificadoSellosDigital() throws ServiceException {
        try{

            OkHttpClient cliente = new OkHttpClient();

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("csd_cer", "Usuario de certificado IMSS cargado");
            jsonObject.put("csd_key", "Certificado IMSS digital cargado");
            jsonObject.put("csd_pass", "12345");

            RequestBody formBody = RequestBody.create(jsonObject.toString(),null);

            Request request = new Request.Builder()
                    .url("https://us-central1-cosmonaut-299500.cloudfunctions.net/cosmonaut-csd")
                    .addHeader("Content-Type","application/json")
                    .put(formBody)
                    .build();

            Call call = cliente.newCall(request);
            Response response = call.execute();
            System.out.println(response.body().string());
            if (response.isSuccessful()){
                ObjectMapper objectMapper = new ObjectMapper();
            }

        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " clienteGestorContenido " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Test
    public void testRespuesta() throws ServiceException {
        try{
            String request = "{\"codigo_resultado\":\"000\",\"contenido\":[{\"advertencia\":\"El certificado no es del SAT\",\"csd_id\":\"92cd0ed9-cbb9-4f32-b8ae-8efa3aca456f\"}],\"exito\":true,\"mensaje\":\"Certificado almacenado\",\"resultado_servicio\":[{\"identificador_entrada\":\"NA\",\"identificador_operacion\":\"92cd0ed9-cbb9-4f32-b8ae-8efa3aca456f\",\"mensaje_servicio\":\"Solicitud ejecutada correctamente\",\"servicio\":\"interno\",\"status_servicio\":\"OK\"}],\"tipo\":\"alta_csd\"}\n";
            ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
            CertificadoSelloDigitalRespuestaDto certificado =
                    objectMapper.readValue(request,CertificadoSelloDigitalRespuestaDto.class);
            GuardarCsdImssResponseDto guardarCsd = ObjetoMapper.map(certificado,GuardarCsdImssResponseDto.class);
            guardarCsd.setResultadoCodigo(certificado.getCodigo_resultado());
            guardarCsd.setResultadoServicio(certificado.getResultado_servicio()[0]);
            System.out.println(guardarCsd.getContenido());
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " clienteGestorContenido " + Constantes.ERROR_EXCEPCION, e);
        }
    }

}
