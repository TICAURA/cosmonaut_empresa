package mx.com.ga.cosmonaut.empresa.services.impl;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

import mx.com.ga.cosmonaut.common.dto.*;
import mx.com.ga.cosmonaut.common.dto.consultas.ResultadoBoolean;
import mx.com.ga.cosmonaut.common.dto.consultas.ResultadoRepetidos;
import mx.com.ga.cosmonaut.common.entity.catalogo.negocio.CatSumaHorasJornada;
import mx.com.ga.cosmonaut.common.entity.catalogo.sat.CsTipoJornada;
import mx.com.ga.cosmonaut.common.entity.cliente.NclGrupoNomina;
import mx.com.ga.cosmonaut.common.entity.cliente.NclHorarioJornada;
import mx.com.ga.cosmonaut.common.entity.cliente.NclJornada;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.common.interceptor.BitacoraUsuario;
import mx.com.ga.cosmonaut.common.repository.catalogo.negocio.CatSumaHorasJornadaRepository;
import mx.com.ga.cosmonaut.common.repository.cliente.NclHorarioJornadaRepository;
import mx.com.ga.cosmonaut.common.repository.cliente.NclJornadaRepository;
import mx.com.ga.cosmonaut.common.repository.colaborador.NcoContratoColaboradorRepository;
import mx.com.ga.cosmonaut.common.repository.nativo.JornadasRepository;
import mx.com.ga.cosmonaut.common.util.Constantes;
import mx.com.ga.cosmonaut.common.util.ObjetoMapper;
import mx.com.ga.cosmonaut.common.util.Utilidades;
import mx.com.ga.cosmonaut.empresa.services.JornadasService;

@Singleton
public class JornadaServiceImpl implements JornadasService {

    @Inject
    private JornadasRepository jornadasRepository;

    @Inject
    private NclJornadaRepository nclJornadaRepository;

    @Inject
    private NclHorarioJornadaRepository nclHorarioJornadaRepository;

    @Inject
    private NcoContratoColaboradorRepository ncoContratoColaboradorRepository;

    @Inject
    private CatSumaHorasJornadaRepository catSumaHorasJornadaRepository;

    @Override
    public RespuestaGenerica consultaEmpleadosJornadaEmpresa(Integer idCliente, Integer idJornada) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            respuesta.setDatos(jornadasRepository.consultaEmpleadosJornadaEmpresa(idCliente, idJornada));
            respuesta.setMensaje(Constantes.EXITO);
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " consultaEmpleadosJornadaEmpresa " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    public RespuestaGenerica consultaJornadasEmpresa(Integer idCliente, Integer idJornada) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            JornadasDto jornadaDto = new JornadasDto();
            List<Jornada> jornadasEmpresa = jornadasRepository.consultaJornadaXEmpresa(idCliente, idJornada);
            if (jornadasEmpresa != null && !jornadasEmpresa.isEmpty()) {
                jornadaDto.setJornadaId(jornadasEmpresa.get(0).getJornadaId());
                jornadaDto.setNombre(jornadasEmpresa.get(0).getNombre());
                jornadaDto.setSumaHorasJornadaId(new CatSumaHorasJornada());
                jornadaDto.getSumaHorasJornadaId().setSumaHorasJornadaId(jornadasEmpresa.get(0).getSumaHorasJornadaId());
                jornadaDto = obtieneCatSumahoras(jornadaDto);
                jornadaDto.setTipoJornadaId(new CsTipoJornada());
                jornadaDto.getTipoJornadaId().setTipoJornadaId(jornadasEmpresa.get(0).getTipoJornadaId());
                jornadaDto.getTipoJornadaId().setDescripcion(jornadasEmpresa.get(0).getDescripcion());
                jornadaDto.setCount(jornadasEmpresa.get(0).getCount());
                jornadaDto = buscaHorariosJornada(jornadaDto);
            }
            respuesta.setDatos(jornadaDto);
            respuesta.setMensaje(Constantes.EXITO);
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            return respuesta;

        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " consultaJornadasEmpresa " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private JornadasDto obtieneCatSumahoras(JornadasDto jornadasDto) throws ServiceException {
        try {
            CatSumaHorasJornada catSumaHorasJornada = catSumaHorasJornadaRepository.findById(jornadasDto.getSumaHorasJornadaId().getSumaHorasJornadaId()).orElse(null);
            if (catSumaHorasJornada != null) {
                jornadasDto.setSumaHorasJornadaId(catSumaHorasJornada);
            }
            return jornadasDto;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " obtieneCatSumahoras " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    public JornadasDto buscaHorariosJornada(JornadasDto jornadaDto) throws ServiceException {
        try {
            List<NclHorarioJornada> horariosJornada = nclHorarioJornadaRepository.findByNclJornadaJornadaId(jornadaDto.getJornadaId());
            if (horariosJornada != null && !horariosJornada.isEmpty()) {
                jornadaDto.setNclHorarioJornada(ObjetoMapper.mapAll(horariosJornada, HorarioJornadaDto.class));
            }
            return jornadaDto;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " buscaHorariosJornada " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    public RespuestaGenerica obtieneJornadasXEmpresa(Integer idCliente) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            List<Jornada> listaRespuesta = new ArrayList<>();
            List<Jornada> lista = jornadasRepository.obtieneJornadasXEmpresa(idCliente);
            lista.forEach(jornada -> {
                try {
                    List<EmpleadosXJornada> listaEmpleados =
                            jornadasRepository.consultaEmpleadosJornadaEmpresa(idCliente, jornada.getJornadaId());
                    Jornada jornadaEmpleado = jornada;
                    jornadaEmpleado.setCount(listaEmpleados.size());
                    listaRespuesta.add(jornadaEmpleado);
                } catch (ServiceException e) {
                    e.printStackTrace();
                }
            });
            respuesta.setDatos(listaRespuesta);
            respuesta.setMensaje(Constantes.EXITO);
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " obtieneJornadasXEmpresa " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    public RespuestaGenerica guardaJornada(NclJornada nclJornada) throws ServiceException {
        RespuestaGenerica respuesta = new RespuestaGenerica();
        try {
            respuesta.setDatos(ObjetoMapper.map(nclJornadaRepository.save(nclJornada), JornadasDto.class));
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            respuesta.setMensaje(Constantes.EXITO);
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + "guardaJornada " + Constantes.ERROR_EXCEPCION, e);
        }
    }


    @Override
    public RespuestaGenerica guardar(JornadasDto jornadasDto) throws ServiceException {
        ResultadoRepetidos resultadoRepetidos = new ResultadoRepetidos();
    try {
        resultadoRepetidos=jornadasRepository.obtieneJornadasXEmpresaRepetidos(jornadasDto.getCentrocClienteId().getCentrocClienteId(),jornadasDto.getNombre().trim());
    } catch (Exception e){
        throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                + Constantes.ERROR_METODO + "jornadas " + Constantes.ERROR_EXCEPCION, e);
    }

    if (resultadoRepetidos.getResultado()==0) {
        try {
            NclJornada nclJornada = parseaDTO(jornadasDto);
            RespuestaGenerica respuesta = validarCamposObligatorios(nclJornada);
            if (respuesta.isResultado()) {
                respuesta = guardaTipoJornada(nclJornada, jornadasDto);
            }
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + "guardar " + Constantes.ERROR_EXCEPCION, e);
        }
    }
    else {
        throw new ServiceException("Jornada duplicada");
    }
    }

    
    public RespuestaGenerica guardaTipoJornada(NclJornada nclJornada, JornadasDto jornadasDto) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            if (nclJornada.getMismoHorario().equals(Constantes.ESTATUS_INACTIVO) && nclJornada.getHorarioComida().equals(Constantes.ESTATUS_ACTIVO)) {
                if  ((!jornadasDto.getHoraInicioComida().isEmpty()) && !jornadasDto.getHoraFinComida().isEmpty())
                {
                    nclJornada.setEsActivo(Constantes.ESTATUS_ACTIVO);
                    nclJornada.setHoraInicioComida(Utilidades.covierteHorario(jornadasDto.getHoraInicioComida()));
                    nclJornada.setHoraFinComida(Utilidades.covierteHorario(jornadasDto.getHoraFinComida()));
                    respuesta = guardaJornada(nclJornada);
                    if (respuesta.isResultado()) {
                        respuesta = guardaHorarios(jornadasDto, respuesta);
                    }
                }else{
                    nclJornada.setMismoHorario(false);
                    nclJornada.setHorarioComida(false);
                }
            }
            if (nclJornada.getMismoHorario().equals(Constantes.ESTATUS_INACTIVO)
                    && nclJornada.getHorarioComida().equals(Constantes.ESTATUS_INACTIVO)) {
                nclJornada.setEsActivo(Constantes.ESTATUS_ACTIVO);
                respuesta = guardaJornada(nclJornada);
                if (respuesta.isResultado()) {
                    respuesta = guardaHorarioComida(jornadasDto, respuesta);
                }
            }
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + "guardaTipoJornada " + Constantes.ERROR_EXCEPCION, e);
        }

    }

    public RespuestaGenerica guardaHorarios(JornadasDto jornadasDto, RespuestaGenerica respuesta) throws ServiceException {
        try {
            if (jornadasDto.getNclHorarioJornada() != null && !jornadasDto.getNclHorarioJornada().isEmpty()) {
                JornadasDto jornadaDto = (JornadasDto) respuesta.getDatos();
                for (HorarioJornadaDto horarioJornada : jornadasDto.getNclHorarioJornada()) {
                    NclHorarioJornada horario = parseaHorarioDTO(horarioJornada);
                    horario.setEsActivo(horarioJornada.getEsActivo());
                    if (jornadaDto.getMismoHorario() != null && jornadaDto.getMismoHorario().equals(Constantes.ESTATUS_ACTIVO)) {
                        horario.setEsActivo(Constantes.ESTATUS_INACTIVO);
                    }
                    horario.setNclJornada(new NclJornada());
                    horario.getNclJornada().setJornadaId(jornadaDto.getJornadaId());
                    horario.setTipoJornadaId(new CsTipoJornada());
                    horario.getTipoJornadaId().setTipoJornadaId(jornadasDto.getTipoJornadaId().getTipoJornadaId());
                    nclHorarioJornadaRepository.save(horario);
                }
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
                respuesta.setMensaje(Constantes.EXITO);

            } else {
                respuesta.setMensaje(Constantes.HORARIO_COMIDA);
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
            }
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + "guardaHorarios " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    public RespuestaGenerica guardaHorarioComida(JornadasDto jornadasDto, RespuestaGenerica respuesta) throws ServiceException {
        try {
            NclHorarioJornada horario = new NclHorarioJornada();
            if (jornadasDto.getNclHorarioJornada() != null && !jornadasDto.getNclHorarioJornada().isEmpty()) {
                JornadasDto jornadaDto = (JornadasDto) respuesta.getDatos();
                for (HorarioJornadaDto horarioJornada : jornadasDto.getNclHorarioJornada()) {
                    horario.setDia(horarioJornada.getDia());
                    horario.setHoraEntrada(Utilidades.covierteHorario(horarioJornada.getHoraEntrada()));
                    horario.setHoraSalida(Utilidades.covierteHorario(horarioJornada.getHoraSalida()));
                    horario.setNclJornada(horarioJornada.getNclJornada());
                    horario.setTipoJornadaId(horarioJornada.getTipoJornadaId());
                    horario.setEsActivo(horarioJornada.getEsActivo());
                    if (jornadaDto.getMismoHorario() != null && jornadaDto.getMismoHorario().equals(Constantes.ESTATUS_ACTIVO)) {
                        horario.setEsActivo(Constantes.ESTATUS_INACTIVO);
                    }
                    horario.setNclJornada(new NclJornada());
                    horario.getNclJornada().setJornadaId(jornadaDto.getJornadaId());
                    horario.setTipoJornadaId(new CsTipoJornada());
                    horario.getTipoJornadaId().setTipoJornadaId(jornadasDto.getTipoJornadaId().getTipoJornadaId());
                    nclHorarioJornadaRepository.save(horario);
                }
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
                respuesta.setMensaje(Constantes.EXITO);

            } else {
                respuesta.setMensaje(Constantes.HORARIO_COMIDA);
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
            }
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + "guardaHorarioComida " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    
    public RespuestaGenerica modificarJornada(NclJornada nclJornada) throws ServiceException {
        RespuestaGenerica respuesta = new RespuestaGenerica();
        try {
            respuesta.setDatos(ObjetoMapper.map(nclJornadaRepository.update(nclJornada), JornadasDto.class));
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            respuesta.setMensaje(Constantes.EXITO);
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + "modificarJornada " + Constantes.ERROR_EXCEPCION, e);
        }
    }


    @Override
    public RespuestaGenerica modificar(JornadasDto jornadasDto) throws ServiceException {
        boolean editable;

        try {
            List<NclJornada> nclJornada =  nclJornadaRepository.CentrocClienteIdCentrocClienteIdAndNombreAndEsActivoEditar(jornadasDto.getCentrocClienteId().getCentrocClienteId(),jornadasDto.getJornadaId());
            editable= nclJornada.get(0).getNombre().toUpperCase().equals(jornadasDto.getNombre().toUpperCase().trim() );
            if (editable){
                editable = true;
            }else {
               List<NclJornada> resultadoRepetidos =nclJornadaRepository.centrocClienteIdCentrocClienteIdAndNombreAndEsActivoRepetidos(jornadasDto.getCentrocClienteId().getCentrocClienteId(),jornadasDto.getNombre().toUpperCase().trim());
                if (resultadoRepetidos.size()==0)
                    editable = true;
                else
                    editable =false;
            }

        } catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + "jornadas " + Constantes.ERROR_EXCEPCION, e);
        }

        if (editable) {
            try {
                RespuestaGenerica respuesta = validarCamposObligatorios(jornadasDto);
                if (respuesta.isResultado()) {
                    NclJornada nclJornada = parseaDTO(jornadasDto);
                    respuesta = modificarTipoJornada(nclJornada, jornadasDto);
                    if (respuesta.isResultado()) {
                        respuesta = consultaJornadasEmpresa(jornadasDto.getCentrocClienteId().getCentrocClienteId(), jornadasDto.getJornadaId());
                    }
                }
                return respuesta;
            } catch (Exception e) {
                throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                        + Constantes.ERROR_METODO + "modificar " + Constantes.ERROR_EXCEPCION, e);
            }
        }
        else {
            throw new ServiceException("Jornada duplicada");
        }
    }

    public RespuestaGenerica modificarTipoJornada(NclJornada nclJornada, JornadasDto jornadasDto) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            if (nclJornada.getMismoHorario().equals(Constantes.ESTATUS_INACTIVO) && nclJornada.getHorarioComida().equals(Constantes.ESTATUS_ACTIVO)) {
                if (!jornadasDto.getHoraInicioComida().isEmpty() && !jornadasDto.getHoraFinComida().isEmpty()) {
                    nclJornada.setEsActivo(Constantes.ESTATUS_ACTIVO);
                    nclJornada.setHoraInicioComida(Utilidades.covierteHorario(jornadasDto.getHoraInicioComida()));
                    nclJornada.setHoraFinComida(Utilidades.covierteHorario(jornadasDto.getHoraFinComida()));
                    respuesta = modificarJornada(nclJornada);
                    if (respuesta.isResultado()) {
                        respuesta = modificarHorarios(jornadasDto, respuesta);
                    }
                }else{
                    nclJornada.setMismoHorario(false);
                    nclJornada.setHorarioComida(false);
                }
            }
            if (nclJornada.getMismoHorario().equals(Constantes.ESTATUS_INACTIVO)
                    && nclJornada.getHorarioComida().equals(Constantes.ESTATUS_INACTIVO)) {
                nclJornada.setEsActivo(Constantes.ESTATUS_ACTIVO);
                respuesta = modificarJornada(nclJornada);
                if (respuesta.isResultado()) {
                    respuesta = modificarHorarioComida(jornadasDto, respuesta);
                }
            }
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + "modificarTipoJornada " + Constantes.ERROR_EXCEPCION, e);
        }

    }

    public RespuestaGenerica modificarHorarios(JornadasDto jornadasDto, RespuestaGenerica respuesta) throws ServiceException {
        try {
            if (jornadasDto.getNclHorarioJornada() != null && !jornadasDto.getNclHorarioJornada().isEmpty()) {
                JornadasDto jornadaDto = (JornadasDto) respuesta.getDatos();
                for (HorarioJornadaDto horarioJornada : jornadasDto.getNclHorarioJornada()) {
                    NclHorarioJornada horario = parseaHorarioDTO(horarioJornada);
                    horario.setEsActivo(horarioJornada.getEsActivo());
                    if (jornadaDto.getMismoHorario() != null && jornadaDto.getMismoHorario().equals(Constantes.ESTATUS_ACTIVO)) {
                        horario.setEsActivo(Constantes.ESTATUS_INACTIVO);
                    }
                    horario.setNclJornada(new NclJornada());
                    horario.getNclJornada().setJornadaId(jornadaDto.getJornadaId());
                    horario.setTipoJornadaId(new CsTipoJornada());
                    horario.getTipoJornadaId().setTipoJornadaId(jornadasDto.getTipoJornadaId().getTipoJornadaId());
                    nclHorarioJornadaRepository.update(horario);
                }
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
                respuesta.setMensaje(Constantes.EXITO);

            } else {
                respuesta.setMensaje(Constantes.HORARIO_COMIDA);
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
            }
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + "modificarHorarios " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    public RespuestaGenerica modificarHorarioComida(JornadasDto jornadasDto, RespuestaGenerica respuesta) throws ServiceException {
        try {
            NclHorarioJornada horario = new NclHorarioJornada();
            if (jornadasDto.getNclHorarioJornada() != null && !jornadasDto.getNclHorarioJornada().isEmpty()) {
                JornadasDto jornadaDto = (JornadasDto) respuesta.getDatos();
                for (HorarioJornadaDto horarioJornada : jornadasDto.getNclHorarioJornada()) {
                    horario.setHorarioJornadaId(horarioJornada.getHorarioJornadaId());
                    horario.setDia(horarioJornada.getDia());
                    horario.setHoraEntrada(Utilidades.covierteHorario(horarioJornada.getHoraEntrada()));
                    horario.setHoraSalida(Utilidades.covierteHorario(horarioJornada.getHoraSalida()));
                    horario.setNclJornada(horarioJornada.getNclJornada());
                    horario.setTipoJornadaId(horarioJornada.getTipoJornadaId());
                    horario.setEsActivo(horarioJornada.getEsActivo());
                    if (jornadaDto.getMismoHorario() != null && jornadaDto.getMismoHorario().equals(Constantes.ESTATUS_ACTIVO)) {
                        horario.setEsActivo(Constantes.ESTATUS_INACTIVO);
                    }
                    horario.setNclJornada(new NclJornada());
                    horario.getNclJornada().setJornadaId(jornadaDto.getJornadaId());
                    horario.setTipoJornadaId(new CsTipoJornada());
                    horario.getTipoJornadaId().setTipoJornadaId(jornadasDto.getTipoJornadaId().getTipoJornadaId());
                    nclHorarioJornadaRepository.update(horario);
                }
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
                respuesta.setMensaje(Constantes.EXITO);

            } else {
                respuesta.setMensaje(Constantes.HORARIO_COMIDA);
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
            }
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + "modificarHorarioComida " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    public RespuestaGenerica duplicarJornada(Integer idJornada) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            NclJornada jornada = nclJornadaRepository.findByJornadaId(idJornada);
            if (jornada != null) {
                jornada.setEsActivo(Constantes.ESTATUS_ACTIVO);
                jornada.setJornadaId(null);
                respuesta = guardaJornada(jornada);
                if (respuesta.isResultado()) {
                    List<NclHorarioJornada> horarios = nclHorarioJornadaRepository.findByNclJornadaJornadaId(idJornada);
                    if (!horarios.isEmpty()) {
                        for (NclHorarioJornada horario : horarios) {
                            horario.setEsActivo(Constantes.ESTATUS_ACTIVO);
                            horario.setHorarioJornadaId(null);
                            horario.setNclJornada(new NclJornada());
                            horario.setTipoJornadaId(new CsTipoJornada());
                            horario.getNclJornada().setJornadaId(((JornadasDto) respuesta.getDatos()).getJornadaId());
                            horario.setTipoJornadaId(((JornadasDto) respuesta.getDatos()).getTipoJornadaId());
                            nclHorarioJornadaRepository.save(horario);
                        }
                    }
                    respuesta.setMensaje(Constantes.EXITO);
                    respuesta.setResultado(Constantes.RESULTADO_EXITO);
                } else {
                    respuesta.setMensaje(Constantes.JORNADA_ERROR);
                    respuesta.setResultado(Constantes.RESULTADO_ERROR);
                }
            } else {
                respuesta.setMensaje(Constantes.JORNADA_NOEXISTE);
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
            }
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + "duplicarJornada " + Constantes.ERROR_EXCEPCION, e);
        }

    }

    public RespuestaGenerica validarCamposObligatorios(JornadasDto nclJornada) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            if (nclJornada.getNombre() == null
                    || nclJornada.getNombre().isEmpty()
                    || nclJornada.getTipoJornadaId() == null
                    || nclJornada.getMismoHorario() == null
                    || nclJornada.getHorarioComida() == null
                    || nclJornada.getHoraEntrada() == null
                    || nclJornada.getCentrocClienteId() == null
                    || nclJornada.getCentrocClienteId().getCentrocClienteId() == null) {
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(Constantes.CAMPOS_REQUERIDOS);
            } else {
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
                respuesta.setMensaje(Constantes.EXITO);
            }
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + "validaCamposObligatorios " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    public RespuestaGenerica validarCamposObligatorios(NclJornada nclJornada) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            if (nclJornada.getNombre() == null
                    || nclJornada.getNombre().isEmpty()
                    || nclJornada.getTipoJornadaId() == null
                    || nclJornada.getMismoHorario() == null
                    || nclJornada.getHorarioComida() == null
                    || nclJornada.getCentrocClienteId() == null
                    || nclJornada.getCentrocClienteId().getCentrocClienteId() == null) {
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(Constantes.CAMPOS_REQUERIDOS);
            } else {
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
                respuesta.setMensaje(Constantes.EXITO);
            }
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + "validaCamposObligatorios " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    public NclJornada parseaDTO(JornadasDto jornadasDto) throws ServiceException {
        NclJornada nclJornada = new NclJornada();
        try {
            nclJornada.setJornadaId(jornadasDto.getJornadaId());
            nclJornada.setNombre(jornadasDto.getNombre());
            nclJornada.setTipoJornadaId(jornadasDto.getTipoJornadaId());
            nclJornada.setMismoHorario(jornadasDto.getMismoHorario());
            nclJornada.setHorarioComida(jornadasDto.getHorarioComida());
            nclJornada.setIncidirAsistencias(jornadasDto.getIncidirAsistencias());
            nclJornada.setSumaHorasJornadaId(jornadasDto.getSumaHorasJornadaId());
            nclJornada.setHoraEntrada(Utilidades.covierteHorario(jornadasDto.getHoraEntrada()));
            nclJornada.setHoraSalida(Utilidades.covierteHorario(jornadasDto.getHoraSalida()));
            nclJornada.setCentrocClienteId(jornadasDto.getCentrocClienteId());
            nclJornada.setAnMinutosTolerancia(jornadasDto.getAnMinutosTolerancia());
            nclJornada.setAnHayTolerancia(jornadasDto.getAnHayTolerancia());
            nclJornada.setRegistroPrimaDominicalAuto(jornadasDto.getRegistroPrimaDominicalAuto());
            nclJornada.setRegistroDescansoLaboralAuto(jornadasDto.getRegistroDescansoLaboralAuto());
            nclJornada.setAnPermiteJustificarRetardo(jornadasDto.getAnPermiteJustificarRetardo());
            nclJornada.setSoloUnRegistroDia(jornadasDto.getSoloUnRegistroDia());
            nclJornada.setParaDiasParcialmenteLaborados(jornadasDto.getParaDiasParcialmenteLaborados());
            nclJornada.setHeSolicitudHorasExtra(jornadasDto.getHeSolicitudHorasExtra());
            nclJornada.setHeMinutos(jornadasDto.getHeMinutos());
            return nclJornada;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + "parseaDTO" + Constantes.ERROR_EXCEPCION, e);
        }
    }

    public NclHorarioJornada parseaHorarioDTO(HorarioJornadaDto horarioJornadaDto) throws ServiceException {
        NclHorarioJornada nclHorarioJornada = new NclHorarioJornada();
        try {
            nclHorarioJornada.setHorarioJornadaId(horarioJornadaDto.getHorarioJornadaId());
            nclHorarioJornada.setDia(horarioJornadaDto.getDia());
            nclHorarioJornada.setHoraEntrada(Utilidades.covierteHorario(horarioJornadaDto.getHoraEntrada()));
            nclHorarioJornada.setHoraInicioComida(Utilidades.covierteHorario(horarioJornadaDto.getHoraInicioComida()));
            nclHorarioJornada.setHoraFinComida(Utilidades.covierteHorario(horarioJornadaDto.getHoraFinComida()));
            nclHorarioJornada.setHoraSalida(Utilidades.covierteHorario(horarioJornadaDto.getHoraSalida()));
            nclHorarioJornada.setNclJornada(horarioJornadaDto.getNclJornada());
            nclHorarioJornada.setTipoJornadaId(horarioJornadaDto.getTipoJornadaId());
            return nclHorarioJornada;

        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + "parseaHorarioDTO" + Constantes.ERROR_EXCEPCION, e);
        }

    }


    @Override
    public RespuestaGenerica eliminar(NclJornada nclJornada) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            if (nclJornada.getJornadaId() != null && nclJornada.getCentrocClienteId() != null) {
                Integer count = ncoContratoColaboradorRepository.findJornadaXColaborador(nclJornada.getCentrocClienteId().getCentrocClienteId(), nclJornada.getJornadaId());
                if (count.equals(Constantes.CERO)) {
                    nclJornadaRepository.update(nclJornada.getJornadaId(), Constantes.ESTATUS_INACTIVO);
                    List<NclHorarioJornada> horarios = nclHorarioJornadaRepository.findByNclJornadaJornadaId(nclJornada.getJornadaId());
                    if (!horarios.isEmpty()) {
                        for (NclHorarioJornada horario : horarios) {
                            nclHorarioJornadaRepository.update(horario.getHorarioJornadaId(), Constantes.ESTATUS_INACTIVO);
                        }
                    }
                    respuesta.setMensaje(Constantes.EXITO);
                    respuesta.setResultado(Constantes.RESULTADO_EXITO);
                } else {
                    return new RespuestaGenerica(null, Constantes.RESULTADO_ERROR, Constantes.JORNADA_REL + count + Constantes.JORNADA_RELACIONADOS);
                }

            } else {
                respuesta.setMensaje(Constantes.JORNADA_REQ);
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
            }
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " eliminar " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica findByActivo(Boolean activo) throws ServiceException {
        RespuestaGenerica respuestaGenerica = new RespuestaGenerica();
        try {
            respuestaGenerica.setDatos(nclJornadaRepository.findByEsActivoOrderByNombre(activo));
            respuestaGenerica.setResultado(Constantes.RESULTADO_EXITO);
            respuestaGenerica.setMensaje(Constantes.EXITO);
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" findByEsActivo" + Constantes.ERROR_EXCEPCION, e);
        }
        return respuestaGenerica;
    }

}
