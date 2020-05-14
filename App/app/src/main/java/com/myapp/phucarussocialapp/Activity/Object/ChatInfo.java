package com.myapp.phucarussocialapp.Activity.Object;

public class ChatInfo {

        String content,time;
        String sender,receiver;
        String isSeen,type;

    public ChatInfo() {
    }

    public ChatInfo(String content, String time, String sender, String receiver, String isSeen, String type) {
        this.content = content;
        this.time = time;
        this.sender = sender;
        this.receiver = receiver;
        this.isSeen = isSeen;
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getIsSeen() {
        return isSeen;
    }

    public void setIsSeen(String isSeen) {
        this.isSeen = isSeen;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
