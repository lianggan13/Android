package com.example.zhangliang.videoprojects.util;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.Map;


// 新增工具类，建议写成内部类或单独文件
public class ImageLoader {
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
        if (urlOrRes == null || urlOrRes.isEmpty() || imageView == null) return;
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
