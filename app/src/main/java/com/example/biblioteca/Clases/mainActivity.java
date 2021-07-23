package com.example.biblioteca.Clases;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.example.biblioteca.R;
import com.example.biblioteca.databinding.ActivityMainBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 *
 */

public class mainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private static final String TAG ="logout";
    public FragmentManager fragmentManager;

    GoogleSignInClient googleSignInClient;

    public final String[] EXTERNAL_PERMS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    public final int EXTERNAL_REQUEST = 138;

    /**
     *
     */
    public void requestForExternalStoragePermission() {
        boolean isPermissionOn = true;
        final int version = Build.VERSION.SDK_INT;
        if (version >= 23) {
            if (!canAccessExternalSd()) {
                isPermissionOn = false;
                requestPermissions(EXTERNAL_PERMS, EXTERNAL_REQUEST);
            }
        }
    }

    /**
     *
     * @return
     */
    public boolean canAccessExternalSd() {
        return (hasPermission());
    }

    /**
     *
     * @return
     */
    private boolean hasPermission() {
        return (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE));
    }

        @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                Log.d("MyApp", "No SDCARD");
            }

            if (Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy =
                        new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }


            requestForExternalStoragePermission();
            //acceder a la cuenta de google creada
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(mainActivity.this);
            GoogleSignInOptions gso = new GoogleSignInOptions
                    .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestProfile().build();
            googleSignInClient = GoogleSignIn.getClient(mainActivity.this, gso);
            //acceder al usuario de firebase
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            //generico creacion de drawer para la vista de fragmentos
            binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            NavigationView  navigationView = binding.navView ;
            setSupportActionBar(binding.appBarMain.tool);
            DrawerLayout drawer = binding.drawerLayout;
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_fav, R.id.nav_nube, R.id.nav_almacenamiento)
                    .setDrawerLayout(drawer)
                    .build();
            NavController navController = Navigation.findNavController(mainActivity.this, R.id.nav_host_fragment_content_main);
            NavigationUI.setupActionBarWithNavController(mainActivity.this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(binding.navView, navController);


            //setters para metodos "Header View"
            View headerView = navigationView.getHeaderView(0);
            ImageView img = headerView.findViewById(R.id.nav_imageView);
            navigationView = (NavigationView) findViewById(R.id.nav_view);
            TextView correo = (TextView) headerView.findViewById(R.id.nav_correo);
            TextView userv = (TextView) headerView.findViewById(R.id.nav_nombre);

        //creacion de la vista "Header View"
        if (account!=null){

            userv.setText(account.getDisplayName());
            correo.setText(account.getEmail());
            Glide.with(getApplication()).load(account.getPhotoUrl()).override(img.getWidth(),img.getHeight()).into(img);
        }else if (user!=null){
            correo.setText(user.getEmail());
            userv.setText(user.getDisplayName());
            Glide.with(getApplication()).load(user.getPhotoUrl()).override(img.getWidth(),img.getHeight()).into(img);


        }

    }


    //Metodos para obtener el boton "atrás" al presionarlo
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }



    //Metodos generados
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;

    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();

    }

    //Llamada de salida para cerrar sesion en google y firebase
    public void Signout(MenuItem item) {
        FirebaseAuth.getInstance().signOut();
        googleSignInClient.signOut();
        startActivity(new Intent(mainActivity.this, login.class));
        Toast.makeText(mainActivity.this, "Ha cerrado sesion correctamente", Toast.LENGTH_SHORT).show();
        finish();
    }
    public void nube(View view){
        Navigation.findNavController(view).navigate(R.id.nav_nube);
    }
    public void fav(View view) {
        Navigation.findNavController(view).navigate(R.id.nav_fav);

    }
}