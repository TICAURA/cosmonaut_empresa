package mx.com.ga.cosmonaut.empresa.services.impl;

import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenericaGruop;
import mx.com.ga.cosmonaut.common.dto.imss.IMSSFiltradoRequest;
import mx.com.ga.cosmonaut.common.dto.imss.IMSSFiltradoResponse;
import mx.com.ga.cosmonaut.common.dto.imss.VariabilidadFiltradoRequest;
import mx.com.ga.cosmonaut.common.dto.imss.VariabilidadFiltradoResponse;
import mx.com.ga.cosmonaut.common.entity.cliente.NclCentrocCliente;
import mx.com.ga.cosmonaut.common.entity.colaborador.NcoKardexColaborador;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.common.repository.cliente.NclCentrocClienteRepository;
import mx.com.ga.cosmonaut.common.repository.cliente.NclRegistroPatronalRepository;
import mx.com.ga.cosmonaut.common.repository.colaborador.NcoCatMovimientoImssRepository;
import mx.com.ga.cosmonaut.common.repository.colaborador.NcoKardexColaboradorRepository;
import mx.com.ga.cosmonaut.common.repository.nativo.IMSSRepository;
import mx.com.ga.cosmonaut.common.util.Constantes;
import mx.com.ga.cosmonaut.common.util.Utilidades;
import mx.com.ga.cosmonaut.empresa.services.IMSSService;
import org.apache.commons.lang3.time.DateUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;

@Singleton
public class IMSSServiceImpl implements IMSSService {

    @Inject
    private IMSSRepository imssRepository;

    @Inject
    private NcoCatMovimientoImssRepository ncoCatMovimientoImssRepository;

    @Inject
    private NclRegistroPatronalRepository nclRegistroPatronalRepository;

    @Inject
    private NclCentrocClienteRepository nclCentrocClienteRepository;

    @Inject
    private NcoKardexColaboradorRepository ncoKardexColaboradorRepository;

    @Override
    public RespuestaGenerica listarRegistrosPatronales(Integer id) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();

            Optional<NclCentrocCliente> centrocCliente = nclCentrocClienteRepository.findById(id);
            if (centrocCliente.isPresent()) {
                respuesta.setDatos(nclRegistroPatronalRepository.findByCentrocClienteId(centrocCliente.get()));
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
                respuesta.setMensaje(Constantes.EXITO);
            } else {
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(Constantes.ERROR_CLIENTE_NO_EXISTE);
            }
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" listarRegistrosPatronales " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica listarMovimientos() throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            respuesta.setDatos(ncoCatMovimientoImssRepository.findAll());
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            respuesta.setMensaje(Constantes.EXITO);
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" listarMovimientos " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenericaGruop filtrar(IMSSFiltradoRequest request) throws ServiceException {
        try {
            RespuestaGenericaGruop respuesta = new RespuestaGenericaGruop();
            //Se agrega persona ID
            List<IMSSFiltradoResponse> listRequest= imssRepository.filtrar(request);
            List<IMSSFiltradoResponse> listRequestTemp = new ArrayList<>();
            List<Date> listaFechasNoLaborables = new ArrayList<>();;
            listaFechasNoLaborables.add(Utilidades.textoFecha("2021-12-25"));;
            Calendar c = Calendar.getInstance();
            //c.add(Calendar.DATE, -1);
            Date newDate = c.getTime();
            // Date newDate =Utilidades.textoFecha("2022-09-03");

            for (IMSSFiltradoResponse i: listRequest ) {
                   if (i.getTipo_compensacion_id()==3)
                {
                    Calendar temp= DateUtils.toCalendar(i.getFecha_movimiento());
                    int month = temp.get(Calendar.MONTH)+1; //
                    if (month <3) {
                        temp.set(Calendar.DAY_OF_MONTH, 1);
                        temp.set(Calendar.MONTH, 2);
                    }
                    if (month >=3 && month <5) {
                        temp.set(Calendar.DAY_OF_MONTH, 1);
                        temp.set(Calendar.MONTH, 4);
                    }
                    if (month >=5 && month <7) {
                        temp.set(Calendar.DAY_OF_MONTH, 1);
                        temp.set(Calendar.MONTH, 6);
                    }
                    if (month >=7 && month <9) {
                        temp.set(Calendar.DAY_OF_MONTH, 1);
                        temp.set(Calendar.MONTH, 8);
                    }
                    if (month >=9 && month <11) {
                        temp.set(Calendar.DAY_OF_MONTH, 1);
                        temp.set(Calendar.MONTH, 10);
                    }
                    if (month >=11) {
                        temp.set(Calendar.DAY_OF_MONTH, 1);
                        temp.set(Calendar.MONTH, 0);
                        temp.set(Calendar.YEAR,  temp.get(Calendar.YEAR)+1);
                    }
                    int dia= diasHabiles(DateUtils.toCalendar(temp.getTime()),DateUtils.toCalendar(newDate),listaFechasNoLaborables)+1;
                    if (!i.getEstatus().equals("Aceptado")) {
                         i.setVigencia_movimiento(esatatusIdse(dia));
                    } else {
                        i.setVigencia_movimiento("");
                    }
                } else {
                    int dia= diasHabiles(DateUtils.toCalendar(i.getFecha_movimiento()),DateUtils.toCalendar(newDate),listaFechasNoLaborables);
                    if (!i.getEstatus().equals("Aceptado")) {
                        i.setVigencia_movimiento(esatatusIdse(dia));
                    } else {
                        i.setVigencia_movimiento("");
                    }
                }

            }

            Map<String, Long> counting = listRequest.stream().collect(
                    Collectors.groupingBy(IMSSFiltradoResponse::getVigencia_movimiento, Collectors.counting()));
            respuesta.setDatos(listRequest);
            respuesta.setGroup(counting);
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            respuesta.setMensaje(Constantes.EXITO);
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" filtrar " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    public String esatatusIdse(int dia) {
        String aTiempo = null;
        if (dia <= 3)
            aTiempo = "A tiempo";
        if (dia > 3 && dia <= 5)
            aTiempo = "Próximo a vencer";
        if (dia > 5)
            aTiempo = "Extemporáneo";
        return  aTiempo;
    }

    public int diasHabiles(Calendar fechaInicial, Calendar fechaFinal, List<Date> listaFechasNoLaborables) {
        int diffDays = 0;
        boolean diaHabil = false;
        while (fechaInicial.before(fechaFinal) || fechaInicial.equals(fechaFinal)) {

            if (!listaFechasNoLaborables.isEmpty()) {
                for (Date date : listaFechasNoLaborables) {
                    Date fechaNoLaborablecalendar = fechaInicial.getTime();
                    //si el dia de la semana de la fecha minima es diferente de sabado o domingo
                    if (fechaInicial.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY && fechaInicial.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && !fechaNoLaborablecalendar.equals(date)) {
                        //se aumentan los dias de diferencia entre min y max
                        diaHabil = true;
                    } else {
                        diaHabil = false;
                        break;
                    }
                }
            } else {
                if (fechaInicial.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY && fechaInicial.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
                    //se aumentan los dias de diferencia entre min y max
                    diffDays++;
                }
            }
            if (diaHabil == true) {
                diffDays++;
            }
            //se suma 1 dia para hacer la validacion del siguiente dia.
            fechaInicial.add(Calendar.DATE, 1);
        }
        return diffDays;
    }
    
    @Override
    public RespuestaGenerica eliminar(Long id) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();

            Optional<NcoKardexColaborador> ncoKardexColaborador = ncoKardexColaboradorRepository.findById(id);
            if (ncoKardexColaborador.isPresent() && !ncoKardexColaborador.get().isEsImss()) {
                ncoKardexColaboradorRepository.update(id.intValue(), Constantes.ESTATUS_INACTIVO);

                respuesta.setDatos(null);
                respuesta.setResultado(Constantes.RESULTADO_EXITO);
                respuesta.setMensaje(Constantes.EXITO);
            } else {
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(Constantes.ERROR);
            }
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" eliminar " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica filtrarVariabilidad(VariabilidadFiltradoRequest request) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();
            List<VariabilidadFiltradoResponse> lstfiltro=imssRepository.filtroVariabilidad(request);
            for(VariabilidadFiltradoResponse filtro:lstfiltro){
                DateFormat df = new SimpleDateFormat("dd-MMM-yyyy", new Locale("es", "MX"));
                String fecha="Del " + df.format(filtro.getFechaInicio()) +" al " +df.format(filtro.getFechaFin());
                filtro.setPeriodoCalculo(fecha);
            }
            respuesta.setDatos(lstfiltro);
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            respuesta.setMensaje(Constantes.EXITO);
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" filtrarVariabilidad " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica calcularDias(Integer bimestre) throws ServiceException {
        try {
            RespuestaGenerica respuesta = new RespuestaGenerica();

            if (bimestre < 1 || bimestre > 6) {
                respuesta.setDatos(null);
                respuesta.setResultado(Constantes.RESULTADO_ERROR);
                respuesta.setMensaje(Constantes.ERROR_BAD_BIMESTER);
                return respuesta;
            }

            Map<String, Object> response = new HashMap<>();
            LocalDate inicio = LocalDate.of(LocalDate.now().getYear(), (bimestre*2)-1, 1);
            LocalDate fin = LocalDate.of(LocalDate.now().getYear(), bimestre*2, 1);
            fin = LocalDate.of(LocalDate.now().getYear(), bimestre*2, fin.lengthOfMonth());
            long daysBetween = DAYS.between(inicio, fin);
            response.put("diasTotales", daysBetween+1);

            respuesta.setDatos(response);
            respuesta.setResultado(Constantes.RESULTADO_EXITO);
            respuesta.setMensaje(Constantes.EXITO);
            return respuesta;
        } catch (Exception e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO +" calcularDias " + Constantes.ERROR_EXCEPCION, e);
        }
    }

}
