package com.example.mapion.settings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.example.mapion.R;


public class BuilderBoolean implements CompoundButton.OnCheckedChangeListener {

    private final Context context;
    private final WrapperSettings ws;
    private final Object o;
    private final IActionSettings iAction;

    public BuilderBoolean(Context context, WrapperSettings ws, Object o, IActionSettings iAction  ) {

        this.context = context;
        this.ws = ws;
        this.o = o;
        this.iAction = iAction;
    }

    public LinearLayout build(){
        LinearLayout view = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.settings_boolean, null);
        TextView b=view.findViewById(R.id.text_big);
        TextView s=view.findViewById(R.id.text_smail);
        final Switch aSwitch=view.findViewById(R.id.switch_s);

        if(ws.item.strbig()!=0){
            b.setText(ws.item.strbig());
        }else {
            b.setVisibility(View.GONE);
        }
        if(ws.item.strsmail()!=0){
            s.setText(ws.item.strsmail());
        }else {
            s.setVisibility(View.GONE);
        }
        try {
            aSwitch.setChecked((Boolean) ws.field.get(o));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        aSwitch.setOnCheckedChangeListener(this);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean s=aSwitch.isChecked();
                aSwitch.setChecked(!s);
            }
        });

        return view;

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(iAction!=null){
            try {
                ws.field.set(o,isChecked);
            } catch (IllegalAccessException e) {
                System.out.println(e.getMessage());
            }
            iAction.action(new ResultUpdate(ws.field.getName(),isChecked));
        }

    }
}
