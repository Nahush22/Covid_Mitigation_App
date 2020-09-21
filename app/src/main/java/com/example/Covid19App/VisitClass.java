package com.example.Covid19App;

public class VisitClass {

    String doctorId, clinic, name, timings;

    public VisitClass(String doctorId, String clinic, String name, String timings) {
        this.doctorId = doctorId;
        this.clinic = clinic;
        this.name = name;
        this.timings = timings;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getClinic() {
        return clinic;
    }

    public void setClinic(String clinic) {
        this.clinic = clinic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimings() {
        return timings;
    }

    public void setTimings(String timings) {
        this.timings = timings;
    }
}
