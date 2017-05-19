package com.example.a15017523.eventful;

/**
 * Created by 15017523 on 7/5/2017.
 */

public class Event {

    private String title;
    private String description;
    private String image;
    private String address;
    private String datetime;
    private String head_chief;
    private String organiser;
    private int pax;
    private String status;

    public Event() {

    }

    public Event(String title, String description, String image, String address, String datetime, String head_chief, String organiser, int pax, String status) {
        this.title = title;
        this.description = description;
        this.image = image;
        this.address = address;
        this.datetime = datetime;
        this.head_chief = head_chief;
        this.organiser = organiser;
        this.pax = pax;
        this.status = status;
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

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getHead_chief() {
        return head_chief;
    }

    public void setHead_chief(String head_chief) {
        this.head_chief = head_chief;
    }

    public String getOrganiser() {
        return organiser;
    }

    public void setOrganiser(String organiser) {
        this.organiser = organiser;
    }

    public int getPax() {
        return pax;
    }

    public void setPax(int pax) {
        this.pax = pax;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
