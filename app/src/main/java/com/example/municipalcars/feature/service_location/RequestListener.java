package com.example.municipalcars.feature.service_location;


public interface RequestListener<T> {

    void onSuccess(T data);

    void onFail(String message, int code);
}
