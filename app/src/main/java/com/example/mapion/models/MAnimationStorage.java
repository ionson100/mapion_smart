package com.example.mapion.models;

import com.example.mapion.orm2.Column;
import com.example.mapion.orm2.Configure;
import com.example.mapion.orm2.PrimaryKey;
import com.example.mapion.orm2.Table;

import java.util.List;

@Table(MAnimationStorage.TABLE_NAME)
public class MAnimationStorage {
    public static final String TABLE_NAME = "storage_animation";
    @PrimaryKey("_id")
    public int _id;
    @Column("index_e")
    public int index;
    @Column("m_lat")
    public double markerLatitude;
    @Column("m_lon")
    public double marketLongitude;
    @Column("run")
    public boolean isRun;

    public static MAnimationStorage getInstance() {
        List<MAnimationStorage> list = Configure.getSession().getList(MAnimationStorage.class, null);
        if (list.size() == 0) {
            return null;
        }
        return list.get(0);
    }

    public static void save(MAnimationStorage storage) {
        Configure configure = Configure.getSession();
        try {
            configure.beginTransaction();
            configure.deleteTable(MAnimationStorage.TABLE_NAME);
            configure.insert(storage);
            configure.commitTransaction();
        } finally {
            configure.endTransaction();
        }
    }

    public static void clearStorage() {
        Configure.getSession().deleteTable(MAnimationStorage.TABLE_NAME);
    }
}
