package com.ikouz.android.chpermission;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.SparseArray;

import java.lang.annotation.Annotation;

/**
 * We divide the permissions into two part:static permissions and runtime permissions.
 *
 * Static Permissions is the permissions you would like to check when PermissionHelper is created, you may do
 * this in your Activity's onCreate() or Fragment's onCreateView(),you can use @StaticPermission annotation to add them;
 * Runtime Permissions is which you may want to check during runtime,such as click a button, you should use
 * the @RuntimePermission annotation to add them.
 *
 * When you add them your Activity/Fragment should implement PermissionProposer to get the callback.
 * We suggest you do this in your base Activity/Fragment.
 *
 * Created by franksays on 2016/11/24.
 */
public class PermissionHelper<T> {
    private static final String TAG = "PermissionHelper";

    private PermissionProposer mProposer;
    private String[] staticPermissions, runtimePermissions;
    private int sIndex;
    private SparseArray<String> staticCodeMap = new SparseArray<String>();
    private SparseArray<String> runtimeCodeMap = new SparseArray<String>();

    private int resultCodeBaseline = 100;
    private T context;

    private String mAuxPermission;

    public PermissionHelper(T t) {
        //we just ignore the version less than 23.
        if (Build.VERSION.SDK_INT < 23) {
            return;
        }
        try {
            mProposer = (PermissionProposer) t;
        } catch (Exception e) {
            throw new RuntimeException("your class must implement PermissionProposer");
        }

        if (!(t instanceof Fragment) && !(t instanceof Activity)){
            throw new RuntimeException("not support class type");
        }

        context = t;
        Class cls = t.getClass();
        parseAnnotation(cls);
    }


    private void parseAnnotation(Class cls){
        Annotation staticPermission = cls.getAnnotation(StaticPermission.class);
        if (staticPermission != null) {
            StaticPermission permission = (StaticPermission) staticPermission;
            staticPermissions = permission.permission();
        }

        Annotation runtimePermission = cls.getAnnotation(RuntimePermission.class);
        if (runtimePermission != null) {
            RuntimePermission permission = (RuntimePermission) runtimePermission;
            runtimePermissions = permission.permission();
        }

        checkStaticPermission();
    }

    //When you got a denied callback in static permission list, you may want to ignore it
    //and keep going on checking, then you need this method.
    public void ignore(){
        checkStaticPermission();
    }

    /**
     * check your the permission
     */
    public void checkStaticPermission() {
        if (allStaticPermissionChecked()) {
            Log.v(TAG, "all static permission are granted");
            mProposer.onAllStaticPermissionGranted();
            return;
        }
        String permission = staticPermissions[sIndex++];
        Log.v(TAG, "checking permission [" + permission +"]");
        if (!isPermissionGranted(permission)) {
            Log.v(TAG, "request permission [" + permission +"]");
            staticCodeMap.put(++resultCodeBaseline,permission);
            requestPermission(permission, resultCodeBaseline);
        } else {
            Log.v(TAG, "permission [" + permission +"] is granted");
            mProposer.onPermissionGranted(permission);
            checkStaticPermission();
        }
    }

    /**
     * check permission whose note in @RuntimePermission
     * throw runtime exceptions if you pass a bad index value
     * @param index the index of the runtime permission in the @RuntimePermission annotation
     */
    public void checkRuntimePermission(int index) {
        if(runtimePermissions == null) {
            return;
        }

        if(index >= runtimePermissions.length) {
            throw new RuntimeException("permissions out of index");
        }
        String permission = runtimePermissions[index];
        Log.v(TAG, "checking permission [" + permission +"]");
        if (!isPermissionGranted(permission)) {
            Log.v(TAG, "request permission [" + permission +"]");
            runtimeCodeMap.put(++resultCodeBaseline,permission);
            requestPermission(permission, resultCodeBaseline);
        }else {
            Log.v(TAG, "permission [" + permission +"] is granted");
            mProposer.onPermissionGranted(permission);
        }
    }

    /**
     * request a permission,this method will call a system dialog.
     * @param permissionName
     * @param code
     */
    @TargetApi(23)
    private void requestPermission(String permissionName, int code) {
        if (context instanceof Fragment) {
            ((Fragment)context).requestPermissions(new String[]{permissionName}, code);
        }else if (context instanceof Activity) {
            ((Activity)context).requestPermissions(new String[]{permissionName}, code);
        }
    }

    /**
     * check if the permission is granted
     * @param permissionName
     * @return true:granted other:denied
     */
    @TargetApi(23)
    private boolean isPermissionGranted(String permissionName) {
        if (context instanceof Fragment) {
            return ContextCompat.checkSelfPermission(((Fragment) context).getContext(),permissionName) == PackageManager.PERMISSION_GRANTED;
        } else if (context instanceof Activity) {
            return ContextCompat.checkSelfPermission((Activity)context,permissionName) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    public void makeAsIntercepted(String permission) {
        mAuxPermission = permission;
    }

    private boolean isIntercepted(String permission) {
        return permission.equals(mAuxPermission);
    }

    /**
     * This method should be called in Activity/Fragment's onRequestPermissionResult() callback.
     * It will deal with the permission result for you
     * @param requestCode
     * @param grantResults
     */
    public void onResult(int requestCode,@NonNull String[] permissions, @NonNull int[] grantResults) {
        if(grantResults.length == 0) return;

        // To prevent from reprocess.eg. Both Fragment and its attached Activity
        // used PermissionHelper. It means if we deal with one permission in a
        // Fragment then we don't wanna deal it again in its attaching Activity.

        if (context instanceof Activity && isIntercepted(permissions[0])) {
            return;
        }
        if(context instanceof Fragment && ((Fragment) context).getActivity() instanceof PermissionProposer) {
            PermissionProposer activityProposer = (PermissionProposer) ((Fragment) context).getActivity();
            if(activityProposer.getPermissionHelper() != null) {
                activityProposer.getPermissionHelper().makeAsIntercepted(permissions[0]);
            }
        }

        if (isStaticPermission(requestCode)) {
            String permission = staticCodeMap.get(requestCode);
            if(grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Log.v(TAG, "static permission ["+ permission + "] denied");
                mProposer.onPermissionDenied(permission);
            } else if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "static permission ["+ permission + "] granted");
                mProposer.onPermissionGranted(permission);
                //go on check the static permission
                checkStaticPermission();
            }
        } else if(isStaticPermission(requestCode)) {
            String permission = runtimeCodeMap.get(requestCode);
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Log.v(TAG, "runtime permission [" + permission + "] denied");
                mProposer.onPermissionDenied(permission);
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "runtime permission [" + permission + "] granted");
                mProposer.onPermissionGranted(permission);
            }
        }
    }

    /**
     * check if it's a static permission
     * @param code
     * @return
     */
    private boolean isStaticPermission(int code) {
        return staticCodeMap.get(code) != null;
    }


    /**
     * check if the static permissions had been all checked.
     * @return true:all checked, false:otherwise
     */
    private boolean allStaticPermissionChecked() {
        return staticPermissions == null || sIndex >= staticPermissions.length;
    }

}
