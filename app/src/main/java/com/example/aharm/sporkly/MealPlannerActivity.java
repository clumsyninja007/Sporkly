package com.example.aharm.sporkly;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;

import static android.icu.util.Calendar.getInstance;


/**
 * Created by Trevor Dewitt
 */

public class MealPlannerActivity extends AppCompatActivity implements  AdapterView.OnItemClickListener {
    private static final String[] CONTEXT_OPTIONS = { "Delete Entry", "Edit Date", "Add to favorites", "Return" };

    MyApplication app;

    ListView scheduleList;
    ListStorage scheduleStorage, favoritesStorage;
    ArrayAdapter adapter;
    int year_x, month_x, day_x, last_id;
    static final int DIAL_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meal_planner);

        final Calendar cal = getInstance();
        year_x = cal.get(Calendar.YEAR);
        month_x = cal.get(Calendar.MONTH);
        day_x = cal.get(Calendar.DAY_OF_MONTH);

        app = (MyApplication)this.getApplication();
        scheduleStorage = app.getScheduleStorage();
        favoritesStorage = app.getFavoritesStorage();

        scheduleList = (ListView)findViewById(R.id.scheduleList);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, scheduleStorage.getItems());
        scheduleList.setAdapter(adapter);
        registerForContextMenu(scheduleList);

        scheduleList.setOnItemClickListener(this);
    }



    @Override
    protected Dialog onCreateDialog(int id) {
        if(id == DIAL_ID)
            return new DatePickerDialog(this, dpickerListener, year_x, month_x, day_x);
        else
            return null;
    }

    private DatePickerDialog.OnDateSetListener dpickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            year_x = year;
            month_x = month+1;
            day_x = day;
            Toast.makeText(MealPlannerActivity.this, month_x + " / " + day_x + " / " + year_x, Toast.LENGTH_LONG).show();

            try {
                String name = scheduleStorage.getData(last_id).getString("name");

                scheduleStorage.getData(last_id).put("year", year_x);
                scheduleStorage.getData(last_id).put("month", month_x);
                scheduleStorage.getData(last_id).put("day", day_x);
                scheduleStorage.getData(last_id).put("text", name + "\n" + Integer.toString(month_x) + "/" + Integer.toString(day_x) + "/" + Integer.toString(year_x));
                scheduleStorage.refresh();
                adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i("ItemClick", "Position: " + position);

        try {
            app.viewRecipe(this, scheduleStorage.getData(position).getInt("id"));
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
            scheduleStorage.remove(selectedIndex);
            adapter.notifyDataSetChanged();
        }
        else if (option.equals(CONTEXT_OPTIONS[1])) {
            last_id = selectedIndex;
            showDialog(DIAL_ID);
        } else if (option.equals(CONTEXT_OPTIONS[2])) {
            try {
                JSONObject obj = scheduleStorage.getData(selectedIndex);

                String title = obj.getString("name");
                int id = obj.getInt("id");
                int index = favoritesStorage.findInt("id", id);

                if (index != -1) {
                    favoritesStorage.remove(index);
                } else {
                    JSONObject newFavorite = new JSONObject();
                    newFavorite.put("id", id);

                    favoritesStorage.add(title, newFavorite);
                }
            } catch (Exception e) {
                Log.e("JSON", e.toString());
            }
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

}
