package com.example.aharm.sporkly;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import java.util.Random;

/**
 * This class is used to display detailed recipe information.
 */

public class RecipeViewActivity extends AppCompatActivity implements View.OnClickListener {
    MyApplication app;

    ListStorage favoritesStorage;

    TextView recipeTitle, recipeIngredientsTitle, recipeInstructionsTitle, recipeInstructions, failedText;
    ListView recipeInfo, recipeIngredients;
    ProgressBar recipeLoad;
    Button recipeAddFavorite;

    int recipeID;

    JSONObject recipeData;

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

        app = (MyApplication)this.getApplication();

        favoritesStorage = app.getFavoritesStorage();

        recipeTitle = (TextView) findViewById(R.id.recipeTitle);
        recipeIngredientsTitle = (TextView) findViewById(R.id.recipeIngredientsTitle);
        recipeInstructionsTitle = (TextView) findViewById(R.id.recipeInstructionsTitle);
        recipeInstructions = (TextView) findViewById(R.id.recipeInstructions);
        failedText = (TextView) findViewById(R.id.recipeLoadFailed);
        recipeInfo = (ListView) findViewById(R.id.recipeInfo);
        recipeIngredients = (ListView) findViewById(R.id.recipeIngredients);
        recipeLoad = (ProgressBar) findViewById(R.id.recipeLoadProgress);
        recipeAddFavorite = (Button) findViewById(R.id.recipeAddFavorite);

        recipeInfoList = new ArrayList<>();
        recipeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, recipeInfoList);
        recipeInfo.setAdapter(recipeAdapter);

        recipeIngredientsList = new ArrayList<>();
        recipeIngredientsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, recipeIngredientsList);
        recipeIngredients.setAdapter(recipeIngredientsAdapter);

        recipeTitle.setOnClickListener(this);
        recipeIngredientsTitle.setOnClickListener(this);
        recipeInstructionsTitle.setOnClickListener(this);
        recipeAddFavorite.setOnClickListener(this);

        recipeLoad.setVisibility(View.VISIBLE);

        recipeID = app.getRecipeID();

        new ViewRecipeTask().execute(recipeID);
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
            case R.id.recipeAddFavorite:
                try {
                    String title = recipeData.getString("title");
                    int id = recipeData.getInt("id");
                    int index = favoritesStorage.find(title);

                    if (index != -1) {
                        favoritesStorage.remove(index);

                        recipeAddFavorite.setText("Add to favorites");
                    } else {
                        JSONObject obj = new JSONObject();
                        obj.put("id", id);

                        favoritesStorage.add(title, obj);

                        recipeAddFavorite.setText("Remove from favorites");
                    }

                } catch (Exception e) {
                    Log.e("JSON", e.toString());
                }
            default:
                break;
        }

        UpdateSizes();
    }

    /*
     * Updates the heights of the Views that display information relating to
     * the recipe, if a specific view shouldn't be visible, it sets the height
     * to a small number so it isn't visible, but if it should be visible, it sets
     * the height to its content height.
     */
    private void UpdateSizes() {
        int infoHeight = 6;
        int ingredientsHeight = 6;
        int instructionsHeight = 6;

        if (infoVisible) {
            infoHeight = app.listViewMeasuredHeight(recipeInfo);
        }

        if (ingredientsVisible) {
            ingredientsHeight = app.listViewMeasuredHeight(recipeIngredients);
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

    /*
     * Updates the instructions text view, by formating the
     * raw instructions string returned from the api, so it is
     * more easily readable.
     * @param instructions the instructions string
     * @return the formatted instructions
     */
    private String UpdateInstructions(String instructions) {
        StringBuilder sb = new StringBuilder();

        char first = instructions.charAt(0);
        if (first != ' ') {
            sb.append(first);
        }

        for (int i = 1; i < instructions.length(); i++) {
            char c = instructions.charAt(i);
            char p = instructions.charAt(i - 1);

            if (p == '.') {
                sb.append('\n');
                sb.append('\n');
            } else if (p == ':'){
                sb.append('\n');
            } else if (Character.isLowerCase(p) && Character.isUpperCase(c)) {
                sb.append('\n');
            }

            if (c != ' ' || p != ' ') {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    private class ViewRecipeTask extends AsyncTask<Integer, Void, Boolean> {
        JSONObject result = null;
        int retries = 5;

        protected Boolean doInBackground(Integer... recipeID) {
            for (int i = 0; i < retries; i++) {
                Log.d("Attempt", Integer.toString(i));
                try {
                    if (recipeID[0] != 0) {
                        // Get specific recipe if recipe id is not 0
                        result = app.apiRequestObject("https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/" +
                                recipeID[0] +
                                "/information?includeNutrition=false");
                    } else {
                        // Get random recipe if recipe id is 0
                        result = app.apiRequestObject("https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/random?limitLicense=false&number=1");
                        if (result != null) {
                            result = result.getJSONArray("recipes").getJSONObject(0);
                        }
                    }

                    if (result != null) {
                        return true;
                    }
                } catch (Exception e) {
                    Log.e("ViewRecipe", e.toString());
                }
                SystemClock.sleep(3000);
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

                    //UpdateInstructions(instructions);
                    recipeInstructions.setText(UpdateInstructions(instructions));

                    UpdateSizes();

                    recipeLoad.setVisibility(View.GONE);

                    if (instructions == "null") {
                        recipeInstructionsTitle.setVisibility(View.GONE);
                    }

                    if (favoritesStorage.find(title) != -1) {
                        recipeAddFavorite.setText("Remove from favorites");
                    }

                    recipeData = result;

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
