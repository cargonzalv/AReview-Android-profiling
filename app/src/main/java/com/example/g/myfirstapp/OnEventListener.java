package com.example.g.myfirstapp;

/**
 * Created by G on 22-Mar-18.
 */

public interface OnEventListener<T> {
    public void onSuccess(T object);
    public void onFailure(Exception e);
}
