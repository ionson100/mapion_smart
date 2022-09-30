package com.example.mapion.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.mapion.IAction;
import com.example.mapion.R;

public class DialogYesNo extends BaseDialog {
    public static final String TAG = "a15c2b78-92dc-4a78-9425-67259863d10f";
    public IAction<Object> iAction;
    public String message;
    public String title;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_yes_no, null);

        ((TextView)v.findViewById(R.id.title_dalog)).setText(String.valueOf(this.title));

        ((TextView)v.findViewById(R.id.d_message)).setText(String.valueOf(this.message));

        v.findViewById(R.id.bt_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        v.findViewById(R.id.bt_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(iAction!=null){
                    iAction.Action(null);
                }
                dismiss();
            }
        });


        builder.setView(v);
        AlertDialog dialog=builder.create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }


}
