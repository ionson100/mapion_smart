package com.example.mapion.downloader;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.example.mapion.IAction;
import com.example.mapion.utils.Utils;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

class InnerWorker extends AsyncTask<Void, Integer, Exception> {
    private Exception exception;
    private IAction<Object> iAction;
    private DownloaderTranslate mTanslate;

    public InnerWorker(IAction<Object> iAction, DownloaderTranslate translate){

        this.iAction = iAction;
        mTanslate = translate;
    }



    @Override
    protected Exception doInBackground(Void... t) {

        int count;
        try {
            URL url = new URL(mTanslate.url);
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(Utils.READ_CONNECT_TIMEOUT);
            connection.connect();

            // this will be useful so that you can show a tipical 0-100%
            // progress bar
            int lenghtOfFile = connection.getContentLength();


            // download the file
            InputStream input = new BufferedInputStream(url.openStream(),
                    8192);

            // Output stream
            OutputStream output = new FileOutputStream(Environment.
                    getExternalStorageDirectory().toString()
                    + "/data/downloadedfile.kml");

            byte data[] = new byte[1024];
            while ((count = input.read(data)) != -1) {
                output.write(data, 0, count);
            }
            output.flush();
            output.close();
            input.close();
        } catch (Exception e) {
            exception=e;
            Log.e("Error: ", e.getMessage());
        }
        return exception;
    }

    @Override
    protected void onPostExecute(Exception e) {
        iAction.Action(e);
    }

}
