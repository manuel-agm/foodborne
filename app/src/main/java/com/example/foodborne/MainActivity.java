package com.example.foodborne;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.app.ActionBar.LayoutParams;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import adapters.RecipesAdapter;
import models.Recipe;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends Activity implements RecipesAdapter.OnNoteListener {

    private EditText txtSearch;
    private EditText txtPages;
    private Button btnSearch;
    private Button btnPrev;
    private Button btnNext;
    private Button btnGo;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private CheckBox checkVegetariano;
    private CheckBox checkVegano;
    private CheckBox checkGluten;
    private CheckBox checkLactosa;

    private TextView headerplanner;
    private TextView subHeaderPlanner;
    private TextView startPlanning;
    private ImageView sushiIcon;
    private Button startPlanner;

    private final int NUM_RECETAS = 4;

    private int offset = 0; //Numero de items cargados

    private LinearLayoutManager layoutManager;
    private RecipesAdapter recipesAdapter;

    private int actualMod = -1;
    private String actualInformacion = "";

    private int paginaActual = 1;
    private int paginasTotales = 1;

    @SuppressLint("ResourceType")
    private void initWidgets() {
        headerplanner = findViewById(R.id.headerplanner);
        headerplanner.setTextSize(30);
        headerplanner.setTypeface(ResourcesCompat.getFont(this,R.font.lato_black));

        subHeaderPlanner = findViewById(R.id.subheaderplanner);
        subHeaderPlanner.setTextSize(20);
        subHeaderPlanner.setTypeface(ResourcesCompat.getFont(this,R.font.lato));

        startPlanning = findViewById(R.id.startplanning);
        startPlanning.setTextSize(15);
        startPlanning.setTypeface(ResourcesCompat.getFont(this,R.font.lato_bold));

        sushiIcon = findViewById(R.id.sushiIcon);
        String uri = "@drawable/headerplanner";
        int imageResource = getResources().getIdentifier(uri, null, getPackageName());
        sushiIcon.setImageResource(imageResource);

        txtSearch = (EditText) findViewById(R.id.txtSearch);
        txtSearch.setTypeface(ResourcesCompat.getFont(this,R.font.lato));

        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnSearch.setTypeface(ResourcesCompat.getFont(this,R.font.lato_black));

        btnPrev = (Button) findViewById(R.id.btnPre);
        btnPrev.setTypeface(ResourcesCompat.getFont(this,R.font.lato_black));

        btnNext = (Button) findViewById(R.id.btnNext);
        btnNext.setTypeface(ResourcesCompat.getFont(this,R.font.lato_black));

        btnGo = (Button) findViewById(R.id.btnGo);
        btnGo.setTypeface(ResourcesCompat.getFont(this,R.font.lato_black));

        txtPages = (EditText) findViewById(R.id.txtPages);
        txtPages.setTypeface(ResourcesCompat.getFont(this,R.font.lato));

        checkVegetariano = (CheckBox) findViewById(R.id.checkVegetariano);
        checkVegetariano.setTypeface(ResourcesCompat.getFont(this,R.font.lato));

        checkVegano = (CheckBox) findViewById(R.id.checkVegano);
        checkVegano.setTypeface(ResourcesCompat.getFont(this,R.font.lato));

        checkGluten = (CheckBox) findViewById(R.id.checkGluten);
        checkGluten.setTypeface(ResourcesCompat.getFont(this,R.font.lato));

        checkLactosa = (CheckBox) findViewById(R.id.checkLactosa);
        checkLactosa.setTypeface(ResourcesCompat.getFont(this,R.font.lato));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.enableDefaults();

        initWidgets();

        startPlanner = findViewById(R.id.startplanning);
        startPlanner.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PlannerActivity.class);
                startActivity(intent);
            }
        });




        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        recyclerView = (RecyclerView) findViewById(R.id.recicler);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                offset = 0;
                actualizarRecycler(actualMod, actualInformacion, offset);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        btnPrev.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                offset -= 5;
                actualizarRecycler(actualMod, actualInformacion, offset);
            }
        });

        btnNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                offset += 5;
                actualizarRecycler(actualMod, actualInformacion, offset);
            }
        });

        btnGo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if((txtPages.getText() == null || txtPages.getText().toString() == "" || Integer.parseInt(txtPages.getHint().toString().split("/")[1]) < Integer.parseInt(txtPages.getText().toString()))){
                    Toast.makeText(v.getContext(), "Introduzca número de página válido", Toast.LENGTH_SHORT).show();
                } else {
                    offset = Integer.parseInt(txtPages.getText().toString()) * NUM_RECETAS;
                    actualizarRecycler(actualMod, actualInformacion, offset);
                }
            }
        });


        checkVegetariano.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                checkVegano.setChecked(false);
            }
        });

        checkVegano.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                checkVegetariano.setChecked(false);
            }
        });

        btnSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                actualMod = 0;
                actualInformacion = "";
                actualInformacion += "query=" + txtSearch.getText();
                if(checkVegetariano.isChecked()) actualInformacion += "&diet=vegetarian";
                if(checkVegano.isChecked()) actualInformacion += "&diet=vegetarian";
                if(checkGluten.isChecked() || checkLactosa.isChecked()) {
                    actualInformacion += "&intolerances=";

                    if(checkLactosa.isChecked()) actualInformacion += "dairy,";
                    if(checkGluten.isChecked()) actualInformacion += "gluten,";

                    actualInformacion.substring(0, actualInformacion.lastIndexOf(','));
                }
                offset = 0;
                actualizarRecycler(actualMod, actualInformacion, offset);
                //  https://api.spoonacular.com/recipes/complexSearch?query=pasta

            }
        });

        actualizarRecycler(-1, "", 0);
    }

    // mode
    // Search Recipe by Name and Filter: 0
    // Random:                           default
    private void actualizarRecycler(int mode, String information, int offset){
        RecyclerView rvRecipes = (RecyclerView) findViewById(R.id.recicler);
        rvRecipes.removeAllViews();
        ArrayList<Recipe> recipes = null;
        try {
            recipes = Recipe.createRecipesList(NUM_RECETAS, mode, offset, information, this);
        } catch (Exception  e) {
            Toast myToast = Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG);
            myToast.show();
            e.printStackTrace();
        }
        
        // Create adapter passing in the sample user data
        recipesAdapter = new RecipesAdapter(recipes, this);
        // Attach the adapter to the recyclerview to populate items
        rvRecipes.setAdapter(recipesAdapter);
        // Set layout manager to position the items
        layoutManager = new LinearLayoutManager(this);
        rvRecipes.setLayoutManager(layoutManager);
        // That's all!
    }

    @Override
    public void onNoteClick(int position) {
        int id = recipesAdapter.getRecetasId(position);
        Intent intent = new Intent(MainActivity.this, RecipeDetailsActivity.class);
        intent.putExtra("id",id);
        startActivity(intent);
    }
}