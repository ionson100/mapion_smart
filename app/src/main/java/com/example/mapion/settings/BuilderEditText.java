package com.example.mapion.settings;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mapion.R;



public class BuilderEditText {

    private final Context context;
    private final WrapperSettings ws;
    private final Object o;
    private final IActionSettings iAction;

    public BuilderEditText(Context context, WrapperSettings ws, Object o, IActionSettings iAction  ) {

        this.context = context;
        this.ws = ws;
        this.o = o;
        this.iAction = iAction;
    }
    public LinearLayout build(){
        LinearLayout v = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.settings_editext, null);
        TextView b=v.findViewById(R.id.text_big);
        TextView s=v.findViewById(R.id.text_smail);
        TextView vs=v.findViewById(R.id.text_value);
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
            Object ss=ws.field.get(o);
            if (ss!=null){
                vs.setText(String.valueOf(ss));
            }

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogEditText  editText=new DialogEditText();
                editText.iActionSettings=iAction;
                editText.settings=o;
                editText.ws=ws;
                editText.show(((AppCompatActivity)context).getSupportFragmentManager(),"asdv34dd");
            }
        });
        return v;

    }
}
