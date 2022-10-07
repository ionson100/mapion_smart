package com.example.mapion.models;

import android.app.Activity;

import com.example.mapion.IAction;
import com.example.mapion.models.route.MRoute;
import com.example.mapion.orm2.Column;
import com.example.mapion.orm2.Configure;
import com.example.mapion.orm2.PrimaryKey;
import com.example.mapion.orm2.Table;
import com.example.mapion.seder.SenderRouteFreeOne;
import com.google.gson.Gson;

import java.util.List;
import java.util.UUID;

@Table(MCurrentRoute.TABLE_NAME)
public class MCurrentRoute {
    public static final String TABLE_NAME="current_route_1";
    @PrimaryKey("_id")
    public int _id;
    @Column("json")
    public String json;

    public static void getCurrentRoute(Activity activity, IAction<MRoute> iAction){
        List<MCurrentRoute> list=Configure.getSession().getList(MCurrentRoute.class,null);
        if(list.size()==0){
             iAction.Action(null);

        }else{
            String s=list.get(0).json;
            SenderRouteFreeOne routeFactory=new SenderRouteFreeOne();
            routeFactory.getFreeRoute(UUID.fromString(s),activity,mRoute -> {
                iAction.Action(mRoute);
            });
        }


    }
    public static void clear(){
        Configure.getSession().deleteTable(MCurrentRoute.TABLE_NAME);
    }

    public static void saveRouteAsCurrent(MRoute mRoute){
       Configure c= Configure.getSession();
       try{
           c.beginTransaction();
           c.deleteTable(MCurrentRoute.TABLE_NAME);
           MCurrentRoute currentRoute=new MCurrentRoute();
           currentRoute.json=mRoute.id+"";
           c.insert(currentRoute);
           c.commitTransaction();
       }finally {
           c.endTransaction();
       }

    }
    public static int getAnyRoute(){
       int f= (int) Configure.getSession().executeScalar("select count(*) from "+MCurrentRoute.TABLE_NAME);
       return f;
    }
}
