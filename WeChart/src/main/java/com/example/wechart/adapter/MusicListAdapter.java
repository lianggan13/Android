package com.example.wechart.adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wechart.R;
import com.example.wechart.bean.Music;
import com.example.wechart.bean.NotepadBean;
import com.example.wechart.databinding.ItemMusicBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MusicListAdapter extends RecyclerView.Adapter<MusicListAdapter.ViewHolder> {

    List<Music> musicList = new ArrayList<>();

    private MediaPlayer mediaPlayer;
    private Uri uri;

    @NonNull
    @Override
    public MusicListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemMusicBinding binding = ItemMusicBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicListAdapter.ViewHolder holder, int position) {
        Music music = musicList.get(position);
        holder.binding.musicTv.setText(music.getName());
        // holder.binding.artistTv.setText(music.getArtist());


        //点击
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playMusic(v.getContext(),music);
            }
        });
    }

    private void playMusic(Context context, Music music) {

        if(music.getUri().equals(this.uri)){   //点击过了
            if (mediaPlayer.isPlaying()){
                mediaPlayer.pause();
            }else{
                mediaPlayer.start();
            }
        }else{
            //第一次播放  or 切换另一首音乐，停止当前音乐
            if (mediaPlayer!= null){
                mediaPlayer.stop();
                mediaPlayer.release();
            }

            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(context,music.getUri());
                mediaPlayer.prepare();
                mediaPlayer.start();
                this.uri = music.getUri();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    public void update(List<Music> list) {
        this.musicList.clear();
        this.musicList.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ItemMusicBinding binding;
        public ViewHolder(@NonNull ItemMusicBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}