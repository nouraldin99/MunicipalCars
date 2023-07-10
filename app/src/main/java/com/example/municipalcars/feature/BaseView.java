package com.example.municipalcars.feature;

public interface BaseView {

    void showProgress();

    void hideProgress();

    void showErrorMessage(String massage);

    void showErrorDialog(String massage, String okMsg, String cancelMsg, String action);

}
