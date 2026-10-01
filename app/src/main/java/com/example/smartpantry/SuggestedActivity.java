package com.example.smartpantry;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// Shows only the recipes the user can make with what is in their pantry
public class SuggestedActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private RecyclerView recyclerView;
    private TextView textEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested);

        db = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.recyclerSuggested);
        textEmpty = findViewById(R.id.textEmpty);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();

        List<Ingredient> pantry = db.getAllItems();
        List<Recipe> allRecipes = db.getAllRecipes();
        List<Recipe> suggested = RecipeMatcher.getMakeableRecipes(allRecipes, pantry);

        recyclerView.setAdapter(new RecipeAdapter(suggested, recipe -> {
            Intent intent = new Intent(SuggestedActivity.this, RecipeDetailActivity.class);
            intent.putExtra("recipe_id", recipe.getId());
            startActivity(intent);
        }));

        // Friendly message instead of a blank screen
        if (suggested.isEmpty()) {
            textEmpty.setVisibility(View.VISIBLE);
        } else {
            textEmpty.setVisibility(View.GONE);
        }
    }
}
