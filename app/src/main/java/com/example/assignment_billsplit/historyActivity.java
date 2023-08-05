package com.example.assignment_billsplit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class historyActivity extends AppCompatActivity {

    private ListView listView;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Initialize TabLayout and select the "History" tab
        TabLayout tabLayout = findViewById(R.id.tabLayout); // Replace with your TabLayout ID
        TabLayout.Tab mainTab = tabLayout.getTabAt(0); // Assuming "Main" is the first tab
        TabLayout.Tab historyTab = tabLayout.getTabAt(1); // Assuming "History" is the second tab
        historyTab.select();

        // Initialize ListView and DatabaseHelper
        listView = findViewById(R.id.listView);
        databaseHelper = new DatabaseHelper(this);

        // Retrieve details from the database
        List<String> detailsList = databaseHelper.getAllDetails();

        // Create an ArrayAdapter and set it to the ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, detailsList);
        listView.setAdapter(adapter);

        // Set a click listener for items in the ListView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = detailsList.get(position);

                // Create a sharing intent to share the selected item's text
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, selectedItem);

                // Display a chooser to share the item's text
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });

        // Set a tab selected listener to switch to the main activity when the "Main" tab is selected
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab == mainTab) {
                    // Create an intent to navigate to the main activity
                    Intent intent = new Intent(historyActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Handle unselected tabs if needed
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Handle reselected tabs if needed
            }
        });
    }
}