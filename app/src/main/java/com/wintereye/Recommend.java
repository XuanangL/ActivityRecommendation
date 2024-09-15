package com.wintereye;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Recommend extends AppCompatActivity {

    private TextView recommend;
    private final double avgPressure = 1013.25;
    private ImageButton home_button;

    private ImageButton account_button;

    private ImageView recommend_img;

    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private ShakeDetector shakeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recommend);

        // Create outdoor and indoor activities for recommendation use
        createActivities();

        // Initialize the SensorManager and accelerometer sensor
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        ImageView viewToShake = findViewById(R.id.phoneImageView);
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake_anim);
        viewToShake.startAnimation(shake);

        Button tap_button = findViewById(R.id.tap_button);
        tap_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRecommendation();
            }
        });


        // Initialize your shake detector
        shakeDetector = new ShakeDetector(new ShakeDetector.OnShakeListener() {
            @Override
            public void onShake(int count) {
                // Trigger the popup here
                showRecommendation();
            }
        });

        // Open the home page
        home_button = (ImageButton) findViewById(R.id.home_button);
        home_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMainActivity();
            }
        });

        account_button = findViewById(R.id.account_button);
        account_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAccount();
            }
        });
    }

    private void showRecommendation() {
        ImageView shakeView = findViewById(R.id.phoneImageView);
        shakeView.clearAnimation();
        shakeView.setVisibility(View.GONE);
        Button tap_button = findViewById(R.id.tap_button);
        tap_button.setVisibility(View.GONE);
        TextView orText = findViewById(R.id.orView);
        orText.setVisibility(View.GONE);

        // Set activities text
        recommend = findViewById(R.id.activity);
        String recommendation = predict_recommend();
        recommendation = recommendation.substring(0, recommendation.length() - 6);
        recommend.setText(recommendation);

        LinearLayout container = findViewById(R.id.activity_container);
        container.setVisibility(View.VISIBLE);

        // Set activity img
        ImageView imageView = findViewById(R.id.recommend_img);
        int id = getResources().getIdentifier(recommendation, "drawable", getPackageName());
        imageView.setImageResource(id);
        imageView.setVisibility(View.VISIBLE);

        // Set activity description
        Map<String,String> activityDescriptionMap = createActivityMap();
        TextView descriptionText = findViewById(R.id.activity_description);
        String description = activityDescriptionMap.get(recommendation);
        descriptionText.setText(description);
        descriptionText.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register the sensor listener
        sensorManager.registerListener(shakeDetector, accelerometerSensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        // Unregister the sensor listener
        sensorManager.unregisterListener(shakeDetector);
        super.onPause();
    }

    public void openMainActivity() {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    public void openAccount() {
        startActivity(new Intent(this,Account.class));
    }

    private float getPressureFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("SensorPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getFloat("PressureValue", -1); // -1 is the default value if not found
    }

    private float getHumidityFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("SensorPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getFloat("HumidityValue", -1); // -1 is a default value if nothing is found
    }

    /*
        Recommend an activity to user using all info it obtained
     */
    private String predict_recommend() {
        // get weather info
        SharedPreferences sharedPreferences = getSharedPreferences("WeatherInfo", Context.MODE_PRIVATE);
        String weather = (sharedPreferences.getString("main", "Clear"));
        float pressure = getPressureFromSharedPreferences();
        float humidity = getHumidityFromSharedPreferences();
        String tmp = (sharedPreferences.getString("temp", "0"));

        System.out.println("tmp"+ tmp);
        double temp = Double.parseDouble(tmp.replaceAll("[^0-9]", ""));
        double windSpeed = Double.parseDouble(((sharedPreferences.getString("windSpeed", "0"))));
        int month = sharedPreferences.getInt("month", 1);
        System.out.println("weather info " + weather + " " + pressure + " " + humidity + " " + temp + " " + windSpeed + " " + month);

        // Get outdoor activities
        List<String> read_outdoor = new ArrayList<>();
        try {
            FileInputStream fileInputStream = openFileInput("outdoor_activities.txt");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            read_outdoor = (List<String>) objectInputStream.readObject();
            objectInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Convert every string to lowercase
        for (int i = 0; i < read_outdoor.size(); i++) {
            String originalString = read_outdoor.get(i);
            String lowercaseString = originalString.toLowerCase();
            read_outdoor.set(i, lowercaseString);
        }

        // Get indoor activities
        List<String> read_indoor = new ArrayList<>();
        try {
            FileInputStream fileInputStream = openFileInput("indoor_activities.txt");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            read_indoor = (List<String>) objectInputStream.readObject();
            objectInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Convert every string to lowercase
        for (int i = 0; i < read_indoor.size(); i++) {
            String originalString = read_indoor.get(i);
            String lowercaseString = originalString.toLowerCase();
            read_indoor.set(i, lowercaseString);
        }

        // Get user preference
        List<String> preferences = getPreference();
        if (preferences.contains("skiingoption")) {
            preferences.remove("skiingoption");
        }
        System.out.println("pref" + preferences);

        List<String> outdoorPref = getOutdoorPref(preferences, read_outdoor);
        List<String> indoorPref = getIndoorPref(preferences, read_indoor);
        System.out.println("indoor pref" + indoorPref);
        System.out.println("outdoor pref" + outdoorPref);
        System.out.println("read_indoor" + read_indoor);
        System.out.println("read_outdoor" + read_outdoor);

        String recommendation = "";

        switch (weather) {
            case "Clear":
                // check if wind is below Strong Breeze and humidity is less than 90%
                if ((windSpeed < 25) && (humidity < 90.0)) {
                    // check season
                    if ((month <= 8) && (month >= 6)) {
                        return recommendOutdoorWinter(outdoorPref, read_outdoor);
                    } else {
                        // check if temp is suitable
                        if ((temp > 10.0) && (temp < 30)) {
                            if (humidity == -1.0) {
                                return recommendOutdoor(outdoorPref, read_outdoor);
                            } else if (humidity < 10.0) {
                                return recommendIndoor(indoorPref, read_indoor);
                            } else {
                                return recommendOutdoor(outdoorPref, read_outdoor);
                            }
                        }
                    }
                } else {
                    return recommendIndoor(indoorPref, read_indoor);
                }

            case "Clouds":
                if (pressure < avgPressure - 10)  {
                    return recommendIndoor(indoorPref, read_indoor);
                } else {
                    return recommendOutdoor(outdoorPref, read_outdoor);
                }

            case "Snow":
                return "Skiing";

            case "Rain":
                return recommendIndoor(indoorPref, read_indoor);

            case "Thunderstorm":
                return recommendIndoor(indoorPref, read_indoor);

            case "Drizzle":
                return recommendIndoor(indoorPref, read_indoor);

            default:
                return "Reading";
        }
    }

    public String recommendIndoor(List<String> indoorPref, List<String> read_indoor) {
        Random rand = new Random();
        String recommendation = "";
        if (indoorPref.size() > 0) {
            int randomIndex = rand.nextInt(indoorPref.size());
            recommendation = indoorPref.get(randomIndex);
        } else {
            int randomIndex = rand.nextInt(read_indoor.size());
            recommendation = read_indoor.get(randomIndex);
        }
        return recommendation;
    }

    public String recommendOutdoorWinter(List<String> outdoorPref, List<String> read_outdoor) {
        Random rand = new Random();
        String recommendation = "";
        if (outdoorPref.size() > 0) {
            int randomIndex = rand.nextInt(outdoorPref.size());
            recommendation = outdoorPref.get(randomIndex);
        } else {
            int randomIndex = rand.nextInt(read_outdoor.size());
            recommendation = read_outdoor.get(randomIndex);
        }
        return recommendation;
    }

    public String recommendOutdoor(List<String> outdoorPref, List<String> read_outdoor) {
        read_outdoor.remove("skiingOption");
        Random rand = new Random();
        String recommendation = "";
        if (outdoorPref.size() > 0) {
            int randomIndex = rand.nextInt(outdoorPref.size());
            recommendation = outdoorPref.get(randomIndex);
        } else {
            int randomIndex = rand.nextInt(read_outdoor.size());
            recommendation = read_outdoor.get(randomIndex);
        }
        return recommendation;
    }

    public List<String> getOutdoorPref(List<String> preferences, List<String> outdoorActivities) {
        List<String> outdoorPref = new ArrayList<>();

        for (String item: preferences) {
            if (outdoorActivities.contains(item)) {

                outdoorPref.add(item.toLowerCase());
            }
        }
        return outdoorPref;

    }

    public List<String> getIndoorPref(List<String> preferences, List<String> indoorActivities) {
        List<String> indoorPref = new ArrayList<>();

        for (String item: preferences) {
            if (indoorActivities.contains(item)) {
                indoorPref.add(item.toLowerCase());
            }
        }
        return indoorPref;

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
    public List<String> getPreference() {

        String inputString = readFromCSV();
        inputString = inputString.toLowerCase();
        // Split the input string by commas
        String[] parts = inputString.split(",");

        // Create a List from the array
        List<String> stringList = new ArrayList<>(Arrays.asList(parts));
        stringList.remove(0);
        // Print the List
        System.out.println("string list" + stringList);

        return stringList;
    }

    public void createActivities() {
        // List of outdoor activities
        List<String> outdoorActivities = new ArrayList<>();
        outdoorActivities.add("hikingOption");
        outdoorActivities.add("basketballOption");
        outdoorActivities.add("tennisOption");
        outdoorActivities.add("joggingOption");
        outdoorActivities.add("skiingOption");
        outdoorActivities.add("citywalkOption");
        outdoorActivities.add("footballOption");
        outdoorActivities.add("NetballOption");
        outdoorActivities.add("CricketOption");
        outdoorActivities.add("GolfOption");

        try {
            FileOutputStream fileOutputStream = openFileOutput("outdoor_activities.txt", Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(outdoorActivities);
            objectOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // List of indoor activities
        List<String> indoorActivities = new ArrayList<>();
        indoorActivities.add("GymOption");
        indoorActivities.add("BadmintonOption");
        indoorActivities.add("ChessOption");
        indoorActivities.add("TableTennisOption");
        indoorActivities.add("DancingOption");
        indoorActivities.add("ReadingOption");
        indoorActivities.add("yogaOption");

        try {
            FileOutputStream fileOutputStream = openFileOutput("indoor_activities.txt", Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(indoorActivities);
            objectOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static Map<String, String> createActivityMap() {
        Map<String,String> activityMap = new HashMap<String,String>();
        activityMap.put("hiking", "Get in touch with nature and get a bit of exercise as you explore.");
        activityMap.put("basketball", "Improve motor coordination, flexibility and endurance with a game of Basketball.");
        activityMap.put("tennis", "increase aerobic capacities and lower resting heart rate and blood pressure with a game of Tennis.");
        activityMap.put("jogging", "Running helps build strong bones, strengthens muscles and helps maintain a healthy weight.");
        activityMap.put("skiing", "Skiing keeps the body in the squat position, which strengthens the quads, hamstrings, calves, and glutes");
        activityMap.put("citywalk", "Explore, enjoy the urban environment by wandering around the city.");
        activityMap.put("football", "Football help building your muscle mass and burning fat by recruiting both slow-twitch and fast-twitch muscle fibers.");
        activityMap.put("netball", "Improve your aerobic fitness, stamina, strength and balance with a game of Netball.");
        activityMap.put("cricket", "Develop your overall fitness, stamina and hand–eye coordination with a game of Cricket.");
        activityMap.put("golf", "Stay fit and improve muscle tone and endurance with a game of Golf.");
        activityMap.put("gym", "Get fit and strong at the gym – your body will thank you.");
        activityMap.put("badminton", "Stay active and boost your reflexes with a game of Badminton.");
        activityMap.put("chess", "Improve your strategic thinking and mental agility with a game of Chess.");
        activityMap.put("tabletennis", "Sharpen your hand-eye coordination with a game of Table Tennis.");
        activityMap.put("dancing", "Dance raises your heart rate and works your muscles.");
        activityMap.put("reading", "\"There is more treasure in books than in all the pirate's loot on Treasure Island.\" - Walt Disney");
        activityMap.put("yoga", "Wants to improve your strength, balance and flexibility? Yoga is your best choice.");
        return activityMap;
    }
}