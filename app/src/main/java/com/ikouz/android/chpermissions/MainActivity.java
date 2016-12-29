package com.ikouz.android.chpermissions;

import android.Manifest;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.ikouz.android.chpermission.RuntimePermission;
import com.ikouz.android.chpermission.StaticPermission;

@StaticPermission(permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_PHONE_STATE, Manifest.permission.SEND_SMS})
@RuntimePermission(permission = {Manifest.permission.CAMERA,Manifest.permission.ACCESS_COARSE_LOCATION})
public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnOpenCamera = (Button)findViewById(R.id.btn_open_camera);
        btnOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPermissionHelper.checkRuntimePermission(0);
            }
        });
    }

    @Override
    public void onAllStaticPermissionGranted() {
        super.onAllStaticPermissionGranted();
        Log.i(TAG, "all static permissions is Granted");
    }

    @Override
    public void onPermissionDenied(String permission) {
        super.onPermissionDenied(permission);
        Log.i(TAG, "permission [" + permission + "] is denied");
        if(permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            finish();
        }
    }

    @Override
    public void onPermissionGranted(String permission) {
        super.onPermissionGranted(permission);
        Log.i(TAG, "permission [" + permission + "] is granted");
        if(permission.equals(Manifest.permission.CAMERA)) {
            // Open camera.
        }
    }

}
