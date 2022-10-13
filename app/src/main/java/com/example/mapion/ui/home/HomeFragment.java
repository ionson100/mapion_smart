package com.example.mapion.ui.home;

import android.location.LocationManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.mapion.BuildConfig;
import com.example.mapion.R;
import com.example.mapion.databinding.FragmentHomeBinding;
import com.example.mapion.databinding.LeftNavigationFreeBinding;
import com.example.mapion.dialogs.DialogFactory;
import com.example.mapion.models.MStorageMapView;
import com.example.mapion.seder.SenderRouteFactory;
import com.example.mapion.utils.IOnBackPressed;
import com.example.mapion.utils.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class HomeFragment extends Fragment implements IOnBackPressed {

    boolean isLocation=false;
    private  MyLocationNewOverlay locationOverlay;

    private MapView mMap = null;
    private IMapController mMapController;

    private FragmentHomeBinding binding;
    private MStorageMapView storageMapView=MStorageMapView.getInstance();
    DrawerLayout drawerLayout;
    WorkerFreeRoute workerFreeRoute;
    private Animation mAnimation;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        Utils.CURRENT_FRAGMENT=this;
        Utils.HOME_FRAGMENT=this;
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        mMap = binding.map;
        drawerLayout=binding.myHomeDraver;
        mAnimation=new Animation(mMap,root,getActivity());


        mMap.setTileSource(TileSourceFactory.MAPNIK);


        mMap.setMultiTouchControls(true);
        mMapController = mMap.getController();
        mMapController.setZoom(storageMapView.zoom);
        GeoPoint startPoint = new GeoPoint(storageMapView.lat, storageMapView.lon);
        mMapController.setCenter(startPoint);


        // add compass to map
        CompassOverlay compassOverlay = new CompassOverlay(getActivity(),
                new InternalCompassOrientationProvider(getActivity()), mMap);
        compassOverlay.enableCompass();
        mMap.getOverlays().add(compassOverlay);

        GpsMyLocationProvider prov= new GpsMyLocationProvider(getContext());
        prov.addLocationSource(LocationManager.NETWORK_PROVIDER);
        prov.addLocationSource(LocationManager.GPS_PROVIDER);
        locationOverlay = new MyLocationNewOverlay(prov, mMap);
        locationOverlay.disableMyLocation();

        FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            private MyLocationNewOverlay mLocationOverlay;

            @Override
            public void onClick(View view) {

                if(isLocation==false){
                    locationOverlay.enableMyLocation();
                    mMap.getOverlayManager().add(locationOverlay);
                    mMap.invalidate();
                    isLocation=true;
                    fab.setImageDrawable(getActivity().getDrawable(R.drawable.ic_baseline_my_location_green_24));

                }else{
                    locationOverlay.disableMyLocation();
                    mMap.getOverlayManager().remove(locationOverlay);
                    mMap.invalidate();
                    isLocation=false;
                    fab.setImageDrawable(getActivity().getDrawable(R.drawable.ic_baseline_my_location_24));
                }


//                if(mGpsTracker==null){
//                    mGpsTracker=new GPSTracker(getContext());
//                    if(mGpsTracker.canGetLocation==false){
//                        mGpsTracker.showSettingsAlert();
//                    }else{
//                        GeoPoint startPoint = new GeoPoint(mGpsTracker.getLatitude(), mGpsTracker.getLongitude());
//                        mMapController.setCenter(startPoint);
//                        mGpsTracker.onLocationChangedCore(location -> {
//                            mMapController.setCenter(new GeoPoint(location.getLatitude(), location.getLongitude()));
//                        });
//                        Snackbar.make(view, "Open Location", Snackbar.LENGTH_LONG)
//                                .setAction("Action", null).show();
//                    }
//                }else{
//                    mGpsTracker.stopUsingGPS();
//                    mGpsTracker=null;
//                    Snackbar.make(view, "Close Location", Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();
//                }

            }
        });
        FloatingActionButton fabFree = root.findViewById(R.id.fabFree);
        fabFree.setOnClickListener(v -> {

            workerFreeRoute.OpenClose();

        });


        workerFreeRoute=new WorkerFreeRoute(mMap,drawerLayout,getActivity());
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mMap != null) {
            mMap.onResume();
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        if (mMap != null) {
            mMap.onPause();
        }
    }

    @Override
    public void onStop() {
        storageMapView.lat=  mMap.getMapCenter().getLatitude();
        storageMapView.lon= mMap.getMapCenter().getLongitude();
        storageMapView.zoom= mMap.getZoomLevelDouble();
        storageMapView.save();
        workerFreeRoute.UnregisterReceiver();
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        mAnimation.destroy();
    }

    @Override
    public boolean onBackPressed() {
        if(drawerLayout.isDrawerOpen(Gravity.RIGHT)){
            drawerLayout.closeDrawer(Gravity.RIGHT);
            return false;
        }
        return true; // можно закрывать
    }

    @Override
    public void clearRoute() {
        mMap.getOverlays().clear();
    }
}