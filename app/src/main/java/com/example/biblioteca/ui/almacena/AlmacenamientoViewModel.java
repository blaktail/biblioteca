package com.example.biblioteca.ui.almacena;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AlmacenamientoViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public AlmacenamientoViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}