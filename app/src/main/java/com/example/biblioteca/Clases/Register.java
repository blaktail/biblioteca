package com.example.biblioteca.Clases;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.biblioteca.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

/**
 *
 */
public class Register extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private EditText correo, contrasena, contrasenac,nombre;

    public void irMain(View view){
        Intent i = new Intent(this, Login.class);
        startActivity(i);
    }

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();
        nombre = findViewById(R.id.edtUsuario);
        correo = findViewById(R.id.edtemailLogin);
        contrasena = findViewById(R.id.edtpassLogin);
        contrasenac = findViewById(R.id.edtConfcontrasena);
    }

    /**
     *
     */
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(currentUser != null){
            firebaseAuth.signOut();
        }
    }


    /**
     *
     * @param view
     */
    public void registrarUsuario(View view){
        if (contrasena.getText().toString().equals(contrasenac.getText().toString())){
            firebaseAuth.createUserWithEmailAndPassword(correo.getText().toString(), contrasena.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = firebaseAuth.getCurrentUser();

                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(nombre.getText().toString())
                                        .setPhotoUri(Uri.parse("https://d1nhio0ox7pgb.cloudfront.net/_img/o_collection_png/green_dark_grey/256x256/plain/user.png"))
                                        .build();
                                user.updateProfile(profileUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(getApplicationContext(),"Usuario creado",Toast.LENGTH_SHORT).show();
                                    }
                                });

                                Intent i = new Intent(getApplicationContext(), Login.class);
                                startActivity(i);
                            } else {
                                Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                                //updateUI(null);
                            }
                        }
                    });
        } else {
            Toast.makeText(this,"Las contraseñas no coinciden",Toast.LENGTH_SHORT).show();}
    }
}