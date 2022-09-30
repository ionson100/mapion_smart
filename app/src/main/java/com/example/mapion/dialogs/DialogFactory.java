package com.example.mapion.dialogs;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mapion.IAction;

public class DialogFactory {

    public static void dialogYsNo(AppCompatActivity activity, String title, String message, IAction iAction){
        DialogYesNo dialog=new DialogYesNo();
        dialog.iAction=iAction;
        dialog.title=title;
        dialog.message=message;
        dialog.show(activity.getSupportFragmentManager(),DialogYesNo.TAG);
    }

    public static void dialogInfo(AppCompatActivity activity, String title, String message, IAction<Object> iAction){
        DialogInfo dialog=new DialogInfo();
        dialog.iAction=iAction;
        dialog.title=title;
        dialog.message=message;
        dialog.show(activity.getSupportFragmentManager(),DialogInfo.TAG);
    }
}
