package models;

import java.util.List;

public class RecipeDetails {
    //GET Recipe Information
    boolean isVegan;
    int servings;
    String title;
    String image;
    String instructions;
    int time;
    List<String> ingredients;

    //GET Search Recipes by Nutrients
    int calories;
    int carbs;
    int protein;
    int fat;

    public RecipeDetails(boolean isVegan, int servings, String title, String image, String instructions,
                         int time, List<String> ingredients, int calories, int carbs, int protein, int fat) {
        this.isVegan = isVegan;
        this.servings = servings;
        this.title = title;
        this.image = image;
        this.instructions = instructions;
        this.time = time;
        this.ingredients = ingredients;
        this.calories = calories;
        this.carbs = carbs;
        this.protein = protein;
        this.fat = fat;
    }

    public boolean isVegan() {
        return isVegan;
    }

    public void setVegan(boolean vegan) {
        isVegan = vegan;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public int getCarbs() {
        return carbs;
    }

    public void setCarbs(int carbs) {
        this.carbs = carbs;
    }

    public int getProtein() {
        return protein;
    }

    public void setProtein(int protein) {
        this.protein = protein;
    }

    public int getFat() {
        return fat;
    }

    public void setFat(int fat) {
        this.fat = fat;
    }
}
