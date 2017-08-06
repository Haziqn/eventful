package com.example.a15017523.eventful;

/**
 * Created by 15017523 on 6/8/2017.
 */

public class JOIN {
    String id;
    String ref;
    String name;
    String datetime;
    String status;

    public JOIN() {}

    public JOIN(String id, String ref, String status, String name, String datetime) {
        this.id = id;
        this.status = status;
        this.name = name;
        this.datetime = datetime;
        this.ref = ref;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }
}
