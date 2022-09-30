package com.example.mapion.utils;

import android.content.Context;

import com.example.mapion.models.ModelCollection;
import com.example.mapion.orm2.Configure;

public class Starter {
    public static void start(Context context){
        new Configure(Utils.patchDir+Utils.BASE_NAME, context, ModelCollection.classes);
    }
}
