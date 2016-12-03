package com.example.aharm.sporkly;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
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
 * This class controls the My Pantry layout including allowing users to create a list of
 * ingredients on hand as well as checking that those ingredients exist within the spoonacular
 * ingredients database.
 * Written by: Trevor Dewitt, David Sides
 */

public class myPantryActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener{
    private static final String[] CONTEXT_OPTIONS = { "Delete Entry", "Move to Shopping List",  "Return" };

    MyApplication app;

    ListStorage pantryStorage;
    ListStorage shoppingStorage;

    Button addButton;
    EditText editText;
    ListView pantryList;
    ListView searchList;

    //Array for list of ingredients in the pantry.
    ArrayAdapter<String> pantryAdapter;

    //Array for search results.
    ArrayList<String> searchItems = new ArrayList<>();
    ArrayAdapter<String> searchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_pantry);

        app = ((MyApplication) this.getApplication());

        pantryStorage = app.getPantryStorage();
        shoppingStorage = app.getShoppingStorage();

        addButton = (Button)findViewById(R.id.pantryAddButton);
        editText = (EditText)findViewById(R.id.pantryAddText);
        pantryList = (ListView)findViewById(R.id.pantryIngredientList);
        searchList = (ListView)findViewById(R.id.pantrySearchList);

        // Initialize data for the ingredients list view.
        pantryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, pantryStorage.getItems());
        pantryList.setAdapter(pantryAdapter);

        // Enable context menu on the list, so we can remove list items.
        registerForContextMenu(pantryList);

        // Initialize data for the search list view.
        searchItems = new ArrayList<>();
        searchAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, searchItems);
        searchList.setAdapter(searchAdapter);
        searchList.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
        searchList.setStackFromBottom(false);

        // Make the search list items clickable
        searchList.setOnItemClickListener(this);

        // Make the search ingredient button clickable
        addButton.setOnClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i("ItemClick", "Position: " + position);

        pantryStorage.add(searchItems.get(position));
        pantryAdapter.notifyDataSetChanged();

        searchAdapter.clear();
        searchAdapter.notifyDataSetChanged();

        resizeSearchResults();
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
            pantryStorage.remove(selectedIndex);
            pantryAdapter.notifyDataSetChanged();
        } else if (option.equals(CONTEXT_OPTIONS[1])) {
            shoppingStorage.add(pantryStorage.remove(selectedIndex));
            pantryAdapter.notifyDataSetChanged();
        }

        return true;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.pantryAddButton:
                Log.i("pantryAddButton", "clicked");

                new SearchForIngredientsTask().execute(editText.getText().toString());

                // This forces the keyboard to hide, because for some reason
                // the keyboard showing makes the search results not show.
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                break;
            default:
                break;
        }
    }

    private void resizeSearchResults() {
        // Resize search view to item count
        int numberOfItems = searchAdapter.getCount();

        // Get total height of all items.
        int totalItemsHeight = 0;
        for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
            View item = searchAdapter.getView(itemPos, null, searchList);
            item.measure(0, 0);
            totalItemsHeight += item.getMeasuredHeight();
        }

        // Get total height of all item dividers.
        int totalDividersHeight = searchList.getDividerHeight() *
                (numberOfItems - 1);

        // Set list height.
        ViewGroup.LayoutParams params = searchList.getLayoutParams();
        params.height = totalItemsHeight + totalDividersHeight;
        searchList.setLayoutParams(params);
        searchList.requestLayout();
    }

    private class SearchForIngredientsTask extends AsyncTask<String, Void, Boolean> {
        JSONArray result;

        protected Boolean doInBackground(String... query) {
            result = Util.ApiRequestArray("https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/food/ingredients/autocomplete?" +
                    "metaInformation=" + false +
                    "&number=" + 5 +
                    "&query=" + query[0]);

            return result != null;
        }

        protected void onPostExecute(Boolean success) {
            Log.d("onPostExecute", "" + success);

            if (success) {
                searchAdapter.clear();
                Log.i("Parsing JSONArray", "Length: " + result.length());

                if (result.length() > 0) {
                    for (int i = 0; i < result.length(); i++) {
                        try {
                            Log.i("Parsing JSONArray", "Current: " + i);
                            JSONObject obj = result.getJSONObject(i);
                            String name = obj.getString("name");
                            Log.i("Parsing JSONArray", "Name: " + name);
                            searchAdapter.add(name);
                        } catch (Exception e) {
                            Log.i("Parsing JSONArray", e.getMessage());
                        }
                    }
                } else {
                    searchAdapter.add("No ingredients found");
                }

                Log.i("SearchList", "Updated");
                searchAdapter.notifyDataSetChanged();

                resizeSearchResults();
            } else {
                Log.d("http", "API request failed");
            }
        }
    }
}
