package com.mohit.newwhatsupp.Models;

public class userInformation {
    String uid,name,email,photo,password,phoneNo;

    public userInformation() {
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public userInformation(String uid, String name, String email, String photo, String password, String phoneNo) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.photo = photo;
        this.password = password;
        this.phoneNo = phoneNo;
    }
}
