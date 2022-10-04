package com.example.mapion.models.route;

import com.example.mapion.utils.Utils;

import java.util.List;

public class GeoJsonPolygon {
    public String type;
    public List<List<List<Double>>> coordinates;

    public  String toString()
    {
        StringBuilder builder = new StringBuilder("POLYGON((");

        for (List<List<Double>> coordinates : coordinates) {
            for (List<Double> doubles : coordinates) {

                builder.append(doubles.get(0).toString().replace(",", ".") +" "+
                                doubles.get(1).toString().replace(",", ".")+",");
            }
        }
        String res = Utils.myTrim(builder.toString(),',') + "))";
        return res;
    }
}
