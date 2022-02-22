package mx.com.ga.cosmonaut.empresa.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.*;

import mx.com.ga.cosmonaut.common.dto.NclAreaDto;
import mx.com.ga.cosmonaut.common.dto.NclPuestoDto;
import mx.com.ga.cosmonaut.common.dto.NclPuestoXareaDto;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.dto.consultas.ResultadoRepetidos;
import mx.com.ga.cosmonaut.common.entity.cliente.*;
import mx.com.ga.cosmonaut.common.interceptor.BitacoraUsuario;
import mx.com.ga.cosmonaut.common.repository.nativo.AbstractAreaRepository;
import mx.com.ga.cosmonaut.common.repository.nativo.AbstractPuestoRepository;
import mx.com.ga.cosmonaut.empresa.services.NclAreaService;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.common.repository.cliente.NclAreaRepository;
import mx.com.ga.cosmonaut.common.repository.cliente.NclGuardaPuestoRepository;
import mx.com.ga.cosmonaut.common.repository.cliente.NclPuestoRepository;
import mx.com.ga.cosmonaut.common.repository.cliente.NclPuestoXareaRepository;
import mx.com.ga.cosmonaut.common.repository.colaborador.NcoContratoColaboradorRepository;
import mx.com.ga.cosmonaut.common.util.Constantes;
import mx.com.ga.cosmonaut.common.util.ObjetoMapper;
import mx.com.ga.cosmonaut.common.util.Utilidades;

@Singleton
public class NclAreaServiceImpl implements NclAreaService {

    @Inject
    private NclAreaRepository nclAreaRepository;

    @Inject
    private NclGuardaPuestoRepository nclPuestoRepository;

    @Inject
    private NclPuestoRepository puestoRepository;

    @Inject
    private NclPuestoXareaRepository nclPuestoXareaRepository;

    @Inject
    private AbstractAreaRepository abstractAreaRepository;

    @Inject
    private AbstractPuestoRepository abstractPuestoRepository;

    @Inject
    private NcoContratoColaboradorRepository ncoContratoColaboradorRepository;

    @Override
    public RespuestaGenerica findAll() throws ServiceException {

        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            respuesta.setDatos(nclAreaRepository.findAll());
            respuesta.setMensaje(Constantes.EXITO);
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " findAll " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica obtenerAreas(Integer centrocClienteId) throws ServiceException {

        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            respuesta.setDatos(abstractAreaRepository.consultaEmpleadoXArea(centrocClienteId));
            respuesta.setMensaje(Constantes.EXITO);
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " obtenerAreas() " + Constantes.ERROR_EXCEPCION, e);
        }

    }

    public RespuestaGenerica obtenerAreaXEmpresa(Integer idCentrocCliente) throws ServiceException {

        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            respuesta.setDatos(abstractAreaRepository.consultaAreaXEmpresa(idCentrocCliente));
            respuesta.setResultado(true);
            respuesta.setMensaje(Constantes.EXITO);
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " obtenerAreaxEmpresa " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    public RespuestaGenerica obtenerPuestoXEmpleado(Integer idCentrocCliente, Integer idArea) throws ServiceException {

        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            respuesta.setDatos(abstractAreaRepository.obtenerPuestoXEmpleado(idCentrocCliente, idArea));
            respuesta.setResultado(true);
            respuesta.setMensaje(Constantes.EXITO);
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " obtenerPuestoXEmpleado " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica obtenerIdArea(Integer areaId) throws ServiceException {

        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            respuesta.setDatos(ObjetoMapper.map(
                    nclAreaRepository.findByAreaId(areaId).orElse(
                            new NclArea()), NclAreaDto.class));
            respuesta.setResultado(true);
            respuesta.setMensaje(Constantes.EXITO);
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " obtenerIdArea " + Constantes.ERROR_EXCEPCION, e);
        }
    }


    public RespuestaGenerica agregarPuesto(NclAreaDto nclAreaDto) throws ServiceException {
        try {
            NclGuardaPuestos nclPuesto = new NclGuardaPuestos();
            NclPuestoXareaDto nclPuestoXareaDto = new NclPuestoXareaDto();
            RespuestaGenerica respuesta = validaCamposObligatorios(nclAreaDto);
            if (respuesta.isResultado()) {
               boolean puestos = abstractPuestoRepository.consultaPuestosxEmpresaArea(nclAreaDto.getCentrocClienteId(),nclAreaDto.getAreaId(),nclAreaDto.getNclPuestoDto().get(0).getNombreCorto().toUpperCase().trim());
                if (puestos) {
                    if (nclAreaDto.getAreaId() != null && !nclAreaDto.getNclPuestoDto().isEmpty()) {
                        for (NclPuestoDto nclPuestoDto : nclAreaDto.getNclPuestoDto()) {
                            nclPuesto.setDescripcion(nclPuestoDto.getDescripcion());
                            nclPuesto.setNombreCorto(nclPuestoDto.getNombreCorto());
                            nclPuesto.setFechaAlta(Utilidades.obtenerFechaSystema());
                            nclPuesto.setPuestoIdReporta(nclPuestoDto.getPuestoIdReporta());
                            nclPuesto.setCentrocClienteId(nclPuestoDto.getCentrocClienteId());
                            nclPuesto.setEsActivo(Constantes.ESTATUS_ACTIVO);
                            respuesta = guardarPuesto(nclPuesto);
                            if (respuesta.isResultado()) {
                                nclPuestoXareaDto.setAreaId(nclAreaDto.getAreaId());
                                nclPuestoXareaDto.setPuestoId((Integer) respuesta.getDatos());
                                nclPuestoXareaDto.setEsActivo(Constantes.ESTATUS_ACTIVO);
                                nclPuestoXareaDto.setFechaAlta(Utilidades.obtenerFechaSystema());
                                respuesta = guardaRelacion(nclPuestoXareaDto);
                            }
                        }
                    } else {
                        respuesta.setMensaje(Constantes.AREA_REQ);
                    }
                } else{
                    respuesta.setResultado(Constantes.RESULTADO_ERROR);
                    respuesta.setMensaje("El puesto ya está dado de alta");
                }
            }
            return respuesta;
        } catch (ServiceException e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " agregarPuesto " + Constantes.ERROR_EXCEPCION, e);
        }

    }

    @Override
    public RespuestaGenerica guardar(NclAreaDto nclAreaDto) throws ServiceException {
        ResultadoRepetidos resultadoRepetidos = new ResultadoRepetidos();
        try {
            resultadoRepetidos=abstractAreaRepository.consultaAreaXEmpresaRepetida(nclAreaDto.getCentrocClienteId(),nclAreaDto.getNombreCorto().trim());
        } catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + "Area " + Constantes.ERROR_EXCEPCION, e);
        }

        if (resultadoRepetidos.getResultado()==0) {
            try {
                NclArea nclArea = new NclArea();
                NclGuardaPuestos nclPuesto = new NclGuardaPuestos();
                List<NclPuestoDto> listaNclPuestoDto = new ArrayList<>();
                NclPuestoXareaDto nclPuestoXareaDto = new NclPuestoXareaDto();
                RespuestaGenerica respuesta = validaCamposObligatorios(nclAreaDto);
                if (respuesta.isResultado()) {
                    if (!nclAreaDto.getNclPuestoDto().isEmpty()) {
                        listaNclPuestoDto = nclAreaDto.getNclPuestoDto();
                    }
                    if (nclAreaDto.getDescripcion() == null) {
                        nclAreaDto.setDescripcion(Constantes.ESPACIO);
                    }
                    nclArea.setDescripcion(nclAreaDto.getDescripcion());
                    nclArea.setNombreCorto(nclAreaDto.getNombreCorto());
                    nclArea.setFechaAlta(Utilidades.obtenerFechaSystema());
                    nclArea.setCentrocClienteId(nclAreaDto.getCentrocClienteId());
                    nclArea.setEsActivo(Constantes.ESTATUS_ACTIVO);
                    respuesta = guardarArea(nclArea);
                    if (respuesta.isResultado()) {
                        nclPuestoXareaDto.setAreaId((Integer) respuesta.getDatos());
                        for (NclPuestoDto nclPuestoDto : listaNclPuestoDto) {
                            nclPuesto.setDescripcion(nclPuestoDto.getDescripcion());
                            nclPuesto.setNombreCorto(nclPuestoDto.getNombreCorto());
                            nclPuesto.setFechaAlta(Utilidades.obtenerFechaSystema());
                            nclPuesto.setPuestoIdReporta(nclPuestoDto.getPuestoIdReporta());
                            nclPuesto.setCentrocClienteId(nclPuestoDto.getCentrocClienteId());
                            nclPuesto.setEsActivo(Constantes.ESTATUS_ACTIVO);
                            respuesta = guardarPuesto(nclPuesto);
                            if (respuesta.isResultado()) {
                                nclPuestoXareaDto.setPuestoId((Integer) respuesta.getDatos());
                                nclPuestoXareaDto.setEsActivo(Constantes.ESTATUS_ACTIVO);
                                nclPuestoXareaDto.setFechaAlta(Utilidades.obtenerFechaSystema());
                                respuesta = guardaRelacion(nclPuestoXareaDto);
                            }
                        }
                    }
                }
                return respuesta;
            } catch (ServiceException e) {
                throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                        + Constantes.ERROR_METODO + " guardar " + Constantes.ERROR_EXCEPCION, e);
            }
        }
            else{
                throw new ServiceException("Area repetida");
            }
        }

    public RespuestaGenerica guardaRelacion(NclPuestoXareaDto nclPuestoXareaDto) throws ServiceException {
        RespuestaGenerica respuesta = new RespuestaGenerica();

        try {
            if (nclPuestoXareaDto.getAreaId() != null) {
                nclPuestoXareaDto = ObjetoMapper.map(nclPuestoXareaRepository.save(ObjetoMapper.map(nclPuestoXareaDto, NclPuestoXarea.class
                )), NclPuestoXareaDto.class
                );
                nclPuestoXareaDto.setNombreArea(nclAreaRepository.obtieneDescripcionArea(nclPuestoXareaDto.getAreaId()));
                nclPuestoXareaDto.setNombrePuesto(puestoRepository.obtieneDescripcionPuesto(nclPuestoXareaDto.getPuestoId()));
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
                respuesta.setDatos(nclPuestoXareaDto);
                respuesta.setMensaje(Constantes.EXITO);
            } else {
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(Constantes.ERROR);
            }
            return respuesta;
        } catch (Exception e) {
            respuesta.setDatos(e.getCause());
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " guardaRelacion " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    public RespuestaGenerica guardarPuesto(NclGuardaPuestos nclPuesto) throws ServiceException {
        RespuestaGenerica respuesta = new RespuestaGenerica();
        try {
            respuesta.setDatos(nclPuestoRepository.save(nclPuesto).getPuestoId());
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            respuesta.setMensaje(Constantes.EXITO);
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " guardaPuesto " + Constantes.ERROR_EXCEPCION, e);
        }

        return respuesta;
    }

    public RespuestaGenerica guardarArea(NclArea nclArea) throws ServiceException {

        RespuestaGenerica respuesta = new RespuestaGenerica();

        try {
            respuesta.setDatos(ObjetoMapper.map(nclAreaRepository.save(nclArea), NclAreaDto.class
            ).getAreaId());
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            respuesta.setMensaje(Constantes.EXITO);
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " guardaArea " + Constantes.ERROR_EXCEPCION, e);
        }

        return respuesta;

    }


    @Override
    public RespuestaGenerica modificar(NclAreaDto nclAreaDto) throws ServiceException {
        ResultadoRepetidos resultadoRepetidos = new ResultadoRepetidos();
        boolean editableeditable;
        boolean editablepuesto= false ;
        boolean repetido = false;
        List<NclPuesto> puestos;
        try {
            Optional<NclArea> byAreaId = nclAreaRepository.findByAreaId(nclAreaDto.getAreaId());

            if (!nclAreaDto.getNclPuestoDto().isEmpty())
            {
                puestos = puestoRepository.obtieneDescripcionPuestoArea(nclAreaDto.getCentrocClienteId(), nclAreaDto.getAreaId(), nclAreaDto.getNclPuestoDto().get(0).getPuestoId());

            if (puestos.stream().anyMatch(str -> str.getNombreCorto().toUpperCase().trim().equals(nclAreaDto.getNclPuestoDto().get(0).getNombreCorto().toUpperCase().trim()))) {
                editablepuesto = true;
            } else {
                puestos = puestoRepository.obtieneAreaPuesto(nclAreaDto.getCentrocClienteId(), nclAreaDto.getAreaId());
                repetido = puestos.stream().anyMatch(str -> str.getNombreCorto().toUpperCase().trim().equals(nclAreaDto.getNclPuestoDto().get(0).getNombreCorto().toUpperCase().trim()));
            }
        }
            editableeditable = byAreaId.get().getNombreCorto().toUpperCase().trim().equals(nclAreaDto.getNombreCorto().toUpperCase().trim());
            if (editableeditable)
            {
                editableeditable = true;
            }else{
                resultadoRepetidos = abstractAreaRepository.consultaAreaXEmpresaRepetida(nclAreaDto.getCentrocClienteId(), nclAreaDto.getNombreCorto().toUpperCase().trim());
                editableeditable=resultadoRepetidos.getResultado()==0;
            }
        } catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + "Area " + Constantes.ERROR_EXCEPCION, e);
        }

        if (editableeditable) {
            try {
                RespuestaGenerica respuesta = new RespuestaGenerica();
                NclArea nclArea = new NclArea();
                NclGuardaPuestos nclPuesto = new NclGuardaPuestos();
                List<NclPuestoDto> listaNclPuestoDto;
                if (nclAreaDto.getAreaId() != null) {
                    if (editablepuesto || !repetido) {

                        nclArea.setAreaId(nclAreaDto.getAreaId());
                        nclArea.setDescripcion(nclAreaDto.getDescripcion());
                        nclArea.setCentrocClienteId(nclAreaDto.getCentrocClienteId());
                        nclArea.setNombreCorto(nclAreaDto.getNombreCorto());
                        nclArea.setFechaAlta(Utilidades.obtenerFechaSystema());
                        nclArea.setEsActivo(Constantes.ESTATUS_ACTIVO);
                        respuesta = modificarArea(nclArea);
                        if (respuesta.isResultado() && !nclAreaDto.getNclPuestoDto().isEmpty()) {
                            listaNclPuestoDto = nclAreaDto.getNclPuestoDto();
                            for (NclPuestoDto nclPuestoDto : listaNclPuestoDto) {
                                if (nclPuestoDto.getPuestoId() != null) {
                                    nclPuesto.setPuestoId(nclPuestoDto.getPuestoId());
                                    nclPuesto.setDescripcion(nclPuestoDto.getDescripcion());
                                    nclPuesto.setCentrocClienteId(nclPuestoDto.getCentrocClienteId());
                                    nclPuesto.setNombreCorto(nclPuestoDto.getNombreCorto());
                                    nclPuesto.setFechaAlta(Utilidades.obtenerFechaSystema());
                                    nclPuesto.setPuestoIdReporta(nclPuestoDto.getPuestoIdReporta());
                                    nclPuesto.setEsActivo(Constantes.ESTATUS_ACTIVO);
                                    respuesta = modificarPuesto(nclPuesto);
                                } else {
                                    respuesta.setResultado(Constantes.RESULTADO_ERROR);
                                    respuesta.setMensaje(Constantes.PUESTO_REQU);
                                }
                            }
                        }
                    }
                    else {
                        respuesta.setResultado(Constantes.RESULTADO_ERROR);
                        respuesta.setMensaje("El puesto ya existe");
                    }
                } else {
                    respuesta.setResultado(Constantes.RESULTADO_ERROR);
                    respuesta.setMensaje(Constantes.AREA_REQU);
                }
                return respuesta;
            } catch (ServiceException e) {
                throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                        + Constantes.ERROR_METODO + " modificar " + Constantes.ERROR_EXCEPCION, e);
            }
        }
        else{
                throw new ServiceException("Area repetida");
            }
    }

    public RespuestaGenerica modificarArea(NclArea nclArea) throws ServiceException {
        RespuestaGenerica respuesta = new RespuestaGenerica();

        try {
            respuesta.setDatos(ObjetoMapper.map(nclAreaRepository.update(nclArea), NclAreaDto.class
            ));
            respuesta.setResultado(Constantes.RESULTADO_EXITO);

            respuesta.setMensaje(Constantes.EXITO);
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " modificarArea " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    public RespuestaGenerica modificarPuesto(NclGuardaPuestos nclPuesto) throws ServiceException {
        RespuestaGenerica respuesta = new RespuestaGenerica();
        try {
            nclPuestoRepository.update(nclPuesto);
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            respuesta.setMensaje(Constantes.EXITO);
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " modificarPuesto " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private RespuestaGenerica validaCamposObligatorios(NclAreaDto nclAreaDto) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            if (nclAreaDto.getCentrocClienteId() == null || nclAreaDto.getNclPuestoDto().isEmpty()) {
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(Constantes.CAMPOS_REQUERIDOS);
            } else {
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
                respuesta.setMensaje(Constantes.EXITO);
            }
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " validaCamposObligatorios " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    public RespuestaGenerica listarDinamica(NclEmpleadoXArea nclEmpleadoXArea) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            if (nclEmpleadoXArea != null) {
                respuesta.setDatos(abstractAreaRepository.consultaAreaXEmpresaDinamica(nclEmpleadoXArea));
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
                respuesta.setMensaje(Constantes.EXITO);
            } else {
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(Constantes.ERROR);
            }

            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " listarDinamica " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    public RespuestaGenerica eliminaRelacionPuestoArea(NclPuestoDto nclPuestoDto) throws ServiceException {
        RespuestaGenerica respuesta = new RespuestaGenerica();
        try {
            respuesta.setDatos(nclPuestoXareaRepository.update(nclPuestoDto.getPuestoId(), Constantes.ESTATUS_INACTIVO));
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            respuesta.setMensaje(Constantes.EXITO);
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " eliminaRelacionPuestoArea " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    public RespuestaGenerica eliminaRelacionArea(NclAreaDto nclAreaDto) throws ServiceException {
        RespuestaGenerica respuesta = new RespuestaGenerica();
        List<NclPuestoXarea> listaRelacion;
        try {
            listaRelacion = nclPuestoXareaRepository.obtenerXId(nclAreaDto.getAreaId());
            for (NclPuestoXarea nclPuestoxArea : listaRelacion) {
                nclPuestoxArea.setEsActivo(Constantes.ESTATUS_INACTIVO);
                respuesta
                        .setDatos(ObjetoMapper.map(nclPuestoXareaRepository.update(nclPuestoxArea), NclPuestoXareaDto.class
                        ));
                respuesta.setDatos(ObjetoMapper.map(nclPuestoRepository.update(nclPuestoxArea.getPuestoId(), Constantes.ESTATUS_INACTIVO), NclPuestoXareaDto.class
                ));
            }
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            respuesta.setMensaje(Constantes.EXITO);
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " eliminaRelacionArea " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    public RespuestaGenerica eliminaPuesto(NclPuestoDto nclPuestoDto) throws ServiceException {
        RespuestaGenerica respuesta = new RespuestaGenerica();
        try {
            Optional<NclGuardaPuestos> puesto = nclPuestoRepository.findById(nclPuestoDto.getPuestoId());
            if (!puesto.isEmpty()) {
                nclPuestoDto.setCentrocClienteId(nclPuestoDto.getCentrocClienteId());
                nclPuestoDto.setDescripcion(puesto.get().getDescripcion());
                nclPuestoDto.setNombreCorto(puesto.get().getNombreCorto());
                nclPuestoDto.setFechaAlta(Utilidades.obtenerFechaSystema());
                nclPuestoDto.setPuestoIdReporta(puesto.get().getPuestoIdReporta());
                nclPuestoDto.setEsActivo(Constantes.ESTATUS_INACTIVO);

            }
            respuesta.setDatos(ObjetoMapper.map(nclPuestoRepository.update(ObjetoMapper.map(nclPuestoDto, NclGuardaPuestos.class
            )), NclPuestoDto.class
            ));
            respuesta.setResultado(Constantes.RESULTADO_EXITO);

            respuesta.setMensaje(Constantes.EXITO);
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " eliminaPuesto " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private RespuestaGenerica buscaPuestosPorAreaEliminar(NclAreaDto nclAreaDto) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            for (NclPuestoDto nclPuestoDto : nclAreaDto.getNclPuestoDto()) {
                Integer count = ncoContratoColaboradorRepository.findAreaPuestoXColaborador(nclAreaDto.getCentrocClienteId(), nclAreaDto.getAreaId(), nclPuestoDto.getPuestoId());
                if (count != null && count.equals(Constantes.CERO)) {
                    respuesta = eliminaRelacionPuestoArea(nclPuestoDto);
                    if (respuesta.isResultado()) {
                        respuesta = eliminaPuesto(nclPuestoDto);
                    }
                } else {
                    respuesta.setResultado(Constantes.RESULTADO_ERROR);
                    respuesta.setMensaje(Constantes.PUESTO_REL + count + Constantes.AREAPUESTO_REL);
                }
            }
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " buscaPuestosPorAreaEliminar " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private RespuestaGenerica buscaAreaEliminar(NclAreaDto nclAreaDto) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            Integer count = ncoContratoColaboradorRepository.findAreaXColaborador(nclAreaDto.getCentrocClienteId(), nclAreaDto.getAreaId());
            if (count != null && count.equals(Constantes.CERO)) {
                respuesta = eliminaRelacionArea(nclAreaDto);
                if (respuesta.isResultado()) {
                    nclAreaRepository.update(nclAreaDto.getAreaId(), Constantes.ESTATUS_INACTIVO);
                    respuesta.setMensaje(Constantes.EXITO);
                    respuesta.setResultado(Constantes.RESULTADO_EXITO);
                }
            } else {
                respuesta.setMensaje(Constantes.AREA_REL + count + Constantes.AREAPUESTO_REL);
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
            }
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " eliminarArea " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica eliminarArea(NclAreaDto nclAreaDto) throws ServiceException {
        RespuestaGenerica respuesta = new RespuestaGenerica();
        if (nclAreaDto.getAreaId() != null && nclAreaDto.getCentrocClienteId() != null) {
            if (nclAreaDto.getNclPuestoDto() != null) {
                respuesta = buscaPuestosPorAreaEliminar(nclAreaDto);
            } else {
                respuesta = buscaAreaEliminar(nclAreaDto);
            }
        } else {
            respuesta.setMensaje(Constantes.AREA_ELIMINAR);
            respuesta.setResultado(Constantes.RESULTADO_ERROR);
        }
        return respuesta;
    }

    @Override
    public RespuestaGenerica findByEsActivo(Boolean activo) throws ServiceException {
        RespuestaGenerica respuesta = new RespuestaGenerica();
        try {
            respuesta.setDatos(nclAreaRepository.findByEsActivoOrderByDescripcion(activo));
            respuesta.setMensaje(Constantes.EXITO);
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " findByEsActivo " + Constantes.ERROR_EXCEPCION, e);
        }
        return respuesta;
    }
}
