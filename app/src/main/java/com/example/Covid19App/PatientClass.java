package com.example.Covid19App;

public class PatientClass {

    String userId, name, symptoms, age;

    public PatientClass(String userId, String name, String symptoms, String age) {
        this.userId = userId;
        this.name = name;
        this.symptoms = symptoms;
        this.age = age;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
}
