package com.example.mapion.dialogs;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mapion.IAction;
import com.example.mapion.models.MTempFreeRoutes;

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
    public static void dialogGiftRoute(AppCompatActivity activity, MTempFreeRoutes route, IAction<MTempFreeRoutes> iAction){
        DialogGiftRoute dialog=new DialogGiftRoute();
        dialog.iAction=iAction;
        dialog.route=route;
        dialog.show(activity.getSupportFragmentManager(),DialogGiftRoute.TAG);
    }
    public static DialogMediaPlayer dialogMediaPlayer(AppCompatActivity activity,String message,String pathFile,IAction<Object>  iAction){
        DialogMediaPlayer dialogMediaPlayer = new DialogMediaPlayer();
        dialogMediaPlayer.iAction=iAction;
        dialogMediaPlayer.message=message;
        dialogMediaPlayer.fileName=pathFile;
        dialogMediaPlayer.mActivity=activity;
        dialogMediaPlayer.show(activity.getSupportFragmentManager(),DialogMediaPlayer.TAG);
        return dialogMediaPlayer;
    }
}
