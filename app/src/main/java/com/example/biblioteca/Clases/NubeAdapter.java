package com.example.biblioteca.Clases;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.biblioteca.R;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class NubeAdapter extends BaseAdapter {
    private final Activity context;
    private final ArrayList<String> imagenes;
    private final ArrayList<String> texto;


    public NubeAdapter(Activity context, ArrayList<String> imagenes, ArrayList<String> texto) {
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

    public View getView(int position, View view, ViewGroup viewGroup) {
        view = context.getLayoutInflater().inflate(R.layout.custom_grid_view_nube, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        TextView textView = (TextView) view.findViewById(R.id.textView);
        try {
            URL url = new URL(imagenes.get(position));
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.ic_download)
                    .error(R.drawable.ic_download_error);
            Glide.with(context).load(url).apply(options).override(imageView.getWidth(),imageView.getHeight()).into(imageView);
            textView.setText(texto.get(position));

        } catch(IOException e) {
            System.out.println(e);
        }

        return view;
    }


}

