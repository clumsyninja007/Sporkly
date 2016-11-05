package com.example.aharm.sporkly;

import android.content.Context;
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
 * This class controls the My Ingredients layout including accessing the spoonacular API
 * and displaying the results in a user-friendly way
 * Written by: David Sides, Archer Harmony
 */

public class myIngredientsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String API_KEY = "9rUDYWAnkEmshQkuvwanU54zDmXDp15QkyljsnQa9nVIoFwLY8";

    ListView recipeList;
    Button recipeButton;
    EditText recipeText;

    //Array for list of recipes
    ArrayList<String> recipeItems =new ArrayList<String>();
    ArrayAdapter<String> recipeAdapter;

    HttpURLConnection con;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_ingredients);

        recipeButton = (Button)findViewById(R.id.recipeButton);
        recipeText = (EditText)findViewById(R.id.recipeText);
        recipeList = (ListView)findViewById(R.id.recipeList);

        // Initialize data for the ingredients list view.
        recipeItems = new ArrayList<String>();
        recipeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, recipeItems);
        recipeList.setAdapter(recipeAdapter);

        recipeButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.recipeButton:
                Log.i("recipeButton", "clicked");

                searchForRecipes(recipeText.getText().toString());

                // This forces the keyboard to hide, because for some reason
                // the keyboard showing makes the search results not show.
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                break;
            default:
                break;
        }
    }

    private void searchForRecipes(final String ingredients) {
        new Thread(new Runnable() {
            public void run() {
                if(ingredients.length() > 1) {
                    Log.i("pantryAddButton", "clicked");

                    // Real query
                    final JSONArray arr = requestFindByIngredients(false, ingredients, false, 10, 1);

                    // Test query
                    /*JSONArray testArr = null;
                    try {
                        testArr = new JSONArray("[{\"name\":\"apple\",\"image\":\"apple.jpg\"}]");
                    } catch (Exception e) {

                    }
                    final JSONArray arr = testArr;*/

                    recipeList.post(new Runnable() {
                        public void run() {
                            recipeAdapter.clear();
                            Log.i("Parsing JSONArray", "Length: " + arr.length());

                            if (arr != null) {
                                if (arr.length() > 0) {
                                    for (int i = 0; i < arr.length(); i++) {
                                        try {
                                            Log.i("Parsing JSONArray", "Current: " + i);
                                            JSONObject obj = arr.getJSONObject(i);
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
                            }

                            Log.i("SearchList", "Updated");
                            recipeAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        }).start();
    }

    private JSONArray requestFindByIngredients(boolean fillIngredients, String ingredients, boolean limitLicense, int number, int ranking) {
        JSONArray arr = null;
        try {
            URL url = new URL("https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/findByIngredients?" +
                    "fillIngredients=" + fillIngredients +
                    "&ingredients=" + ingredients +
                    "&limitLicense=" + limitLicense +
                    "&number" + number +
                    "&ranking" + ranking);

            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            // Add header properties, such as api key
            con.setRequestProperty("X-Mashape-Key", API_KEY);
            con.setRequestProperty("Accept", "application/json");

            int responseCode = con.getResponseCode();

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            Log.d("http", response.toString());

            arr = new JSONArray(response.toString());
        }catch( Exception e) {
            e.printStackTrace();
        }
        finally {
            con.disconnect();
        }
        return arr;
    }
}
