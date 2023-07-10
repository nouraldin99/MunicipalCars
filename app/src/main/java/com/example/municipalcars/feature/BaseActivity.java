package com.example.municipalcars.feature;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.municipalcars.databinding.DialogMessageBinding;


public class BaseActivity extends AppCompatActivity implements BaseView {

    private LoadingDialog loadingDialog;
    private BroadcastReceiver myReceiver;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        initDialog();


//        getGlobalViewModel().print();
//
//        getGlobalViewModel().addListener(status -> {
//
//            Log.d("DDDD" , " Network Changed to: " + status);
//        });
//
//
//        getGlobalViewModel().updateInternetStatus(true);

        Log.d("DDDD", "Current Activity: " + this.getClass().getSimpleName());
        receiveBroadcastInApp();
    }

    public void initDialog() {
        loadingDialog = new LoadingDialog(this);
        loadingDialog.setDialogCancelable();
    }



    public void makeToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    public int getColorFromR(int id) {
        return getResources().getColor(id);
    }

    public Drawable getDrawableFromR(int id) {
        return ContextCompat.getDrawable(this, id);
    }

    @Override
    public void showProgress() {
        loadingDialog.showDialog();
    }

    @Override
    public void hideProgress() {
        loadingDialog.hideDialog();
    }

    @Override
    public void showErrorMessage(String massage) {
        DialogMessageBinding bindingDialog = DialogMessageBinding.inflate(getLayoutInflater());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(bindingDialog.getRoot());
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        bindingDialog.txtMessage.setText(massage);
        bindingDialog.btnOk.setOnClickListener(view ->{
            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    public void showErrorDialog(String massage, String okMsg, String cancelMsg, String action) {
    }

    ActivityResultLauncher<Intent> noInternetActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                // There are no request codes
                Intent data = result.getData();
            }
        }
    });

    private void receiveBroadcastInApp() {
//        myReceiver = new BroadcastReceiver() {
//            @SuppressLint("SetTextI18n")
//            public void onReceive(Context context, Intent intent) {
//                if (intent.hasExtra(TYPE) && intent.hasExtra(OBJECT_BY_TYPE)) {
//                    Bundle extras = intent.getExtras();
//                    if (extras != null) {
//                        String type = extras.getString(TYPE);
//                        Object object = extras.getSerializable(OBJECT_BY_TYPE);
//                        setReceiveData(type, object);
//                    }
//                }
//            }
//        };
    }

    public void setReceiveData(String type, Object object) {
        // EMPTY FUNCTION
    }

    @Override
    protected void onResume() {
//        IntentFilter intentFilter = new IntentFilter(BROADCAST);
//        if (myReceiver != null) registerReceiver(myReceiver, intentFilter);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
//        try {
//            if (myReceiver != null) unregisterReceiver(myReceiver);
//        } catch (Exception ignored) {
//            // ignore this
//        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @SuppressLint("SetTextI18n")
    public void updateLabel(TextView txt, int day, int month, int year) {
        String finalDay = String.valueOf(day);
        String finalMonth = String.valueOf(getTrueMonth(month));
        if (day < 10) {
            finalDay = "0" + day;
        }
        if (getTrueMonth(month) < 10) {
            finalMonth = "0" + finalMonth;
        }
        txt.setText(year + "-" + finalMonth + "-" + finalDay);
    }

    public static int getTrueMonth(int calMonth) {
        if (calMonth == 1) return 2;
        else if (calMonth == 2) return 3;
        else if (calMonth == 3) return 4;
        else if (calMonth == 4) return 5;
        else if (calMonth == 5) return 6;
        else if (calMonth == 6) return 7;
        else if (calMonth == 7) return 8;
        else if (calMonth == 8) return 9;
        else if (calMonth == 9) return 10;
        else if (calMonth == 10) return 11;
        else if (calMonth == 11) return 12;
        return 1;
    }

}
