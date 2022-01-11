package com.example.foodborne;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

import models.APIUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RecipeDetailsActivity extends AppCompatActivity {
    TextView instructionsText;
    String respuesta;
    ImageView isVegan;
    ImageView recipeImage;
    TextView recipeTitle;
    TextView timeNeeded;
    TextView servings;
    TextView cal;
    TextView protein;
    TextView carbs;
    TextView fat;
    TextView ingredients;
    Button goBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);
        initElements();
        getRecipeDetails(getRecipeId());
    }

    private int getRecipeId() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            return extras.getInt("id");
        } else {
            return 0;
        }
    }

    private void initElements() {
        instructionsText = findViewById(R.id.instructionsText);
        recipeImage = findViewById(R.id.recipeImage);
        isVegan = findViewById(R.id.isVegan);
        recipeTitle = findViewById(R.id.recipeTitle);
        timeNeeded = findViewById(R.id.timeNeeded);
        servings = findViewById(R.id.servings);
        cal = findViewById(R.id.cal);
        carbs = findViewById(R.id.carbs);
        protein = findViewById(R.id.protein);
        fat = findViewById(R.id.fat);
        ingredients = findViewById(R.id.ingredientsInformation);
        goBack = findViewById(R.id.goback);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecipeDetailsActivity.this.finish();
                /*Intent intent = new Intent(RecipeDetailsActivity.this, MainActivity.class);
                startActivity(intent);*/
            }
        });
    }

    private void getRecipeDetails(int recipeID) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.spoonacular.com/recipes/" + recipeID + "/information?apiKey=" + APIUtils.API_KEY + "&includeNutrition=true")
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    respuesta = response.body().string();
                    RecipeDetailsActivity.this.runOnUiThread(() -> {
                        try {
                            JSONObject json = new JSONObject(respuesta);
                            if (!json.getBoolean("vegan")) {
                                isVegan.setImageResource(R.drawable.nonvegan);
                            } else {
                                isVegan.setImageResource(R.drawable.vegan);
                            }
                            Picasso.get().load(json.getString("image")).into(recipeImage);
                            recipeTitle.setText(json.getString("title"));
                            timeNeeded.setText(json.getInt("readyInMinutes") + " " + getString(R.string.minutes));
                            servings.setText(json.getInt("servings") + " " + getString(R.string.servings));
                            String instructions = json.getString("instructions");
                            if (instructions.equals("null") || instructions == null) {
                                instructionsText.setText(getString(R.string.none));
                            } else {
                                instructionsText.setText(instructions);
                            }
                            JSONObject nutrition = json.getJSONObject("nutrition");
                            JSONArray nutrients = nutrition.getJSONArray("nutrients");
                            cal.setText(nutrients.getJSONObject(0).getString("amount") + " " + getString(R.string.kcal));
                            fat.setText(getString(R.string.fat) + ": " + nutrients.getJSONObject(1).getString("amount") + "g (" + nutrients.getJSONObject(2).getString("amount") + "g " + getString(R.string.saturated) + ")");
                            protein.setText(getString(R.string.protein) + ": " + nutrients.getJSONObject(8).getString("amount") + "g");
                            carbs.setText(getString(R.string.carbs) + ": " + nutrients.getJSONObject(3).getString("amount") + "g (" + nutrients.getJSONObject(5).getString("amount") + "g " + getString(R.string.sugar) + ")");
                            JSONArray ingredientsJSON = json.getJSONArray("extendedIngredients");
                            StringBuilder sb = new StringBuilder();
                            for (int i = 0; i < ingredientsJSON.length(); i++) {
                                sb.append("\n");
                                JSONObject ingredient = ingredientsJSON.getJSONObject(i);
                                sb.append(ingredient.getString("originalString"));
                                sb.append("\n");
                            }
                            ingredients.setText(sb.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
                }
                response.close();
            }
        });
    }
}