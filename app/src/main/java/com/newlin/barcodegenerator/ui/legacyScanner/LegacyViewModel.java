package com.newlin.barcodegenerator.ui.legacyScanner;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LegacyViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public LegacyViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Camera Functionality Coming Soon!");
    }

    public LiveData<String> getText() {
        return mText;
    }
}