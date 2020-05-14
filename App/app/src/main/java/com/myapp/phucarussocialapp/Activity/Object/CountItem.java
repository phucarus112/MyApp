package com.myapp.phucarussocialapp.Activity.Object;

public class CountItem {
    String avt,name;
    int count;

    public CountItem() {
    }

    public CountItem(String avt, String name, int count) {
        this.avt = avt;
        this.name = name;
        this.count = count;
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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
