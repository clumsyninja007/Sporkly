package com.example.aharm.sporkly;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * This class controls the My Ingredients layout including accessing the spoonacular API
 * and displaying the results in a user-friendly way
 * Written by: David Sides, Archer Harmony
 */

public class MyIngredientsActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, TextView.OnEditorActionListener {
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
        setContentView(R.layout.my_ingredients);

        app = (MyApplication)this.getApplication();

        recipeButton = (Button)findViewById(R.id.recipeButton);
        recipeText = (EditText)findViewById(R.id.recipeText);
        recipeList = (ListView)findViewById(R.id.recipeList);

        // Initialize data for the ingredients list view.
        recipeItems = new ArrayList<>();
        recipeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, recipeItems);
        recipeList.setAdapter(recipeAdapter);

        recipeButton.setOnClickListener(this);
        recipeList.setOnItemClickListener(this);
        recipeText.setOnEditorActionListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.recipeButton:
                Log.i("recipeButton", "clicked");

                new SearchByIngredientsTask().execute(recipeText.getText().toString());

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

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            new SearchByIngredientsTask().execute(recipeText.getText().toString());

            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            return true;
        }
        return false;
    }

    private class SearchByIngredientsTask extends AsyncTask<String, Void, Boolean> {
        JSONArray result;

        protected Boolean doInBackground(String... query) {
            result = app.apiRequestArray("https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/findByIngredients?" +
                    "fillIngredients=" + false +
                    "&ingredients=" + query[0] +
                    "&limitLicense=" + false +
                    "&number" + 5 +
                    "&ranking" + 1);

            return result != null;
        }

        protected void onPostExecute(Boolean success) {
            Log.d("onPostExecute", "" + success);

            if (success) {
                Log.i("pantryAddButton", "clicked");

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
