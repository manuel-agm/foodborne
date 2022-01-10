package com.example.foodborne;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.CountDownLatch;

import database.SQLiteHelper;
import models.APIUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class PlannerActivity extends AppCompatActivity {
    final static String API_KEY = "9fe1d7086ba94d9c887a4cf647acf753";
    ArrayList<String> meals;
    ListView mealsView;
    CalendarView calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planner);
        initElements();
    }

    private void initElements() {
        SQLiteHelper db = new SQLiteHelper(PlannerActivity.this);
        mealsView = findViewById(R.id.meals);
        meals = new ArrayList<>();
        ArrayAdapter mealsAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,meals) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTypeface(ResourcesCompat.getFont(getContext(),R.font.lato));
                text.setTextSize(20);
                text.setTextColor(Color.BLACK);
                return view;
            }
        };
        calendar = findViewById(R.id.calendarView);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                String selected = Integer.toString(i) + Integer.toString(i1) + Integer.toString(i2);
                String [] planning = db.getMeals(selected);
                meals.clear();
                if (planning == null) {
                    meals.add(getString(R.string.breakfast)+ ": -");
                    meals.add(getString(R.string.launch)+ ": -");
                    meals.add(getString(R.string.snack)+ ": -");
                    meals.add(getString(R.string.dinner)+ ": -");
                } else {

                    if (planning[0] != null) {

                        meals.add(getString(R.string.breakfast)+ ": " + planning[0]);
                    } else {
                        meals.add(getString(R.string.breakfast)+ ": -");
                    }
                    if (planning[1] != null) {
                        meals.add(getString(R.string.launch) + ": " + planning[1]);
                    } else {
                        meals.add(getString(R.string.launch)+ ": -");
                    }
                    if (planning[2] != null) {
                        meals.add(getString(R.string.snack)+ ": " + planning[2]);
                    } else {
                        meals.add(getString(R.string.snack)+ ": -");
                    }
                    if (planning[3] != null) {
                        meals.add(getString(R.string.dinner) + ": "+ planning[3]);
                    } else {
                        meals.add(getString(R.string.dinner)+ ": -");
                    }
                }
                mealsView.setAdapter(mealsAdapter);
            }
        });
    }
}