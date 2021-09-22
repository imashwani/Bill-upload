package com.example.fileupload.model;


public class LoginResponse {
    private String info;
    private Payload payload;


    public LoginResponse(String info, Payload payload) {
        this.info = info;
        this.payload = payload;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Payload getPayload() {
        return payload;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }
}