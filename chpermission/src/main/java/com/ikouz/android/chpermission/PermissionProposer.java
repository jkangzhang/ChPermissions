package com.ikouz.android.chpermission;

/**
 * You sh
 * Created by fidozhang on 2016/11/24.
 */
public interface PermissionProposer {

    /**
     * call back when all static permission is granted
     */
    void onAllStaticPermissionGranted();

    /**
     * call back when a permission is denied.
     * @param permission
     */
    void onPermissionDenied(String permission);

    /**
     * call back when a permission is granted.
     * @param permission
     */
    void onPermissionGranted(String permission);

    PermissionHelper getPermissionHelper();

}
