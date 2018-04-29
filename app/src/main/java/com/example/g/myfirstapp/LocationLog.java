package com.example.g.myfirstapp;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by G on 27-Apr-18.
 */

public class LocationLog
{
    private  double lat;
    private  double lng;
    private  Date createdAt;
    private  String uid;
    private  String username;
    public LocationLog(String uid, double lat, double lng)
    {
        this.uid=uid;
        this.lat=lat;
        this.lng=lng;
        this.createdAt=new Date();

    }

    public LocationLog(Map location)
    {
        uid = (String) location.get("uid");
        String latValue=location.get("lat").toString();
        lat = Double.parseDouble(latValue);
        String lngValue=location.get("lng").toString();
        lng = Double.parseDouble(lngValue);
        username =(String) location.get("username");

    }

    Map<String,Object> getMap()
    {
        Map<String,Object>locationLog = new HashMap<>();
        locationLog.put("uid",uid);
        locationLog.put("lat",lat);
        locationLog.put("lng",lng);
        locationLog.put("createdAt",createdAt);
        return locationLog;
    }

    public String getUsername()
    {
        return username;
    }

    public double getLat()
    {
        return lat;
    }

    public double getLng()
    {
        return lng;
    }
}
