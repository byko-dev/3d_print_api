package com.byko.api_3d_printing.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    public static String getCurrentDate(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date);
    }
}
