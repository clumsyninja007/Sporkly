package com.example.aharm.sporkly;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * This class controls loading the main page layout as well as button functionality
 * Written by Archer Harmony, Trevor Dewitt, and Kevin Navarro
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button recipe, pantry, shoppingList, recipeSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        recipe = (Button)findViewById(R.id.myIngredients);
        pantry = (Button)findViewById(R.id.myPantry);
        shoppingList = (Button)findViewById(R.id.myShoppingList);
        recipeSearch = (Button)findViewById(R.id.recipeSearch);

        recipe.setOnClickListener(this);
        pantry.setOnClickListener(this);
        shoppingList.setOnClickListener(this);
        recipeSearch.setOnClickListener(this);

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
            default:
                break;
        }
    }
}
