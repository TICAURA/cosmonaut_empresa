package mx.com.ga.cosmonaut.empresa.services.impl;

import mx.com.ga.cosmonaut.common.dto.NclCentrocClienteDto;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.dto.RespuestaGoogleStorage;
import mx.com.ga.cosmonaut.common.dto.consultas.CentroCostosClienteConsulta;
import mx.com.ga.cosmonaut.common.dto.csd.CertificadoSelloDigitalPeticionDto;
import mx.com.ga.cosmonaut.common.dto.csd.CertificadoSelloDigitalRespuestaDto;
import mx.com.ga.cosmonaut.common.entity.catalogo.negocio.CatCalculoAntiguedad;
import mx.com.ga.cosmonaut.common.entity.catalogo.sat.CsActividadEconomica;
import mx.com.ga.cosmonaut.common.entity.cliente.BeneficioXpolitica;
import mx.com.ga.cosmonaut.common.entity.cliente.NclCentrocCliente;
import mx.com.ga.cosmonaut.common.entity.cliente.NclPolitica;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.common.repository.catalogo.sat.CsActividadEconomicaRepository;
import mx.com.ga.cosmonaut.common.repository.cliente.NclCentrocClienteRepository;
import mx.com.ga.cosmonaut.common.repository.cliente.NclGrupoNominaRepository;
import mx.com.ga.cosmonaut.common.repository.colaborador.NcoPersonaRepository;
import mx.com.ga.cosmonaut.common.repository.nativo.CentroCostoCienteRepository;
import mx.com.ga.cosmonaut.common.service.GoogleStorageService;
import mx.com.ga.cosmonaut.common.util.Constantes;
import mx.com.ga.cosmonaut.common.util.ObjetoMapper;
import mx.com.ga.cosmonaut.common.util.Utilidades;
import mx.com.ga.cosmonaut.common.util.Validar;
import mx.com.ga.cosmonaut.empresa.services.CentroCostosClienteService;
import mx.com.ga.cosmonaut.empresa.services.CertificadoSelloDigitalServices;
import mx.com.ga.cosmonaut.empresa.services.NclPoliticaService;
import mx.com.ga.cosmonaut.empresa.services.PercepcionDeduccionService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton
public class CentroCostosClienteServiceImpl implements CentroCostosClienteService {

    @Inject
    private NclCentrocClienteRepository nclCentrocClienteRepository;

    @Inject
    private CentroCostoCienteRepository centrocClienteRepository;

    @Inject
    private GoogleStorageService googleStorageService;

    @Inject
    private NclGrupoNominaRepository nclGrupoNominaRepository;

    @Inject
    private CertificadoSelloDigitalServices certificadoSelloDigitalServices;

    @Inject
    private NclPoliticaService politicaService;

    @Inject
    private NcoPersonaRepository ncoPersonaRepository;

    @Inject
    private PercepcionDeduccionService percepcionDeduccionService;

    @Inject
    private CsActividadEconomicaRepository csActividadEconomicaRepository;

    @Override
    public RespuestaGenerica guardarCompania(NclCentrocClienteDto centroCostosClienteDto) throws ServiceException {
        try{
            RespuestaGenerica respuesta = validaCamposObligatorios(centroCostosClienteDto);
            if(respuesta.isResultado()){
                respuesta = guardar(centroCostosClienteDto, respuesta);
            }
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" guardarCompania " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica guardarEmpresa(NclCentrocClienteDto centroCostosClienteDto) throws ServiceException {
        try{
            RespuestaGenerica respuesta = validaCamposObligatoriosEmpresa(centroCostosClienteDto);
            if(respuesta.isResultado()){
                CertificadoSelloDigitalRespuestaDto csd = guardarCsd(centroCostosClienteDto);
                if(csd.isExito()){
                    centroCostosClienteDto.setCertificadoSelloDigitalId(csd.getContenido()[0].getCsd_id());
                    RespuestaGenerica respuestaGenerica = guardar(centroCostosClienteDto, respuesta);
                    if (respuestaGenerica.isResultado()){
                        guardarDeducionesPercepciones(respuestaGenerica);
                    }
                    return respuestaGenerica;
                }
            }
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" guardarEmpresa " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private RespuestaGenerica guardar(NclCentrocClienteDto centroCostosClienteDto,
                                      RespuestaGenerica respuesta) throws ServiceException {
        try{
            if(respuesta.isResultado()){
                respuesta = validarEstructura(centroCostosClienteDto);
                if (respuesta.isResultado()){
                    if(centroCostosClienteDto.getImagen()!=null){
                        String ruta = generateUrl(centroCostosClienteDto);
                        googleStorageService.subirArchivo(centroCostosClienteDto.getImagen(),ruta);
                        centroCostosClienteDto.setUrlLogo(ruta);
                    }
                    centroCostosClienteDto.setEsActivo(Constantes.ESTATUS_ACTIVO);
                    centroCostosClienteDto = ObjetoMapper.map(
                            nclCentrocClienteRepository.save(
                                    ObjetoMapper.map(centroCostosClienteDto, NclCentrocCliente.class)),
                            NclCentrocClienteDto.class);

                    if(centroCostosClienteDto.getActividadEconomicaId() != null
                            && centroCostosClienteDto.getActividadEconomicaId().getActividadEconomicaId() != null){
                        centroCostosClienteDto.setPadreActividadEconomicaId(csActividadEconomicaRepository.
                                findById(centroCostosClienteDto.getActividadEconomicaId().getActividadEconomicaId()).orElse(new CsActividadEconomica()));
                    }

                    respuesta.setDatos(centroCostosClienteDto);
                    respuesta.setResultado(Constantes.RESULTADO_EXITO);
                    respuesta.setMensaje(Constantes.EXITO);
                    if (centroCostosClienteDto.getCentroCostosCentrocClienteId() != null &&
                            centroCostosClienteDto.getCentroCostosCentrocClienteId().getCentrocClienteId() != null){
                        RespuestaGenerica respuestaPolitica = guardarPoliticaEstandar(centroCostosClienteDto.getCentrocClienteId());
                        guardarBeneficiosPolitica((NclPolitica) respuestaPolitica.getDatos());
                    }
                }
            }
            return respuesta;
        }catch (Exception e ){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" guardar " + Constantes.ERROR_EXCEPCION, e);
        }
    }
    

    @Override
    public RespuestaGenerica listarCompania() throws ServiceException {
        try{
            List<NclCentrocClienteDto> listaCentroCliente = ObjetoMapper.mapAll(
                    nclCentrocClienteRepository.findByCentroCostoCliente(),
                    NclCentrocClienteDto.class);
            return obtenerListaLogo(listaCentroCliente);
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" listarCompania " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica listarCompaniaCombo() throws ServiceException {
        try{
            return new RespuestaGenerica(ObjetoMapper.mapAll(
                    nclCentrocClienteRepository.findByCentroCostoCliente(),
                    NclCentrocClienteDto.class),Constantes.RESULTADO_EXITO,Constantes.EXITO);
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" listarCompania " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica listarCompaniaSimple() throws ServiceException {
        try{
            RespuestaGenerica respuestaGenerica = new RespuestaGenerica();
            respuestaGenerica.setDatos(nclCentrocClienteRepository
                    .findByEsActivoAndCentroCostosCentrocClienteIdCentrocClienteIdIsNull(Constantes.ESTATUS_ACTIVO));
            respuestaGenerica.setResultado(Constantes.RESULTADO_EXITO);
            respuestaGenerica.setMensaje(Constantes.EXITO);
            return respuestaGenerica;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" listarCompania " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica listaCompaniaEmpresa(Long idCentroCostosCliente) throws ServiceException {
        try{
            List<NclCentrocClienteDto> listaCentroCliente = ObjetoMapper.mapAll(
                    nclCentrocClienteRepository.findByCentroCostosCentrocClienteIdCentrocClienteId(idCentroCostosCliente.intValue()),
                    NclCentrocClienteDto.class);
            return obtenerListaLogo(listaCentroCliente);
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" listaCompaniaEmpresa " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica listaEmpresaSimple() throws ServiceException {
        try{
            RespuestaGenerica respuestaGenerica = new RespuestaGenerica();
            respuestaGenerica.setDatos(nclCentrocClienteRepository
                    .findByEsActivoAndCentroCostosCentrocClienteIdCentrocClienteIdNotNull(Constantes.ESTATUS_ACTIVO));
            respuestaGenerica.setResultado(Constantes.RESULTADO_EXITO);
            respuestaGenerica.setMensaje(Constantes.EXITO);
            return respuestaGenerica;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" listarCompania " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private RespuestaGenerica obtenerListaLogo(List<NclCentrocClienteDto> listaCentroCliente) {
        List<NclCentrocClienteDto> lista = new ArrayList<>();
        listaCentroCliente.parallelStream().forEach(centrocClienteDto -> {
            if(centrocClienteDto.getActividadEconomicaId() != null
                    && centrocClienteDto.getActividadEconomicaId().getActividadEconomicaId() != null){
                centrocClienteDto.setPadreActividadEconomicaId(csActividadEconomicaRepository.
                        findById(centrocClienteDto.getActividadEconomicaId().getActividadEconomicaId()).orElse(new CsActividadEconomica()));
            }

            if (centrocClienteDto.getUrlLogo() != null && !centrocClienteDto.getUrlLogo().isEmpty()){
                RespuestaGoogleStorage respuestaStored = null;
                try {
                    respuestaStored = googleStorageService.obtenerArchivo(centrocClienteDto.getUrlLogo());
                } catch (ServiceException e) {
                    e.printStackTrace();
                }
                centrocClienteDto.setUrl(respuestaStored.getUrl());
            }

            centrocClienteDto.setCerKeyConstrasenia(centrocClienteDto.getCertificadoSelloDigitalId() != null
                    && !centrocClienteDto.getCertificadoSelloDigitalId().isEmpty());

            lista.add(centrocClienteDto);
        });

        return new RespuestaGenerica(lista,Constantes.RESULTADO_EXITO,Constantes.EXITO);
    }

    @Override
    public RespuestaGenerica modificarCompania(NclCentrocClienteDto centroCostosClienteDto) throws ServiceException {
        try{
            RespuestaGenerica respuesta = validaCamposObligatorios(centroCostosClienteDto);
            if(respuesta.isResultado()){
                respuesta = modificar(centroCostosClienteDto, respuesta);
            }
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" modificarCompania " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica modificarEmpresa(NclCentrocClienteDto centroCostosClienteDto) throws ServiceException {
        try{
            RespuestaGenerica respuesta = new RespuestaGenerica();
            if(centroCostosClienteDto.getCentroCostosCentrocClienteId().getCentrocClienteId() != null){
                respuesta = validaCamposObligatorios(centroCostosClienteDto);
                if (respuesta.isResultado()){
                    respuesta = validaCamposObligatoriosEmpresa(centroCostosClienteDto);
                    if(respuesta.isResultado()){
                        CertificadoSelloDigitalRespuestaDto csd;
                        if (centroCostosClienteDto.isCerKeyConstrasenia()){
                            csd = guardarCsd(centroCostosClienteDto);
                            centroCostosClienteDto.setCertificadoSelloDigitalId(csd.getContenido()[0].getCsd_id());
                        }
                        return modificar(centroCostosClienteDto, respuesta);
                    }
                }
            }else{
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(Constantes.ID_NULO);
            }
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" modificarEmpresa " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private RespuestaGenerica modificar(NclCentrocClienteDto centroCostosClienteDto,
                                       RespuestaGenerica respuesta) throws ServiceException {
        try{
            if (centroCostosClienteDto.getCentrocClienteId() != null){
                    if (cambioPrimitivoMultiEmpresa(centroCostosClienteDto.getMultiempresa())){
                        respuesta = validarEstructuraModificar(centroCostosClienteDto);
                    }else
                        respuesta=validarEstructuraModificarSinMultiEmp(centroCostosClienteDto);
                    if (respuesta.isResultado()){
                        if(centroCostosClienteDto.getImagen()!=null){
                            String ruta = generateUrl(centroCostosClienteDto);
                            googleStorageService.actualizarArchivo(centroCostosClienteDto.getImagen(),ruta);
                            centroCostosClienteDto.setUrlLogo(ruta);
                        }

                        centroCostosClienteDto = ObjetoMapper.map(
                                nclCentrocClienteRepository.update(
                                        ObjetoMapper.map(centroCostosClienteDto, NclCentrocCliente.class)),
                                NclCentrocClienteDto.class);

                        if(centroCostosClienteDto.getActividadEconomicaId() != null
                                && centroCostosClienteDto.getActividadEconomicaId().getActividadEconomicaId() != null){
                            centroCostosClienteDto.setPadreActividadEconomicaId(csActividadEconomicaRepository.
                                    findById(centroCostosClienteDto.getActividadEconomicaId().getActividadEconomicaId()).orElse(new CsActividadEconomica()));
                        }

                        respuesta.setDatos(centroCostosClienteDto);
                        respuesta.setResultado(Constantes.RESULTADO_EXITO);
                        respuesta.setMensaje(Constantes.EXITO);
                    }
            }else {
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(Constantes.ID_NULO);
            }
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" modificar " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica obtenerId(Long idCentroCostosCliente) throws ServiceException {
        try{
            RespuestaGenerica respuesta = new RespuestaGenerica();
            RespuestaGoogleStorage respuestaGoogle;
            NclCentrocCliente nclCentrocCliente = nclCentrocClienteRepository.findById(idCentroCostosCliente.intValue()).orElse(
                    new NclCentrocCliente());
            NclCentrocClienteDto nclCentrocClienteDto = ObjetoMapper.map(nclCentrocCliente, NclCentrocClienteDto.class);

            if (nclCentrocClienteDto.getUrlLogo() != null && !nclCentrocClienteDto.getUrlLogo().isEmpty()){
                respuestaGoogle = googleStorageService.obtenerArchivo(nclCentrocClienteDto.getUrlLogo());
                nclCentrocClienteDto.setUrl(respuestaGoogle.getUrl());
                nclCentrocClienteDto.setImagen(respuestaGoogle.getArreglo());
            }

            if(nclCentrocClienteDto.getActividadEconomicaId() != null
                    && nclCentrocClienteDto.getActividadEconomicaId().getActividadEconomicaId() != null){
                nclCentrocClienteDto.setPadreActividadEconomicaId(csActividadEconomicaRepository.
                        findById(nclCentrocClienteDto.getActividadEconomicaId().getActividadEconomicaId()).
                        orElse(new CsActividadEconomica()));
            }

            respuesta.setDatos(nclCentrocClienteDto);
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            respuesta.setMensaje(Constantes.EXITO);
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" obtenerId " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica modificarLista(List<NclCentrocClienteDto> listCentroCostosClienteDto) throws ServiceException{
        try{
            for (NclCentrocClienteDto dto:listCentroCostosClienteDto) {
                nclCentrocClienteRepository.update(dto.getCentrocClienteId(),dto.getEsActivo());
            }
            return listarCompania();
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" modificarLista " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica eliminarId(Long idCentroCostosCliente) throws ServiceException{
        try{
            RespuestaGenerica respuesta = new RespuestaGenerica();
            if (idCentroCostosCliente != null){
                NclCentrocCliente nclCentrocCliente = nclCentrocClienteRepository.findById(idCentroCostosCliente.intValue())
                        .orElse(new NclCentrocCliente());
                if (nclCentrocCliente.getCentroCostosCentrocClienteId() == null ){
                    nclCentrocClienteRepository.update(idCentroCostosCliente.intValue(),Constantes.ESTATUS_INACTIVO);
                    respuesta.setResultado(true);
                    respuesta.setMensaje(Constantes.EXITO);
                }else {
                    return new RespuestaGenerica(null,Constantes.RESULTADO_ERROR,Constantes.ERROR_ELIMINAR_COMPANIA);
                }
                return respuesta;
            }else{
                return new RespuestaGenerica(null,Constantes.RESULTADO_ERROR,Constantes.ID_NULO);
            }
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" eliminarId " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica eliminarEmpresa(Long idCentroCostosCliente) throws ServiceException{
        try{
            RespuestaGenerica respuesta = new RespuestaGenerica();
            if (idCentroCostosCliente != null){
                if (nclGrupoNominaRepository.findByCentrocClienteIdCentrocClienteId(
                        idCentroCostosCliente.intValue()).isEmpty()){
                    nclCentrocClienteRepository.update(idCentroCostosCliente.intValue(),Constantes.ESTATUS_INACTIVO);
                    respuesta.setResultado(true);
                    respuesta.setMensaje(Constantes.EXITO);
                }else {
                    return new RespuestaGenerica(null,Constantes.RESULTADO_ERROR,Constantes.ERROR_ELIMINAR_EMPRESA);
                }
                return respuesta;
            }else{
                return new RespuestaGenerica(null,Constantes.RESULTADO_ERROR,Constantes.ID_NULO);
            }
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" eliminarId " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica listaDinamica(NclCentrocClienteDto centrocClienteDto) throws ServiceException{
        try{
            RespuestaGenerica respuesta = new RespuestaGenerica();
            RespuestaGoogleStorage respuestaGoogle;
            List<CentroCostosClienteConsulta> listaCentroCliente = centrocClienteRepository.consultaDimanica(centrocClienteDto);
            List<CentroCostosClienteConsulta> lista = new ArrayList<>();
            for (CentroCostosClienteConsulta centroCostosClienteConsulta: listaCentroCliente) {
                if (centroCostosClienteConsulta.getUrlLogo() != null && !centroCostosClienteConsulta.getUrlLogo().isEmpty()){
                    respuestaGoogle = googleStorageService.obtenerArchivo(centroCostosClienteConsulta.getUrlLogo());
                    centroCostosClienteConsulta.setUrl(respuestaGoogle.getUrl());
                }
                lista.add(centroCostosClienteConsulta);
            }
            respuesta.setDatos(lista);
            respuesta.setResultado(true);
            respuesta.setMensaje(Constantes.EXITO);
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" listaDinamica " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica login(String correo) throws ServiceException {
        try{
            RespuestaGenerica respuesta = new RespuestaGenerica();
            respuesta.setDatos(ncoPersonaRepository.findByEmailCorporativo(correo));
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            respuesta.setMensaje(Constantes.EXITO);
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" obtenerId " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica listarCompaniaPaginado(Integer numeroRegistros, Integer pagina) throws ServiceException {
        try{
            Map<String, Object> repuesta = new HashMap<>();
            List<NclCentrocClienteDto> listaCentroCliente = ObjetoMapper.mapAll(
                    nclCentrocClienteRepository.findByCentroCostoClientePaginado(numeroRegistros,pagina),
                    NclCentrocClienteDto.class);

            List<NclCentrocClienteDto> listaCentro = ObjetoMapper.mapAll(
                    nclCentrocClienteRepository.findByCentroCostoCliente(),
                    NclCentrocClienteDto.class);

            RespuestaGenerica respuestaGenerica = obtenerListaLogo(listaCentroCliente);

            repuesta.put("lista",respuestaGenerica.getDatos());
            repuesta.put("totalResgistros",listaCentro.size());
            respuestaGenerica.setDatos(repuesta);

            return respuestaGenerica;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" listarCompaniaPaginado " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica listaDinamicaPaginado(NclCentrocClienteDto centroCostosClienteDto,Integer numeroRegistros, Integer pagina) throws ServiceException{
        try{
            Map<String, Object> repuesta = new HashMap<>();
            RespuestaGoogleStorage respuestaGoogle;
            List<CentroCostosClienteConsulta> listaCentroCliente = centrocClienteRepository.consultaDimanicaPaginado(centroCostosClienteDto,
                    numeroRegistros,pagina);

            List<CentroCostosClienteConsulta> listaCentro = centrocClienteRepository.consultaDimanica(centroCostosClienteDto);

            List<CentroCostosClienteConsulta> lista = new ArrayList<>();
            for (CentroCostosClienteConsulta centroCostosClienteConsulta: listaCentroCliente) {
                if (centroCostosClienteConsulta.getUrlLogo() != null && !centroCostosClienteConsulta.getUrlLogo().isEmpty()){
                    respuestaGoogle = googleStorageService.obtenerArchivo(centroCostosClienteConsulta.getUrlLogo());
                    centroCostosClienteConsulta.setUrl(respuestaGoogle.getUrl());
                }
                lista.add(centroCostosClienteConsulta);
            }
            repuesta.put("lista",lista);
            repuesta.put("totalResgistros",listaCentro.size());

            return new RespuestaGenerica(repuesta,Constantes.RESULTADO_EXITO,Constantes.EXITO);
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" listaDinamicaPaginado " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private RespuestaGenerica validarEstructura(NclCentrocClienteDto centroCostosClienteDto) throws ServiceException{
        try{
            RespuestaGenerica respuesta = new RespuestaGenerica();
            if(centroCostosClienteDto.getRfc() != null){
                if (validarDuplicateRfc(centroCostosClienteDto)){
                        if(Validar.rfc(centroCostosClienteDto.getRfc())){
                            respuesta.setMensaje(Constantes.EXITO);
                            respuesta.setResultado(Constantes.RESULTADO_EXITO);
                        }else{
                            respuesta.setMensaje(Constantes.RFC_NO_VALIDO);
                            respuesta.setResultado(Constantes.RESULTADO_ERROR);
                        }
                }else{
                    respuesta.setMensaje(Constantes.RFC_DUPLICADO);
                    respuesta.setResultado(Constantes.RESULTADO_ERROR);
                }
            }else{
                respuesta.setMensaje(Constantes.EXITO);
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
            }
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" validarEstructura " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private RespuestaGenerica validarEstructuraModificar(NclCentrocClienteDto centroCostosClienteDto) throws ServiceException{
        try{
            RespuestaGenerica respuesta = new RespuestaGenerica(null,Constantes.RESULTADO_EXITO,Constantes.EXITO);
            if (centroCostosClienteDto.getRfc() != null){
                //Se verifica si es multi empresa, en caso de ser multiempresa solo podra modificar RFC a los uqe tenga dados de alta como cleinte
                if (validarDuplicadoRfcModificarMultiEmp(centroCostosClienteDto)) {
                    if (Validar.rfc(centroCostosClienteDto.getRfc())) {
                        respuesta.setMensaje(Constantes.EXITO);
                        respuesta.setResultado(Constantes.RESULTADO_EXITO);
                    } else {
                        respuesta.setMensaje(Constantes.RFC_NO_VALIDO);
                        respuesta.setResultado(Constantes.RESULTADO_ERROR);
                    }
                } else {
                    respuesta = validarEstructura(centroCostosClienteDto);
                }
            }else {
                /*if (validarDuplicadoRfcModificar(centroCostosClienteDto)) {
                    if (Validar.rfc(centroCostosClienteDto.getRfc())) {
                        respuesta.setMensaje(Constantes.EXITO);
                        respuesta.setResultado(Constantes.RESULTADO_EXITO);
                    } else {
                        respuesta.setMensaje(Constantes.RFC_NO_VALIDO);
                        respuesta.setResultado(Constantes.RESULTADO_ERROR);
                    }
                } else {
                    respuesta = validarEstructura(centroCostosClienteDto);
                }*/
                respuesta = validarEstructura(centroCostosClienteDto);
            }
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" validarEstructuraModificar " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private RespuestaGenerica validarEstructuraModificarSinMultiEmp(NclCentrocClienteDto centroCostosClienteDto) throws ServiceException{
        try{
            RespuestaGenerica respuesta = new RespuestaGenerica(null,Constantes.RESULTADO_EXITO,Constantes.EXITO);
            if (validarDuplicadoRfcModificar(centroCostosClienteDto)){
                if(Validar.rfc(centroCostosClienteDto.getRfc())){
                    respuesta.setMensaje(Constantes.EXITO);
                    respuesta.setResultado(Constantes.RESULTADO_EXITO);
                }else{
                    respuesta.setMensaje(Constantes.RFC_NO_VALIDO);
                    respuesta.setResultado(Constantes.RESULTADO_ERROR);
                }
            }else{
                respuesta = validarEstructura(centroCostosClienteDto);
            }
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" validarEstructuraModificar " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private RespuestaGenerica validaCamposObligatoriosEmpresa(NclCentrocClienteDto centroCostosClienteDto) throws ServiceException {
        try{
            RespuestaGenerica respuesta = new RespuestaGenerica();
            if(centroCostosClienteDto.getNombre() == null
                    || centroCostosClienteDto.getNombre().isEmpty()
                    || centroCostosClienteDto.getRegimenfiscalId() == null
                    || centroCostosClienteDto.getRegimenfiscalId().getRegimenfiscalId() == null
                    || centroCostosClienteDto.getRegimenfiscalId().getRegimenfiscalId().isEmpty()
                    || centroCostosClienteDto.getCentroCostosCentrocClienteId() == null
                    || centroCostosClienteDto.getCentroCostosCentrocClienteId().getCentrocClienteId() == null){
                respuesta.setMensaje(Constantes.CAMPOS_REQUERIDOS);
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
            }else {
                if (centroCostosClienteDto.isCerKeyConstrasenia()){
                    if (centroCostosClienteDto.getCer() == null
                            || centroCostosClienteDto.getKey() == null
                            || centroCostosClienteDto.getContrasenia() == null){
                        respuesta.setMensaje(Constantes.CAMPOS_REQUERIDOS);
                        respuesta.setResultado(Constantes.RESULTADO_ERROR);
                    }else{
                        respuesta.setMensaje(Constantes.EXITO);
                        respuesta.setResultado(Constantes.RESULTADO_EXITO);
                    }
                }else {
                    respuesta.setMensaje(Constantes.EXITO);
                    respuesta.setResultado(Constantes.RESULTADO_EXITO);
                }

                if (!centroCostosClienteDto.getRegimenfiscalId().getRegimenfiscalId().equals("612")
                    && !centroCostosClienteDto.getRegimenfiscalId().getRegimenfiscalId().equals("621")){
                    if (centroCostosClienteDto.getRazonSocial() == null
                            || centroCostosClienteDto.getRazonSocial().isEmpty()){
                        respuesta.setMensaje(Constantes.CAMPOS_REQUERIDOS);
                        respuesta.setResultado(Constantes.RESULTADO_ERROR);
                    }
                }
            }
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" validaCamposObligatoriosEmpresa " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private RespuestaGenerica validaCamposObligatorios(NclCentrocClienteDto centroCostosClienteDto) throws ServiceException {
        try{
            RespuestaGenerica respuesta = new RespuestaGenerica();
            if(centroCostosClienteDto.getNombre() == null
                    || centroCostosClienteDto.getNombre().isEmpty()
                    || centroCostosClienteDto.getRazonSocial() == null
                    || centroCostosClienteDto.getRazonSocial().isEmpty()){
                respuesta.setMensaje(Constantes.CAMPOS_REQUERIDOS);
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
            }else {
                if (cambioPrimitivoMultiEmpresa(centroCostosClienteDto.getMultiempresa())){
                    respuesta.setMensaje(Constantes.EXITO);
                    respuesta.setResultado(Constantes.RESULTADO_EXITO);
                }else {
                    if (centroCostosClienteDto.getRfc() == null
                            || centroCostosClienteDto.getRfc().isEmpty()){
                        respuesta.setMensaje(Constantes.CAMPOS_REQUERIDOS);
                        respuesta.setResultado(Constantes.RESULTADO_ERROR);
                    }else {
                        respuesta.setMensaje(Constantes.EXITO);
                        respuesta.setResultado(Constantes.RESULTADO_EXITO);
                    }
                }
            }
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" validaCamposObligatorios " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private boolean validarDuplicateRfc(NclCentrocClienteDto centroCostosClienteDto) throws ServiceException {//Sabemos que es cliente....
        try {
            if (centroCostosClienteDto.getCentroCostosCentrocClienteId() != null
                && centroCostosClienteDto.getCentroCostosCentrocClienteId().getCentrocClienteId() != null){
                return !nclCentrocClienteRepository.
                        existsByRfcAndCentroCostosCentrocClienteIdCentrocClienteId(centroCostosClienteDto.getRfc(),
                                centroCostosClienteDto.getCentroCostosCentrocClienteId().getCentrocClienteId());
            }else {
                return !nclCentrocClienteRepository.existsByRfc(centroCostosClienteDto.getRfc());
            }
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" validarDuplicateRfc " + Constantes.ERROR_EXCEPCION, e);
        }
    }
    //regresa true si esta duplicado, regresa false en otro caso
    private boolean validarDuplicadoRfcModificar(NclCentrocClienteDto centroCostosClienteDto) throws ServiceException {
        try {
            boolean eval=nclCentrocClienteRepository.findByCentrocClienteIdAndRfc(
                    centroCostosClienteDto.getCentrocClienteId(), centroCostosClienteDto.getRfc()).isEmpty();
            if(eval)
                eval=nclCentrocClienteRepository.findByRfc(centroCostosClienteDto.getRfc()).isEmpty();
            else eval=!eval;
            return eval;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" validarDuplicadoRfcModificar " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private boolean validarDuplicadoRfcModificarMultiEmp(NclCentrocClienteDto centroCostosClienteDto) throws ServiceException {
        try {
            boolean eval=nclCentrocClienteRepository.findByCentrocClienteIdAndRfc(
                    centroCostosClienteDto.getCentrocClienteId(), centroCostosClienteDto.getRfc()).isEmpty();
            if(eval) {
                List<NclCentrocCliente> lstCentro=nclCentrocClienteRepository.findByRfc(centroCostosClienteDto.getRfc());
                eval = lstCentro.isEmpty();
                //Si regresa false entonces ya existe el RFC en la BD y debo verificar si pertenece a una empresa que el mismo dio de alta en caso de ser asi regresar true
                if(!eval){
                    for(NclCentrocCliente centro:lstCentro){
                        if(centro.getCentroCostosCentrocClienteId().getCentrocClienteId()==centroCostosClienteDto.getCentrocClienteId())return true;
                    }
                    eval=false;
                }
            }
            else eval=!eval;
            return eval;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" validarDuplicadoRfcModificar " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private String generateUrl(NclCentrocClienteDto centroCostosClienteDto){
        UUID objectId = Utilidades.generateObjectId();
        return Constantes.PROYECTO + "/"
                + centroCostosClienteDto.getNombre() + "/"
                + centroCostosClienteDto.getRazonSocial() + "/"
                + centroCostosClienteDto.getRfc() + "/"
                + objectId;
    }

    private CertificadoSelloDigitalRespuestaDto guardarCsd(NclCentrocClienteDto centrocClienteDto) throws ServiceException {
        try {
            CertificadoSelloDigitalPeticionDto peticion = new CertificadoSelloDigitalPeticionDto();
            peticion.setCsd_cer(Utilidades.encodeContent(centrocClienteDto.getCer()));
            peticion.setCsd_key(Utilidades.encodeContent(centrocClienteDto.getKey()));
            peticion.setCsd_pass(centrocClienteDto.getContrasenia());
            return certificadoSelloDigitalServices.guardarCertificadoSellosDigital(peticion);
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" validarDuplicadoRfcModificar " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private RespuestaGenerica guardarPoliticaEstandar(Integer idCentroCliente) throws ServiceException {
        NclPolitica politica = new NclPolitica();
        politica.setNombre(Constantes.POLITICA_ESTANDAR_NOMBRE);
        politica.setDiasEconomicos(Constantes.POLITICA_ESTANDAR_DIAS_ECONOMICOS);
        politica.setDescuentoPropDia(Constantes.POLITICA_ESTANDAR_DESCUENTO_PROPORCIONAR);
        politica.setDescuentaFaltas(Constantes.POLITICA_ESTANDAR_DESCUENTO_FALTA);
        politica.setDescuentaIncapacidades(Constantes.POLITICA_ESTANDAR_DESCUENTO_INCAPACIDAD);
        politica.setPrimaAniversario(Constantes.POLITICA_ESTANDAR_PRIMA_ANIVERSARIO);
        politica.setCentrocClienteId(new NclCentrocCliente());
        politica.getCentrocClienteId().setCentrocClienteId(idCentroCliente);
        politica.setCalculoAntiguedadId(new CatCalculoAntiguedad());
        politica.getCalculoAntiguedadId().setCalculoAntiguedadxId(Constantes.POLITICA_ESTANDAR_CALCULO_ANTIGUEDAD);
        return politicaService.guardaPoliticaEstandar(politica);
    }

    private void guardarBeneficiosPolitica(NclPolitica idPolitica) throws ServiceException {
        List<BeneficioXpolitica> listaBeneficios = new ArrayList<>();
        int aniosAntiguedad = Constantes.BENEFICIO_ESTANDAR_ANIOS_ANTIGUEDAD;
        int diasVacaciones = Constantes.BENEFICIO_ESTANDAR_DIAS_VACACIONES;
        for (int i = 0; i < 17; i++) {
            BeneficioXpolitica beneficio = new BeneficioXpolitica();
            if (i != 0){
                if (aniosAntiguedad < 4){
                    aniosAntiguedad = aniosAntiguedad + 1;
                }else{
                    aniosAntiguedad = aniosAntiguedad + 5;
                }
                diasVacaciones = diasVacaciones + 2;
            }

            beneficio.setAniosAntiguedad((short) aniosAntiguedad);
            beneficio.setDiasVacaciones((short) diasVacaciones);
            beneficio.setDiasAguinaldo(Constantes.BENEFICIO_ESTANDAR_DIAS_AGUINALDO);
            beneficio.setPrimaVacacional(Constantes.BENEFICIO_ESTANDAR_DIAS_PRIMA_VACACIONAL);
            beneficio.setEsActivo(Constantes.ESTATUS_ACTIVO);
            beneficio.setPoliticaId(new NclPolitica());
            beneficio.getPoliticaId().setPoliticaId(idPolitica.getPoliticaId());
            listaBeneficios.add(beneficio);
        }
        politicaService.guardaBeneficiosEstandar(listaBeneficios);
    }

    private void guardarDeducionesPercepciones(RespuestaGenerica respuesta) throws ServiceException {
        NclCentrocClienteDto empresa = (NclCentrocClienteDto) respuesta.getDatos();
        percepcionDeduccionService.guardaPercepcionesEstandar(empresa.getCentrocClienteId());
        percepcionDeduccionService.guardaDeduccionesEstandar(empresa.getCentrocClienteId());
    }

    private boolean cambioPrimitivoMultiEmpresa(Boolean is){
        return is == null ? false : is;
    }

}
