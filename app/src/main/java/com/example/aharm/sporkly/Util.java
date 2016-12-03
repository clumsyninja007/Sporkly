package com.example.aharm.sporkly;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This class contains some useful functions used by various classes.
 */

public class Util {
    static MyApplication app;

    static JSONArray apiRequestArray(String urlString) {
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

    static JSONObject apiRequestObject(String urlString) {
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

    static int listViewMeasuredHeight(ListView view) {
        int height = 0;

        ListAdapter adapter = view.getAdapter();
        int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            View item = adapter.getView(i, null, view);
            item.measure(0, 0);
            height += item.getMeasuredHeight();
        }

        return height + view.getDividerHeight() * (count - 1);
    }
}
