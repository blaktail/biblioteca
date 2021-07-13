package com.example.biblioteca;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Register extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private EditText correo, contrasena, contrasenac;

    public void irMain(View view){
        Intent i = new Intent(this, Login.class);
        startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();
        correo = findViewById(R.id.edtemailLogin);
        contrasena = findViewById(R.id.edtpassLogin);
        contrasenac = findViewById(R.id.edtConfcontraseña);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(currentUser != null){
            currentUser.reload();
        }
    }



    // registro de usuarios por correo
    public void registrarUsuario(View view){
        if (contrasena.getText().toString().equals(contrasenac.getText().toString())){
            firebaseAuth.createUserWithEmailAndPassword(correo.getText().toString(), contrasena.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(),"Usuario creado",Toast.LENGTH_SHORT).show();
                                FirebaseUser user = firebaseAuth.getCurrentUser();
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