package com.example.aharm.sporkly;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
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

public class myRecipeSearchActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    MyApplication app;

    ListView recipeList;
    Button recipeButton;
    EditText recipeText;

    //Array for list of recipes
    ArrayList<String> recipeItems =new ArrayList<>();
    ArrayAdapter<String> recipeAdapter;

    int[] recipeIDs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_recipe_search);

        app = ((MyApplication) this.getApplication());

        recipeButton = (Button)findViewById(R.id.recipeSearchButton);
        recipeText = (EditText)findViewById(R.id.recipeSearchText);
        recipeList = (ListView)findViewById(R.id.recipeSearchList);

        // Initialize data for the ingredients list view.
        recipeItems = new ArrayList<>();
        recipeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, recipeItems);
        recipeList.setAdapter(recipeAdapter);

        recipeButton.setOnClickListener(this);
        recipeList.setOnItemClickListener(this);
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

    @Override
    public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
        Log.i("recipeList", "clicked");

        app.viewRecipe(this, recipeIDs[position]);
    }

    private class SearchForRecipesTask extends AsyncTask<String, Void, Boolean> {
        JSONArray result;

        protected Boolean doInBackground(String... query) {
            result = Util.ApiRequestArray("https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/autocomplete?" +
                    "number=" + 5 +
                    "&query=" + query[0]);

            return result != null;
        }

        protected void onPostExecute(Boolean success) {
            Log.d("onPostExecute", "" + success);

            if (success) {
                recipeAdapter.clear();
                Log.i("Parsing JSONArray", "Length: " + result.length());

                if (result.length() > 0) {
                    recipeIDs = new int[result.length()];
                    for (int i = 0; i < result.length(); i++) {
                        try {
                            Log.i("Parsing JSONArray", "Current: " + i);
                            JSONObject obj = result.getJSONObject(i);
                            String name = obj.getString("title");
                            int id = obj.getInt("id");

                            Log.i("Parsing JSONArray", "Name: " + name);
                            Log.i("Parsing JSONArray", "ID: " + id);

                            recipeAdapter.add(name);
                            recipeIDs[i] = id;
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
