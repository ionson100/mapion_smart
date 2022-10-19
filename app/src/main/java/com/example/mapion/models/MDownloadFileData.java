package com.example.mapion.models;

import com.example.mapion.orm2.Column;
import com.example.mapion.orm2.PrimaryKey;
import com.example.mapion.orm2.Table;
@Table(MDownloadFileData.TABLE_NAME)
public class MDownloadFileData {
    public static final String TABLE_NAME="download_data";
    @PrimaryKey("_id")
    public  int _id;
    @Column("id_content")
    public  String idContent;
    @Column("id_route")
    public  String idRoute;
    @Column("id_size")
    public long size;
    @Column("id_download")
    public long idDownload;
    @Column("name_temp")
    public String nameTemp;
    @Column("name_core")
    public String nameCore;
    @Column("type")
    public String type;
}




