package com.example.smartpantry;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

// Shows the full ingredient list and method for one recipe
public class RecipeDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        int recipeId = getIntent().getIntExtra("recipe_id", -1);
        Recipe recipe = new DatabaseHelper(this).getRecipe(recipeId);
        if (recipe == null) {
            finish();
            return;
        }

        StringBuilder ingredientText = new StringBuilder();
        for (Ingredient ingredient : recipe.getIngredients()) {
            ingredientText.append("- ")
                    .append(ingredient.getQuantityText()).append(" ")
                    .append(ingredient.getUnit()).append(" ")
                    .append(ingredient.getName()).append("\n");
        }

        // Number the steps
        String[] steps = recipe.getSteps().split("\n");
        StringBuilder stepText = new StringBuilder();
        for (int i = 0; i < steps.length; i++) {
            stepText.append(i + 1).append(". ").append(steps[i]).append("\n");
        }

        ((TextView) findViewById(R.id.textRecipeTitle)).setText(recipe.getName());
        ((TextView) findViewById(R.id.textIngredients)).setText(ingredientText.toString());
        ((TextView) findViewById(R.id.textSteps)).setText(stepText.toString());
    }
}
