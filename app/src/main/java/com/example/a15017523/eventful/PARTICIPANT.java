package com.example.a15017523.eventful;

/**
 * Created by 15017523 on 21/5/2017.
 */

public class PARTICIPANT {
    String email;
    String image;
    String password;
    String status;
    String user_name;

    public PARTICIPANT() {

    }

    public PARTICIPANT(String email, String image, String password, String status, String user_name) {
        this.email = email;
        this.image = image;
        this.password = password;
        this.status = status;
        this.user_name = user_name;
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
}
