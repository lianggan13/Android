package com.example.zhangliang.videoprojects.ui.skill;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SkillViewModel extends AndroidViewModel {

    private final MutableLiveData<String> markdown = new MutableLiveData<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public SkillViewModel(@NonNull Application application) {
        super(application);
        loadMarkdownFromAssets();
    }

    public LiveData<String> getMarkdown() {
        return markdown;
    }

    private void loadMarkdownFromAssets() {
        executor.execute(() -> {
            String md;
            try (InputStream is = getApplication().getAssets().open("skill.md");
                 BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append('\n');
                }
                md = sb.toString();
            } catch (Exception e) {
                android.util.Log.e("SkillViewModel", "read markdown failed", e);
                md = "无法读取文档";
            }
            // post value so it's safe from background thread
            markdown.postValue(md);
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdownNow();
    }
}