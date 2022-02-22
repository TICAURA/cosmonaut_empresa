package mx.com.ga.cosmonaut.empresa.services.impl;

import mx.com.ga.cosmonaut.common.dto.NclGrupoNominaDto;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.entity.cliente.NclGrupoNomina;
import mx.com.ga.cosmonaut.common.entity.cliente.NclPolitica;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.common.repository.cliente.NclGrupoNominaRepository;
import mx.com.ga.cosmonaut.common.repository.colaborador.NcoContratoColaboradorRepository;
import mx.com.ga.cosmonaut.common.repository.nativo.GrupoNominaRepository;
import mx.com.ga.cosmonaut.common.util.Constantes;
import mx.com.ga.cosmonaut.common.util.ObjetoMapper;
import mx.com.ga.cosmonaut.empresa.services.GrupoNominaService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class GrupoNominaServiceImpl implements GrupoNominaService {

    @Inject
    private NclGrupoNominaRepository nclGrupoNominaRepository;

    @Inject
    private GrupoNominaRepository grupoNominaRepository;

    @Inject
    private NcoContratoColaboradorRepository ncoContratoColaboradorRepository;


    @Override
    public RespuestaGenerica guardar(NclGrupoNominaDto grupoNominaDto) throws ServiceException {
          try {
              RespuestaGenerica respuesta = validarCamposObligatorios(grupoNominaDto);
              if (respuesta.isResultado()) {
                  RespuestaGenerica respuestaRequest  = new RespuestaGenerica();
                  respuestaRequest = validarEstructura(grupoNominaDto);
                  if (respuestaRequest.isResultado()) {
                      grupoNominaDto.setEsActivo(Constantes.ESTATUS_ACTIVO);
                      respuesta.setDatos(ObjetoMapper.map(
                              nclGrupoNominaRepository.save(
                                      ObjetoMapper.map(grupoNominaDto, NclGrupoNomina.class)),
                              NclGrupoNominaDto.class));
                      respuesta.setResultado(Constantes.RESULTADO_EXITO);
                      respuesta.setMensaje(Constantes.EXITO);
                  } else {
                      respuesta.setResultado(respuestaRequest.isResultado());
                      respuesta.setMensaje(respuestaRequest.getMensaje());
                  }
              }
                  return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" guardar " + Constantes.ERROR_EXCEPCION, e);
        }
    }


    private RespuestaGenerica validarEstructura(NclGrupoNominaDto nclGrupoNominaDto) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            if (validaDuplicados(nclGrupoNominaDto)) {
                respuesta.setMensaje(Constantes.EXITO);
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
            } else {
                respuesta.setMensaje("El nombre de este grupo de nómina ya existe");
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
            }
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " validarEstructura " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica listarTodos(Long idCompania) throws ServiceException {
        try{
            RespuestaGenerica respuesta =  new RespuestaGenerica();
            respuesta.setDatos(grupoNominaRepository.consultaListaGrupoNomina(idCompania.intValue()));
            respuesta.setResultado(true);
            respuesta.setMensaje(Constantes.EXITO);
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" listarTodos " + Constantes.ERROR_EXCEPCION, e);
        }
    }


    @Override
    public RespuestaGenerica modificar(NclGrupoNominaDto grupoNominaDto) throws ServiceException {
        boolean editable;
        try{
            if (grupoNominaDto.getGrupoNominaId() != null){
                RespuestaGenerica respuesta = validarCamposObligatorios(grupoNominaDto);
                if (respuesta.isResultado()){
                    List<NclGrupoNomina> nclGrupoNomina =  nclGrupoNominaRepository.duplicadoCentroIDNominaID(grupoNominaDto.getCentrocClienteId().getCentrocClienteId(),grupoNominaDto.getGrupoNominaId());
                    editable=   nclGrupoNomina.get(0).getNombre().toUpperCase().trim().equals(grupoNominaDto.getNombre().toUpperCase().trim());
                    if (editable) {
                        editable = true;
                    } else {
                        editable= validaDuplicados(grupoNominaDto);
                    }
                    if (editable) {
                        respuesta.setDatos(ObjetoMapper.map(
                                nclGrupoNominaRepository.update(
                                        ObjetoMapper.map(grupoNominaDto,
                                                NclGrupoNomina.class)), NclGrupoNomina.class));
                        respuesta.setResultado(Constantes.RESULTADO_EXITO);
                        respuesta.setMensaje(Constantes.EXITO);
                    } else {
                        respuesta.setMensaje("El nombre de este grupo de nómina ya existe");
                        respuesta.setResultado(Constantes.RESULTADO_ERROR);
                    }
                }
                return respuesta;
            }else{
                return new RespuestaGenerica(null,Constantes.RESULTADO_ERROR,Constantes.ID_NULO);
            }
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" modificar " + Constantes.ERROR_EXCEPCION, e);
        }
    }


    @Override
    public RespuestaGenerica eliminar(Long grupoNominaId) throws ServiceException {
        try{
            if (grupoNominaId != null){
                RespuestaGenerica respuesta = new RespuestaGenerica();
                if (!ncoContratoColaboradorRepository.existsByGrupoNominaIdGrupoNominaId(grupoNominaId.intValue())){
                    nclGrupoNominaRepository.update(grupoNominaId.intValue(),false);
                    respuesta.setResultado(Constantes.RESULTADO_EXITO);
                    respuesta.setMensaje(Constantes.EXITO);
                }else {
                    respuesta.setResultado(Constantes.RESULTADO_ERROR);
                    respuesta.setMensaje(Constantes.ERROR_GRUPO_NOMINA_EXISTENTE);
                }
                return respuesta;
            }else{
                return new RespuestaGenerica(null,Constantes.RESULTADO_ERROR,Constantes.ID_NULO);
            }
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" eliminar " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica obtenerId(Long idGrupoNomina) throws ServiceException {
        try{
            RespuestaGenerica respuesta = new RespuestaGenerica();
            respuesta.setDatos(ObjetoMapper.map(
                    nclGrupoNominaRepository.findById(idGrupoNomina.intValue()).orElse(
                            new NclGrupoNomina()), NclGrupoNomina.class));
            respuesta.setResultado(true);
            respuesta.setMensaje(Constantes.EXITO);
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" obtenerId " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica listaDinamica(NclGrupoNominaDto grupoNominaDto) throws ServiceException {
        
        try{
            RespuestaGenerica respuesta =  new RespuestaGenerica();
            //Se realiza una correccion para el conteo de usuarios unicamente con contrato activo
            respuesta.setDatos(grupoNominaRepository.consultaDimanicaGrupoNomina(grupoNominaDto));
            respuesta.setResultado(true);
            respuesta.setMensaje(Constantes.EXITO);
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" listaDinamica " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private boolean validaDuplicados(NclGrupoNominaDto nclGrupoNominaDto) throws ServiceException {
        try {
            List<NclGrupoNomina> nclGrupoNomina = nclGrupoNominaRepository.
                    existsByCentrocClienteIdCentrocClienteIdAndNominaAndEsActivo(
                            nclGrupoNominaDto.getCentrocClienteId().getCentrocClienteId(), nclGrupoNominaDto.getNombre().toUpperCase().trim());
            return nclGrupoNomina.size() == 0;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " validaDuplicados " + Constantes.ERROR_EXCEPCION, e);
        }
    }


    private RespuestaGenerica validarCamposObligatorios(NclGrupoNominaDto grupoNominaDto) throws ServiceException {
        try{
            RespuestaGenerica respuesta =  new RespuestaGenerica();
            if(grupoNominaDto.getNombre() == null
                    || grupoNominaDto.getNombre().isEmpty()
                    || grupoNominaDto.getCentrocClienteId() == null
                    || grupoNominaDto.getCentrocClienteId().getCentrocClienteId() == null
                    || grupoNominaDto.getCuentaBancoId() == null
                    || grupoNominaDto.getCuentaBancoId().getCuentaBancoId() == null
                    || grupoNominaDto.getMonedaId() == null
                    || grupoNominaDto.getMonedaId().getMonedaId() == null
                    || grupoNominaDto.getPeriodicidadPagoId() == null
                    || grupoNominaDto.getPeriodicidadPagoId().getPeriodicidadPagoId() == null
                    || grupoNominaDto.getBasePeriodoId() == null
                    || grupoNominaDto.getBasePeriodoId().getBasePeriodoId() == null
                    || grupoNominaDto.getPeriodoAguinaldoId() == null
                    || grupoNominaDto.getPeriodoAguinaldoId().getPeriodoAguinaldoId() == null
                    || grupoNominaDto.getManeraCalcularSubsidio() == null){
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(Constantes.CAMPOS_REQUERIDOS);
            }else{
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
                respuesta.setMensaje(Constantes.EXITO);
            }
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" validarCamposObligatorios " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica findByEsActivo(Boolean activo) throws ServiceException {
        RespuestaGenerica respuestaGenerica = new RespuestaGenerica();
        try {
            respuestaGenerica.setDatos(nclGrupoNominaRepository.findByEsActivoOrderByNombre(activo));
            respuestaGenerica.setResultado(Constantes.RESULTADO_EXITO);
            respuestaGenerica.setMensaje(Constantes.EXITO);
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" findByEsActivo" + Constantes.ERROR_EXCEPCION, e);
        }
        return respuestaGenerica;
    }
}
