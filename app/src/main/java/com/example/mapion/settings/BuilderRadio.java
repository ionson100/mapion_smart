package com.example.mapion.settings;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mapion.R;


class BuilderRadio {
    private final Context context;
    private final WrapperSettings ws;
    private final Object settings;
    private final IActionSettings iActionSettings;

    public BuilderRadio(Context context, WrapperSettings p, Object settings, IActionSettings iActionSettings) {
        this.context = context;
        this.ws = p;
        this.settings = settings;
        this.iActionSettings = iActionSettings;
    }

    public View build() {
        LinearLayout v = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.settings_editext, null);
        TextView b=v.findViewById(R.id.text_big);
        TextView s=v.findViewById(R.id.text_smail);
        final TextView vs=v.findViewById(R.id.text_value);
        if(ws.item.strbig()!=0){
            b.setText(ws.item.strbig());
        }else {
            v.findViewById(R.id.h_big).setVisibility(View.GONE);
        }
        if(ws.item.strsmail()!=0){
            s.setText(ws.item.strsmail());
        }else {
            v.findViewById(R.id.h_smail).setVisibility(View.GONE);
        }
        try {
            Object ss=ws.field.get(settings);
            if (ss!=null){
                vs.setText(String.valueOf(ss));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogRadio  editText=new DialogRadio();
                editText.iActionSettings=new IActionSettings() {
                    @Override
                    public String action(Object o) {
                        try {
                            vs.setText(String.valueOf(ws.field.get(settings)));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                        return iActionSettings.action(o);
                    }
                };
                editText.settings=settings;
                editText.ws=ws;
                editText.show(((AppCompatActivity)context).getSupportFragmentManager(),"asdv34dd");
            }
        });
        return v;

    }
}
