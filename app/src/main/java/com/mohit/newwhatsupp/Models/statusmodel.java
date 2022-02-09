package com.mohit.newwhatsupp.Models;

import java.util.ArrayList;

public class statusmodel {
    String statususerName,statuserimage;
    private long statuslastupdatetime;
    private ArrayList<multistatus>  statuses;

    public String getStatususerName() {
        return statususerName;
    }

    public void setStatususerName(String statususerName) {
        this.statususerName = statususerName;
    }

    public String getStatuserimage() {
        return statuserimage;
    }

    public void setStatuserimage(String statuserimage) {
        this.statuserimage = statuserimage;
    }

    public long getStatuslastupdatetime() {
        return statuslastupdatetime;
    }

    public void setStatuslastupdatetime(long statuslastupdatetime) {
        this.statuslastupdatetime = statuslastupdatetime;
    }

    public ArrayList<multistatus> getStatuses() {
        return statuses;
    }

    public void setStatuses(ArrayList<multistatus> statuses) {
        this.statuses = statuses;
    }

    public statusmodel() {
    }

    public statusmodel(String statususerName, String statuserimage, long statuslastupdatetime, ArrayList<multistatus> statuses) {
        this.statususerName = statususerName;
        this.statuserimage = statuserimage;
        this.statuslastupdatetime = statuslastupdatetime;
        this.statuses = statuses;
    }
}
