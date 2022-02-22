package mx.com.ga.cosmonaut.empresa.services;

import io.micronaut.retry.annotation.CircuitBreaker;
import io.micronaut.retry.annotation.Retryable;
import mx.com.ga.cosmonaut.common.dto.imss.GuardarCsdImssRequestDto;
import mx.com.ga.cosmonaut.common.dto.imss.GuardarCsdImssResponseDto;
import mx.com.ga.cosmonaut.common.dto.imss.tectel.AfiliaRecepcionRequestDto;
import mx.com.ga.cosmonaut.common.dto.imss.tectel.AfiliaRecepcionResponseDto;
import mx.com.ga.cosmonaut.common.exception.ServiceException;

public interface CredencialesImssServices {

    @CircuitBreaker
    @Retryable
    GuardarCsdImssResponseDto guardarCredencialesImss(GuardarCsdImssRequestDto peticion)
            throws ServiceException;

    @CircuitBreaker
    @Retryable
    AfiliaRecepcionResponseDto afiliaRecepcion(AfiliaRecepcionRequestDto peticion)
            throws ServiceException;

}
