package com.example.biblioteca.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.biblioteca.R;

import org.jetbrains.annotations.NotNull;

public class InfoDialogFragment extends DialogFragment {


    String name,path,date;
    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        Bundle mArgs = getArguments();
        if (mArgs!=null){
             name = mArgs.getString("name");
             path = mArgs.getString("path");
             date = mArgs.getString("date");
        }
        AlertDialog.Builder build = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View inflatedView = inflater.inflate(R.layout.dialog_info_file, null);
        build.setView(inflatedView);

        TextView nametxv = inflatedView.findViewById(R.id.txv_name_info);
        TextView pathtxv = inflatedView.findViewById(R.id.txv_info_path);
        TextView datetxv = inflatedView.findViewById(R.id.txv_info_date);
        nametxv.setText(name);
        pathtxv.setText(path);
        datetxv.setText(date);

        return  build.create();
    }
}
