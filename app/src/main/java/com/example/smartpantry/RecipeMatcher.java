package com.example.smartpantry;

import java.util.ArrayList;
import java.util.List;

// The strict-matching logic. A recipe is only suggested when EVERY ingredient
// is in the pantry in at least the amount the recipe needs.
public class RecipeMatcher {

    public static List<Recipe> getMakeableRecipes(List<Recipe> recipes, List<Ingredient> pantry) {
        List<Recipe> result = new ArrayList<>();
        for (Recipe recipe : recipes) {
            if (canMake(recipe, pantry)) {
                result.add(recipe);
            }
        }
        return result;
    }

    // If even one ingredient is missing or short, the recipe is out
    public static boolean canMake(Recipe recipe, List<Ingredient> pantry) {
        for (Ingredient needed : recipe.getIngredients()) {
            if (!hasEnough(needed, pantry)) {
                return false;
            }
        }
        return true;
    }

    // Adds up every pantry item with the same name and unit type, then compares
    private static boolean hasEnough(Ingredient needed, List<Ingredient> pantry) {
        String neededName = cleanName(needed.getName());
        String neededType = unitType(needed.getUnit());
        double neededAmount = toBaseUnit(needed.getQuantity(), needed.getUnit());

        double total = 0;
        for (Ingredient have : pantry) {
            boolean sameName = cleanName(have.getName()).equals(neededName);
            boolean sameType = unitType(have.getUnit()).equals(neededType);
            if (sameName && sameType) {
                total += toBaseUnit(have.getQuantity(), have.getUnit());
            }
        }
        return total >= neededAmount;
    }

    // Makes names easier to compare: "Tomatoes" and "tomato" both become "tomato"
    public static String cleanName(String name) {
        String n = name.trim().toLowerCase().replaceAll("\\s+", " ");
        if (n.endsWith("ies")) {
            return n.substring(0, n.length() - 3) + "y";   // berries -> berry
        }
        if (n.endsWith("oes")) {
            return n.substring(0, n.length() - 2);          // tomatoes -> tomato
        }
        if (n.endsWith("s") && !n.endsWith("ss")) {
            return n.substring(0, n.length() - 1);          // eggs -> egg
        }
        return n;
    }

    // Units can only be compared if they measure the same kind of thing
    private static String unitType(String unit) {
        switch (unit.toLowerCase()) {
            case "g":
            case "kg":
                return "weight";
            case "ml":
            case "l":
            case "cup":
            case "tbsp":
            case "tsp":
                return "volume";
            default:
                return "count";
        }
    }

    // Converts to grams, millilitres or a plain count
    private static double toBaseUnit(double quantity, String unit) {
        switch (unit.toLowerCase()) {
            case "kg":
            case "l":
                return quantity * 1000;
            case "cup":
                return quantity * 250;
            case "tbsp":
                return quantity * 15;
            case "tsp":
                return quantity * 5;
            default:
                return quantity;
        }
    }
}
