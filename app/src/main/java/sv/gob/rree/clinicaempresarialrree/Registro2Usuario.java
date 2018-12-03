package sv.gob.rree.clinicaempresarialrree;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Registro2Usuario extends AppCompatActivity {

    Button btnRegis;
    ImageView img;
    TextView correoN,pass,pass2;
    CheckBox condiciones;
    Integer aceptaCondiciones;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro2_usuario);

        img = findViewById(R.id.imgUsuario);

        //extraemos el drawable en un bitmap
        Drawable originalDrawable = getResources().getDrawable(R.drawable.ic_launcher);
        Bitmap originalBitmap = ((BitmapDrawable) originalDrawable).getBitmap();

        //creamos el drawable redondeado
        RoundedBitmapDrawable roundedDrawable;
        roundedDrawable = RoundedBitmapDrawableFactory.create(getResources(), originalBitmap);

        //asignamos el CornerRadius
        roundedDrawable.setCornerRadius(originalBitmap.getHeight());
        img.setImageDrawable(roundedDrawable);

        //PARA EL REGISTRO DEL USUARIO

        correoN = findViewById(R.id.txtCorreo);
        pass = findViewById(R.id.txtPass);
        pass2 = findViewById(R.id.txtPass2);
        condiciones = findViewById(R.id.chCondiciones);

        btnRegis = (Button) findViewById(R.id.btnRegistrarFinal);

        btnRegis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = correoN.getText().toString().trim();
                String contra = pass.getText().toString().trim();
                String repeat = pass2.getText().toString().trim();

                int acptaTerminos =0;


                if (email.isEmpty()){
                    correoN.setError("ingrese su correo electronico institucional");
                }else if (contra.isEmpty()){
                    pass.setError("ingrese la contraseña");
                }else if (repeat.isEmpty())
                {
                    pass2.setError("Porfavor repita la contraseña");
                }else if (contra.equals(repeat)){
                    if (condiciones.isChecked()){
                        saveData(email, repeat);
                    }else{
                        Toast.makeText(Registro2Usuario.this , "Necesitamos que acepte los terminos y condiciones!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    pass2.setError("contraseñas deben ser iguales");
                }

            }
        });


    }

    private void saveData(String email, String Pass) {

        String url = "https://lfernandoflamenco.000webhostapp.com/WebService/WSregistroUsuario.php?correo="+email+"&pass="+Pass;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Integer success = jsonObject.getInt("resp");
                    JSONArray jsonArray = jsonObject.getJSONArray("response");
                    if (success==1) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);

                            //PARA GUARDAR EL ID DEL USUARIO QUE SE ACABA DE REGISTRAR
                            String id= obj.getString("tusua_usuarioid");
                            SharedPreferences pref = getSharedPreferences("Preferencias", Context.MODE_PRIVATE);
                            SharedPreferences.Editor prefEditor = pref.edit();
                            prefEditor.putString("idUsuario",id);
                            prefEditor.commit();

                            Intent continueR = new Intent(Registro2Usuario.this, RegistroUsuario.class);
                            startActivity(continueR);
                        }
                    }else{
                        Toast.makeText(Registro2Usuario.this , "El correo electronico ya ha sido registrado1", Toast.LENGTH_SHORT).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(Registro2Usuario.this, "Error no deseado! \n"+e, Toast.LENGTH_SHORT).show();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(Registro2Usuario.this, "Error! \n Revisa tu conexion a internet.", Toast.LENGTH_SHORT).show();
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

    public void checkClic(View view) {

        if (condiciones.isChecked()){
            aceptaCondiciones=1;
        }else {
            aceptaCondiciones=1;
        }

    }
}
