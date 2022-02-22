package mx.com.ga.cosmonaut.empresa.services.impl;

import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.repository.administracion.NmmConfiguraDeduccionXdocumentoRepository;
import mx.com.ga.cosmonaut.common.service.DocumentosEmpleadoService;
import mx.com.ga.cosmonaut.empresa.services.DocumentosService;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DocumentosServiceImpl implements DocumentosService {

    @Inject
    private NmmConfiguraDeduccionXdocumentoRepository configuraDeduccionXdocumentoRepository;

    @Inject
    private DocumentosEmpleadoService documentosEmpleadoService;

    @Override
    public RespuestaGenerica eliminar(Integer documentoId) throws Exception {
        // LOGICA ESPECIAL POR RELACION ENTRE DOC EMPLEADO E INFONAVIT
        configuraDeduccionXdocumentoRepository.updateSetNullByDocumentoSuspensionId(documentoId);
        configuraDeduccionXdocumentoRepository.updateSetNullByDocumentoRetencionId(documentoId);

        return documentosEmpleadoService.eliminar(documentoId);
    }

}
