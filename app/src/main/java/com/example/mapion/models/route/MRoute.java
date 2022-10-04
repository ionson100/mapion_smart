package com.example.mapion.models.route;

import java.util.List;
import java.util.UUID;

public class MRoute  {

    public UUID id;
    public Double price;
    public String hashurl;
    public GeoJsonLine coordinates ;

    public List<MDescription> mDescription ;

    public List<MPolygon> polygons ;
}
