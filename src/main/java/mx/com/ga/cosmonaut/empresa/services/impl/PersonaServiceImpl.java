package mx.com.ga.cosmonaut.empresa.services.impl;

import mx.com.ga.cosmonaut.common.dto.NcoPersonaDto;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.dto.RespuestaGoogleStorage;
import mx.com.ga.cosmonaut.common.dto.consultas.PersonaConsulta;
import mx.com.ga.cosmonaut.common.dto.persona.DetalleEventosResponse;
import mx.com.ga.cosmonaut.common.entity.catalogo.negocio.CatFacultadPoder;
import mx.com.ga.cosmonaut.common.entity.catalogo.negocio.CatParentesco;
import mx.com.ga.cosmonaut.common.entity.catalogo.negocio.CatTipoPersona;
import mx.com.ga.cosmonaut.common.entity.catalogo.ubicacion.CatNacionalidad;
import mx.com.ga.cosmonaut.common.entity.cliente.NclBeneficioXpolitica;
import mx.com.ga.cosmonaut.common.entity.cliente.NclPolitica;
import mx.com.ga.cosmonaut.common.entity.colaborador.NcoContratoColaborador;
import mx.com.ga.cosmonaut.common.entity.colaborador.NcoPersona;
import mx.com.ga.cosmonaut.common.entity.colaborador.servicios.NscIncidencia;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.common.repository.calculo.NcrEmpleadoXnominaRepository;
import mx.com.ga.cosmonaut.common.repository.catalogo.negocio.CatFacultadPoderRepository;
import mx.com.ga.cosmonaut.common.repository.catalogo.negocio.CatParentescoRepository;
import mx.com.ga.cosmonaut.common.repository.catalogo.ubicacion.CatNacionalidadRepository;
import mx.com.ga.cosmonaut.common.repository.cliente.NclBeneficioXpoliticaRepository;
import mx.com.ga.cosmonaut.common.repository.colaborador.NcoContratoColaboradorRepository;
import mx.com.ga.cosmonaut.common.repository.colaborador.NcoPersonaRepository;
import mx.com.ga.cosmonaut.common.repository.colaborador.servicios.NscIncidenciaRepository;
import mx.com.ga.cosmonaut.common.repository.nativo.PersonaRepository;
import mx.com.ga.cosmonaut.common.service.GoogleStorageService;
import mx.com.ga.cosmonaut.common.util.Constantes;
import mx.com.ga.cosmonaut.common.util.ObjetoMapper;
import mx.com.ga.cosmonaut.common.util.Utilidades;
import mx.com.ga.cosmonaut.common.util.Validar;
import mx.com.ga.cosmonaut.empresa.services.PersonaService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;

@Singleton
public class PersonaServiceImpl implements PersonaService {

    @Inject
    private NcoPersonaRepository ncoPersonaRepository;

    @Inject
    private PersonaRepository personaRepository;

    @Inject
    private GoogleStorageService googleStorageService;

    @Inject
    private CatParentescoRepository catParentescoRepository;

    @Inject
    private CatNacionalidadRepository catNacionalidadRepository;

    @Inject
    private CatFacultadPoderRepository catFacultadPoderRepository;

    @Inject
    private NcoContratoColaboradorRepository ncoContratoColaboradorRepository;

    @Inject
    private NclBeneficioXpoliticaRepository nclBeneficioXpoliticaRepository;

    @Inject
    private NscIncidenciaRepository nscIncidenciaRepository;

    @Inject
    private NcrEmpleadoXnominaRepository ncrEmpleadoXnominaRepository;

    @Override
    public RespuestaGenerica guardarRepresentanteLegal(NcoPersonaDto personaDto) throws ServiceException {
        try{
            RespuestaGenerica respuesta = validaCamposObligatoriosRepresentanteLegal(personaDto);
            if (respuesta.isResultado()){
                respuesta = validaEstructuraCurp(personaDto);
                if (respuesta.isResultado()){
                    if(personaDto.getImagen()!=null){
                        String ruta = generateUrl(personaDto);
                        googleStorageService.subirArchivo(personaDto.getImagen(),ruta);
                        personaDto.setUrlFirma(ruta);
                    }
                    respuesta =  guardar(personaDto, Constantes.ID_REPRESENTANTE_LEGAL.intValue());
                }
            }
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" guardarRepresentanteLegal " + Constantes.ERROR_EXCEPCION, e);
        }
    }


    @Override
    public RespuestaGenerica guardarContactoRH(NcoPersonaDto personaDto) throws ServiceException {
        try{
            RespuestaGenerica respuesta = validaCamposObligatoriosContactoRh(personaDto);
            if (respuesta.isResultado()){
                respuesta = validaEstructuraCurp(personaDto);
                if (respuesta.isResultado()){
                    respuesta =  guardar(personaDto, Constantes.ID_CONTACTO_RECURSOS_HUMANOS.intValue());
                }
            }
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" guardarContactoRH " + Constantes.ERROR_EXCEPCION, e);
        }
    }


    @Override
    public RespuestaGenerica guardarContactoInicial(NcoPersonaDto personaDto) throws ServiceException {
        try{
            return guardar(personaDto, Constantes.ID_CONTACTO_INICIAL.intValue());
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" guardarContactoInicial " + Constantes.ERROR_EXCEPCION, e);
        }
    }


    @Override
    public RespuestaGenerica guardarApoderadoLegal(NcoPersonaDto personaDto) throws ServiceException {
        try{
            RespuestaGenerica respuesta = validaCamposObligatoriosApoderadoLegal(personaDto);
            if (respuesta.isResultado()){
                respuesta = validaEstructuraCurp(personaDto);
                if (respuesta.isResultado()){
                    respuesta =  guardar(personaDto, Constantes.ID_APODERADO_LEGAL.intValue());
                }
            }
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" guardarApoderadoLegal " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private RespuestaGenerica guardar(NcoPersonaDto personaDto, Integer idTipoPersona) throws ServiceException {
        try{
            RespuestaGenerica respuesta = validaCamposObligatorios(personaDto);
            personaDto.setTipoPersonaId(new CatTipoPersona());
            personaDto.getTipoPersonaId().setTipoPersonaId(idTipoPersona);
            if (respuesta.isResultado()){
                respuesta =  validaEstructuraCampos(personaDto);
                if (respuesta.isResultado()){
                    personaDto.setEsActivo(Constantes.ESTATUS_ACTIVO);
                    respuesta =  new RespuestaGenerica();
                    respuesta.setDatos(ObjetoMapper.map(ncoPersonaRepository.save(ObjetoMapper.map(personaDto,
                            NcoPersona.class)), NcoPersonaDto.class));
                    respuesta.setResultado(Constantes.RESULTADO_EXITO);
                    respuesta.setMensaje(Constantes.EXITO);
                }
            }
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" guardar " + Constantes.ERROR_EXCEPCION, e);
        }
    }


    @Override
    public RespuestaGenerica modificarRepresentanteLegal(NcoPersonaDto personaDto) throws ServiceException {
        try{
            String ruta;
            RespuestaGenerica respuesta = validaCamposObligatoriosRepresentanteLegal(personaDto);
            if (respuesta.isResultado()){
                respuesta = validaEstructuraCurp(personaDto);
                if (respuesta.isResultado()){
                    if(personaDto.getImagen()!=null){
                        ruta = generateUrl(personaDto);
                        personaDto.setUrlFirma(ruta);
                        googleStorageService.subirArchivo(personaDto.getImagen(),ruta);
                    }
                    respuesta =  modificar(personaDto, Constantes.ID_REPRESENTANTE_LEGAL.intValue());
                }
            }
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" modificarRepresentanteLegal " + Constantes.ERROR_EXCEPCION, e);
        }
    }


    @Override
    public RespuestaGenerica modificarContactoRH(NcoPersonaDto personaDto) throws ServiceException {
        try{
            RespuestaGenerica respuesta = validaCamposObligatoriosContactoRh(personaDto);
            if (respuesta.isResultado()){
                respuesta = validaEstructuraCurp(personaDto);
                if (respuesta.isResultado()){
                    respuesta =  modificar(personaDto, Constantes.ID_CONTACTO_RECURSOS_HUMANOS.intValue());
                }
            }
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" modificarContactoRH " + Constantes.ERROR_EXCEPCION, e);
        }
    }


    @Override
    public RespuestaGenerica modificarApoderadoLegal(NcoPersonaDto personaDto) throws ServiceException {
        try{
            RespuestaGenerica respuesta = validaCamposObligatoriosApoderadoLegal(personaDto);
            if (respuesta.isResultado()){
                respuesta = validaEstructuraCurp(personaDto);
                if (respuesta.isResultado()){
                    respuesta =  modificar(personaDto, Constantes.ID_APODERADO_LEGAL.intValue());
                }
            }
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" modificarApoderadoLegal " + Constantes.ERROR_EXCEPCION, e);
        }
    }


    @Override
    public RespuestaGenerica modificarContactoInicial(NcoPersonaDto personaDto) throws ServiceException {
        try{
            return modificar(personaDto, Constantes.ID_CONTACTO_INICIAL.intValue());
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" modificarContactoInicial " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private RespuestaGenerica modificar(NcoPersonaDto ncoPersonaDto, Integer idTipoPersona) throws ServiceException {
        try{
            if (ncoPersonaDto.getPersonaId() != null){
                RespuestaGenerica respuesta = validaCamposObligatorios(ncoPersonaDto);
                if (respuesta.isResultado()){
                    respuesta =  validaEstructuraCamposModificar(ncoPersonaDto);
                    if (respuesta.isResultado()){
                        ncoPersonaDto.setTipoPersonaId(new CatTipoPersona());
                        ncoPersonaDto.getTipoPersonaId().setTipoPersonaId(idTipoPersona);
                        respuesta.setDatos(ObjetoMapper.map(
                                ncoPersonaRepository.update(
                                        ObjetoMapper.map(ncoPersonaDto,
                                                NcoPersona.class)), NcoPersonaDto.class));
                        respuesta.setResultado(Constantes.RESULTADO_EXITO);
                        respuesta.setMensaje(Constantes.EXITO);
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
    public RespuestaGenerica modificarLista(List<NcoPersona> listaNcoPersona) throws ServiceException{
        try{
            RespuestaGenerica respuesta = new RespuestaGenerica();
            for (NcoPersona persona: listaNcoPersona) {
                ncoPersonaRepository.update(persona.getPersonaId(),persona.isEsActivo());
            }
            respuesta.setResultado(true);
            respuesta.setMensaje(Constantes.EXITO);
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" modificarLista " + Constantes.ERROR_EXCEPCION, e);
        }
    }


    @Override
    public RespuestaGenerica eliminar(Long idPersona) throws ServiceException{
        try{
            ncoPersonaRepository.update(idPersona.intValue(),Constantes.ESTATUS_INACTIVO);
            return new RespuestaGenerica(null,Constantes.RESULTADO_EXITO,Constantes.EXITO);
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" eliminar " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica obtenerId(Long idPersona) throws ServiceException {
        try{
            RespuestaGenerica respuesta = new RespuestaGenerica();
            RespuestaGoogleStorage respuestaGoogle;
            NcoPersonaDto persona = ObjetoMapper.map(
                    ncoPersonaRepository.findByPersonaId(idPersona.intValue()), NcoPersonaDto.class);
            if (persona.getParentescoId() != null && persona.getParentescoId().getParentescoId() != null){
                persona.setParentescoId(catParentescoRepository.findById(persona.getParentescoId().getParentescoId()).orElse(new CatParentesco()));
            }
            if (persona.getNacionalidadId() != null && persona.getNacionalidadId().getNacionalidadId() != null){
                persona.setNacionalidadId(catNacionalidadRepository.findById(persona.getNacionalidadId().getNacionalidadId()).orElse(new CatNacionalidad()));
            }
            if (persona.getUrlImagen() != null && !persona.getUrlImagen().isEmpty()){
                respuestaGoogle = googleStorageService.obtenerArchivo(persona.getUrlImagen());
                persona.setUrl(respuestaGoogle.getUrl());
                persona.setImagen(respuestaGoogle.getArreglo());
            }
            respuesta.setDatos(persona);
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            respuesta.setMensaje(Constantes.EXITO);
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" obtenerId " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica obtenerIdCompania(Long idCompania) throws ServiceException {
        try{
            RespuestaGenerica respuesta =  new RespuestaGenerica();
            respuesta.setDatos(ObjetoMapper.mapAll(
                    ncoPersonaRepository.findByCentrocClienteIdCentrocClienteIdOrderByPersonaIdDesc(
                            idCompania.intValue()), NcoPersonaDto.class));
            respuesta.setResultado(true);
            respuesta.setMensaje(Constantes.EXITO);
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" obtenerIdCompania " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica obtenerEmailCorporativoCentrocClienteId(String correo, Long centroclienteId)
            throws ServiceException {
        try{
            Optional<NcoPersona> persona = ncoPersonaRepository.
                    findByEmailCorporativoAndCentrocClienteIdCentrocClienteId(correo,centroclienteId.intValue());
            if (persona.isPresent()){
                return new RespuestaGenerica(persona, Constantes.RESULTADO_EXITO, Constantes.EXITO);
            }else {
                return new RespuestaGenerica(null, Constantes.RESULTADO_ERROR, Constantes.ERROR_PERSONA_CORREO);
            }
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" obtenerEmailCorporativoCentrocClienteId " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica obtenerDetalleEventos(Long id) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            Optional<NcoContratoColaborador> ncoContratoColaborador = ncoContratoColaboradorRepository
                    .findFirstByPersonaIdPersonaIdOrderByFechaContratoDesc(id);

            if (ncoContratoColaborador.isPresent() && ncoContratoColaborador.get().getPoliticaId() != null) {
                NclPolitica nclPolitica = ncoContratoColaborador.get().getPoliticaId();

                DetalleEventosResponse data = new DetalleEventosResponse();
                data.setNombrePolitica(nclPolitica.getNombre());
                data.setDescuentoSeptimoDia(nclPolitica.isDescuentoPropDia());
                data.setDescuentoIncapacidad(nclPolitica.isDescuentaIncapacidades());
                data.setDescuentoFalta(nclPolitica.isDescuentaFaltas());
                data.setPrimaVacacionalAniversario(nclPolitica.isPrimaAniversario());
                data.setDiasEconomicos(nclPolitica.getDiasEconomicos());
                data.setEconomicosDisponibles(nclPolitica.getDiasEconomicos());
                data.setVacacionesDisponibles(ncoContratoColaborador.get()
                        .getDiasVacaciones()!=null?ncoContratoColaborador.get().getDiasVacaciones():0);

                int antiguedad = obtenerAntiguedad(nclPolitica, ncoContratoColaborador.get());
                Optional<NclBeneficioXpolitica> nclBeneficioXpolitica = nclBeneficioXpoliticaRepository
                        .findFirstByNclPoliticaPoliticaIdAndAniosAntiguedadGreaterThanEqualsAndEsActivoOrderByAniosAntiguedad(
                                nclPolitica.getPoliticaId(), antiguedad, Constantes.ESTATUS_ACTIVO);

                if (nclBeneficioXpolitica.isPresent()) {
                    data.setDiasVacaciones(nclBeneficioXpolitica.get().getDiasVacaciones());
                    data.setPrimaVacacional(nclBeneficioXpolitica.get().getPrimaVacacional());
                    data.setDiasAguinaldo(nclBeneficioXpolitica.get().getDiasAguinaldo());
                    data.setVacacionesDisponibles(data.getVacacionesDisponibles()+data.getDiasVacaciones());
                }
                // Se calculan los dias de aguinaldo si aun no cumple el año
                if (antiguedad < 1) {
                    Optional<NclBeneficioXpolitica> nclBeneficiosPrimerAnio = nclBeneficioXpoliticaRepository
                            .findFirstByNclPoliticaPoliticaIdAndAniosAntiguedadLessThanEqualsAndEsActivoOrderByAniosAntiguedadDesc(
                                    nclPolitica.getPoliticaId(),1, Constantes.ESTATUS_ACTIVO);
                    if (nclBeneficiosPrimerAnio.isPresent()) {
                        data.setDiasAguinaldo(nclBeneficiosPrimerAnio.get().getDiasAguinaldo());
                        data.setPrimaVacacional(nclBeneficiosPrimerAnio.get().getPrimaVacacional());
                    }
                }
                setearIncidencias(id, data);
                data.setEconomicosDisponibles(data.getEconomicosDisponibles()-data.getEconomicosUsados());
                data.setVacacionesDisponibles(data.getVacacionesDisponibles()-data.getVacacionesUsadas());

                respuesta.setDatos(data);
                respuesta.setResultado(true);
                respuesta.setMensaje(Constantes.EXITO);
            } else {
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(Constantes.ERROR_CLIENTE_NO_EXISTE);
            }
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" obtenerDetalleEventos " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private int obtenerAntiguedad(NclPolitica nclPolitica, NcoContratoColaborador ncoContratoColaborador ) {
        LocalDate startDate;
        if (nclPolitica.getCalculoAntiguedadId().getNombreCorto().equals(Constantes.ANTIGUEDAD_XANTIGUEDAD)) {
            startDate = LocalDate.parse(ncoContratoColaborador.getFechaAntiguedad().toString());
        } else {
            startDate = LocalDate.parse(ncoContratoColaborador.getFechaContrato().toString());
        }
        int anios= Period.between(startDate, LocalDate.now()).getYears();
        if (anios == 0)
            return Period.between(startDate, startDate.plusYears(anios)).getYears();
        else
            return Period.between(startDate, startDate.plusYears(anios+1)).getYears();
    }

    private void setearIncidencias(Long personaId, DetalleEventosResponse data) {
        List<NscIncidencia> incidencias = nscIncidenciaRepository.findByPersonaIdDetalleEvento(personaId,
                Arrays.asList(Constantes.VACACIONES_ID, Constantes.DIASECONOMICOS_ID, Constantes.INCAPACIDAD_ID),true);

        for (NscIncidencia incidencia : incidencias) {
            switch (incidencia.getTipoIncidenciaId().getTipoIncidenciaId()) {
                case Constantes.VACACIONES_ID:
                    data.setVacacionesUsadas(data.getVacacionesUsadas()
                            +(incidencia.getDuracion()!=null?incidencia.getDuracion():0));
                    break;
                case Constantes.DIASECONOMICOS_ID:
                    data.setEconomicosUsados(data.getEconomicosUsados()
                            +(incidencia.getDuracion()!=null?incidencia.getDuracion():0));
                    break;
                case Constantes.INCAPACIDAD_ID:
                    data.setIncapacidadesUsadas(data.getIncapacidadesUsadas()
                            +(incidencia.getDuracion()!=null?incidencia.getDuracion():0));
                    break;
            }
        }
    }

    @Override
    public RespuestaGenerica listarTodos(Integer tipoPersona) throws ServiceException {
        try{
            RespuestaGenerica respuesta =  new RespuestaGenerica();
            List<NcoPersonaDto> listaPersona = ObjetoMapper.mapAll(
                    ncoPersonaRepository.findByTipoPersonaIdTipoPersonaIdOrderByPersonaIdDesc(tipoPersona), NcoPersonaDto.class);
            if(tipoPersona ==  Constantes.ID_REPRESENTANTE_LEGAL.intValue()){
                respuesta.setDatos(obtenerListaPersonaFirma(listaPersona));
            }else{
                respuesta.setDatos(listaPersona);
            }
            respuesta.setResultado(true);
            respuesta.setMensaje(Constantes.EXITO);
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" listarTodos " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica listarCompaniaPersona(NcoPersonaDto ncoPersonaDto) throws ServiceException {
        try{
            RespuestaGenerica respuesta = new RespuestaGenerica();
            respuesta.setDatos(ObjetoMapper.mapAll(
                    ncoPersonaRepository.findByCentrocClienteIdCentrocClienteIdAndTipoPersonaIdTipoPersonaIdOrderByPersonaIdDesc(
                            ncoPersonaDto.getCentrocClienteId().getCentrocClienteId(),
                            ncoPersonaDto.getTipoPersonaId().getTipoPersonaId()), NcoPersonaDto.class));
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            respuesta.setMensaje(Constantes.EXITO);
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" listarCompaniaPersona " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica listarDinamica(NcoPersonaDto ncoPersonaDto) throws ServiceException {
        try{
            RespuestaGenerica respuesta =  new RespuestaGenerica();
            if(ncoPersonaDto.getTipoPersonaId().getTipoPersonaId() != null){
                List<PersonaConsulta> listaPersonaConsulta = personaRepository.consultaDimanicaPersona(ncoPersonaDto);
                List<PersonaConsulta> listaPersona = new ArrayList<>();
                for (PersonaConsulta persona: listaPersonaConsulta) {
                    if (persona.getNacionalidad() != null){
                        persona.setNacionalidadId(catNacionalidadRepository.findById(persona.getNacionalidad()).orElse(new CatNacionalidad()));
                    }
                    if (persona.getFacultadPoder() != null){
                        persona.setFacultadPoderId(catFacultadPoderRepository.findById(persona.getFacultadPoder().intValue()).orElse(new CatFacultadPoder()));
                    }
                    listaPersona.add(persona);
                }
                respuesta.setDatos(listaPersona);
                respuesta.setResultado(true);
                respuesta.setMensaje(Constantes.EXITO);
            }else{
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje("El tipoPersonaId es requerido para consulta dinámica.");
            }
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" listarDinamica " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica listaEmpleadoIncompleto(Long idEmpresa) throws ServiceException {
        try{
            RespuestaGenerica respuesta =  new RespuestaGenerica();
            respuesta.setDatos(ObjetoMapper.mapAll(ncoPersonaRepository.findByColaboradorNotExists(
                    idEmpresa.intValue()), NcoPersonaDto.class));
            respuesta.setResultado(true);
            respuesta.setMensaje(Constantes.EXITO);
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" listaEmpleadoIncompleto " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica validarFechaFinPago(Integer personaId) throws ServiceException {
        try{
            Map<String, Boolean> result = new HashMap<>();
            result.put("mostrarFechaFinUltimoPago",
                    ncrEmpleadoXnominaRepository.countByPersonaIdPersonaId(personaId) == 0);
            return new RespuestaGenerica(result, Constantes.RESULTADO_EXITO, Constantes.EXITO);
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" validarFechaFinPago " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private List<NcoPersonaDto> obtenerListaPersonaFirma(List<NcoPersonaDto> listaPersona) throws ServiceException {
        try{
            RespuestaGoogleStorage respuestaGoogle;
            List<NcoPersonaDto> listaRepresentante = new ArrayList<>();
            for (NcoPersonaDto personaDto : listaPersona){
                if(personaDto.getUrlFirma() != null && !personaDto.getUrlFirma().isEmpty()){
                    respuestaGoogle = googleStorageService.obtenerArchivo(personaDto.getUrlFirma());
                    personaDto.setImagen(respuestaGoogle.getArreglo());
                }
                listaRepresentante.add(personaDto);
            }
            return listaRepresentante;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" obtenerListaPersonaFirma " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private RespuestaGenerica validaCamposObligatorios(NcoPersonaDto ncoPersonaDto) throws ServiceException {
        try{
            RespuestaGenerica respuesta =  new RespuestaGenerica();
            if(ncoPersonaDto.getNombre() == null
                    || ncoPersonaDto.getNombre().isEmpty()
                    || ncoPersonaDto.getApellidoPaterno() == null
                    || ncoPersonaDto.getApellidoPaterno().isEmpty()
                    || ncoPersonaDto.getEmailCorporativo() == null
                    || ncoPersonaDto.getEmailCorporativo().isEmpty()
                    || ncoPersonaDto.getCentrocClienteId() == null
                    || ncoPersonaDto.getCentrocClienteId().getCentrocClienteId() == null){
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(Constantes.CAMPOS_REQUERIDOS);
            }else{
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
                respuesta.setMensaje(Constantes.EXITO);
            }
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" validaCamposObligatorios " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private RespuestaGenerica validaCamposObligatoriosContactoRh(NcoPersonaDto ncoPersonaDto) throws ServiceException {
        try{
            RespuestaGenerica respuesta = new RespuestaGenerica();
            if(ncoPersonaDto.getCurp() == null
                    || ncoPersonaDto.getCurp().isEmpty()){
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(Constantes.CAMPOS_REQUERIDOS);
            }else {
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
                respuesta.setMensaje(Constantes.EXITO);
            }
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" validaCamposObligatoriosContactoRh " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private RespuestaGenerica validaCamposObligatoriosRepresentanteLegal(NcoPersonaDto ncoPersonaDto) throws ServiceException {
        try{
            RespuestaGenerica respuesta = new RespuestaGenerica();
            if(ncoPersonaDto.getCurp() == null
                    || ncoPersonaDto.getCurp().isEmpty()
                    || ncoPersonaDto.getRfc() == null
                    || ncoPersonaDto.getRfc().isEmpty()
                    || ncoPersonaDto.getNacionalidadId().getNacionalidadId()== null
                    || ncoPersonaDto.getTipoRepresentanteId() == null
                    || ncoPersonaDto.getTipoRepresentanteId().getTipoRepresentanteId() == null){
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(Constantes.CAMPOS_REQUERIDOS);
            }else {
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
                respuesta.setMensaje(Constantes.EXITO);
            }
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" validaCamposObligatoriosRepresentanteLegal " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private RespuestaGenerica validaCamposObligatoriosApoderadoLegal(NcoPersonaDto ncoPersonaDto) throws ServiceException {
        try{
            RespuestaGenerica respuesta = new RespuestaGenerica();
            if(ncoPersonaDto.getCurp() == null
                    || ncoPersonaDto.getCurp().isEmpty()
                    || ncoPersonaDto.getRfc() == null
                    || ncoPersonaDto.getRfc().isEmpty()
                    || ncoPersonaDto.getNacionalidadId() == null
                    || ncoPersonaDto.getNacionalidadId().getNacionalidadId() == null
                    || ncoPersonaDto.getPoderNotarial() == null
                    || ncoPersonaDto.getPoderNotarial().isEmpty()
                    || ncoPersonaDto.getFacultadPoderId() == null
                    || ncoPersonaDto.getFacultadPoderId().getFacultadPoderId() == null){
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(Constantes.CAMPOS_REQUERIDOS);
            }else {
                if (ncoPersonaDto.getPoderNotarial().length() > 30){
                    respuesta.setResultado(Constantes.RESULTADO_ERROR);
                    respuesta.setMensaje(Constantes.ERROR_PODER_NOTARIAL);
                }else{
                    respuesta.setResultado(Constantes.RESULTADO_EXITO);
                    respuesta.setMensaje(Constantes.EXITO);
                }
            }
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" validaCamposObligatoriosApoderadoLegal " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private RespuestaGenerica validaEstructuraCampos(NcoPersonaDto personaDto) throws ServiceException {
        try{
            RespuestaGenerica respuesta = new RespuestaGenerica(null,Constantes.RESULTADO_EXITO, Constantes.EXITO);
            if (personaDto.getTipoPersonaId().getTipoPersonaId() != 1 && personaDto.getTipoPersonaId().getTipoPersonaId() != 6){
                respuesta = validarCorreo(personaDto);
            }
            if (respuesta.isResultado()){
                respuesta = validaEstructura(personaDto);
            }
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" validaEstructuraCampos " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private RespuestaGenerica validaEstructuraCurp(NcoPersonaDto personaDto) throws ServiceException {
        try{
            RespuestaGenerica respuesta = new RespuestaGenerica();
                if(Validar.curp(personaDto.getCurp())){
                    respuesta.setMensaje(Constantes.EXITO);
                    respuesta.setResultado(Constantes.RESULTADO_EXITO);
                }else {
                    respuesta.setMensaje(Constantes.CURP_NO_VALIDO);
                    respuesta.setResultado(Constantes.RESULTADO_ERROR);
                }
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" validaEstructuraCurp " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private RespuestaGenerica validarCorreo(NcoPersonaDto ncoPersonaDto) throws ServiceException {
        try {
            RespuestaGenerica respuesta =  new RespuestaGenerica();
            if (Validar.correo(ncoPersonaDto.getEmailCorporativo())){
                if (!ncoPersonaRepository.existsByEmailCorporativoAndTipoPersonaIdTipoPersonaId(
                                ncoPersonaDto.getEmailCorporativo(),
                                ncoPersonaDto.getTipoPersonaId().getTipoPersonaId())
                        && !ncoPersonaRepository.existsByContactoInicialEmailPersonalAndTipoPersonaIdTipoPersonaId(
                                ncoPersonaDto.getEmailCorporativo(),
                                ncoPersonaDto.getTipoPersonaId().getTipoPersonaId())){
                    respuesta.setMensaje(Constantes.EXITO);
                    respuesta.setResultado(Constantes.RESULTADO_EXITO);
                }else {
                    respuesta.setMensaje(Constantes.CORREO_DUPLICADO);
                    respuesta.setResultado(Constantes.RESULTADO_ERROR);
                }
            }else{
                respuesta.setMensaje(Constantes.CORREO_NO_VALIDO);
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
            }
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" validarCorreo " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private RespuestaGenerica validaEstructura(NcoPersonaDto ncoPersonaDto) throws ServiceException {
        try{
            RespuestaGenerica respuesta =  new RespuestaGenerica(null, Constantes.ESTATUS_ACTIVO, Constantes.EXITO);
            if (Validar.telefono(ncoPersonaDto.getContactoInicialTelefono())){
                respuesta.setMensaje(Constantes.EXITO);
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
            }else{
                respuesta.setMensaje(Constantes.TELEFONO_NO_VALIDO);
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
            }
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" validaEstructura " + Constantes.ERROR_EXCEPCION, e);
        }

    }

    private RespuestaGenerica validaEstructuraCamposModificar(NcoPersonaDto personaDto) throws ServiceException {
        try{
            RespuestaGenerica respuesta;
            respuesta = validarCorreoEmpresa(personaDto);
            if (respuesta.isResultado()){
                respuesta = validaEstructura(personaDto);
            }
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" validaEstructuraCamposModificar " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private RespuestaGenerica validarCorreoEmpresa(NcoPersonaDto ncoPersonaDto) throws ServiceException {
        try {
            RespuestaGenerica respuesta =  new RespuestaGenerica();
            if (Validar.correo(ncoPersonaDto.getEmailCorporativo())){
                if (!ncoPersonaRepository.findByPersonaIdAndEmailCorporativo(
                        ncoPersonaDto.getPersonaId(), ncoPersonaDto.getEmailCorporativo()).isEmpty()){
                    respuesta.setMensaje(Constantes.EXITO);
                    respuesta.setResultado(Constantes.RESULTADO_EXITO);
                }else {
                    if (!ncoPersonaRepository.existsByEmailCorporativoAndTipoPersonaIdTipoPersonaId(
                            ncoPersonaDto.getEmailCorporativo(),
                            ncoPersonaDto.getTipoPersonaId().getTipoPersonaId()) ){
                        respuesta.setMensaje(Constantes.EXITO);
                        respuesta.setResultado(Constantes.RESULTADO_EXITO);
                    }else {
                        respuesta.setMensaje(Constantes.CORREO_DUPLICADO);
                        respuesta.setResultado(Constantes.RESULTADO_ERROR);
                    }
                }
            }else{
                respuesta.setMensaje(Constantes.CORREO_NO_VALIDO);
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
            }
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" validarCorreoEmpresa " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private String generateUrl(NcoPersonaDto ncoPersonaDto){
        UUID objectId = Utilidades.generateObjectId();
        return Constantes.PROYECTO + "/"
                + Constantes.PROYECTO_URL_FIRMA + "/"
                + ncoPersonaDto.getCurp() + "/"
                + objectId;
    }

}
