package es.MiHipotecaApp.TFG.Transfers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class HipotecaSegMixta extends HipotecaSeguimiento implements Serializable {

    //Hipoteca mixta
    private int anios_fija_mixta;
    private double porcentaje_fijo_mixta;
    private double porcentaje_diferencial_mixta;
    private boolean revision_anual;

    public HipotecaSegMixta(String nombre, String comunidad_autonoma, String tipo_vivienda, String antiguedad_vivienda, double precio_vivienda, double cantidad_abonada, int plazo_anios, Date fecha_inicio, String tipo_hipoteca, double totalGastos, List<Double> arrayVinculacionesAnual, String banco_asociado, int anios_fija_mixta, double porcentaje_fijo_mixta, double porcentaje_diferencial_mixta, boolean revision_anual) {
        super(nombre, comunidad_autonoma, tipo_vivienda, antiguedad_vivienda, precio_vivienda, cantidad_abonada, plazo_anios, fecha_inicio, tipo_hipoteca, totalGastos, arrayVinculacionesAnual, banco_asociado);
        this.anios_fija_mixta = anios_fija_mixta;
        this.porcentaje_fijo_mixta = porcentaje_fijo_mixta;
        this.porcentaje_diferencial_mixta = porcentaje_diferencial_mixta;
        this.revision_anual = revision_anual;
    }

    /** Esta funcion devuelve el capital pendiente total por amortizar**/
    @Override
    public double getCapitalPendienteTotalActual(int numero_pago, HashMap<Integer, List<Object>> amortizaciones, List<Double> euribors){
        double capital_pendiente = precio_vivienda - cantidad_abonada;
        int plazoActual = plazo_anios * 12;
        double cuota_mensual = getCuotaMensual(porcentaje_fijo_mixta, capital_pendiente, plazoActual);
        double cantidad_capital;

        int aux = numero_pago > anios_fija_mixta * 12 ? anios_fija_mixta * 12 : numero_pago;
        for (int i = 1; i <= aux; i++){
            if(amortizaciones.containsKey(i)){
                if(amortizaciones.get(i).get(0).equals("total")) return 0;
                else if (amortizaciones.get(i).get(0).equals("parcial_cuota")){
                    capital_pendiente -= (Double) amortizaciones.get(i).get(1);
                    cuota_mensual = getCuotaMensual(porcentaje_fijo_mixta, capital_pendiente, plazoActual);
                }
                else {
                    capital_pendiente -= (Double) amortizaciones.get(i).get(1);
                    plazoActual -= (Long) amortizaciones.get(i).get(2);
                }
            }
            cantidad_capital = getCapitalAmortizadoMensual(cuota_mensual, capital_pendiente, porcentaje_fijo_mixta);
            capital_pendiente = capital_pendiente - cantidad_capital;
        }

        int j = aux;
        while(j < numero_pago){
            double euribor = getEuriborPasado(j + 1, euribors);
            double porcentaje = porcentaje_diferencial_mixta + euribor;
            if (porcentaje < 0) porcentaje = 0;
            cuota_mensual = getCuotaMensual(porcentaje, capital_pendiente, plazoActual - j);
            if(amortizaciones.containsKey(j)){
                if(amortizaciones.get(j).get(0).equals("total")) return 0;
                else if (amortizaciones.get(j).get(0).equals("parcial_cuota")){
                    capital_pendiente -= (Double) amortizaciones.get(j).get(1);
                    cuota_mensual = getCuotaMensual(porcentaje, capital_pendiente, plazoActual);
                }
                else {
                    capital_pendiente -= (Double) amortizaciones.get(j).get(1);
                    plazoActual -= (Long) amortizaciones.get(j).get(2);
                }
            }

            cantidad_capital = getCapitalAmortizadoMensual(cuota_mensual, capital_pendiente, porcentaje);
            capital_pendiente = capital_pendiente - cantidad_capital;
            j++;

        }
        return capital_pendiente;
    }

    /** Esta funcion devuelve la cantidad de intereses hasta el numero de pago pasado**/
    @Override
    public double getInteresesHastaNumPago(int numero_pago, HashMap<Integer, List<Object>> amortizaciones, List<Double> euribors){
        double intereses_totales = 0;
        double capital_pendiente = precio_vivienda - cantidad_abonada;
        int plazoActual = plazo_anios * 12;
        double cuota_mensual = getCuotaMensual(porcentaje_fijo_mixta, capital_pendiente, plazoActual);
        double cantidad_capital;
        int aux = numero_pago > anios_fija_mixta * 12 ? anios_fija_mixta * 12 : numero_pago;
        for (int i = 1; i <= aux; i++){
            if(amortizaciones.containsKey(i)){
                if(amortizaciones.get(i).get(0).equals("total")) return 0;
                else if (amortizaciones.get(i).get(0).equals("parcial_cuota")){
                    capital_pendiente -= (Double) amortizaciones.get(i).get(1);
                    cuota_mensual = getCuotaMensual(porcentaje_fijo_mixta, capital_pendiente, plazoActual);
                }
                else {
                    capital_pendiente -= (Double) amortizaciones.get(i).get(1);
                    plazoActual -= (Long) amortizaciones.get(i).get(2);
                }
            }
            intereses_totales += getInteresMensual(capital_pendiente, porcentaje_fijo_mixta);
            cantidad_capital = getCapitalAmortizadoMensual(cuota_mensual, capital_pendiente, porcentaje_fijo_mixta);
            capital_pendiente = capital_pendiente - cantidad_capital;
        }

        int j = aux;
        while(j < numero_pago){
            double euribor = getEuriborPasado(j + 1, euribors);
            double porcentaje = porcentaje_diferencial_mixta + euribor;
            if (porcentaje < 0 ) porcentaje = 0;
            cuota_mensual = getCuotaMensual(porcentaje, capital_pendiente, plazoActual - j);

            if(amortizaciones.containsKey(j)){
                if(amortizaciones.get(j).get(0).equals("total")) return 0;
                else if (amortizaciones.get(j).get(0).equals("parcial_cuota")){
                    capital_pendiente -= (Double) amortizaciones.get(j).get(1);
                    cuota_mensual = getCuotaMensual(porcentaje, capital_pendiente, plazoActual);
                }
                else {
                    capital_pendiente -= (Double) amortizaciones.get(j).get(1);
                    plazoActual -= (Long) amortizaciones.get(j).get(2);
                }
            }

            intereses_totales += getInteresMensual(capital_pendiente, porcentaje);
            cantidad_capital = getCapitalAmortizadoMensual(cuota_mensual, capital_pendiente, porcentaje);
            capital_pendiente = capital_pendiente - cantidad_capital;
            j++;

        }

        return intereses_totales;
    }

    /** Esta funcion devuelve el capital y los intereses pendientes por pagar, simulando que el euribor se mantiene fijo
     *  durante los años restantes. (Se utiliza el euribor del mes actual) **/
    @Override
    public double getDineroRestanteActual(int numPago, HashMap<Integer, List<Object>> amortizaciones, List<Double> euribors){

        // Si estas en la fase fija, tienes que acabar la fase fija y luego estimar con el euribor actual
        // Si estas en la fase variable, simular lo que queda en funcion del euribor actual

        int cuotasRestantes = getPlazoActual(amortizaciones) - numPago;
        double capital_pendiente = getCapitalPendienteTotalActual(numPago, amortizaciones, euribors);
        double porcentaje_aplicado  = numPago < anios_fija_mixta * 12 ? porcentaje_fijo_mixta : getEuriborActual(euribors) + porcentaje_diferencial_mixta;
        if (porcentaje_aplicado < 0) porcentaje_aplicado = 0;
        double cuota_mensual = getCuotaMensual(porcentaje_aplicado, capital_pendiente, cuotasRestantes);

        //ESTAS EN LA PARTE FIJA
        double dinero_restante = 0;

        if(numPago < anios_fija_mixta * 12){
            int cuotas_pdte_primer_porcentaje = (anios_fija_mixta * 12) - numPago;
            dinero_restante = cuota_mensual * cuotas_pdte_primer_porcentaje;
            porcentaje_aplicado = getEuriborActual(euribors) + porcentaje_diferencial_mixta;
            if(porcentaje_aplicado < 0) porcentaje_aplicado = 0;
            cuotasRestantes = cuotasRestantes - cuotas_pdte_primer_porcentaje;
        }
        // coger euribor actual
        cuota_mensual = getCuotaMensual(porcentaje_aplicado, capital_pendiente, cuotasRestantes);
        dinero_restante += cuota_mensual * cuotasRestantes;
        return dinero_restante;
    }

    /** Esta funcion devuelve si en el siguiente pago toca revision **/
    @Override
    public boolean siguienteCuotaRevision(HashMap<Integer, List<Object>> amortizaciones){

        int numCuotasPagadas = getNumeroCuotaActual(amortizaciones);
        if (numCuotasPagadas < anios_fija_mixta * 12) return false;
        if (numCuotasPagadas == anios_fija_mixta * 12) return true;
        if ((revision_anual  && (numCuotasPagadas % 12 == 0)) || (!revision_anual && (numCuotasPagadas % 6  == 0))) return true;
        return false;
    }

    /** Esta funcion devuelve el porcentaje que se aplica para un determinado numero de cuota**/
    @Override
    public double getPorcentajePorCuota(int numCuota, HashMap<Integer, List<Object>> amortizaciones, List<Double> euribors){
        if (numCuota <= anios_fija_mixta * 12) return porcentaje_fijo_mixta;
        double porcentaje = getEuriborPasado(numCuota, euribors) + porcentaje_diferencial_mixta;
        if (porcentaje < 0 ) return 0;
        else return porcentaje;
    }

    /** Esta funcion devuelve la cuota, capital, intereses y capital pendiente del numero de cuota pasado **/
    @Override
    public ArrayList<Double> getFilaCuadroAmortizacionMensual(int numCuota, HashMap<Integer, List<Object>> amortizaciones, List<Double> euribors){

        ArrayList<Double> valores = new ArrayList<>();
        double porcentaje_aplicado = getPorcentajePorCuota(numCuota, amortizaciones,euribors);
        double capitalPdteCuota = getCapitalPendienteTotalActual(numCuota, amortizaciones, euribors);
        double capPdte = numCuota == 0 ? precio_vivienda - cantidad_abonada : getCapitalPendienteTotalActual(numCuota - 1, amortizaciones, euribors);
        double cuota = cogerCuotaActual(numCuota, amortizaciones, euribors);

        double capAmortMensual;
        if(amortizaciones.containsKey(numCuota)) capAmortMensual = getCapitalAmortizadoMensual(cuota, capPdte - (Double) amortizaciones.get(numCuota).get(1), porcentaje_aplicado);
        else capAmortMensual = getCapitalAmortizadoMensual(cuota, capPdte, porcentaje_aplicado);

        //double capAmortMensual = getCapitalAmortizadoMensual(cuota, capPdte, porcentaje_aplicado);

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
        valores.add(getInteresMensual(capPdte, porcentaje_aplicado));
        valores.add(capitalPdteCuota);
        return valores;
    }

    @Override
    public double cogerCuotaActual(int num_cuota, HashMap<Integer, List<Object>> amortizaciones, List<Double> euribors){

        // si el numero de cuota es mayor al plazo actual la cuota es 0
        if (num_cuota > getPlazoActual(amortizaciones)) return 0;

        double capital_pendiente = precio_vivienda - cantidad_abonada;
        double cuota = getCuotaMensual(porcentaje_fijo_mixta, capital_pendiente, plazo_anios * 12);


        // estoy en el parte fija
        if(num_cuota <= anios_fija_mixta * 12){
            //Ver si hay reducción de cuota
            for (int i = 1; i <= num_cuota; i++) {
                if(amortizaciones.containsKey(i)){
                    if(amortizaciones.get(i).get(0).equals("total")) return 0;
                    else if (amortizaciones.get(i).get(0).equals("parcial_cuota")){
                        capital_pendiente -= (Double) amortizaciones.get(i).get(1);
                        cuota = getCuotaMensual(porcentaje_fijo_mixta, capital_pendiente, getPlazoNumPago(i, amortizaciones)  - i + 1);
                    }
                    // Si hay reducción de plazo da igual porque la cuota es la misma
                }
                double cantidad_capital = getCapitalAmortizadoMensual(cuota, capital_pendiente, porcentaje_fijo_mixta);
                capital_pendiente = capital_pendiente - cantidad_capital;
            }
        }
        //caso cuota esta en la parte variable
        else{
            int revision = isRevision_anual() ? 12 : 6;
            int numCuotaRevisionAnterior = 1;
            if (num_cuota % revision == 0)  numCuotaRevisionAnterior += num_cuota - revision;
            else numCuotaRevisionAnterior += num_cuota - num_cuota % revision;

            capital_pendiente = getCapitalPendienteTotalActual(numCuotaRevisionAnterior - 1, amortizaciones, euribors);
            double porcentaje_aplicado = getEuriborPasado(num_cuota, euribors) + porcentaje_diferencial_mixta;
            if(porcentaje_aplicado < 0) porcentaje_aplicado = 0;
            cuota = getCuotaMensual(porcentaje_aplicado, capital_pendiente, getPlazoNumPago(numCuotaRevisionAnterior, amortizaciones) - numCuotaRevisionAnterior + 1);

            //Comprobar si hay reduccion de cuota
            for (int i = numCuotaRevisionAnterior; i <= num_cuota; i++) {
                if(amortizaciones.containsKey(i)){
                    if(amortizaciones.get(i).get(0).equals("total")) return 0;
                    else if (amortizaciones.get(i).get(0).equals("parcial_cuota")){
                        capital_pendiente -= (Double) amortizaciones.get(i).get(1);
                        porcentaje_aplicado = getEuriborPasado(i, euribors) + porcentaje_diferencial_mixta;
                        if(porcentaje_aplicado < 0) porcentaje_aplicado = 0;
                        cuota = getCuotaMensual(porcentaje_aplicado, capital_pendiente, getPlazoNumPago(i, amortizaciones)  - i + 1);
                    }
                    // Si hay reducción de plazo da igual porque la cuota es la misma
                }
                double cantidad_capital = getCapitalAmortizadoMensual(cuota, capital_pendiente, porcentaje_aplicado);
                capital_pendiente = capital_pendiente - cantidad_capital;
            }
        }
        return cuota;
    }



    /** Esta funcion devuelve el total anual, capital anual, intereses anuales y capital pendiente del numero de anio pasado**/
    @Override
    public ArrayList<Double> getFilaCuadroAmortizacionAnual(int anio, int num_anio, HashMap<Integer, List<Object>> amortizaciones, List<Double> euribors){

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
        }else cuotasAnuales = 12;


        int cuotasPrimerAnio = (getNumeroCuotaEnEnero(inicio.get(Calendar.YEAR)) + 12) - 1;

        int cuotasPagadas = num_anio > 1 ?  cuotasPrimerAnio + (num_anio - 2) * 12 + cuotasAnuales : cuotasAnuales;

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
    public double getPorcentajeUltimaCuota(HashMap<Integer, List<Object>> amortizaciones, List<Double> euribors){
        int ultimaCuota = getPlazoActual(amortizaciones);
        if(ultimaCuota <= anios_fija_mixta * 12) return porcentaje_fijo_mixta;
        double porcentaje = porcentaje_diferencial_mixta + getEuriborPasado(ultimaCuota, euribors);
        if (porcentaje < 0) return 0;
        else return porcentaje;
    }
    /** Getters y Setters*/
    public int getAnios_fija_mixta() {
        return anios_fija_mixta;
    }

    public double getPorcentaje_fijo_mixta() {
        return porcentaje_fijo_mixta;
    }

    public double getPorcentaje_diferencial_mixta() {
        return porcentaje_diferencial_mixta;
    }

    public boolean isRevision_anual() {
        return revision_anual;
    }

    public void setAnios_fija_mixta(int anios_fija_mixta) {
        this.anios_fija_mixta = anios_fija_mixta;
    }

    public void setPorcentaje_fijo_mixta(double porcentaje_fijo_mixta) {
        this.porcentaje_fijo_mixta = porcentaje_fijo_mixta;
    }

    public void setPorcentaje_diferencial_mixta(double porcentaje_diferencial_mixta) {
        this.porcentaje_diferencial_mixta = porcentaje_diferencial_mixta;
    }

    public void setRevision_anual(boolean revision_anual) {
        this.revision_anual = revision_anual;
    }
}
