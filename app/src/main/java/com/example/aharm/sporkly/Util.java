package com.example.aharm.sporkly;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by David on 12/3/2016.
 */

public class Util {
    static MyApplication app;

    static JSONArray ApiRequestArray(String urlString) {
        try {
            URL url = new URL(urlString);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            // Add header properties, such as api key
            con.setRequestProperty("X-Mashape-Key", app.getAPIKey());
            con.setRequestProperty("Accept", "application/json");

            int responseCode = con.getResponseCode();

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            con.disconnect();

            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            Log.d("http", response.toString());

            return new JSONArray(response.toString());
        }catch( Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    static JSONObject ApiRequestObject(String urlString) {
        try {
            URL url = new URL(urlString);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            // Add header properties, such as api key
            con.setRequestProperty("X-Mashape-Key", app.getAPIKey());
            con.setRequestProperty("Accept", "application/json");

            int responseCode = con.getResponseCode();

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            con.disconnect();

            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            Log.d("http", response.toString());

            return new JSONObject(response.toString());
        }catch( Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
