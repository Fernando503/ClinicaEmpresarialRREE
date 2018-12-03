package sv.gob.rree.clinicaempresarialrree;

import android.app.DatePickerDialog;
import android.app.Dialog;
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
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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

public class RegistroBeneficiario extends AppCompatActivity {

    private ImageView imgNewBene;
    private ArrayAdapter<CharSequence> adapterBene;
    private EditText t2,nombresB,apellidosB,fechaB;
    CheckBox primera;
    private Button btnAgregar;
    private int mYearIni, mMonthIni, mDayIni, sYearIni, sMonthIni, sDayIni;
    static final int DATE_ID = 0;
    Calendar C = Calendar.getInstance();
    Bitmap bitmap;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_beneficiario);

        imgNewBene =(ImageView)findViewById(R.id.imgBene);

        //para la fechaa
        sMonthIni = C.get(Calendar.MONTH);
        sDayIni = C.get(Calendar.DAY_OF_MONTH);
        sYearIni = C.get(Calendar.YEAR);
        t2 = (EditText) findViewById(R.id.txtFechaBeneficiario);

        t2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showDialog(DATE_ID);
            }
        });
        primera = findViewById(R.id.chPrimera);
        btnAgregar = findViewById(R.id.btnAgregarBeneficiario);

        nombresB = findViewById(R.id.txtNombreBeneficiario);
        apellidosB = findViewById(R.id.txtApellidosBeneficiario);
        fechaB = findViewById(R.id.txtFechaBeneficiario);

        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String nombres = nombresB.getText().toString().trim();
                final String apellidos = apellidosB.getText().toString().trim();
                final String fecha = fechaB.getText().toString().trim();
                SharedPreferences pref = getSharedPreferences("Preferencias",Context.MODE_PRIVATE);
                final String idUsuario = pref.getString("idUsuario","nada");
                String che ="";
                if (fechaB.isSelected())
                {
                   che = "1";
                }else{
                    {
                        che = "0";
                    }
                }
                final String primeraVez = che;
                final String imagen = convertirImagen(bitmap);

                /*---------------INICIO DE EL GUARDADO-----------------------------------------------------------*/



                String url3 = "https://lfernandoflamenco.000webhostapp.com/WebService/WSregistroBeneficiario.php?";

                StringRequest stringRequest = new StringRequest(Request.Method.POST, url3, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("resp");
                            String mensaje = jsonObject.getString("mensaje");
                            JSONArray jsonArray = jsonObject.getJSONArray("response");
                            if (success.equals("1")) {

                                new FancyGifDialog.Builder(RegistroBeneficiario.this)
                                        .setTitle("Exito!")
                                        .setMessage("El Beneficiario ha sido agregado con Exito. ¿Desea agregar uno nuevo?")
                                        .setNegativeBtnText("NO")
                                        .setPositiveBtnBackground("#FF4081")
                                        .setPositiveBtnText("SI")
                                        .setNegativeBtnBackground("#FFA9A7A8")
                                        .setGifResource(R.drawable.gif16)   //Pass your Gif here
                                        .isCancellable(true)
                                        .OnPositiveClicked(new FancyGifDialogListener() {
                                            @Override
                                            public void OnClick() {
                                                Intent despa = new Intent(RegistroBeneficiario.this, RegistroBeneficiario.class);
                                                startActivity(despa);
                                            }
                                        })
                                        .OnNegativeClicked(new FancyGifDialogListener() {
                                            @Override
                                            public void OnClick() {
                                                new FancyGifDialog.Builder(RegistroBeneficiario.this)
                                                        .setTitle("Registro Exitoso")
                                                        .setMessage("Haz realizado el proceso con exito pero antes, debes iniciar sesion.")
                                                        .setPositiveBtnBackground("#4AD5E7")
                                                        .setPositiveBtnText("Ok")
                                                        .setGifResource(R.drawable.gif16)   //Pass your Gif here
                                                        .OnPositiveClicked(new FancyGifDialogListener() {
                                                            @Override
                                                            public void OnClick() {

                                                                Intent despa = new Intent(RegistroBeneficiario.this , MainActivity.class);
                                                                startActivity(despa);
                                                            }
                                                        })
                                                        .build();
                                            }
                                        })
                                        .build();

                            } else {

                                Toast.makeText(RegistroBeneficiario.this, mensaje, Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(RegistroBeneficiario.this, "Error no deseado! \n" + e, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                Toast.makeText(RegistroBeneficiario.this, "Error! \n Revisa tu conexion a internet.", Toast.LENGTH_SHORT).show();
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("nombres", nombres);
                        params.put("idUsuario", idUsuario);
                        params.put("apellidos", apellidos);
                        params.put("fecha", fecha);
                        params.put("primeraVez", primeraVez );
                        params.put("imagen", imagen);

                        return params;
                    }
                };

                RequestQueue requestQueue = Volley.newRequestQueue(RegistroBeneficiario.this);
                requestQueue.add(stringRequest);


                /*----------------------------------------------------------FIN DEL GUARDADO*----------------------*/
            }

        });

    }


    public void imgBene(View view) {
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
            //imgNewBene.setImageURI(patch);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(),patch);
                imgNewBene.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else
        {

        }
    }

    //convertir la imaen a un string
    public String convertirImagen(Bitmap bit)
    {

        ByteArrayOutputStream array = new ByteArrayOutputStream();
        bit.compress(Bitmap.CompressFormat.JPEG,100,array);
        byte[] imagenByte = array.toByteArray();
        String imagenString = Base64.encodeToString(imagenByte,Base64.DEFAULT);

        return imagenString;
    }

    //PARA LA FECHA


    private void colocar_fecha() {
        t2.setText((mDayIni) + "/" + (mMonthIni+ 1) + "/" + mYearIni+" ");
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


}
