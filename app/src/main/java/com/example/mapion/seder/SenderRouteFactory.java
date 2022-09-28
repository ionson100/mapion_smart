package com.example.mapion.seder;

import android.os.AsyncTask;

import com.example.mapion.IAction;
import com.example.mapion.models.TempBound;
import com.google.gson.Gson;

import org.osmdroid.util.BoundingBox;

import java.util.ArrayList;
import java.util.List;

/*
получение доступных маршрутов с сервера
 */
public class SenderRouteFactory {

    protected IAction<String> iAction;
    private TempBound bound;

    public void getFreeRoute(BoundingBox box, IAction<String>  iAction){

        bound=TempBound.getInstance(box);
        this.iAction = iAction;
        new ActionTask().execute();
    }
    private class ActionTask extends AsyncTask<IAction, Void, Void> {
        @Override
        protected Void doInBackground(IAction... iActions) {

            return null;
        }
        @Override
        protected void onPreExecute() {

        }
        @Override
        protected void onPostExecute(Void aVoid) {
            if(iAction!=null){
                Gson gson = new Gson();
                String string = gson.toJson(bound);
                iAction.Action(string);
            }
        }
    }
}
