package com.myapp.phucarussocialapp.Activity.Object;

import java.io.Serializable;

public class Auth implements Serializable {

    String phone;
    String email;
    String name;
    String dateOfBirth;
    String male_female;
    String avatar;
    String uid;
    String onlineStatus;

    public Auth() {
    }

    public Auth(String phone, String email, String name, String dateOfBirth, String male_female, String avatar, String uid, String onlineStatus) {
        this.phone = phone;
        this.email = email;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.male_female = male_female;
        this.avatar = avatar;
        this.uid = uid;
        this.onlineStatus = onlineStatus;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getMale_female() {
        return male_female;
    }

    public void setMale_female(String male_female) {
        this.male_female = male_female;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }
}
