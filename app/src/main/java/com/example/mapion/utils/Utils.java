package com.example.mapion.utils;

import android.os.Environment;

import androidx.fragment.app.Fragment;

import com.example.mapion.models.MTempFreeRoutes;
import com.example.mapion.ui.home.HomeFragment;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public  class Utils {
    public static List<MTempFreeRoutes> listFreeRoutes;
    public static Fragment CURRENT_FRAGMENT;
    public static HomeFragment HOME_FRAGMENT;
    public static final String patchDir= "";
    public static final int READ_CONNECT_TIMEOUT = 35000;
    public static final String USER_CODE = "0";
    public static final String ERROR="Error";
    public static final String BASE_NAME = "osm_1234.sqlite";

    public static void closerableAll(Closeable... d) throws IOException {
        for (Closeable closeable : d) {
            if (closeable != null) {
                closeable.close();
            }
        }

    }
    public static String responseBodyToString(InputStream stream) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
        StringBuilder result = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }
        return result.toString();

    }

    public static String myTrim(String value, char c) {

        if (c <= 32) return value.trim();

        int len = value.length();
        int st = 0;
        char[] val = value.toCharArray();    /* avoid getfield opcode */

        while ((st < len) && (val[st] == c)) {
            st++;
        }
        while ((st < len) && (val[len - 1] == c)) {
            len--;
        }
        return ((st > 0) || (len < value.length())) ? value.substring(st, len) : value;
    }
}


