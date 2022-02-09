package com.mohit.newwhatsupp.Models;

public class multistatus {
    private String imageurl;
    private long timestamp;

    public multistatus() {
    }

    public multistatus(String imageurl, long timestamp) {
        this.imageurl = imageurl;
        this.timestamp = timestamp;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
