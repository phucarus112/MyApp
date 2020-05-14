package com.myapp.phucarussocialapp.Activity.Object;

import java.io.Serializable;

public class Post implements Serializable {

    String uid;
    String avt;
    String name;
    String time;
    String caption;
    String img;
    String like;
    String comment;
    String share;

    public Post() {
    }

    public Post(String uid, String avt, String name, String time, String caption, String img, String like, String comment, String share) {
        this.uid = uid;
        this.avt = avt;
        this.name = name;
        this.time = time;
        this.caption = caption;
        this.img = img;
        this.like = like;
        this.comment = comment;
        this.share = share;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAvt() {
        return avt;
    }

    public void setAvt(String avt) {
        this.avt = avt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getShare() {
        return share;
    }

    public void setShare(String share) {
        this.share = share;
    }
}
