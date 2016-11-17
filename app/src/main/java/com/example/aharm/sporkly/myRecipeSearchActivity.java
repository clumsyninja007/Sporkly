package com.example.aharm.sporkly;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by David on 11/16/2016.
 */

public class myRecipeSearchActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String API_KEY = "9rUDYWAnkEmshQkuvwanU54zDmXDp15QkyljsnQa9nVIoFwLY8";

    ListView recipeList;
    Button recipeButton;
    EditText recipeText;

    //Array for list of recipes
    ArrayList<String> recipeItems =new ArrayList<>();
    ArrayAdapter<String> recipeAdapter;

    HttpURLConnection con;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_recipe_search);

        recipeButton = (Button)findViewById(R.id.recipeSearchButton);
        recipeText = (EditText)findViewById(R.id.recipeSearchText);
        recipeList = (ListView)findViewById(R.id.recipeSearchList);

        // Initialize data for the ingredients list view.
        recipeItems = new ArrayList<>();
        recipeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, recipeItems);
        recipeList.setAdapter(recipeAdapter);

        recipeButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.recipeSearchButton:
                Log.i("recipeButton", "clicked");

                new SearchForRecipesTask().execute(recipeText.getText().toString());

                // This forces the keyboard to hide, because for some reason
                // the keyboard showing makes the search results not show.
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                break;
            default:
                break;
        }
    }

    private class SearchForRecipesTask extends AsyncTask<String, Void, Boolean> {
        JSONArray result;

        protected Boolean doInBackground(String... query) {
            try {
                URL url = new URL("https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/autocomplete?" +
                        "number=" + 5 +
                        "&query=" + query[0]);

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

                result = new JSONArray(response.toString());

                return true;
            }catch( Exception e) {
                e.printStackTrace();
            }

            return false;
        }

        protected void onPostExecute(Boolean success) {
            Log.d("onPostExecute", "" + success);

            if (success) {
                recipeAdapter.clear();
                Log.i("Parsing JSONArray", "Length: " + result.length());

                if (result.length() > 0) {
                    for (int i = 0; i < result.length(); i++) {
                        try {
                            Log.i("Parsing JSONArray", "Current: " + i);
                            JSONObject obj = result.getJSONObject(i);
                            String name = obj.getString("title");
                            Log.i("Parsing JSONArray", "Name: " + name);
                            recipeAdapter.add(name);
                        } catch (Exception e) {
                            Log.i("Parsing JSONArray", e.getMessage());
                        }
                    }
                } else {
                    recipeAdapter.add("No recipes found");
                }

                Log.i("SearchList", "Updated");
                recipeAdapter.notifyDataSetChanged();
            } else {
                Log.d("http", "API request failed");
            }
        }
    }
}
