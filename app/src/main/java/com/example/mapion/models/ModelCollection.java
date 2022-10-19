package com.example.mapion.models;

import java.util.ArrayList;
import java.util.List;

public class ModelCollection {

    public static final List<Class> classes = new ArrayList<>();

    static {
        classes.add(MStorageMapView.class);
        classes.add(TotalSettings.class);
        classes.add(MCurrentRoute.class);
        classes.add(MMenuRouteStorage.class);
        classes.add(MAnimationStorage.class);
        classes.add(MDownloadFileData.class);
        classes.add(MContentFile.class);
    }
}
