package com.myapp.phucarussocialapp.Activity.Object;

public class WhoLikePost {
    String avt,name,uid;

    public WhoLikePost() {
    }

    public WhoLikePost(String avt, String name, String uid) {
        this.avt = avt;
        this.name = name;
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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
