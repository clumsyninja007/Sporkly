package com.example.aharm.sporkly;

import android.app.Application;

/**
 * Created by David on 11/16/2016.
 */

public class MyApplication extends Application {
    private ListStorage pantry = new ListStorage(this, "my_pantry.txt");
    private ListStorage shoppingList = new ListStorage(this, "my_shopping_list.txt");

    @Override
    public void onCreate() {
        super.onCreate();
        pantry.load();
        shoppingList.load();
    }

    public ListStorage getPantry() {
        return pantry;
    }

    public ListStorage getShoppingList() {
        return shoppingList;
    }
}
