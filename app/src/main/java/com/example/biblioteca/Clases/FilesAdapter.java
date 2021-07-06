package com.example.biblioteca.Clases;
import android.annotation.SuppressLint;
import android.app.Activity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.biblioteca.R;

import java.util.ArrayList;

public class FilesAdapter extends BaseAdapter {

    private final Activity context;
    private final ArrayList<Integer> imagenes;
    private final  ArrayList<String> texto;

    public FilesAdapter(Activity context, ArrayList<Integer> imagenes, ArrayList<String> texto) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.imagenes = imagenes;
        this.texto = texto;
    }


    @Override
    public int getCount() {
        return imagenes.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public View getView(int position, View view, ViewGroup parent) {


        View rowView = context.getLayoutInflater().inflate(R.layout.custom_list_almacenamiento, null);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.ItemIcon);
        TextView txtPath = (TextView) rowView.findViewById(R.id.ItemName);

        imageView.setImageResource(imagenes.get(position));
        txtPath.setText(texto.get(position));


        return rowView;

    };
}