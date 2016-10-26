package com.example.aharm.sporkly;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button recipe, pantry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        recipe = (Button)findViewById(R.id.myIngredients);
        pantry = (Button)findViewById(R.id.myPantry);

        recipe.setOnClickListener(this);
        pantry.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.myIngredients:
                startActivity(new Intent(MainActivity.this, myIngredientsActivity.class));
                break;
            case R.id.myPantry:
                startActivity(new Intent(MainActivity.this, myPantryActivity.class));
                break;
            default:
                break;
        }
    }
}
