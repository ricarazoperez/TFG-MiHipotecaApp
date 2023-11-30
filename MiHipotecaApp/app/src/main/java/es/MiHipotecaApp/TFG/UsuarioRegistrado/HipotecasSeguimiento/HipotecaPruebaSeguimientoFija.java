package es.MiHipotecaApp.TFG.UsuarioRegistrado.HipotecasSeguimiento;

import android.util.Log;

import java.util.ArrayList;

public class HipotecaPruebaSeguimientoFija {

    private float capitalTotal, capitalAbonado, porcentajeIntAnual, vinculacionesMensuales;
    private int numAnios;


    public HipotecaPruebaSeguimientoFija(float capitalTotal, float capitalAbonado, float porcentajeIntAnual, float vinculacionesMensuales, int numAnios) {
        this.capitalTotal = capitalTotal;
        this.capitalAbonado = capitalAbonado;
        this.porcentajeIntAnual = porcentajeIntAnual;
        this.vinculacionesMensuales = vinculacionesMensuales;
        this.numAnios = numAnios;
        capitalInicial();
        interesAnualInicial();
        numTotalPagos();
        cuotaMensual();
    }

    //El capital va variando "mensualmente"
    private float capital, interesesAnuales, cuotaMensual;
    private int numPagos;

    private float totalIntereses = 0, totalPagado = 0, totalAmortizado = 0, totalVinculaciones;

    public float getCapitalTotal() {
        return capitalTotal;
    }

    public float getCapitalAbonado() {
        return capitalAbonado;
    }

    public float getPorcentajeIntAnual() {
        return porcentajeIntAnual;
    }

    public float getVinculacionesMensuales() {
        return vinculacionesMensuales;
    }

    public int getNumAnios() {
        return numAnios;
    }

    public float getCapital() {
        return capital;
    }

    public float getInteresesAnuales() {
        return interesesAnuales;
    }

    public float getCuotaMensual() {
        return cuotaMensual;
    }

    public int getNumPagos() {
        return numPagos;
    }

    public float getTotalIntereses() {
        return totalIntereses;
    }

    public float getTotalPagado() {
        return totalPagado;
    }

    public float getTotalAmortizado() {
        return totalAmortizado;
    }

    public float getTotalVinculaciones() {
        return totalVinculaciones;
    }

    public void setCapitalTotal(float capitalTotal) {
        this.capitalTotal = capitalTotal;
    }

    public void setCapitalAbonado(float capitalAbonado) {
        this.capitalAbonado = capitalAbonado;
    }

    public void setPorcentajeIntAnual(float porcentajeIntAnual) {
        this.porcentajeIntAnual = porcentajeIntAnual;
    }

    public void setVinculacionesMensuales(float vinculacionesMensuales) {
        this.vinculacionesMensuales = vinculacionesMensuales;
    }

    public void setNumAnios(int numAnios) {
        this.numAnios = numAnios;
    }

    public void capitalInicial(){
        capital = capitalTotal - capitalAbonado;
    }

    public void interesAnualInicial(){
        interesesAnuales = (float) Math.pow(1 + porcentajeIntAnual/100f, 1/12f) - 1;
    }

    public void numTotalPagos(){
        numPagos = numAnios * 12;
    }

    public void cuotaMensual(){
        float aux = (float) Math.pow(1+ interesesAnuales, -numPagos);
        cuotaMensual = (capital * interesesAnuales) / (1- aux);
    }

    public float interesesPorMes(){
        return capital*interesesAnuales;
    }

    public float capitalAmortPorMes(){
        return cuotaMensual-interesesPorMes();
    }

    public void saldoDeuda(){
        capital = capital-capitalAmortPorMes();
    }

    public void totalVinculaciones(){
        totalVinculaciones = vinculacionesMensuales*numPagos;
    }
    public void calcularHipoteca(){
        for(int i = 0; i < numPagos;i++){
            totalIntereses += interesesPorMes();
            totalAmortizado += capitalAmortPorMes();
            saldoDeuda();
        }
        totalVinculaciones();
        totalPagado = totalAmortizado + totalIntereses;
        Log.e("", "Intereses totales: " + totalIntereses);
        Log.e("", "Amortizado total: " + totalAmortizado);
        Log.e("", "Total pago: " + totalPagado);

    }

    public ArrayList getInteresesPorAnio(){
        ArrayList<Float> array = new ArrayList<Float>();
        totalIntereses = 0;
        for(int i = 1; i <= numPagos; i++){
            totalIntereses += interesesPorMes();
            if(i % 12 == 0){
                if(i != 12) {
                    totalIntereses -= array.get((i/12) - 2);
                }
                array.add(totalIntereses);
            }
        }


        return array;
    }
}