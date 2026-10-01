package com.example.smartpantry;

import java.util.List;

// Holds one recipe with its list of required ingredients.
public class Recipe {

    private int id;
    private String name;
    private String steps;
    private List<Ingredient> ingredients;

    public Recipe(int id, String name, String steps, List<Ingredient> ingredients) {
        this.id = id;
        this.name = name;
        this.steps = steps;
        this.ingredients = ingredients;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSteps() {
        return steps;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }
}
