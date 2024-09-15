package com.wintereye;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class Login extends AppCompatActivity {


    private EditText mLoginEmailEditText;
    private EditText mLoginPasswordEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mLoginEmailEditText = findViewById(R.id.edit_login_email); // Assign appropriate ID in XML
        mLoginPasswordEditText = findViewById(R.id.edit_login_password); // Assign appropriate ID in XML



    }

    public void openSignUp(View view){
        startActivity(new Intent(this,SignUp.class));

    }

    private boolean isValidLogin(String enteredEmail, String enteredPassword) {
        try {
            File csvFile = new File(getFilesDir(), "sign_up_data.csv");

            if (!csvFile.exists()) {
                return false;
            }

            FileInputStream fileInputStream = new FileInputStream(csvFile);
            InputStreamReader reader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line;

            // Skipping the header line
            bufferedReader.readLine();

            while ((line = bufferedReader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {  // Ensure line has the correct number of fields
                    String savedEmail = parts[1].trim();
                    String savedPassword = parts[2].trim();

                    if (enteredEmail.equals(savedEmail) && enteredPassword.equals(savedPassword)) {
                        bufferedReader.close();
                        return true;
                    }
                }
            }
            bufferedReader.close();
        } catch (Exception e) {
            Toast.makeText(this, "Error reading data!", Toast.LENGTH_SHORT).show();
        }

        return false;
    }
    public void onLoginClicked(View view) {
        String enteredEmail = mLoginEmailEditText.getText().toString();
        String enteredPassword = mLoginPasswordEditText.getText().toString();

        if (isValidLogin(enteredEmail, enteredPassword)) {
            // Successful login action
            Toast.makeText(this, "Successfully logged in!", Toast.LENGTH_SHORT).show();
            SharedPreferences sharedPreferences = this.getSharedPreferences("CurrentEmail", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("email", enteredEmail);
            editor.apply();
            // Navigate to the next activity or do whatever is needed
            startActivity(new Intent(this, PreferencesSelection.class));

        } else {
            Toast.makeText(this, "Invalid email or password!", Toast.LENGTH_SHORT).show();
        }
    }

}