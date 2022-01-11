package models;

import com.example.foodborne.PlannerActivity;
import com.example.foodborne.RecipeDetailsActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class APIUtils {
    //final static String API_KEY = "9fe1d7086ba94d9c887a4cf647acf753"; // GOMEZ
    //final static String API_KEY = "01e611e7d4954ce39cd97fb5312e23cd"; // ROLDAN 2
    public final static String API_KEY = "b8efe4ac504b4e66a294db52cbb7c152";

    private static String name = "";
    private static ArrayList<Recipe> recipesPlanner = new ArrayList<Recipe>();

    public static String getRecipeNameById(int id, CountDownLatch cdl) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.spoonacular.com/recipes/" + id + "/information?apiKey=" + API_KEY + "&includeNutrition=false")
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                cdl.countDown();
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody r = response.body()) {
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected code " + response);
                    } else {
                        assert response.body() != null;
                        String res = response.body().string();
                        try {
                            JSONObject json = new JSONObject(res);
                            name = json.getString("title");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                cdl.countDown();
            }
        });
        try {
            cdl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return name;
    }

    public static ArrayList<Recipe> getRecipesBulk(String[] planning, CountDownLatch cdl) {
        OkHttpClient client = new OkHttpClient();
        ArrayList<Integer> nulls = new ArrayList<Integer>();
        recipesPlanner.clear();
        String url = "https://api.spoonacular.com/recipes/informationBulk?ids=";
        for (int i = 0; i < planning.length; i++) {
            if (planning[i] != null) {
                url += planning[i] + ",";
            } else {
                nulls.add(i);
            }
        }
        if(nulls.size() == 4) {
            for(int i = 0; i < 4 ; i++){
                recipesPlanner.add(null);
            }
            return recipesPlanner;
        }
        url = url.substring(0, url.lastIndexOf(','));
            Request request = new Request.Builder()
                    .url(url + "&apiKey=" + API_KEY + "&includeNutrition=false")
                    .get()
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    cdl.countDown();
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try (ResponseBody r = response.body()) {
                        assert response.body() != null;
                        String res = response.body().string();
                        try {
                            int offset = 0;
                            JSONArray json = new JSONArray(res);
                            JSONObject obj;
                            for (int i = 0; recipesPlanner.size() <= 4; i++) {
                                if (nulls.contains(i)) {
                                    recipesPlanner.add(null);
                                    offset++;
                                } else {
                                    obj = json.getJSONObject(i - offset);
                                    recipesPlanner.add(new Recipe(obj.getInt("id"), obj.getString("title"), obj.getString("image")));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    cdl.countDown();
                }
            });
            try {
                cdl.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return recipesPlanner;
        }
    }
