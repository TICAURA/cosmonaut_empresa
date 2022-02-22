package mx.com.ga.cosmonaut.empresa.services.impl;

import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.common.repository.nativo.ColaboradorGrupoNominaRepository;
import mx.com.ga.cosmonaut.common.util.Constantes;
import mx.com.ga.cosmonaut.empresa.services.ColaboradorGrupoNominaService;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ColaboradorGrupoNominaServiceImpl implements ColaboradorGrupoNominaService {

    @Inject
    private ColaboradorGrupoNominaRepository colaboradorGrupoNominaRepository;

    @Override
    public RespuestaGenerica listaIdGrupoNomina(Long idGrupoNomina) throws ServiceException {
        try{
            RespuestaGenerica respuesta = new RespuestaGenerica();
            respuesta.setDatos(
                    colaboradorGrupoNominaRepository.consultaGrupoNominaPersona(idGrupoNomina.intValue()));
            respuesta.setResultado(true);
            respuesta.setMensaje(Constantes.EXITO);
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" listaIdGrupoNomina " + Constantes.ERROR_EXCEPCION, e);
        }
    }
}
