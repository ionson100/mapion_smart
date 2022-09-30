package com.example.mapion.settings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;


import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.example.mapion.R;


public class DialogEditText extends DialogFragment {

    public IActionSettings iActionSettings;

    public WrapperSettings ws;
    private EditText editText;
    public Object settings;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_edit_text, null);

        ((TextView)v.findViewById(R.id.title_dalog)).setText(this.ws.item.strbig());
        try {
            editText=v.findViewById(R.id.edit_text);
            editText.setInputType(ws.item.EDIT_INPUT_TYPE());
            Object s=ws.field.get(settings);
            if(s!=null){
                editText.setText(String.valueOf(s));
            }

        } catch (IllegalAccessException e) {
            e.printStackTrace();
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
                if(editText.getText().toString().trim().length()==0){
                    editText.setError("Пустое поле!");
                    return;
                }
                try{
                    if(ws.field.getType()==int.class){
                        ws.field.set(settings,Integer.parseInt(editText.getText().toString()));
                    }else  if(ws.field.getType()==double.class){
                        ws.field.set(settings,Double.parseDouble(editText.getText().toString()));
                    }else if(ws.field.getType()==float.class){
                        ws.field.set(settings,Float.parseFloat(editText.getText().toString()));
                    } else {
                        ws.field.set(settings,editText.getText().toString());
                    }


                    iActionSettings.action(new ResultUpdate(ws.field.getName(),editText.getText().toString()));
                    dismiss();
                }catch (Exception e){
                    Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_LONG).show();
                }


            }
        });

        builder.setView(v);


        AlertDialog dialog=builder.create();
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }
}
