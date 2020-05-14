package com.myapp.phucarussocialapp.Activity.Object;

public class RecentlyChat {
    String uid,avt,name,message,time,type;

    public RecentlyChat() {
    }

    public RecentlyChat(String uid, String avt, String name, String message, String time, String type) {
        this.uid = uid;
        this.avt = avt;
        this.name = name;
        this.message = message;
        this.time = time;
        this.type = type;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
