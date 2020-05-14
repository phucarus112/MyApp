package com.myapp.phucarussocialapp.Activity.Object;

public class Comment {
    String content,time,name,avt,uid;

    public Comment() {
    }

    public Comment(String content, String time, String name, String avt, String uid) {
        this.content = content;
        this.time = time;
        this.name = name;
        this.avt = avt;
        this.uid = uid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvt() {
        return avt;
    }

    public void setAvt(String avt) {
        this.avt = avt;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
