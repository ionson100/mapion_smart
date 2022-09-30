package com.example.mapion.models;

import com.example.mapion.orm2.Column;
import com.example.mapion.orm2.Configure;
import com.example.mapion.orm2.PrimaryKey;
import com.example.mapion.orm2.Table;

import java.io.Console;
import java.util.List;

@Table(MStorageMapView.TABLE_NAME)
public class MStorageMapView {

    private static MStorageMapView _StorageMapView;
    public static final String TABLE_NAME="storage_map_view";
    @PrimaryKey("_id")
    public int _id;

    @Column("lon")
    public double lon;

    @Column("lat")
    public double lat ;

    @Column("zoom")
    public double zoom;

    public static MStorageMapView getInstance(){
        if(MStorageMapView._StorageMapView==null){
            List<MStorageMapView> list = Configure.getSession().getList(MStorageMapView.class,null);
            if(list.size()==0){
                MStorageMapView._StorageMapView=new MStorageMapView();
                MStorageMapView._StorageMapView.lat=48.8583;
                MStorageMapView._StorageMapView.lon=2.2944;
                MStorageMapView._StorageMapView.zoom=10;
            }else {
                MStorageMapView._StorageMapView=new MStorageMapView();
                MStorageMapView._StorageMapView.lat=list.get(0).lat;
                MStorageMapView._StorageMapView.lon=list.get(0).lon;
                MStorageMapView._StorageMapView.zoom=list.get(0).zoom;;
            }
        }
        return _StorageMapView;
    }
    public void save(){
        Configure configure=Configure.getSession();
        configure.beginTransaction();
        try{
            configure.deleteTable(MStorageMapView.TABLE_NAME,null);
            configure.insert(this);
            configure.commitTransaction();
        }catch (Exception e){

        }finally {
            configure.endTransaction();
        }

    }
}
