package com.example.mapion.ui.home;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mapion.BuildConfig;
import com.example.mapion.R;
import com.example.mapion.databinding.FragmentHomeBinding;
import com.example.mapion.seder.SenderRouteFactory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;

public class HomeFragment extends Fragment {

    private MapView map = null;
    private IMapController mapController;
    private Marker startMarker;
    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        map = (MapView) root.findViewById(R.id.map);

        startMarker = new Marker(map);

        map.setTileSource(TileSourceFactory.MAPNIK);


        map.setMultiTouchControls(true);
        mapController = map.getController();
        mapController.setZoom(10.0);
        GeoPoint startPoint = new GeoPoint(48.8583, 2.2944);
        mapController.setCenter(startPoint);
        startMarker.setPosition(startPoint);

        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        map.getOverlays().add(startMarker);


        // add compass to map
        CompassOverlay compassOverlay = new CompassOverlay(getActivity(),
                new InternalCompassOrientationProvider(getActivity()), map);
        compassOverlay.enableCompass();
        map.getOverlays().add(compassOverlay);
        //startMarker.setPosition(startPoint);

        //startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        //map.getOverlays().add(startMarker);

        FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BoundingBox boundingBox = map.getProjection().getBoundingBox();
                new SenderRouteFactory().getFreeRoute(boundingBox,(s) -> {
                    Snackbar.make(view, s, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                });


            }
        });


        return root;
    }
    @Override
    public void onResume() {
        super.onResume();

        if (map != null) {
            map.onResume();
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        if (map != null) {
            map.onPause();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}