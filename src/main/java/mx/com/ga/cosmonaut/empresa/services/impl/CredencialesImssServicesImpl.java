package mx.com.ga.cosmonaut.empresa.services.impl;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.micronaut.context.annotation.Value;
import mx.com.ga.cosmonaut.common.dto.csd.CertificadoSelloDigitalRespuestaDto;
import mx.com.ga.cosmonaut.common.dto.imss.GuardarCsdImssRequestDto;
import mx.com.ga.cosmonaut.common.dto.imss.GuardarCsdImssResponseDto;
import mx.com.ga.cosmonaut.common.dto.imss.tectel.AfiliaRecepcionRequestDto;
import mx.com.ga.cosmonaut.common.dto.imss.tectel.AfiliaRecepcionResponseDto;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.common.util.Constantes;
import mx.com.ga.cosmonaut.common.util.ObjetoMapper;
import mx.com.ga.cosmonaut.empresa.services.CredencialesImssServices;
import okhttp3.*;
import org.json.JSONObject;

import javax.inject.Singleton;
import java.util.Objects;

@Singleton
public class CredencialesImssServicesImpl implements CredencialesImssServices {

    @Value("${servicio.csd.host}")
    private String host;

    @Value("${servicio.csd.path}")
    private String ruta;

    @Value("${servicio.imss.host}")
    private String hostImss;

    @Value("${servicio.imss.path}")
    private String rutaImss;

    @Override
    public GuardarCsdImssResponseDto guardarCredencialesImss(GuardarCsdImssRequestDto peticion)
            throws ServiceException {
        try {
            OkHttpClient client = new OkHttpClient();

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("csd_cer", peticion.getCertificadoCds());
            jsonObject.put("csd_key", peticion.getLlaveCds());
            jsonObject.put("csd_pass", peticion.getContraseniaCsd());

            RequestBody formBody = RequestBody.create(jsonObject.toString(), null);

            Request request = new Request.Builder()
                    .url(host + ruta)
                    .addHeader("Content-Type", "application/json")
                    .put(formBody)
                    .build();

            Call call = client.newCall(request);
            Response response = call.execute();
            if (response.isSuccessful()) {
                ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                CertificadoSelloDigitalRespuestaDto certificado =
                        objectMapper.readValue(Objects.requireNonNull(response.body()).string(),
                                CertificadoSelloDigitalRespuestaDto.class);
                GuardarCsdImssResponseDto guardarCsd = ObjetoMapper.map(certificado, GuardarCsdImssResponseDto.class);
                guardarCsd.setResultadoCodigo(certificado.getCodigo_resultado());
                guardarCsd.setResultadoServicio(certificado.getResultado_servicio()[0]);
                guardarCsd.setContenido(certificado.getContenido()[0]);
                return guardarCsd;
            }
            return null;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public AfiliaRecepcionResponseDto afiliaRecepcion(AfiliaRecepcionRequestDto peticion) throws ServiceException {
        try {
            peticion.setServicio(Constantes.TECTEL_SRV);

            ObjectMapper mapper = new ObjectMapper().configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true);
            ObjectWriter writer = mapper.writer().withDefaultPrettyPrinter();
            OkHttpClient client = new OkHttpClient();

            RequestBody formBody = RequestBody.create(writer.writeValueAsString(peticion), null);

            Request request = new Request.Builder()
                    .url(hostImss + rutaImss + Constantes.TECTEL_OP_AFILIA_RECEPCION)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .post(formBody)
                    .build();

            Call call = client.newCall(request);
            Response response = call.execute();

            if (response.isSuccessful()) {
                String cuerpo = response.body().string();
                cuerpo = cuerpo.replace("{\"output\":","");
                cuerpo = cuerpo.substring(0,cuerpo.length()-2);
                return mapper
                        .readValue(Objects.requireNonNull(cuerpo), AfiliaRecepcionResponseDto.class);
            }
            return null;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + Constantes.ERROR_EXCEPCION, e);
        }
    }

}
