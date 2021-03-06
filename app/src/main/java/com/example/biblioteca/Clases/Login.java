package com.example.biblioteca.Clases;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.biblioteca.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

/**
 *
 */
public class Login extends AppCompatActivity {

    private static final int RC_SIGN_IN = 100;
    private GoogleSignInClient googleSignInClient;

    private FirebaseAuth firebaseAuth;

    private static final String TAG = "GOOGLE_SIGN_IN";

    /**
     * Crear vista
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.example.biblioteca.databinding.ActivityLoginBinding binding = com.example.biblioteca.databinding.ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //creacion de opciones para el uso de inicio con Google
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this,gso);
        firebaseAuth = FirebaseAuth.getInstance();
        //Inicio de sesion con google
        binding.googleSignBtn.setOnClickListener(view -> {
            Intent intent = googleSignInClient.getSignInIntent();
            startActivityForResult(intent,RC_SIGN_IN);
        });

    }

    /**
     * Espera para inicio de google por boton
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN){
            Log.d(TAG,"google correcto");
            Task<GoogleSignInAccount> at = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = at.getResult(ApiException.class);
                assert account != null;
                firebaseAuthWithGoogle(account);
            }catch (Exception e){
                Log.d(TAG,e.toString());
                Log.d(TAG,"error en google");
            }
        }
    }

    /**
     * Metodo para obtener la instancia de firebase + credencial de google
      * @param account
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        startActivity(new Intent(Login.this, MainActivity.class));
                        Toast.makeText(Login.this,"Bienvenido "+account.getDisplayName(),Toast.LENGTH_SHORT).show();
                        finish();

                    }
                })
                .addOnFailureListener(e ->
                        Log.d(TAG,"Error en firebase")
                );
    }

    /**
     * comprobar si existen sesiones iniciadas en la app
     */
   @Override
    protected void onStart() {
        super.onStart();
        //valida si existe un usuario previo he inicia sesion
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account!=null&&user!=null){
            Toast.makeText(this,"Iniciando sesion...",Toast.LENGTH_SHORT).show();
            firebaseAuthWithGoogle(account);
        } else if(user!=null){
            startActivity(new Intent(Login.this, MainActivity.class));
            Toast.makeText(Login.this,"Bienvenido "+user.getDisplayName(),Toast.LENGTH_SHORT).show();
            finish();

        }

    }

    /**
     * Redireccionar a la siguente activity
     * @param view
     */
    public void irRegistro(View view){
        Intent i = new Intent(this, Register.class);
        startActivity(i);
    }
    /**
     * Redireccionar a la siguente activity
     * @param view
     */
    public void irIniciosesion(View view){
        Intent i = new Intent(this, LoginCorreo.class);
        startActivity(i);

    }


}