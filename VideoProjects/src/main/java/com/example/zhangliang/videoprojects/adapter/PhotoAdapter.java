package com.example.zhangliang.videoprojects.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zhangliang.videoprojects.R;
import com.example.zhangliang.videoprojects.entity.PhotoEntity;
import com.example.zhangliang.videoprojects.util.ImageLoader;

import java.io.Serializable;
import java.util.List;

/**
 * @author: wei
 * @date: 2020-06-27
 **/
public class PhotoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<PhotoEntity> datas;
    private OnItemClickListener mOnItemClickListener;
    private OnItemChildClickListener mOnItemChildClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void setDatas(List<PhotoEntity> datas) {
        this.datas = datas;
    }

    public PhotoAdapter(Context context) {
        this.mContext = context;
    }

    public PhotoAdapter(Context context, List<PhotoEntity> datas) {
        this.mContext = context;
        this.datas = datas;
    }

    @Override
    public int getItemViewType(int position) {
        int type = datas.get(position).getType();
        return type;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 1) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_photo_one, parent, false);
            return new ViewHolderOne(view);
        } else if (viewType == 2) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_photo_two, parent, false);
            return new ViewHolderTwo(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_photo_three, parent, false);
            return new ViewHolderThree(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        PhotoEntity photoEntity = datas.get(position);
        ImageLoader imageLoader = new ImageLoader(mContext);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(photoEntity);
            }
        });

        if (type == 1) {
            ViewHolderOne vh = (ViewHolderOne) holder;
            vh.title.setText(photoEntity.getNewsTitle());
            vh.author.setText(photoEntity.getAuthorName());
            vh.comment.setText(photoEntity.getComment());
            vh.time.setText(photoEntity.getReleaseDate());
            vh.photoEntity = photoEntity;

            imageLoader.loadCircle(photoEntity.getHeaderUrl(), vh.header);
            if (photoEntity.getThumbEntities() != null && !photoEntity.getThumbEntities().isEmpty()) {
                final String imageUrl = photoEntity.getThumbEntities().get(0).getThumbUrl();
                vh.thumb.setOnClickListener(v -> {
                    if (mOnItemClickListener != null) mOnItemClickListener.onItemClick(imageUrl);
                });
                imageLoader.load(imageUrl, vh.thumb);
            }
        } else if (type == 2) {
            ViewHolderTwo vh = (ViewHolderTwo) holder;
            vh.title.setText(photoEntity.getNewsTitle());
            vh.author.setText(photoEntity.getAuthorName());
            vh.comment.setText(photoEntity.getComment());
            vh.time.setText(photoEntity.getReleaseDate());
            vh.photoEntity = photoEntity;

            imageLoader.loadCircle(photoEntity.getHeaderUrl(), vh.header);
            if (photoEntity.getThumbEntities() != null && photoEntity.getThumbEntities().size() >= 3) {

                final String url1 = photoEntity.getThumbEntities().get(0).getThumbUrl();
                final String url2 = photoEntity.getThumbEntities().get(1).getThumbUrl();
                final String url3 = photoEntity.getThumbEntities().get(2).getThumbUrl();

                imageLoader.load(url1, vh.pic1);
                imageLoader.load(url2, vh.pic2);
                imageLoader.load(url3, vh.pic3);

                vh.pic1.setOnClickListener(v -> {
                    if (mOnItemClickListener != null) mOnItemClickListener.onItemClick(url1);
                });
                vh.pic2.setOnClickListener(v -> {
                    if (mOnItemClickListener != null) mOnItemClickListener.onItemClick(url2);
                });
                vh.pic3.setOnClickListener(v -> {
                    if (mOnItemClickListener != null) mOnItemClickListener.onItemClick(url3);
                });
            }
        } else {
            ViewHolderThree vh = (ViewHolderThree) holder;
            vh.title.setText(photoEntity.getNewsTitle());
            vh.author.setText(photoEntity.getAuthorName());
            vh.comment.setText(photoEntity.getComment());
            vh.time.setText(photoEntity.getReleaseDate());
            vh.photoEntity = photoEntity;

            imageLoader.loadCircle(photoEntity.getHeaderUrl(), vh.header);
            if (photoEntity.getThumbEntities() != null && !photoEntity.getThumbEntities().isEmpty()) {
                final String imageUrl = photoEntity.getThumbEntities().get(0).getThumbUrl();
                vh.thumb.setOnClickListener(v -> {
                    if (mOnItemClickListener != null) mOnItemClickListener.onItemClick(imageUrl);
                });
                imageLoader.load(imageUrl, vh.thumb);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (datas != null && datas.size() > 0) {
            return datas.size();
        } else {
            return 0;
        }
    }

    public class ViewHolderOne extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView author;
        private TextView comment;
        private TextView time;
        private ImageView header;
        private ImageView thumb;
        private PhotoEntity photoEntity;

        public ViewHolderOne(@NonNull View view) {
            super(view);
            title = view.findViewById(R.id.title);
            author = view.findViewById(R.id.author);
            comment = view.findViewById(R.id.comment);
            time = view.findViewById(R.id.time);
            header = view.findViewById(R.id.header);
            thumb = view.findViewById(R.id.thumb);
        }
    }

    public class ViewHolderTwo extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView author;
        private TextView comment;
        private TextView time;
        private ImageView header;
        private ImageView pic1, pic2, pic3;
        private PhotoEntity photoEntity;

        public ViewHolderTwo(@NonNull View view) {
            super(view);
            title = view.findViewById(R.id.title);
            author = view.findViewById(R.id.author);
            comment = view.findViewById(R.id.comment);
            time = view.findViewById(R.id.time);
            header = view.findViewById(R.id.header);
            pic1 = view.findViewById(R.id.pic1);
            pic2 = view.findViewById(R.id.pic2);
            pic3 = view.findViewById(R.id.pic3);
        }
    }

    public class ViewHolderThree extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView author;
        private TextView comment;
        private TextView time;
        private ImageView header;
        private ImageView thumb;
        private PhotoEntity photoEntity;

        public ViewHolderThree(@NonNull View view) {
            super(view);
            title = view.findViewById(R.id.title);
            author = view.findViewById(R.id.author);
            comment = view.findViewById(R.id.comment);
            time = view.findViewById(R.id.time);
            header = view.findViewById(R.id.header);
            thumb = view.findViewById(R.id.thumb);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Serializable obj);
    }

    public interface OnItemChildClickListener {
        void onItemChildClick(int position);
    }
}
