package com.example.amst_grupo2_labb_cloud_firebase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PerfilFB extends AppCompatActivity {

    ImageView imageView;
    TextView name;
    Button logOutbtn;
    Button tweet;
    EditText txt_tweet, txt_date;
    DatabaseReference db_reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_fb);

        imageView = findViewById(R.id.imageView);
        name = findViewById(R.id.name);
        logOutbtn = findViewById(R.id.logout);

        txt_date=findViewById(R.id.txt_date);
        txt_tweet=findViewById(R.id.txt_tweet);
        tweet= (Button) findViewById(R.id.btn_tweet);

        AccessToken accessToken = AccessToken.getCurrentAccessToken();

        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        // Application code
                        try {
                           String fullname = object.getString("name");
                           String url = object.getJSONObject("picture").getJSONObject("data").getString("url");
                           name.setText(fullname);
                            Picasso.get().load(url).into(imageView);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,picture.type(large)");
        request.setParameters(parameters);
        request.executeAsync();

        logOutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logOut();
                startActivity(new Intent(PerfilFB.this,MainActivity.class));
                finish();
            }
        });
        iniciarBaseDeDatos();
        leerTweets();
        tweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                escribirTweets("Jeff Cabello Sanchez",txt_date.getText().toString(),txt_tweet.getText().toString());
                Toast mensaje = Toast.makeText(getApplicationContext(),"Se envio el Tweet", Toast.LENGTH_SHORT);
                mensaje.show();
                txt_date.setText("");
                txt_tweet.setText("");
            }
        }) ;
    }
    public void iniciarBaseDeDatos(){
        db_reference = FirebaseDatabase.getInstance().getReference().child("Grupos");
    }

    public void leerTweets(){
        db_reference.child("Grupo2").child("tweets")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            System.out.println(snapshot);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError error) {
                        System.out.println(error.toException());
                    }
                });
    }

    public void escribirTweets(String autor, String txt_date, String txt_tweet){

        String tweet = txt_tweet;
        String fecha = txt_date; //Fecha actual
        Map<String, String> hola_tweet = new HashMap<String, String>();
        hola_tweet.put("autor", autor);
        hola_tweet.put("fecha", fecha);
        DatabaseReference tweets = db_reference.child("Grupo2").child("tweets");
        tweets.setValue(tweet);
        tweets.child(tweet).child("autor").setValue(autor);
        tweets.child(tweet).child("fecha").setValue(fecha);

    }
}