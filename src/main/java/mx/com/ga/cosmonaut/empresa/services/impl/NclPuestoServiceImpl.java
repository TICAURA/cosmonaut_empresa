package mx.com.ga.cosmonaut.empresa.services.impl;

import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import mx.com.ga.cosmonaut.common.dto.NclPuestoDto;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.repository.cliente.NclPuestoRepository;
import mx.com.ga.cosmonaut.common.repository.nativo.AbstractPuestoRepository;
import mx.com.ga.cosmonaut.empresa.services.NclPuestoService;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.common.repository.cliente.NclPuestoXareaRepository;
import mx.com.ga.cosmonaut.common.util.Constantes;
import mx.com.ga.cosmonaut.common.util.ObjetoMapper;

@Singleton
public class NclPuestoServiceImpl implements NclPuestoService {

    @Inject
    private AbstractPuestoRepository abstractPuestoRepository;

    @Inject
    NclPuestoXareaRepository nclPuestoXareaRepository;

    @Inject
    private NclPuestoRepository nclPuestoRepository;

    @Override
    public RespuestaGenerica findAll() throws ServiceException {

        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            List<NclPuestoDto> lista = abstractPuestoRepository.consultaPuestos();
            respuesta.setDatos(ObjetoMapper.mapAll(lista, NclPuestoDto.class));
            respuesta.setMensaje(Constantes.EXITO);
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " findAll " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    public RespuestaGenerica obtenerId(Integer idPuesto) throws ServiceException {

        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            List<NclPuestoDto> lista = abstractPuestoRepository.consultaPuestosId(idPuesto);
            respuesta.setDatos(ObjetoMapper.mapAll(lista, NclPuestoDto.class));
            respuesta.setResultado(true);
            respuesta.setMensaje(Constantes.EXITO);
            return respuesta;
        } catch (Exception e) {
           throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " obtenerId " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    public RespuestaGenerica obtenerIdCentroCliente(Integer idCentroCliente) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            List<NclPuestoDto> lista = abstractPuestoRepository.consultaPuestosxEmpresa(idCentroCliente);
            respuesta.setDatos(ObjetoMapper.mapAll(lista, NclPuestoDto.class));
            respuesta.setResultado(true);
            respuesta.setMensaje(Constantes.EXITO);
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " obtenerIdCentroCliente " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    public RespuestaGenerica obtenerIdCentroClienteArea(Integer idCentroCliente, Integer idArea) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            List<NclPuestoDto> lista = abstractPuestoRepository.consultaPuestosxEmpresaArea(idCentroCliente, idArea);
            respuesta.setDatos(ObjetoMapper.mapAll(lista, NclPuestoDto.class));
            respuesta.setResultado(true);
            respuesta.setMensaje(Constantes.EXITO);
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " obtenerIdCentroClienteArea " + Constantes.ERROR_EXCEPCION, e);
        }
    }
    
    public RespuestaGenerica obtenerPuestosXArea(Integer idArea) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            List<NclPuestoDto> lista = abstractPuestoRepository.obtenerPuestosxArea(idArea);
            respuesta.setDatos(ObjetoMapper.mapAll(lista, NclPuestoDto.class));
            respuesta.setResultado(true);
            respuesta.setMensaje(Constantes.EXITO);
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " obtenerPuestosXArea " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica findByEsActivo(Boolean activo) throws ServiceException {
        RespuestaGenerica respuestaGenerica = new RespuestaGenerica();
        try {
            respuestaGenerica.setDatos(nclPuestoRepository.findByEsActivoOrderByDescripcion(activo));
            respuestaGenerica.setResultado(Constantes.RESULTADO_EXITO);
            respuestaGenerica.setMensaje(Constantes.EXITO);
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" findByEsActivo" + Constantes.ERROR_EXCEPCION, e);
        }
        return respuestaGenerica;
    }

}
