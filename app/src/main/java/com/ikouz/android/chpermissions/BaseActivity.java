package com.ikouz.android.chpermissions;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.ikouz.android.chpermission.PermissionHelper;
import com.ikouz.android.chpermission.PermissionProposer;


/**
 * Created by frankzhang on 2016/11/24.
 */
public class BaseActivity extends AppCompatActivity implements PermissionProposer {
    private static final String TAG = "BaseActivity";
    protected PermissionHelper mPermissionHelper;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPermissionHelper = new PermissionHelper<Activity>(this);
    }
    @Override
    public void onAllStaticPermissionGranted() {

    }
    @Override
    public void onPermissionDenied(String permission) {

    }
    @Override
    public void onPermissionGranted(String permission) {

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPermissionHelper.onResult(requestCode, permissions, grantResults);
    }
    @Override
    public PermissionHelper getPermissionHelper() {
        return mPermissionHelper;
    }
}
