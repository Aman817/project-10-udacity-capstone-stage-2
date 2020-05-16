package com.robertkiszelirk.newsselector.data.model;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class GetJsonData {

    public static String getJson(URL url){

        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            InputStream inputStream = null;
            try {
                if (urlConnection != null) {
                    inputStream = urlConnection.getInputStream();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Scanner scanner;
            if (inputStream != null) {
                scanner = new Scanner(inputStream);
                scanner.useDelimiter("\\A");
                boolean hasInput = scanner.hasNext();
                if (hasInput) {
                    return scanner.next();
                } else {
                    return null;
                }
            }else{
                return null;
            }
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }
}
