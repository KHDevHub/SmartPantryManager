package com.example.smartpantry;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

// Used for both adding a new ingredient and editing an existing one
public class AddEditActivity extends AppCompatActivity {

    private final String[] units = {"g", "kg", "ml", "l", "cup", "tbsp", "tsp", "pcs"};

    private DatabaseHelper db;
    private EditText editName, editQuantity, editExpiry;
    private Spinner spinnerUnit;
    private int itemId = -1; // -1 means we are adding a new item

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        db = new DatabaseHelper(this);
        editName = findViewById(R.id.editName);
        editQuantity = findViewById(R.id.editQuantity);
        editExpiry = findViewById(R.id.editExpiry);
        spinnerUnit = findViewById(R.id.spinnerUnit);
        Button buttonSave = findViewById(R.id.buttonSave);

        spinnerUnit.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, units));

        // If an item id was sent with the Intent, we are editing
        itemId = getIntent().getIntExtra("item_id", -1);
        if (itemId != -1) {
            setTitle("Edit Ingredient");
            fillForm();
        } else {
            setTitle("Add Ingredient");
        }

        buttonSave.setOnClickListener(v -> saveItem());
    }

    private void fillForm() {
        Ingredient item = db.getItem(itemId);
        if (item == null) {
            return;
        }
        editName.setText(item.getName());
        editQuantity.setText(item.getQuantityText());
        editExpiry.setText(item.getExpiry());
        for (int i = 0; i < units.length; i++) {
            if (units[i].equals(item.getUnit())) {
                spinnerUnit.setSelection(i);
            }
        }
    }

    private void saveItem() {
        String name = editName.getText().toString().trim();
        String quantityText = editQuantity.getText().toString().trim();
        String expiry = editExpiry.getText().toString().trim();

        // Validation
        if (name.isEmpty()) {
            editName.setError("Please enter a name");
            return;
        }
        if (quantityText.isEmpty()) {
            editQuantity.setError("Please enter a quantity");
            return;
        }
        double quantity;
        try {
            quantity = Double.parseDouble(quantityText);
        } catch (NumberFormatException e) {
            editQuantity.setError("Enter a valid number");
            return;
        }
        if (quantity <= 0) {
            editQuantity.setError("Quantity must be more than 0");
            return;
        }
        if (!expiry.isEmpty() && !isValidDate(expiry)) {
            editExpiry.setError("Use the format yyyy-mm-dd");
            return;
        }

        String unit = spinnerUnit.getSelectedItem().toString();

        if (itemId == -1) {
            db.addItem(new Ingredient(0, name, quantity, unit, expiry));
            Toast.makeText(this, "Ingredient added", Toast.LENGTH_SHORT).show();
        } else {
            db.updateItem(new Ingredient(itemId, name, quantity, unit, expiry));
            Toast.makeText(this, "Ingredient updated", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private boolean isValidDate(String text) {
        if (text.length() != 10) {
            return false;
        }
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            format.setLenient(false);
            format.parse(text);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}
