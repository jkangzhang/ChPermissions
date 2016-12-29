package com.ikouz.android.chpermissions;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ikouz.android.chpermission.PermissionHelper;
import com.ikouz.android.chpermission.PermissionProposer;
import com.ikouz.android.chpermission.RuntimePermission;
import com.ikouz.android.chpermission.StaticPermission;

/**
 * Created by franksays on 2016/11/24.
 */


@StaticPermission(permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_PHONE_STATE, Manifest.permission.CAMERA})
@RuntimePermission(permission = {Manifest.permission.SEND_SMS,Manifest.permission.ACCESS_COARSE_LOCATION})
public class BaseFragment extends Fragment implements PermissionProposer {
    protected PermissionHelper mPermissionHelper;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPermissionHelper = new PermissionHelper<Fragment>(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_base,container,false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mPermissionHelper.onResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onAllStaticPermissionGranted() {

    }

    @Override
    public void onPermissionDenied(String permission) {
        mPermissionHelper.ignore();
    }

    @Override
    public void onPermissionGranted(String permission) {

    }

    @Override
    public PermissionHelper getPermissionHelper() {
        return mPermissionHelper;
    }
}
