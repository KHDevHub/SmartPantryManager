package com.example.smartpantry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

// Handles the SQLite database: creating the tables, the pantry CRUD methods
// and reading the recipes.
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "pantry.db";
    private static final int DB_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // Runs only the first time the app is opened, so the recipes are seeded once
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE pantry (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT, quantity REAL, unit TEXT, expiry TEXT)");
        db.execSQL("CREATE TABLE recipes (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT, steps TEXT)");
        db.execSQL("CREATE TABLE recipe_ingredients (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "recipe_id INTEGER, name TEXT, quantity REAL, unit TEXT)");
        seedRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS pantry");
        db.execSQL("DROP TABLE IF EXISTS recipes");
        db.execSQL("DROP TABLE IF EXISTS recipe_ingredients");
        onCreate(db);
    }

    // ---------- Pantry CRUD ----------

    // Create
    public void addItem(Ingredient item) {
        SQLiteDatabase db = getWritableDatabase();
        db.insert("pantry", null, toValues(item));
    }

    // Read (all items)
    public List<Ingredient> getAllItems() {
        List<Ingredient> list = new ArrayList<>();
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM pantry ORDER BY name", null);
        while (cursor.moveToNext()) {
            list.add(readItem(cursor));
        }
        cursor.close();
        return list;
    }

    // Read (one item, used when editing)
    public Ingredient getItem(int id) {
        Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT * FROM pantry WHERE id = ?", new String[]{String.valueOf(id)});
        Ingredient item = null;
        if (cursor.moveToFirst()) {
            item = readItem(cursor);
        }
        cursor.close();
        return item;
    }

    // Update
    public void updateItem(Ingredient item) {
        SQLiteDatabase db = getWritableDatabase();
        db.update("pantry", toValues(item), "id = ?",
                new String[]{String.valueOf(item.getId())});
    }

    // Delete
    public void deleteItem(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("pantry", "id = ?", new String[]{String.valueOf(id)});
    }

    private ContentValues toValues(Ingredient item) {
        ContentValues values = new ContentValues();
        values.put("name", item.getName());
        values.put("quantity", item.getQuantity());
        values.put("unit", item.getUnit());
        values.put("expiry", item.getExpiry());
        return values;
    }

    private Ingredient readItem(Cursor cursor) {
        return new Ingredient(cursor.getInt(0), cursor.getString(1),
                cursor.getDouble(2), cursor.getString(3), cursor.getString(4));
    }

    // ---------- Recipes ----------

    public List<Recipe> getAllRecipes() {
        List<Recipe> list = new ArrayList<>();
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM recipes ORDER BY name", null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            list.add(new Recipe(id, cursor.getString(1), cursor.getString(2),
                    getRecipeIngredients(id)));
        }
        cursor.close();
        return list;
    }

    public Recipe getRecipe(int id) {
        Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT * FROM recipes WHERE id = ?", new String[]{String.valueOf(id)});
        Recipe recipe = null;
        if (cursor.moveToFirst()) {
            recipe = new Recipe(id, cursor.getString(1), cursor.getString(2),
                    getRecipeIngredients(id));
        }
        cursor.close();
        return recipe;
    }

    private List<Ingredient> getRecipeIngredients(int recipeId) {
        List<Ingredient> list = new ArrayList<>();
        Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT name, quantity, unit FROM recipe_ingredients WHERE recipe_id = ?",
                new String[]{String.valueOf(recipeId)});
        while (cursor.moveToNext()) {
            list.add(new Ingredient(0, cursor.getString(0), cursor.getDouble(1),
                    cursor.getString(2), ""));
        }
        cursor.close();
        return list;
    }

    // ---------- Seed data ----------

    // Each ingredient is written as "name,quantity,unit"
    private void seedRecipes(SQLiteDatabase db) {
        addRecipe(db, "Scrambled Eggs",
                "Crack the eggs into a bowl and whisk with the salt.\nMelt the butter in a pan.\nPour in the eggs and stir gently until cooked.",
                "egg,2,pcs", "butter,10,g", "salt,1,tsp");

        addRecipe(db, "Cheese Toastie",
                "Butter the outside of the bread.\nPut the cheese between the slices.\nToast in a pan until golden on both sides.",
                "bread,2,pcs", "cheese,50,g", "butter,10,g");

        addRecipe(db, "Tomato Pasta",
                "Boil the pasta in salted water until soft.\nFry the garlic in oil, then add chopped tomatoes.\nCook for 10 minutes, then mix with the pasta.",
                "pasta,200,g", "tomato,3,pcs", "garlic,2,pcs", "olive oil,2,tbsp", "salt,1,tsp");

        addRecipe(db, "Egg Fried Rice",
                "Cook the rice and let it cool.\nFry the chopped onion and carrot in oil.\nAdd the rice, push to the side and scramble the eggs.\nMix everything together.",
                "rice,200,g", "egg,2,pcs", "onion,1,pcs", "carrot,1,pcs", "cooking oil,2,tbsp");

        addRecipe(db, "Pancakes",
                "Mix the flour, sugar, milk and egg into a smooth batter.\nMelt a little butter in a pan.\nPour in some batter and cook both sides until golden.",
                "flour,150,g", "milk,200,ml", "egg,1,pcs", "sugar,1,tbsp", "butter,10,g");

        addRecipe(db, "Banana Oat Porridge",
                "Heat the milk and oats in a pot for 5 minutes, stirring.\nSlice the banana on top.\nAdd the honey and serve.",
                "oats,50,g", "milk,250,ml", "banana,1,pcs", "honey,1,tbsp");

        addRecipe(db, "Mashed Potatoes",
                "Peel and boil the potatoes until soft.\nDrain them and mash with the butter and milk.\nAdd the salt.",
                "potato,4,pcs", "butter,30,g", "milk,100,ml", "salt,1,tsp");

        addRecipe(db, "Cheese Omelette",
                "Whisk the eggs with the salt.\nMelt the butter in a pan and pour in the eggs.\nAdd the cheese, fold in half and cook for a minute.",
                "egg,3,pcs", "cheese,40,g", "butter,10,g", "salt,1,tsp");

        addRecipe(db, "Tuna Mayo Sandwich",
                "Drain the tuna and mix it with the mayonnaise.\nSpread it on a slice of bread and top with the other slice.",
                "bread,2,pcs", "tuna,1,pcs", "mayonnaise,2,tbsp");

        addRecipe(db, "Chicken and Rice",
                "Fry the chopped onion and garlic in oil.\nAdd the chicken pieces and salt and cook until brown.\nAdd the rice and enough water to cover it.\nCover and simmer for 20 minutes.",
                "chicken,300,g", "rice,200,g", "onion,1,pcs", "garlic,2,pcs", "salt,1,tsp", "cooking oil,1,tbsp");

        addRecipe(db, "Garlic Bread",
                "Mash the butter with the crushed garlic.\nSpread it on the bread.\nBake at 180 degrees for 10 minutes.",
                "bread,4,pcs", "butter,40,g", "garlic,2,pcs");

        addRecipe(db, "Peanut Butter Banana Toast",
                "Toast the bread.\nSpread on the peanut butter.\nTop with sliced banana.",
                "bread,2,pcs", "peanut butter,2,tbsp", "banana,1,pcs");

        addRecipe(db, "Vegetable Soup",
                "Chop the carrots, potatoes and onion.\nFry the onion in oil, then add the rest with water and salt.\nSimmer for 25 minutes.",
                "carrot,2,pcs", "potato,2,pcs", "onion,1,pcs", "salt,1,tsp", "cooking oil,1,tbsp");

        addRecipe(db, "Bean Stew",
                "Fry the onion and garlic in oil.\nAdd the chopped tomatoes and cook for 5 minutes.\nAdd the beans and salt and simmer for 15 minutes.",
                "beans,1,pcs", "tomato,2,pcs", "onion,1,pcs", "garlic,1,pcs", "cooking oil,1,tbsp", "salt,1,tsp");

        addRecipe(db, "Lemon Yogurt Dessert",
                "Squeeze the lemon juice into the yogurt.\nStir in the honey.\nChill for 10 minutes and serve.",
                "yogurt,250,g", "lemon,1,pcs", "honey,1,tbsp");

        addRecipe(db, "French Toast",
                "Whisk the eggs, milk and sugar together.\nDip the bread in the mix.\nFry in butter until golden on both sides.",
                "bread,4,pcs", "egg,2,pcs", "milk,100,ml", "sugar,1,tbsp", "butter,10,g");

        addRecipe(db, "Cheesy Pasta",
                "Boil the pasta until soft and drain.\nMelt the butter with the milk in the pot.\nAdd the cheese and pasta and stir until creamy.",
                "pasta,200,g", "cheese,80,g", "butter,20,g", "milk,100,ml");

        addRecipe(db, "Tomato Soup",
                "Chop the tomatoes and onion.\nFry the onion in butter, then add the tomatoes and salt.\nSimmer for 20 minutes and blend until smooth.",
                "tomato,6,pcs", "onion,1,pcs", "butter,20,g", "salt,1,tsp");

        addRecipe(db, "Oat Cookies",
                "Mix the butter and sugar, then add the egg.\nStir in the oats and flour.\nScoop onto a tray and bake at 180 degrees for 12 minutes.",
                "oats,100,g", "flour,50,g", "sugar,50,g", "butter,50,g", "egg,1,pcs");

        addRecipe(db, "Honey Lemon Tea",
                "Pour hot water into a cup.\nSqueeze in the lemon juice.\nStir in the honey.",
                "lemon,1,pcs", "honey,1,tbsp");
    }

    private void addRecipe(SQLiteDatabase db, String name, String steps, String... ingredients) {
        ContentValues recipeValues = new ContentValues();
        recipeValues.put("name", name);
        recipeValues.put("steps", steps);
        long recipeId = db.insert("recipes", null, recipeValues);

        for (String text : ingredients) {
            String[] parts = text.split(",");
            ContentValues values = new ContentValues();
            values.put("recipe_id", recipeId);
            values.put("name", parts[0]);
            values.put("quantity", Double.parseDouble(parts[1]));
            values.put("unit", parts[2]);
            db.insert("recipe_ingredients", null, values);
        }
    }
}
