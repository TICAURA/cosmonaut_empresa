package mx.com.ga.cosmonaut.empresa.services;

import mx.com.ga.cosmonaut.common.dto.NcoPersonaDto;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.entity.colaborador.NcoPersona;
import mx.com.ga.cosmonaut.common.exception.ServiceException;

import java.util.List;

public interface PersonaService {

    RespuestaGenerica guardarRepresentanteLegal(NcoPersonaDto ncoPersonaDto) throws ServiceException;

    RespuestaGenerica guardarContactoRH(NcoPersonaDto personaDto) throws ServiceException;

    RespuestaGenerica guardarContactoInicial(NcoPersonaDto personaDto) throws ServiceException;

    RespuestaGenerica guardarApoderadoLegal(NcoPersonaDto personaDto) throws ServiceException;

    RespuestaGenerica modificarContactoInicial(NcoPersonaDto personaDto) throws ServiceException;

    RespuestaGenerica modificarContactoRH(NcoPersonaDto personaDto) throws ServiceException;

    RespuestaGenerica modificarRepresentanteLegal(NcoPersonaDto personaDto) throws ServiceException;

    RespuestaGenerica modificarApoderadoLegal(NcoPersonaDto personaDto) throws ServiceException;

    RespuestaGenerica obtenerId(Long id) throws ServiceException;

    RespuestaGenerica obtenerIdCompania(Long id) throws ServiceException;

    RespuestaGenerica obtenerDetalleEventos(Long id) throws ServiceException;

    RespuestaGenerica listarTodos(Integer tipoPersona) throws ServiceException;

    RespuestaGenerica listarCompaniaPersona(NcoPersonaDto ncoPersonaDto) throws ServiceException;

    RespuestaGenerica listaEmpleadoIncompleto(Long id) throws ServiceException;

    RespuestaGenerica listarDinamica(NcoPersonaDto ncoPersonaDto) throws ServiceException;

    RespuestaGenerica modificarLista(List<NcoPersona> listaNcoPersonaDto) throws ServiceException;

    RespuestaGenerica eliminar(Long id) throws ServiceException;

    RespuestaGenerica validarFechaFinPago(Integer personaId) throws ServiceException;

    RespuestaGenerica obtenerEmailCorporativoCentrocClienteId(String correo, Long centroclienteId)
            throws ServiceException;

}
