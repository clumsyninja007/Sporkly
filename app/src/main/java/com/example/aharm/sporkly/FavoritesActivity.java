package com.example.aharm.sporkly;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;

/**
 *
 */

public class FavoritesActivity extends AppCompatActivity {
    private static final String[] CONTEXT_OPTIONS = { "Delete Entry", "Return" };

    MyApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorites);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("What do you want??");

        for(String option : CONTEXT_OPTIONS){
            menu.add(option);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        return true;
    }
}
