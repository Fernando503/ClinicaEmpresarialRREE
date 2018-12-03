package sv.gob.rree.clinicaempresarialrree;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;
import com.shashank.sony.fancygifdialoglib.FancyGifDialogListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {


    LinearLayout btnNCita;
    ImageView imgHome;
    String idUsurper="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        imgHome = findViewById(R.id.imgHome);
        //para la imagen
        //para obtener el id del USUARIO y guardar el paciente;
        SharedPreferences pref = getSharedPreferences("PreferenciasImagenes", Context.MODE_PRIVATE);
        idUsurper= pref.getString("idUsuario","nada");
        //test para verificar el id del usuario
        Toast.makeText(this, idUsurper , Toast.LENGTH_SHORT).show();

        if (!idUsurper.equals("nada"))
        {
            cargarImagen(idUsurper);
        }




        btnNCita =(LinearLayout)findViewById(R.id.btnCrearCita);
        btnNCita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogo();
            }
        });
    }

    private void cargarImagen(String idUsurper) {

        String url = "https://lfernandoflamenco.000webhostapp.com/WebService/WSgetImgByIdUsuario.php?id="+idUsurper;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("tpaci_imagen");

                    if (!success.isEmpty()) {
                        Picasso.get()
                                .load(success)
                                .into(imgHome);
                    }else{
                        Toast.makeText(HomeActivity.this , "Agrega una imagen a tu perfil.", Toast.LENGTH_SHORT).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(HomeActivity.this, "Error no deseado! \n"+e, Toast.LENGTH_SHORT).show();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(HomeActivity.this, "Error! \n Revisa tu conexion a internet.", Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String>  params = new HashMap<>();
                //params.put("user", email);
                //params.put("pass", pass);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void mostrarDialogo() {
        new FancyGifDialog.Builder(HomeActivity.this)
                .setTitle("Tipo de Cita")
                .setMessage("Puedes elegir tu cita general, o cita metabolica(Control de embrazo)")
                .setNegativeBtnText("General")
                .setPositiveBtnBackground("#FF4081")
                .setPositiveBtnText("Metabolica")
                .setNegativeBtnBackground("#FFA9A7A8")
                .setGifResource(R.drawable.gif1)   //Pass your Gif here
                .isCancellable(true)
                .OnPositiveClicked(new FancyGifDialogListener() {
                    @Override
                    public void OnClick() {
                        new FancyGifDialog.Builder(HomeActivity.this)
                                .setTitle("Cita Metabolica")
                                .setMessage("Selecciona quien pasara consulta")
                                .setNegativeBtnText("Cancel")
                                .setPositiveBtnBackground("#FF4081")
                                .setPositiveBtnText("Ok")
                                .setNegativeBtnBackground("#FFA9A7A8")
                                .setGifResource(R.drawable.gif1)   //Pass your Gif here
                                .isCancellable(true)
                                .OnPositiveClicked(new FancyGifDialogListener() {
                                    @Override
                                    public void OnClick() {
                                        Toast.makeText(HomeActivity.this,"Ok",Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .OnNegativeClicked(new FancyGifDialogListener() {
                                    @Override
                                    public void OnClick() {
                                        Toast.makeText(HomeActivity.this,"Cancel",Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .build();
                    }
                })
                .OnNegativeClicked(new FancyGifDialogListener() {
                    @Override
                    public void OnClick() {
                        new FancyGifDialog.Builder(HomeActivity.this)
                                .setTitle("General")
                                .setMessage("Seleccion quien pasara consulta")
                                .setNegativeBtnText("Cancel")
                                .setPositiveBtnBackground("#FF4081")
                                .setPositiveBtnText("Ok")
                                .setNegativeBtnBackground("#FFA9A7A8")
                                .setGifResource(R.drawable.gif1)   //Pass your Gif here
                                .isCancellable(true)
                                .OnPositiveClicked(new FancyGifDialogListener() {
                                    @Override
                                    public void OnClick() {
                                        Toast.makeText(HomeActivity.this,"Ok",Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .OnNegativeClicked(new FancyGifDialogListener() {
                                    @Override
                                    public void OnClick() {
                                        Toast.makeText(HomeActivity.this,"Cancel",Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .build();
                    }
                })
                .build();

    }
}
