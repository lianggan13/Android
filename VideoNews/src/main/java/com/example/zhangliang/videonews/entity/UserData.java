package com.example.zhangliang.videonews.entity;

import java.util.List;

public class UserData {
    // @SerializedName("No")
    private int no;

    // @SerializedName("CardNo")
    private int cardNo;

    // @SerializedName("Name")
    private String name;

    // @SerializedName("PhotoUrl")
    private String photoUrl;

    // @SerializedName("Password")
    private String password;

    // @SerializedName("OldPassword")
    private String oldPassword;

    // @SerializedName("NewPassword")
    private String newPassword;

    // @SerializedName("RoleId")
    private int roleId;

    // @SerializedName("GroupId")
    private int groupId;

    // @SerializedName("Authorities")
    private List<String> authorities;

    // @SerializedName("Id")
    private int id;

    // Getters and Setters
    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public int getCardNo() {
        return cardNo;
    }

    public void setCardNo(int cardNo) {
        this.cardNo = cardNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public List<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<String> authorities) {
        this.authorities = authorities;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
