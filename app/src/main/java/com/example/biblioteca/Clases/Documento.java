package com.example.biblioteca.Clases;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

public class Documento {
    FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    String Nombre,Id_doc,Id_usuario,url;
    //getters and setters
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getId_doc() {
        return Id_doc;
    }
    public void setId_doc(String id_doc) { Id_doc = id_doc; }
    public String getId_usuario() {
        return auth.getUid();
    }
    public void setId_usuario() {
        Id_usuario = auth.getUid();
    }
    public String getNombre() {
        return Nombre;
    }
    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    //metodo para cargar el archivo a la db y
    public void carga(String nombre, File path, Context context) {
        Documento doc = new Documento();
        DatabaseReference myRef = database.getReference("Documentos");
        StorageReference storageRef =storage.getReference("Archivos");
        InputStream stream = null;
        try {
            stream = new FileInputStream(new File(String.valueOf(path)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (stream!=null){
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setCustomMetadata("usuario",doc.getId_usuario())
                    .build();
           UploadTask uploadtask = storageRef.child(nombre).putStream(stream,metadata);
           uploadtask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(context,"Carga de pdf correcta",Toast.LENGTH_SHORT).show();
                            Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    doc.setUrl(uri.toString());
                                    doc.setId_usuario();
                                    doc.setNombre(nombre);
                                    doc.setId_doc(myRef.push().getKey());
                                    if (myRef.push().getKey()!=null){
                                        myRef.child(myRef.push().getKey()).setValue(doc);
                                    }
                                    AlertDialog.Builder builder
                                            = new AlertDialog
                                            .Builder(context);
                                    builder.setMessage("Se ha cargado correctamente el archivo, ¿Desea verlo en su Nube personal?");
                                    builder.setTitle("Archivo Cargado");
                                    builder
                                            .setPositiveButton(
                                                    "Ver + documento",
                                                    new DialogInterface
                                                            .OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog,
                                                                            int which)
                                                        {
                                                            dialog.cancel();
                                                        }
                                                    });
                                    builder.setNegativeButton(
                                                    "No",
                                                    new DialogInterface
                                                            .OnClickListener() {

                                                        @Override
                                                        public void onClick(DialogInterface dialog,
                                                                            int which)
                                                        {
                                                            dialog.cancel();
                                                        }
                                                    });
                                    AlertDialog alertDialog = builder.create();
                                    alertDialog.show();
                                }
                            });
                        }
                    })
           .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    Toast.makeText(context,"error al cargar pdf",Toast.LENGTH_SHORT).show();
                }
           });
        }
    }
}