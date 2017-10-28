package com.abdullahhussein.gdgfirebaseexample.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Abdullah Hussein on 25/10/2017.
 * abdullah.hussein109@gmail.com
 */

public class User implements Serializable {
    private String name;
    private HashMap<String, String> contacts = new HashMap<>();

    public User() {
    }

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<String, String> getContacts() {
        return contacts;
    }

    public void setContacts(HashMap<String, String> contacts) {
        this.contacts = contacts;
    }

    @Override
    public String toString() {
        return name + " : " + contacts.toString();
    }
}
