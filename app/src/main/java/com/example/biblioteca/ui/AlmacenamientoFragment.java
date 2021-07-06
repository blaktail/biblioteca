package com.example.biblioteca.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.biblioteca.Clases.FilesAdapter;
import com.example.biblioteca.R;
import com.example.biblioteca.databinding.FragmentAlmacenamientoBinding;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;



public class AlmacenamientoFragment extends Fragment {

    private FragmentAlmacenamientoBinding binding;
    public ListView lst_Folder;
    public String dirPath="";
    public String ParentdirPath="";
    public ArrayList<String> theNamesOfFiles;
    public ArrayList<Integer> intImages;
    public FilesAdapter filesAdapter;
    public File dir;
    public String path;
    public ArrayList<Integer> intSelected;
    public ArrayList<String> strSelected;
    String TAG = "menu";



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //Metodos para el uso de fragmentos
        binding = FragmentAlmacenamientoBinding.inflate(inflater, container, false);


        //Variables necesarias
        theNamesOfFiles = new ArrayList<String >();
        intImages = new ArrayList<Integer>();
        strSelected = new ArrayList<String>();
        intSelected = new ArrayList<Integer>();

        lst_Folder=(ListView) binding.lsvFolder.findViewById(R.id.lsvFolder);
        registerForContextMenu(lst_Folder);

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

                        MimeTypeMap mime = MimeTypeMap.getSingleton();
                        String ext = file.getName().substring(file.getName().indexOf(".") + 1);
                        String type = mime.getMimeTypeFromExtension(ext);
                        Log.d(TAG, "onItemClick: "+ext+" type:"+type);

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
        try{
        dir = new File(dirPath);
        File[] filelist = dir.listFiles();

        //reset ArrayLists
        theNamesOfFiles.clear();
        intImages.clear();

        for (int i = 0; i < filelist.length; i++) {

            theNamesOfFiles.add(filelist[i].getName());
            //   intImages[i] = R.drawable.folder;

            if(filelist[i].isDirectory()){
                intImages.add(R.drawable.folder);
            }else if(filelist[i].isFile()) {
                intImages.add(R.drawable.file);

            }else{
                intImages.add(R.drawable.file);
            }
        }
    }catch (Exception e){
            Log.d(TAG, "RefreshListView: "+e.toString());
        }
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

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onContextItemSelected(@NonNull @NotNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.info:
                path = dirPath+"/"+theNamesOfFiles.get(info.position);
                int img = intImages.get(info.position);
                File file = new File(path);
                long createdDate = file.lastModified();
                DialogFragment newFragment = new InfoDialogFragment();
                Bundle args = new Bundle();
                args.putString("name",file.getName());
                args.putString("path",path);
                args.putString("date",String.valueOf(createdDate));
                args.putString("size", String.valueOf(file.getTotalSpace()));
                args.putInt("img",img);
                Log.d(TAG, "onContextItemSelected: "+img);
                Toast.makeText(getContext(),String.valueOf(createdDate), Toast.LENGTH_LONG).show();
                newFragment.setArguments(args);
                newFragment.show(getActivity().getSupportFragmentManager(), "Informacion");


                return false;
            case R.id.upload:
                path = dirPath+"/"+theNamesOfFiles.get(info.position);
                Toast.makeText(getContext(),path, Toast.LENGTH_LONG).show();

                Log.d(TAG, "onContextItemSelected: "+path);

                return true;
            case R.id.delete:
                Toast.makeText(getContext(),"Borrar,Agregar dialogo", Toast.LENGTH_LONG).show();

            default:
                Toast.makeText(getContext(),"error, default", Toast.LENGTH_LONG).show();

                return super.onContextItemSelected(item);
        }
    }


}