package com.example.biblioteca.Clases;
import android.annotation.SuppressLint;
import android.app.Activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.pdf.PdfRenderer;
import android.os.AsyncTask;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.biblioteca.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class FilesAdapter extends BaseAdapter {

    private final Activity context;
    private final ArrayList<Bitmap> imagenes;
    private final  ArrayList<String> texto;



    public FilesAdapter(Activity context, ArrayList<Bitmap> imagenes, ArrayList<String> texto) {
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
        txtPath.setText(texto.get(position));

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.ic_loading_file)
                .error(R.drawable.ic_download_error);
        Glide.with(context).load(imagenes.get(position)).apply(options).into(imageView);


        return rowView;

    };


}