package com.example.municipalcars.model;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class Car {

    private long createdAt;
    private String typeCar = null;
    private String colorCar = null;
    private String numCar = null;
    private String details = null;
    private String name = null;
    private String photo ;



    public static Map<String, Object> toMap(Car car) {
        HashMap<String, Object> result = new HashMap<>();
        result.put("createdAt", Calendar.getInstance().getTimeInMillis());
        result.put("typeCar",car.getTypeCar());
        result.put("photo",car.getPhoto());
        result.put("colorCar",car.getColorCar());
        result.put("details",car.getDetails());
        result.put("numCar",car.getNumCar());
        result.put("name",car.getName());
        return result;
    }


    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }


    public String getTypeCar() {
        return typeCar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public void setTypeCar(String typeCar) {
        this.typeCar = typeCar;
    }

    public String getColorCar() {
        return colorCar;
    }

    public void setColorCar(String colorCar) {
        this.colorCar = colorCar;
    }

    public String getNumCar() {
        return numCar;
    }

    public void setNumCar(String numCar) {
        this.numCar = numCar;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

}
