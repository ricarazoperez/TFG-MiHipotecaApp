package es.MiHipotecaApp.TFG.UsuarioRegistrado.HipotecasSeguimiento;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import es.MiHipotecaApp.TFG.PaginaPrincipal;
import es.MiHipotecaApp.TFG.R;
import es.MiHipotecaApp.TFG.Transfers.HipotecaSegFija;
import es.MiHipotecaApp.TFG.Transfers.HipotecaSegMixta;
import es.MiHipotecaApp.TFG.Transfers.HipotecaSegVariable;
import es.MiHipotecaApp.TFG.Transfers.HipotecaSeguimiento;

public class NuevoSeguimiento extends AppCompatActivity {
    private final String TAG = "SEGUIMIENTO ACTIVITY";

    //Campos fijos
    private CircleImageView closeIcon;
    private Spinner sp_comunidad;
    private CheckBox check_vivienda_general;
    private CheckBox check_vivienda_poficial;
    private CheckBox check_vivienda_nueva;
    private CheckBox check_vivienda_smano;
    private CheckBox check_parte_fija_variable;
    private EditText precio_vivienda;
    private EditText cantidad_abonada;
    private EditText plazo;
    //private DatePicker inicio_hipoteca;
    private EditText inicio_hipoteca;

    private CheckBox check_fija;
    private CheckBox check_variable;
    private CheckBox check_mixta;
    private EditText gastos_notaria;
    private EditText gastos_registro;
    private EditText gastos_gestoria;
    private EditText gastos_tasacion;

    private EditText gastos_vinculaciones;
    private EditText gastos_comisiones;
    private EditText nombre_hipoteca;

    //campos variables
    //Hipoteca Fija
    private TextView label_porcentaje_fijo;
    private EditText edit_porcentaje_fijo;
    //Hipoteca Variable
    private TextView label_duracion_primer_porcentaje;
    private EditText edit_duracion_primer_porcentaje;
    private TextView label_primer_porcentaje;
    private EditText edit_primer_porcentaje;
    private TextView label_diferencial_variable;
    private EditText edit_diferencial_variable;
    //Hipoteca mixta
    private TextView label_anios_fija;
    private EditText edit_anios_fija;
    private TextView label_porcentaje_fijo_mix;
    private EditText edit_porcentaje_fijo_mix;
    private TextView label_diferencial_mixto;
    private EditText edit_diferencial_mixto;
    //Campos comun a mixta y variable
    private TextView label_cuando_revision;
    private CheckBox check_seis_meses;
    private CheckBox check_un_anio;
    private Button btn_anadir_hipoteca;
    private Spinner sp_bancos;

    //Bases de datos

    private FirebaseFirestore db;

    private FirebaseAuth auth;
    private FirebaseUser user;

    private String[] comunidades;
    private String[] comunidades_base_datos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_seguimiento);
        sp_comunidad = findViewById(R.id.sp_comunidad);
        comunidades = new String[]{"Andalucía", "Aragón", "Asturias", "Baleares", "Canarias", "Cantabria", "Castilla La Mancha", "Castilla León", "Cataluña", "Ceuta", "Comunidad de Madrid", "Comunidad Valenciana", "Extremadura", "Galicia", "La Rioja", "Melilla", "Murcia", "Navarra", "País Vasco"};
        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, comunidades){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                ((TextView) view).setTextColor(getResources().getColor(R.color.grey_color));
                return view;
            }    @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                ((TextView) view).setTextColor(getResources().getColor(R.color.background_aplicacion));
                return view;
            }
        };;
        sp_comunidad.setAdapter(adapter);
        comunidades_base_datos = new String[]{"Andalucía", "Aragón", "Asturias", "Baleares", "Canarias", "Cantabria", "Castilla_La_Mancha", "Castilla_León", "Cataluña", "Ceuta", "Madrid", "Comunidad_Valenciana", "Extremadura", "Galicia", "La_Rioja", "Melilla", "Murcia", "Navarra", "País_Vasco"};

        initUI();
        Eventos();
        db   = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
    }

    private void initUI() {
        //Campos fijos
        closeIcon = findViewById(R.id.close_icon_seg);
        check_vivienda_general = findViewById(R.id.checkBox_ViviendaRegGeneral);
        check_vivienda_poficial= findViewById(R.id.checkBox_ViviendaPOficial);
        check_vivienda_nueva= findViewById(R.id.checkBox_ViviendaNueva);
        check_vivienda_smano=findViewById(R.id.checkBox_ViviendaSegundaMano);
        precio_vivienda= findViewById(R.id.edit_precio_vivienda);
        cantidad_abonada=findViewById(R.id.edit_cant_abonada_comprador);
        plazo=findViewById(R.id.edit_plazo_pagar);
        inicio_hipoteca = findViewById(R.id.inicio_hipoteca);
        check_fija=findViewById(R.id.checkBoxFijaNuevoSeg);
        check_variable=findViewById(R.id.checkBoxVariableNuevoSeg);
        check_mixta=findViewById(R.id.checkBoxMixtaNuevoSeg);
        check_parte_fija_variable=findViewById(R.id.check_parte_fija_variable);
        gastos_notaria=findViewById(R.id.edit_gastos_notaria);
        gastos_registro=findViewById(R.id.edit_gastos_registro);
        gastos_gestoria=findViewById(R.id.edit_gastos_gestoria);
        gastos_tasacion=findViewById(R.id.edit_gastos_tasacion);
        gastos_vinculaciones=findViewById(R.id.edit_gastos_vinculaciones);
        gastos_comisiones=findViewById(R.id.edit_comisiones);
        nombre_hipoteca=findViewById(R.id.edit_nombre_hipoteca);

        //campos variables
        //Hipoteca Fija
        label_porcentaje_fijo=findViewById(R.id.label_porcentaje_fijo);
        edit_porcentaje_fijo=findViewById(R.id.edit_porcentaje_fijo);
        //Hipoteca Variable
        label_duracion_primer_porcentaje=findViewById(R.id.label_duracion_primer_porcentaje_variable);
        edit_duracion_primer_porcentaje=findViewById(R.id.edit_duracion_primer_porcentaje_variable);
        label_primer_porcentaje=findViewById(R.id.label_primer_porcentaje_variable);
        edit_primer_porcentaje=findViewById(R.id.edit_primer_porcentaje_variable);
        label_diferencial_variable=findViewById(R.id.label_diferencial_variable);
        edit_diferencial_variable=findViewById(R.id.edit_porcentaje_diferencial_variable);
        //Hipoteca mixta
        label_anios_fija=findViewById(R.id.label_cuantos_anios_fijo_mixta);
        edit_anios_fija=findViewById(R.id.edit_anios_fijos_mixta);
        label_porcentaje_fijo_mix=findViewById(R.id.label_porcentaje_fijo_mixta);
        edit_porcentaje_fijo_mix=findViewById(R.id.edit_porcentaje_fijo_mixta);
        label_diferencial_mixto=findViewById(R.id.label_diferencial_mixta);
        edit_diferencial_mixto=findViewById(R.id.edit_porcentaje_diferencial_mixta);
        //Campos comun a mixta y variable
        label_cuando_revision=findViewById(R.id.label_cada_cuanto_revision);
        check_seis_meses=findViewById(R.id.checkBox_revision_seis_meses);
        check_un_anio=findViewById(R.id.checkBox_revision_un_anio);

        btn_anadir_hipoteca = findViewById(R.id.btn_anadir_hipoteca);

        sp_bancos = findViewById(R.id.sp_banco_nuevo_seg);
        String [] bancos = {"ING","SANTANDER","BBVA","CAIXABANK","BANKINTER","EVO BANCO","SABADELL","UNICAJA","DEUTSCHE BANK","OPEN BANK","KUTXA BANK","IBERCAJA","ABANCA"};
        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item,bancos){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                ((TextView) view).setTextColor(getResources().getColor(R.color.grey_color));
                return view;
            }    @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                ((TextView) view).setTextColor(getResources().getColor(R.color.background_aplicacion));
                return view;
            }
        };
        sp_bancos.setAdapter(adapter);

    }

    @Override
    public void onBackPressed() {
        AlertDialog dialogo = new AlertDialog.Builder(this)
                .setPositiveButton(getString(R.string.si_eliminar_cuenta), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton(getString(R.string.no_eliminar_cuenta), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setTitle("CANCELAR NUEVO SEGUIMIENTO").setMessage("¿Desea dejar de crear nuevo seguimeinto? Perderá todo su progreso").create();
        dialogo.show();
    }

    private void Eventos() {
        closeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialogo = new AlertDialog.Builder(NuevoSeguimiento.this)
                        .setPositiveButton(getString(R.string.si_eliminar_cuenta), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setNegativeButton(getString(R.string.no_eliminar_cuenta), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setTitle("CANCELAR NUEVO SEGUIMIENTO").setMessage("¿Desea dejar de crear nuevo seguimiento? Perderá todo su progreso").create();
                dialogo.show();
            }
        });
        check_vivienda_general.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) check_vivienda_poficial.setChecked(false);
                if(!check_vivienda_poficial.isChecked()) check_vivienda_general.setChecked(true);
            }
        });

        check_vivienda_poficial.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) check_vivienda_general.setChecked(false);
                if(!check_vivienda_general.isChecked()) check_vivienda_poficial.setChecked(true);
            }
        });
        check_vivienda_nueva.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) check_vivienda_smano.setChecked(false);
                if(!check_vivienda_smano.isChecked()) check_vivienda_nueva.setChecked(true);
            }
        });

        check_vivienda_smano.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) check_vivienda_nueva.setChecked(false);
                if(!check_vivienda_nueva.isChecked()) check_vivienda_smano.setChecked(true);
            }
        });
        check_fija.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    check_mixta.setChecked(false);
                    check_variable.setChecked(false);
                    ActivarCampos("fija");
                    check_parte_fija_variable.setChecked(false);
                }
                if(!check_variable.isChecked() && !check_mixta.isChecked()) {
                    check_fija.setChecked(true);
                }
            }
        });

        check_variable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    check_mixta.setChecked(false);
                    check_fija.setChecked(false);
                    ActivarCampos("variable");
                }
                if(!check_fija.isChecked() && !check_mixta.isChecked()) {
                    check_variable.setChecked(true);
                }
            }
        });
        check_mixta.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    check_variable.setChecked(false);
                    check_fija.setChecked(false);
                    ActivarCampos("mixto");
                    check_parte_fija_variable.setChecked(false);
                }
                if(!check_fija.isChecked() && !check_variable.isChecked()) {
                    check_mixta.setChecked(true);
                }
            }

        });

        check_seis_meses.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) check_un_anio.setChecked(false);
                if(!check_un_anio.isChecked()) check_seis_meses.setChecked(true);
            }
        });

        check_un_anio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) check_seis_meses.setChecked(false);
                if(!check_seis_meses.isChecked()) check_un_anio.setChecked(true);
            }
        });


        check_parte_fija_variable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edit_duracion_primer_porcentaje.setVisibility(View.VISIBLE);
                    label_duracion_primer_porcentaje.setVisibility(View.VISIBLE);
                    edit_primer_porcentaje.setVisibility(View.VISIBLE);
                    label_primer_porcentaje.setVisibility(View.VISIBLE);
                } else{
                    edit_duracion_primer_porcentaje.setVisibility(View.GONE);
                    label_duracion_primer_porcentaje.setVisibility(View.GONE);
                    edit_primer_porcentaje.setVisibility(View.GONE);
                    label_primer_porcentaje.setVisibility(View.GONE);
                }
            }
        });

        inicio_hipoteca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        btn_anadir_hipoteca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comprobarCampos();
            }
        });


    }

    /*private void showDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance((datePicker, year, month, day) -> {
            // +1 porque enero es cero
            final String selectedDate =  twoDigits(day) + "/" + twoDigits(month+1) + "/" + fourDigits(year);
            inicio_hipoteca.setText(selectedDate);
        });

        newFragment.show(getSupportFragmentManager(), "datePicker");
    }*/

    private void showDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance((datePicker, year, month, day) -> {
            // +1 porque enero es cero
            final int MIN_YEAR = 1800;
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, MIN_YEAR);
            Date minDate = calendar.getTime();
            calendar.set(Calendar.YEAR, year);
            Date selectedDate = new Date(calendar.getTimeInMillis());

            if (selectedDate.after(minDate)) {
                final String formattedDate =  twoDigits(day) + "/" + twoDigits(month+1) + "/" + fourDigits(year);
                inicio_hipoteca.setText(formattedDate);
            } else Toast.makeText(this, "El año seleccionado debe ser mayor o igual a 1999", Toast.LENGTH_SHORT).show();
        });

        newFragment.show(getSupportFragmentManager(), "datePicker");
    }


    private String twoDigits(int n) {
        return (n<=9) ? ("0"+n) : String.valueOf(n);
    }

    private String fourDigits(int n) {
        if (n<=9)          return ("000" + n);
        else if (n <= 99)  return ("00" + n);
        else if (n <= 999) return ("0"  + n);
        else return String.valueOf(n);
    }


    private void ActivarCampos(String hipoteca){

        switch (hipoteca){
            case "fija" :
                ModificarCamposFija(View.VISIBLE);
                ModificarCamposVariable(View.GONE);
                ModificarCamposMixta(View.GONE);
                ModificarCamposMixtaVariable(View.GONE);


            break;
            case "variable" :
                ModificarCamposFija(View.GONE);
                ModificarCamposVariable(View.VISIBLE);
                ModificarCamposMixta(View.GONE);
                ModificarCamposMixtaVariable(View.VISIBLE);

            break;
            default:
                ModificarCamposFija(View.GONE);
                ModificarCamposVariable(View.GONE);
                ModificarCamposMixta(View.VISIBLE);
                ModificarCamposMixtaVariable(View.VISIBLE);

            break;

        }
    }
    private void ModificarCamposFija(int view) {
        edit_porcentaje_fijo.setVisibility(view);
        label_porcentaje_fijo.setVisibility(view);

    }
    private void ModificarCamposVariable(int view) {
        check_parte_fija_variable.setVisibility(view);
        label_diferencial_variable.setVisibility(view);
        edit_diferencial_variable.setVisibility(view);

    }
    private void ModificarCamposMixta(int view) {
        edit_anios_fija.setVisibility(view);
        label_anios_fija.setVisibility(view);
        label_porcentaje_fijo_mix.setVisibility(view);
        edit_porcentaje_fijo_mix.setVisibility(view);
        label_diferencial_mixto.setVisibility(view);
        edit_diferencial_mixto.setVisibility(view);

    }
    private void ModificarCamposMixtaVariable(int view) {
        label_cuando_revision.setVisibility(view);
        check_seis_meses.setVisibility(view);
        check_un_anio.setVisibility(view);

    }
    private void comprobarCampos(){

        boolean camposCorrectos = true;
        //PRIMERO COMPROBAR CAMPOS COMUNES
        if(TextUtils.isEmpty(precio_vivienda.getText())){
            precio_vivienda.setError(getString(R.string.precio_vacio));
            camposCorrectos = false;
        } else{

            //COMPROBACION CANTIDAD APORTADA POR EL BANCO <= 80%
            double precio_viv = Double.parseDouble(precio_vivienda.getText().toString());
            if(precio_viv <= 0){
                precio_vivienda.setError(getString(R.string.cantidad_mayor_igual_cero));
                camposCorrectos = false;
            }

            if(TextUtils.isEmpty(cantidad_abonada.getText())){
                cantidad_abonada.setError(getString(R.string.cantidad_abonada_vacio));
                camposCorrectos = false;
            } else {
                double ahorro_aport = Double.parseDouble(cantidad_abonada.getText().toString());
                if (ahorro_aport <= 0) {
                    cantidad_abonada.setError(getString(R.string.cantidad_mayor_igual_cero));
                    camposCorrectos = false;
                } else if(precio_viv < ahorro_aport){
                    precio_vivienda.setError(getString(R.string.cantidad_mayor_igual_cero));
                    camposCorrectos = false;
                }
                double dinero_aport_banco = precio_viv - ahorro_aport;
                if (dinero_aport_banco > precio_viv * 0.8) {
                    cantidad_abonada.setError(getString(R.string.ahorro_mayor_80_por_ciento));
                    camposCorrectos = false;
                }
            }
        }

        if (TextUtils.isEmpty(inicio_hipoteca.getText())) {
            inicio_hipoteca.setError("Debe seleccionar una fecha de inicio para la hipoteca");
            camposCorrectos = false;
        }else if(Integer.parseInt(inicio_hipoteca.getText().toString().substring(6, 10)) <  1999){
            inicio_hipoteca.setError("La hipoteca no puede ser anterior a 1999");
            camposCorrectos = false;
        }

        if(TextUtils.isEmpty(plazo.getText())){
            plazo.setError(getString(R.string.plazo_vacio));
            camposCorrectos = false;
        } else {
            if (Integer.parseInt(plazo.getText().toString()) <= 0 || Integer.parseInt(plazo.getText().toString()) > 40) {
                plazo.setError(getString(R.string.plazo_vacio));
                camposCorrectos = false;
            }
        }

        //CAMPOS FIJOS
        if(check_fija.isChecked()){
            if(TextUtils.isEmpty(edit_porcentaje_fijo.getText())){
                edit_porcentaje_fijo.setError(getString(R.string.porcentaje_vacio));
                camposCorrectos = false;
            } else {
                if (Double.parseDouble(edit_porcentaje_fijo.getText().toString()) <= 0) {
                    edit_porcentaje_fijo.setError(getString(R.string.porcentaje_mayor_igual_cero));
                    camposCorrectos = false;
                }
            }
        } //CAMPOS VARIABLES
        else if(check_variable.isChecked()){
            if(check_parte_fija_variable.isChecked()) {
                if (TextUtils.isEmpty(edit_duracion_primer_porcentaje.getText())) {
                    edit_duracion_primer_porcentaje.setError(getString(R.string.duracion_vacio));
                    camposCorrectos = false;
                } else {
                    if (Integer.parseInt(edit_duracion_primer_porcentaje.getText().toString()) <= 0) {
                        edit_duracion_primer_porcentaje.setError(getString(R.string.duracion_mayor_igual_cero));
                        camposCorrectos = false;
                    } else if(Integer.parseInt(edit_duracion_primer_porcentaje.getText().toString()) > (Integer.parseInt(plazo.getText().toString()) - 1) * 12){
                        edit_duracion_primer_porcentaje.setError(getString(R.string.meses_menor_plazo));
                        camposCorrectos = false;
                    }
                }
                if (TextUtils.isEmpty(edit_primer_porcentaje.getText())) {
                    edit_primer_porcentaje.setError(getString(R.string.porcentaje_vacio));
                    camposCorrectos = false;
                } else {
                    if (Double.parseDouble(edit_primer_porcentaje.getText().toString()) <= 0) {
                        edit_primer_porcentaje.setError(getString(R.string.porcentaje_mayor_igual_cero));
                        camposCorrectos = false;
                    }
                }
            }
            if (TextUtils.isEmpty(edit_diferencial_variable.getText())) {
                edit_diferencial_variable.setError(getString(R.string.porcentaje_vacio));
                camposCorrectos = false;
            } else {
                if (Double.parseDouble(edit_diferencial_variable.getText().toString()) <= 0) {
                    edit_diferencial_variable.setError(getString(R.string.porcentaje_mayor_igual_cero));
                    camposCorrectos = false;
                }
            }
        } //CAMPOS MIXTOS
        else{
            if(TextUtils.isEmpty(edit_anios_fija.getText())){
                edit_anios_fija.setError(getString(R.string.duracion_vacio));
                camposCorrectos = false;
            } else {
                if (Integer.parseInt(edit_anios_fija.getText().toString()) <= 0) {
                    edit_anios_fija.setError(getString(R.string.anios_mayor_cero));
                    camposCorrectos = false;
                } else if(Integer.parseInt(edit_anios_fija.getText().toString()) > Integer.parseInt(plazo.getText().toString()) - 1){
                    edit_anios_fija.setError(getString(R.string.anios_menor_plazo));
                    camposCorrectos = false;
                }
            }
            if(TextUtils.isEmpty(edit_porcentaje_fijo_mix.getText())){
                edit_porcentaje_fijo_mix.setError(getString(R.string.porcentaje_vacio));
                camposCorrectos = false;
            } else {
                if (Double.parseDouble(edit_porcentaje_fijo_mix.getText().toString()) <= 0) {
                    edit_porcentaje_fijo_mix.setError(getString(R.string.porcentaje_mayor_igual_cero));
                    camposCorrectos = false;
                }
            }
            if(TextUtils.isEmpty(edit_diferencial_mixto.getText())){
                edit_diferencial_mixto.setError(getString(R.string.porcentaje_vacio));
                camposCorrectos = false;
            } else {
                if (Double.parseDouble(edit_diferencial_mixto.getText().toString()) <= 0) {
                    edit_diferencial_mixto.setError(getString(R.string.porcentaje_mayor_igual_cero));
                    camposCorrectos = false;
                }
            }
        }
        if(TextUtils.isEmpty(nombre_hipoteca.getText())){
            nombre_hipoteca.setError(getString(R.string.nombre_hipoteca_vacio));
            camposCorrectos = false;
        }
        else {
            if(camposCorrectos){
                Query hipotecas_usuario =  db.collection("hipotecas_seguimiento").whereEqualTo("idUsuario",auth.getCurrentUser().getUid()).whereEqualTo("nombre",nombre_hipoteca.getText().toString());
                //COMPROBACION DE QUE NO INTRODUCE UNA HIPOTECA CON EL MISMO NOMBRE EL MISMO USUARIO
                hipotecas_usuario.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            if(!task.getResult().isEmpty() && task.getResult() != null)nombre_hipoteca.setError("Una de sus hipotecas ya tiene este nombre");
                            else registrarNuevaHipoteca();
                        }else Log.d(TAG, "Error en query hipotecas seg por nombre", task.getException());
                    }
                });
            }else return;
        }
    }

    private void registrarNuevaHipoteca(){

        String nombre = nombre_hipoteca.getText().toString();
        String comunidad = comunidades_base_datos[sp_comunidad.getSelectedItemPosition()];
        String tipo_vivienda = "general";
        if(check_vivienda_poficial.isChecked()) tipo_vivienda = "proteccion_oficial";
        String antiguedad_vivienda = "nueva";
        if(check_vivienda_smano.isChecked()) antiguedad_vivienda = "segunda_mano";
        double precio_viv = Double.parseDouble(precio_vivienda.getText().toString());
        double cant_abonada = Double.parseDouble(cantidad_abonada.getText().toString());
        int plazo_hip = Integer.parseInt(plazo.getText().toString());

        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        Date fecha_inicio;
        try {
            fecha_inicio = formato.parse(inicio_hipoteca.getText().toString());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        /*
        int year = inicio_hipoteca.getYear();
        int month = inicio_hipoteca.getMonth();
        int dayOfMonth = inicio_hipoteca.getDayOfMonth();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        Date fecha_inicio = calendar.getTime();*/


        double gastos_gest, gastos_not, gastos_reg, gastos_tas, gastos_vin, gastos_com;
        if(TextUtils.isEmpty(gastos_gestoria.getText())) gastos_gest = 0;
        else gastos_gest = Double.parseDouble(gastos_gestoria.getText().toString());
        if(TextUtils.isEmpty(gastos_notaria.getText())) gastos_not = 0;
        else gastos_not = Double.parseDouble(gastos_notaria.getText().toString());
        if(TextUtils.isEmpty(gastos_registro.getText())) gastos_reg = 0;
        else gastos_reg = Double.parseDouble(gastos_registro.getText().toString());
        if(TextUtils.isEmpty(gastos_tasacion.getText())) gastos_tas = 0;
        else gastos_tas = Double.parseDouble(gastos_tasacion.getText().toString());

        List<Double> arrayVinculacionesAnuales = new ArrayList<>();
        if(TextUtils.isEmpty(gastos_vinculaciones.getText())) gastos_vin = 0;
        else gastos_vin = Double.parseDouble(gastos_vinculaciones.getText().toString());

        //calculo la variable aniosHastaAhora, que tiene el numero de años + 1  que llevamos de hipoteca
        int aniosHastaAhora = plazo_hip - getAniosMesesRestantes(plazo_hip, getNumeroCuotaActual(fecha_inicio, plazo_hip)).get(0);
        if(aniosHastaAhora > 1){
            for(int i = 0; i < aniosHastaAhora; i++){
                arrayVinculacionesAnuales.add(gastos_vin);
            }
        }

        if(TextUtils.isEmpty(gastos_comisiones.getText())) gastos_com = 0;
        else gastos_com = Double.parseDouble(gastos_comisiones.getText().toString());
        double totalGastos = gastos_gest + gastos_not + gastos_reg + gastos_tas + gastos_com;
        String banco_asociado = sp_bancos.getSelectedItem().toString();
        HipotecaSeguimiento nuevaHip;
        //Hipoteca Fija
        String tipo_hipoteca = "fija";
        if(check_fija.isChecked()){
            double porcentaje_fijo = Double.parseDouble(edit_porcentaje_fijo.getText().toString());

            nuevaHip = new HipotecaSegFija(nombre, comunidad, tipo_vivienda, antiguedad_vivienda, precio_viv, cant_abonada, plazo_hip, fecha_inicio, tipo_hipoteca, totalGastos, arrayVinculacionesAnuales, banco_asociado, porcentaje_fijo);

            nuevaHip.setTipo_hipoteca(tipo_hipoteca);
            nuevaHip.setIdUsuario(user.getUid());
        }else if (check_variable.isChecked()){
            tipo_hipoteca = "variable";
            int duracion_primer_porcentaje = check_parte_fija_variable.isChecked() ? Integer.parseInt(edit_duracion_primer_porcentaje.getText().toString()) : 0;
            double primer_porc_variable = check_parte_fija_variable.isChecked() ? Double.parseDouble(edit_primer_porcentaje.getText().toString()) : 0;

            double diferencial_variable = Double.parseDouble(edit_diferencial_variable.getText().toString());
            boolean revision_anual = true;
            if(check_seis_meses.isChecked()) revision_anual = false;

            nuevaHip = new HipotecaSegVariable(nombre, comunidad, tipo_vivienda, antiguedad_vivienda, precio_viv, cant_abonada, plazo_hip, fecha_inicio, tipo_hipoteca, totalGastos, arrayVinculacionesAnuales, banco_asociado, duracion_primer_porcentaje, primer_porc_variable, diferencial_variable, revision_anual);

            nuevaHip.setTipo_hipoteca(tipo_hipoteca);
            nuevaHip.setIdUsuario(user.getUid());
        }else{
            tipo_hipoteca = "mixta";
            boolean revision_anual = true;
            if(check_seis_meses.isChecked()) revision_anual = false;
            int anios_fijo = Integer.parseInt(edit_anios_fija.getText().toString());
            double porc_fijo_mixta = Double.parseDouble(edit_porcentaje_fijo_mix.getText().toString());
            double porc_diferencial_mixta = Double.parseDouble(edit_diferencial_mixto.getText().toString());

            nuevaHip = new HipotecaSegMixta(nombre, comunidad, tipo_vivienda, antiguedad_vivienda, precio_viv, cant_abonada, plazo_hip, fecha_inicio, tipo_hipoteca, totalGastos, arrayVinculacionesAnuales, banco_asociado, anios_fijo, porc_fijo_mixta, porc_diferencial_mixta, revision_anual);

            nuevaHip.setTipo_hipoteca(tipo_hipoteca);
            nuevaHip.setIdUsuario(user.getUid());
        }

        db.collection("hipotecas_seguimiento").add(nuevaHip).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                registrarNuevaTablaAmortizacionAnticipada();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG,"Error al registrar hipoteca de seguimiento en Firestore: ");
            }
        });
    }

    private void registrarNuevaTablaAmortizacionAnticipada(){


        Map<Integer, List<Object>> lista_amortizaciones_anticipadas = new HashMap<>();

        Map<String, Object> doc_amortizacion_anticipada = new HashMap<>();
        doc_amortizacion_anticipada.put("nombre_hipoteca", nombre_hipoteca.getText().toString());
        doc_amortizacion_anticipada.put("idUsuario", auth.getCurrentUser().getUid());
        doc_amortizacion_anticipada.put("amortizaciones_anticipadas", lista_amortizaciones_anticipadas);

        db.collection("amortizaciones_anticipadas").add(doc_amortizacion_anticipada).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(NuevoSeguimiento.this, getString(R.string.hipoteca_seguimiento_exito), Toast.LENGTH_LONG).show();
                Intent i = new Intent(NuevoSeguimiento.this, PaginaPrincipal.class);
                startActivity(i);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG,"Error al registrar tabla amortizaciones anticipadas en Firestore: ");
            }
        });

    }

    /** Esta funcion devuelve los años y meses que quedan de hipoteca**/
    public ArrayList<Integer> getAniosMesesRestantes(int plazo, int cuota){

        ArrayList<Integer> anios_meses = new ArrayList<>();
        int cuotasRestantes = (plazo * 12) - cuota;
        int anios = cuotasRestantes / 12;
        int meses = cuotasRestantes % 12;
        anios_meses.add(anios);
        anios_meses.add(meses);
        return anios_meses;
    }

    /** Devuelve el numero de cuota por el que va actualmente la hipoteca [1 - plazo_anios * 12 ] **/
    public int getNumeroCuotaActual(Date fecha_inicio, int plazo){
        Calendar inicio = Calendar.getInstance();
        inicio.setTime(fecha_inicio);
        // Dia actual
        Calendar actual = Calendar.getInstance();
        // En caso de que todavia no haya empezado el seguimiento de la hipoteca
        if(actual.compareTo(inicio) < 0) return 0;
        int difA = actual.get(Calendar.YEAR) - inicio.get(Calendar.YEAR);
        int numeroPagoActual = difA * 12 + actual.get(Calendar.MONTH) - inicio.get(Calendar.MONTH);

        // Si el dia es el mismo que el de pago, devuelve como si ya ha pagado esa cuota
        if(actual.get(Calendar.DAY_OF_MONTH) >= inicio.get(Calendar.DAY_OF_MONTH)) numeroPagoActual = numeroPagoActual + 1; //Se le sumaria 1 debido a que ya ha pasado el dia de pago del mes correspondiente
        //else return numeroPagoActual + 1;
        // Fin de hipoteca
        if (numeroPagoActual >= plazo * 12) numeroPagoActual = plazo * 12;
        return numeroPagoActual;

    }

}