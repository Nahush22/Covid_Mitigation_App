package com.example.Covid19App;

public class MigrantClass {

    String docId, name, number, gender, home, current, lat, lng;

    public MigrantClass(String docId, String name, String number, String gender, String home, String current, String lat, String lng) {
        this.docId = docId;
        this.name = name;
        this.number = number;
        this.gender = gender;
        this.home = home;
        this.current = current;
        this.lat = lat;
        this.lng = lng;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public String getCurrent() {
        return current;
    }

    public void setCurrent(String current) {
        this.current = current;
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
}
