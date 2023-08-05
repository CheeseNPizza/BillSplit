package com.example.assignment_billsplit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    private EditText numPersonField, totalBillField, bilNameField;
    private Button splitButton, resetButton, saveButton;
    private LinearLayout parentLayout;
    private LayoutInflater inflater;
    private RelativeLayout[] personTemplates;
    int numPerson;
    float totalBill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize DatabaseHelper instance
        databaseHelper = new DatabaseHelper(this);

        // Initialize UI elements
        numPersonField = findViewById(R.id.numPerson);
        totalBillField = findViewById(R.id.totalBill);
        bilNameField = findViewById(R.id.bilName);
        splitButton = findViewById(R.id.splitButton);
        resetButton = findViewById(R.id.resetButton);
        saveButton = findViewById(R.id.saveButton);
        parentLayout = findViewById(R.id.personList);
        inflater = LayoutInflater.from(MainActivity.this);

        // Initialize radio group for breakdown types
        RadioGroup radioGroupBreakdownType = findViewById(R.id.breakdownType);
        // Get the ID of the selected RadioButton
        int selectedRadioButtonId = radioGroupBreakdownType.getCheckedRadioButtonId();

        // Set a listener for radio group changes
        radioGroupBreakdownType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {

                String numPersonStr = numPersonField.getText().toString(), totalBillStr = totalBillField.getText().toString();

                // Handle different breakdown options based on the selected RadioButton
                if (checkedId == R.id.equalType) // Handle equal breakdown option
                {

                } else if (checkedId == R.id.percentageType || checkedId == R.id.amountType) // Handle percentage/ amount breakdown option
                {
                    if (!numPersonStr.isEmpty() && !totalBillStr.isEmpty()) {
                    } else {
                        radioGroupBreakdownType.check(R.id.equalType);
                        Toast.makeText(MainActivity.this, "Please enter values in fields", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Set a click listener for the split button
        splitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String numPersonStr = numPersonField.getText().toString(), totalBillStr = totalBillField.getText().toString();

                int selectedRadioButtonId = radioGroupBreakdownType.getCheckedRadioButtonId();

                if (!numPersonStr.isEmpty() && !totalBillStr.isEmpty()) {

                    numPerson = Integer.parseInt(numPersonStr);
                    totalBill = Float.parseFloat(totalBillStr);

                    // Check if any RadioButton is selected
                    if (selectedRadioButtonId != -1) {
                        // At least one RadioButton is selected
                        if (selectedRadioButtonId == R.id.equalType) {
                            splitEqual();

                        } else if (selectedRadioButtonId == R.id.percentageType) {
                            // Handle custom breakdown option
                            splitPercentage();
                            splitButton.setText("Calculate");

                        } else if (selectedRadioButtonId == R.id.amountType) {
                            splitAmount();
                            splitButton.setText("Verify");
                        }
                    }

                } else {
                    Toast.makeText(MainActivity.this, "Please enter values in fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set an OnClickListener for the saveButton
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Retrieve the entered values for number of persons and total bill
                String numPersonStr = numPersonField.getText().toString(), totalBillStr = totalBillField.getText().toString();

                // Check if both number of persons and total bill are not empty
                if (!numPersonStr.isEmpty() && !totalBillStr.isEmpty()) {

                    String billName = bilNameField.getText().toString();

                    // Initialize a string to hold the formatted text to be saved
                    String text = databaseHelper.getTime() +"\n" + billName + "\n";

                    // Loop through each person template to retrieve name and balance
                    for (int i = 0; i < numPerson; i++)
                    {
                        EditText personNameField = personTemplates[i].findViewById(R.id.personName);
                        String personNameStr = personNameField.getText().toString();

                        EditText personBalanceField = personTemplates[i].findViewById(R.id.personBalance);
                        String personBalanceStr = personBalanceField.getText().toString();

                        // Concatenate the person's name and balance to the text
                        text += personNameStr + ": " + personBalanceStr + "\n";
                    }

                    // Insert the formatted text into the database and get the row ID
                    long newRowId = databaseHelper.insertRecord(text);

                    // Check if the data was successfully inserted into the database
                    if (newRowId != -1) {
                        Toast.makeText(MainActivity.this, "Data saved to database", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Error saving data to database", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(MainActivity.this, "Please enter values in fields", Toast.LENGTH_SHORT).show();
                }
            }

        });

        // Set an OnClickListener for the resetButton
        resetButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Clear the bill name, number of persons, and total bill fields
                bilNameField.setText("");
                numPersonField.setText("");
                totalBillField.setText("");

                // Reset the split button text
                splitButton.setText("Split now");

                // Remove all dynamically added person templates from the parent layout
                parentLayout.removeAllViews();
                // Reset the radio button selection to the "Equal" type
                radioGroupBreakdownType.check(R.id.equalType);

                Toast.makeText(MainActivity.this, "Form has been reset", Toast.LENGTH_SHORT).show();
            }

        });

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        // Get the Tab at the desired position, assuming "History" is the second tab
        TabLayout.Tab historyTab = tabLayout.getTabAt(1);

        // Add a tab selected listener to the TabLayout
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Check if the selected tab is the "History" tab
                if (tab == historyTab) {
                    // Create an Intent to navigate to the historyActivity
                    Intent intent = new Intent(MainActivity.this, historyActivity.class);
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

    // Handles the equal split calculation
    public void splitEqual()
    {
        // Initialize arrays to store percentage and balance values for each person
        float[] percentage = new float[numPerson];
        float[] balance = new float[numPerson];

        // Calculate equal percentage and balance values for each person
        for (int i = 0; i < numPerson; i++)
        {
            // Divide total percentage and total bill equally among all persons
            percentage[i] = 100.0f / numPerson;
            balance[i] = totalBill / numPerson;
        }

        // Call createPersonTemplate to display equal split among all persons
        createPersonTemplate(numPerson, percentage, balance);
    }

    // Handles the percentage split calculation
    public void splitPercentage()
    {
        // Initialize arrays to store percentage and balance values for each person
        float[] percentage = new float[numPerson];
        float[] balance = new float[numPerson];

        float totalPercentage = 0;

        // Check if the split button text is "Calculate"
        if (splitButton.getText().toString() == "Calculate")
        {
            for (int i = 0; i < numPerson; i++) {
                // Get the EditText for person's percentage from the person template
                EditText personPercentageField = personTemplates[i].findViewById(R.id.personPercentage);
                String personPercentageStr = personPercentageField.getText().toString();
                float personPercentage = Float.parseFloat(personPercentageStr.replace("%", ""));

                // Accumulate individual percentages and total percentage
                totalPercentage += personPercentage;
                percentage[i] = personPercentage;
            }

            // Calculate balance values based on individual percentages and total bill
            for (int i = 0; i < numPerson; i++)
                balance[i] = percentage[i] / 100.0f * totalBill;
        }
        else {
            // Reset percentage and balance arrays to 0, and set totalPercentage to 100
            for (int i = 0; i < numPerson; i++)
            {
                percentage[i] = 0;
                balance[i] = 0;

                totalPercentage = 100;
            }
        }

        // Check if the calculated total percentage equals 100
        if (totalPercentage != 100)
            Toast.makeText(MainActivity.this, "Total percentage must equal 100%", Toast.LENGTH_SHORT).show();
        else
            createPersonTemplate(numPerson, percentage, balance);
    }

    // Handles the custom amount split calculation
    public void splitAmount()
    {
        // Initialize arrays to store percentage and balance values for each person
        float[] percentage = new float[numPerson];
        float[] balance = new float[numPerson];

        float totalBalance = 0;

        // Check if the split button text is "Verify"
        if (splitButton.getText().toString() == "Verify")
        {
            for (int i = 0; i < numPerson; i++) {
                // Get the EditText for person's balance from the person template
                EditText personBalanceField = personTemplates[i].findViewById(R.id.personBalance);
                String personBalanceStr = personBalanceField.getText().toString();
                float personBalance = Float.parseFloat(personBalanceStr.replace("RM", ""));

                // Accumulate individual balances and total balance
                totalBalance += personBalance;
                balance[i] = personBalance;
            }

            // Calculate percentage values based on individual balances and total bill
            for (int i = 0; i < numPerson; i++)
            {
                percentage[i] = balance[i] / totalBill * 100.0f;
            }
        }
        else {
            // Reset percentage and balance arrays to 0, and set totalBalance to totalBill
            for (int i = 0; i < numPerson; i++)
            {
                percentage[i] = 0;
                balance[i] = 0;

                totalBalance = totalBill;
            }
        }

        // Check if the calculated total balance matches the total bill
        if (totalBalance != totalBill)
            Toast.makeText(MainActivity.this, "Sum of individual amount must equal to RM" + totalBill, Toast.LENGTH_SHORT).show();
        else
        {
            Toast.makeText(MainActivity.this, "Verified! Sum: RM" + totalBill, Toast.LENGTH_SHORT).show();
            createPersonTemplate(numPerson, percentage, balance);
        }
    }

    // Creates and displays person templates based on the selected breakdown option
    public void createPersonTemplate(int numPerson, float[] percentage, float[] balance)
    {
        // Clear any existing views in the parent layout
        parentLayout.removeAllViews();
        // Create an array to store the personTemplate views
        personTemplates = new RelativeLayout[numPerson];

        // Loop through each person and create their template
        for (int i = 0; i < numPerson; i++) {
            // Inflate the person_template.xml layout to get a new instance of RelativeLayout
            RelativeLayout personTemplate = (RelativeLayout) inflater.inflate(R.layout.person_template, parentLayout, false);

            // Find the views within the inflated layout
            // Set the calculated percentage and balance values for the current person
            EditText personPercentage = personTemplate.findViewById(R.id.personPercentage);
            personPercentage.setText(String.format("%.2f%%", percentage[i]));
            EditText personBalance = personTemplate.findViewById(R.id.personBalance);
            personBalance.setText("RM" + String.format("%.2f", balance[i]));

            // Add the inflated personTemplate to the parentLayout
            parentLayout.addView(personTemplate);
            // Store the personTemplate view in the array
            personTemplates[i] = personTemplate;
        }
    }
}

