package com.wintereye;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.util.ArrayList;

public class PreferencesSelection extends AppCompatActivity {
    Button submitButton;
    private ArrayList<View> sportsOptions;  // Declare it as a class variable
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences_selection);

        Intent intent = getIntent();
        String emailId = intent.getStringExtra("EMAIL_ID");
        if (emailId != null) {
            // Use the email here or store it in a member variable for later use
        }
        setClickListenerForSportOptions();

        submitButton = findViewById(R.id.submitButton); // Assuming you have a button with this ID
        submitButton.setOnClickListener(view -> {
            ArrayList<String> selectedPreferences = new ArrayList<>();

            // Fetch the selected preferences using view IDs
            for (View sportOption : sportsOptions) {
                if (sportOption.isSelected()) {
                    String sportId = getResources().getResourceEntryName(sportOption.getId());
                    selectedPreferences.add(sportId);
                }
            }
            // Check if any preference is selected
            if (selectedPreferences.size() == 0) {
                Toast.makeText(this, "Please select at least one preference.", Toast.LENGTH_SHORT).show();
                return; // exit from the OnClickListener
            }
            else {
                savePreferences(emailId, selectedPreferences);
                startActivity(new Intent(this, MainActivity.class));
            }
        });


    }



    private void savePreferences(String emailId, ArrayList<String> selectedPreferences) {
        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append(emailId).append(",");

        for (String preference : selectedPreferences) {
            csvBuilder.append(preference).append(",");
        }

        // Remove the trailing comma
        String csvString = csvBuilder.toString().substring(0, csvBuilder.length()-1);

        // Save this csvString to a file or wherever you want to store it
        saveToCSVFile(csvString);
    }

    private void saveToCSVFile(String csvString) {

        String filename = "preferences.csv";
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(csvString.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setClickListenerForSportOptions() {
        ViewGroup parentLayout = findViewById(R.id.parentLayout); // Replace with your actual parent layout ID
        sportsOptions = findViewsWithTag(parentLayout, "sportOption");

        for (View sportOption : sportsOptions) {
            sportOption.setOnClickListener(view -> {
                view.setSelected(!view.isSelected());
                // Update your model/data based on this change
                if (view.isSelected()) {
                    Toast.makeText(this, view.getTag() + " Option Selected!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, view.getTag() + " Option Unselected!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private ArrayList<View> findViewsWithTag(ViewGroup root, String tag) {
        ArrayList<View> viewsWithTag = new ArrayList<>();
        final int childCount = root.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = root.getChildAt(i);
            if (child instanceof ViewGroup) {
                viewsWithTag.addAll(findViewsWithTag((ViewGroup) child, tag));
            }
            final Object tagObj = child.getTag();
            if (tagObj != null && tagObj.equals(tag)) {
                viewsWithTag.add(child);
            }
        }
        return viewsWithTag;
    }



}