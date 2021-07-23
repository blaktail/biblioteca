package com.example.biblioteca.Adapters;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.biblioteca.Fragmentos.almacenamientoFragment;
import com.example.biblioteca.R;

import java.io.File;
import java.util.ArrayList;

public class favAdapter extends BaseAdapter {

    private final Activity context;
    private final ArrayList<String> files;

    /**
     *
     * @param context
     * @param files
     */
    public favAdapter(Activity context, ArrayList<String> files) {
        this.context = context;
        this.files = files;
    }

    @Override
    public int getCount() {
        return files.size();
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
    public View getView(int position, View view, ViewGroup viewGroup) {
        view = context.getLayoutInflater().inflate(R.layout.custom_grid_fav, null);
        ImageView img = (ImageView) view.findViewById(R.id.imageFav);
        TextView nametx = (TextView) view.findViewById(R.id.textName);
        TextView pathtx = (TextView) view.findViewById(R.id.textPath);
        File file = new File(files.get(position));
        nametx.setText(file.getName().trim());
        pathtx.setText(file.getPath());
        Log.d("fav", "getView: "+file.getPath());
        almacenamientoFragment alm = new almacenamientoFragment();

        img.setImageBitmap(alm.pdfToBitmap(file));

        return view;
    };

}
