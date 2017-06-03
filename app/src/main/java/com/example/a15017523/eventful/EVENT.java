package com.example.a15017523.eventful;

/**
 * Created by 15017523 on 7/5/2017.
 */

public class EVENT {

    private String title;
    private String description;
    private String image;
    private String address;
    private String head_chief;
    private String pax;
    private String status;
    private String organiser;
    private String date;
    private String time;
    private String timeStamp;
    private String organiser_name;

    public EVENT() {

    }

    public String getOrganiser_name() {
        return organiser_name;
    }

    public void setOrganiser_name(String organiser_name) {
        this.organiser_name = organiser_name;
    }

    public EVENT(String title, String description, String image, String address, String date, String time, String head_chief, String organiser, String pax, String status, String timeStamp, String organiser_name) {
        this.title = title;
        this.description = description;
        this.image = image;
        this.address = address;
        this.date = date;
        this.time = time;
        this.head_chief = head_chief;
        this.pax = pax;
        this.status = status;
        this.organiser = organiser;
        this.timeStamp = timeStamp;
        this.organiser_name = organiser_name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getHead_chief() {
        return head_chief;
    }

    public void setHead_chief(String head_chief) {
        this.head_chief = head_chief;
    }

    public String getPax() {
        return pax;
    }

    public void setPax(String pax) {
        this.pax = pax;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrganiser() {
        return organiser;
    }

    public void setOrganiser(String organiser) {
        this.organiser = organiser;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }


}
