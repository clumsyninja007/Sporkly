package com.example.aharm.sporkly;

import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * This class controls loading the main page layout as well as button functionality
 * Written by Archer Harmony, Trevor Dewitt, and Kevin Navarro
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    MyApplication app;

    private Button recipe, pantry, shoppingList, recipeSearch, randomRecipe, favorites, mealPlan;
    private ImageView milkMan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        app = (MyApplication) this.getApplication();

        recipe = (Button)findViewById(R.id.myIngredients);
        pantry = (Button)findViewById(R.id.myPantry);
        shoppingList = (Button)findViewById(R.id.myShoppingList);
        recipeSearch = (Button)findViewById(R.id.recipeSearch);
        randomRecipe = (Button)findViewById(R.id.randomRecipe);
        favorites = (Button)findViewById(R.id.favorites);
        mealPlan = (Button) findViewById(R.id.meal_schedule);
        milkMan = (ImageView) findViewById(R.id.milkManButton);

        recipe.setOnClickListener(this);
        pantry.setOnClickListener(this);
        shoppingList.setOnClickListener(this);
        recipeSearch.setOnClickListener(this);
        randomRecipe.setOnClickListener(this);
        favorites.setOnClickListener(this);
        mealPlan.setOnClickListener(this);
        milkMan.setOnClickListener(this);

        //((MyApplication) this.getApplication()).viewRecipe(this, 721001);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.myIngredients:
                startActivity(new Intent(this, MyIngredientsActivity.class));
                break;
            case R.id.myPantry:
                startActivity(new Intent(this, PantryActivity.class));
                break;
            case R.id.myShoppingList:
                startActivity(new Intent(this, ShoppingListActivity.class));
                break;
            case R.id.recipeSearch:
                startActivity(new Intent(this, RecipeSearchActivity.class));
                break;
            case R.id.randomRecipe:
                app.viewRandomRecipe(this);
                break;
            case R.id.favorites:
                startActivity(new Intent(this, FavoritesActivity.class));
                break;
            case R.id.meal_schedule:
                startActivity(new Intent(this, MealPlannerActivity.class));
                break;
            case R.id.milkManButton:
                startActivity(new Intent(this, MilkMan.class));
                break;
            default:
                break;
        }
    }
}
