package com.example.mapion.settings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;


import com.example.mapion.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SettingsBuilder {
    private final Context context;
    private final Object settings;
    private IActionSettings onUpdateaction;
    ListView v;
    ListAdapteSettings adapter;

    public SettingsBuilder(Context context, Object settings,IActionSettings onUpdateaction){

        this.context = context;
        this.settings = settings;
        this.onUpdateaction = onUpdateaction;
        activate();
    }

    public SettingsBuilder(Context context, Object settings,ListView listView,IActionSettings onUpdateaction){

        this.context = context;
        this.settings = settings;
        this.onUpdateaction = onUpdateaction;
        this.v=listView;
        activate();
    }
    private void activate(){
        if(v==null){
            v = (ListView) LayoutInflater.from(context).inflate(R.layout.settings_host, null);
        }

        List<WrapperSettings>  settingsList=new ArrayList<>();

        List<Field> fields = getAllFields(settings.getClass());
        for (Field field : fields) {
            SettingItem item=field.getAnnotation(SettingItem.class);
            if(item!=null){
                settingsList.add(new WrapperSettings(field,item));
            }
        }
        Collections.sort(settingsList, new Comparator<WrapperSettings>() {
            @Override
            public int compare(WrapperSettings o1, WrapperSettings o2) {
                return Integer.compare(o1.item.index(),o2.item.index());
            }
        });
        if(settingsList.size()>0){
            adapter=new ListAdapteSettings(context,0,settingsList,settings,onUpdateaction);
            v.setAdapter(adapter);
        }

    }
    public View getViev(){
        return v;
    }

    public void  refresh(){
        adapter.notifyDataSetChanged();
    }

    private static List<Field> getAllFields(Class clazz) {
        List<Field> fields = new ArrayList<>();

        fields.addAll(Arrays.asList(clazz.getDeclaredFields()));

        Class superClazz = clazz.getSuperclass();
        if (superClazz != null) {
            fields.addAll(getAllFields(superClazz));
        }

        return fields;
    }

}
