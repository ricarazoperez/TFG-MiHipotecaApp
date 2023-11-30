package es.MiHipotecaApp.TFG.Transfers;

import org.json.JSONArray;

public class Oferta {
    private String banco;
    private String desc;
    private String tin;
    private String tae;
    private String cuota;

    private String cuota_x;
    private String cuota_resto;

    private String tin_x;
    private String tin_resto;
    private String vinculaciones;
    private String nombre ;
    private boolean guardada;

    private int cuota_num;
    private double tae_num;



    public Oferta(String banco, String desc, String tin, String tae, String cuota) {
        this.banco = banco;
        this.desc = desc;
        this.tin = tin;
        this.tae = tae;
        this.cuota = cuota;
        this.guardada = false;
    }
    public Oferta(String banco, String desc, String tin_x, String tin_resto, String tae, String cuota_x,String cuota_resto) {
        this.banco = banco;
        this.desc = desc;
        this.tin_x = tin_x;
        this.tin_resto = tin_resto;
        this.tae = tae;
        this.cuota_x = cuota_x;
        this.cuota_resto = cuota_resto;
        this.guardada = false;
    }
    public Oferta(String banco, String desc, String tin, String tae, String cuota, String vinculaciones) {
        this.banco = banco;
        this.desc = desc;
        this.tin = tin;
        this.tae = tae;
        this.cuota = cuota;
        this.vinculaciones = vinculaciones;
        this.guardada  = false;
    }
    public Oferta(String banco, String desc, String tin_x, String tin_resto, String tae, String cuota_x,String cuota_resto,String vinculaciones) {
        this.banco = banco;
        this.desc = desc;
        this.tin_x = tin_x;
        this.tin_resto = tin_resto;
        this.tae = tae;
        this.cuota_x = cuota_x;
        this.cuota_resto = cuota_resto;
        this.vinculaciones = vinculaciones;
        this.guardada = false;
    }

    public int getCuota_num(){
        return cuota_num;
    }

    public void setCuota_num(int cuota_num) {
        this.cuota_num = cuota_num;
    }

    public double getTae_num() {
        return tae_num;
    }

    public void setTae_num(double tae_num) {
        this.tae_num = tae_num;
    }

    public String getVinculaciones() {
        return vinculaciones;
    }

    public void setVinculaciones(String vinculaciones) {
        this.vinculaciones = vinculaciones;
    }

    public boolean isGuardada() {
        return guardada;
    }

    public void setGuardada(boolean guardada) {
        this.guardada = guardada;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
         this.nombre = nombre;
    }
    public String getCuota_x() {
        return cuota_x;
    }

    public void setCuota_x(String cuota_x) {
        this.cuota_x = cuota_x;
    }

    public String getCuota_resto() {
        return cuota_resto;
    }

    public void setCuota_resto(String cuota_resto) {
        this.cuota_resto = cuota_resto;
    }

    public String getTin_x() {
        return tin_x;
    }

    public void setTin_x(String tin_x) {
        this.tin_x = tin_x;
    }

    public String getTin_resto() {
        return tin_resto;
    }

    public void setTin_resto(String tin_resto) {
        this.tin_resto = tin_resto;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String tipo) {
        this.desc = desc;
    }

    public String getTin() {
        return tin;
    }

    public void setTin(String tin) {
        this.tin = tin;
    }

    public String getTae() {
        return tae;
    }

    public void setTae(String tae) {
        this.tae = tae;
    }
    public String getCuota() {return cuota;}

    public void setCuota(String cuota) {
        this.cuota = cuota;
    }
}
