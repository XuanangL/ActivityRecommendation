package com.wintereye;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Access extends AppCompatActivity {

    private Button login_button;
    private Button register_button;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.access);

        if (getScreenHeight() / getScreenWidth() < 0.785) {
            findViewById(R.id.activities_image).setVisibility(View.GONE);
        }

        // Open the login page
        login_button = findViewById(R.id.access_login_btn);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLogin();
            }
        });

        // Open the register page
        register_button = findViewById(R.id.access_register_btn);
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openRegister();
            }
        });

    }
    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }
    public void openLogin(){
        startActivity(new Intent(this,Login.class));
    }
    public void openRegister(){
        startActivity(new Intent(this,SignUp.class));
    }
}
