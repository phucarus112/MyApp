package com.myapp.phucarussocialapp.Activity.Object;

public class Song {
    String name,singer,location;

    public Song() {
    }

    public Song(String name, String singer, String location) {
        this.name = name;
        this.singer = singer;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
