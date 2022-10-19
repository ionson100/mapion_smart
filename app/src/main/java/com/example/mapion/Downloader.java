package com.example.mapion;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;

import com.example.mapion.models.MDownloadFileData;
import com.example.mapion.models.MMediaContent;
import com.example.mapion.orm2.Configure;

import java.io.File;
import java.util.List;

public class Downloader extends BroadcastReceiver {
    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
        List<MDownloadFileData> list=Configure.getSession().getList(MDownloadFileData.class," id_download = "+id);
        if(list.size()==0) return;
        MDownloadFileData fileData=list.get(0);
        Main2Activity main2Activity=new Main2Activity(context);
        try {
            main2Activity.SaveFileCore(fileData.nameTemp, fileData.nameCore, fileData.idRoute,fileData.idContent);
            Configure.getSession().delete(fileData);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public Downloader(Context context){
        mContext = context;
        context.registerReceiver(this,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }
    public void dispose(){
        mContext.unregisterReceiver(this);
    }
    public void DownloadFile(MMediaContent mMediaContent){
       // List<MDownloadFileData> list= Configure.getSession().getList(MDownloadFileData.class,null);
      //  if(list.size()!=0) return;
        MDownloadFileData fileData=new MDownloadFileData();
        fileData.idContent=mMediaContent.idContent;
        fileData.idRoute=mMediaContent.idRoute;
        fileData.nameTemp=mMediaContent.idContent;
        fileData.nameCore=mMediaContent.idContent+".mp3";
        fileData.size=mMediaContent.size;
        fileData.type=mMediaContent.type;


        DownloadManager manager;
        manager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        String url = mMediaContent.url;
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);


        // request.setDestinationInExternalFilesDir(mContext,"temp",fileData.nameTemp);
        request.setDestinationInExternalPublicDir(mContext.getApplicationInfo().dataDir+
                File.separator+"temp"+File.separator,fileData.nameTemp);
        fileData.idDownload=manager.enqueue(request);
        Configure.getSession().insert(fileData);

    }
}
