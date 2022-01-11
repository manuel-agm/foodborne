package models;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.example.foodborne.MainActivity;
import com.example.foodborne.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Recipe {
    private int id;
    private String title;
    private String image;

    private static final String API_KEY = "b8efe4ac504b4e66a294db52cbb7c152";
    private static final int MODE_SEARCH = 0;

    private static String respuesta = "";
    private static int offset = 0;

    public Recipe(int id, String title, String image){
        this.id = id;
        this.title = title;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    private static int lastContactId = 0;

    public static ArrayList<Recipe> createRecipesList(int numRecipes, int mode, int offset, String information, MainActivity mainActivity) throws JSONException, InterruptedException {
        ArrayList<Recipe> recipes = new ArrayList<Recipe>();


        CountDownLatch countDownLatch = new CountDownLatch(1);

        String jsonArrayName = "";

        switch(mode){
            case MODE_SEARCH: // Search Recipe by Name and Filter
                jsonArrayName = "results";
                break;

            default: // Random
                jsonArrayName = "recipes";
        }
        getRecipes(5, information, mode, offset, countDownLatch);

        countDownLatch.await();

        JSONObject rootObj = new JSONObject(respuesta);

        JSONArray recipesJSON = rootObj.getJSONArray(jsonArrayName);

        for(int i = 0; i <numRecipes;i++) {
            JSONObject object = recipesJSON.getJSONObject(i);

            recipes.add(new Recipe(object.getInt("id"), object.getString("title"), object.getString("image").split("/")[object.getString("image").split("/").length-1]));
        }

        if(mode == MODE_SEARCH){
            EditText txtPages = (EditText) mainActivity.findViewById(R.id.txtPages);
            txtPages.setHint(((int)Math.floor(offset/numRecipes)+1 + "/" + (int)Math.round(rootObj.getInt("totalResults")/numRecipes)));
        }

        //https://api.spoonacular.com/recipes/random?number=1&tags=vegetarian,dessert&apiKey=65757ee1a05f42769b1ec419f5a10cb2

        /*for(int i = 1; i <=numRecipes;i++) {
            Receta recipe = new Receta(i, "aguacate", 10*i, "Cajun-Spiced-Black-Bean-and-Sweet-Potato-Burgers-227961.jpg");
            recipes.add(recipe);
        }*/

        return recipes;
    }

    private static void getRecipes(int numRecipes, String information, int mode, int offset, CountDownLatch countDownLatch){
        String modeString;
        switch (mode) {
            case MODE_SEARCH: modeString = "complexSearch";
                break;
            default: modeString = "random";
        }

        if(offset > 0){
            information += "&offset=" + offset;
        }

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.spoonacular.com/recipes/" + modeString + "?number=" + numRecipes + "&" + information + "&apiKey=" + API_KEY)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                respuesta = "";
                e.printStackTrace();
                countDownLatch.countDown();
            }

            @Override
            public void onResponse(@NonNull Call call, Response response) throws IOException {

                respuesta = response.body().string();
                response.close();
                countDownLatch.countDown();
            }
        });
    }
}