package com.example.a15017523.eventful;

import java.io.StringBufferInputStream;
import java.util.ArrayList;

/**
 * Created by 15017523 on 21/5/2017.
 */

public class PARTICIPANT {
    String email;
    String image;
    String password;
    String status;
    String user_name;
    String age;
    String gender;
    String race;
    String occupation;
    ArrayList<String> interests;

    public PARTICIPANT() {

    }

    public PARTICIPANT(String email, String image, String password, String status, String user_name, String age, String gender, String race, String occupation, ArrayList<String> interests) {
        this.email = email;
        this.image = image;
        this.password = password;
        this.status = status;
        this.user_name = user_name;
        this.age = age;
        this.gender = gender;
        this.race = race;
        this.occupation = occupation;
        this.interests = interests;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public ArrayList<String> getInterests() {
        return interests;
    }

    public void setInterests(ArrayList<String> interests) {
        this.interests = interests;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
}
