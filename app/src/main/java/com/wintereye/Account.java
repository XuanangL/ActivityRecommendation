package com.wintereye;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class Account extends AppCompatActivity {
    private Button edit_button;
    private ImageButton home_button;
    private ImageButton recommend_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account);

        TextView username = findViewById(R.id.usernameDisplay);
        TextView email = findViewById(R.id.emailDisplay);
        TextView preferences = findViewById(R.id.preferencesDisplay);

        SharedPreferences sharedPreferences = getSharedPreferences("CurrentEmail", Context.MODE_PRIVATE);
        String emailText = sharedPreferences.getString("email", "");
        String usernameText = "";

        try {
            File csvFile = new File(getFilesDir(), "sign_up_data.csv");

            if (csvFile.exists()) {
                FileInputStream fileInputStream = new FileInputStream(csvFile);
                InputStreamReader reader = new InputStreamReader(fileInputStream);
                BufferedReader bufferedReader = new BufferedReader(reader);
                String line;

                // Skipping the header line
                bufferedReader.readLine();

                while ((line = bufferedReader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 3) {  // Ensure line has the correct number of fields
                        String savedUsername = parts[0].trim();
                        String savedEmail = parts[1].trim();

                        if (emailText.equals(savedEmail)) {
                            usernameText = savedUsername;
                            bufferedReader.close();
                        }
                    }
                }
                bufferedReader.close();
            }


        } catch (Exception ignored) {

        }

        String inputString = readFromCSV();
        inputString = inputString.toLowerCase();
        String[] parts = inputString.split(",");

        List<String> stringList = new ArrayList<>(Arrays.asList(parts));
        stringList.remove(0);
        String res = String.join(", ", stringList);
        String result = res.replaceAll("option", "");

        username.setText(usernameText);
        email.setText(emailText);
        preferences.setText(result);

        edit_button = findViewById(R.id.edit_button);
        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { openPreference();}
        });

        recommend_button = findViewById(R.id.shake_button);
        recommend_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openRecommend();
            }
        });

        home_button = findViewById(R.id.home_button);
        home_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMainActivity();
            }
        });

    }

    public void openMainActivity() {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    public void openRecommend() {
        startActivity(new Intent(this,Recommend.class));
    }

    public void openPreference() {
        Intent intent = new Intent(this, PreferencesSelection.class);
        startActivity(intent);
    }

    private String readFromCSV() {
        StringBuilder data = new StringBuilder();
        try {
            File csvFile = new File(getFilesDir(), "preferences.csv");

            if (csvFile.exists()) {
                FileInputStream fileInputStream = new FileInputStream(csvFile);
                InputStreamReader reader = new InputStreamReader(fileInputStream);
                BufferedReader bufferedReader = new BufferedReader(reader);
                String line;
                while ((line = bufferedReader.readLine()) != null) {
//                    data.append(line).append("\n");
                    data.append(line);
                }
                bufferedReader.close();
                reader.close();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error reading data!", Toast.LENGTH_SHORT).show();
        }
        return data.toString();
    }
}