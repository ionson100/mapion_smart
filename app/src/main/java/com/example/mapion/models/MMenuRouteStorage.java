package com.example.mapion.models;

import com.example.mapion.orm2.Column;
import com.example.mapion.orm2.Configure;
import com.example.mapion.orm2.PrimaryKey;
import com.example.mapion.orm2.Table;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

@Table(MMenuRouteStorage.TABLE_NAME)
public class MMenuRouteStorage {
    public static final String TABLE_NAME="menu_routes";
    @PrimaryKey("_id")
    public int _id;
    @Column("json")
    public String json;
    public static void saveRouteAsMenu(List<MTempFreeRoutes> routes){
        Configure c= Configure.getSession();
        try{
            c.beginTransaction();
            c.deleteTable(MMenuRouteStorage.TABLE_NAME);
            MMenuRouteStorage currentRoute=new MMenuRouteStorage();
            currentRoute.json=new Gson().toJson(routes);
            c.insert(currentRoute);
            c.commitTransaction();
        }finally {
            c.endTransaction();
        }
    }
    public static List<MTempFreeRoutes> getMTempFreeRoutesForMenu(){
        List<MMenuRouteStorage> list=Configure.getSession().getList(MMenuRouteStorage.class,null);
        if(list.size()==0) return null;
        Type listType = new TypeToken<List<MTempFreeRoutes>>(){}.getType();
        List<MTempFreeRoutes> routes=new Gson().fromJson(list.get(0).json,listType);
        return routes;
    }
}
