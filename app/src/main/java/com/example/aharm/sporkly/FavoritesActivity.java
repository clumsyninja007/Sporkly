package com.example.aharm.sporkly;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 *
 */

public class FavoritesActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static final String[] CONTEXT_OPTIONS = { "Delete Entry", "Return" };

    MyApplication app;

    ListView favoritesList;

    ListStorage favoritesStorage;

    ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorites);

        app = (MyApplication)this.getApplication();

        favoritesStorage = app.getFavoritesStorage();

        favoritesList = (ListView) findViewById(R.id.favoritesList);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, favoritesStorage.getItems());
        favoritesList.setAdapter(adapter);

        registerForContextMenu(favoritesList);

        favoritesList.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i("ItemClick", "Position: " + position);

        try {
            app.viewRecipe(this, favoritesStorage.getData(position).getInt("id"));
        } catch (Exception e) {
            Log.e("Error", e.toString());
        }
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
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int selectedIndex = info.position;
        CharSequence option = item.getTitle();

        if (option.equals(CONTEXT_OPTIONS[0])) {
            favoritesStorage.remove(selectedIndex);
            adapter.notifyDataSetChanged();
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }
}
