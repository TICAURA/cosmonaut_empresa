package mx.com.ga.cosmonaut.empresa.services.impl;

import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.dto.cliente.CredencialesImssDto;
import mx.com.ga.cosmonaut.common.dto.imss.GuardarCsdImssRequestDto;
import mx.com.ga.cosmonaut.common.dto.imss.GuardarCsdImssResponseDto;
import mx.com.ga.cosmonaut.common.entity.cliente.NclRegistroPatronal;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.common.repository.cliente.NclRegistroPatronalRepository;
import mx.com.ga.cosmonaut.common.util.Constantes;
import mx.com.ga.cosmonaut.empresa.services.CredencialesImssServices;
import mx.com.ga.cosmonaut.empresa.services.RegistroPatronalServices;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Singleton
public class RegistroPatronalServicesImpl implements RegistroPatronalServices {

    @Inject
    private NclRegistroPatronalRepository nclRegistroPatronalRepository;

    @Inject
    private CredencialesImssServices credencialesImssServices;

    @Override
    public RespuestaGenerica guardar(NclRegistroPatronal registroPatronal,
                                     CredencialesImssDto credencialesImss) throws ServiceException {
        try{
            RespuestaGenerica respuesta = validaCamposObligatorios(registroPatronal);
            if (respuesta.isResultado()) {
                // TECTEL: SOLO SI ESTAN LAS CREDENCIALES COMPLETAS SE ALMACENA CSD
                if (errorGuardarCredencialesImss(registroPatronal, credencialesImss, respuesta)) {
                    return respuesta;
                }
                registroPatronal.setEsActivo(Constantes.ESTATUS_ACTIVO);

                respuesta.setDatos(nclRegistroPatronalRepository.save(registroPatronal));
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
    public RespuestaGenerica modificar(NclRegistroPatronal registroPatronal,
                                       CredencialesImssDto credencialesImss) throws ServiceException {
        try{
            RespuestaGenerica respuesta =  new RespuestaGenerica();
            if (registroPatronal.getRegistroPatronalId() != null){
                respuesta = validaCamposObligatorios(registroPatronal);
                Optional<NclRegistroPatronal> registroPatronalEntityOp = nclRegistroPatronalRepository
                        .findById(registroPatronal.getRegistroPatronalId());
                if (respuesta.isResultado() && registroPatronalEntityOp.isPresent()){
                    if (registroPatronalEntityOp.get().getCredencialesImssId() != null) {
                        registroPatronal.setCredencialesImssId(registroPatronalEntityOp.get().getCredencialesImssId());
                    }

                    // TECTEL: SOLO SI ESTAN LAS CREDENCIALES COMPLETAS SE ALMACENA CSD
                    if (errorGuardarCredencialesImss(registroPatronal, credencialesImss, respuesta)) {
                        return respuesta;
                    }

                    respuesta.setDatos(nclRegistroPatronalRepository.update(registroPatronal));
                    respuesta.setResultado(Constantes.RESULTADO_EXITO);
                    respuesta.setMensaje(Constantes.EXITO);
                }
            }else{
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(Constantes.ERROR_NO_EXISTE);
            }
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" modificar " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private boolean errorGuardarCredencialesImss(NclRegistroPatronal registroPatronal, CredencialesImssDto credencialesImss,
                                            RespuestaGenerica respuesta) throws ServiceException {
        if (credencialesImss.getUsuarioImss() != null && credencialesImss.getPwdImss() != null
                && credencialesImss.getCertificadoImss() != null) {

            GuardarCsdImssResponseDto imss = guardarCredImss(credencialesImss);
            if (imss.isExito()) {
                registroPatronal.setCredencialesImssId(imss.getContenido().getCsd_id());
            } else {
                respuesta.setDatos(null);
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(imss.getMensaje());
                return true;
            }
        }
        return false;
    }

    private GuardarCsdImssResponseDto guardarCredImss(CredencialesImssDto credencialesImss) throws ServiceException {
        try {
            GuardarCsdImssRequestDto peticion = new GuardarCsdImssRequestDto();
            String base64ArchivoZip = this.createArchivoCertificado(credencialesImss.getCertificadoImss());
            peticion.setCertificadoCds(credencialesImss.getUsuarioImss());
            peticion.setLlaveCds(base64ArchivoZip);
            peticion.setContraseniaCsd(credencialesImss.getPwdImss());
            return credencialesImssServices.guardarCredencialesImss(peticion);
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" guardarCredImss " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica obtenerId(Long registroPatronalId) throws ServiceException {
        try{
            RespuestaGenerica respuesta = new RespuestaGenerica();
            respuesta.setDatos(
                    nclRegistroPatronalRepository.findById(registroPatronalId.intValue()).orElse(new NclRegistroPatronal()));
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            respuesta.setMensaje(Constantes.EXITO);
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" obtenerId " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica obtenerEmpresaId(Long registroPatronalId) throws ServiceException {
        try{
            RespuestaGenerica respuesta = new RespuestaGenerica();
            respuesta.setDatos(
                    nclRegistroPatronalRepository.findByCentrocClienteIdCentrocClienteId(registroPatronalId.intValue()).
                            orElse(new NclRegistroPatronal()));
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            respuesta.setMensaje(Constantes.EXITO);
            return respuesta;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" obtenerId " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private RespuestaGenerica validaCamposObligatorios(NclRegistroPatronal registroPatronal) throws ServiceException {
        try{
            RespuestaGenerica respuesta =  new RespuestaGenerica();
            if(registroPatronal.getEmClaveDelegacionalImss() == null
                || registroPatronal.getEmPrimaRiesgo() == null){
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

    private String createArchivoCertificado(String certificadoPfx) throws ServiceException {

        ByteArrayOutputStream archivoArray = this.createArchivoTxt(certificadoPfx);

        String comprimido = this.generarZip(archivoArray.toByteArray());

        return comprimido;

    }


    private ByteArrayOutputStream createArchivoTxt(String certificadoPfx) throws ServiceException {
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        try (OutputStreamWriter escribir = new OutputStreamWriter(salida)) {
            escribir.write(certificadoPfx);
        }catch (Exception ex) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " escribirArchivoIdseConsulta " + Constantes.ERROR_EXCEPCION, ex);
        }
        return salida;
    }


    private String generarZip(byte[] archivo){
        String respuesta = "";
        try{
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            ZipOutputStream zos = new ZipOutputStream(baos);

            String uuid = UUID.randomUUID().toString();

            ZipEntry entrada = new ZipEntry(String.valueOf(uuid+"_archivoPfxZip"+".txt"));
            zos.putNextEntry(entrada);
            zos.write(archivo);
            zos.closeEntry();

            zos.close();
            byte[] bytes = baos.toByteArray();
            String encodedBase64 = new String(Base64.getEncoder().encodeToString(bytes));
            respuesta = encodedBase64;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return respuesta;
    }

}
