package com.example.aharm.sporkly;

import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Date;

/**
 * Created by aharm on 12/7/2016.
 */

public class MilkMan extends AppCompatActivity implements View.OnClickListener {

    MyApplication app;

    private Button milkDateButton;
    private TextView bad_yn;
    private EditText milkDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.milk_man);

        milkDateButton = (Button) findViewById(R.id.findMeMilk);
        bad_yn = (TextView) findViewById(R.id.bad_yn);
        milkDate = (EditText) findViewById(R.id.milkDate);

        milkDateButton.setOnClickListener(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.findMeMilk:
                onMilkButtonClick();
                break;
            default:
                break;
        }
    }

    public void onMilkButtonClick() {
        String isGood = "";
        SimpleDateFormat format =  new SimpleDateFormat("MM/dd/yyyy");

        try {
            Date expire = format.parse(milkDate.getText().toString());

            if (expire.after(new Date())) {
                isGood = "milk is good";
            }
            else {
                isGood = "milk is bad";
            }
            Log.d("entered date: ", expire.toString());
            Log.e("new Date: ", new Date().toString());
        } catch (Exception e) {
            isGood = "date must be in mm/dd/yyyy format";
            Log.e("exception: ", e.toString());
        }
        bad_yn.setText(isGood);
    }

}
