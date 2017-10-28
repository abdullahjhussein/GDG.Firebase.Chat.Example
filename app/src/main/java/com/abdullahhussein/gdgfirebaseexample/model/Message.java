package com.abdullahhussein.gdgfirebaseexample.model;

import java.io.Serializable;

/**
 * Created by Abdullah Hussein on 25/10/2017.
 * abdullah.hussein109@gmail.com
 */

public class Message implements Serializable {

    private String name;
    private String message;
    private long timestamp;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "name : " + name + " message : " + message + " timestamp : " + timestamp;
    }
}
