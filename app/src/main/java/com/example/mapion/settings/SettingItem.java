package com.example.mapion.settings;

import android.text.InputType;

import androidx.annotation.StringRes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(value = ElementType.FIELD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface SettingItem {
    SettingType value();
     int index() default 0;
    @StringRes int strbig() default 0;
    @StringRes int strsmail() default 0;
    int  EDIT_INPUT_TYPE() default InputType.TYPE_CLASS_TEXT;
    Class TYPE_SETTINGS_LIST() default int.class;
}
