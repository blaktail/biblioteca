package com.example.biblioteca.ui;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.biblioteca.Clases.Documento;
import com.example.biblioteca.Clases.NubeAdapter;
import com.example.biblioteca.R;
import com.example.biblioteca.databinding.FragmentNubeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class NubeFragment extends Fragment {

    private static final String TAG = "nube";
    private FragmentNubeBinding binding;
    ArrayList<String> name;
    ArrayList<String> imgs;
    Documento doc;
    NubeAdapter nubeAdapter;
    TextView textview;
    GridView gridView;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference myRef;
    ValueEventListener escucha;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user.getUid() == null){
            return null;
        }else {
        binding = FragmentNubeBinding.inflate(inflater, container, false);
        doc = new Documento();
        name = new ArrayList<String>();
        imgs = new ArrayList<String>();
        textview = binding.empynube.findViewById(R.id.empynube);
        gridView = binding.getRoot().findViewById(R.id.listnube);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Documentos");

        crearview();

        return binding.getRoot();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        myRef.removeEventListener(escucha);
        binding = null;
    }
    //Darle datos a el adapter
    private void crearview() {
            if (user!=null) {
                obtenerNube(user, this.getActivity());
            }

    }
    //Metodos para actualizar el adapter
    public void actualizarGrid(){
        nubeAdapter.notifyDataSetChanged();
        gridView.setColumnWidth(70);
        gridView.setNumColumns(2);
        gridView.setAdapter(nubeAdapter);
    }

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
                        if (ds!=null) {
                            Documento result = ds.getValue(Documento.class);
                            String id= result.getId_usuario();
                           if (id.equals(user.getUid())) {
                                imgs.add(result.getImg_pdf());
                                name.add(result.getNombre());
                                nubeAdapter = new NubeAdapter(getActivity(), imgs, name);
                                actualizarGrid();
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
    //downloadFile(context,result.getNombre(),"pdf",Environment.getExternalStorageDirectory()+File.separator+"BibliotecaAppDocumentos",result.getUrl());

    public long downloadFile(Context context, String fileName, String fileExtension, String destinationDirectory, String url) {
        DownloadManager downloadmanager = (DownloadManager) context.
                getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName + fileExtension);
        Log.d("nube", "downloadFile: ");
        return downloadmanager.enqueue(request);

    }

}




