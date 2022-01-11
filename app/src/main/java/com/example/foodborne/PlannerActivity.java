package com.example.foodborne;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
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
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

import adapters.PlannerAdapter;
import adapters.RecipesAdapter;
import database.SQLiteHelper;
import models.APIUtils;
import models.Recipe;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class PlannerActivity extends AppCompatActivity {
    private ArrayList<String> meals;
    private ListView mealsView;
    private CalendarView calendar;
    private PlannerAdapter plannerAdapter;
    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planner);
        SQLiteHelper sql = new SQLiteHelper(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            sql.insertRecipeIntoDay(extras.getInt("id"), extras.getInt("meal"), extras.getString("date"));
            actualizarRecycler(extras.getString("date"));
        }


        sql.insertRecipeIntoDay(41212, 0, "20220112");
        sql.insertRecipeIntoDay(33312, 3, "20220111");
        //sql.deleteRecipeFromDay(0, "2022011");
        sql.deleteRecipeFromDay(2, "20220012");
        actualizarRecycler("20220111");
        initElements();
    }

    private void initElements() {
        calendar = findViewById(R.id.calendarView);
        Date date = new Date(calendar.getDate());
        SimpleDateFormat df2 = new SimpleDateFormat("yyyyMMdd");
        String dateText = df2.format(date);
        TextView txtDate = (TextView) findViewById(R.id.txtDate);
        txtDate.setText(dateText);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                String dia = Integer.toString(i2);
                String mes = Integer.toString(i1 + 1);
                String año = Integer.toString(i);

                if(i2 < 10) dia = "0" + dia;
                if(i1 < 10) mes = "0" + mes;

                String selected = año + mes + dia;
                TextView txtDate = (TextView) findViewById(R.id.txtDate);
                txtDate.setText(selected);
                actualizarRecycler(selected);
            }
        });
    }

    public void actualizarRecycler(String selected){
        RecyclerView rvPlanner = (RecyclerView) findViewById(R.id.recycler2);
        rvPlanner.removeAllViews();
        try {
            //recipes = Recipe.createRecipesList(NUM_RECETAS, mode, offset, information, this);
            SQLiteHelper db = new SQLiteHelper(PlannerActivity.this);
            String [] planning = db.getMeals(selected);
            db.close();
            CountDownLatch cdl = new CountDownLatch(1);
            if(planning == null) {
                //rvPlanner.setVisibility(View.GONE);
                //return;
                planning = new String[]{null, null, null, null};
            }
            ArrayList<Recipe> recipes = APIUtils.getRecipesBulk(planning, cdl);

            PlannerAdapter.OnNoteListener onNoteListenerNull = new PlannerAdapter.OnNoteListener() {
                @Override
                public void onNoteClick(int position) {
                    Intent intent = new Intent(com.example.foodborne.PlannerActivity.this, RecipeSelectActivity.class);
                    intent.putExtra("meal",position);
                    intent.putExtra("date",selected);
                    startActivity(intent);
                }
            };

            PlannerAdapter.OnNoteListener onNoteListenerImage = new PlannerAdapter.OnNoteListener() {
                @Override
                public void onNoteClick(int position) {
                    Intent intent = new Intent(com.example.foodborne.PlannerActivity.this, RecipeDetailsActivity.class);
                    intent.putExtra("id", recipes.get(position).getId());
                    startActivity(intent);
                }
            };

            plannerAdapter = new PlannerAdapter(recipes, onNoteListenerNull, onNoteListenerImage, this);
            // Attach the adapter to the recyclerview to populate items
            rvPlanner.setAdapter(plannerAdapter);
            // Set layout manager to position the items
            layoutManager = new LinearLayoutManager(this);
            rvPlanner.setLayoutManager(layoutManager);
            rvPlanner.setVisibility(View.VISIBLE);
        } catch (Exception  e) {
            Toast myToast = Toast.makeText(this, "No se han encontrado resultados", Toast.LENGTH_LONG);
            myToast.show();
            e.printStackTrace();
        }
    }
}