package com.example.Covid19App;

import java.io.Serializable;


//https://stackoverflow.com/questions/2736389/how-to-pass-an-object-from-one-activity-to-another-on-android
@SuppressWarnings("serial")
public class DoctorClass {

    String name;
    String number;
    String id;
    String userId;
    String address;
    String clinic;
    String domain;
    String lat;
    String lng;
    String start;
    String end;

    public DoctorClass(String name, String number, String id, String userId, String address, String clinic, String domain, String lat, String lng, String start, String end) {
        this.name = name;
        this.number = number;
        this.id = id;
        this.userId = userId;
        this.address = address;
        this.clinic = clinic;
        this.domain = domain;
        this.lat = lat;
        this.lng = lng;
        this.start = start;
        this.end = end;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getClinic() {
        return clinic;
    }

    public void setClinic(String clinic) {
        this.clinic = clinic;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

}
