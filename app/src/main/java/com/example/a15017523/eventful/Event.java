package com.example.a15017523.eventful;

/**
 * Created by 15017523 on 7/5/2017.
 */

public class Event {

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


    public Event() {

    }

    public Event(String title, String description, String image, String address, String head_chief, String pax, String status, String organiser, String date, String time) {
        this.title = title;
        this.description = description;
        this.image = image;
        this.address = address;
        this.head_chief = head_chief;
        this.pax = pax;
        this.status = status;
        this.organiser = organiser;
        this.date = date;
        this.time = time;
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
}
