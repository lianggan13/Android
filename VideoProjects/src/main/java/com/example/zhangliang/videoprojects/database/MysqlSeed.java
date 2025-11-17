package com.example.zhangliang.videoprojects.database;

import com.example.zhangliang.videoprojects.entity.PhotoEntity;
import com.example.zhangliang.videoprojects.entity.VideoEntity;
import com.example.zhangliang.videoprojects.entity.VideoEntity.VideoSocialEntity;

import java.util.ArrayList;
import java.util.List;

public class MysqlSeed {

    public static List<VideoEntity> loadLocalVideos() {
        List<VideoEntity> videoList = new ArrayList<>();

        videoList.add(new VideoEntity(1, "安全联锁桌面端", "WPF",
                "asis_baise01.png",
                "author.png",
                "安全联锁.mp4",
                "2023-10-27", "2023-10-27", 0,
                new VideoSocialEntity()));

        videoList.add(new VideoEntity(1, "一体机终端", "WPF Prism",
                "asis_plct02.png",
                "author.png",
                "一体机终端.mp4",
                "2020-07-14 11:21:45", "2020-07-19 12:05:33", 0,
                new VideoSocialEntity()));

        videoList.add(new VideoEntity(1, "车顶异物检测", "WPF Prism",
                "rrd01.png",
                "author.png",
                "车顶异物检测.mp4",
                "2020-07-14 11:21:45", "2020-07-19 12:05:33", 0,
                new VideoSocialEntity()));

        videoList.add(new VideoEntity(1, "巡检机器人 HMI", "WPF Prism",
                "robot_app.png",
                "author.png",
                "机器人 HMI.mp4",
                "2024-04-01 08:21:45", "2025-05-20 12:05:33", 0,
                new VideoSocialEntity()));

        videoList.add(new VideoEntity(1, "巡检机器人调试01", "WPF Prism",
                "robot_debug01.png",
                "author.png",
                "机器人现场调试01.mp4",
                "2025-05-25 11:21:45", "2025-07-19 18:05:33", 0,
                new VideoSocialEntity()));

        videoList.add(new VideoEntity(1, "巡检机器人调试02", "WPF Prism",
                "robot_debug02.png",
                "author.png",
                "机器人现场调试02.mp4",
                "2025-05-25 11:21:45", "2025-07-19 18:05:33", 0,
                new VideoSocialEntity()));

        return videoList;
    }


    public static List<VideoEntity> paginateVideos(List<VideoEntity> videoList, int pageIndex, int pageSize, int categoryId) {
        List<VideoEntity> pagedList = new ArrayList<>();

        // 计算起始索引
        int start = (pageIndex - 1) * pageSize;

        for (int i = start; i < Math.min(start + pageSize, videoList.size()); i++) {
            VideoEntity video = videoList.get(i);
            if (video.getCategoryId() == categoryId) {
                pagedList.add(video);
            }
        }

        return pagedList;
    }

    public static List<PhotoEntity> paginateNews(List<PhotoEntity> newsList, int pageIndex, int pageSize) {
        List<PhotoEntity> pagedList = new ArrayList<>();

        // 计算起始索引
        int start = (pageIndex - 1) * pageSize;

        for (int i = start; i < Math.min(start + pageSize, newsList.size()); i++) {
            PhotoEntity video = newsList.get(i);
            pagedList.add(video);
        }

        return pagedList;
    }

    public static List<PhotoEntity> loadLocalPhoto() {
        List<PhotoEntity> photoList = new ArrayList<>();
        // type:1 小图x1 + 详情
        // type:2 小图x3
        // type:3 大图x1
        photoList.add(createPhotoEntity(1, "安全联锁桌面端", "张亮",
                "author.png", "WPF", "2023-10-27", 2,
                List.of("asis_baise01.png", "asis_baise02.png", "asis_powerless.png")));

        photoList.add(createPhotoEntity(1, "一体机终端", "张亮",
                "author.png", "WPF", "2024-11-20", 1,
                List.of("asis_plct02.png")));

        photoList.add(createPhotoEntity(1, "车顶异物检测", "张亮",
                "author.png", "WPF", "2024-10-13", 2,
                List.of("rrd01.png", "rrd02.png", "rrd03.png")));


        photoList.add(createPhotoEntity(1, "巡检机器人 HMI 客户端", "张亮",
                "author.png", "WPF", "2020-07-31 22:23:00", 2,
                List.of("robot_charge.png", "robot_manual.png", "robot_work.png")));

        photoList.add(createPhotoEntity(1, "现场调试", "张亮",
                "author.png", "WPF", "2020-07-31 22:23:00", 2,
                List.of("my_asis_debug.jpg", "my_robot_debug01.jpg", "my_robot_debug02.jpg")));

        return photoList;
    }


    private static PhotoEntity createPhotoEntity(int newsId, String newsTitle, String authorName,
                                                 String headerUrl, String comment, String releaseDate, int type, List<String> thumbUrls) {
        PhotoEntity photoEntity = new PhotoEntity();
        photoEntity.setNewsId(newsId);
        photoEntity.setNewsTitle(newsTitle);
        photoEntity.setAuthorName(authorName);
        photoEntity.setHeaderUrl(headerUrl);
        photoEntity.setComment(comment);
        photoEntity.setReleaseDate(releaseDate);
        photoEntity.setType(type);

        List<PhotoEntity.ThumbEntitiesBean> thumbList = new ArrayList<>();

        for (int i = 0; i < thumbUrls.size(); i++) {
            PhotoEntity.ThumbEntitiesBean thumb = new PhotoEntity.ThumbEntitiesBean();
            thumb.setThumbId(i + 1); // 设置唯一 ID
            thumb.setThumbUrl(thumbUrls.get(i)); // 设置缩略图 URL
            thumbList.add(thumb);
        }
        photoEntity.setThumbEntities(thumbList);

        return photoEntity;
    }
}
