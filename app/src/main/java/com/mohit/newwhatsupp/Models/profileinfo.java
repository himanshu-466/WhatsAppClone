package com.mohit.newwhatsupp.Models;

public class profileinfo {
    String uid,name,photo,phoneNo;

    public profileinfo(String uid, String name, String photo, String phoneNo) {
        this.uid = uid;
        this.name = name;
        this.photo = photo;
        this.phoneNo = phoneNo;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public profileinfo() {
    }
}
