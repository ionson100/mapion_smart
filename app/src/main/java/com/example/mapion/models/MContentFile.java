package com.example.mapion.models;

import com.example.mapion.orm2.Column;
import com.example.mapion.orm2.Configure;
import com.example.mapion.orm2.PrimaryKey;
import com.example.mapion.orm2.Table;

import java.util.List;

@Table(MContentFile.TABLE_NAME)
public class MContentFile {
    public static final String TABLE_NAME="context_file";
    @PrimaryKey("_id")
    public  int _id;
    @Column("id_content")
    public String idContent;

    @Column("file_name")
    public String fileName;

    public static int Contain(String idContext){
         int d=(int)Configure.getSession().executeScalar("select count(*) from "+ MContentFile.TABLE_NAME +
                " where id_content = '"+idContext+"'");
        return d;
    }

    public static String getFileName(String idContext){

        List<MContentFile> list= Configure.getSession().getList(MContentFile.class," id_content ='"+idContext+"'");
        if(list.size()==0) return null;
        return list.get(0).fileName;
    }
    public static void saveFile(String idContext,String fileName){
        if(Contain(idContext)==0){
            MContentFile mContextFile=new MContentFile();
            mContextFile.fileName=fileName;
            mContextFile.idContent =idContext;
            Configure.getSession().insert(mContextFile);
        }

    }
}
