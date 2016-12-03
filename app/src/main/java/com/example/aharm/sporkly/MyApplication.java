package com.example.aharm.sporkly;

import android.app.Application;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by David on 11/16/2016.
 */

public class MyApplication extends Application {
    private static final String API_KEY = "9rUDYWAnkEmshQkuvwanU54zDmXDp15QkyljsnQa9nVIoFwLY8";
    private ListStorage pantryStorage = new ListStorage(this, "my_pantry.txt");
    private ListStorage shoppingStorage = new ListStorage(this, "my_shopping_list.txt");
    private int recipeID;

    @Override
    public void onCreate() {
        super.onCreate();
        pantryStorage.load();
        shoppingStorage.load();

        Util.app = this;
    }

    public ListStorage getPantryStorage() {
        return pantryStorage;
    }

    public ListStorage getShoppingStorage() {
        return shoppingStorage;
    }

    public void viewRecipe(AppCompatActivity activity, int recipeID) {
        Log.d("ViewingRecipe", Integer.toString(recipeID));

        this.recipeID = recipeID;
        Intent intent = new Intent(activity, RecipeView.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public int getRecipeID() {return recipeID;}

    public String getAPIKey() {return API_KEY;}
}
