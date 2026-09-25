package com.example.cap;

import org.osmdroid.util.GeoPoint;

public class DivingSite {
    private String name;
    private double latitude;
    private double longitude;
    private String contact;
    private String description;

    public DivingSite(String name, double latitude, double longitude){
        this.name = name;
        this.latitude= latitude;
        this.longitude = longitude;
    }

    public String getName(){
        return name;
    }
    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public GeoPoint getGeoPoint(){
        return new GeoPoint(latitude,longitude);
    };

    public void setDescription(String description){
        this.description = description;
    }

    public String getDescription(){
        return this.description;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getContact() {
        return contact;
    }


}
