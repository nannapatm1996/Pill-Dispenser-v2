package com.example.myapplication;

import android.content.pm.PackageManager;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class BaseActivity extends AppCompatActivity {
    private ProgressBar mProgressBar;

    public void setProgressBar(int resId) {
        mProgressBar = findViewById(resId);
    }

    public void showProgressBar() {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    public void hideProgressBar() {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    protected boolean hasPermissions(String... permissions){
        for (String permission : permissions)
            if(checkSelfPermission(permission)!= PackageManager.PERMISSION_GRANTED)
                return false;
        return true;
    }

    public void requestPermission(int requestCode, String... permissions) {
        for(String permission: permissions) {
            if (shouldShowRequestPermissionRationale(permission)) {
                showLongToast(getString(R.string.permission_required));
                return;
            }
        }

        requestPermissions(permissions, requestCode);
    }

    public void showToast(String text) {
        Toast.makeText(
                this,
                text,
                Toast.LENGTH_SHORT
        ).show();
    }

    public void showLongToast(String text){
        Toast.makeText(
                this,
                text,
                Toast.LENGTH_LONG
        ).show();
    }






}



