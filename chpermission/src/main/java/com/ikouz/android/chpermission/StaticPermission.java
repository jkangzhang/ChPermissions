package com.ikouz.android.chpermission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Permissions you want to check before as far as
 * the PermissionHelper is created. You will often
 * do this in your Activity's onCreate or Fragment's onCreateView()
 *
 * Created by franksays on 2016/11/24.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface StaticPermission {
    String[] permission();
}


