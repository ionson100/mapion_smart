package com.example.mapion.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class MTempFreeRoutes {
    public UUID id ;
    public List<MInnerDescription> names=new ArrayList<>();
    public List<MInnerDescription> description=new ArrayList<>() ;
    public int rating ;
    public String getNames(String lang){
        if(names.size()==0){
            return "no data name";
        }
        String res=null;
        String resEn=null;
        for (MInnerDescription name : names) {
            if(name.lang.equals(lang)){
                res=name.text;
            }
            if(name.lang.equals("en")){
                resEn=name.text;
            }
        }
        if(res==null){
            if(resEn!=null){
                res=resEn;
            }else{
                res=names.get(0).text;
            }

        }
        return res;
    }

    public String getDescription(String lang){
        if(description.size()==0){
            return "no data description";
        }
        String res=null;
        String resEn=null;
        for (MInnerDescription description : description) {
            if(description.lang.equals(lang)){
                res=description.text;
            }
            if(description.lang.equals("en")){
                resEn=description.text;
            }
        }
        if(res==null){
            if(resEn!=null){
                res=resEn;
            }else{
                res=description.get(0).text;
            }

        }
        return res;
    }
}
