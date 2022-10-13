package com.example.mapion.ui.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.mapion.IAction;
import com.example.mapion.R;
import com.example.mapion.models.MAnimationStorage;
import com.example.mapion.models.MCurrentRoute;
import com.example.mapion.models.MMediaContent;
import com.example.mapion.models.TotalSettings;
import com.example.mapion.models.route.MContent;
import com.example.mapion.models.route.MPolygon;
import com.example.mapion.models.route.MRoute;
import com.example.mapion.utils.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;


import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class Animation {
    private  AtomicBoolean isRun= new AtomicBoolean(true);
    private MapView mMap;
    private View mRoot;
    private FragmentActivity mActivity;
    Polyline currentLine=null;
    private  Marker startMarker;
    private List<GeoPoint> list;
    private int index=1;//
    private String lastContainsIdPolygon="";
    Map<Polygon,com.snatik.polygon.Polygon> dictionaryPolygon=new Hashtable<>();
    private MRoute mRoute;
    private TotalSettings totalSettings=TotalSettings.getInstance();
    private BroadcastReceiver receiverContinueAnimation;
    FloatingActionButton buttonStartStop;

    //List<com.snatik.polygon.Polygon> listPolygon=new ArrayList<>();
    public Animation(MapView mMap, View root, FragmentActivity activity) {

        this.mMap = mMap;
        mRoot = root;
        mActivity = activity;
        startMarker = new Marker(mMap);

        buttonStartStop=mRoot.findViewById(R.id.fab_animation);
        buttonStartStop.setOnClickListener(v -> {

            if(currentLine==null){
                extractedStart(mMap, activity,null);
            }else {

                boolean va=isRun.get();
                setRun(!va);
                if(isRun.get()==true){
                    InnerRun();

                }
            }

        });
        receiverContinueAnimation=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int t= intent.getIntExtra("type",-1);
                if(t==312312){
                    buttonStartStop.callOnClick();// продолжаем анимация
                }
                if(t==22){
                    //index=1;
                    currentLine=null;// перегрузка маршрута

                    MAnimationStorage storage=MAnimationStorage.getInstance();
                    if(storage!=null){
                        extractedStart(mMap,activity,storage);
                    }
                    //dictionaryPolygon.clear();
                    //setRun(false);
                }
            }
        };
        mActivity.registerReceiver(receiverContinueAnimation,new IntentFilter(Utils.CONTINUE_ANIMATION));

    }

    private void extractedStart(MapView mMap, FragmentActivity activity,MAnimationStorage storage) {
        MCurrentRoute.getCurrentRoute(activity, route -> {
            this.mRoute=route;
        });
        dictionaryPolygon.clear();
        for (Overlay overlay : mMap.getOverlayManager()) {
            if(overlay instanceof Polyline){
                currentLine= (Polyline) overlay;
            }
            if(overlay instanceof Polygon){

                List<GeoPoint> geoPoints=((Polygon)overlay).getActualPoints();
                com.snatik.polygon.Polygon.Builder polygon1=com.snatik.polygon.Polygon.Builder();
                for (GeoPoint geoPoint : geoPoints) {
                    polygon1.addVertex(new com.snatik.polygon.Point(
                            geoPoint.getLongitude(),
                            geoPoint.getLatitude()));
                }
               //listPolygon.add(polygon1.build());
                dictionaryPolygon.put(((Polygon)overlay),polygon1.build());
            }
        }
        if(currentLine== null) return;
        if(storage==null){
            index=1;
            setRun(true);
        }else{
            index= storage.index;
            setRun(true);
        }


        lastContainsIdPolygon="";
        run(storage);
    }


    private void  run(MAnimationStorage storage){
       list= currentLine.getActualPoints();

       if(storage==null){
           startMarker.setPosition(list.get(0));
       }else{
           //GeoPoint(final double aLatitude, final double aLongitude) {
           startMarker.setPosition(new GeoPoint(storage.markerLatitude,storage.marketLongitude));
       }

        startMarker.setVisible(true);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        Drawable newMarker = mActivity.getResources().getDrawable(R.drawable.ic_marcer_animation_24);
        startMarker.setIcon(newMarker);
        mMap.getOverlayManager().add(startMarker);
        buttonStartStop.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.ic_pause_24));
        InnerRun();
    }

    private  void setRun(boolean v){
        isRun.set(v);
        if(v){
            buttonStartStop.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.ic_pause_24));
        }else{
            buttonStartStop.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.ic_start_24));
        }
    }

    private void  InnerRun(){
        animateMarker(startMarker,index,o -> {
            index=o+1;
            InnerRun();
        });
    }

    public void animateMarker(final Marker marker, int index,IAction<Integer> iAction) {
        if(list.size()==index) {
            this.currentLine=null;
            marker.setVisible(false);
            buttonStartStop.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.ic_start_24));
            for (Map.Entry<Polygon, com.snatik.polygon.Polygon> polygonEntry : dictionaryPolygon.entrySet()) {
                polygonEntry.getKey().getOutlinePaint().setStrokeWidth(2);
            }
            return;
        }
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();

        final GeoPoint toPosition=list.get(index);
        final long duration = 4000;



        final IGeoPoint startGeoPoint = marker.getPosition();
        final Interpolator interpolator = new LinearInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(isRun.get()==false){

                }else {

                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = interpolator.getInterpolation((float) elapsed / duration);
                    double lng = t * toPosition.getLongitude() + (1 - t) * startGeoPoint.getLongitude();
                    double lat = t * toPosition.getLatitude() + (1 - t) * startGeoPoint.getLatitude();
                    marker.setPosition(new GeoPoint(lat, lng));

                    if(checkContains(lng,lat)){
                        setRun(false);
                    }

                    if (t < 1.0) {
                        handler.postDelayed(this, 15);
                    }else {

                        iAction.Action(index);
                    }
                    mMap.postInvalidate();
                }

            }
        });
    }
    public boolean checkContains(double longitude,double latitude){
        com.snatik.polygon.Point   point=new com.snatik.polygon.Point(longitude,latitude);
        for (Map.Entry<Polygon, com.snatik.polygon.Polygon> entry : dictionaryPolygon.entrySet()) {
            if(entry.getValue().contains(point)==true&&lastContainsIdPolygon.equals(entry.getKey().getId())==false){
                lastContainsIdPolygon=entry.getKey().getId();
                if(mRoute==null) {
                    return false;
                }
                for (Map.Entry<Polygon, com.snatik.polygon.Polygon> polygonEntry : dictionaryPolygon.entrySet()) {
                    polygonEntry.getKey().getOutlinePaint().setStrokeWidth(2);
                }
                MPolygon mPolygon=null;
                if(mRoute.polygons==null) return false;
                for (MPolygon polygon : mRoute.polygons) {
                    if(String.valueOf(polygon.id).equals(entry.getKey().getId())){
                        mPolygon=polygon;
                    }
                }
                if(mPolygon==null){
                    return false;
                }
                if(mPolygon.content==null) return false;
                MContent mContent=null;
                for (MContent content : mPolygon.content) {
                    if(content.isDefault){
                        mContent=content;
                    }
                }
                if(mContent==null) return  false;

                {
                    MMediaContent mMediaContent=new MMediaContent();
                    String fileName=mContent.id+".mp3";
                    mMediaContent.fileName =fileName;
                    mMediaContent.message=mPolygon.getName(mActivity);
                    mMediaContent.type=mContent.contentType;
                    mMediaContent.size=mContent.contentSize;
                    mMediaContent.url=totalSettings.url+"/HubApi/GetFileStream?id="+mContent.id;


                    Intent intent = new Intent(Utils.BR_SERVICE);
                    intent.putExtra("type",1001);// воспроизведение с санимации
                    intent.putExtra("data",new Gson().toJson(mMediaContent));
                    mActivity.sendBroadcast(intent);
                }
                entry.getKey().getOutlinePaint().setStrokeWidth(5);

                return true;
            }
        }

        return  false;
    }
    public void destroy() {
        mActivity.unregisterReceiver(receiverContinueAnimation);
        if(currentLine!=null){
            MAnimationStorage storage=new MAnimationStorage();
            storage.index=index;
            storage.markerLatitude=startMarker.getPosition().getLatitude();
            storage.marketLongitude=startMarker.getPosition().getLongitude();
            storage.isRun=isRun.get();
            MAnimationStorage.save(storage);
        }else{
            MAnimationStorage.clearStorage();
        }
    }
}
