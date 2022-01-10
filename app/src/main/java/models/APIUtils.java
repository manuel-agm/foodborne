package models;

import com.example.foodborne.PlannerActivity;
import com.example.foodborne.RecipeDetailsActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class APIUtils {
    final static String API_KEY = "9fe1d7086ba94d9c887a4cf647acf753";

    private static String name = "";

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
}
