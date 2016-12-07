package com.example.aharm.sporkly;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Toast;

import static android.icu.util.Calendar.getInstance;

/**
 * Created by Trevor Dewitt
 */

public class MealPlannerActivity extends AppCompatActivity implements  AdapterView.OnItemClickListener {
    private static final String[] CONTEXT_OPTIONS = { "Delete Entry", "Return" };

    MyApplication app;
    ListView scheduleList;
    ListStorage scheduleStorage;
    ArrayAdapter adapter;
    int year_x, month_x, day_x;
    static final int DIAL_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meal_planner);

        /*
        Calendar widget here to later implement editing the date
         */

        final Calendar cal = getInstance();
        year_x = cal.get(Calendar.YEAR);
        month_x = cal.get(Calendar.MONTH);
        day_x = cal.get(Calendar.DAY_OF_MONTH);

        app = (MyApplication)this.getApplication();

        scheduleStorage = app.getScheduleStorage();

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
            month_x = month;
            day_x = day;
            Toast.makeText(MealPlannerActivity.this, month_x + " / " + day_x + " / " + year_x,Toast.LENGTH_LONG).show();
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
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }
}
