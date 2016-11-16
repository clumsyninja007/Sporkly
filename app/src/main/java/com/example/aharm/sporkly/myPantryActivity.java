package com.example.aharm.sporkly;

import android.content.Context;
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
    private static final String API_KEY = "9rUDYWAnkEmshQkuvwanU54zDmXDp15QkyljsnQa9nVIoFwLY8";
    private static final String PANTRY_FILE = "myPantry.txt";

    ListStorage pantry;
    ListStorage shoppingList;

    Button addButton;
    EditText editText;
    ListView pantryList;
    ListView searchList;

    //Array for list of ingredients in the pantry.
    ArrayAdapter<String> pantryAdapter;

    //Array for search results.
    ArrayList<String> searchItems = new ArrayList<String>();
    ArrayAdapter<String> searchAdapter;

    HttpURLConnection con;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_pantry);

        pantry = ((MyApplication) this.getApplication()).getPantry();
        shoppingList = ((MyApplication) this.getApplication()).getShoppingList();

        addButton = (Button)findViewById(R.id.pantryAddButton);
        editText = (EditText)findViewById(R.id.pantryAddText);
        pantryList = (ListView)findViewById(R.id.pantryIngredientList);
        searchList = (ListView)findViewById(R.id.pantrySearchList);

        // Initialize data for the ingredients list view.
        pantryAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, pantry.getItems());
        pantryList.setAdapter(pantryAdapter);

        // Enable context menu on the list, so we can remove list items.
        registerForContextMenu(pantryList);

        // Initialize data for the search list view.
        searchItems = new ArrayList<String>();
        searchAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, searchItems);
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

        pantry.add(searchItems.get(position));
        pantryAdapter.notifyDataSetChanged();

        searchAdapter.clear();
        searchAdapter.notifyDataSetChanged();

        resizeSearchResults();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if(v.getId() != R.id.pantryIngredientList){
            return;
        }

        menu.setHeaderTitle("What do you want??");

        String[] options = { "Delete Entry", "Return" };

        for(String option : options){
            menu.add(option);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int selectedIndex = info.position;

        if(item.getTitle().equals("Delete Entry")){
            pantry.remove(selectedIndex);
            pantryAdapter.notifyDataSetChanged();
        }

        return true;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.pantryAddButton:
                Log.i("pantryAddButton", "clicked");

                searchForIngredients(editText.getText().toString());

                // This forces the keyboard to hide, because for some reason
                // the keyboard showing makes the search results not show.
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                break;
            default:
                break;
        }
    }

    private void searchForIngredients(final String query) {
        new Thread(new Runnable() {
            public void run() {
                if(query.length() > 1) {
                    Log.i("pantryAddButton", "clicked");

                    // Real query
                    final JSONArray arr = requestAutoCompleteIngredients(false, 5, query);

                    // Test query
                    /*JSONArray testArr = null;
                    try {
                        testArr = new JSONArray("[{\"name\":\"apple\",\"image\":\"apple.jpg\"}]");
                    } catch (Exception e) {

                    }
                    final JSONArray arr = testArr;*/

                    searchList.post(new Runnable() {
                        public void run() {
                            searchAdapter.clear();
                            Log.i("Parsing JSONArray", "Length: " + arr.length());

                            if (arr != null) {
                                for (int i=0; i < arr.length(); i++)
                                {
                                    try {
                                        Log.i("Parsing JSONArray", "Current: " + i);
                                        JSONObject obj = arr.getJSONObject(i);
                                        String name = obj.getString("name");
                                        Log.i("Parsing JSONArray", "Name: " + name);
                                        searchAdapter.add(name);
                                    } catch (Exception e) {
                                        Log.i("Parsing JSONArray", e.getMessage());
                                    }
                                }
                            }

                            Log.i("SearchList", "Updated");
                            searchAdapter.notifyDataSetChanged();

                            resizeSearchResults();
                        }
                    });
                }
            }
        }).start();
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

    private JSONArray requestAutoCompleteIngredients(boolean metaInfo, int number, String query) {
        JSONArray arr = null;
        try {
            URL url = new URL("https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/food/ingredients/autocomplete?" +
                    "metaInformation=" + metaInfo +
                    "&number=" + number +
                    "&query=" + query);

            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            // Add header properties, such as api key
            con.setRequestProperty("X-Mashape-Key", API_KEY);
            con.setRequestProperty("Accept", "application/json");

            int responseCode = con.getResponseCode();

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            Log.d("http", response.toString());

            arr = new JSONArray(response.toString());
        }catch( Exception e) {
            e.printStackTrace();
        }
        finally {
            con.disconnect();
        }
        return arr;
    }
}
