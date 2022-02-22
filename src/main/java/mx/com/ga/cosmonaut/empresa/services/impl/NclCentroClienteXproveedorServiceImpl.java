package mx.com.ga.cosmonaut.empresa.services.impl;

import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.dto.cliente.FiltrarReponse;
import mx.com.ga.cosmonaut.common.dto.cliente.FiltrarRequest;
import mx.com.ga.cosmonaut.common.dto.cliente.GuardarRequest;
import mx.com.ga.cosmonaut.common.dto.cliente.ModificarRequest;
import mx.com.ga.cosmonaut.common.entity.catalogo.negocio.CatProveedorDispersion;
import mx.com.ga.cosmonaut.common.entity.catalogo.negocio.CatProveedorTimbrado;
import mx.com.ga.cosmonaut.common.entity.cliente.NclCentrocCliente;
import mx.com.ga.cosmonaut.common.entity.cliente.NclCentrocClienteXproveedor;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.common.repository.catalogo.negocio.CatProveedorDispersionRepository;
import mx.com.ga.cosmonaut.common.repository.catalogo.negocio.CatProveedorTimbradoRepository;
import mx.com.ga.cosmonaut.common.repository.cliente.NclCentrocClienteRepository;
import mx.com.ga.cosmonaut.common.repository.cliente.NclCentrocClienteXproveedorRepository;
import mx.com.ga.cosmonaut.common.repository.nativo.CentrocClienteXproveedorRepository;
import mx.com.ga.cosmonaut.common.util.Constantes;
import mx.com.ga.cosmonaut.empresa.services.NclCentroClienteXproveedorService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.stream.Collectors;

@Singleton
public class NclCentroClienteXproveedorServiceImpl implements NclCentroClienteXproveedorService {

    @Inject
    private NclCentrocClienteXproveedorRepository nclCentrocClienteXproveedorRepository;

    @Inject
    private CentrocClienteXproveedorRepository centrocClienteXproveedorRepository;

    @Inject
    private NclCentrocClienteRepository nclCentrocClienteRepository;

    @Inject
    private CatProveedorDispersionRepository catProveedorDispersionRepository;

    @Inject
    private CatProveedorTimbradoRepository catProveedorTimbradoRepository;

    @Override
    public RespuestaGenerica guardar(GuardarRequest request) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();

            Optional<NclCentrocCliente> nclCentrocCliente = nclCentrocClienteRepository
                    .findById(request.getClienteId());
            if (nclCentrocCliente.isPresent()) {
                NclCentrocClienteXproveedor nclCentrocClienteXproveedor = new NclCentrocClienteXproveedor();
                nclCentrocClienteXproveedor.setCentrocClienteId(nclCentrocCliente.get());

                if (request.getDispersionId() != null && request.getTimbradoId() != null) {
                    Optional<CatProveedorDispersion> catProveedorDispersion = catProveedorDispersionRepository
                            .findById(request.getDispersionId());
                    Optional<CatProveedorTimbrado> catProveedorTimbrado = catProveedorTimbradoRepository
                            .findById(request.getTimbradoId());
                    if (catProveedorDispersion.isPresent() && catProveedorTimbrado.isPresent()) {
                        nclCentrocClienteXproveedor.setProveedorDispersionId(catProveedorDispersion.get());
                        nclCentrocClienteXproveedor.setProveedorTimbradoId(catProveedorTimbrado.get());
                    } else {
                        respuesta.setResultado(Constantes.RESULTADO_ERROR);
                        respuesta.setMensaje(Constantes.ERROR_PROVEEDOR_NO_EXISTE);
                    }
                }

                respuesta.setDatos(nclCentrocClienteXproveedorRepository.save(nclCentrocClienteXproveedor));
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
                respuesta.setMensaje(Constantes.EXITO);
            } else {
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(Constantes.ERROR_CLIENTE_NO_EXISTE);
            }
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" guardar " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica modificar(ModificarRequest request) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();

            Optional<NclCentrocClienteXproveedor> nclCentrocClienteXproveedorOp = nclCentrocClienteXproveedorRepository
                    .findById(request.getClienteXproveedorId());
            Optional<NclCentrocCliente> nclCentrocCliente = nclCentrocClienteRepository
                    .findById(request.getClienteId());
            Optional<CatProveedorDispersion> catProveedorDispersion = catProveedorDispersionRepository
                    .findById(request.getDispersionId());
            Optional<CatProveedorTimbrado> catProveedorTimbrado = catProveedorTimbradoRepository
                    .findById(request.getTimbradoId());
            if (nclCentrocClienteXproveedorOp.isPresent() && nclCentrocCliente.isPresent()
                    && catProveedorDispersion.isPresent() && catProveedorTimbrado.isPresent()) {
                NclCentrocClienteXproveedor nclCentrocClienteXproveedor = nclCentrocClienteXproveedorOp.get();
                nclCentrocClienteXproveedor.setCentrocClienteId(nclCentrocCliente.get());
                nclCentrocClienteXproveedor.setProveedorDispersionId(catProveedorDispersion.get());
                nclCentrocClienteXproveedor.setProveedorTimbradoId(catProveedorTimbrado.get());

                respuesta.setDatos(nclCentrocClienteXproveedorRepository.update(nclCentrocClienteXproveedor));
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
                respuesta.setMensaje(Constantes.EXITO);
            } else {
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(Constantes.ERROR_CLIENTE_NO_EXISTE);
            }
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" modificar " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica listar() throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            respuesta.setDatos(nclCentrocClienteXproveedorRepository.findAll());
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            respuesta.setMensaje(Constantes.EXITO);
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" listar " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica obtener(Integer id) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            nclCentrocClienteXproveedorRepository.findById(id).ifPresent(respuesta::setDatos);
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            respuesta.setMensaje(Constantes.EXITO);
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" obtener " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica filtrar(FiltrarRequest request) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();

            /*if (request.getEmpresaId() != null) {
                respuesta.setDatos(Collections.singleton(nclCentrocClienteXproveedorRepository
                        .findByCentrocClienteIdCentrocClienteId(request.getEmpresaId())));
            } else if (request.getClienteId() != null) {
                Set<NclCentrocCliente> nclCentrocClientes = new HashSet<>(nclCentrocClienteRepository
                        .findByCentroCostosCentrocClienteIdCentrocClienteId(request.getClienteId()));
                respuesta.setDatos(nclCentrocClienteXproveedorRepository.findByCentrocClienteIdIn(nclCentrocClientes));
            } else {
                respuesta.setDatos(nclCentrocClienteXproveedorRepository.findAll());
            }*/

            respuesta.setDatos(centrocClienteXproveedorRepository.filtrar(request));
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            respuesta.setMensaje(Constantes.EXITO);
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" filtrar " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica filtrarPaginado(FiltrarRequest request, Integer numeroRegistros, Integer pagina) throws ServiceException {
        try {
            Map<String, Object> repuesta = new HashMap<>();
            List<FiltrarReponse> lista = centrocClienteXproveedorRepository.filtrarPaginado(request,numeroRegistros,pagina);
            List<FiltrarReponse> listaCentro = centrocClienteXproveedorRepository.filtrar(request);
            repuesta.put("lista",lista);
            repuesta.put("totalResgistros",listaCentro.size());
            return new RespuestaGenerica(repuesta,Constantes.RESULTADO_EXITO,Constantes.EXITO);
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" filtrar " + Constantes.ERROR_EXCEPCION, e);
        }
    }
}
