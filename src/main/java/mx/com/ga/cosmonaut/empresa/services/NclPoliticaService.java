package mx.com.ga.cosmonaut.empresa.services;

import java.util.List;
import mx.com.ga.cosmonaut.common.dto.NclPoliticaDto;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.entity.cliente.BeneficioXpolitica;
import mx.com.ga.cosmonaut.common.entity.cliente.NclPolitica;
import mx.com.ga.cosmonaut.common.exception.ServiceException;

public interface NclPoliticaService {
    
    RespuestaGenerica findAll() throws ServiceException;
    
    RespuestaGenerica consultaPoliticaXEmpPol(Integer idPolitica, Integer idCliente) throws ServiceException;
    
    RespuestaGenerica consultaPoliticaId(Integer id) throws ServiceException;
    
    RespuestaGenerica consultaPoliticaEmpresaId(Integer id, Integer idPolitica) throws ServiceException;
    
    RespuestaGenerica consultaPoliticasXEmpresaId(Integer id) throws ServiceException;
        
    RespuestaGenerica consultaBeneficiosPoliticaId(Integer idPolitica, Integer idCliente) throws ServiceException;
    
    RespuestaGenerica guardar(NclPolitica nclPolitica) throws ServiceException;

    
    RespuestaGenerica modificar(NclPoliticaDto nclPolitica) throws ServiceException;
        
    RespuestaGenerica guardaPoliticaEstandar(NclPolitica nclPolitica) throws ServiceException;
    
    RespuestaGenerica guardaBeneficiosEstandar(List<BeneficioXpolitica> listabeneficioXpolitica) throws ServiceException;
    
    RespuestaGenerica eliminar(NclPolitica nclPolitica) throws ServiceException;

    RespuestaGenerica findByEsActivo(Boolean activo) throws ServiceException;
    
}
