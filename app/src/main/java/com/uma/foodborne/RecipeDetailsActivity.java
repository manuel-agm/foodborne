package com.uma.foodborne;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.textclassifier.ConversationActions;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import models.RecipeDetails;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RecipeDetailsActivity extends AppCompatActivity {
    final static String API_KEY = "9fe1d7086ba94d9c887a4cf647acf753";
    int recipeID = 600;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);
        initElements();
        getRecipeDetails(recipeID);
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
    }

    private void getRecipeDetails(int recipeID) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.spoonacular.com/recipes/" + recipeID + "/information?apiKey=" + API_KEY + "&includeNutrition=true")
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    respuesta = response.body().string();
                    RecipeDetailsActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject json = new JSONObject(respuesta);
                                if (json.getBoolean("vegan") == false) {
                                    isVegan.setImageResource(R.drawable.nonvegan);
                                } else {
                                    isVegan.setImageResource(R.drawable.vegan);
                                }
                                Picasso.get().load(json.getString("image")).into(recipeImage);
                                recipeTitle.setText(json.getString("title"));
                                timeNeeded.setText(json.getInt("readyInMinutes") + " minutes");
                                servings.setText(json.getInt("servings") + " servings");
                                String instructions = json.getString("instructions");
                                if (instructions.equals("null")) {
                                    instructionsText.setText("None");
                                } else {
                                    instructionsText.setText(instructions);
                                }
                                JSONObject nutrition = json.getJSONObject("nutrition");
                                JSONArray nutrients = nutrition.getJSONArray("nutrients");
                                cal.setText(nutrients.getJSONObject(0).getString("amount") + " kcal/100g");
                                fat.setText("Fat: " + nutrients.getJSONObject(1).getString("amount") + "g (" + nutrients.getJSONObject(2).getString("amount") + "g saturated)");
                                protein.setText("Protein: " + nutrients.getJSONObject(8).getString("amount") + "g");
                                carbs.setText("Carbohydrates: " + nutrients.getJSONObject(3).getString("amount") + "g (" + nutrients.getJSONObject(5).getString("amount") + "g of sugar)");
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

                        }
                    });
                }
                response.close();
            }
        });
    }
}