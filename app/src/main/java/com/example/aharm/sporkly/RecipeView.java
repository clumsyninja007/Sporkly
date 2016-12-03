package com.example.aharm.sporkly;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by David on 11/27/2016.
 */

public class RecipeView extends AppCompatActivity implements View.OnClickListener{
    MyApplication app;

    TextView recipeTitle, recipeIngredientsTitle, recipeInstructionsTitle, recipeInstructions, failedText;
    ListView recipeInfo, recipeIngredients;
    ProgressBar recipeLoad;

    int recipeID;

    //Array for recipe info
    ArrayList<String> recipeInfoList = new ArrayList<>();
    ArrayAdapter<String> recipeAdapter;

    //Array for recipe info
    ArrayList<String> recipeIngredientsList = new ArrayList<>();
    ArrayAdapter<String> recipeIngredientsAdapter;

    boolean infoVisible = true;
    boolean ingredientsVisible = false;
    boolean instructionsVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_view);

        app = ((MyApplication) this.getApplication());

        recipeTitle = (TextView) findViewById(R.id.recipeTitle);
        recipeIngredientsTitle = (TextView) findViewById(R.id.recipeIngredientsTitle);
        recipeInstructionsTitle = (TextView) findViewById(R.id.recipeInstructionsTitle);
        recipeInstructions = (TextView) findViewById(R.id.recipeInstructions);
        failedText = (TextView) findViewById(R.id.recipeLoadFailed);
        recipeInfo = (ListView) findViewById(R.id.recipeInfo);
        recipeIngredients = (ListView) findViewById(R.id.recipeIngredients);
        recipeLoad = (ProgressBar) findViewById(R.id.recipeLoadProgress);

        recipeInfoList = new ArrayList<>();
        recipeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, recipeInfoList);
        recipeInfo.setAdapter(recipeAdapter);

        recipeIngredientsList = new ArrayList<>();
        recipeIngredientsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, recipeIngredientsList);
        recipeIngredients.setAdapter(recipeIngredientsAdapter);

        recipeID = app.getRecipeID();

        recipeTitle.setOnClickListener(this);
        recipeIngredientsTitle.setOnClickListener(this);
        recipeInstructionsTitle.setOnClickListener(this);

        new GetRecipeInfo().execute(recipeID);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.recipeTitle:
                infoVisible = !infoVisible;
                break;
            case R.id.recipeIngredientsTitle:
                ingredientsVisible = !ingredientsVisible;
                break;
            case R.id.recipeInstructionsTitle:
                instructionsVisible = !instructionsVisible;
                break;
            default:
                break;
        }

        UpdateSizes();
    }

    private void UpdateSizes() {
        int infoHeight = 6;
        int ingredientsHeight = 6;
        int instructionsHeight = 6;

        if (infoVisible) {
            int count = recipeAdapter.getCount();
            for (int i = 0; i < count; i++) {
                View item = recipeAdapter.getView(i, null, recipeInfo);
                item.measure(0, 0);
                infoHeight += item.getMeasuredHeight();
            }

            infoHeight += recipeInfo.getDividerHeight() * (count - 1);
        }

        if (ingredientsVisible) {
            int count = recipeIngredientsAdapter.getCount();
            for (int i = 0; i < count; i++) {
                View item = recipeIngredientsAdapter.getView(i, null, recipeIngredients);
                item.measure(0, 0);
                ingredientsHeight += item.getMeasuredHeight();
            }

            ingredientsHeight += recipeIngredients.getDividerHeight() * (count - 1);
        }

        if (instructionsVisible) {
            instructionsHeight = ViewGroup.LayoutParams.WRAP_CONTENT;;
        }

        recipeInfo.getLayoutParams().height = infoHeight;
        recipeIngredients.getLayoutParams().height = ingredientsHeight;
        recipeInstructions.getLayoutParams().height = instructionsHeight;

        recipeInfo.requestLayout();
        recipeIngredients.requestLayout();
        recipeInstructions.requestLayout();
    }

    private class GetRecipeInfo extends AsyncTask<Integer, Void, Boolean> {
        JSONObject result = null;
        int retries = 5;

        protected Boolean doInBackground(Integer... recipeID) {
            for (int i = 0; i < retries; i++) {
                Log.d("Attempt", Integer.toString(i));
                result = Util.ApiRequestObject("https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/" +
                        recipeID[0] + "/information?includeNutrition=false");

                if (result != null) {
                    return true;
                }
            }

            return false;
        }

        protected void onPostExecute(Boolean success) {
            Log.d("onPostExecute", "" + success);

            if (success) {
                try {
                    String title = result.getString("title");
                    String ingredients = result.getString("extendedIngredients");
                    String instructions = result.getString("instructions");
                    boolean vegetarian = result.getBoolean("vegetarian");
                    int servings = result.getInt("servings");
                    int minutes = result.getInt("readyInMinutes");

                    recipeTitle.setText(title);

                    recipeAdapter.add("Ready Time: " + minutes + " minutes");
                    recipeAdapter.add("Serving Size: " + servings);
                    recipeAdapter.add("Vegetarian: " + (vegetarian ? "Yes" : "No"));

                    JSONArray ingredientsArray = new JSONArray(ingredients);

                    for (int i = 0; i < ingredientsArray.length(); i++) {
                        JSONObject obj = ingredientsArray.getJSONObject(i);
                        String name = obj.getString("name");
                        String unit = obj.getString("unit");
                        double amount = obj.getDouble("amount");

                        recipeIngredientsAdapter.add(name + ": " + amount + " " + unit);
                    }

                    recipeInstructions.setText(instructions);

                    UpdateSizes();

                    recipeLoad.setVisibility(View.GONE);

                    if (instructions == "null") {
                        recipeInstructionsTitle.setVisibility(View.GONE);
                    }

                } catch (Exception e) {
                    failedText.setVisibility(View.VISIBLE);
                    Log.e("Parsing JSONObject", e.getMessage());
                }
            } else {
                failedText.setVisibility(View.VISIBLE);
                Log.e("http", "API request failed");
            }


        }
    }
}
