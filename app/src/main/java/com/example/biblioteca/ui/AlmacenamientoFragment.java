package com.example.biblioteca.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.pdf.PdfRenderer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.biblioteca.Clases.Documento;
import com.example.biblioteca.Clases.FilesAdapter;
import com.example.biblioteca.R;
import com.example.biblioteca.databinding.FragmentAlmacenamientoBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;


public class AlmacenamientoFragment extends Fragment {

    private FragmentAlmacenamientoBinding binding;
    public ListView lst_Folder;
    public String dirPath="";
    public String ParentdirPath="";
    public ArrayList<String> theNamesOfFiles;
    public ArrayList<Bitmap> intImages;
    public ArrayList<String> pathlist;
    public FilesAdapter filesAdapter;
    public File dir;
    public String path;
    public ArrayList<Integer> intSelected;
    String TAG = "fav";
    public FirebaseUser auth;
    FirebaseDatabase database;
    public FirebaseStorage storage;
    public DatabaseReference myRef;
    StorageReference storageRef;
    String ref;
    ArrayList<File> files;
    SharedPreferences sharedPreferences;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //Metodos para el uso de fragmentos
        binding = FragmentAlmacenamientoBinding.inflate(inflater, container, false);
        auth = FirebaseAuth.getInstance().getCurrentUser();
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Documentos");
        storageRef =storage.getReference("Archivos");
        files = new ArrayList<>();
        pathlist = new ArrayList<>();
        String[] archivos = getActivity().fileList();
        sharedPreferences = getActivity().getSharedPreferences("Archivos", MODE_PRIVATE);


        //Variables necesarias
        theNamesOfFiles = new ArrayList<String >();
        intImages = new ArrayList<Bitmap>();
        intSelected = new ArrayList<Integer>();


        //Variables para la vista
        lst_Folder=(ListView) binding.lsvFolder.findViewById(R.id.lsvFolder);
        registerForContextMenu(lst_Folder);

        //url para descargas
        File pathdescargas = new File(Environment.getExternalStorageDirectory()+File.separator+"BibliotecaAppDocumentos");


        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            dirPath = String.valueOf(Environment.getExternalStorageDirectory());
            Log.d("storage",dirPath);

        }
        //metodos necesarios para el uso de archivos
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        RefreshListView();
        set_Adapter();

        //Manejo del boton "Atrás"
        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                onbackpress();
            }
        });


        Button descargasb = binding.downloadMain.findViewById(R.id.download_main);
        descargasb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
                {
                    ///mounted
                    dirPath = String.valueOf(pathdescargas);
                    RefreshListView();
                    RefreshAdapter();
                }
            }
        });


        Button btnStoragesd = binding.sdStorage.findViewById(R.id.sd_storage);
        btnStoragesd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
                {
                    ///mounted
                    dirPath = String.valueOf(Environment.getExternalStorageDirectory());

                    RefreshListView();
                    RefreshAdapter();
                }
            }
        });


        //Manejo de carpetas y archivos
        binding.lsvFolder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try{
                    ParentdirPath = dirPath;
                    dirPath = dirPath+"/"+theNamesOfFiles.get(i);

                    File f = new File(dirPath);
                    if (f.isDirectory()){
                        RefreshListView();
                        RefreshAdapter();
                    }else{
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        File file = new File(dirPath);

                        String type = extencion(file);
                        intent.setDataAndType(Uri.fromFile(file), type);
                        startActivity(intent);
                        dirPath=ParentdirPath;
                    }

                }catch (Exception e){
                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                    Log.d("storage",e.toString());
                }
            }
        });

        //Metodos generados
        lst_Folder.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

       return binding.getRoot();
    }
    //manejo de el boton "Atrás"
    public void onbackpress(){

        if (!dirPath.equals(String.valueOf(android.os.Environment.getExternalStorageDirectory()))){
            String[] folders = dirPath.split("\\/");
            String[] folders2={};
            folders2 = Arrays.copyOf(folders, folders.length-1);
            dirPath = TextUtils.join("/", folders2);
        }

        RefreshListView();
        RefreshAdapter();


    }
    //Metodos para manejos de archivos

    //Metodos para manejos de archivos
    private void RefreshListView() {
        ///
        try{
        dir = new File(dirPath);
        File[] filelist = dir.listFiles();
        Bitmap file = BitmapFactory.decodeResource(getResources(),R.drawable.file);
        Bitmap folder = BitmapFactory.decodeResource(getResources(),R.drawable.folder);
        theNamesOfFiles.clear();
        intImages.clear();
            for (int i = 0; i < filelist.length; i++) {
                    theNamesOfFiles.add(filelist[i].getName());
                    if(filelist[i].isDirectory()){
                        intImages.add(folder);
                    }
                    else if(filelist[i].isFile()) {
                        intImages.add(getBitmap(filelist[i]));
                    }
                    else if (extencion(filelist[i]).equals("application/pdf")){
                        intImages.add(pdfToBitmap(filelist[i]));
                    }else{
                        intImages.add(file);
                    }
               }
        }catch (Exception e){
            Log.d(TAG, "RefreshListView: "+e.toString());
        }
    }
    //metodo para obtener las imagenes de archivos
    private Bitmap getBitmap(File file){
        return ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(file.getPath()),100,100);
    }
    //Metodos para el manejo de archivos
    private void  set_Adapter(){
         filesAdapter = new FilesAdapter(this.getActivity(),intImages,theNamesOfFiles);

         lst_Folder.setAdapter(filesAdapter);
    }

    //Metodos para el manejo de archivos

    public void RefreshAdapter(){
        filesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onCreateContextMenu(@NonNull @NotNull ContextMenu menu,
                                    @NonNull @NotNull View v,  ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if (v.getId()==R.id.lsvFolder){
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.options_menu_files,menu);
        }

    }

    //Creacion del menu de acciones en archivos
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onContextItemSelected(@NonNull @NotNull MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.info:
                path = dirPath+"/"+theNamesOfFiles.get(info.position);
                File file = new File(path);
                info(file);
                return true;
            case R.id.save:
                path = dirPath+"/"+theNamesOfFiles.get(info.position);
                file = new File(path);
                Documento doc = new Documento();
                String ext = file.getName().substring(file.getName().indexOf(".") + 1);
                if (ext.equals("pdf")){
                    carga(file.getName(),file,this.getContext(),getActivity().getSupportFragmentManager());
                }else {
                    Toast.makeText(getContext(),"Solo Archivos '.pdf'", Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.delete:
                path = dirPath+"/"+theNamesOfFiles.get(info.position);
                file = new File(path);
                delete(file);
                return true;
            case R.id.favorite:
                path = dirPath+"/"+theNamesOfFiles.get(info.position);
                file = new File(path);
                if (!file.isDirectory()){
                    if (extencion(file).equals("application/pdf")){
                        favorite(file);
                        Toast.makeText(getContext(),"Archivo guardado como favorito",Toast.LENGTH_SHORT).show();

                    }else {
                        Toast.makeText(getContext(),"Solo Archivos PDF",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getContext(),"Solo Archivos PDF",Toast.LENGTH_SHORT).show();
                }

                return true;
            case R.id.rename:
                path = dirPath+"/"+theNamesOfFiles.get(info.position);
                file = new File(path);
                renombrar(file);
                return true;
            default:
                Toast.makeText(getContext(),"error, default", Toast.LENGTH_LONG).show();
                return true;
        }

    }

    private void favorite(File file) {
        pathlist.clear();
        Set<String> set = new HashSet<String>();
        set = sharedPreferences.getStringSet("files", null);
        if (set!=null){
            pathlist.addAll(set);
            Log.d(TAG, "favorite: no soy null");
        }else{
            Log.d(TAG, "favorite: soy null");
        }
        pathlist.add(file.toPath().toString());
        set = new HashSet<String>(pathlist);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("files", set);
        editor.commit();
        Log.d(TAG, "favorite: "+set.toString());
    }


    private void info(File file) {
        long createdDate = file.lastModified();
        DialogFragment newFragment = new InfoDialogFragment();
        Bundle args = new Bundle();
        args.putString("name",file.getName());
        args.putString("path",path);
        args.putString("date",String.valueOf(createdDate));
        args.putString("size", String.valueOf(file.getTotalSpace()));
        newFragment.setArguments(args);
        newFragment.show(getActivity().getSupportFragmentManager(), "Informacion");
    }

    private void delete(File file) {
        List<String> command = new ArrayList<String>();
        try {
            command.clear();
            command.add("/system/bin/rm");
            command.add("-rf");
            command.add(file.toString());
            // start the subprocess
            ProcessBuilder pb = new ProcessBuilder(command);
            Process process = pb.start();
            process.waitFor();
            //Refresh ListView

            RefreshListView();
            RefreshAdapter();
            Toast.makeText(getContext(), file.getName()+" Eliminado correctamente", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void renombrar(File file) {

        try{
                //RenameFolder Dialog Builder
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                LayoutInflater inflater = this.getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.dialog_rename_file, null);
                dialogBuilder.setView(dialogView);

                final EditText newname = (EditText) dialogView.findViewById(R.id.newname);
            newname.setText(file.getName());
                dialogBuilder.setTitle("Renombrar");
                dialogBuilder.setMessage("Ingrese el nuevo nombre:");
                dialogBuilder.setPositiveButton("Listo", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        File f = new File(file.toString());
                        extencion(file);
                        File fRename = new File(dirPath+"/"+newname.getText().toString()+"."+file.getName().substring(file.getName().indexOf(".") + 1));
                        f.renameTo(fRename);
                        //Refresh ListView
                        RefreshListView();
                        RefreshAdapter();
                    }
                });
                dialogBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //pass
                    }
                });
                AlertDialog b = dialogBuilder.create();
                b.show();
        }catch (Exception e){
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        RefreshAdapter();
        set_Adapter();
    }

    public String extencion(File file){
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String ext = file.getName().substring(file.getName().indexOf(".") + 1);
        Log.d(TAG, "extencion: "+ext);
        return mime.getMimeTypeFromExtension(ext);
    }

    public void carga(String nombre, File path, Context context, FragmentManager fragmentManager) {
        Documento doc = new Documento();
        //preparacion de pdf
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(new File(String.valueOf(path)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //creacion de imagen
        Bitmap image  = pdfToBitmap(new File(String.valueOf(path)));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        //

        AlertDialog.Builder builder
                = new AlertDialog
                .Builder(context);
        builder.setMessage("Cargando Archivo...");
        builder.setCancelable(false);
        AlertDialog alertDialog = builder.create();
        ref = myRef.push().getKey();
        UploadTask imagetask = (UploadTask) storageRef.child(ref).child("img").putBytes(data);
        imagetask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> result = Objects.requireNonNull(Objects.requireNonNull(taskSnapshot.getMetadata()).getReference()).getDownloadUrl();
                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        doc.setImg_pdf(uri.toString());
                        myRef.child(ref).setValue(doc);
                    }
                });
            }
        });
        UploadTask uploadtask = storageRef.child(ref).child("pdf").putStream(stream);
        uploadtask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Toast.makeText(context,"Carga de pdf correcta",Toast.LENGTH_SHORT).show();
                Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                            doc.setUrl(uri.toString());
                            doc.setId_usuario(auth.getUid());
                            doc.setNombre(nombre);
                            doc.setFecha(Calendar.getInstance().getTime().toString());
                            myRef.child(ref).setValue(doc);
                            alertDialog.dismiss();
                            String titulo=("Se ha cargado correctamente el archivo, ¿Desea verlo en su Nube personal?");
                            String mensaje=("Archivo Cargado");
                            showAlertDialog(context,mensaje,titulo, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    switch (i){
                                        case -1:
                                                /* context.getApplicationContext().d
                                                 NubeFragment nextFrag= new NubeFragment();
                                                 fragmentManager.beginTransaction().
                                                         .replace(R.id.nav_almacenamiento, nextFrag)
                                                         .addToBackStack(null)
                                                         .commit();*/

                                            Log.d("nube", "onClick: "+i);
                                        case -2:dialogInterface.cancel();
                                        default:dialogInterface.cancel();
                                    }
                                    Log.d("nube", "onClick: "+i);
                                }
                            });


                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(context,"error al cargar pdf",Toast.LENGTH_SHORT).show();
                    }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull @NotNull UploadTask.TaskSnapshot snapshot) {
                alertDialog.setCancelable(false);
                alertDialog.show();
            }
        });
    }

    public Bitmap pdfToBitmap(File pdfFile) {
        Bitmap bitmap = null;
        try {
            PdfRenderer renderer = new PdfRenderer(ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY));
            final int pageCount = renderer.getPageCount();
            if(pageCount>0){
                PdfRenderer.Page page = renderer.openPage(0);
                int width = (int) (page.getWidth());
                int height = (int) (page.getHeight());
                bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

                Canvas canvas = new Canvas(bitmap);
                canvas.drawColor(Color.WHITE);
                canvas.drawBitmap(bitmap, 0, 0, null);

                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                page.close();
                renderer.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return bitmap;
    }

    public void showAlertDialog(Context context, String title ,String msg, DialogInterface.OnClickListener listener){
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton("Ver", (DialogInterface.OnClickListener) listener);
        builder.setNegativeButton("Ok",(DialogInterface.OnClickListener) listener);
        builder.create().show();
    }

}
