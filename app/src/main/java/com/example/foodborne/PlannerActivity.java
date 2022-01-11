package com.example.foodborne;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

import adapters.PlannerAdapter;
import database.SQLiteHelper;

import models.APIUtils;
import models.Recipe;

public class PlannerActivity extends AppCompatActivity {
    private ArrayList<String> meals;
    private ListView mealsView;
    private CalendarView calendar;
    private PlannerAdapter plannerAdapter;
    private LinearLayoutManager layoutManager;
    Button goBack;

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
        initElements();
    }

    //INICIALIZAMOS
    private void initElements() {
        calendar = findViewById(R.id.calendarView);
        Date date = new Date(calendar.getDate());
        SimpleDateFormat df2 = new SimpleDateFormat("yyyyMMdd");
        String dateText = df2.format(date);
        TextView txtDate = (TextView) findViewById(R.id.txtDate);
        txtDate.setText(dateText);
        goBack = findViewById(R.id.goback);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PlannerActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
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

    //ACTUALIZAMOS LA LISTA DE COMIDAS
    public void actualizarRecycler(String selected){
        RecyclerView rvPlanner = (RecyclerView) findViewById(R.id.recycler2);
        rvPlanner.removeAllViews();
        try {
            SQLiteHelper db = new SQLiteHelper(PlannerActivity.this);
            String [] planning = db.getMeals(selected);
            db.close();
            CountDownLatch cdl = new CountDownLatch(1);
            if(planning == null) {
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
            rvPlanner.setAdapter(plannerAdapter);
            layoutManager = new LinearLayoutManager(this);
            rvPlanner.setLayoutManager(layoutManager);
            rvPlanner.setVisibility(View.VISIBLE);
        } catch (Exception  e) {
            Toast myToast = Toast.makeText(this, getString(R.string.noresults), Toast.LENGTH_LONG);
            myToast.show();
            e.printStackTrace();
        }
    }
}