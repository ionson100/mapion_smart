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
import com.example.mapion.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.osmdroid.util.BoundingBox;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/*
получение доступных маршрутов с сервера
 */
public class SenderRouteFactory {
    private ProgressDialog mDialog ;
    private Exception  exception;
    private Activity activity;
    protected IAction<List<MTempFreeRoutes>> iAction;
    private TempBound bound;
    private List<MTempFreeRoutes> listResult;
    private TotalSettings totalSettings=TotalSettings.getInstance();

    public void getFreeRoute(BoundingBox box, Activity activity, IAction<List<MTempFreeRoutes>>  iAction){
        bound=TempBound.getInstance(box);
        this.activity = activity;
        this.iAction = iAction;

        mDialog  = new ProgressDialog(activity);
        mDialog.setMessage("Getting data from the server");
        mDialog.setProgress(ProgressDialog.STYLE_SPINNER);

        new ActionTask().execute();
    }
    private class ActionTask extends AsyncTask<IAction, Void, Void> {
        @Override
        protected Void doInBackground(IAction... iActions) {

            String url = totalSettings.url+"/HubApi/GetFreeRoute";
            InputStream stream = null;
            BufferedWriter httpRequestBodyWriter = null;
            OutputStream outputStreamToRequestBody = null;
            OutputStreamWriter dd = null;
            HttpURLConnection connection = null;

            try {
                URL serverUrl = new URL(url);
                connection = (HttpURLConnection) serverUrl.openConnection();
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");

                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                connection.setRequestProperty("Accept-Charset", "UTF-8");
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.155 Safari/537.36");
                connection.setReadTimeout(Utils.READ_CONNECT_TIMEOUT /*milliseconds*/);
                connection.setConnectTimeout(Utils.READ_CONNECT_TIMEOUT /* milliseconds */);
                outputStreamToRequestBody = connection.getOutputStream();
                dd = new OutputStreamWriter(outputStreamToRequestBody,"UTF-8");
                httpRequestBodyWriter = new BufferedWriter(dd);

                httpRequestBodyWriter.flush();

                stream = new ByteArrayInputStream(new Gson().toJson(bound).getBytes());
                int bytesRead;
                byte[] dataBuffer = new byte[1024];
                while ((bytesRead = stream.read(dataBuffer)) != -1) {
                    outputStreamToRequestBody.write(dataBuffer, 0, bytesRead);
                }
                outputStreamToRequestBody.flush();
                httpRequestBodyWriter.flush();
                outputStreamToRequestBody.close();
                httpRequestBodyWriter.close();
                connection.connect();
                int status = connection.getResponseCode();

                if (status == 200) {
                    String sd=Utils.responseBodyToString(connection.getInputStream());
                    Type listType = new TypeToken<List<MTempFreeRoutes>>(){}.getType();
                    listResult=new Gson().fromJson(sd,listType);

                }else {
                    String sd=new String(dataBuffer);
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
                try {
                    Utils.closerableAll(httpRequestBodyWriter, outputStreamToRequestBody, stream, dd);
                } catch (Exception ex) {
                    Log.d(TAG, ex.getMessage());
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
