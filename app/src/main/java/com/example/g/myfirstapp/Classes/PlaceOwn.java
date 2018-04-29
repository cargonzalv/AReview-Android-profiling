package com.example.g.myfirstapp.Classes;

import android.location.Location;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by G on 22-Mar-18.
 */

public class PlaceOwn implements Serializable
{

    private String name;
    private String id;
    private double lat;
    private double lng;
    private String address;
    private String telefono;
    private String horario;
    private float distancia;

    public PlaceOwn(JSONObject actual) throws JSONException {

        JSONObject geometry = actual.getJSONObject("geometry");
        JSONObject location = geometry.getJSONObject("location");
        lat=location.getDouble("lat");
        lng=location.getDouble("lng");
        address=actual.getString("vicinity");
        name=actual.getString("name");
        id=actual.getString("place_id");
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getAddress() {
        return address;
    }

    public String getName() { return name; }

    public String getTelefono() {
        return telefono;
    }

    public String getHorario() {
        return horario;
    }

    public String getId() {
        return id;
    }

    public PlaceOwn(double lat, double lng, String address, String name, String id) {
        this.lat = lat;
        this.lng = lng;
        this.address = address;
        this.name = name;
        this.id = id;
    }

    @Override
    public String toString() {
        return "PlaceOwn{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                ", address='" + address + '\'' +
                '}';
    }

    public void setDistanceTo(double currentLat, double currentLng)
    {
        float[] distance = new float[2];

        Location.distanceBetween( currentLat, currentLng,lat, lng, distance);
        distancia = distance[0];
    }
    public float getDistancia()
    {
        return distancia;
    }
}
