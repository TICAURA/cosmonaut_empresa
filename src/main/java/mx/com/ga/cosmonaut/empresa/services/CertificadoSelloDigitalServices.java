package mx.com.ga.cosmonaut.empresa.services;

import io.micronaut.retry.annotation.CircuitBreaker;
import io.micronaut.retry.annotation.Retryable;
import mx.com.ga.cosmonaut.common.dto.csd.CertificadoSelloDigitalPeticionDto;
import mx.com.ga.cosmonaut.common.dto.csd.CertificadoSelloDigitalRespuestaDto;
import mx.com.ga.cosmonaut.common.exception.ServiceException;

public interface CertificadoSelloDigitalServices {

    @CircuitBreaker
    @Retryable
    CertificadoSelloDigitalRespuestaDto consultarCertificadoSellosDigital(CertificadoSelloDigitalPeticionDto peticion)
            throws ServiceException;

    @CircuitBreaker
    @Retryable
    CertificadoSelloDigitalRespuestaDto obtenerCertificadoSellosDigital(String id) throws ServiceException;

    @CircuitBreaker
    @Retryable
    CertificadoSelloDigitalRespuestaDto guardarCertificadoSellosDigital(CertificadoSelloDigitalPeticionDto peticion)
            throws ServiceException;

    @CircuitBreaker
    @Retryable
    CertificadoSelloDigitalRespuestaDto eliminarCertificadoSellosDigital(String id) throws ServiceException;

}
