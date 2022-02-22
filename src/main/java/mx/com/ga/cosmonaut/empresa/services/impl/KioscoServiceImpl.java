package mx.com.ga.cosmonaut.empresa.services.impl;

import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.entity.calculo.NcrEmpleadoXnominaPK;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.common.repository.nativo.TimbradoRepository;
import mx.com.ga.cosmonaut.common.util.Constantes;
import mx.com.ga.cosmonaut.empresa.services.KioscoService;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class KioscoServiceImpl implements KioscoService {

    @Inject
    private TimbradoRepository timbradoRepository;

    @Override
    public RespuestaGenerica listaEmpleado(NcrEmpleadoXnominaPK ncrEmpleadoXnomina) throws ServiceException {
        try{
            return new RespuestaGenerica(timbradoRepository.listaEmpleados(
                            ncrEmpleadoXnomina.getPersonaId().intValue(),
                            ncrEmpleadoXnomina.getCentrocClienteId().intValue()),
                    Constantes.RESULTADO_EXITO, Constantes.EXITO);
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" listaEmpleado " + Constantes.ERROR_EXCEPCION, e);
        }
    }
}
