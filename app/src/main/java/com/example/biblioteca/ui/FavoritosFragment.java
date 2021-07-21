package com.example.biblioteca.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.biblioteca.Clases.FavAdapter;
import com.example.biblioteca.R;
import com.example.biblioteca.databinding.FragmentFavoritosBinding;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

/**
 * Fragmento Favoritos
 */
public class FavoritosFragment extends Fragment {

    private FragmentFavoritosBinding binding;
    ArrayList<String> pathlist;
    GridView listafav;
    FavAdapter favAdapter;
    public String TAG = "fav";
    SharedPreferences sharedPreferences;

    /**
     * Creacion de vista
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFavoritosBinding.inflate(inflater, container, false);
        pathlist = new ArrayList<String>();
        listafav = binding.listafav.findViewById(R.id.listafav);
        registerForContextMenu(listafav);
        sharedPreferences = getActivity().getSharedPreferences("Archivos", MODE_PRIVATE);
        crearvalores();
        set_adapter();
        return binding.getRoot();
    }

    /**
     * Destrucion de vista
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Craeacion del adapter para el gridview
     */
    public void crearvalores(){
        pathlist.clear();
        Set<String> set = new HashSet<String>();
        set = sharedPreferences.getStringSet("files", null);
        if (set!=null){
            Log.d(TAG, "crearvalores: set= "+set.toString());
            pathlist.addAll(set);
        }
    }

    /**
     * asignacion del adapter ya rellenado
     */
    public void set_adapter(){
        Log.d(TAG, "set_adapter: "+pathlist.toArray().length);
        favAdapter = new FavAdapter(this.getActivity(),pathlist);
        listafav.setNumColumns(2);
        listafav.setAdapter(favAdapter);
        Log.d(TAG, "set_adapter: "+listafav.getAdapter().isEmpty());
    }

    /**
     * Metodo par la actualizacion del adapter
     */
    public void notificar(){
        favAdapter.notifyDataSetChanged();
    }

    /**
     * Metodo para el la cracion y asignacion del menu que se genera sobre cada archivo del fragment Favoritos
     * @param menu
     * @param v
     * @param menuInfo
     */
    @Override
    public void onCreateContextMenu(@NonNull @NotNull ContextMenu menu, @NonNull @NotNull View v, @Nullable @org.jetbrains.annotations.Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

    }

    /**
     * Metodo para la respuesta ante el menu generado
     * @param item
     * @return
     */

    @Override
    public boolean onContextItemSelected(@NonNull @NotNull MenuItem item) {
        return super.onContextItemSelected(item);
    }
}