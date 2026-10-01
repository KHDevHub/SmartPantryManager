package com.example.smartpantry;

// Holds one ingredient. It is used for pantry items and for recipe ingredients.
public class Ingredient {

    private int id;
    private String name;
    private double quantity;
    private String unit;
    private String expiry; // yyyy-mm-dd, empty if the user did not add one

    public Ingredient(int id, String name, double quantity, String unit, String expiry) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiry = expiry;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }

    public String getExpiry() {
        return expiry;
    }

    // Shows 2 instead of 2.0 when the quantity is a whole number
    public String getQuantityText() {
        if (quantity == Math.floor(quantity)) {
            return String.valueOf((long) quantity);
        }
        return String.valueOf(quantity);
    }
}
