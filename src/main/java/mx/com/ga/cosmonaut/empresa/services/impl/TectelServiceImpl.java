package mx.com.ga.cosmonaut.empresa.services.impl;

import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.dto.RespuestaGoogleStorage;
import mx.com.ga.cosmonaut.common.dto.consultas.IdseConsulta;
import mx.com.ga.cosmonaut.common.dto.imss.tectel.AfiliaRecepcionRequest;
import mx.com.ga.cosmonaut.common.dto.imss.tectel.AfiliaRecepcionRequestDto;
import mx.com.ga.cosmonaut.common.dto.imss.tectel.AfiliaRecepcionResponse;
import mx.com.ga.cosmonaut.common.dto.imss.tectel.AfiliaRecepcionResponseDto;
import mx.com.ga.cosmonaut.common.entity.catalogo.negocio.CatEstatusIdse;
import mx.com.ga.cosmonaut.common.entity.colaborador.NcoBitacoraKardexEstatusIdse;
import mx.com.ga.cosmonaut.common.entity.colaborador.NcoKardexEstatusIdse;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.common.repository.colaborador.NcoBitacoraKardexEstatusIdseRepository;
import mx.com.ga.cosmonaut.common.repository.colaborador.NcoKardexColaboradorRepository;
import mx.com.ga.cosmonaut.common.repository.colaborador.NcoKardexEstatusIdseRepository;
import mx.com.ga.cosmonaut.common.repository.nativo.EmpleadosReporteRepository;
import mx.com.ga.cosmonaut.common.repository.nativo.KardexRepository;
import mx.com.ga.cosmonaut.common.service.GoogleStorageService;
import mx.com.ga.cosmonaut.common.service.IdseService;
import mx.com.ga.cosmonaut.common.util.Constantes;
import mx.com.ga.cosmonaut.common.util.ObjetoMapper;
import mx.com.ga.cosmonaut.common.util.Utilidades;
import mx.com.ga.cosmonaut.empresa.services.CredencialesImssServices;
import mx.com.ga.cosmonaut.empresa.services.TectelService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Singleton
public class TectelServiceImpl implements TectelService {

    private static final String CODIGO_EXITO = "20";

    @Inject
    private CredencialesImssServices credencialesImssServices;

    @Inject
    private NcoKardexColaboradorRepository ncoKardexColaboradorRepository;

    @Inject
    private NcoKardexEstatusIdseRepository ncoKardexEstatusIdseRepository;

    @Inject
    private NcoBitacoraKardexEstatusIdseRepository ncoBitacoraKardexEstatusIdseRepository;

    @Inject
    private EmpleadosReporteRepository empleadosReporteRepository;

    @Inject
    private GoogleStorageService googlestorage;

    @Inject
    private IdseService idseService;

    @Override
    public RespuestaGenerica afiliaRecepcion(AfiliaRecepcionRequest request) throws ServiceException {
        try {



            List<IdseConsulta> lista =  empleadosReporteRepository.idseConsultaByIdsKardex(request.getClienteId(),request.getMovimientosKardexIds());


            for(IdseConsulta item : lista){
              boolean estatus =   ncoKardexEstatusIdseRepository.existsById(Long.valueOf(item.getKardexId()));
              if(estatus){
                  ncoKardexEstatusIdseRepository.deleteById(Long.valueOf(item.getKardexId()));
              }
            }


            ByteArrayOutputStream salida =   escribirArchivoIdseConsulta(lista);

            String base64Zip = AnexarZip(salida.toByteArray());

            String idProceso = generarIdProceso();
            RespuestaGenerica response = new RespuestaGenerica();

            AfiliaRecepcionRequestDto peticion = new AfiliaRecepcionRequestDto();
            peticion.setIdProceso(idProceso);
            peticion.setArchivoDispmagZip(base64Zip);
            peticion.setRegistroPatronal(request.getRegistroPatronal());
            peticion.setIdCsd(request.getIdCsd());



            AfiliaRecepcionResponseDto respuesta = credencialesImssServices.afiliaRecepcion(peticion);

            if (respuesta != null && respuesta.getEstado().equals(CODIGO_EXITO)) {
                if (request.getMovimientosKardexIds() != null) {
                    CatEstatusIdse catEstatusIdse = new CatEstatusIdse();
                    catEstatusIdse.setEstatusIdseId(1L);
                    for (Long id : request.getMovimientosKardexIds()) {
                        if (ncoKardexColaboradorRepository.existsById(id)) {
                            NcoKardexEstatusIdse kardexEstatusIdse = new NcoKardexEstatusIdse();
                            kardexEstatusIdse.setKardexColaboradorId(id);
                            kardexEstatusIdse.setIdProceso(idProceso);
                            kardexEstatusIdse.setEstatusIdseId(catEstatusIdse);
                            kardexEstatusIdse.setFechaEstatus(Utilidades.obtenerFechaSystema());

                            guardarKardexIdseAndGenerarBitacora(kardexEstatusIdse);
                        }
                    }
                }

                AfiliaRecepcionResponse data = ObjetoMapper.map(respuesta, new AfiliaRecepcionResponse());
                data.setIdProceso(idProceso);

                response.setResultado(Constantes.RESULTADO_EXITO);
                response.setMensaje(Constantes.EXITO);
                response.setDatos(data);
            } else {
                response.setResultado(Constantes.RESULTADO_ERROR);
                response.setMensaje(respuesta.getMensaje());
            }


            return response;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" afiliaRecepcion " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica getAcuseRecibo(Integer idKardex) throws ServiceException {
        RespuestaGenerica respuesta = new RespuestaGenerica();
        Optional<NcoKardexEstatusIdse> optKardex =  ncoKardexEstatusIdseRepository.findById(Long.valueOf(idKardex));
        if(optKardex.isPresent()){
            NcoKardexEstatusIdse kardex = optKardex.get();
            if(kardex.getReferenciaAcuse() != null){
                RespuestaGoogleStorage respuestaStorage =  googlestorage.obtenerArchivo(kardex.getReferenciaAcuse());
                respuesta.setDatos(respuestaStorage.getArreglo());
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
                respuesta.setMensaje(Constantes.EXITO);
            }else{
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(Constantes.MENSAJE_SIN_RESULTADOS);
            }
        }else{
            respuesta.setResultado(Constantes.RESULTADO_ERROR);
            respuesta.setMensaje(Constantes.MENSAJE_SIN_RESULTADOS);
        }


        return respuesta;
    }

    @Override
    public RespuestaGenerica getConstanciaPresentacion(Integer idKardex) throws ServiceException {
        RespuestaGenerica respuesta = new RespuestaGenerica();
        Optional<NcoKardexEstatusIdse> optKardex =  ncoKardexEstatusIdseRepository.findById(Long.valueOf(idKardex));
        if(optKardex.isPresent()){
            NcoKardexEstatusIdse kardex = optKardex.get();
            if(kardex.getReferenciaPatronal() != null){
                RespuestaGoogleStorage respuestaStorage =  googlestorage.obtenerArchivo(kardex.getReferenciaPatronal());
                respuesta.setDatos(respuestaStorage.getArreglo());
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
                respuesta.setMensaje(Constantes.EXITO);
            }else{
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(Constantes.MENSAJE_SIN_RESULTADOS);
            }
        }else{
            respuesta.setResultado(Constantes.RESULTADO_ERROR);
            respuesta.setMensaje(Constantes.MENSAJE_SIN_RESULTADOS);
        }


        return respuesta;
    }

    private String AnexarZip(byte[] archivo){
        String respuesta = "";
        try{
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            ZipOutputStream zos = new ZipOutputStream(baos);

            String uuid = UUID.randomUUID().toString();

                ZipEntry entrada = new ZipEntry(String.valueOf(uuid+".txt"));
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

    private String generarIdProceso() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
        return sdf.format(new Date());
    }

    private void guardarKardexIdseAndGenerarBitacora(NcoKardexEstatusIdse kardexEstatusIdse) {
        ncoKardexEstatusIdseRepository.save(kardexEstatusIdse);

        NcoBitacoraKardexEstatusIdse ncoBitacoraKardexEstatusIdse = new NcoBitacoraKardexEstatusIdse();
        ncoBitacoraKardexEstatusIdse.setKardexColaboradorId(kardexEstatusIdse.getKardexColaboradorId());
        ncoBitacoraKardexEstatusIdse.setIdProceso(kardexEstatusIdse.getIdProceso());
        ncoBitacoraKardexEstatusIdse.setEstatusIdseId(kardexEstatusIdse.getEstatusIdseId().getEstatusIdseId());
        ncoBitacoraKardexEstatusIdse.setFechaEstatus(kardexEstatusIdse.getFechaEstatus());
        if (kardexEstatusIdse.getNumeroLote() != null) {
            ncoBitacoraKardexEstatusIdse.setNumeroLote(kardexEstatusIdse.getNumeroLote());
        }
        if (kardexEstatusIdse.getReferenciaAcuse() != null) {
            ncoBitacoraKardexEstatusIdse.setReferenciaAcuse(kardexEstatusIdse.getReferenciaAcuse());
        }
        if (kardexEstatusIdse.getReferenciaPatronal() != null) {
            ncoBitacoraKardexEstatusIdse.setReferenciaPatronal(kardexEstatusIdse.getReferenciaPatronal());
        }
        ncoBitacoraKardexEstatusIdseRepository.save(ncoBitacoraKardexEstatusIdse);
    }

    private ByteArrayOutputStream escribirArchivoIdseConsulta(List<IdseConsulta> listaIdseAltasReingresos) throws ServiceException {
        try {
            return idseService.escribirArchivoIdseConsulta(listaIdseAltasReingresos);
        } catch (Exception ex) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " escribirArchivoIdseConsulta " + Constantes.ERROR_EXCEPCION, ex);
        }
    }

    public static String validaCadenaComplementaria(String valor, Integer longitud, Integer lado, Integer ceros) throws ServiceException{

            valor = validaContenido(valor);

            if (valor.length() > longitud) {
                valor = valor.substring(0,longitud);
            }

            if (valor.length() < longitud) {

                /** lado derecho.*/
                if (lado == 1) {
                    valor = String.format("%-".concat(String.valueOf(longitud).concat("s")) , valor);
                } else { /**lado izquierdo*/
                    valor = String.format("%".concat(longitud.toString()).concat("s"), valor);
                }
                if (ceros == 1) {
                    valor = valor.replace(" ", "0");
                }

            }

        return valor;
    }

    public static String validaContenido(String valor) throws ServiceException {
        return (Boolean.TRUE.equals(validaCadena(valor)) ? valor : "");
    }

    public static Boolean validaCadena(String cadena) {
        return (cadena != null && !cadena.isEmpty() && !cadena.equalsIgnoreCase("null")
                ? Boolean.TRUE : Boolean.FALSE);
    }

    private String tipoSalarioCalculo(String tipoSalario) {
        Integer ES_UNO = 1;
        if (tipoSalario != null) {
            return String.valueOf(Integer.parseInt(tipoSalario) - ES_UNO);
        }
        return " ";
    }

}
