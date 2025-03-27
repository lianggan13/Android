package com.example.zhangliang.videonews.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class UserListResponse implements Serializable {
    @SerializedName("Data")
    private List<UserData> UserDatas; // 使用 List 替代 MutableList
    @SerializedName("Code")
    private int code;
    private int page;
    private int pages;
    private int perpage;
    private int total;

    // 无参构造方法
    public UserListResponse() {
    }

    // Getter 和 Setter 方法
    public List<UserData> getUserDatas() {
        return UserDatas;
    }

    public void setUserDatas(List<UserData> UserDatas) {
        this.UserDatas = UserDatas;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getPerpage() {
        return perpage;
    }

    public void setPerpage(int perpage) {
        this.perpage = perpage;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    // 重写 toString 方法
    @Override
    public String toString() {
        return "PhotoResponse{" +
                "UserDatas=" + UserDatas +
                ", page=" + page +
                ", pages=" + pages +
                ", perpage=" + perpage +
                ", total=" + total +
                '}';
    }
}