package com.example.municipalcars.model;


import com.example.municipalcars.APP;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class UserVehicle implements Serializable {

    private String id = null;
    private double engineValue = 1.6;
    private int fuelConsumptionCorrectionFactor = 0;
    private double fuelPrice = 0.0;
    private String fuelType = null;
    private int maxTripIdle = 0;
    private String make = null;
    private String model = null;
    private String year = null;
    private String name = null;
    private String photo = null;
    private long timestamp;
    private String userId;
    private int vehicleWeight = 0;

    @Override
    public String toString() {
        return "UserVehicle{" + "id='" + id + '\'' + ", engineValue=" + engineValue + ", fuelConsumptionCorrectionFactor=" + fuelConsumptionCorrectionFactor + ", fuelPrice=" + fuelPrice + ", fuelType='" + fuelType + '\'' + ", maxTripIdle=" + maxTripIdle + ", make='" + make + '\'' + ", model='" + model + '\'' + ", year='" + year + '\'' + ", name='" + name + '\'' + ", photo='" + photo + '\'' + ", timestamp=" + timestamp + ", userId='" + userId + '\'' + ", vehicleWeight=" + vehicleWeight + '}';
    }

    public static Map<String, Object> toMap(UserVehicle vehicle) {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", vehicle.getId());
        result.put("engineValue", vehicle.getEngineValue());
        result.put("fuelConsumptionCorrectionFactor", vehicle.getFuelConsumptionCorrectionFactor());
        result.put("fuelPrice", vehicle.getFuelPrice());
        result.put("fuelType", vehicle.getFuelType());
        result.put("maxTripIdle", vehicle.getMaxTripIdle());
        result.put("model", vehicle.getModel());
        result.put("make", vehicle.getMake());
        result.put("year", vehicle.getYear());
        result.put("name", vehicle.getName());
        result.put("photo", vehicle.getPhoto());
        result.put("timestamp", Calendar.getInstance().getTimeInMillis());
        result.put("userId", APP.firebaseAuth.getUid());
        result.put("vehicleWeight", vehicle.getVehicleWeight());
        return result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getEngineValue() {
        return engineValue;
    }

    public void setEngineValue(double engineValue) {
        this.engineValue = engineValue;
    }

    public int getFuelConsumptionCorrectionFactor() {
        return fuelConsumptionCorrectionFactor;
    }

    public void setFuelConsumptionCorrectionFactor(int fuelConsumptionCorrectionFactor) {
        this.fuelConsumptionCorrectionFactor = fuelConsumptionCorrectionFactor;
    }

    public double getFuelPrice() {
        return fuelPrice;
    }

    public void setFuelPrice(double fuelPrice) {
        this.fuelPrice = fuelPrice;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public int getMaxTripIdle() {
        return maxTripIdle;
    }

    public void setMaxTripIdle(int maxTripIdle) {
        this.maxTripIdle = maxTripIdle;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getVehicleWeight() {
        return vehicleWeight;
    }

    public void setVehicleWeight(int vehicleWeight) {
        this.vehicleWeight = vehicleWeight;
    }
}
