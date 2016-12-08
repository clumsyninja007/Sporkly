package com.example.aharm.sporkly;

import android.app.Application;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyApplication extends Application {
    private static final String API_KEY = "9rUDYWAnkEmshQkuvwanU54zDmXDp15QkyljsnQa9nVIoFwLY8";
    private ListStorage pantryStorage = new ListStorage(this, "my_pantry.txt");
    private ListStorage shoppingStorage = new ListStorage(this, "my_shopping_list.txt");
    private ListStorage favoritesStorage = new ListStorage(this, "my_favorites.txt");
    private ListStorage scheduleStorage = new ListStorage(this, "my_schedule.txt");

    private int recipeID;

    @Override
    public void onCreate() {
        super.onCreate();
        pantryStorage.load();
        shoppingStorage.load();
        favoritesStorage.load();
        scheduleStorage.load();
    }

    public ListStorage getPantryStorage() {
        return pantryStorage;
    }

    public ListStorage getShoppingStorage() {
        return shoppingStorage;
    }

    public ListStorage getFavoritesStorage() {
        return favoritesStorage;
    }

    public ListStorage getScheduleStorage() {
        return scheduleStorage;
    }

    public void viewRecipe(AppCompatActivity activity, int recipeID) {
        Log.d("ViewingRecipe", Integer.toString(recipeID));

        this.recipeID = recipeID;
        Intent intent = new Intent(activity, RecipeViewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void viewRandomRecipe(AppCompatActivity activity) {
        viewRecipe(activity, 0);
    }

    public int getRecipeID() {return recipeID;}

    String httpRequest(String urlString) {
        try {
            Log.d("url", urlString);

            URL url = new URL(urlString);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            // Add header properties, such as api key
            con.setRequestProperty("X-Mashape-Key", API_KEY);
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

            return response.toString();
        }catch( Exception e) {
            Log.e("httpRequest", e.toString());
        }

        return "";
    }

    JSONArray apiRequestArray(String urlString) {
        String response = httpRequest(urlString);

        if (response != "") {
            try {
                return new JSONArray(response);
            } catch (Exception e) {
                Log.e("apiRequestArray", e.toString());
            }
        }

        return null;
    }

    JSONObject apiRequestObject(String urlString) {
        String response = httpRequest(urlString);

        if (response != "") {
            try {
                return new JSONObject(response);
            } catch (Exception e) {
                Log.e("apiRequestObject", e.toString());
            }
        }

        return null;
    }

    int listViewMeasuredHeight(ListView view) {
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
