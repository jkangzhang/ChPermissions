package com.ikouz.android.chpermission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Permissions you want to check during runtime,
 * you can do it anytime.
 *
 * Created by franksays on 2016/11/24.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface RuntimePermission {
    String[] permission();
}
