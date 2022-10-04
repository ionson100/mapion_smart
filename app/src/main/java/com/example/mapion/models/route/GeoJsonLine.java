package com.example.mapion.models.route;

import com.example.mapion.utils.Utils;

import java.util.List;

public class GeoJsonLine {

    public String type ;
    public List<List<Double>> coordinates ;
    public String toString()
    {
        StringBuilder builder = new StringBuilder(this.type+" (");
        for (List<Double> p : coordinates) {
            builder.append(""+p.get(0).toString().replace(",", ".")+" "+
                    p.get(1).toString().replace(",", ".")+",");
        }
        String res = Utils.myTrim(builder.toString(),',')+ ")";
        return res;
    }
}
