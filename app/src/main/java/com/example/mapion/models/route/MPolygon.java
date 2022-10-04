package com.example.mapion.models.route;

import java.util.List;
import java.util.UUID;

public class MPolygon {
    public UUID id;
    public List<MDescription> mDescription;
    public GeoJsonPolygon coordinates ;
    public List<MContent> content ;
}
