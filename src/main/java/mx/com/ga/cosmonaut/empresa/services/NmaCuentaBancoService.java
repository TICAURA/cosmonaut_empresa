package mx.com.ga.cosmonaut.empresa.services;

import java.util.List;
import mx.com.ga.cosmonaut.common.dto.NmaCuentaBancoDto;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.entity.administracion.NmaCuentaBanco;
import mx.com.ga.cosmonaut.common.exception.ServiceException;



public interface NmaCuentaBancoService {
    
    RespuestaGenerica findAll() throws ServiceException;
    
    RespuestaGenerica obtenerCuentaCliente(Integer idCentrocCliente) throws ServiceException;
    
    RespuestaGenerica obtieneCuentaBancariaPersonaId(Integer personaId) throws ServiceException;
    
    RespuestaGenerica findByNumeroCuenta(String numeroCuenta) throws ServiceException;
    
    RespuestaGenerica guardar(NmaCuentaBancoDto nmaCuentaBancoDto) throws ServiceException;
    
    RespuestaGenerica guardarSTP(NmaCuentaBancoDto nmaCuentaBancoDto) throws ServiceException;
    
    RespuestaGenerica modificarSTP(NmaCuentaBancoDto nmaCuentaBancoDto) throws ServiceException;
    
    RespuestaGenerica obtenerCuentaSTPCliente(Integer idCentrocCliente) throws ServiceException;
    
    RespuestaGenerica guardaLista(List<NmaCuentaBancoDto> listNmaCuentaBancoDto) throws ServiceException;
    
    RespuestaGenerica modificar(NmaCuentaBancoDto nmaCuentaBancoDto) throws ServiceException;

    RespuestaGenerica modificarLista(List<NmaCuentaBancoDto> listNmaCuentaBancoDto) throws ServiceException; 
    
    RespuestaGenerica eliminar(Integer cuentaBancoId) throws ServiceException;
    
    RespuestaGenerica obtieneBanco(String codigo) throws ServiceException;

    RespuestaGenerica findByEsActivo(Boolean activo) throws ServiceException;
    
    RespuestaGenerica obtenerCuentaClienteFuncion(Integer idCentrocCliente) throws ServiceException;

    RespuestaGenerica listarCuentaClienteDinamico(NmaCuentaBanco cuentaBanco) throws ServiceException;

    RespuestaGenerica eliminar(Integer idCentrocCliente, Integer personaId) throws ServiceException;
}
