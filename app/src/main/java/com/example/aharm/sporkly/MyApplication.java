package com.example.aharm.sporkly;

import android.app.Application;

/**
 * Created by David on 11/16/2016.
 */

public class MyApplication extends Application {
    private ListStorage pantryStorage = new ListStorage(this, "my_pantry.txt");
    private ListStorage shoppingStorage = new ListStorage(this, "my_shopping_list.txt");

    @Override
    public void onCreate() {
        super.onCreate();
        pantryStorage.load();
        shoppingStorage.load();
    }

    public ListStorage getPantryStorage() {
        return pantryStorage;
    }

    public ListStorage getShoppingStorage() {
        return shoppingStorage;
    }
}
