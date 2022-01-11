package com.example.foodborne;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.app.Activity;
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

public class PlannerActivity extends AppCompatActivity{
    final static String API_KEY = "b8efe4ac504b4e66a294db52cbb7c152";
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
                    meals.add(getString(R.string.lunch)+ ": -");
                    meals.add(getString(R.string.snack)+ ": -");
                    meals.add(getString(R.string.dinner)+ ": -");
                } else {
                    CountDownLatch cdl = new CountDownLatch(1);
                    if (planning[0] != null) {

                        meals.add(getString(R.string.breakfast)+ ": " + APIUtils.getRecipeNameById(Integer.parseInt(planning[0]), cdl));
                    } else {
                        meals.add(getString(R.string.breakfast)+ ": -");
                    }
                    if (planning[1] != null) {
                        meals.add(getString(R.string.lunch) + ": " + APIUtils.getRecipeNameById(Integer.parseInt(planning[1]), cdl));
                    } else {
                        meals.add(getString(R.string.lunch)+ ": -");
                    }
                    if (planning[2] != null) {
                        meals.add(getString(R.string.snack)+ ": " + APIUtils.getRecipeNameById(Integer.parseInt(planning[2]), cdl));
                    } else {
                        meals.add(getString(R.string.snack)+ ": -");
                    }
                    if (planning[3] != null) {
                        meals.add(getString(R.string.dinner) + ": "+ APIUtils.getRecipeNameById(Integer.parseInt(planning[3]), cdl));
                    } else {
                        meals.add(getString(R.string.dinner)+ ": -");
                    }
                }
                mealsView.setAdapter(mealsAdapter);
            }
        });
    }
}