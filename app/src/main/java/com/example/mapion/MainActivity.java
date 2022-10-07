package com.example.mapion;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Toast;

import com.example.mapion.models.MCurrentRoute;
import com.example.mapion.ui.home.HomeFragment;
import com.example.mapion.utils.IOnBackPressed;
import com.example.mapion.utils.Starter;
import com.example.mapion.utils.Utils;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mapion.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    private  DrawerLayout drawer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Starter.start(getBaseContext());
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        setSupportActionBar(binding.appBarMain.toolbar);
//        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        drawer = binding.drawerLayout;
        binding.appBarMain.getRoot().findViewById(R.id.fab_menu).setOnClickListener(v -> {
            if(drawer.isDrawerOpen(Gravity.LEFT)){// закрываем основное меню
                drawer.closeDrawer(Gravity.LEFT);

            }else{
                drawer.openDrawer(Gravity.LEFT);// open left menu
            }
        });
        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                MenuItem menuItem=binding.navView.getMenu().findItem(R.id.nav_close_current_route);
                if(MCurrentRoute.getAnyRoute()>0){
                    menuItem.setVisible(true);
                }else {
                    menuItem.setVisible(false);
                }
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {


            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,R.id.nav_close_current_route)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        requestPermissionsIfNecessary(new String[]{
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE});




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            permissionsToRequest.add(permissions[i]);
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override public void onBackPressed() {
        if(drawer.isDrawerOpen(Gravity.LEFT)){// закрываем основное меню
            drawer.closeDrawer(Gravity.LEFT);
            return;
        }
        if(Utils.CURRENT_FRAGMENT!=null){
            boolean sd=Utils.CURRENT_FRAGMENT instanceof IOnBackPressed;
            if (sd){ // если открта карта
                boolean d=((IOnBackPressed) Utils.CURRENT_FRAGMENT).onBackPressed();
                if(!d){ // если открыто меню маршрутов, закрываем меню маршрутов
                   return;
                }
            }else {// если открыта не карта, открываем карту.
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.nav_host_fragment_content_main,Utils.HOME_FRAGMENT);
                ft.commit();
            }
        }
        super.onBackPressed();

    }

    public void onClearRoute(MenuItem item) {
        MCurrentRoute.clear();
        boolean sd=Utils.CURRENT_FRAGMENT instanceof IOnBackPressed;
        if (sd){ // очищаем маршрут текущий
            ((IOnBackPressed) Utils.CURRENT_FRAGMENT).clearRoute();

        }
        Toast.makeText(this, "Ok Route Out ", Toast.LENGTH_SHORT).show();
        drawer.closeDrawer(Gravity.LEFT);

    }
}