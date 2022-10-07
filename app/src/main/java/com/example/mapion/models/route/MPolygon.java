package com.example.mapion.models.route;

import android.app.Activity;

import com.example.mapion.models.MInnerDescription;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class MPolygon {
    public UUID id;
    public List<MDescription> mDescription;
    public GeoJsonPolygon coordinates ;
    public List<MContent> content ;
    public String getName(Activity activity){
        if(mDescription==null||mDescription.size()==0){
            return  "no data";
        }
        Locale currentLocale=activity.getResources().getConfiguration().locale;
        String lang=currentLocale.getCountry().toLowerCase();

        String res=null;

        for (MDescription description : mDescription) {
            if(description.lang.equals(lang));
            res=description.text;
        }
        if(res==null){
            for (MDescription description : mDescription) {
                if(description.lang.equals("en"));
                res=description.text;
            }
        }
        if(res==null){
            res=mDescription.get(0).text;
        }
        return res;
    }
}
