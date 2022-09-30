package com.example.mapion.settings;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import java.util.List;


public class ListAdapteSettings extends ArrayAdapter<WrapperSettings> {

    private Context context;
    private final Object settings;
    private final IActionSettings onUpdateaction;

    public ListAdapteSettings(Context context, int resource, List<WrapperSettings> objects, Object settings, IActionSettings onUpdateaction) {
        super(context, resource, objects);
        this.context = context;
        this.settings = settings;
        this.onUpdateaction = onUpdateaction;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View mView = new LinearLayout(context);
        final WrapperSettings p = getItem(position);

        switch (p.item.value()){
            case EDIT_TEXT:{
               mView=new BuilderEditText(context, p, settings, onUpdateaction).build();
                break;
            }
            case BOOLEAN:{
                mView=new BuilderBoolean(context, p, settings, onUpdateaction).build();
                break;
            }
            case RADIO:{
                mView=new BuilderRadio(context, p, settings, onUpdateaction).build();
                break;
            }

        }

        return mView;
    }
}
