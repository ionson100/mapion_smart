package com.example.mapion.seder;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mapion.IAction;
import com.example.mapion.dialogs.DialogFactory;
import com.example.mapion.models.MTempFreeRoutes;
import com.example.mapion.models.TempBound;
import com.example.mapion.models.TotalSettings;
import com.example.mapion.models.route.MRoute;
import com.example.mapion.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.osmdroid.util.BoundingBox;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;

public class SenderRouteFreeOne {
    private ProgressDialog mDialog ;
    private Exception  exception;
    private UUID idRoute;
    private Activity activity;
    protected IAction<MRoute> iAction;

    private MRoute listResult;
    private TotalSettings totalSettings=TotalSettings.getInstance();

    public void getFreeRoute(UUID idRoute,Activity activity, IAction<MRoute>  iAction){
        this.idRoute = idRoute;

        this.activity = activity;
        this.iAction = iAction;

        mDialog  = new ProgressDialog(activity);
        mDialog.setMessage("Getting data from the server");
        mDialog.setProgress(ProgressDialog.STYLE_SPINNER);

        new ActionTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, iAction);
    }
    private class ActionTask extends AsyncTask<IAction, Void, Void> {
        @Override
        protected Void doInBackground(IAction... iActions) {

            String url = totalSettings.url+"/HubApi/GetFreeRouteOne?id="+idRoute;




            HttpURLConnection connection = null;

            try {
                URL serverUrl = new URL(url);
                connection = (HttpURLConnection) serverUrl.openConnection();
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");

                connection.setInstanceFollowRedirects(false);
                connection.setReadTimeout(Utils.READ_CONNECT_TIMEOUT /*milliseconds*/);
                connection.setConnectTimeout(Utils.READ_CONNECT_TIMEOUT /* milliseconds */);
                connection.setRequestMethod("GET");
                connection.connect();

                int status = connection.getResponseCode();

                if (status == 200) {
                    String sd=Utils.responseBodyToString(connection.getInputStream());

                    listResult=new Gson().fromJson(sd, MRoute.class);

                }else {
                    String sd=new String("error");
                    String s = String.format("Request free route %d %s", status,sd);
                    throw new RuntimeException(s);
                }

            } catch (Exception ex) {
                exception = ex;
                Log.d(TAG, ex.getMessage());
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }

            return null;
        }
        @Override
        protected void onPreExecute() {
            mDialog.show();
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            try{
                if(exception!=null){
                    DialogFactory.dialogInfo((AppCompatActivity) activity,"error",exception.getMessage(),null);
                }else{
                    if(iAction!=null){
                        iAction.Action(listResult);
                    }
                }

            }finally {
                mDialog.dismiss();
            }

        }
    }
}
