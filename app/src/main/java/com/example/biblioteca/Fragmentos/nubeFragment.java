package com.example.biblioteca.Fragmentos;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.biblioteca.Adapters.nubeAdapter;
import com.example.biblioteca.Clases.Documento;
import com.example.biblioteca.R;
import com.example.biblioteca.databinding.FragmentNubeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;


public class nubeFragment extends Fragment {

    private static final String TAG = "nube";
    private FragmentNubeBinding binding;
    ArrayList<String> name;
    ArrayList<String> imgs;
    Documento doc;
    com.example.biblioteca.Adapters.nubeAdapter nubeAdapter;
    TextView textview;
    GridView gridView;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference myRef;
    ValueEventListener escucha;
    ArrayList<Object> url;
    ArrayList<Object> date;
    ArrayList<DataSnapshot> ads;
    File pathdescargas;
    ArrayList<Documento> documentos;

    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        pathdescargas = new File(Environment.getExternalStorageDirectory()+File.separator+"BibliotecaAppDocumentos");
        user = FirebaseAuth.getInstance().getCurrentUser();
        binding = FragmentNubeBinding.inflate(inflater, container, false);
        doc = new Documento();
        name = new ArrayList<String>();
        imgs = new ArrayList<String>();
        url = new ArrayList<>();
        date = new ArrayList<>();
        ads= new ArrayList<>();
        documentos = new ArrayList<>();
        textview = binding.empynube.findViewById(R.id.empynube);
        gridView = binding.getRoot().findViewById(R.id.listnube);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Documentos");
        registerForContextMenu(gridView);
        crearview();

        return binding.getRoot();
    }

    /**
     *
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        myRef.removeEventListener(escucha);
        binding = null;
    }

    /**
     *
     */
    //Darle datos a el adapter
    private void crearview() {
            if (user!=null) {
                obtenerNube(user, this.getActivity());
            }

    }

    /**
     *
     */
    //Metodos para actualizar el adapter
    public void actualizarGrid(){
        nubeAdapter.notifyDataSetChanged();
    }

    /**
     *
     */
    public void set_adapter(){
        gridView.setColumnWidth(70);
        gridView.setNumColumns(2);
        gridView.setAdapter(nubeAdapter);
    }

    /**
     *
     * @param user
     * @param context
     */
    //Obtener los datos de firebase de forma Asincrona
    public void obtenerNube(FirebaseUser user, Activity context) {
        Toast.makeText(getActivity(),"Buscando en la nube...",Toast.LENGTH_SHORT).show();
        if (imgs.size()==0&&name.size()==0){
            textview.setVisibility(View.VISIBLE);
        }

        myRef.addValueEventListener(escucha = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                        if (ds!=null&&user!=null) {
                            Documento result = ds.getValue(Documento.class);
                            String id= result.getId_usuario();
                           if (id.equals(user.getUid())) {
                                imgs.add(result.getImg_pdf());
                                name.add(result.getNombre());
                                url.add(result.getUrl());
                                date.add(result.getFecha());
                                ads.add(ds);
                                documentos.add(result);
                                nubeAdapter = new nubeAdapter(getActivity(), imgs, name);

                                actualizarGrid();
                                set_adapter();
                                textview.setVisibility(View.INVISIBLE);
                            }
                        }
                }

            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


    }

    /**
     *
     * @param menu
     * @param v
     * @param menuInfo
     */
    @Override
    public void onCreateContextMenu(@NonNull @NotNull ContextMenu menu, @NonNull @NotNull View v, @Nullable @org.jetbrains.annotations.Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.listnube){
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.options_menu_nube,menu);
        }

    }

    /**
     *
     * @param item
     * @return
     */
    @Override
    public boolean onContextItemSelected(@NonNull @NotNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.download_nube: download((String) url.get(info.position),name.get(info.position));
            return true;
            case R.id.delete: delete(ads.get(info.position));
            return true;
            case R.id.info: info(documentos.get(info.position));
            return true;
            default:
                Log.d(TAG, "onContextItemSelected: Default entregado");
            return true;
        }

    }

    /**
     *
     * @param dataSnapshot
     */
    private void delete(DataSnapshot dataSnapshot){
        DatabaseReference ref = dataSnapshot.getRef();
        Log.d(TAG, "delete: "+ref.toString());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                snapshot.getRef().removeValue();
                cleararrays();
                actualizarGrid();
                Toast.makeText(getContext(),"Archivo borrado de la nube...",Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    /**
     *
     * @param documento
     */
    private void info(Documento documento) {
        Toast.makeText(getActivity(),"Aun no creado",Toast.LENGTH_SHORT).show();
        DialogFragment newFragment = new infoDialogFragment();
        Bundle args = new Bundle();
        args.putString("name",documento.getNombre());
        args.putString("path",documento.getUrl());
        args.putString("date",documento.getFecha());
        args.putString("img_pdf",documento.getImg_pdf());
        newFragment.setArguments(args);
        newFragment.show(getActivity().getSupportFragmentManager(), "Informacion");
    }

    /**
     * Item del menu para descargar el archivo
     * @param url
     * @param s
     */

    private void download(String url, String s) {
        Uri u = Uri.parse(url);
        Log.d(TAG, "open: "+u.toString());
        Intent intent = new Intent(Intent.ACTION_VIEW,u);
        startActivity(intent);
    }

    /**
     * Llamado a metodos de limpieza para la actualizacion de
     */
    public void cleararrays(){
        imgs.clear();
        name.clear();
        url.clear();
        ads.clear();
        date.clear();
        documentos.clear();
    }


    /*
    public long downloadFile(Context context, String fileName, String destinationDirectory, String url) {
        DownloadManager downloadmanager = (DownloadManager) context.
                getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        //request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName );
        return downloadmanager.enqueue(request);

    }

     */

}




