package com.example.mapion.settings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import androidx.fragment.app.DialogFragment;

import com.example.mapion.R;

import java.util.List;

public class DialogRadio extends DialogFragment {
    public IActionSettings iActionSettings;
    public Object settings;
    public WrapperSettings ws;

    private Object selectObject;


    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_radio, null);

        ((TextView)v.findViewById(R.id.title_dalog)).setText(this.ws.item.strbig());

        Class aClass=ws.item.TYPE_SETTINGS_LIST();
        ISettingsList s=null;
        if(aClass!=int.class){
            try {
                s= (ISettingsList) aClass.newInstance();
            } catch (Exception e) {
                System.out.println("**************** "+e.getMessage());
            }
        }
        if(s!=null){
            Object o=null;
            try {
                 o=ws.field.get(settings);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            final List<Pair<String,Object>> pairs=s.getPairList();
            RadioGroup radioGroup=v.findViewById(R.id.radio_group_s);

            int i=0;
            for (Pair<String, Object> pair : pairs) {
                RadioButton button = (RadioButton) LayoutInflater.from(getActivity()).inflate(R.layout.item_radio, null);
                button.setText(pair.first);
                button.setTag(pair.second);
                if(o!=null&&pair.second.toString().equals(o.toString())){
                    button.setChecked(true);
                }
                button.setId(i++);
                radioGroup.addView(button);
            }
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
            {
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                   selectObject=pairs.get(checkedId).second;
                }
            });
        }



        v.findViewById(R.id.bt_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        v.findViewById(R.id.bt_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    ws.field.set(settings,selectObject);

                }catch (Exception s){
                    Toast.makeText(getActivity(), s.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                iActionSettings.action(new ResultUpdate(ws.field.getName(),selectObject));
                dismiss();
            }
        });

        builder.setView(v);
        AlertDialog dialog=builder.create();
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }
}
