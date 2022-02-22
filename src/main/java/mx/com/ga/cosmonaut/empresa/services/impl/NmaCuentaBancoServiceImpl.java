package mx.com.ga.cosmonaut.empresa.services.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import mx.com.ga.cosmonaut.common.dto.CsBancoDto;
import mx.com.ga.cosmonaut.common.dto.NmaCuentaBancoDto;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.common.entity.administracion.NmaCuentaBanco;
import mx.com.ga.cosmonaut.common.entity.catalogo.sat.CsBanco;
import mx.com.ga.cosmonaut.common.util.Constantes;
import mx.com.ga.cosmonaut.common.util.ObjetoMapper;
import mx.com.ga.cosmonaut.common.repository.administracion.NmaCuentaBancoRepository;
import mx.com.ga.cosmonaut.common.repository.catalogo.sat.CsBancoRepository;
import mx.com.ga.cosmonaut.common.repository.cliente.NclCentrocClienteRepository;
import mx.com.ga.cosmonaut.empresa.services.NmaCuentaBancoService;

@Singleton
public class NmaCuentaBancoServiceImpl implements NmaCuentaBancoService {

    @Inject
    private NmaCuentaBancoRepository nmaCuentaBancoRepository;
    @Inject
    private CsBancoRepository csBancoRepository;
    @Inject
    NclCentrocClienteRepository nclCentrocClienteRepository;

    private boolean mismaCuenta;

    @Override
    public RespuestaGenerica findAll() throws ServiceException {

        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            List<NmaCuentaBanco> lista = nmaCuentaBancoRepository.findAll();
            respuesta.setDatos(ObjetoMapper.mapAll(lista, NmaCuentaBancoDto.class));
            respuesta.setMensaje(Constantes.EXITO);
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " findAll " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica obtieneCuentaBancariaPersonaId(Integer personaId) throws ServiceException {

        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            respuesta.setDatos(nmaCuentaBancoRepository.findByNcoPersonaPersonaId(personaId).orElse(null));
            respuesta.setMensaje(Constantes.EXITO);
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " obtieneCuentaBancariaPersonaId " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica obtenerCuentaCliente(Integer idCentrocCliente) throws ServiceException {

        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();

            respuesta.setDatos(nmaCuentaBancoRepository.findByNclCentrocClienteCentrocClienteIdAndUsaStpIsNullOrUsaStp(idCentrocCliente, Constantes.ESTATUS_INACTIVO));
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            respuesta.setMensaje(Constantes.EXITO);
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " obtenerCuentaCliente " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica listarCuentaClienteDinamico(NmaCuentaBanco cuentaBanco) throws ServiceException {
        try {

            List<NmaCuentaBanco> lista =nmaCuentaBancoRepository.
                    findByNumeroCuentaIlikeAndClabeIlikeAndBancoIdNombreCortoIlikeAndNclCentrocClienteCentrocClienteIdAndUsaStpIsNullOrUsaStp(
                            "%" + cuentaBanco.getNumeroCuenta() + "%", "%" + cuentaBanco.getClabe() + "%",
                            "%" + cuentaBanco.getBancoId().getNombreCorto() + "%",
                            cuentaBanco.getNclCentrocCliente().getCentrocClienteId(),Constantes.ESTATUS_INACTIVO);

            if (cuentaBanco.getEsActivo() != null){
                if (cuentaBanco.getEsActivo()){
                    lista = lista.stream().filter(c -> c.getEsActivo()).collect(Collectors.toList());
                }else {
                    lista = lista.stream().filter(c -> !c.getEsActivo()).collect(Collectors.toList());
                }

            }

            return new RespuestaGenerica(lista,Constantes.RESULTADO_EXITO,Constantes.EXITO);
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " listarCuentaClienteDinamico " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica eliminar(Integer idCentrocCliente, Integer personaId) throws ServiceException {

        try {
            nmaCuentaBancoRepository.updateEliminarCLABE(idCentrocCliente,personaId);
            return new RespuestaGenerica(null, Constantes.RESULTADO_EXITO, Constantes.EXITO);
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " eliminar " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    public RespuestaGenerica obtenerCuentaClienteFuncion(Integer idCentrocCliente) throws ServiceException {

        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            respuesta.setDatos(nmaCuentaBancoRepository.findByNclCentrocClienteCentrocClienteIdAndFuncionCuentaIdFuncionCuentaIdIn(idCentrocCliente, Arrays.asList(2,5)));
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            respuesta.setMensaje(Constantes.EXITO);
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " obtenerCuentaCliente " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica obtenerCuentaSTPCliente(Integer idCentrocCliente) throws ServiceException {

        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            respuesta.setDatos(nmaCuentaBancoRepository.findByNclCentrocClienteCentrocClienteIdAndUsaStp(idCentrocCliente, Constantes.ESTATUS_ACTIVO));
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            respuesta.setMensaje(Constantes.EXITO);
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " obtenerCuentaSTPCliente " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica obtieneBanco(String clabe) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            CsBanco banco = csBancoRepository.findByCodBanco(clabe.substring(0, 3)).orElse(null);
            if (banco != null) {
                CsBancoDto bancoDto = ObjetoMapper.map(banco,CsBancoDto.class);
                bancoDto.setSucursal(clabe.substring(3, 6));
                bancoDto.setNumeroCuenta(clabe.substring(6, 16));
                respuesta.setDatos(bancoDto);
                respuesta.setMensaje(Constantes.EXITO);
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
            } else {
                respuesta.setMensaje(Constantes.CUENTA_BANCO_NO_EXISTE);
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
            }

            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " ObtieneBanco " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica findByNumeroCuenta(String numeroCuenta) throws ServiceException {

        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            respuesta.setDatos(ObjetoMapper.map(
                    nmaCuentaBancoRepository.findByNumeroCuenta(numeroCuenta), NmaCuentaBancoDto.class));
            respuesta.setResultado(true);
            respuesta.setMensaje(Constantes.EXITO);
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " findByNumeroCuenta " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    public RespuestaGenerica guardaLista(List<NmaCuentaBancoDto> listaNmaCuentaBancoDto) throws ServiceException {
        try {
            for (NmaCuentaBancoDto dto : listaNmaCuentaBancoDto) {
                this.guardar(dto);
            }
            return findAll();
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " guardaLista " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica guardar(NmaCuentaBancoDto nmaCuentaBancoDto) throws ServiceException {
        try {
            RespuestaGenerica respuesta = validarCamposObligatorios(nmaCuentaBancoDto);
                if (respuesta.isResultado()) {
                    respuesta = validarCuentaClabe(nmaCuentaBancoDto);
                    if (respuesta.isResultado()) {
                        nmaCuentaBancoDto.getBancoId().setRazonSocial((String) respuesta.getDatos());
                        nmaCuentaBancoDto.setEsActivo(Constantes.ESTATUS_ACTIVO);
                        respuesta = new RespuestaGenerica();
                        if(mismaCuenta){
                            if(nmaCuentaBancoDto.getNcoPersona()==null){
                                respuesta.setMensaje(Constantes.CUENTA_BANCARIA_DUPLICADA);
                                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                                return respuesta;
                            }else {
                                respuesta.setDatos(ObjetoMapper.map(
                                        nmaCuentaBancoRepository.update(
                                                ObjetoMapper.map(nmaCuentaBancoDto, NmaCuentaBanco.class)),
                                        NmaCuentaBancoDto.class));
                            }
                        }else {
                            respuesta.setDatos(ObjetoMapper.map(
                                    nmaCuentaBancoRepository.save(
                                            ObjetoMapper.map(nmaCuentaBancoDto, NmaCuentaBanco.class)),
                                    NmaCuentaBancoDto.class));
                        }
                        respuesta.setResultado(Constantes.RESULTADO_EXITO);
                        respuesta.setMensaje(Constantes.EXITO);
                    }
                }
            return respuesta;
        } catch (ServiceException e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " guardar " + Constantes.ERROR_EXCEPCION, e);
        }

    }

    @Override
    public RespuestaGenerica guardarSTP(NmaCuentaBancoDto nmaCuentaBancoDto) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            if (nmaCuentaBancoDto.getNclCentrocCliente().getCentrocClienteId() != null && nmaCuentaBancoDto.getUsaStp().equals(Constantes.ESTATUS_ACTIVO)) {
                if (nmaCuentaBancoRepository.obtieneSTP(nmaCuentaBancoDto.getNclCentrocCliente().getCentrocClienteId()).isEmpty()) {
                    if (nmaCuentaBancoDto.getClabe() == null) {
                        nmaCuentaBancoDto.setClabe(nmaCuentaBancoDto.getClabeStp());
                    }
                    if (nmaCuentaBancoDto.getClabeStp().length() == Constantes.CLABE && nmaCuentaBancoDto.getCuentaStp().length() <= Constantes.CTA_STP) {
                        nmaCuentaBancoDto.setBancoId(new CsBancoDto());
                        nmaCuentaBancoDto.getBancoId().setBancoId(1L);
                        nmaCuentaBancoDto.setEsActivo(Constantes.ESTATUS_ACTIVO);
                        respuesta.setDatos(ObjetoMapper.map(
                                nmaCuentaBancoRepository.save(ObjetoMapper.map(nmaCuentaBancoDto, NmaCuentaBanco.class)), NmaCuentaBancoDto.class));
                        respuesta.setResultado(Constantes.RESULTADO_EXITO);
                        respuesta.setMensaje(Constantes.EXITO);
                    }
                } else {
                    respuesta.setResultado(Constantes.RESULTADO_ERROR);
                    respuesta.setMensaje(Constantes.CUENTA_STP_EXISTE);
                }
            } else {
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(Constantes.ERROR_STP);
            }

            return respuesta;

        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " guardarSTP " + Constantes.ERROR_EXCEPCION, e);
        }

    }

    @Override
    public RespuestaGenerica modificarSTP(NmaCuentaBancoDto nmaCuentaBancoDto) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            if (nmaCuentaBancoDto.getCuentaBancoId() != null && nmaCuentaBancoDto.getNclCentrocCliente().getCentrocClienteId() != null && nmaCuentaBancoDto.getUsaStp().equals(Constantes.ESTATUS_ACTIVO)) {
                if (nmaCuentaBancoDto.getClabe() == null) {
                    nmaCuentaBancoDto.setClabe(nmaCuentaBancoDto.getClabeStp());
                }
                nmaCuentaBancoDto.setEsActivo(Constantes.ESTATUS_ACTIVO);
                nmaCuentaBancoDto.setBancoId(new CsBancoDto());
                nmaCuentaBancoDto.getBancoId().setBancoId(1L);
                respuesta.setDatos(ObjetoMapper.map(
                        nmaCuentaBancoRepository.update(
                                ObjetoMapper.map(nmaCuentaBancoDto,
                                        NmaCuentaBanco.class
                                )), NmaCuentaBancoDto.class
                ));
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
                respuesta.setMensaje(Constantes.EXITO);

            } else {
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(Constantes.CLABE_STP_ID);
            }
            return respuesta;

        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " modificarSTP " + Constantes.ERROR_EXCEPCION, e);
        }

    }

    private RespuestaGenerica validarEstructura(NmaCuentaBancoDto nmaCuentaBancoDto) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            if (validarDuplicateCuentaBanco(nmaCuentaBancoDto)){
                if (validarDuplicateClabeBanco(nmaCuentaBancoDto)){
                    respuesta.setDatos(nmaCuentaBancoDto.getBancoId());
                    respuesta.setMensaje(Constantes.EXITO);
                    respuesta.setResultado(Constantes.RESULTADO_EXITO);
                } else {
                    respuesta.setMensaje(Constantes.CUENTA_BANCARIA_DUPLICADA);
                    respuesta.setResultado(Constantes.RESULTADO_ERROR);
                }
            } else {
                respuesta.setMensaje(Constantes.CUENTA_BANCARIA_DUPLICADA);
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
            }

            if (nmaCuentaBancoDto.getNumeroCuenta().length() < Constantes.CUENTA_BANCARIA){
                respuesta.setMensaje(Constantes.ERROR_NUMERO_CUENTA_BANCO);
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
            }
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " validarEstructura " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private RespuestaGenerica validarCuentaClabe(NmaCuentaBancoDto nmaCuentaBancoDto) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            Optional<CsBanco> banco = csBancoRepository.findById(nmaCuentaBancoDto.getBancoId().getBancoId());
            if (banco.isPresent()) {
                respuesta=validarEstructura(nmaCuentaBancoDto);
                if(respuesta.isResultado()){
                    if (nmaCuentaBancoDto.getClabe().startsWith(banco.get().getCodBanco())) {
                        respuesta.setDatos(banco.get().getRazonSocial());
                        respuesta.setMensaje(Constantes.EXITO);
                        respuesta.setResultado(Constantes.RESULTADO_EXITO);
                    } else {
                        respuesta.setMensaje(Constantes.CUENTA_BANCARIA_BANCO);
                        respuesta.setResultado(Constantes.RESULTADO_ERROR);
                    }
                }else {
                    respuesta.setMensaje(Constantes.CUENTA_BANCARIA_DUPLICADA);
                    respuesta.setResultado(Constantes.RESULTADO_ERROR);
                }
            } else {
                respuesta.setMensaje(Constantes.CUENTA_BANCO_NO_EXISTE);
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
            }

            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " validarCuentaClabe " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private RespuestaGenerica validaCamposObligatoriosPersona(NmaCuentaBancoDto nmaCuentaBancoDto) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            if (nmaCuentaBancoDto.getClabe() == null
                    || nmaCuentaBancoDto.getClabe().length() != Constantes.CLABE
                    || nmaCuentaBancoDto.getNumeroCuenta() == null
                    || nmaCuentaBancoDto.getBancoId() == null
                    || nmaCuentaBancoDto.getBancoId().getBancoId() == null
                    || nmaCuentaBancoDto.getNclCentrocCliente() == null
                    || nmaCuentaBancoDto.getNclCentrocCliente().getCentrocClienteId() == null) {
                respuesta.setMensaje(Constantes.CAMPOS_REQUERIDOS);
                respuesta.setResultado(Constantes.RESULTADO_ERROR);

            } else {
                respuesta.setMensaje(Constantes.EXITO);
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
            }
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " validaCamposObligatoriosPersona " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private RespuestaGenerica validarCamposObligatorios(NmaCuentaBancoDto nmaCuentaBancoDto) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            if (nmaCuentaBancoDto.getNcoPersona() != null && nmaCuentaBancoDto.getNcoPersona().getPersonaId() != null) {
                respuesta = validaCamposObligatoriosPersona(nmaCuentaBancoDto);
            } else {
                if (nmaCuentaBancoDto.getClabe() == null
                        || nmaCuentaBancoDto.getClabe().length() != Constantes.CLABE
                        || nmaCuentaBancoDto.getNombreCuenta() == null
                        || nmaCuentaBancoDto.getNombreCuenta().isEmpty()
                        || nmaCuentaBancoDto.getNumeroCuenta() == null
                        || nmaCuentaBancoDto.getBancoId() == null
                        || nmaCuentaBancoDto.getBancoId().getBancoId() == null
                        || nmaCuentaBancoDto.getNclCentrocCliente().getCentrocClienteId() == null
                        || nmaCuentaBancoDto.getFuncionCuentaId() == null
                        || nmaCuentaBancoDto.getFuncionCuentaId().getFuncionCuentaId() == null) {
                    respuesta.setMensaje(Constantes.CAMPOS_REQUERIDOS);
                    respuesta.setResultado(Constantes.RESULTADO_ERROR);

                } else {
                    respuesta.setMensaje(Constantes.EXITO);
                    respuesta.setResultado(Constantes.RESULTADO_EXITO);
                }
            }

            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " validarCamposObligatorios  " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private boolean validarDuplicateCuentaBanco(NmaCuentaBancoDto nmaCuentaBancoDto) throws ServiceException {
        try {
            mismaCuenta=false;
                List<NmaCuentaBanco> lstNmaCntBank= nmaCuentaBancoRepository.findByNumeroCuenta(nmaCuentaBancoDto.getNumeroCuenta(), nmaCuentaBancoDto.getBancoId().getBancoId(), nmaCuentaBancoDto.getNclCentrocCliente().getCentrocClienteId());
                boolean flag= lstNmaCntBank.isEmpty();
                if(!flag){
                    for (NmaCuentaBanco cuenta:lstNmaCntBank) {
                        // La cuenta bancaria pernetenede a la empresa y el ncopérsona es null
                        if(cuenta.getNcoPersona()==null){
                            //Si el idPersona que llega es null entonces pertenece a la empresa,
                            //Ahora verificamos que la cuenta banco no sea null para evitar nullpointer
                            // se verifica que la tabla cuenta  no tenga el mismo registro que el que se manda
                            // por lo que debe dejarla pasar
                            if(nmaCuentaBancoDto.getNcoPersona()==null && cuenta.getCuentaBancoId().equals(nmaCuentaBancoDto.getCuentaBancoId())){
                                flag=true;
                                mismaCuenta=true;
                                break;
                            }
                            continue;
                        }
                        if (nmaCuentaBancoDto.getNcoPersona()!= null && cuenta.getNcoPersona().getPersonaId().equals(nmaCuentaBancoDto.getNcoPersona().getPersonaId())) {
                            flag = true;
                            mismaCuenta=true;
                            break;
                        }
                    }
                }
            return flag;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " validarDuplicateCuentaBanco " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private boolean validarDuplicateClabeBanco(NmaCuentaBancoDto nmaCuentaBancoDto ) throws ServiceException {
        try {
            List<NmaCuentaBanco> lstCtaBank=nmaCuentaBancoRepository.findByClabeAndNclCentrocClienteCentrocClienteId(nmaCuentaBancoDto.getClabe(),nmaCuentaBancoDto.getNclCentrocCliente().getCentrocClienteId());
            boolean flag=lstCtaBank.isEmpty();
            mismaCuenta=false;
            if(!flag){
                for (NmaCuentaBanco cuenta:lstCtaBank) {
                    //La cuenta bancaria pernetenede a la empresa y el ncopérsona es null
                    if(cuenta.getNcoPersona()==null ){
                        //Si el idPersona que llega es null o 0 entonces pertenece a la empresa por lo que debe dejarla pasar
                        if(nmaCuentaBancoDto.getNcoPersona()==null && cuenta.getCuentaBancoId()!=null && cuenta.getCuentaBancoId().equals(nmaCuentaBancoDto.getCuentaBancoId())){
                            flag=true;
                            mismaCuenta=true;
                            break;
                        }
                        continue;
                    }
                    if (nmaCuentaBancoDto.getNcoPersona()!=null && cuenta.getNcoPersona().getPersonaId().equals(nmaCuentaBancoDto.getNcoPersona().getPersonaId())) {
                        flag = true;
                        mismaCuenta=true;
                        break;
                    }
                }
            }
            return flag;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " ValidarDuplicateClabeBanco " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica modificar(NmaCuentaBancoDto nmaCuentaBancoDto) throws ServiceException {
        try {
            if (nmaCuentaBancoDto.getClabe() != null && nmaCuentaBancoDto.getEsActivo() != null) {
                RespuestaGenerica response = validarCamposObligatorios(nmaCuentaBancoDto);
                if (response.isResultado()) {
                    response = validarCuentaClabe(nmaCuentaBancoDto);
                    if (response.isResultado()) {
                        response
                                .setDatos(ObjetoMapper.map(
                                        nmaCuentaBancoRepository.update(
                                                ObjetoMapper.map(nmaCuentaBancoDto,
                                                        NmaCuentaBanco.class
                                                )), NmaCuentaBancoDto.class
                                ));
                        response.setResultado(Constantes.RESULTADO_EXITO);

                        response.setMensaje(Constantes.EXITO);
                    }
                }
                return response;
            } else {
                return new RespuestaGenerica(null, Constantes.RESULTADO_ERROR, Constantes.ID_NULO);
            }
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " modificar " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica modificarLista(List<NmaCuentaBancoDto> listaNmaCuentaBancoDto) throws ServiceException {
        try {
            for (NmaCuentaBancoDto dto : listaNmaCuentaBancoDto) {
                this.modificar(dto);
            }
            return findAll();
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " modificarLista " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica eliminar(Integer cuentaBancoId) throws ServiceException {

        try {
            nmaCuentaBancoRepository.update(cuentaBancoId, Constantes.ESTATUS_INACTIVO);
            return new RespuestaGenerica(null, Constantes.RESULTADO_EXITO, Constantes.EXITO);
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " eliminar " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica findByEsActivo(Boolean activo) throws ServiceException {
        RespuestaGenerica respuestaGenerica = new RespuestaGenerica();
        try {
            respuestaGenerica.setDatos(nmaCuentaBancoRepository.findByEsActivoOrderByDescripcion(activo));
            respuestaGenerica.setResultado(Constantes.RESULTADO_EXITO);
            respuestaGenerica.setMensaje(Constantes.EXITO);
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" findByEsActivo" + Constantes.ERROR_EXCEPCION, e);
        }
        return respuestaGenerica;
    }

}
