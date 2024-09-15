package com.wintereye;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class SignUp extends AppCompatActivity {

    private EditText mNameEditText;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private EditText mConfirmPasswordEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mNameEditText = findViewById(R.id.edit_name);
        mEmailEditText = findViewById(R.id.edit_email);
        mPasswordEditText = findViewById(R.id.edit_password);
        mConfirmPasswordEditText = findViewById(R.id.edit_confirm_password);
    }

    public void openLogin(View view){

        startActivity(new Intent(this, Login.class));


    }

    public void openSignUp(View view){
        String name = mNameEditText.getText().toString();
        String email = mEmailEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();
        String confirmPassword = mConfirmPasswordEditText.getText().toString();
        // Check if password and confirm password are the same
        if (password.equals(confirmPassword)) {
            // Save information in CSV file
            saveToCSV(name, email, password);
            startActivity(new Intent(this, PreferencesSelection.class));
            SharedPreferences sharedPreferences = this.getSharedPreferences("CurrentEmail", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("email", email);
            editor.apply();
        } else {
            Toast.makeText(this, "Password and confirm password do not match!", Toast.LENGTH_SHORT).show();
        }

    }

    private void saveToCSV(String name, String email, String password) {
        try {
            File csvFile = new File(getFilesDir(), "sign_up_data.csv");

            boolean isNewFile = false;
            // Check if file doesn't exist, then create it
            if (!csvFile.exists()) {
                csvFile.createNewFile();
                isNewFile = true;
            }

            FileOutputStream fileOutputStream = new FileOutputStream(csvFile, true); // true for append mode
            OutputStreamWriter writer = new OutputStreamWriter(fileOutputStream);

            // If it's a new file, write the headers
            if (isNewFile) {
                writer.append("Name,Email,Password\n");
            }

            // Write data in CSV format
            writer.append(name).append(",").append(email).append(",").append(password).append("\n");
            writer.flush();
            writer.close();
            Toast.makeText(this, "Data saved!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error saving data!", Toast.LENGTH_SHORT).show();
        }
    }
}