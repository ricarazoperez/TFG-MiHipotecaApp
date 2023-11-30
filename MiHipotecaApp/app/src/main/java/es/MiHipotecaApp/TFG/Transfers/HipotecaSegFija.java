package es.MiHipotecaApp.TFG.Transfers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class HipotecaSegFija extends HipotecaSeguimiento implements Serializable {

    //Hipoteca fija
    private double porcentaje_fijo;

    public HipotecaSegFija(String nombre, String comunidad_autonoma, String tipo_vivienda, String antiguedad_vivienda, double precio_vivienda, double cantidad_abonada, int plazo_anios, Date fecha_inicio, String tipo_hipoteca, double totalGastos, List<Double> arrayVinculacionesAnual, String banco_asociado, double porcentaje_fijo) {
        super(nombre, comunidad_autonoma, tipo_vivienda, antiguedad_vivienda, precio_vivienda, cantidad_abonada, plazo_anios, fecha_inicio, tipo_hipoteca, totalGastos, arrayVinculacionesAnual, banco_asociado);
        this.porcentaje_fijo = porcentaje_fijo;
    }

    /** Esta funcion devuelve el capital y los intereses pendientes por pagar **/
    @Override
    public double getDineroRestanteActual(int numPago, HashMap<Integer, List<Object>> amortizaciones, List<Double> euribors){
        int cuotasRestantes = getPlazoNumPago(numPago, amortizaciones) - numPago;
        return cogerCuotaActual(numPago + 1, amortizaciones, euribors) * cuotasRestantes;
    }

    /** Esta funcion devuelve el capital pendiente total por amortizar**/
    @Override
    public double getCapitalPendienteTotalActual(int numero_pago, HashMap<Integer, List<Object>> amortizaciones, List<Double> euribors){
        double capital_pendiente = precio_vivienda - cantidad_abonada;
        int plazoActual = plazo_anios * 12;
        double cuota_mensual = getCuotaMensual(porcentaje_fijo, capital_pendiente, plazoActual);

        for (int i = 1; i <= numero_pago; i++){
            if(amortizaciones.containsKey(i)){
                if(amortizaciones.get(i).get(0).equals("total")) return 0;
                else if (amortizaciones.get(i).get(0).equals("parcial_cuota")){
                    capital_pendiente = capital_pendiente - (Double) amortizaciones.get(i).get(1);
                    cuota_mensual = getCuotaMensual(porcentaje_fijo, capital_pendiente, plazoActual - i + 1);
                }
                else {
                    capital_pendiente -= (Double) amortizaciones.get(i).get(1);
                }
            }
            double cantidad_capital = getCapitalAmortizadoMensual(cuota_mensual, capital_pendiente, porcentaje_fijo);
            capital_pendiente = capital_pendiente - cantidad_capital;
        }

        return capital_pendiente;
    }

    /** Devuelve los intereses hasta el numero de pago pasado por parametro **/
    @Override
    public double getInteresesHastaNumPago(int num_pago, HashMap<Integer, List<Object>> amortizaciones, List<Double> euribors) {
        double interesesTotales = 0;
        double capPendiente = precio_vivienda - cantidad_abonada;

        int plazoActual = plazo_anios * 12;

        double cuota_mensual = getCuotaMensual(getPorcentaje_fijo(), capPendiente, plazoActual);

        for (int i = 1; i <= num_pago; i++) {
            if(amortizaciones.containsKey(i)){
                if(amortizaciones.get(i).get(0).equals("total")) return 0;
                else if (amortizaciones.get(i).get(0).equals("parcial_cuota")){
                    capPendiente -= (Double) amortizaciones.get(i).get(1);
                    cuota_mensual = getCuotaMensual(porcentaje_fijo, capPendiente, plazoActual - i + 1);
                }
                else {
                    capPendiente -= (Double) amortizaciones.get(i).get(1);
                }
            }
            interesesTotales += getInteresMensual(capPendiente, porcentaje_fijo);
            capPendiente -= (cuota_mensual - getInteresMensual(capPendiente, getPorcentaje_fijo()));
        }

        return interesesTotales;
    }

    /** Esta funcion devuelve la cuota, capital, intereses y capital pendiente del numero de cuota pasado **/
    @Override
    public ArrayList<Double> getFilaCuadroAmortizacionMensual(int numCuota, HashMap<Integer, List<Object>> amortizaciones, List<Double> euribors){
        ArrayList<Double> valores = new ArrayList<>();
        double capPdteCuota = getCapitalPendienteTotalActual(numCuota, amortizaciones,euribors);
        double capPdte      = getCapitalPendienteTotalActual(numCuota - 1, amortizaciones, euribors);
        double cuota = cogerCuotaActual(numCuota, amortizaciones, euribors);

        double capAmortMensual;

        if(amortizaciones.containsKey(numCuota)) capAmortMensual = getCapitalAmortizadoMensual(cuota, capPdte - (Double) amortizaciones.get(numCuota).get(1), porcentaje_fijo);
        else capAmortMensual = getCapitalAmortizadoMensual(cuota, capPdte, porcentaje_fijo);


        // Restamos al capital pendiente la amortizacion para sacar la nueva cuota
        // Este if es para mostrar la cuota con la amortizacion
        if(amortizaciones.containsKey(numCuota)){
            capPdte -= (Double) amortizaciones.get(numCuota).get(1);
            cuota += (Double) amortizaciones.get(numCuota).get(1);
            capAmortMensual += (Double) amortizaciones.get(numCuota).get(1);
            if(amortizaciones.get(numCuota).get(0).equals("total")){
                cuota = (Double) amortizaciones.get(numCuota).get(1);
                capAmortMensual = (Double) amortizaciones.get(numCuota).get(1);
            }
        }

        if(capPdte < cuota) cuota = capPdte;
        valores.add(cuota);
        valores.add(capAmortMensual);
        valores.add(getInteresMensual(capPdte, porcentaje_fijo));
        valores.add(capPdteCuota);

        return valores;
    }


    // funcion (numero cuota) calcule la cuota para ese num cuota
    @Override
    public double cogerCuotaActual(int num_cuota, HashMap<Integer, List<Object>> amortizaciones, List<Double> euribors){
        double capital_pendiente = precio_vivienda - cantidad_abonada;
        double cuota = getCuotaMensual(porcentaje_fijo, capital_pendiente, plazo_anios * 12);

        // si el numero de cuota es mayor al plazo actual la cuota es 0
        if (num_cuota > getPlazoActual(amortizaciones)) return 0;

        //Ver si hay reducción de cuota
        for (int i = 1; i <= num_cuota; i++) {
            if(amortizaciones.containsKey(i)){
                if(amortizaciones.get(i).get(0).equals("total")) return 0;
                else if (amortizaciones.get(i).get(0).equals("parcial_cuota")){
                    capital_pendiente -= (Double) amortizaciones.get(i).get(1);
                    cuota = getCuotaMensual(porcentaje_fijo, capital_pendiente, getPlazoNumPago(num_cuota, amortizaciones) - i + 1);
                }
                // Si hay reducción de plazo da igual porque la cuota es la misma
            }
            double cantidad_capital = getCapitalAmortizadoMensual(cuota, capital_pendiente, porcentaje_fijo);
            capital_pendiente = capital_pendiente - cantidad_capital;
        }
        return cuota;
    }

    /** Esta funcion devuelve el total anual, capital anual, intereses anuales y capital pendiente del numero de anio pasado**/
    @Override
    public ArrayList<Double> getFilaCuadroAmortizacionAnual(int anio, int num_anio, HashMap<Integer, List<Object>> amortizaciones, List<Double> euribors){

        // TOTAL_ANUAL, CAPITAL_ANUAL, INTERESES_ANUALES, CAPITAL PDTE
        ArrayList<Double> valores = new ArrayList<>();
        Calendar inicio = Calendar.getInstance();
        inicio.setTime(fecha_inicio);
        int cuotasAnuales;
        boolean ultimoAnio = false;
        //si es el primer año de hipoteca
        if(inicio.get(Calendar.YEAR) == anio) cuotasAnuales = 12 + (getNumeroCuotaEnEnero(anio) - 1);
        else if(inicio.get(Calendar.YEAR) + aniosActualesHipoteca(getPlazoActual(amortizaciones)) - 1 == anio) {
            cuotasAnuales = getPlazoActual(amortizaciones) - (getNumeroCuotaEnEnero(anio) - 1);
            ultimoAnio = true;
        }
        else cuotasAnuales = 12;

        int cuotasPrimerAnio = (getNumeroCuotaEnEnero(inicio.get(Calendar.YEAR)) + 12) - 1;

        int cuotasPagadas = num_anio > 1 ? cuotasPrimerAnio + (num_anio - 2) * 12 + cuotasAnuales : cuotasAnuales;

        // Capital pendiente para diciembre de este año
        double capPdteUltimo = ultimoAnio ? 0 : getCapitalPendienteTotalActual(cuotasPagadas, amortizaciones, euribors);
        // Capital pendiente para diciembre del año anterior
        double capPdteAnterior = cuotasPagadas < 12 && !ultimoAnio ? precio_vivienda - cantidad_abonada : getCapitalPendienteTotalActual(cuotasPagadas - cuotasAnuales, amortizaciones, euribors);

        double totalCapitalAnual = capPdteAnterior - capPdteUltimo;

        double interesesAnteriores = cuotasAnuales < 12 && !ultimoAnio ? 0 : getInteresesHastaNumPago(cuotasPagadas - cuotasAnuales, amortizaciones, euribors);
        double interesesSiguientes = getInteresesHastaNumPago(cuotasPagadas, amortizaciones, euribors);
        double totalInteresesAnio = interesesSiguientes - interesesAnteriores;

        valores.add(totalCapitalAnual + totalInteresesAnio);
        valores.add(totalCapitalAnual);
        valores.add(totalInteresesAnio);
        valores.add(capPdteUltimo);

        return valores;
    }

    @Override
    public double getPorcentajeUltimaCuota(HashMap<Integer, List<Object>> amortizaciones, List<Double> euribors){ return porcentaje_fijo; }


    /** Esta funcion devuelve el porcentaje que se aplica para un determinado numero de cuota**/
    @Override
    public double getPorcentajePorCuota(int numCuota, HashMap<Integer, List<Object>> amortizaciones, List<Double> euribors){ return porcentaje_fijo; }

    /** Getters y Setters*/
    @Override
    public double getPorcentaje_fijo() {
        return porcentaje_fijo;
    }


    public void setPorcentaje_fijo(double porcentaje_fijo) {
        this.porcentaje_fijo = porcentaje_fijo;
    }

}
