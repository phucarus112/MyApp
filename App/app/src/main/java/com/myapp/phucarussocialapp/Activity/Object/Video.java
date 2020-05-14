package com.myapp.phucarussocialapp.Activity.Object;

public class Video {
    String id;
    String title;
    String linkThumbnail;

    public Video(String id, String title, String linkThumbnail, String time, String playist) {
        this.id = id;
        this.title = title;
        this.linkThumbnail = linkThumbnail;
        this.time = time;
        this.playist = playist;
    }

    String time;
    String playist;

    public Video() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLinkThumbnail() {
        return linkThumbnail;
    }

    public void setLinkThumbnail(String linkThumbnail) {
        this.linkThumbnail = linkThumbnail;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPlayist() {
        return playist;
    }

    public void setPlayist(String playist) {
        this.playist = playist;
    }
}
