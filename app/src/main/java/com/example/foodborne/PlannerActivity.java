package com.example.foodborne;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ListView;

import java.util.ArrayList;

import database.SQLiteHelper;

public class PlannerActivity extends AppCompatActivity {
    ArrayList<String> meals;
    ListView mealsList;
    CalendarView calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planner);
        SQLiteHelper db = new SQLiteHelper(PlannerActivity.this);

        calendar = findViewById(R.id.calendarView);

        mealsList = findViewById(R.id.meals);
        meals = new ArrayList<>();
        meals.add(getString(R.string.breakfast));
        meals.add(getString(R.string.launch));
        meals.add(getString(R.string.snack));
        meals.add(getString(R.string.dinner));

        ArrayAdapter mealsAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,meals);
        mealsList.setAdapter(mealsAdapter);

        mealsList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                db.insertRecipeIntoDay(600, 1, (int)calendar.getDate());
            }
        });
        mealsAdapter.notifyDataSetChanged();


    }
}