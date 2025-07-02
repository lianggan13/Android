package com.example.zhangliang.videonews.database;

import com.example.zhangliang.videonews.entity.NewsEntity;
import com.example.zhangliang.videonews.entity.VideoEntity;

import java.util.ArrayList;
import java.util.List;

public class MysqlSeed {

    public static List<VideoEntity> loadLocalVideos() {

        // 创建一个 List<VideoEntity>
        List<VideoEntity> videoList = new ArrayList<>();
        videoList.add(createVideo(1, "青龙战甲搭配机动兵，P城上空肆意1V4", "狙击手麦克",
                "http://sf3-xgcdn-tos.pstatp.com/img/tos-cn-i-0004/527d013205a74eb0a77202d7a9d5b511~tplv-crop-center:1041:582.jpg",
                "https://sf1-ttcdn-tos.pstatp.com/img/pgc-image/c783a73368fa4666b7842a635c63a8bf~360x360.image",
                "http://vfx.mtime.cn/Video/2019/02/04/mp4/190204084208765161.mp4",
                "2020-07-14 11:21:45", "2020-07-19 12:05:33", 1));

        videoList.add(createVideo(2, "【仁王2】视频攻略 2-3 虚幻魔城", "黑桐谷歌",
                "https://lf1-xgcdn-tos.pstatp.com/img/tos-cn-p-0000/9ff7fe6c89e44ca3a22aad5744e569e3~tplv-crop-center:1041:582.jpg",
                "https://sf6-ttcdn-tos.pstatp.com/img/mosaic-legacy/8110/752553978~360x360.image",
                "http://vfx.mtime.cn/Video/2019/03/21/mp4/190321153853126488.mp4",
                null, "2020-07-19 12:05:34", 1));

        videoList.add(createVideo(3, "最猛暴击吕布教学，这才是战神该有的样子", "小凡解说游戏",
                "https://sf1-xgcdn-tos.pstatp.com/img/tos-cn-i-0004/83cc11d5e26047c6b0ead149f41a8266~tplv-crop-center:1041:582.jpg",
                "https://p3.pstatp.com/large/a14a000405f16e51842f",
                "http://vfx.mtime.cn/Video/2019/03/19/mp4/190319222227698228.mp4",
                null, "2020-07-19 12:05:35", 1));

        videoList.add(createVideo(4, "拳皇14：小孩输掉一分，印尼选手得意忘形", "E游未尽小E",
                "https://sf1-xgcdn-tos.pstatp.com/img/tos-cn-i-0004/b9553b7a28d94f27a7115157797b52ff~tplv-crop-center:1041:582.jpg",
                "https://sf3-ttcdn-tos.pstatp.com/img/pgc-image/f6b840d23f9e465bb5ac9e570b28321d~360x360.image",
                "http://vfx.mtime.cn/Video/2019/03/19/mp4/190319212559089721.mp4",
                null, "2020-07-19 12:05:37", 1));

        videoList.add(createVideo(5, "阿远花210块买了条20斤的鲅鱼", "食味阿远",
                "https://lf6-xgcdn-tos.pstatp.com/img/tos-cn-i-0004/b821f00833b54e25ac941c7d267c2b75~tplv-crop-center:1041:582.jpg",
                "https://p9.pstatp.com/large/6edc0000758b2daaa6cc",
                "http://vfx.mtime.cn/Video/2019/03/18/mp4/190318231014076505.mp4",
                null, "2020-07-19 12:05:55", 3));

        videoList.add(createVideo(6, "10斤用新鲜牛腿肉分享", "美食作家王刚",
                "https://sf3-xgcdn-tos.pstatp.com/img/p1901/d9d5ae15079a8073f5cdb04b6a80777a~tplv-crop-center:1041:582.jpg",
                "https://sf3-ttcdn-tos.pstatp.com/img/mosaic-legacy/da860012437af2fd24c2~360x360.image",
                "http://vfx.mtime.cn/Video/2019/03/18/mp4/190318214226685784.mp4",
                null, "2020-07-19 12:05:56", 3));

        videoList.add(createVideo(7, "面条这样吃才叫爽，放两斤花甲一拌", "山药视频",
                "https://lf3-xgcdn-tos.pstatp.com/img/tos-cn-i-0004/51109f43de0346f68b7fd93103658aa4~tplv-crop-center:1041:582.jpg",
                "https://p1.pstatp.com/large/719f0015d12364d07c5b",
                "http://vfx.mtime.cn/Video/2019/03/19/mp4/190319104618910544.mp4",
                null, "2020-07-19 12:05:58", 3));

        videoList.add(createVideo(8, "2320买2只蓝色龙虾，一只清蒸，一只刺身", "半吨先生",
                "https://sf3-xgcdn-tos.pstatp.com/img/tos-cn-i-0004/5ecedb083288435cbbf51ef04723d991~tplv-crop-center:1041:582.jpg",
                "https://sf1-ttcdn-tos.pstatp.com/img/mosaic-legacy/dae9000ee0a875804aae~360x360.image",
                "http://vfx.mtime.cn/Video/2019/03/19/mp4/190319125415785691.mp4",
                null, "2020-07-19 12:05:59", 3));

        videoList.add(createVideo(9, "122块钱买了一大堆海螺，想试试", "韩小浪",
                "https://lf6-xgcdn-tos.pstatp.com/img/tos-cn-i-0004/28b99fcd52bf4e45a7f4a28ab2f21685~tplv-crop-center:1041:582.jpg",
                "https://sf6-ttcdn-tos.pstatp.com/img/mosaic-legacy/b77400114e944ff697e4~360x360.image",
                "http://vfx.mtime.cn/Video/2019/03/17/mp4/190317150237409904.mp4",
                null, "2020-07-19 12:06:00", 3));

        videoList.add(createVideo(10, "10块钱的大鲍鱼随便搞50个来烧烤", "阿壮锅",
                "https://sf6-xgcdn-tos.pstatp.com/img/tos-cn-i-0004/edcc153551794b67a2de2683ff8b0ee2~tplv-crop-center:1041:582.jpg",
                "https://sf3-ttcdn-tos.pstatp.com/img/pgc-image/7cbcfbb82fa142058fd45549d3b63a5b~360x360.image",
                "http://vfx.mtime.cn/Video/2019/03/14/mp4/190314223540373995.mp4",
                null, "2020-07-19 12:06:01", 3));

        videoList.add(createVideo(11, "萨德：有钱学森弹道就可以“为所欲为”么", "军武次位面",
                "https://p3-xg.byteimg.com/img/tos-cn-i-0004/bd1c46a6e99a491cab93ae359df1a287~tplv-crop-center:1041:582.jpg",
                "https://p3.pstatp.com/large/888f000186913353fe3f",
                "http://vfx.mtime.cn/Video/2019/03/14/mp4/190314223540373995.mp4",
                "2020-07-19 16:05:38", "2020-07-19 16:05:38", 8));

        videoList.add(createVideo(12, "美舰趁火打劫再闯南海，王洪光将军称“以其人之道还治其人之身”", "火星方阵",
                "https://p1-xg.byteimg.com/img/tos-cn-i-0004/9a50e691dd2646d6983ccebb93607033~tplv-crop-center:1041:582.jpg",
                "https://sf6-ttcdn-tos.pstatp.com/img/pgc-image/1f5b712339ab475aa6ba0280d36189ba~360x360.image",
                "http://vfx.mtime.cn/Video/2019/03/14/mp4/190314223540373995.mp4",
                "2020-07-19 16:05:38", "2020-07-19 16:05:38", 8));

        videoList.add(createVideo(13, "F-22偷袭能力超强，被中国王牌雷达牢牢锁定，不敢造次", "军事观察员东旭",
                "https://p9-xg.byteimg.com/img/tos-cn-i-0004/e6750544a3ee4f8182c984949f966bc2~tplv-crop-center:1041:582.jpg",
                "https://sf6-ttcdn-tos.pstatp.com/img/mosaic-legacy/4110014cf3649fd8d6b~360x360.image",
                "http://vfx.mtime.cn/Video/2019/03/14/mp4/190314223540373995.mp4",
                "2020-07-19 16:05:38", "2020-07-19 16:05:38", 8));

        videoList.add(createVideo(14, "绝美“白天鹅”，俄罗斯镇国重器，近距离感受下图-160战略轰炸机", "YiTube",
                "https://p9-xg.byteimg.com/img/tos-cn-i-0004/1a9fd82c375d4124bd860d253ca1d502~tplv-crop-center:1041:582.jpg",
                "https://sf3-ttcdn-tos.pstatp.com/img/mosaic-legacy/ff3700002d8bc2b3ab3e~360x360.image",
                "http://vfx.mtime.cn/Video/2019/03/14/mp4/190314223540373995.mp4",
                "2020-07-19 16:05:38", "2020-07-19 16:05:38", 8));

        videoList.add(createVideo(15, "中国新歌声：男子开口唱得太奇怪！", "灿星音乐现场",
                "https://p9-xg.byteimg.com/img/tos-cn-i-0004/19c44751e9124b069d23cddbc46e29fb~tplv-crop-center:1041:582.jpg",
                "https://sf1-ttcdn-tos.pstatp.com/img/user-avatar/d58021eb3b4d5a6066eaf84fb793b360~360x360.image",
                "http://vfx.mtime.cn/Video/2019/03/14/mp4/190314223540373995.mp4",
                "2020-07-19 16:05:38", "2020-07-19 16:05:38", 2));

        videoList.add(createVideo(16, "拇指琴演奏《琅琊榜》插曲《红颜旧》琴声动人", "比三呆",
                "https://p1-xg.byteimg.com/img/tos-cn-p-0000/527b08d0f31d4705a4d8f4a72120948c~tplv-crop-center:1041:582.jpg",
                "https://p3.pstatp.com/large/dac80011227f8d67d02b",
                "http://vfx.mtime.cn/Video/2019/03/14/mp4/190314223540373995.mp4",
                "2020-07-19 16:05:38", "2020-07-19 16:05:38", 2));

        videoList.add(createVideo(17, "陈志朋台上唱《大田后生仔》", "下饭音乐",
                "https://p3-xg.byteimg.com/img/tos-cn-i-0004/1820c36d7a3846acaca9c24f18b01944~tplv-crop-center:1041:582.jpg",
                "https://p1.pstatp.com/large/8b5f000540ca210dc4a7",
                "http://vfx.mtime.cn/Video/2019/03/14/mp4/190314223540373995.mp4",
                "2020-07-19 16:05:38", "2020-07-19 16:05:38", 2));

        videoList.add(createVideo(18, "龚喜水库下网偶遇大鱼群，收网过程惊心动魄", "游钓寻鱼之路",
                "https://p3-xg.byteimg.com/img/tos-cn-p-0026/a225869a56d1715823d6f74d6a765b01~tplv-crop-center:1041:582.jpg",
                "https://p3.pstatp.com/large/da57000d5b84204f3e8f",
                "http://vfx.mtime.cn/Video/2019/03/14/mp4/190314223540373995.mp4",
                "2020-07-19 22:39:15", "2020-07-19 22:39:15", 4));

        videoList.add(createVideo(19, "小登父女第一次吃香蕉花没经验以为是大苞米", "麦小登",
                "https://p3-xg.byteimg.com/img/tos-cn-i-0004/36f0b389b3d44d5dbd60590a0adf8c2a~tplv-crop-center:1041:582.jpg",
                "https://sf1-ttcdn-tos.pstatp.com/img/mosaic-legacy/dae9000a4fdeff22675f~360x360.image",
                "http://vfx.mtime.cn/Video/2019/03/14/mp4/190314223540373995.mp4",
                "2020-07-19 16:05:38", "2020-07-19 16:05:38", 5));

        videoList.add(createVideo(20, "管它什么狂风暴雨昼夜不停,躲在房车里炖牛肉", "旗开得胜号",
                "https://p3-xg.byteimg.com/img/tos-cn-i-0004/55386236bbf74f5794251a24fba85ef1~tplv-crop-center:1041:582.jpg",
                "https://p1.pstatp.com/large/986a0004c8fa4adec094",
                "http://vfx.mtime.cn/Video/2019/03/14/mp4/190314223540373995.mp4",
                "2020-07-19 16:05:38", "2020-07-19 16:05:38", 5));

        videoList.add(createVideo(21, "2020年的旅行计划：预算花25万去南极旅行", "麦小兜开车去非洲",
                "https://p9-xg.byteimg.com/img/tos-cn-i-0004/7c09b805c10e44469edfb76eaf7b666b~tplv-crop-center:1041:582.jpg",
                "https://sf6-ttcdn-tos.pstatp.com/img/user-avatar/a0fb5b628254086d23af194c4eec2426~360x360.image",
                "http://vfx.mtime.cn/Video/2019/03/14/mp4/190314223540373995.mp4",
                "2020-07-19 16:05:38", "2020-07-19 16:05:38", 5));

        videoList.add(createVideo(22, "云南咖啡进军美国第一步：纽约最好烘焙厂愿意合作吗？", "我是郭杰瑞",
                "https://p1-xg.byteimg.com/img/tos-cn-i-0004/4a482126d41c4da49c3baaa5ea65b0f6~tplv-crop-center:1041:582.jpg",
                "https://p1.pstatp.com/large/4e6900026fa8d9eaee0a",
                "http://vfx.mtime.cn/Video/2019/03/14/mp4/190314223540373995.mp4",
                "2020-07-19 16:05:38", "2020-07-19 16:05:38", 5));

        videoList.add(createVideo(23, "二货当憨头面炫耀家庭地位，谁料事后认怂惨遭打脸", "爆笑三江锅",
                "https://p1-xg.byteimg.com/img/tos-cn-i-0004/975b48746c584e79b77df1a43531d4bf~tplv-crop-center:1041:582.jpg",
                "https://p3.pstatp.com/large/ef40008c39119bef556",
                "http://vfx.mtime.cn/Video/2019/03/14/mp4/190314223540373995.mp4",
                "2020-07-19 16:05:38", "2020-07-19 16:05:38", 6));

        videoList.add(createVideo(24, "大年初一有家人在身边，最好的朋友在对门！", "陈翔六点半",
                "https://p1-xg.byteimg.com/img/tos-cn-p-0026/71b6b37e67a05c3103de521bcc1bd8cc~tplv-crop-center:1041:582.jpg",
                "https://sf1-ttcdn-tos.pstatp.com/img/mosaic-legacy/dac80012b10b5678b21e~360x360.image",
                "http://vfx.mtime.cn/Video/2019/03/14/mp4/190314223540373995.mp4",
                "2020-07-19 16:05:38", "2020-07-19 16:05:38", 6));

        videoList.add(createVideo(25, "猫一见陌生人就跑，靠近就发抖", "肉蛋儿有个喵",
                "https://p3-xg.byteimg.com/img/tos-cn-i-0004/eadd7aebf3174fa3b793acd310d2549a~tplv-crop-center:1041:582.jpg",
                "https://p3.pstatp.com/large/289a0019c8fc986e193f",
                "http://vfx.mtime.cn/Video/2019/03/14/mp4/190314223540373995.mp4",
                "2020-07-19 16:05:38", "2020-07-19 16:05:38", 7));

        videoList.add(createVideo(26, "网红猫咪精修图vs刚睡醒，还真是两副面孔", "papi家的大小咪",
                "https://p1-xg.byteimg.com/img/tos-cn-i-0004/a2165f779651487c94b27233d162c3dc~tplv-crop-center:1041:582.jpg",
                "https://p1.pstatp.com/large/47220003b76b9bfc799c",
                "http://vfx.mtime.cn/Video/2019/03/14/mp4/190314223540373995.mp4",
                "2020-07-19 16:05:38", "2020-07-19 16:05:38", 7));

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

    public static VideoEntity createVideo(int vid, String vtitle, String author, String coverurl,
                                          String headurl, String playurl, String createTime,
                                          String updateTime, int categoryId) {
        VideoEntity video = new VideoEntity();
        video.setVid(vid);
        video.setVtitle(vtitle);
        video.setAuthor(author);
        video.setCoverurl(coverurl);
        video.setHeadurl(headurl);
        video.setPlayurl(playurl);
        video.setCreateTime(createTime);
        video.setUpdateTime(updateTime);
        video.setCategoryId(categoryId);
        // 这里可以设置 categoryName 和 videoSocialEntity，如果有需要的话
        return video;
    }


    public static List<NewsEntity> paginateNews(List<NewsEntity> newsList, int pageIndex, int pageSize) {
        List<NewsEntity> pagedList = new ArrayList<>();

        // 计算起始索引
        int start = (pageIndex - 1) * pageSize;

        for (int i = start; i < Math.min(start + pageSize, newsList.size()); i++) {
            NewsEntity video = newsList.get(i);
            pagedList.add(video);
        }

        return pagedList;
    }

    public static List<NewsEntity> loadLocalNews() {
        List<NewsEntity> newsList = new ArrayList<>();

        // Sample data based on SQL
        newsList.add(createNewsEntity(1, "《忍者蛙》发售日公布 已上架Steam、支持简中", "3DMGAME",
                "https://p9.pstatp.com/thumb/6eed00026c4eac713a44", 3, "2020-07-31 22:23:00", 1));

        newsList.add(createNewsEntity(2, "外媒爆料：育碧“阿瓦隆”项目胎死腹中，只因为他不喜欢奇幻游戏",
                "爱游戏的萌博士", "https://p3.pstatp.com/thumb/43310001daafa9723ddf", 1,
                "2020-07-31 21:01:17", 2));

        newsList.add(createNewsEntity(3, "索尼公布Ready for PlayStation 5电视阵容", "游戏时光VGtime",
                "https://p1.pstatp.com/thumb/dc0c0004c450216ab2f3", 6, "2020-07-30 13:11:32", 3));

        newsList.add(createNewsEntity(4, "一部不受关注的互动电影佳作——解构《暴雨》", "瑾瑜游乐说",
                "https://sf3-ttcdn-tos.pstatp.com/img/pgc-image/e200b9de317b4e73af24299ea063bec2~120x256.image",
                12, "2020-07-30 13:11:32", 3));

        newsList.add(createNewsEntity(5, "《光环：无限》官方Q&A 无充值战利品，画质优化中", "聚玩社官方",
                "https://sf6-ttcdn-tos.pstatp.com/img/pgc-image/2bfe5f2e082e415cb92a03cfcfcfead8~120x256.image",
                4, "2020-08-01 08:22:47", 2));

        newsList.add(createNewsEntity(6, "2020小编个人力推的耐玩的养老游戏", "游戏我看看",
                "https://sf3-ttcdn-tos.pstatp.com/img/mosaic-legacy/dc0d0001fca5e747f267~120x256.image",
                7, "2020-07-30 12:48:37", 1));

        newsList.add(createNewsEntity(7, "NBA复赛赛况：开拓者加时擒灰熊，太阳胜奇才，魔术“主场”破网",
                "头条专题", "https://sf1-ttcdn-tos.pstatp.com/img/mosaic-legacy/ffbc0000ad1e717b76a6~120x256.image",
                23, "2020-08-01 06:49:44", 1));

        newsList.add(createNewsEntity(8, "NBA最有含金量总冠军？奥拉朱旺95年4次以下克上！无冠军超过2次",
                "网罗篮球", "https://sf6-ttcdn-tos.pstatp.com/img/pgc-image/9f11654ff6184afd8941bcf7ccd3c5dd~120x256.image",
                45, "2020-05-23 14:08:09", 2));

        return newsList;
    }

    private static NewsEntity createNewsEntity(int newsId, String newsTitle, String authorName,
                                               String headerUrl, int commentCount, String releaseDate, int type) {
        NewsEntity newsEntity = new NewsEntity();
        newsEntity.setNewsId(newsId);
        newsEntity.setNewsTitle(newsTitle);
        newsEntity.setAuthorName(authorName);
        newsEntity.setHeaderUrl(headerUrl);
        newsEntity.setCommentCount(commentCount);
        newsEntity.setReleaseDate(releaseDate);
        newsEntity.setType(type);

        List<NewsEntity.ThumbEntitiesBean> thumbList = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            NewsEntity.ThumbEntitiesBean thumb = new NewsEntity.ThumbEntitiesBean();
            thumb.setThumbId(i); // 设置唯一 ID
            thumb.setThumbUrl("http://example.com/thumb" + i + ".jpg"); // 设置缩略图 URL
            thumbList.add(thumb);
        }

        newsEntity.setThumbEntities(thumbList);

        return newsEntity;
    }


}
