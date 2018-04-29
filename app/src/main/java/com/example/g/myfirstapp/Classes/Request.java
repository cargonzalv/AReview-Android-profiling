package com.example.g.myfirstapp.Classes;

/**
 * Created by G on 12-Apr-18.
 */

public class Request {
    private String id;
    private String email;

    @Override
    public String toString() {
        return email;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Request()
    {

    }

    public Request(String id, String email) {
        this.id = id;
        this.email = email;
    }
}
