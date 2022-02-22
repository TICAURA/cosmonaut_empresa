package mx.com.ga.cosmonaut.empresa.services.impl;

import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import mx.com.ga.cosmonaut.common.dto.NclPoliticaDto;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.entity.cliente.BeneficioXpolitica;
import mx.com.ga.cosmonaut.common.entity.cliente.NclBeneficioXpolitica;
import mx.com.ga.cosmonaut.common.entity.cliente.NclPolitica;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.common.interceptor.BitacoraUsuario;
import mx.com.ga.cosmonaut.common.repository.cliente.BeneficioXpoliticaRepository;
import mx.com.ga.cosmonaut.common.repository.cliente.NclBeneficioXpoliticaRepository;
import mx.com.ga.cosmonaut.common.repository.nativo.AbstractPoliticasRepository;
import mx.com.ga.cosmonaut.common.util.Constantes;
import mx.com.ga.cosmonaut.common.repository.cliente.NclPoliticaRepository;
import mx.com.ga.cosmonaut.common.repository.colaborador.NcoContratoColaboradorRepository;
import mx.com.ga.cosmonaut.common.util.ObjetoMapper;
import mx.com.ga.cosmonaut.empresa.services.NclPoliticaService;

@Singleton
public class NclPoliticaServiceImpl implements NclPoliticaService {

    @Inject
    NclPoliticaRepository nclPoliticaRepository;

    @Inject
    AbstractPoliticasRepository abstractPoliticasRepository;

    @Inject
    NcoContratoColaboradorRepository ncoContratoColaboradorRepository;

    @Inject
    NclBeneficioXpoliticaRepository nclBeneficioXpoliticaRepository;

    @Inject
    BeneficioXpoliticaRepository beneficioXpoliticaRepository;

    public RespuestaGenerica findAll() throws ServiceException {

        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            respuesta.setDatos(nclPoliticaRepository.findAll());
            respuesta.setMensaje(Constantes.EXITO);
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " findAll " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    public RespuestaGenerica consultaPoliticaId(Integer id) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            respuesta.setDatos(nclPoliticaRepository.findById(id));
            respuesta.setMensaje(Constantes.EXITO);
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " consultaPoliticaId " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    public RespuestaGenerica consultaPoliticaXEmpPol(Integer idPolitica, Integer idCliente) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            respuesta.setDatos(abstractPoliticasRepository.consultaPoliticaXEmpPol(idPolitica, idCliente));
            respuesta.setMensaje(Constantes.EXITO);
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " consultaPoliticaXEmpPol " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica consultaPoliticaEmpresaId(Integer id, Integer idPolitica) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();

            respuesta.setDatos(nclPoliticaRepository.consultaPoliticaEmpresaId(id, idPolitica));
            respuesta.setMensaje(Constantes.EXITO);
            respuesta.setResultado(Constantes.RESULTADO_EXITO);

            return respuesta;
        } catch (Exception e) {

            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " consultaPoliticaEmpresaId " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica consultaPoliticasXEmpresaId(Integer id) throws ServiceException {

        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            respuesta.setDatos(abstractPoliticasRepository.consultaPoliticasXEmpresa(id));
            respuesta.setMensaje(Constantes.EXITO);
            respuesta.setResultado(Constantes.RESULTADO_EXITO);return respuesta;

        } catch (Exception e) {

            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " consultaPoliticasXEmpresaId " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica consultaBeneficiosPoliticaId(Integer idPolitica, Integer idCliente) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            respuesta.setDatos(nclBeneficioXpoliticaRepository.consultaBeneficiosPoliticaId(idPolitica, idCliente));
            respuesta.setMensaje(Constantes.EXITO);
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " consultaBeneficiosPoliticaId " + Constantes.ERROR_EXCEPCION, e);
        }
    }


    @Override
    public RespuestaGenerica guardar(NclPolitica nclPolitica) throws ServiceException {
        try {
            RespuestaGenerica respuesta = validarCamposObligatorios(nclPolitica);
            respuesta = guardaPolitica(nclPolitica, respuesta);
            if (respuesta.isResultado()) {
                List<BeneficioXpolitica> beneficios
                        = beneficioXpoliticaRepository.findByPoliticaIdPoliticaIdAndPoliticaIdEsEstandar(
                                nclPoliticaRepository.findByCentrocClienteIdCentrocClienteIdAndEsEstandar(
                                        nclPolitica.getCentrocClienteId().getCentrocClienteId(), Constantes.ESTATUS_ACTIVO).getPoliticaId(),
                                Constantes.ESTATUS_ACTIVO);
                if (!beneficios.isEmpty()) {
                    for (BeneficioXpolitica b : beneficios) {
                        b.setBeneficioPolitica(null);
                        b.getPoliticaId().setPoliticaId((Integer) respuesta.getDatos());
                        beneficioXpoliticaRepository.save(b);
                    }
                }
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
                respuesta.setMensaje(Constantes.EXITO);
            } else {
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
            }
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " guardar " + Constantes.ERROR_EXCEPCION, e);
        }

    }

    public RespuestaGenerica guardaPolitica(NclPolitica nclPolitica, RespuestaGenerica respuesta) throws ServiceException {
        try {
            if (respuesta.isResultado()) {
                respuesta = validarEstructura(nclPolitica);
                if (respuesta.isResultado()) {
                    nclPolitica.setEsActivo(Constantes.ESTATUS_ACTIVO);
                    nclPolitica.setEsEstandar(Constantes.ESTATUS_INACTIVO);
                    respuesta.setDatos(nclPoliticaRepository.save(nclPolitica).getPoliticaId());
                    respuesta.setResultado(Constantes.RESULTADO_EXITO);
                    respuesta.setMensaje(Constantes.EXITO);
                }
            } else {
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(Constantes.ERROR);
            }
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " guardaPolitica " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    public RespuestaGenerica guardaPoliticaEstandar(NclPolitica nclPolitica) throws ServiceException {
        try {
            RespuestaGenerica respuesta = validarCamposObligatorios(nclPolitica);
            if (respuesta.isResultado()) {
                respuesta = validarEstructura(nclPolitica);
                if (respuesta.isResultado()) {
                    nclPolitica.setEsActivo(Constantes.ESTATUS_ACTIVO);
                    nclPolitica.setEsEstandar(Constantes.ESTATUS_ACTIVO);
                    if (nclPoliticaRepository.obtienePoliticaXEmpresaNombreEstandar(nclPolitica.getCentrocClienteId().getCentrocClienteId(), Constantes.ESTATUS_ACTIVO).isEmpty()) {
                        respuesta.setDatos(nclPoliticaRepository.save(nclPolitica));
                        respuesta.setResultado(Constantes.RESULTADO_EXITO);
                        respuesta.setMensaje(Constantes.EXITO);
                    } else {
                        respuesta.setMensaje(Constantes.POLITICA_ESTANDAR_DUPLICADA);
                        respuesta.setResultado(Constantes.RESULTADO_ERROR);
                    }
                }
            }
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " guardarPoliticaEstandar " + Constantes.ERROR_EXCEPCION, e);
        }

    }

    public RespuestaGenerica guardaBeneficiosEstandar(List<BeneficioXpolitica> listabeneficioXpolitica) throws ServiceException {
        RespuestaGenerica respuesta = new RespuestaGenerica();
        try {
            if (!listabeneficioXpolitica.isEmpty()) {
                for (BeneficioXpolitica b : listabeneficioXpolitica) {
                    respuesta = validarBeneficiosObligatorios(b);
                    if (respuesta.isResultado()) {
                        b.setEsActivo(Constantes.ESTATUS_ACTIVO);
                        respuesta.setDatos(beneficioXpoliticaRepository.save(b));
                        respuesta.setResultado(Constantes.RESULTADO_EXITO);
                        respuesta.setMensaje(Constantes.EXITO);
                    } else {
                        respuesta.setMensaje(Constantes.BENEFICIOS_INCOMPLETOS);
                        respuesta.setResultado(Constantes.RESULTADO_ERROR);
                    }
                }
            } else {
                respuesta.setMensaje(Constantes.ERROR);
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
            }

            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " guardarPoliticaEstandar " + Constantes.ERROR_EXCEPCION, e);
        }

    }


    @Override
    public RespuestaGenerica modificar(NclPoliticaDto nclPoliticaDto) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            if (nclPoliticaDto != null && nclPoliticaDto.getPoliticaId() != null) {
                respuesta = validarCamposObligatorios(ObjetoMapper.map(nclPoliticaDto, NclPolitica.class));
                if (respuesta.isResultado()) {
                    List<NclPolitica> lista = nclPoliticaRepository.consultaPoliticaEmpresaId(nclPoliticaDto.getCentrocClienteId().getCentrocClienteId(), nclPoliticaDto.getPoliticaId());
                    if (!lista.isEmpty()) {
                        respuesta = modificaPolitica(nclPoliticaDto, lista);
                    } else {
                        respuesta.setMensaje(Constantes.CONSULTA_POLITICA);
                        respuesta.setResultado(Constantes.RESULTADO_ERROR);
                    }
                }
            } else {
                respuesta.setMensaje(Constantes.POLITICA_REQ);
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
            }
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " modificar " + Constantes.ERROR_EXCEPCION, e);
        }

    }

    public RespuestaGenerica modificaPolitica(NclPoliticaDto nclPoliticaDto, List<NclPolitica> lista) throws ServiceException {
        RespuestaGenerica respuestaModificar = new RespuestaGenerica();


        try {
            List<NclPolitica> politica = nclPoliticaRepository
                    .consultaPoliticaEmpresaId(nclPoliticaDto.getCentrocClienteId().getCentrocClienteId(), nclPoliticaDto.getPoliticaId());
            boolean editable= politica.get(0).getNombre().toUpperCase().trim().equals(nclPoliticaDto.getNombre().toUpperCase().trim());

            if (editable) {
                editable = true;
            } else {
                editable= validaDuplicadosEditar(nclPoliticaDto.getCentrocClienteId().getCentrocClienteId(), nclPoliticaDto.getNombre());
            }
                if (editable) {
                RespuestaGenerica respuesta = new RespuestaGenerica();
                if (lista.get(0).isEsEstandar()) {
                    respuesta.setMensaje(Constantes.POLITICA_ESTANDAR);
                    respuesta.setResultado(Constantes.RESULTADO_ERROR);
                } else {
                    if (!nclPoliticaDto.getBeneficiosXPolitica().isEmpty() || nclPoliticaDto.getBeneficiosXPolitica() != null) {

                        List<BeneficioXpolitica> listaBeneficioXpolitica
                                = nclPoliticaDto.getBeneficiosXPolitica().stream().filter(beneficioXpolitica ->
                            beneficioXpolitica.getDiasAguinaldo() < Constantes.BENEFICIO_ESTANDAR_DIAS_AGUINALDO
                            || beneficioXpolitica.getDiasVacaciones() < Constantes.BENEFICIO_ESTANDAR_DIAS_VACACIONES
                            || beneficioXpolitica.getPrimaVacacional().doubleValue() <
                                    Constantes.BENEFICIO_ESTANDAR_DIAS_PRIMA_VACACIONAL.doubleValue())
                                .collect(Collectors.toList());
                        if (!listaBeneficioXpolitica.isEmpty()){
                            return new RespuestaGenerica(null,Constantes.RESULTADO_ERROR,Constantes.ERROR_BENEFICIOS_POLITICA_ESTANDAR);
                        }

                        for (BeneficioXpolitica beneficio : nclPoliticaDto.getBeneficiosXPolitica()) {
                            beneficio.setPoliticaId(new NclPolitica());
                            beneficio.getPoliticaId().setPoliticaId(nclPoliticaDto.getPoliticaId());
                            beneficio.setEsActivo(Constantes.ESTATUS_ACTIVO);
                            respuesta = validarBeneficiosObligatorios(beneficio);
                            if (respuesta.isResultado()) {
                                beneficioXpoliticaRepository.update(beneficio);
                            } else {
                                respuesta.setMensaje(Constantes.BENEFICIOS_INCOMPLETOS);
                                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                            }
                        }
                    }
                    nclPoliticaDto.setEsActivo(Constantes.ESTATUS_ACTIVO);
                    nclPoliticaDto.setEsEstandar(Constantes.ESTATUS_INACTIVO);
                    respuesta.setDatos(nclPoliticaRepository.update(ObjetoMapper.map(nclPoliticaDto, NclPolitica.class)));
                    respuesta.setResultado(Constantes.RESULTADO_EXITO);
                    respuesta.setMensaje(Constantes.EXITO);
                }
                return respuesta;
            } else {
                    respuestaModificar.setResultado(Constantes.RESULTADO_ERROR);
                    respuestaModificar.setMensaje(Constantes.POLITICA_DUPLICADA);
                }
                return respuestaModificar;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " modificaPolitica " + Constantes.ERROR_EXCEPCION, e);
        }

    }

    private RespuestaGenerica validarBeneficiosObligatorios(BeneficioXpolitica beneficioXpolitica) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            if (beneficioXpolitica.getPoliticaId() == null
                    || beneficioXpolitica.getPoliticaId().getPoliticaId() == null
                    || beneficioXpolitica.getAniosAntiguedad() == null
                    || beneficioXpolitica.getDiasAguinaldo() == null
                    || beneficioXpolitica.getDiasVacaciones() == null
                    || beneficioXpolitica.getPrimaVacacional() == null) {
                respuesta.setMensaje(Constantes.CAMPOS_REQUERIDOS);
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
            } else {
                respuesta.setMensaje(Constantes.EXITO);
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
            }
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " validarCamposObligatorios " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private RespuestaGenerica validarCamposObligatorios(NclPolitica nclPolitica) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            if (nclPolitica.getNombre() == null
                    || nclPolitica.getDiasEconomicos() == null
                    || nclPolitica.getCalculoAntiguedadId() == null
                    || nclPolitica.getCalculoAntiguedadId().getCalculoAntiguedadxId() == null
                    || nclPolitica.getCentrocClienteId().getCentrocClienteId() == null) {
                respuesta.setMensaje(Constantes.CAMPOS_REQUERIDOS);
                respuesta.setResultado(Constantes.RESULTADO_ERROR);

            } else {
                respuesta.setMensaje(Constantes.EXITO);
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
            }
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " validarCamposObligatorios " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private RespuestaGenerica validarEstructura(NclPolitica nclPolitica) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            if (validaDuplicados(nclPolitica.getCentrocClienteId().getCentrocClienteId(),nclPolitica.getNombre())) {
                respuesta.setMensaje(Constantes.EXITO);
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
            } else {
                respuesta.setMensaje(Constantes.POLITICA_DUPLICADA);
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
            }
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " validarEstructura " + Constantes.ERROR_EXCEPCION, e);
        }
    }


    private RespuestaGenerica validarEstructuraEditar(NclPolitica nclPolitica) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            if (validaDuplicados(nclPolitica.getCentrocClienteId().getCentrocClienteId(),nclPolitica.getNombreCorto())) {
                respuesta.setMensaje(Constantes.EXITO);
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
            } else {
                respuesta.setMensaje(Constantes.POLITICA_DUPLICADA);
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
            }
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " validarEstructura " + Constantes.ERROR_EXCEPCION, e);
        }
    }


    private boolean validaDuplicados(Integer id, String nombre) throws ServiceException {
        try {
            List<NclPolitica> nclPoliticas = nclPoliticaRepository.
                    existsByCentrocClienteIdCentrocClienteIdAndNombreAndEsActivo(
                            id, nombre.toUpperCase().trim());
            if(nclPoliticas==null)
                return true;
            return nclPoliticas.size() == 0;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " validaDuplicados " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private boolean validaDuplicadosEditar(Integer id, String nombre) throws ServiceException {
        try {
            List<NclPolitica> nclPoliticas = nclPoliticaRepository.
                    existsByCentrocClienteIdCentrocClienteIdAndNombreAndEsActivo(
                            id, nombre.toUpperCase().trim());
            return nclPoliticas.size() == 0;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " validaDuplicados " + Constantes.ERROR_EXCEPCION, e);
        }
    }


    @Override
    public RespuestaGenerica eliminar(NclPolitica nclPolitica) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            if (nclPolitica.getPoliticaId() != null && nclPolitica.getCentrocClienteId() != null) {
                List<NclPolitica> politica = nclPoliticaRepository.consultaPoliticaEmpresaId(nclPolitica.getCentrocClienteId().getCentrocClienteId(), nclPolitica.getPoliticaId());
                if (!politica.isEmpty()) {
                    respuesta = eliminaPolitica(nclPolitica, politica);
                } else {
                    respuesta.setMensaje(Constantes.CONSULTA_POLITICA);
                    respuesta.setResultado(Constantes.RESULTADO_ERROR);
                }
            } else {
                respuesta.setMensaje("Es necesario el Politica/Cliente para poder eliminar");
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
            }
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " eliminar " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    public RespuestaGenerica eliminaPolitica(NclPolitica nclPolitica, List<NclPolitica> politica) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            Integer count = 0;
            if (!politica.get(0).isEsEstandar()) {
                count = ncoContratoColaboradorRepository.findAreaPoliticaXColaborador(nclPolitica.getCentrocClienteId().getCentrocClienteId(), nclPolitica.getPoliticaId());
                if (count != null && count.equals(Constantes.CERO)) {
                    List<NclBeneficioXpolitica> beneficios = nclBeneficioXpoliticaRepository.consultaBeneficiosPoliticaId(nclPolitica.getPoliticaId(), nclPolitica.getCentrocClienteId().getCentrocClienteId());
                    if (!beneficios.isEmpty()) {
                        for (NclBeneficioXpolitica lista : beneficios) {
                            nclBeneficioXpoliticaRepository.update(lista.getBeneficioPolitica(), Constantes.ESTATUS_INACTIVO);
                        }
                    }
                    nclPoliticaRepository.update(nclPolitica.getPoliticaId(), Constantes.ESTATUS_INACTIVO);
                    respuesta.setMensaje(Constantes.EXITO);
                    respuesta.setResultado(Constantes.RESULTADO_EXITO);
                } else {
                    return new RespuestaGenerica(null, Constantes.RESULTADO_ERROR, Constantes.POLITICA_REL + count + Constantes.POLITICA_RELACIONADOS);
                }
            } else {
                return new RespuestaGenerica(null, Constantes.RESULTADO_ERROR, Constantes.POLITICA_ESTANDAR_ELIMINAR);
            }
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " eliminaPolitica " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica findByEsActivo(Boolean activo) throws ServiceException {
        RespuestaGenerica respuestaGenerica = new RespuestaGenerica();
        try {
            respuestaGenerica.setDatos(nclPoliticaRepository.findByEsActivoOrderByNombre(activo));
            respuestaGenerica.setResultado(Constantes.RESULTADO_EXITO);
            respuestaGenerica.setMensaje(Constantes.EXITO);
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" findByEsActivo" + Constantes.ERROR_EXCEPCION, e);
        }
        return respuestaGenerica;
    }

}
