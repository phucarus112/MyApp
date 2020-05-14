package com.myapp.phucarussocialapp.Activity.Object;

public class Message {
    String time_post;
    String avt;
    String time;
    String content;
    String isSeen;
    String uid;

    public Message() {
    }

    public Message(String time_post, String avt, String time, String content, String isSeen, String uid) {
        this.time_post = time_post;
        this.avt = avt;
        this.time = time;
        this.content = content;
        this.isSeen = isSeen;
        this.uid = uid;
    }

    public String getTime_post() {
        return time_post;
    }

    public void setTime_post(String time_post) {
        this.time_post = time_post;
    }

    public String getAvt() {
        return avt;
    }

    public void setAvt(String avt) {
        this.avt = avt;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIsSeen() {
        return isSeen;
    }

    public void setIsSeen(String isSeen) {
        this.isSeen = isSeen;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}


