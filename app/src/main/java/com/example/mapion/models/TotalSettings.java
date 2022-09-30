package com.example.mapion.models;

import com.example.mapion.R;
import com.example.mapion.orm2.Column;
import com.example.mapion.orm2.Configure;
import com.example.mapion.orm2.PrimaryKey;
import com.example.mapion.orm2.Table;
import com.example.mapion.settings.SettingItem;
import com.example.mapion.settings.SettingType;
import com.google.gson.Gson;

import java.util.List;

@Table(TotalSettings.TABLE_NAME)
public class TotalSettings {
    public static final String TABLE_NAME="total_settings";
    private static final Object lock=new Object();
    private static TotalSettings totalSettings;

    @PrimaryKey("_id")
    public transient int _id;

    @Column("json")
    public transient String json;




    @SettingItem(value = SettingType.EDIT_TEXT,index = 1, strbig = R.string.s1)
    public   String url="http://192.168.1.63:3000";

//    /**
//     * показывать кнопку пробитие чка электронной продажи
//     */
//    //@SettingItem(value = SettingType.BOOLEAN,index = 5, strbig = R.string.s7)
//    public boolean useAcuaring = true;
//    /**
//     * использовать страницу сдачи
//     */
//    @SettingItem(value = SettingType.BOOLEAN,index = 6, strbig = R.string.s9)
//    public boolean isShowDilivery=true;
//
//
//
//
//    /**
//     * использовать тестовый режим
//     */
//    @SettingItem(value = SettingType.BOOLEAN,index = 8, strbig = R.string.s1)
//    public boolean isUsageMaxSumm=true;


    public static TotalSettings getInstance(){
        synchronized (lock){
            if(totalSettings==null){
                List<TotalSettings> ss= Configure.getSession().getList(TotalSettings.class,null);

                if(ss.size()==0){
                    totalSettings =  new TotalSettings();

                }else {
                    String s=ss.get(0).json;
                    Gson gson=new Gson();
                    totalSettings= gson.fromJson(s, TotalSettings.class);
                    totalSettings._id=ss.get(0)._id;
                }
            }
            return totalSettings;
        }
    }


    public void save(){
        synchronized (lock){
            this.json=new Gson().toJson(this);
            Configure configure=Configure.getSession();
            configure.beginTransaction();
            try {
                configure.deleteTable(TotalSettings.TABLE_NAME);
                configure.insert(this);
                configure.commitTransaction();
            }finally {
                configure.endTransaction();
            }
        }
    }
}
