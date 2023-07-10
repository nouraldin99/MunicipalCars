package com.example.municipalcars.feature;


public interface RequestListener<T> {

    void onSuccess(T data);

    void onFail(String message);
}
