package com.example.mapion.ui.home;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.Menu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.mapion.R;
import com.example.mapion.dialogs.DialogFactory;
import com.example.mapion.models.MCurrentRoute;
import com.example.mapion.models.MTempFreeRoutes;
import com.example.mapion.models.route.MPolygon;
import com.example.mapion.models.route.MRoute;
import com.example.mapion.seder.SenderRouteFactory;
import com.example.mapion.seder.SenderRouteFreeOne;
import com.example.mapion.utils.Utils;
import com.google.android.material.navigation.NavigationView;

import org.osmdroid.api.IMapController;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
        MRoute mRoute=MCurrentRoute.getCurrentRoute();
        if(mRoute!=null){
            workerMapCurrentRoute(mRoute);
        }
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
                    Utils.listFreeRoutes=s;
                    Menu m= navigationView.getMenu();
                    m.clear();
                     String lang=currentLocale.getCountry().toLowerCase();
                     int index=0;
                    for (MTempFreeRoutes route : s) {
                        m.add(0,index,0,route.getNames(lang));
                        index++;
                    }
                    drawerLayout.openDrawer(Gravity.RIGHT);
                });
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
        MCurrentRoute.saveRouteAsCurrent(mRoute);
    }
    private void workerMapCurrentRoute(MRoute mRoute){

        mMapView.getOverlays().clear();
        // setPosition(mRoute);
        addLineRoute(mRoute);
        for (MPolygon polygon : mRoute.polygons) {
            addPolygonItems(polygon);
        }
        //MCurrentRoute.saveRouteAsCurrent(mRoute);
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
        polygon.setOnClickListener((polygon1, mapView, eventPos) -> {
            getContentPolygon(mPolygon);
            return false;
        });
        polygon.getOutlinePaint().setStrokeWidth(3);
        // int a, int r, int g, int b
        polygon.getFillPaint().setARGB(10, 50,0,0);
        polygon.getOutlinePaint().setColor(Color.RED);
        polygon.setPoints(geoPoints);
        mMapView.getOverlayManager().add(polygon);
    }
    void getContentPolygon(MPolygon mPolygon){

        Toast.makeText(mActivity, ""+mPolygon.id, Toast.LENGTH_SHORT).show();
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
