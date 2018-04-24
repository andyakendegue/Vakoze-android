package com.vakoze.chatFirebase;

import java.util.Date;

/**
 * Created by capp on 30/01/2018.
 */

public class ChatMessage {
    private String messageText;
    private String messageTo;
    private String messageUser;
    private long messageTime;

    public ChatMessage(String messageText, String messageUser, String messageTo) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        this.messageTo = messageTo;

        // Initialize to current time
        messageTime = new Date().getTime();
    }

    public ChatMessage(){

    }


    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public String getMessageTo() {
        return messageTo;
    }

    public void setMessageTo(String messageTo) {
        this.messageTo = messageTo;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }
}
