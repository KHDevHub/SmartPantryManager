package com.example.smartpantry;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

// Pantry list screen. This is the first screen the user sees.
public class MainActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private PantryAdapter adapter;
    private TextView textEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);
        textEmpty = findViewById(R.id.textEmpty);
        RecyclerView recyclerView = findViewById(R.id.recyclerPantry);
        Button buttonAdd = findViewById(R.id.buttonAdd);

        adapter = new PantryAdapter(new ArrayList<>(), new PantryAdapter.Listener() {
            @Override
            public void onEdit(Ingredient item) {
                // Send the item's id to the edit screen with an Intent
                Intent intent = new Intent(MainActivity.this, AddEditActivity.class);
                intent.putExtra("item_id", item.getId());
                startActivity(intent);
            }

            @Override
            public void onDelete(Ingredient item) {
                confirmDelete(item);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        buttonAdd.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AddEditActivity.class)));
    }

    // Reload the list every time we come back to this screen
    @Override
    protected void onResume() {
        super.onResume();
        loadItems();
    }

    private void loadItems() {
        List<Ingredient> items = db.getAllItems();
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        boolean showAlerts = prefs.getBoolean("expiry_alerts", true);
        adapter.setItems(items, showAlerts);

        if (items.isEmpty()) {
            textEmpty.setVisibility(View.VISIBLE);
        } else {
            textEmpty.setVisibility(View.GONE);
        }
    }

    private void confirmDelete(Ingredient item) {
        new AlertDialog.Builder(this)
                .setTitle("Delete ingredient")
                .setMessage("Remove " + item.getName() + " from your pantry?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    db.deleteItem(item.getId());
                    loadItems();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.menu_suggested) {
            startActivity(new Intent(this, SuggestedActivity.class));
            return true;
        }
        if (id == R.id.menu_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
