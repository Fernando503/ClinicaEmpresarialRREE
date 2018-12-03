package sv.gob.rree.clinicaempresarialrree;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.SimpleMaskTextWatcher;
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;
import com.shashank.sony.fancygifdialoglib.FancyGifDialogListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RegistroUsuario extends AppCompatActivity {

    StringRequest stringRequest;

    //para instaciar los items de nuestra actividad
    private EditText nombresP, apellidosP,departamentoP, fechaP, isssP, duiP, telefonoP;
    private ImageView imgNewUser,imgNewUserP;
    private Button btnRegistrar;
    private Spinner selectDepP;

    //para el spinner de los departamentos
    private ArrayAdapter<CharSequence> adapter;
    private Spinner selectDep;
    private ProgressDialog progressBar;

    //para el dateTimePicker
    private EditText t1;
    private int mYearIni, mMonthIni, mDayIni, sYearIni, sMonthIni, sDayIni;
    static final int DATE_ID = 0;
    Calendar C = Calendar.getInstance();
    Bitmap bitmap;

    //variable de sharedpreference
    String idUsurper="";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_usuario);

        //para obtener el id del USUARIO y guardar el paciente;
        SharedPreferences pref = getSharedPreferences("Preferencias", Context.MODE_PRIVATE);
        idUsurper= pref.getString("idUsuario","nada");
        //test para verificar el id del usuario
        //Toast.makeText(RegistroUsuario.this, idUsurper , Toast.LENGTH_SHORT).show();

        //para la imagen del usuario
        imgNewUser =(ImageView)findViewById(R.id.imgRegistro);
        selectDep = findViewById(R.id.selectDep);
        cargarSelect();

        //para la fechaa
        sMonthIni = C.get(Calendar.MONTH);
        sDayIni = C.get(Calendar.DAY_OF_MONTH);
        sYearIni = C.get(Calendar.YEAR);
        t1 = (EditText) findViewById(R.id.txtFecha);

        //obteniendo datos de los items de la actividad
        nombresP = findViewById(R.id.txtNombres);
        apellidosP = findViewById(R.id.txtApellidos);
        fechaP = findViewById(R.id.txtFecha);
        selectDepP = findViewById(R.id.selectDep);
        isssP =findViewById(R.id.txtIsss);
        duiP  = findViewById(R.id.txtDui);
        telefonoP = findViewById(R.id.txtTelefono);
        imgNewUserP = findViewById(R.id.imgRegistro);
        btnRegistrar = findViewById(R.id.btnRegistrar);

        t1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showDialog(DATE_ID);
            }
        });

        // HILO PARA MANTENER UNA CONEXION
        new Thread(new Runnable() {
            @Override
            public void run() {
                upLoadData();

            }
        }).start();


        /*------------FIN DEL ONCREATE------------------*/
    }

    public void upLoadData(){
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*------------CREACIION DE LAS MASCARAS------------------------*/

                //ISSS
                SimpleMaskFormatter FrmIss = new SimpleMaskFormatter("NNNNNNNNN");
                SimpleMaskTextWatcher WFrmIss = new SimpleMaskTextWatcher(isssP,FrmIss);
                isssP.addTextChangedListener(WFrmIss);

                //DUI
                SimpleMaskFormatter FrmDui = new SimpleMaskFormatter("NNNNNNNN-N");
                SimpleMaskTextWatcher WFrmDui = new SimpleMaskTextWatcher(isssP,FrmDui);
                isssP.addTextChangedListener(WFrmDui);

                //Telefono
                SimpleMaskFormatter FrmTel= new SimpleMaskFormatter("NNNN-NNNN");
                SimpleMaskTextWatcher WFrmTel = new SimpleMaskTextWatcher(isssP,FrmTel);
                isssP.addTextChangedListener(WFrmTel);

                /*------------FIN CREACIION DE LAS MASCARAS------------------------*/

                final String nombres = nombresP.getText().toString().trim();
                final String apellidos = apellidosP.getText().toString().trim();
                final String fecha = fechaP.getText().toString().trim();
                final String departamento = selectDep.getSelectedItem().toString().trim();
                final String isss = isssP.getText().toString().trim();
                final String dui = duiP.getText().toString().trim();
                final String telefono = telefonoP.getText().toString().trim();
                final String imagen = convertirImagen(bitmap);

                if(nombres.isEmpty()){
                    nombresP.setError("Porfavor ingrese sus nombres.");
                }else if (apellidos.isEmpty()){
                    apellidosP.setError("Porfavor ingrese sus apellidos.");
                }else if(fecha.isEmpty()){
                    fechaP.setError("Porfavor seleccione una fecha.");
                }else if (departamento.equals("Seleccionar Departamento"))
                {
                    departamentoP.setError("Porfavor seleccione un departamento.");
                }else if (isss.isEmpty())
                {
                    isssP.setError("Porfavor ingrese su numero de ISSS.");
                }else if (dui.isEmpty())
                {
                    duiP.setError("Porfavor ingresar su numero de DUI.");
                }else if (telefono.isEmpty())
                {
                    telefonoP.setError("Porfavor ingresar su numero de Telefono.");
                }if (imagen.isEmpty()){
                    Toast.makeText(RegistroUsuario.this , "Porfavor ingrese una imagen para su Perfil.", Toast.LENGTH_SHORT).show();
                }else{
                    /* INICIO  DEL GUARDADO DE LOS PACIENTES*/

                    progressBar = new ProgressDialog(v.getContext());
                    progressBar.setCancelable(true);
                    progressBar.setMessage("Guardando ...");
                    progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressBar.setProgress(0);
                    progressBar.setMax(100);
                    progressBar.show();

                    String url2 = "https://lfernandoflamenco.000webhostapp.com/WebService/WSregistroPaciente.php?";

                    stringRequest = new StringRequest(Request.Method.POST, url2, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String success = jsonObject.getString("resp");
                                String mensaje = jsonObject.getString("mensaje");
                                JSONArray jsonArray = jsonObject.getJSONArray("response");
                                if (success.equals("1")) {
                                    progressBar.hide();
                                    mostrarMensajeBeneficiarios();

                                }else{

                                    Toast.makeText(RegistroUsuario.this , mensaje, Toast.LENGTH_SHORT).show();

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(RegistroUsuario.this, "Error no deseado! \n"+e, Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                    Toast.makeText(RegistroUsuario.this, "Error! \n Revisa tu conexion a internet.", Toast.LENGTH_SHORT).show();
                                }
                            })
                    {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String>  params = new HashMap<>();
                            params.put("nombres", nombres);
                            params.put("apellidos", apellidos);
                            params.put("idUsuario", idUsurper);
                            params.put("fecha", fecha);
                            params.put("departamento", departamento);
                            params.put("isss", isss);
                            params.put("dui", dui);
                            params.put("telefono", telefono);
                            params.put("imagen", imagen);

                            return params;
                        }
                    };

                    RequestQueue requestQueue = Volley.newRequestQueue(RegistroUsuario.this);
                    requestQueue.add(stringRequest);

                    /* FIN DEL GUARDADO DE LOS PACIENTES*/
                }

            }
        });

    }

    public void cargarSelect()
    {
        this.adapter = ArrayAdapter.createFromResource(this, R.array.areas, android.R.layout.simple_spinner_item);
        this.adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.selectDep.setAdapter(this.adapter);
    }

    public void seleccionar(View view) {
        cargarImagen();
    }

    public void cargarImagen()
    {
        Intent in = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        in.setType("image/");
        startActivityForResult(in.createChooser(in,"seleccione la aplicacion"),10);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK)
        {
            Uri patch = data.getData();
            //imgNewUser.setImageURI(patch);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(),patch);
                imgNewUser.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else
        {

        }
    }

    //PARA LA FECHA

    private void colocar_fecha() {
        t1.setText((mDayIni) + "/" + (mMonthIni+ 1) + "/" + mYearIni+" ");
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    mYearIni = year;
                    mMonthIni = monthOfYear;
                    mDayIni = dayOfMonth;
                    colocar_fecha();
                }

            };


    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_ID:
                DatePickerDialog fecha = new DatePickerDialog(this, mDateSetListener, sYearIni, sMonthIni, sDayIni);
                fecha.getDatePicker().setMaxDate(System.currentTimeMillis());
                fecha.getDatePicker().setMinDate(-629992400000L);
                return fecha;
        }


        return null;
    }

    // PARA GUARDAR INFORMACION DEL PACIENTE
    public String convertirImagen(Bitmap bit)
    {

        ByteArrayOutputStream array = new ByteArrayOutputStream();
        bit.compress(Bitmap.CompressFormat.JPEG,100,array);
        byte[] imagenByte = array.toByteArray();
        String imagenString = Base64.encodeToString(imagenByte,Base64.DEFAULT);

        return imagenString;
    };
//para el registro de beneficiarios

    public void abrirRegistro2(View view) {

        //mostrarMensajeBeneficiarios();

    }

    private void mostrarMensajeBeneficiarios()
    {
        new FancyGifDialog.Builder(this)
                .setTitle("Beneficiarios")
                .setMessage("¿Deseas ingresar a tus hijos como benefiarios del Sistema de Salud?")
                .setNegativeBtnText("NO")
                .setPositiveBtnBackground("#FF4081")
                .setPositiveBtnText("SI")
                .setNegativeBtnBackground("#FFA9A7A8")
                .setGifResource(R.drawable.beneficiarios)   //Pass your Gif here
                .isCancellable(true)
                .OnPositiveClicked(new FancyGifDialogListener() {
                    @Override
                    public void OnClick() {
                        Intent despa = new Intent(RegistroUsuario.this, RegistroBeneficiario.class);
                        startActivity(despa);
                    }
                })
                .OnNegativeClicked(new FancyGifDialogListener() {
                    @Override
                    public void OnClick() {
                        new FancyGifDialog.Builder(RegistroUsuario.this)
                                .setTitle("Registro Exitoso")
                                .setMessage("Haz realizado el proceso con exito pero antes, debes iniciar sesion.")
                                .setPositiveBtnBackground("#FF4081")
                                .setPositiveBtnText("Ok")
                                .isCancellable(true)
                                .setGifResource(R.drawable.gif16)   //Pass your Gif here
                                .OnPositiveClicked(new FancyGifDialogListener() {
                                    @Override
                                    public void OnClick() {
                                        SharedPreferences pref = getSharedPreferences("Preferencias", Context.MODE_PRIVATE);
                                        pref.edit().remove("Preferencias").commit();
                                        Intent despa = new Intent(RegistroUsuario.this , MainActivity.class);
                                        startActivity(despa);
                                    }
                                })
                                .build();
                    }
                })
                .build();


    }


}
