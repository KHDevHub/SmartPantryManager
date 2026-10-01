# Smart Pantry Manager

A Java Android app that helps cut food waste. You add the ingredients you already have at home, and the app only suggests recipes you can cook with those ingredients. No shopping trip needed.

Built for Mobile App Development 700 (Richfield).

## What the app does

- Add, edit and delete pantry items (name, quantity, unit and an optional expiry date)
- Pantry list screen (RecyclerView with a custom adapter)
- 20 recipes stored in the database and loaded the first time the app runs
- Suggested Recipes screen using a strict-matching rule: a recipe is only shown if **every** ingredient is in the pantry in at least the amount needed
- Matching ignores simple differences like "tomato" vs "tomatoes" and converts units (for example 0.5 kg counts as 500 g)
- Recipe detail screen with the ingredients and method
- Settings screen with a toggle that highlights items expiring within 3 days
- A message is shown when no recipes match the pantry

The app does not use maps, GPS or any location features.

## Database choice: SQLite

I chose SQLite (using `SQLiteOpenHelper`) because it is built into Android, works offline and needs no extra setup or accounts. The pantry and recipes are small and simple, so a local database fits well. The data is saved on the phone, so it is still there after the app is closed and reopened.

Tables:
- `pantry` (id, name, quantity, unit, expiry)
- `recipes` (id, name, steps)
- `recipe_ingredients` (id, recipe_id, name, quantity, unit)

## How to run

1. Install Android Studio.
2. Clone the repository: `git clone <your-repo-link>`
3. In Android Studio choose **File > Open** and select the project folder.
4. Wait for Gradle to finish syncing (it may ask to download some files).
5. Create an emulator in Device Manager, or plug in an Android phone with USB debugging on.
6. Press the green **Run** button.

## Project structure

| File | Purpose |
|---|---|
| `MainActivity` | Pantry list, navigation menu |
| `AddEditActivity` | Add or edit an ingredient, with validation |
| `SuggestedActivity` | Lists the recipes that pass the strict match |
| `RecipeDetailActivity` | Shows one recipe |
| `SettingsActivity` | Expiry highlight toggle |
| `DatabaseHelper` | SQLite tables, CRUD and recipe seed data |
| `RecipeMatcher` | The strict-matching logic |
| `PantryAdapter`, `RecipeAdapter` | RecyclerView adapters |
