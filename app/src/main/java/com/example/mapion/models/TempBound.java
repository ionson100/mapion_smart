package com.example.mapion.models;

import org.osmdroid.util.BoundingBox;

import java.util.ArrayList;
import java.util.List;
/*
Получения границ видимости карты, для получения возможных
маршрутов, которые могут входить в эти границы
 */
public class TempBound {
    public List<Double> P1=new ArrayList<>(2);
    public List<Double> P2=new ArrayList<>(2);
    public List<Double> P3=new ArrayList<>(2);
    public List<Double> P4=new ArrayList<>(2);
    public List<Double> P5=new ArrayList<>(2);
    public static TempBound getInstance(BoundingBox box){
        TempBound bound=new TempBound();

        bound.P1.add(box.getLonWest());
        bound.P1.add(box.getLatNorth());

        bound.P2.add(box.getLonEast());
        bound.P2.add(box.getLatNorth());

        bound.P3.add(box.getLonEast());
        bound.P3.add(box.getLatSouth());

        bound.P4.add(box.getLonWest());
        bound.P4.add(box.getLatSouth());

        bound.P5.add(box.getLonWest());
        bound.P5.add(box.getLatNorth());
        return bound;
    }
}
