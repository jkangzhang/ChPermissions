package com.ikouz.android.chpermission;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

/**
 * Created by franksays on 2016/11/26.
 */
public class PermissionUtil {

    /**
     * Extra name less than version 2.1
     */
    private static final String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";
    /**
     * Extra name bigger than version 2.2
     */
    private static final String APP_PKG_NAME_22 = "pkg";

    /**
     * settings's package name
     */
    private static final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";
    /**
     * setting permission's class name
     */
    private static final String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";

    /**
     * Go to the Setting.You may call this method at a proper time after the user
     * denied the permission you requested.
     * @param context
     * @param packageName
     */
    public static void showInstalledAppDetails(Context context, String packageName) {
        Intent intent = new Intent();
        final int apiLevel = Build.VERSION.SDK_INT;
        if (apiLevel >= 9) {
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", packageName, null);
            intent.setData(uri);
        } else {
            final String appPkgName = (apiLevel == 8 ? APP_PKG_NAME_22
                    : APP_PKG_NAME_21);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName(APP_DETAILS_PACKAGE_NAME,
                    APP_DETAILS_CLASS_NAME);
            intent.putExtra(appPkgName, packageName);
        }
        context.startActivity(intent);
    }
}
