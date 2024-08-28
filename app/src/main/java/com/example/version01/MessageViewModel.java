package com.example.version01;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MessageViewModel extends ViewModel {
    private final MutableLiveData<String> messageLiveData = new MutableLiveData<>();

    public void setMessage(String message) {
        messageLiveData.setValue(message);
    }

    public LiveData<String> getMessage() {
        return messageLiveData;
    }
}
