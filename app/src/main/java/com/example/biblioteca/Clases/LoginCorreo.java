package com.example.biblioteca.Clases;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.biblioteca.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginCorreo extends AppCompatActivity {

    private EditText correo, contraseña;
    private FirebaseAuth firebaseAuth;
    private Button btnIngresar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_correo);
        correo = findViewById(R.id.edtemailLogin);
        contraseña = findViewById(R.id.edtpassLogin);
        firebaseAuth = FirebaseAuth.getInstance();
        btnIngresar = findViewById(R.id.btnIniciar);
        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IniciarSesion();
            }
        });
    }

    public void irLogin(View view){
        Intent i = new Intent(this, Login.class);
        startActivity(i);
        finish();
    }

    public void IniciarSesion(){
        firebaseAuth.signInWithEmailAndPassword(correo.getText().toString(), contraseña.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            startActivity(new Intent(LoginCorreo.this, MainActivity.class));
                            Toast.makeText(LoginCorreo.this,"Bienvenido "+user.getDisplayName(),Toast.LENGTH_SHORT).show();
                            finishAffinity();
                        } else {
                            // If sign in fails, display a message to the user.

                            Toast.makeText(getApplicationContext(), "Inicio de sesión fallido",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });
    }

}