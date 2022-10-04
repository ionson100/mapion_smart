package com.example.mapion.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.mapion.IAction;
import com.example.mapion.R;
import com.example.mapion.models.MTempFreeRoutes;

import java.util.Locale;

public class DialogGiftRoute extends BaseDialog{

    public static final String TAG = "a15c2b78-92dc-4a78-9425-67349868d10f";
    public IAction<MTempFreeRoutes> iAction;
    public MTempFreeRoutes route;
    public String message;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_gift_route, null);
        Locale currentLocale=getActivity().getResources().getConfiguration().locale;
        String lang=currentLocale.getCountry().toLowerCase();
        ((TextView)v.findViewById(R.id.title_dalog)).setText("Selected Route");
        ((TextView)v.findViewById(R.id.d_name)).setText(this.route.getNames(lang));
        ((TextView)v.findViewById(R.id.d_description)).setText(this.route.getDescription(lang));
        v.findViewById(R.id.bt_close).setOnClickListener(v1 -> {
            dismiss();
        });
        v.findViewById(R.id.bt_show).setOnClickListener(v1 -> {
            dismiss();
            if(iAction!=null){
                iAction.Action(route);
            }
        });
        builder.setView(v);
        AlertDialog dialog=builder.create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

}
