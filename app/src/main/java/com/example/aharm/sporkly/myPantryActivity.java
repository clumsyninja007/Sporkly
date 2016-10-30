package com.example.aharm.sporkly;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by aharm on 10/26/2016.
 */

public class myPantryActivity extends AppCompatActivity implements View.OnClickListener {
    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<String> listItems=new ArrayList<String>();

    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    ArrayAdapter<String> adapter;

    private Button addButton;

    HttpURLConnection urlConnection;

    private String apikey = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_pantry);

        listItems = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);

        ListView theList = (ListView)findViewById(R.id.pantryIngredientList);
        theList.setAdapter(adapter);

        addButton = (Button)findViewById(R.id.pantryAddButton);
        addButton.setOnClickListener(this);

        registerForContextMenu(theList);

        StringBuilder result = new StringBuilder();

        // Test http stuff
        /*StringBuilder result = new StringBuilder();
        try {
            URL url = new URL("https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/parseIngredients");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("X-Mashape-Key", apikey);
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            String urlParams = "ingredientList=egg&servings=2";

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

        }catch( Exception e) {
            e.printStackTrace();
        }
        finally {
            urlConnection.disconnect();
        }*/

        try{
            Log.i("ON CREATE", "OnCreate event occurred.");

            Scanner scanner = new Scanner(openFileInput("myPantry.txt"));

            while (scanner.hasNextLine()) {
                String toDo = scanner.nextLine();
                adapter.add(toDo);
            }
            scanner.close();
        } catch (Exception e) {
            Log.i("ON CREATE", e.getMessage());
        }
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
            listItems.remove(selectedIndex);
            adapter.notifyDataSetChanged();
        }

        return true;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.pantryAddButton:
                Log.i("pantryAddButon", "clicked");
                EditText editTextToDo = (EditText)findViewById(R.id.pantryAddText);
                String newEntry = editTextToDo.getText().toString();

                if(newEntry.isEmpty()) {
                    return;
                }

                adapter.add(newEntry);
                editTextToDo.setText("");
                adapter.notifyDataSetChanged();

                try{
                    PrintWriter pw = new PrintWriter(openFileOutput("myPantry.txt", Context.MODE_PRIVATE));

                    for(String toDo : listItems){
                        pw.println(toDo);
                    }
                    pw.close();
                } catch (Exception e) {
                    Log.i("ON BACK PRESSED", e.getMessage());
                }

                break;
            default:
                break;
        }
    }
}
