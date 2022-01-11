package com.example.foodborne;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import adapters.RecipesAdapter;
import database.SQLiteHelper;
import models.Recipe;

import java.util.ArrayList;

public class RecipeSelectActivity extends Activity implements RecipesAdapter.OnNoteListener {
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

        private final int NUM_RECETAS = 4;

        private int offset = 0; //Numero de items cargados

        private LinearLayoutManager layoutManager;
        private RecipesAdapter recipesAdapter;

        private int actualMod = -1;
        private String actualInformacion = "";

        private int paginaActual = 1;
        private int paginasTotales = 1;

        private String date;
        private int meal;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_recipe_select);

            StrictMode.enableDefaults();

            txtSearch = (EditText) findViewById(R.id.txtSearchSel);
            btnSearch = (Button) findViewById(R.id.btnSearchSel);

            btnPrev = (Button) findViewById(R.id.btnPreSel);
            btnNext = (Button) findViewById(R.id.btnNextSel);
            btnGo = (Button) findViewById(R.id.btnGoSel);
            txtPages = (EditText) findViewById(R.id.txtPagesSel);


            checkVegetariano = (CheckBox) findViewById(R.id.checkVegetarianoSel);
            checkVegano = (CheckBox) findViewById(R.id.checkVeganoSel);
            checkGluten = (CheckBox) findViewById(R.id.checkGlutenSel);
            checkLactosa = (CheckBox) findViewById(R.id.checkLactosaSel);

            swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

            recyclerView = (RecyclerView) findViewById(R.id.reciclerSel);

            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                date = extras.getString("date");
                meal = extras.getInt("meal");
            }

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

                @Override
                public void onRefresh() {
                    offset = 0;
                    actualizarRecycler(actualMod, actualInformacion, offset);
                    swipeRefreshLayout.setRefreshing(false);
                }
            });

            btnPrev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(offset >= NUM_RECETAS) {

                        offset -= NUM_RECETAS;
                        actualizarRecycler(actualMod, actualInformacion, offset);
                    }
                }
            });

            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(offset <= (NUM_RECETAS)*Integer.parseInt(txtPages.getHint().toString().split("/")[txtPages.getHint().toString().split("/").length - 1])) {
                        offset += NUM_RECETAS;
                        actualizarRecycler(actualMod, actualInformacion, offset);
                    }
                }
            });

            btnGo.setOnClickListener(new View.OnClickListener() {
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


            checkVegetariano.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkVegano.setChecked(false);
                }
            });

            checkVegano.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkVegetariano.setChecked(false);
                }
            });

            btnSearch.setOnClickListener(new View.OnClickListener() {
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

                    Toast myToast = Toast.makeText( v.getContext(), "https://api.spoonacular.com/recipes/complexSearch?" + actualInformacion, Toast.LENGTH_SHORT);
                    myToast.show();
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
            RecyclerView rvRecipes = (RecyclerView) findViewById(R.id.reciclerSel);
            rvRecipes.removeAllViews();
            ArrayList<Recipe> recipes = null;
            try {
                recipes = Recipe.createRecipesList(NUM_RECETAS, mode, offset, information, findViewById(R.id.txtPagesSel));
                recipesAdapter = new RecipesAdapter(recipes, this);
                // Attach the adapter to the recyclerview to populate items
                rvRecipes.setAdapter(recipesAdapter);
                // Set layout manager to position the items
                layoutManager = new LinearLayoutManager(this);
                rvRecipes.setLayoutManager(layoutManager);
            } catch (Exception  e) {
                Toast myToast = Toast.makeText(this, "No se han encontrado resultados", Toast.LENGTH_LONG);
                myToast.show();
                e.printStackTrace();
            }
        }

        @Override
        public void onNoteClick(int position) {
            int id = recipesAdapter.getRecetasId(position);
            Intent intent = new Intent(com.example.foodborne.RecipeSelectActivity.this, PlannerActivity.class);
            intent.putExtra("id",id);
            intent.putExtra("meal", meal);
            intent.putExtra("date", date);
            startActivity(intent);
        }
    }
