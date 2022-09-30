package com.example.mapion.settings;

import java.lang.reflect.Field;

public  class WrapperSettings{
    public WrapperSettings(Field field, SettingItem item){

        this.field = field;
        this.item = item;
    }
    public final Field field;
    public final SettingItem item;
}
