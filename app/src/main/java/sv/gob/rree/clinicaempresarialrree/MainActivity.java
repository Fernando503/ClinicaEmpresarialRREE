package sv.gob.rree.clinicaempresarialrree;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity{

    private EditText email, passWd;
    private Button login;
    private ProgressBar loading;
    private TextView link_Register;
    private ImageView logo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       loading = findViewById(R.id.loading);
       email = findViewById(R.id.txtCorreo);
       passWd = findViewById(R.id.txtPassword);
       login = findViewById(R.id.btnLogin);
       link_Register = findViewById(R.id.lblRegister);
       logo = findViewById(R.id.imgLogo_Login);


       login.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String mEmail = email.getText().toString().trim();
               String mPass = passWd.getText().toString().trim();

               if(!mEmail.isEmpty() || !mPass.isEmpty())
               {
                   Login(mEmail, mPass);
               } else {
                   email.setError("Por favor ingrese su correo");
                   passWd.setError("Por favor ingrese su Constrasenia");
               }

           }
       });
       loadImageWithPicasso();
    }

    private void loadImageWithPicasso() {
        Picasso.get()
                .load("https://rree.gob.sv/wp-content/uploads/2018/05/logorree.jpg")
                .into(logo);

    }



    private void Login(final String email, final String pass) {
        loading.setVisibility(View.VISIBLE);
        login.setVisibility(View.GONE);
        String url = "https://lfernandoflamenco.000webhostapp.com/WebService/login.php?user="+email+"&pass="+pass;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    JSONArray jsonArray = jsonObject.getJSONArray("login");
                    if (success.equals("1")) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            Toast.makeText(MainActivity.this, "Ingresando...", Toast.LENGTH_SHORT).show();

                            String id = obj.getString("tusua_usuarioid");

                            SharedPreferences pref = getSharedPreferences("PreferenciasImagenes", Context.MODE_PRIVATE);
                            SharedPreferences.Editor prefEditor = pref.edit();
                            prefEditor.putString("idUsuario",id);
                            prefEditor.commit();

                            Intent home = new Intent(MainActivity.this, HomeActivity.class);
                            startActivity(home);
                            loading.setVisibility(View.GONE);
                        }
                    }else{
                        Toast.makeText(MainActivity.this, "Error! Usuario y/o Constrasenia Invalido.", Toast.LENGTH_SHORT).show();
                        loading.setVisibility(View.GONE);
                        login.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    loading.setVisibility(View.GONE);
                    login.setVisibility(View.VISIBLE);
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Error no deseado! \n"+e, Toast.LENGTH_SHORT).show();
                }
            }
        },
                new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.setVisibility(View.GONE);
                login.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this, "Error! \n Revisa tu conexion a internet.", Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String>  params = new HashMap<>();
                params.put("user", email);
                params.put("pass", pass);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

//para abrir la actividad de registro de usuario

    public void abrirActivityRegistro(View view) {
        Intent registro = new Intent(MainActivity.this,Registro2Usuario.class );
        startActivity(registro);
        loading.setVisibility(View.GONE);
    }
}
