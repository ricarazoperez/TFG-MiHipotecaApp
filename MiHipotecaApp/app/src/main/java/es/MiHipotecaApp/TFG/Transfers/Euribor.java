package es.MiHipotecaApp.TFG.Transfers;

public class Euribor {




    private String anio;

    private String mes;

    private Double valor;

    public Euribor(String  anio,String mes,Double valor) {
        this.anio=anio;
        this.mes=mes;
        this.valor=valor;

    }
    public Euribor(Double valor){

        this.valor=valor;
    }
    public String getAnio() {
        return anio;
    }

    public String getMes() {
        return mes;
    }

    public Double getValor() {
        return valor;
    }
    public void setAnio(String anio) {
        this.anio = anio;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

}
