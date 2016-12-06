package com.example.aharm.sporkly;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

/**
 * Created by Trevor
 */

public class MealPlannerActivity extends AppCompatActivity implements View.OnClickListener {

    Button date_display;
    int year_x, month_x, day_x;
    static final int DIAL_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meal_planner);

        final Calendar cal = Calendar.getInstance();
        year_x = cal.get(Calendar.YEAR);
        month_x = cal.get(Calendar.MONTH);
        day_x = cal.get(Calendar.DAY_OF_MONTH);

        date_display = (Button)findViewById(R.id.dateSelect);

        date_display.setOnClickListener(this);
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
            day_x = day+3;
            Toast.makeText(MealPlannerActivity.this, year_x + " / " + month_x + " / " + day_x,Toast.LENGTH_LONG).show();
        }
    };

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.dateSelect:
                Log.i("DateSelect", "clicked");
                showDialog(DIAL_ID);

                // This forces the keyboard to hide, because for some reason
                // the keyboard showing makes the search results not show.
    //            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
       //         imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                break;
            default:
                break;
        }
    }
}
