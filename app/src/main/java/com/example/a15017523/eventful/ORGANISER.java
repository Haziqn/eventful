package com.example.a15017523.eventful;

/**
 * Created by 15017470 on 22/7/2017.
 */

public class ORGANISER {

    String email;
    String image;
    String password;
    String status;
    String user_name;
    String description;
    String web;
    String address;
    private Double lat;
    private Double lng;

    public ORGANISER() {

    }

    public ORGANISER(String email, String image, String password, String status, String user_name, String description, String web, String address, Double lat, Double lng) {
        this.email = email;
        this.image = image;
        this.password = password;
        this.status = status;
        this.user_name = user_name;
        this.description = description;
        this.web = web;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }
}