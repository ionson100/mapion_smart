package com.example.mapion.ui.home;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.view.Gravity;
import android.view.Menu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.mapion.R;
import com.example.mapion.dialogs.DialogFactory;
import com.example.mapion.models.MAnimationStorage;
import com.example.mapion.models.MCurrentRoute;
import com.example.mapion.models.MMediaContent;
import com.example.mapion.models.MMenuRouteStorage;
import com.example.mapion.models.MTempFreeRoutes;
import com.example.mapion.models.TotalSettings;
import com.example.mapion.models.route.MContent;
import com.example.mapion.models.route.MPolygon;
import com.example.mapion.models.route.MRoute;
import com.example.mapion.seder.SenderRouteFactory;
import com.example.mapion.seder.SenderRouteFreeOne;
import com.example.mapion.utils.Utils;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import org.osmdroid.api.IMapController;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.IconOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.Polyline;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/*
Работа с маршрутами
 */
public class WorkerFreeRoute {
    private MapView mMapView;
    private IMapController mMapController;
    private DrawerLayout drawerLayout;
    private Activity mActivity;
    private Locale currentLocale;
    NavigationView navigationView;
    private TotalSettings totalSettings=TotalSettings.getInstance();

    private BroadcastReceiver onDownloadComplete;

    private MMediaContent mMediaContent=new MMediaContent();


    private  ProgressDialog mDialog ;
    public WorkerFreeRoute(MapView mapView,  DrawerLayout layout, Activity activity){

        mMapView = mapView;
        mMapController=mMapView.getController();

        drawerLayout = layout;
        navigationView=drawerLayout.findViewById(R.id.nav_view_free);
        mActivity = activity;
        currentLocale = mActivity.getResources().getConfiguration().locale;
        navigationView.setNavigationItemSelectedListener(item -> {
            if(Utils.listFreeRoutes==null||Utils.listFreeRoutes.size()==0) return false;
            DialogFactory.dialogGiftRoute((AppCompatActivity) activity,Utils.listFreeRoutes.get(item.getItemId()), route -> {
               new SenderRouteFreeOne().getFreeRoute(route.id,activity,mRoute -> {
                   showRouteToMap(mRoute);
               });
            });
            return false;
        });
       MCurrentRoute.getCurrentRoute(activity,mRoute -> {
            if(mRoute!=null){
                workerMapCurrentRoute(mRoute);
            }
        });

        onDownloadComplete = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Fetching the download id received with the broadcast
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                //Checking if the received broadcast is for our enqueued download by matching download id
                if (mMediaContent.idDownload == id) {
                    Toast.makeText(mActivity, "Download Completed", Toast.LENGTH_SHORT).show();
                    if(mDialog!=null)mDialog.dismiss();
                }
            }
        };
        //mActivity.registerReceiver(onDownloadComplete,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        List<MTempFreeRoutes> list=MMenuRouteStorage.getMTempFreeRoutesForMenu();
        InitMenuRoute(list);
    }
    public void UnregisterReceiver(){
        //mActivity.unregisterReceiver(onDownloadComplete);
    }
    public void OpenClose(){
        if(drawerLayout.isDrawerOpen(Gravity.RIGHT)){
            drawerLayout.closeDrawer(Gravity.RIGHT);
        }else{
            innerRequestFreeRoute();
        }
    }
    private void innerRequestFreeRoute(){
                                    BoundingBox boundingBox = mMapView.getProjection().getBoundingBox();
                new SenderRouteFactory().getFreeRoute(boundingBox,mActivity,(s) -> {
                    InitMenuRoute(s);
                    drawerLayout.openDrawer(Gravity.RIGHT);
                    MMenuRouteStorage.saveRouteAsMenu(s);
                });
    }

    private void InitMenuRoute(List<MTempFreeRoutes> s) {
        if(s==null||s.size()==0) return;
        Utils.listFreeRoutes= s;
        Menu m= navigationView.getMenu();
        m.clear();
        String lang=currentLocale.getCountry().toLowerCase();
        int index=0;
        for (MTempFreeRoutes route : s) {
            m.add(0,index,0,route.getNames(lang));
            index++;
        }
    }

    private void showRouteToMap(MRoute route){
        // запрос к базе за маршрутом
        workerMapNewRoute(route);
        drawerLayout.closeDrawer(Gravity.RIGHT);
    }
    private void workerMapNewRoute(MRoute mRoute){

        mMapView.getOverlays().clear();

        setPosition(mRoute);

        addLineRoute(mRoute);
        for (MPolygon polygon : mRoute.polygons) {
            addPolygonItems(polygon);
        }

        addStartOverlayRoute(mRoute);
        MCurrentRoute.saveRouteAsCurrent(mRoute);
        NewRoute();
        //refreshRoute();
    }
    private void workerMapCurrentRoute(MRoute mRoute){

        mMapView.getOverlays().clear();
        // setPosition(mRoute);
        addLineRoute(mRoute);

        for (MPolygon polygon : mRoute.polygons) {
            addPolygonItems(polygon);
        }
        addStartOverlayRoute(mRoute);
        //MCurrentRoute.saveRouteAsCurrent(mRoute);
        refreshRoute();
    }
    void refreshRoute(){
        Intent intent = new Intent(Utils.CONTINUE_ANIMATION);
        intent.putExtra("type",22); // перегрузка маршрута
        mActivity.sendBroadcast(intent);
        Intent intent2 = new Intent(Utils.BR_SERVICE);
        intent2.putExtra("type",22); // перегрузка маршрута
        mActivity.sendBroadcast(intent2);
    }

    void NewRoute(){
        MAnimationStorage.clearStorage(); // очистка кеша проигрывателя
        Intent intent2 = new Intent(Utils.BR_SERVICE);
        intent2.putExtra("type",22); // перегрузка маршрута
        mActivity.sendBroadcast(intent2);
    }
    void addStartOverlayRoute(MRoute mRoute){
        GeoPoint point=new GeoPoint(mRoute.coordinates.coordinates.get(0).get(1),
                mRoute.coordinates.coordinates.get(0).get(0));
        Drawable newMarker = mActivity.getResources().getDrawable(R.drawable.ic_start_route_24);
        IconOverlay olItem = new IconOverlay();

        olItem.set(point,newMarker);



        mMapView.getOverlays().add(olItem);
    }

    void addLineRoute(MRoute mRoute){
        List<GeoPoint> geoPoints = new ArrayList();
        //double aLatitude, final double aLongitude
        for (List<Double> coordinate : mRoute.coordinates.coordinates) {
            geoPoints.add(new GeoPoint(coordinate.get(1),coordinate.get(0)));
        }
        Polyline line = new Polyline();
        line.setColor(Color.GREEN);
        line.setPoints(geoPoints);
        mMapView.getOverlayManager().add(line);
    }

    void addPolygonItems(MPolygon mPolygon){
        List<GeoPoint> geoPoints = new ArrayList<>();
        for (List<List<Double>> cPol : mPolygon.coordinates.coordinates) {

            for (List<Double> doubles : cPol) {
                geoPoints.add(new GeoPoint(doubles.get(1),doubles.get(0)));
            }
        }
        Polygon polygon = new Polygon();
        polygon.setId(String.valueOf(mPolygon.id));
        polygon.setOnClickListener((polygon1, mapView, eventPos) -> {


            for (Overlay overlay : mMapView.getOverlayManager()) {
                if(overlay instanceof Polygon){
                    ((Polygon)overlay).getOutlinePaint().setStrokeWidth(2);
                }
            }
            polygon1.getOutlinePaint().setStrokeWidth(5);
            mapView.invalidate();


            getContentPolygon(mPolygon);
            return false;
        });
        polygon.getOutlinePaint().setStrokeWidth(3);
        // int a, int r, int g, int b
        polygon.getFillPaint().setARGB(10, 50,0,0);
        polygon.getOutlinePaint().setColor(Color.RED);
        polygon.getOutlinePaint().setStrokeWidth(2);
        polygon.setPoints(geoPoints);
        mMapView.getOverlayManager().add(polygon);
    }
    void getContentPolygon(MPolygon mPolygon){
        MContent mContentCore=null;
        if(mPolygon.content==null) {
            DialogFactory.dialogInfo((AppCompatActivity) mActivity,"downloader content",
                    "Polygon has no content",null);
            return;
        }
        for (MContent mContent : mPolygon.content) {
            if(mContent.isDefault){
                mContentCore=mContent;
            }
        }
        if(mContentCore==null){
            DialogFactory.dialogInfo((AppCompatActivity) mActivity,"downloader content",
                    "Default content not found",null);
            return;
        }
        String fileName=mContentCore.id+".mp3";
        mMediaContent.fileName =fileName;
        mMediaContent.idContent=String.valueOf(mContentCore.id);
        mMediaContent.message=mPolygon.getName(mActivity);
        mMediaContent.type=mContentCore.contentType;
        mMediaContent.size=mContentCore.contentSize;
        mMediaContent.idRoute=String.valueOf(mContentCore.idRoute);
        mMediaContent.url=totalSettings.url+"/HubApi/GetFileStream?id="+mContentCore.id;


        Intent intent = new Intent(Utils.BR_SERVICE);
        intent.putExtra("type",1);
        intent.putExtra("data",new Gson().toJson(mMediaContent));
        mActivity.sendBroadcast(intent);
        //showMediaPlayer(mMediaContent);

//        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),fileName);
//        if(file.exists()) {
//            showMediaPlayer(mMediaContent);
//            return;
//        }
        //Toast.makeText(mActivity, fileName, Toast.LENGTH_LONG).show();
        //return;
        /*
        Запуск диалога загрузки файла
         */
//        if(mDialog!=null) mDialog.dismiss();
//        mDialog  = new ProgressDialog(mActivity);
//        mDialog.setMessage("Download file");
//        mDialog.setProgress(ProgressDialog.STYLE_SPINNER);
//        mDialog.show();
//        DownloadManager manager;
//        manager = (DownloadManager) mActivity.getSystemService(Context.DOWNLOAD_SERVICE);
//        String url = totalSettings.url+"/HubApi/GetFileStream?range=true&id="+mContentCore.id;
//        Uri uri = Uri.parse(url);
//        DownloadManager.Request request = new DownloadManager.Request(uri);
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
//
//
//        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,fileName);
//        mMediaContent.idDownload=manager.enqueue(request);

    }
    private void showMediaPlayer(MMediaContent mMediaContent){

//        MediaPlayer  mpintro = MediaPlayer.create(mActivity, Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/"+mMediaContent.fileName));
//        mpintro.setLooping(true);
//        mpintro.start();

        DialogFactory.dialogMediaPlayer((AppCompatActivity) mActivity,
                mMediaContent, o -> {

        });
    }

    void setPosition(MRoute mRoute){
        //"hashurl":"#/center/20.649439356399945,54.3893866629262/zoom/16.4"
        String s=mRoute.hashurl;
        if(s==null||s.contains("#/center/")==false) return;
        int z=s.indexOf("/zoom/");
        if(z==-1) return;

        String ps=s.substring(9,z);
        String[] s1=ps.split(",");
        if(s1.length!=2) return;
        double d1=Double.parseDouble(s1[0]);
        double d2=Double.parseDouble(s1[1]);
        String zoom=s.substring(z+6);
        double zoomD=Double.parseDouble(zoom);

        mMapController.setZoom(zoomD);
        GeoPoint startPoint = new GeoPoint(d2, d1);
        mMapController.setCenter(startPoint);
    }

}
