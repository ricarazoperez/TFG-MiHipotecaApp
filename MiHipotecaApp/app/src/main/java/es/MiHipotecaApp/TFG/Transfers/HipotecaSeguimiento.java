package es.MiHipotecaApp.TFG.Transfers;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HipotecaSeguimiento implements Serializable {

    protected String nombre;
    protected String comunidad_autonoma;
    protected String tipo_vivienda;
    protected String antiguedad_vivienda;
    protected double precio_vivienda;
    protected double cantidad_abonada;
    protected int plazo_anios;
    protected Date fecha_inicio;
    protected String tipo_hipoteca;
    protected String banco_asociado;

    //Gastos
    protected double totalGastos;
    protected List<Double> arrayVinculacionesAnual;
    protected String idUsuario;



    public HipotecaSeguimiento(String nombre) {
        this.nombre = nombre;
    }
    public HipotecaSeguimiento(String nombre, String comunidad_autonoma, String tipo_vivienda, String antiguedad_vivienda, double precio_vivienda, double cantidad_abonada, int plazo_anios, Date fecha_inicio, String tipo_hipoteca, double totalGastos, List<Double> arrayVinculacionesAnual, String banco_asociado) {
        this.nombre = nombre;
        this.comunidad_autonoma = comunidad_autonoma;
        this.tipo_vivienda = tipo_vivienda;
        this.antiguedad_vivienda = antiguedad_vivienda;
        this.precio_vivienda = precio_vivienda;
        this.cantidad_abonada = cantidad_abonada;
        this.plazo_anios = plazo_anios;
        this.fecha_inicio = fecha_inicio;
        this.tipo_hipoteca = tipo_hipoteca;
        this.totalGastos = totalGastos;
        this.arrayVinculacionesAnual = arrayVinculacionesAnual;
        this.banco_asociado = banco_asociado;
    }

    //FUNCIONES

    /** Esta funcion devuelve el plazo total actual de la hipoteca en meses/numero de cuotas en funcion de las amortizaciones
     *  anticapadas realizadas (afectarían las parciales reduciendo el plazo solo) **/
    public int getPlazoActual(HashMap<Integer, List<Object>> amortizaciones){
        int plazoTotalActual = plazo_anios * 12;

        //int cuotaActual = getNumeroCuotaActual(amortizaciones);

        for (Map.Entry<Integer, List<Object>> entry: amortizaciones.entrySet()) {

            if(entry.getValue().get(0).equals("parcial_plazo")) plazoTotalActual -= (Long) entry.getValue().get(2); //Campo con los meses reducidos
            else if (entry.getValue().get(0).equals("total")) plazoTotalActual = entry.getKey(); //Pones el plazo actual al pago donde se hizo la amortizacion total

        }
        return plazoTotalActual;
    }

    /** Esta funcion devuelve el plazo en un numero de pago**/
    public int getPlazoNumPago(int numPago, HashMap<Integer, List<Object>> amortizaciones){
        int plazo = plazo_anios * 12;

        for(int i = 1; i <= numPago; i++){
            if(amortizaciones.containsKey(i)){
                if(amortizaciones.get(i).get(0).equals("parcial_plazo")) plazo -= (Long) amortizaciones.get(i).get(2);
            }
        }

        return plazo;
    }


    /** Esta funcion devuelve la cuota mensual de una hipoteca en funcion del porcentaje aplicado
     *  y de la cantidad pendiente del prestamo **/
    public double getCuotaMensual(double porcentaje_aplicado, double cantidad_pendiente, int num_cuotas_restantes){

        if (num_cuotas_restantes <= 0) return 0;
        if (porcentaje_aplicado == 0) return cantidad_pendiente / num_cuotas_restantes;
        double aux = Math.pow((1 + (porcentaje_aplicado / 100) / 12), num_cuotas_restantes); //+ cuotasReducidas);
        double cuotaMensual = ((cantidad_pendiente) * ((porcentaje_aplicado / 100) / 12))/(1 -(1 / aux));
        return cuotaMensual;
    }

    /** Funcion que devuelve el interes pagado en funcion del capital que quede por pagar
     *  y un porcentaje aplicado **/
    public double getInteresMensual(double capitalPendiente, double porcentaje_aplicado){
        double interesesMensual = (capitalPendiente * (porcentaje_aplicado / 100) / 12);
        return interesesMensual;
    }

    /** Funcion que devuelve el capital amortizado mensual en funcion del capital pendiente y un
     *  porcentaje aplicado **/
    public double getCapitalAmortizadoMensual(double cuota_mensual, double capitalPendiente, double porcentaje_aplicado){
        double capitalAmortizadoMensual = cuota_mensual - getInteresMensual(capitalPendiente, porcentaje_aplicado);
        return capitalAmortizadoMensual;
    }

    /** Esta funcion devuelve los años y meses que quedan de hipoteca**/
    public ArrayList<Integer> getAniosMesesRestantes(HashMap<Integer, List<Object>> amortizaciones){
        ArrayList<Integer> anios_meses = new ArrayList<>();

        int cuotasRestantes = getPlazoActual(amortizaciones) - getNumeroCuotaActual(amortizaciones);
        int anios = cuotasRestantes / 12;
        int meses = cuotasRestantes % 12;
        anios_meses.add(anios);
        anios_meses.add(meses);

        return anios_meses;
    }

    public String getNombreMesActual(HashMap<Integer, List<Object>> amortizaciones){
        Calendar fechaActual = Calendar.getInstance();
        Calendar inicio = Calendar.getInstance();

        inicio.setTime(fecha_inicio);
        String nombreMesActual = "---";
        if(getNumeroCuotaActual(amortizaciones) <= getPlazoActual(amortizaciones)){
            //Comprobar si ya se ha pagado
            if (fechaActual.get(Calendar.DAY_OF_MONTH) > inicio.get(Calendar.DAY_OF_MONTH)) fechaActual.add(Calendar.MONTH, 1);
            else if (fechaActual.get(Calendar.DAY_OF_MONTH) == inicio.get(Calendar.DAY_OF_MONTH) && fechaActual.after(inicio)) fechaActual.add(Calendar.MONTH, 1);
            nombreMesActual = fechaActual.getDisplayName(Calendar.MONTH, Calendar.LONG, new Locale("es", "ES"));

            nombreMesActual = nombreMesActual.substring(0, 1).toUpperCase() + nombreMesActual.substring(1);
        }
        return nombreMesActual;
    }

    /** Devuelve el numero de cuotas pagadas **/
    public int getNumeroCuotaActual(HashMap<Integer, List<Object>> amortizaciones){
        Calendar inicio = Calendar.getInstance();
        inicio.setTime(fecha_inicio);
        Calendar actual = Calendar.getInstance(); // Dia actual

        // En caso de que todavia no haya empezado el seguimiento de la hipoteca
        if(actual.compareTo(inicio) < 0) return 0;
        int difA = actual.get(Calendar.YEAR) - inicio.get(Calendar.YEAR);
        int numeroPagoActual = difA * 12 + actual.get(Calendar.MONTH) - inicio.get(Calendar.MONTH);

        // Si el dia es el mismo que el de pago, devuelve como si ya ha pagado esa cuota
        if(actual.get(Calendar.DAY_OF_MONTH) >= inicio.get(Calendar.DAY_OF_MONTH)) numeroPagoActual = numeroPagoActual + 1; //Se le sumaria 1 debido a que ya ha pasado el dia de pago del mes correspondiente
        // Fin de hipoteca
        int plazoActual = plazo_anios * 12;

        for (Map.Entry<Integer, List<Object>> entry : amortizaciones.entrySet()) {
            if (entry.getKey() <= numeroPagoActual) {
                if (entry.getValue().get(0).equals("parcial_plazo"))
                    plazoActual -= (Long) entry.getValue().get(2); //Campo con los meses reducidos
                else if (entry.getValue().get(0).equals("total"))
                    plazoActual = entry.getKey(); //Pones el plazo actual al pago donde se hizo la amortizacion total
            }
        }

        //Solo para fin de hipoteca
        if(numeroPagoActual >= plazoActual) numeroPagoActual = plazoActual;
        return numeroPagoActual;
    }

    /** Funcion que devuelve el numero de cuota de la hipoteca en enero del anio pasado por parametros **/
    public int getNumeroCuotaEnEnero(int anio){
        Calendar inicio = Calendar.getInstance();
        inicio.setTime(fecha_inicio);
        // 1 de Enero del anio dado por parametros
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, anio);
        cal.set(Calendar.MONTH, 0);
        cal.set(Calendar.DAY_OF_MONTH, 1);

        // En caso de que todavia no haya empezado el seguimiento de la hipoteca
        int difA = cal.get(Calendar.YEAR) - inicio.get(Calendar.YEAR);
        int numeroPagoActual = difA * 12 + cal.get(Calendar.MONTH) - inicio.get(Calendar.MONTH);

        // Si el dia es el mismo que el de pago, devuelve como si ya ha pagado esa cuota
        if(cal.get(Calendar.DAY_OF_MONTH) >= inicio.get(Calendar.DAY_OF_MONTH)) numeroPagoActual = numeroPagoActual + 1; //Se le sumaria 1 debido a que ya ha pasado el dia de pago del mes correspondiente
        if(inicio.get(Calendar.DAY_OF_MONTH) == 1) return numeroPagoActual;
        return numeroPagoActual + 1;
    }

    public int aniosActualesHipoteca(int plazoActual){
        int aniosHipoteca = 0;
        Calendar inicio = Calendar.getInstance();
        inicio.setTime(fecha_inicio);
        plazoActual -= (12 + getNumeroCuotaEnEnero(inicio.get(Calendar.YEAR)) - 1);
        int aniosCompletos = 0;
        while(plazoActual >= 12){
            plazoActual -= 12;
            aniosCompletos++;
        }
        aniosHipoteca = plazoActual == 0 ? aniosCompletos + 1 : aniosCompletos + 2;

        return aniosHipoteca;
    }

    //FUNCIONES SOBREESCRITAS
    public double getCapitalPendienteTotalActual(int numero_pago, HashMap<Integer, List<Object>> amortizaciones, List<Double> euribors){
        return 0;
    }

    public double getInteresesHastaNumPago(int numero_pago, HashMap<Integer, List<Object>> amortizaciones, List<Double> euribors){ return 0; }
    public boolean siguienteCuotaRevision(HashMap<Integer, List<Object>> amortizaciones){ return false; }

    public ArrayList<Double> getFilaCuadroAmortizacionMensual(int numCuota, HashMap<Integer, List<Object>> amortizaciones, List<Double> euribors){ return null; }
    public ArrayList<Double> getFilaCuadroAmortizacionAnual(int anio, int num_anio, HashMap<Integer, List<Object>> amortizaciones, List<Double> euribors){ return null; }

    public double getPorcentajePorCuota(int numCuota, HashMap<Integer, List<Object>> amortizaciones, List<Double> euribors){ return 0; }

    public double cogerCuotaActual(int num_cuota, HashMap<Integer, List<Object>> amortizaciones, List<Double> euribors){return 0;}

    //GETTERS Y SETTERS SOBREESCRITOS
    public double getPorcentaje_fijo() {
        return 0;
    }

    public int getDuracion_primer_porcentaje_variable(){ return 0; }

    public double getPrimer_porcentaje_variable(){ return 0; }

    public double getPorcentaje_diferencial_variable(){ return 0; }

    public boolean isRevision_anual() { return false; }

    public int getAnios_fija_mixta() {
        return 0;
    }

    public double getPorcentaje_fijo_mixta() {
        return 0f;
    }

    public double getPorcentaje_diferencial_mixta() {
        return 0;
    }

    //GETTERS
    public String getNombre() {
        return nombre;
    }

    public String getComunidad_autonoma() {
        return comunidad_autonoma;
    }

    public String getTipo_vivienda() {
        return tipo_vivienda;
    }

    public String getAntiguedad_vivienda() {
        return antiguedad_vivienda;
    }

    public double getPrecio_vivienda() {
        return precio_vivienda;
    }

    public double getCantidad_abonada() {
        return cantidad_abonada;
    }

    public int getPlazo_anios() {
        return plazo_anios;
    }

    public Date getFecha_inicio() { return fecha_inicio; }

    public String getTipo_hipoteca() {
        return tipo_hipoteca;
    }

    public double getTotalGastos() {
        return totalGastos;
    }

    public List<Double> getArrayVinculacionesAnual(){ return arrayVinculacionesAnual; }

    public Double getPosArrayVinculacionesAnual(int i) {
        if(arrayVinculacionesAnual.size() == 0) return 0.0;
        return Double.parseDouble(String.valueOf(arrayVinculacionesAnual.get(i)));
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public String getBanco_asociado() {
        return banco_asociado;
    }

    //SETTERS
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setComunidad_autonoma(String comunidad_autonoma) {
        this.comunidad_autonoma = comunidad_autonoma;
    }

    public void setTipo_vivienda(String tipo_vivienda) {
        this.tipo_vivienda = tipo_vivienda;
    }

    public void setAntiguedad_vivienda(String antiguedad_vivienda) {
        this.antiguedad_vivienda = antiguedad_vivienda;
    }

    public void setPrecio_vivienda(double precio_vivienda) {
        this.precio_vivienda = precio_vivienda;
    }

    public void setCantidad_abonada(double cantidad_abonada) {
        this.cantidad_abonada = cantidad_abonada;
    }

    public void setPlazo_anios(int plazo_anios) {
        this.plazo_anios = plazo_anios;
    }

    public void setFecha_inicio(Date fecha_inicio) {
        this.fecha_inicio = fecha_inicio;
    }

    public void setTipo_hipoteca(String tipo_hipoteca) {
        this.tipo_hipoteca = tipo_hipoteca;
    }

    public void setTotalGastos(double totalGastos) {
        this.totalGastos = totalGastos;
    }

    public void setPosArrayVinculacionesAnual(int i, double vinculaciones) {
        this.arrayVinculacionesAnual.set(i, vinculaciones);
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public void setBanco_asociado(String banco_asociado) {
        this.banco_asociado = banco_asociado;
    }

    public double getDineroRestanteActual(int i, HashMap<Integer, List<Object>> amortizaciones, List<Double> euribors) { return 0;}

    public double getPorcentajeUltimaCuota(HashMap<Integer, List<Object>> amortizaciones, List<Double> euribors){ return 0; }

    public double getEuriborActual(List<Double> euribors){ return euribors.get(euribors.size() - 1); }

    //Calcula el euribor que hubo correspondiente a un numero de pago

    public double getEuriborPasado(int numPago, List<Double> euribors){
        if (numPago == 0) numPago = 1;

        int mesesRevision = isRevision_anual() ? 12 : 6;

        int pago;
        if (numPago % mesesRevision == 0)  pago = numPago - mesesRevision;
        else pago = numPago - numPago % mesesRevision;
        if (pago >= euribors.size()) return euribors.get(euribors.size() - 1);
        return euribors.get(pago);
    }

    public List<Object> getAnioYMesDeUnPago(int numPago, HashMap<Integer, List<Object>> amortizaciones){
        List<Object> anioMes = new ArrayList<>();
        String[] mesesNombre = new String[]{"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};


        Calendar inicio = Calendar.getInstance();

        inicio.setTime(fecha_inicio);
        if(numPago <= getPlazoActual(amortizaciones)){
            int meses = inicio.get(Calendar.MONTH) + numPago - 1;
            int anio = inicio.get(Calendar.YEAR);
            while(meses > 11){
                anio++;
                meses -= 12;
            }
            anioMes.add(anio);
            anioMes.add(mesesNombre[meses]);
        }

        return anioMes;
    }
}
