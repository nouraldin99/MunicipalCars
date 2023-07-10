package com.example.municipalcars.model;

import com.example.municipalcars.APP;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class UserData {

    private String userId;
    private long createdAt;
    private String email = null;
    private String name = null;
    private String password = null;
    private String type = null;
    private String phone = null;
    private String numCard = null;
    private String address = null;

    private Car car;
    private String photo ;


    public static Map<String, Object> toMap(UserData userData) {
        HashMap<String, Object> result = new HashMap<>();
        result.put("createdAt", Calendar.getInstance().getTimeInMillis());
        result.put("email", userData.getEmail());
        result.put("name", userData.getName());
        result.put("photo", userData.getPhoto());
        result.put("address", userData.getAddress());
        result.put("type", userData.getType());
        result.put("numCard", userData.getNumCard());
        result.put("phone", userData.getPhone());
        result.put("car", Car.toMap(userData.getCar()));
        result.put("userId", APP.firebaseAuth.getUid());
        return result;
    }


    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getNumCard() {
        return numCard;
    }

    public void setNumCard(String numCard) {
        this.numCard = numCard;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }
}
