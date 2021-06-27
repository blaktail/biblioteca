package com.example.biblioteca.ui.almacena;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.biblioteca.MainActivity;
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
    public TextView txtPath;
    public Spinner spin;
    public CustomList customList;
    public File dir;
    public ArrayList<Integer> intSelected;
    public ArrayList<String> strSelected;
    String TAG = "menu";



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAlmacenamientoBinding.inflate(inflater, container, false);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        theNamesOfFiles = new ArrayList<String >();
        intImages = new ArrayList<Integer>();
        strSelected = new ArrayList<String>();
        intSelected = new ArrayList<Integer>();
        lst_Folder=(ListView) binding.lsvFolder.findViewById(R.id.lsvFolder);

        //Get txtvPath
        txtPath=(TextView) binding.txtpath.findViewById(R.id.txtpath);

        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
        {
            dirPath = String.valueOf(android.os.Environment.getExternalStorageDirectory());
        }

        RefreshListView();
        set_Adapter();
        setPath();

        Button btnParentDir = binding.btnParentDir.findViewById(R.id.btnParentDir);
        btnParentDir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onbackpress(view);
            }
        });
        lst_Folder.setOnItemLongClickListener(new android.widget.AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int i, long l) {
                registerForContextMenu(view);

                return false;

            }
        });

        binding.lsvFolder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try{
                    ParentdirPath = dirPath+"/..";
                    dirPath = dirPath+"/"+theNamesOfFiles.get(i);

                    File f = new File(dirPath);
                    if (f.isDirectory()){
                        RefreshListView();
                        RefreshAdapter();
                        setPath();
                    }else{
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setAction(android.content.Intent.ACTION_VIEW);
                        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        File file = new File(dirPath);

                        MimeTypeMap mime = MimeTypeMap.getSingleton();
                        String ext = file.getName().substring(file.getName().indexOf(".") + 1);
                        String type = mime.getMimeTypeFromExtension(ext);
                        intent.setDataAndType(Uri.fromFile(file), type);
                        startActivity(intent);
                    }

                }catch (Exception e){
                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });




        return binding.getRoot();
    }

    public void onbackpress(View v){
        if (!dirPath.equals("") && !dirPath.equals("/")){
            String[] folders = dirPath.split("\\/");
            String[] folders2={};
            folders2 = Arrays.copyOf(folders, folders.length-1);
            dirPath = TextUtils.join("/", folders2);
        }

        if (dirPath.equals("")){
            dirPath="/";
        }
        RefreshListView();
        RefreshAdapter();
        setPath();
    }

    private void setPath() {
        txtPath.setText(dirPath);
    }

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

            if(filelist[i].isDirectory()==true){
                intImages.add(R.drawable.folder);
            }else if(filelist[i].isFile()==true){
                intImages.add(R.drawable.file);
            }else{
                intImages.add(R.drawable.file);
            }
        }
    }catch (Exception e){
    }
    }

    private void  set_Adapter(){
        customList = new CustomList();
        lst_Folder.setAdapter(customList);
    }
    public void RefreshAdapter(){
        customList.notifyDataSetChanged();
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    public class CustomList extends BaseAdapter {

        @Override
        public int getCount() {
            return intImages.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            View view1 = getLayoutInflater().inflate(R.layout.custom_list_almacenamiento, null);

            ImageView imageView = (ImageView) view1.findViewById(R.id.ItemIcon);
            TextView txtPath = (TextView) view1.findViewById(R.id.ItemName);

            imageView.setImageResource(intImages.get(i));
            txtPath.setText(theNamesOfFiles.get(i));

            return view1;
        }
    }

    @Override
    public void onCreateContextMenu(@NonNull @NotNull ContextMenu menu,
                                    @NonNull @NotNull View v,  ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.options_menu_files,menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull @NotNull MenuItem item) {
        Log.d(TAG, "onContextItemSelected: "+item.getTitle());
        switch (item.getItemId()) {
            case R.id.open:
                Log.d(TAG, "onContextItemSelected: "+item.getItemId());
                return true;
            case R.id.upload:
                Log.d(TAG, "onContextItemSelected: "+item.getItemId());
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}