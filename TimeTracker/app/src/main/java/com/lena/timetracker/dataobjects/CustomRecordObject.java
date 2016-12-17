package com.lena.timetracker.dataobjects;

import java.util.Date;
import java.util.HashMap;

public class CustomRecordObject {
    private long id;
    private String desc;
    private String category;
    private Date startTime;
    private  Date endTime;
    private long time;
    private HashMap<Long, String> photos;// <Id, Uri>

    public CustomRecordObject(long id, String desc, String category, Date startTime, Date endTime,
                              long time, HashMap<Long, String> photos) {
        this.id = id;
        this.desc = desc;
        this.category = category;
        this.startTime = startTime;
        this.endTime = endTime;
        this.time = time;
        if (photos == null) {
            this.photos = new HashMap<>();
        }
        else {
            this.photos = photos;
        }


    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public HashMap<Long, String> getPhotos() {
        return photos;
    }

    public void setPhotos(HashMap<Long, String> photos) {
        this.photos = photos;
    }
}
