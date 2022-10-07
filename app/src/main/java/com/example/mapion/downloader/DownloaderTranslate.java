package com.example.mapion.downloader;

import com.example.mapion.IAction;

import java.util.UUID;

public class DownloaderTranslate{
    public String url;
    public UUID idContext;
    public int rangeStart;
    public int rangeFinish;
    public IAction<Exception> callback;

}