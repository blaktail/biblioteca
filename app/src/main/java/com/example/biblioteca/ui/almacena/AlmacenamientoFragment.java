package com.example.biblioteca.ui.almacena;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.biblioteca.databinding.FragmentAlmacenamientoBinding;

public class AlmacenamientoFragment extends Fragment {

    private AlmacenamientoViewModel almacenamientoViewModel;
    private FragmentAlmacenamientoBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        almacenamientoViewModel =
                new ViewModelProvider(this).get(AlmacenamientoViewModel.class);

        binding = FragmentAlmacenamientoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textSlideshow;
        almacenamientoViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}