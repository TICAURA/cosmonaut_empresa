
package mx.com.ga.cosmonaut.empresa.services.impl;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import mx.com.ga.cosmonaut.common.dto.NcoContratoColaboradorDto;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.entity.colaborador.NcoContratoColaborador;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.common.repository.colaborador.NcoContratoColaboradorRepository;
import mx.com.ga.cosmonaut.common.util.Constantes;
import mx.com.ga.cosmonaut.common.util.ObjetoMapper;
import mx.com.ga.cosmonaut.empresa.services.NcoContratoColaboradorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@Singleton
public class NcoContratoColaboradorServiceImpl implements NcoContratoColaboradorService {

    private static final Logger LOG = LoggerFactory.getLogger(NcoContratoColaboradorServiceImpl.class);
    private final RespuestaGenerica respuesta = new RespuestaGenerica();

    @Inject
    private NcoContratoColaboradorRepository ncoContratoColaboradorRepository;

    @Override
    public RespuestaGenerica findAll() throws ServiceException {

        try {
            List<NcoContratoColaborador> lista = new ArrayList<>();
            ncoContratoColaboradorRepository.findAll().forEach(lista::add);
            respuesta.setDatos(ObjetoMapper.mapAll(lista, NcoContratoColaboradorDto.class));
            respuesta.setMensaje(Constantes.EXITO);
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            return respuesta;
        } catch (Exception e) {
            LOG.error(Constantes.EXCEPCION, e);
            throw new ServiceException(Constantes.ERROR);
        }
    }

}
