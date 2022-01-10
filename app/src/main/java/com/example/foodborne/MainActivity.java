package com.example.foodborne;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.os.Bundle;
import android.app.ActionBar.LayoutParams;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import adapters.RecipesAdapter;
import models.Recipe;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends Activity {

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

    private final int NUM_RECETAS = 5;

    private int offset = 0; //Numero de items cargados

    private LinearLayoutManager layoutManager;
    private RecipesAdapter recipesAdapter;

    private int actualMod = -1;
    private String actualInformacion = "";

    private int paginaActual = 1;
    private int paginasTotales = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.enableDefaults();

        txtSearch = (EditText) findViewById(R.id.txtSearch);
        btnSearch = (Button) findViewById(R.id.btnSearch);

        btnPrev = (Button) findViewById(R.id.btnPre);
        btnNext = (Button) findViewById(R.id.btnNext);
        btnGo = (Button) findViewById(R.id.btnGo);
        txtPages = (EditText) findViewById(R.id.txtPages);


        checkVegetariano = (CheckBox) findViewById(R.id.checkVegetariano);
        checkVegano = (CheckBox) findViewById(R.id.checkVegano);
        checkGluten = (CheckBox) findViewById(R.id.checkGluten);
        checkLactosa = (CheckBox) findViewById(R.id.checkLactosa);

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
        recipesAdapter = new RecipesAdapter(recipes);
        // Attach the adapter to the recyclerview to populate items
        rvRecipes.setAdapter(recipesAdapter);
        // Set layout manager to position the items
        layoutManager = new LinearLayoutManager(this);
        rvRecipes.setLayoutManager(layoutManager);
        // That's all!
    }
}