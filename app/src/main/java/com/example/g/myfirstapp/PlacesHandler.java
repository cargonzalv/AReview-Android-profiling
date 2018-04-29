package com.example.g.myfirstapp;

import android.util.Log;

import com.example.g.myfirstapp.Classes.PlaceOwn;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by G on 23-Mar-18.
 */

public class PlacesHandler
{
    //save pagetoken anterior (si lo hay)
    public static final String more = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?key=%s&pagetoken=%s";
    public static final String URL_PLACES = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=%f,%f&rankby=distance&type=restaurant&key=%s";
    private String json;

    public PlacesHandler(String pJson)
    {
        json=pJson;
    }
    public ArrayList<PlaceOwn> getPlaces(double currentLat, double currentLng) throws Exception
    {
        ArrayList<PlaceOwn> arr = null;
        try
        {
            JSONObject obj = new JSONObject(json);
            JSONArray results = obj.getJSONArray("results");
             arr = new ArrayList<>();
            for(int i=0;i<results.length();i++)
            {
                JSONObject actual =(JSONObject) results.get(i);
                PlaceOwn p = new PlaceOwn(actual);
                p.setDistanceTo(currentLat,currentLng);

                arr.add(p);
            }
        }
        catch (Throwable t)
        {
            Log.e("My App", "Could not parse malformed JSON: \"" + json + "\"");
            t.printStackTrace();
            throw  new Exception("Check your internet connection");
        }
        return arr;
    }
}
