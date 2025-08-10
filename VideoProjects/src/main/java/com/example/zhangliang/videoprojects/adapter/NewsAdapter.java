package com.example.zhangliang.videoprojects.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zhangliang.videoprojects.R;
import com.example.zhangliang.videoprojects.entity.NewsEntity;
import com.example.zhangliang.videoprojects.util.CircleTransform;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import xyz.doikki.videocontroller.component.PrepareView;

/**
 * @author: wei
 * @date: 2020-06-27
 **/
public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<NewsEntity> datas;
    private OnItemClickListener mOnItemClickListener;
    private OnItemChildClickListener mOnItemChildClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void setDatas(List<NewsEntity> datas) {
        this.datas = datas;
    }

    public NewsAdapter(Context context) {
        this.mContext = context;
    }

    public NewsAdapter(Context context, List<NewsEntity> datas) {
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
            View view = LayoutInflater.from(mContext).inflate(R.layout.news_item_one, parent, false);
            return new ViewHolderOne(view);
        } else if (viewType == 2) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.news_item_two, parent, false);
            return new ViewHolderTwo(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.news_item_three, parent, false);
            return new ViewHolderThree(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        NewsEntity newsEntity = datas.get(position);

        // 公共方法：根据url或资源名加载图片
        ImageLoader imageLoader = new ImageLoader(mContext);

        if (type == 1) {
            ViewHolderOne vh = (ViewHolderOne) holder;
            vh.title.setText(newsEntity.getNewsTitle());
            vh.author.setText(newsEntity.getAuthorName());
            vh.comment.setText(newsEntity.getCommentCount() + "评论 .");
            vh.time.setText(newsEntity.getReleaseDate());
            vh.newsEntity = newsEntity;

            imageLoader.loadCircle(newsEntity.getHeaderUrl(), vh.header);
            if (newsEntity.getThumbEntities() != null && !newsEntity.getThumbEntities().isEmpty()) {
                imageLoader.load(newsEntity.getThumbEntities().get(0).getThumbUrl(), vh.thumb);
            }
        } else if (type == 2) {
            ViewHolderTwo vh = (ViewHolderTwo) holder;
            vh.title.setText(newsEntity.getNewsTitle());
            vh.author.setText(newsEntity.getAuthorName());
            vh.comment.setText(newsEntity.getCommentCount() + "评论 .");
            vh.time.setText(newsEntity.getReleaseDate());
            vh.newsEntity = newsEntity;

            imageLoader.loadCircle(newsEntity.getHeaderUrl(), vh.header);
            if (newsEntity.getThumbEntities() != null && newsEntity.getThumbEntities().size() >= 3) {
                imageLoader.load(newsEntity.getThumbEntities().get(0).getThumbUrl(), vh.pic1);
                imageLoader.load(newsEntity.getThumbEntities().get(1).getThumbUrl(), vh.pic2);
                imageLoader.load(newsEntity.getThumbEntities().get(2).getThumbUrl(), vh.pic3);
            }
        } else {
            ViewHolderThree vh = (ViewHolderThree) holder;
            vh.title.setText(newsEntity.getNewsTitle());
            vh.author.setText(newsEntity.getAuthorName());
            vh.comment.setText(newsEntity.getCommentCount() + "评论 .");
            vh.time.setText(newsEntity.getReleaseDate());
            vh.newsEntity = newsEntity;

            imageLoader.loadCircle(newsEntity.getHeaderUrl(), vh.header);
            if (newsEntity.getThumbEntities() != null && !newsEntity.getThumbEntities().isEmpty()) {
                imageLoader.load(newsEntity.getThumbEntities().get(0).getThumbUrl(), vh.thumb);
            }

            vh.mPosition = position;
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
        private NewsEntity newsEntity;

        public ViewHolderOne(@NonNull View view) {
            super(view);
            title = view.findViewById(R.id.title);
            author = view.findViewById(R.id.author);
            comment = view.findViewById(R.id.comment);
            time = view.findViewById(R.id.time);
            header = view.findViewById(R.id.header);
            thumb = view.findViewById(R.id.thumb);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(newsEntity);
                }
            });
        }
    }

    public class ViewHolderTwo extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView author;
        private TextView comment;
        private TextView time;
        private ImageView header;
        private ImageView pic1, pic2, pic3;
        private NewsEntity newsEntity;

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
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(newsEntity);
                }
            });
        }
    }

    public class ViewHolderThree extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView title;
        private TextView author;
        private TextView comment;
        private TextView time;
        private ImageView header;
        private ImageView thumb;
        private NewsEntity newsEntity;
        public PrepareView mPrepareView;
        public FrameLayout mPlayerContainer;
        public int mPosition;

        public ViewHolderThree(@NonNull View view) {
            super(view);
            title = view.findViewById(R.id.title);
            author = view.findViewById(R.id.author);
            comment = view.findViewById(R.id.comment);
            time = view.findViewById(R.id.time);
            header = view.findViewById(R.id.header);
            thumb = view.findViewById(R.id.thumb);
            mPlayerContainer = view.findViewById(R.id.player_container);
            mPrepareView = view.findViewById(R.id.prepare_view);

//            view.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mOnItemClickListener.onItemClick(newsEntity);
//                }
//            });
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.player_container) {
                if (mOnItemChildClickListener != null) {
                    mOnItemChildClickListener.onItemChildClick(mPosition);
                }
            } else {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(mPosition);
                }
            }

        }
    }

    public interface OnItemClickListener {
        void onItemClick(Serializable obj);
    }

    public interface OnItemChildClickListener {
        void onItemChildClick(int position);
    }

    // 新增工具类，建议写成内部类或单独文件
    private static class ImageLoader {
        private final Context context;
        private final static Map<String, Integer> resIdCache = new java.util.HashMap<>();

        public ImageLoader(Context context) {
            this.context = context;
        }

        public void load(String urlOrRes, ImageView imageView) {
            if (urlOrRes == null || imageView == null) return;
            if (urlOrRes.startsWith("http")) {
                Picasso.get().load(urlOrRes).into(imageView);
            } else {
                int resId = getResIdByName(urlOrRes);
                if (resId != 0) {
                    imageView.setImageResource(resId);
                } else {
                    // imageView.setImageResource(R.mipmap.default_thumb); // 可选默认图
                }
            }
        }

        public void loadCircle(String urlOrRes, ImageView imageView) {
            if (urlOrRes == null || imageView == null) return;
            if (urlOrRes.startsWith("http")) {
                Picasso.get().load(urlOrRes).transform(new CircleTransform()).into(imageView);
            } else {
                int resId = getResIdByName(urlOrRes);
                if (resId != 0) {
                    // 先解码为 Bitmap
                    Bitmap src = BitmapFactory.decodeResource(context.getResources(), resId);
                    if (src != null) {
                        // 用 CircleTransform 处理
                        CircleTransform circleTransform = new CircleTransform();
                        Bitmap circleBitmap = circleTransform.transform(src);
                        imageView.setImageBitmap(circleBitmap);
                    } else {
                        // imageView.setImageResource(R.mipmap.default_avatar);
                    }
                } else {
                    // imageView.setImageResource(R.mipmap.default_avatar);
                }
            }
        }

        private int getResIdByName(String name) {
            if (name == null) return 0;
            // 去除扩展名
            int dotIndex = name.lastIndexOf('.');
            String resName = (dotIndex > 0) ? name.substring(0, dotIndex) : name;
            // 查缓存
            Integer cached = resIdCache.get(resName);
            if (cached != null) return cached;
            // 反射查找
            int resId = context.getResources().getIdentifier(resName, "mipmap", context.getPackageName());
            resIdCache.put(resName, resId);
            return resId;
        }
    }
}
