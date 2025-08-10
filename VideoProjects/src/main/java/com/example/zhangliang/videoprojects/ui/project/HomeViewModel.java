package com.example.zhangliang.videoprojects.ui.project;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.zhangliang.videoprojects.entity.NewsEntity;

import java.util.List;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private MutableLiveData<List<NewsEntity>> newsList = new MutableLiveData<>();

    public LiveData<List<NewsEntity>> getNewsList() {
        return newsList;
    }

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public void setNewsList(List<NewsEntity> list) {
        newsList.setValue(list);
    }
}