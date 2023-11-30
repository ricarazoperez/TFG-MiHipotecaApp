package es.MiHipotecaApp.TFG.Transfers;

import java.io.Serializable;

public class Usuario implements Serializable {

    private String correo;
    private String password;
    private String nombre;
    private boolean premium;
    private int avatar;

    public Usuario() {

    }

    public Usuario(String correo, String password, String nombre, boolean premium, int avatar) {
        this.correo = correo;
        this.password = password;
        this.nombre = nombre;
        this.premium = premium;
        this.avatar = avatar;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }

    public int getAvatar() {
        return avatar;
    }

    public void setAvatar(int avatar) {
        this.avatar = avatar;
    }


    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
