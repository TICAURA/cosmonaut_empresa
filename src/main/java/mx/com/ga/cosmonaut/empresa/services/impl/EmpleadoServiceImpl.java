package mx.com.ga.cosmonaut.empresa.services.impl;

import mx.com.ga.cosmonaut.common.dto.CargaMasivaDto;
import mx.com.ga.cosmonaut.common.dto.NcoPersonaDto;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.entity.catalogo.negocio.CatTipoPersona;
import mx.com.ga.cosmonaut.common.entity.colaborador.NcoPersona;
import mx.com.ga.cosmonaut.common.entity.temporal.CargaMasivaEmpleado;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.common.interceptor.BitacoraUsuario;
import mx.com.ga.cosmonaut.common.repository.colaborador.NcoPersonaRepository;
import mx.com.ga.cosmonaut.common.repository.temporal.CargaMasivaRepository;
import mx.com.ga.cosmonaut.common.service.GoogleStorageService;
import mx.com.ga.cosmonaut.common.util.Constantes;
import mx.com.ga.cosmonaut.common.util.ObjetoMapper;
import mx.com.ga.cosmonaut.common.util.Utilidades;
import mx.com.ga.cosmonaut.common.util.Validar;
import mx.com.ga.cosmonaut.empresa.services.CargaMasivaEmpleadoServices;
import mx.com.ga.cosmonaut.empresa.services.EmpleadoService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Singleton
public class EmpleadoServiceImpl implements EmpleadoService {

    @Inject
    private NcoPersonaRepository ncoPersonaRepository;

    @Inject
    private CargaMasivaRepository cargaMasivaRepository;

    @Inject
    private CargaMasivaEmpleadoServices cargaMasivaEmpleadoServices;

    @Inject
    private GoogleStorageService googleStorageService;

    @Override
    public RespuestaGenerica guardar(NcoPersonaDto personaDto) throws ServiceException {
        try{
            personaDto.setEsActivo(Constantes.ESTATUS_ACTIVO);
            personaDto.setTipoPersonaId(new CatTipoPersona());
            personaDto.getTipoPersonaId().setTipoPersonaId(Constantes.ID_EMPLEADO.intValue());
            RespuestaGenerica respuesta = validar(personaDto);
            if (respuesta.isResultado()){
                if(personaDto.getImagen() != null){
                    String ruta = generateUrl(personaDto);
                    googleStorageService.subirArchivo(personaDto.getImagen(),ruta);
                    personaDto.setUrlImagen(ruta);
                }
                respuesta.setDatos(ObjetoMapper.map(ncoPersonaRepository.save(ObjetoMapper.map(personaDto,
                        NcoPersona.class)), NcoPersonaDto.class));
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
                respuesta.setMensaje(Constantes.EXITO);
            }
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" guardar " + Constantes.ERROR_EXCEPCION, e);
        }
    }


    @Override
    public RespuestaGenerica modificar(NcoPersonaDto personaDto) throws ServiceException {
        try{
            RespuestaGenerica respuesta =  new RespuestaGenerica();
            personaDto.setTipoPersonaId(new CatTipoPersona());
            personaDto.getTipoPersonaId().setTipoPersonaId(Constantes.ID_EMPLEADO.intValue());
            if (personaDto.getPersonaId() != null ){
                respuesta = validarModificar(personaDto);
                if (respuesta.isResultado()){
                    if(personaDto.getImagen()!=null){
                        String ruta = generateUrl(personaDto);
                        googleStorageService.actualizarArchivo(personaDto.getImagen(),ruta);
                        personaDto.setUrlImagen(ruta);
                    }
                    respuesta.setDatos(ncoPersonaRepository.update(ObjetoMapper.map(personaDto,
                            NcoPersona.class) ));
                    respuesta.setResultado(Constantes.RESULTADO_EXITO);
                    respuesta.setMensaje(Constantes.EXITO);
                }
            }else{
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
    public RespuestaGenerica listaCompaniaPersonaEstatus(NcoPersonaDto ncoPersonaDto) throws ServiceException {
        try{
            RespuestaGenerica respuesta = new RespuestaGenerica();
            respuesta.setDatos(ObjetoMapper.mapAll(
                    ncoPersonaRepository.findByCentrocClienteIdCentrocClienteIdAndTipoPersonaIdTipoPersonaIdAndEsActivoOrderByPersonaIdDesc(
                            ncoPersonaDto.getCentrocClienteId().getCentrocClienteId(),
                            ncoPersonaDto.getTipoPersonaId().getTipoPersonaId(),
                            ncoPersonaDto.getEsActivo()), NcoPersonaDto.class));
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            respuesta.setMensaje(Constantes.EXITO);
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" listaCompaniaPersonaEstatus " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica cargaMasivaEmpleados(CargaMasivaDto cargaMasivaDto) throws ServiceException {
        try{
            if (cargaMasivaDto.getTipoCargaId() != null){
                cargaMasivaRepository.deleteByCentrocClienteId(cargaMasivaDto.getCentrocClienteId());
                List<CargaMasivaEmpleado> lista = cargaMasivaEmpleadoServices.carga(cargaMasivaDto.getArchivo(),
                        cargaMasivaDto.getCentrocClienteId(), cargaMasivaDto.getTipoCargaId());
                //lista.forEach(cargaMasivaEmpleado -> cargaMasivaRepository.save(cargaMasivaEmpleado));
                cargaMasivaRepository.saveAll(lista);
                cargaMasivaEmpleadoServices.guardarCargaMasiva(cargaMasivaDto.getCentrocClienteId(), cargaMasivaDto.getTipoCargaId());
                return generaRespuestaCargaMasiva(cargaMasivaDto.getCentrocClienteId());
            }else{
                return new RespuestaGenerica(null,Constantes.RESULTADO_ERROR,"No selecciono el tipo de carga");
            }
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" cargaMasivaEmpleados " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica cargaMasivaXEmpleado(CargaMasivaDto cargaMasivaDto) throws ServiceException {
        try{
            if (cargaMasivaDto.getTipoCargaId() != null){
                cargaMasivaRepository.deleteByCentrocClienteId(cargaMasivaDto.getCentrocClienteId());
                InputStream fichero = new ByteArrayInputStream(cargaMasivaDto.getArchivo());
                XSSFWorkbook libro = new XSSFWorkbook(fichero);
                XSSFSheet hoja = libro.getSheetAt(0);
                hoja.forEach(fila -> {
                    if (fila.getRowNum() != 0 && fila.getRowNum() != 1){
                        CargaMasivaEmpleado empleado =
                                cargaMasivaEmpleadoServices.
                                        obtenEmpleadoCargaMasiva(fila,
                                                cargaMasivaDto.getCentrocClienteId(),
                                                cargaMasivaDto.getTipoCargaId());

                        CargaMasivaEmpleado empleadoValidado =
                                cargaMasivaEmpleadoServices.
                                        validaCargaMasiva(empleado,cargaMasivaDto.getTipoCargaId());

                        CargaMasivaEmpleado cargaMasivaEmpleado = cargaMasivaRepository.save(empleadoValidado);
                        if (empleadoValidado.isEsCorrecto()){
                            try {
                                cargaMasivaEmpleadoServices.
                                        guardarCargaMasivaXEmpleado(cargaMasivaEmpleado,
                                                cargaMasivaDto.getCentrocClienteId(),
                                                cargaMasivaDto.getTipoCargaId());
                            } catch (ServiceException e) {
                                try {
                                    throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                                            + Constantes.ERROR_METODO +" cargaMasivaEmpleados " + Constantes.ERROR_EXCEPCION, e);
                                } catch (ServiceException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }
                    }
                });
                return generaRespuestaCargaMasiva(cargaMasivaDto.getCentrocClienteId());
            }else{
                return new RespuestaGenerica(null,Constantes.RESULTADO_ERROR,"No selecciono el tipo de carga");
            }
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" cargaMasivaEmpleados " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica listaCargaMasivaEmpleados(Integer centroClienteId) throws ServiceException {
        try{
            return new RespuestaGenerica(
                    cargaMasivaRepository.findByCentrocClienteId(centroClienteId),
                    Constantes.RESULTADO_EXITO, Constantes.EXITO);
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" listaCargaMasivaEmpleados " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica listaCargaMasivaEmpleadosEsCorrecto(Integer centroClienteId,Boolean isEsCorrecto) throws ServiceException {
        try{
            return new RespuestaGenerica(
                    cargaMasivaRepository.findByCentrocClienteIdAndEsCorrecto(centroClienteId,isEsCorrecto),
                    Constantes.RESULTADO_EXITO, Constantes.EXITO);
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" listaCargaMasivaEmpleadosEsCorrecto " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private RespuestaGenerica generaRespuestaCargaMasiva(Integer centroClienteId){
        if (cargaMasivaRepository.existsByCentrocClienteIdAndEsCorrecto(centroClienteId,false)){
            return new RespuestaGenerica(cargaMasivaRepository.findByCentrocClienteId(centroClienteId), Constantes.RESULTADO_ERROR, Constantes.ERROR_CARGA_MASIVA_EMPLEADOS);
        }else{
            return new RespuestaGenerica(cargaMasivaRepository.findByCentrocClienteId(centroClienteId), Constantes.RESULTADO_EXITO, Constantes.EXITO_CARGA_MASIVA_PTU);
        }
    }

    private RespuestaGenerica validarModificar(NcoPersonaDto personaDto) throws ServiceException {
        try {
            RespuestaGenerica respuesta = validaCamposObligatoriosModificar(personaDto);
            if (respuesta.isResultado()){
                respuesta = validaEstructuraCampos(personaDto);
                if (respuesta.isResultado()){
                    respuesta = validaCorreoModificar(personaDto);
                }
            }
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" validarModificar " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private RespuestaGenerica validar(NcoPersonaDto personaDto) throws ServiceException {
        try {
            RespuestaGenerica respuesta = validaCamposObligatorios(personaDto);
            if (respuesta.isResultado()){
                respuesta = validaEstructuraCampos(personaDto);
                if (respuesta.isResultado()){
                    respuesta = validaCorreo(personaDto);
                }
            }
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" validar " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private RespuestaGenerica validaCamposObligatorios(NcoPersonaDto ncoPersonaDto) throws ServiceException {
        try{
            RespuestaGenerica respuesta =  new RespuestaGenerica(null,Constantes.RESULTADO_EXITO,Constantes.EXITO);
            if(ncoPersonaDto.getNombre() == null
                    || ncoPersonaDto.getNombre().isEmpty()
                    || ncoPersonaDto.getApellidoPaterno() == null
                    || ncoPersonaDto.getApellidoPaterno().isEmpty()
                    || ncoPersonaDto.getEmailCorporativo() == null
                    || ncoPersonaDto.getEmailCorporativo().isEmpty()
                    || ncoPersonaDto.getRfc() == null
                    || ncoPersonaDto.getRfc().isEmpty()
                    || ncoPersonaDto.getCentrocClienteId() == null
                    || ncoPersonaDto.getCentrocClienteId().getCentrocClienteId() == null){
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(Constantes.CAMPOS_REQUERIDOS);
            }
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" validaCamposObligatorios " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private RespuestaGenerica validaCamposObligatoriosModificar(NcoPersonaDto ncoPersonaDto) throws ServiceException {
        try{
            RespuestaGenerica respuesta =  new RespuestaGenerica(null,Constantes.RESULTADO_EXITO,Constantes.EXITO);
            if(ncoPersonaDto.getNombre() == null
                    || ncoPersonaDto.getNombre().isEmpty()
                    || ncoPersonaDto.getApellidoPaterno() == null
                    || ncoPersonaDto.getApellidoPaterno().isEmpty()
                    || ncoPersonaDto.getRfc() == null
                    || ncoPersonaDto.getRfc().isEmpty()
                    || ncoPersonaDto.getCentrocClienteId() == null
                    || ncoPersonaDto.getCentrocClienteId().getCentrocClienteId() == null){
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(Constantes.CAMPOS_REQUERIDOS);
            }
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" validaCamposObligatoriosModificar " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private RespuestaGenerica validaEstructuraCampos(NcoPersonaDto personaDto) throws ServiceException {
        try{
            RespuestaGenerica respuesta = new RespuestaGenerica(null,Constantes.RESULTADO_EXITO, Constantes.EXITO);
            if (Validar.validaTexto(personaDto.getNss())){
                respuesta = Validar.numeroSerguroSocial(personaDto.getNss(), personaDto.getFechaNacimiento());
                if (!respuesta.isResultado()){
                    return respuesta;
                }else {
                    boolean duplicado = true;
                    if(personaDto.getPersonaId() != null){
                        NcoPersona persona =   ncoPersonaRepository.findByPersonaId(personaDto.getPersonaId());
                        if(!persona.getNss().equalsIgnoreCase((personaDto.getNss()))){
                            duplicado = ncoPersonaRepository.findCentroAndNSS(personaDto.getCentrocClienteId().getCentrocClienteId(),personaDto.getNss()).isEmpty();
                        }
                    }else{
                        duplicado = ncoPersonaRepository.findCentroAndNSS(personaDto.getCentrocClienteId().getCentrocClienteId(),personaDto.getNss()).isEmpty();
                    }

                    if(!duplicado){
                        respuesta.setResultado(Constantes.RESULTADO_ERROR);
                        respuesta.setMensaje(Constantes.NSS_DUPLICADO);
                        return respuesta;
                    }
                }
            }
            
            if (Validar.validaTexto(personaDto.getCurp())) {
                boolean duplicadoCurp = true;
                NcoPersona personaCurp = null;
                if(personaDto.getPersonaId() != null){
                    personaCurp =   ncoPersonaRepository.findByPersonaId(personaDto.getPersonaId());
                    if(!personaCurp.getCurp().equalsIgnoreCase((personaDto.getCurp()))){
                        duplicadoCurp = ncoPersonaRepository
                                .findCentroAndCurp(personaDto.getCentrocClienteId().getCentrocClienteId(),personaDto.getCurp())
                                .isEmpty();
                    }
                }else{
                    duplicadoCurp = ncoPersonaRepository
                            .findCentroAndCurp(personaDto.getCentrocClienteId().getCentrocClienteId(),personaDto.getCurp())
                            .isEmpty();
                }
                if (personaCurp != null) {
                    if (personaDto.getCurp().trim().equals(personaCurp.getCurp().trim())) {
                        duplicadoCurp=true;
                    }
                }

                if(!duplicadoCurp){
                    respuesta.setResultado(Constantes.RESULTADO_ERROR);
                    respuesta.setMensaje(Constantes.CURP_DUPLICADO);
                    return respuesta;
                }

                boolean duplicadoRfc = true;
                NcoPersona personaRFC = null;
                if(personaDto.getPersonaId() != null){
                    personaRFC =   ncoPersonaRepository.findByPersonaId(personaDto.getPersonaId());
                    if(!personaRFC.getCurp().equalsIgnoreCase((personaDto.getCurp()))){
                        duplicadoRfc = ncoPersonaRepository
                                .findCentroAndRfc(personaDto.getCentrocClienteId().getCentrocClienteId(),
                                        personaDto.getRfc()).isEmpty();

                    }
                }else{
                    duplicadoRfc = ncoPersonaRepository
                            .findCentroAndRfc(personaDto.getCentrocClienteId().getCentrocClienteId(),
                                    personaDto.getRfc()).isEmpty();

                }
                if (personaRFC != null) {
                    if (personaDto.getRfc().trim().equals(personaRFC.getRfc().trim())) {
                        duplicadoRfc=true;
                    }
                }

                if(!duplicadoRfc){
                    respuesta.setResultado(Constantes.RESULTADO_ERROR);
                    respuesta.setMensaje(Constantes.RFC_DUPLICADO);
                    return respuesta;
                }

                boolean anio2000;
                if(personaDto.getCurp().substring(16,17).matches("[0-9]+")){
                    anio2000 = false;
                }else{
                    anio2000 = true;
                }
                String anioCurp = personaDto.getCurp().substring(4, 6);
                Date date = new Date();
                SimpleDateFormat getYearFormat = new SimpleDateFormat("yy");
                String currentYear = getYearFormat.format(date);
                if (anio2000) {
                    if (Integer.parseInt(anioCurp) < Integer.parseInt(currentYear)) {
                        if (Validar.curp(personaDto.getCurp())) {
                            respuesta.setResultado(Constantes.RESULTADO_EXITO);
                            respuesta.setMensaje(Constantes.EXITO);
                        } else {
                            respuesta.setResultado(Constantes.RESULTADO_ERROR);
                            respuesta.setMensaje(Constantes.CURP_NO_VALIDO);
                            return respuesta;
                        }
                    } else {
                        respuesta.setResultado(Constantes.RESULTADO_ERROR);
                        respuesta.setMensaje(Constantes.CURP_NO_VALIDO);
                        return respuesta;
                    }
                } else {
                    if (Integer.parseInt(anioCurp) > Integer.parseInt(currentYear)) {
                        if (Validar.curp(personaDto.getCurp())) {
                            respuesta.setResultado(Constantes.RESULTADO_EXITO);
                            respuesta.setMensaje(Constantes.EXITO);
                        } else {
                            respuesta.setResultado(Constantes.RESULTADO_ERROR);
                            respuesta.setMensaje(Constantes.CURP_NO_VALIDO);
                            return respuesta;
                        }
                    } else {
                        respuesta.setResultado(Constantes.RESULTADO_ERROR);
                        respuesta.setMensaje(Constantes.CURP_NO_VALIDO);
                        return respuesta;
                    }
                }
            }

            /**if (Validar.validaTexto(personaDto.getRfc())){
                if (Validar.rfc(personaDto.getRfc())){
                    respuesta.setResultado(Constantes.RESULTADO_EXITO);
                    respuesta.setMensaje(Constantes.EXITO);
                }else{
                    respuesta.setResultado(Constantes.RESULTADO_ERROR);
                    respuesta.setMensaje(Constantes.RFC_NO_VALIDO);
                    return respuesta;
                }
            }*/

            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" validaEstructuraCampos " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private RespuestaGenerica validaCorreoModificar(NcoPersonaDto personaDto) throws ServiceException {
        try{
            RespuestaGenerica respuesta = validaCorreoPersonalModificar(personaDto);
            if (respuesta.isResultado()){
                respuesta = validaCorreoCorporativoModificar(personaDto);
            }
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" validaCorreoModificar " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private RespuestaGenerica validaCorreo(NcoPersonaDto personaDto) throws ServiceException {
        try{
            RespuestaGenerica respuesta = validaCorreoPersonal(personaDto);
            if (respuesta.isResultado()){
                respuesta = validaCorreoCorporativo(personaDto);
            }
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" validaCorreo " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private RespuestaGenerica validaCorreoPersonalModificar(NcoPersonaDto personaDto) throws ServiceException {
        RespuestaGenerica respuesta = new RespuestaGenerica(null, Constantes.RESULTADO_EXITO, Constantes.EXITO);
        try{
            if (personaDto.getContactoInicialEmailPersonal()!= null &&
                    !personaDto.getContactoInicialEmailPersonal().isEmpty()){
                if (Validar.correo(personaDto.getContactoInicialEmailPersonal())){
                    if (ncoPersonaRepository.existsByPersonaIdAndContactoInicialEmailPersonalAndTipoPersonaIdTipoPersonaId(
                            personaDto.getPersonaId(),
                            personaDto.getContactoInicialEmailPersonal(),
                            personaDto.getTipoPersonaId().getTipoPersonaId())){
                        respuesta.setResultado(Constantes.RESULTADO_EXITO);
                        respuesta.setMensaje(Constantes.EXITO);
                    }else{
                        return validaCorreoPersonal(personaDto);
                    }
                }else{
                    respuesta.setResultado(Constantes.RESULTADO_ERROR);
                    respuesta.setMensaje(Constantes.CORREO_NO_VALIDO);
                    return respuesta;
                }
            }
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" validaCorreoPersonalModificar " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private RespuestaGenerica validaCorreoPersonal(NcoPersonaDto personaDto) throws ServiceException {
        RespuestaGenerica respuesta = new RespuestaGenerica(null, Constantes.RESULTADO_EXITO, Constantes.EXITO);
        try{
            if (personaDto.getContactoInicialEmailPersonal()!= null &&
                    !personaDto.getContactoInicialEmailPersonal().isEmpty()){
                if (Validar.correo(personaDto.getContactoInicialEmailPersonal())){
                    if (!ncoPersonaRepository.existsByEmailCorporativoAndTipoPersonaIdTipoPersonaId(
                            personaDto.getContactoInicialEmailPersonal(),
                            personaDto.getTipoPersonaId().getTipoPersonaId())){
                        respuesta.setResultado(Constantes.RESULTADO_EXITO);
                        respuesta.setMensaje(Constantes.EXITO);
                    }else{
                        respuesta.setMensaje(Constantes.CORREO_DUPLICADO);
                        respuesta.setResultado(Constantes.RESULTADO_ERROR);
                    }
                }else{
                    respuesta.setResultado(Constantes.RESULTADO_ERROR);
                    respuesta.setMensaje(Constantes.CORREO_NO_VALIDO);
                    return respuesta;
                }
            }
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" validaCorreoPersonal " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private RespuestaGenerica validaCorreoCorporativo(NcoPersonaDto personaDto) throws ServiceException {
        try{
            RespuestaGenerica respuesta = new RespuestaGenerica(null, Constantes.RESULTADO_EXITO, Constantes.EXITO);
            if (personaDto.getEmailCorporativo()!= null &&
                    !personaDto.getEmailCorporativo().isEmpty()){
                if (Validar.correo(personaDto.getEmailCorporativo())){
                    if (!ncoPersonaRepository.existsByEmailCorporativoAndTipoPersonaIdTipoPersonaId(
                            personaDto.getEmailCorporativo(),
                            personaDto.getTipoPersonaId().getTipoPersonaId())){
                        respuesta.setResultado(Constantes.RESULTADO_EXITO);
                        respuesta.setMensaje(Constantes.EXITO);
                    }else{
                        respuesta.setMensaje(Constantes.CORREO_DUPLICADO);
                        respuesta.setResultado(Constantes.RESULTADO_ERROR);
                    }
                }else{
                    respuesta.setResultado(Constantes.RESULTADO_ERROR);
                    respuesta.setMensaje(Constantes.CORREO_NO_VALIDO);
                    return respuesta;
                }
            }
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" validaCorreoCorporativo " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private RespuestaGenerica validaCorreoCorporativoModificar(NcoPersonaDto personaDto) throws ServiceException {
        try{
            RespuestaGenerica respuesta = new RespuestaGenerica(null, Constantes.RESULTADO_EXITO, Constantes.EXITO);
            if (personaDto.getEmailCorporativo()!= null &&
                    !personaDto.getEmailCorporativo().isEmpty()){
                if (Validar.correo(personaDto.getEmailCorporativo())){
                    if (ncoPersonaRepository.existsByPersonaIdAndEmailCorporativoAndTipoPersonaIdTipoPersonaId(
                            personaDto.getPersonaId(),
                            personaDto.getEmailCorporativo(),
                            personaDto.getTipoPersonaId().getTipoPersonaId())){
                        respuesta.setResultado(Constantes.RESULTADO_EXITO);
                        respuesta.setMensaje(Constantes.EXITO);
                    }else{
                        return validaCorreoCorporativo(personaDto);
                    }
                }else{
                    respuesta.setResultado(Constantes.RESULTADO_ERROR);
                    respuesta.setMensaje(Constantes.CORREO_NO_VALIDO);
                    return respuesta;
                }
            }
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" validaCorreoCorporativoModificar " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private String generateUrl(NcoPersonaDto personaDto){
        UUID objectId = Utilidades.generateObjectId();
        return Constantes.PROYECTO + "/"
                + personaDto.getNombre() + "/"
                + personaDto.getRfc() + "/"
                + objectId;
    }

}
