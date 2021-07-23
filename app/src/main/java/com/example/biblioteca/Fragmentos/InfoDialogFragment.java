package com.example.biblioteca.Fragmentos;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.example.biblioteca.R;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class InfoDialogFragment extends DialogFragment {


    private String name,path,date,img_pdf;
    private byte[] img;

    public InfoDialogFragment() {
    }

    /**
     *
     * @param savedInstanceState
     * @return
     */
    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        Bundle mArgs = getArguments();
        if (mArgs!=null){
             name = mArgs.getString("name");
             path = mArgs.getString("path");
             date = mArgs.getString("date");
             img = mArgs.getByteArray("img");
             img_pdf = mArgs.getString("img_pdf");
        }
        AlertDialog.Builder build = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View inflatedView = inflater.inflate(R.layout.dialog_info_file, null);
        build.setView(inflatedView);

        TextView nametxv = inflatedView.findViewById(R.id.txv_name_info);
        TextView pathtxv = inflatedView.findViewById(R.id.txv_info_path);
        TextView datetxv = inflatedView.findViewById(R.id.txv_info_date);
        ImageView imageView = inflatedView.findViewById(R.id.imageView_info);


        if (img!=null){
            Bitmap bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
            bitmap = Bitmap.createScaledBitmap(bitmap,imageView.getMaxWidth(),imageView.getMaxHeight(),true);
            imageView.setImageBitmap(bitmap);
        }else {
            Glide.with(getContext()).load(img_pdf).override(imageView.getMaxWidth(),imageView.getMaxHeight()).into(imageView);
        }

        nametxv.setText(name);
        pathtxv.setText(path);
        Date dates = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        date = dateFormat.format(dates);

        datetxv.setText(date.trim());

        return  build.create();
    }
}
