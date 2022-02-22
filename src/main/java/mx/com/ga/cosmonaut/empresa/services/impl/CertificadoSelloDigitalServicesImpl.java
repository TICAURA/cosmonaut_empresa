package mx.com.ga.cosmonaut.empresa.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.context.annotation.Value;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import mx.com.ga.cosmonaut.common.dto.csd.CertificadoSelloDigitalPeticionDto;
import mx.com.ga.cosmonaut.common.dto.csd.CertificadoSelloDigitalRespuestaDto;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.common.util.Constantes;
import mx.com.ga.cosmonaut.empresa.services.CertificadoSelloDigitalServices;
import okhttp3.*;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Objects;

@Singleton
public class CertificadoSelloDigitalServicesImpl implements CertificadoSelloDigitalServices {

    @Inject
    @Client("${servicio.csd.host}")
    private RxHttpClient cliente;

    @Value("${servicio.csd.path}")
    private String ruta;

    @Override
    public CertificadoSelloDigitalRespuestaDto consultarCertificadoSellosDigital(CertificadoSelloDigitalPeticionDto peticion)
            throws ServiceException {
        try{
            return cliente.retrieve(HttpRequest.POST(ruta,peticion), CertificadoSelloDigitalRespuestaDto.class).blockingFirst();
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO  + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public CertificadoSelloDigitalRespuestaDto obtenerCertificadoSellosDigital(String id) throws ServiceException {
        try{
            return cliente.retrieve(HttpRequest.GET(ruta + id), CertificadoSelloDigitalRespuestaDto.class).blockingFirst();
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO  + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public CertificadoSelloDigitalRespuestaDto guardarCertificadoSellosDigital(CertificadoSelloDigitalPeticionDto peticion)
            throws ServiceException {
        try{
            OkHttpClient client = new OkHttpClient();

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("csd_cer", peticion.getCsd_cer());
            jsonObject.put("csd_key", peticion.getCsd_key());
            jsonObject.put("csd_pass", peticion.getCsd_pass());

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
                return objectMapper.readValue(Objects.requireNonNull(response.body()).string(),
                        CertificadoSelloDigitalRespuestaDto.class);
            }
            return null;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO  + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public CertificadoSelloDigitalRespuestaDto eliminarCertificadoSellosDigital(String id) throws ServiceException {
        try{
            return cliente.retrieve(HttpRequest.DELETE(ruta + id,""), CertificadoSelloDigitalRespuestaDto.class).blockingFirst();
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO  + Constantes.ERROR_EXCEPCION, e);
        }
    }

}
