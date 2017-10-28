package com.abdullahhussein.gdgfirebaseexample.model;

import java.io.Serializable;

/**
 * Created by Abdullah Hussein on 25/10/2017.
 * abdullah.hussein109@gmail.com
 */

public class Chat implements Serializable {

    private String chatID;

    private String title;
    private String lastMessage;
    private long timestamp;

    public Chat() {
    }

    public String getChatID() {
        return chatID;
    }

    public Chat setChatID(String chatID) {
        this.chatID = chatID;
        return this;
    }

    public Chat(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
