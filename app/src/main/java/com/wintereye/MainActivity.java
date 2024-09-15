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

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {

    String API = "8118ed6ee68db2debfaaa5a44c832918";
    String LAT = "-37.8136";
    String LON = "144.9631";

    // Reference: https://www.spc.noaa.gov/faq/tornado/beaufort.html#:~:text=5,in%20leaf%20begin%20to%20sway
    List<Integer> WINDS = Arrays.asList(1, 3, 6, 10, 16, 21, 27, 33, 40, 47, 55, 63);
    List<String> WMO_WIND_CLASSIFICATION = Arrays.asList("Calm", "Light Air", "Light Breeze",
            "Gentle Breeze", "Moderate Breeze", "Fresh Breeze", "Strong Breeze", "Near Gale",
            "Gale", "Strong Gale", "Storm", "Violent Storm", "Hurricane");

    // Reference: https://hookedonrunning.com.au/running-in-humidity/
    List<Integer> DEW_POINTS = Arrays.asList(5, 10, 15, 18, 21, 24);
    List<String> DEW_POINT_COMFORT_LEVELS = Arrays.asList("Very dry", "Dry", "Comfortable",
            "Slightly Uncomfortable", "Somewhat Muggy", "Quite Muggy", "Oppressive");

    // Reference:
    // https://media.bom.gov.au/social/blog/2391/the-art-of-the-chart-how-to-read-a-weather-map/#:~:text=A%20typical%20high%20is%20around,intense%20low%20below%20980%20hPa.
    List<Integer> PRESSURES = Arrays.asList(980, 1000, 1006, 1020);
    List<String> PRESSURE_LEVELS = Arrays.asList("Intense Low", "Moderate Low", "Shallow Low",
            "Standard", "Typical High");

    int REQUEST_CODE = 44;

    FusedLocationProviderClient mFusedLocationClient;

    TextView addressTxt, updated_atTxt, statusTxt, tempTxt, temp_minTxt, temp_maxTxt, sunriseTxt,
            sunsetTxt, windTxt, pressureTxt, humidityTxt, wind_descriptionTxt, dew_pointTxt,
            humidity_descriptionTxt, pressure_descriptionTxt, sunrise_titleTxt, sunset_titleTxt;

    Barometer barometer;
    Hygrometer hygrometer;
    private Button access_button;
    private Button splash_button;
    private ImageButton recommend_button;

    private ImageButton account_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_display);

        // Open the recommend page
        recommend_button = findViewById(R.id.shake_button);
        recommend_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openRecommend();
            }
        });

        account_button = findViewById(R.id.account_button);
        account_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAccount();
            }
        });

        // Sensors
        barometer = new Barometer(this);
        hygrometer = new Hygrometer(this);

        addressTxt = findViewById(R.id.address);
        updated_atTxt = findViewById(R.id.updated_at);
        statusTxt = findViewById(R.id.status);
        tempTxt = findViewById(R.id.temp);
        temp_minTxt = findViewById(R.id.temp_min);
        temp_maxTxt = findViewById(R.id.temp_max);
        sunriseTxt = findViewById(R.id.sunrise);
        sunsetTxt = findViewById(R.id.sunset);
        windTxt = findViewById(R.id.wind);
        pressureTxt = findViewById(R.id.pressure);
        humidityTxt = findViewById(R.id.humidity);
        wind_descriptionTxt = findViewById(R.id.wind_description);
        dew_pointTxt = findViewById(R.id.dew_point);
        humidity_descriptionTxt = findViewById(R.id.humidity_description);
        pressure_descriptionTxt = findViewById(R.id.pressure_description);
        sunrise_titleTxt = findViewById(R.id.sun_next);
        sunset_titleTxt = findViewById(R.id.sun_current);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        getCurrentLocation();
        new weatherTask().execute();
    }

    public void openAccount() {
        startActivity(new Intent(this,Account.class));
    }

    public void openRecommend() {
        startActivity(new Intent(this,Recommend.class));
    }

    private float getPressureFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("SensorPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getFloat("PressureValue", -1); // -1 is the default value if not found
    }

    private float getHumidityFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("SensorPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getFloat("HumidityValue", -1); // -1 is a default value if nothing is found
    }


    @Override
    protected void onDestroy() {
        hygrometer.disableSensor();
        barometer.disableSensor();
        super.onDestroy();
    }


    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if (location == null) {
                        LAT = "-37.8136";
                        LON = "144.9631";
                    } else {
                        LAT = location.getLatitude() + "";
                        LON = location.getLongitude() + "";
                        System.out.println(LAT);
                        System.out.println(LON);
                    }
                }
            });
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE);
        }
    }

    private String getWindDescription(Long windSpeed) {
        for (int i = 0; i < WINDS.size(); i++) {
            if (windSpeed < WINDS.get(i)) {
                return "L" + i + ": " + WMO_WIND_CLASSIFICATION.get(i);
            }
        }
        int highestLevel = WMO_WIND_CLASSIFICATION.size()-1;
        return "L" + highestLevel + ": " + WMO_WIND_CLASSIFICATION.get(highestLevel);
    }

    private double calculateDewPoint(double temp, double humidity) {
        double a = Math.log(humidity/100) + (17.625 * temp / (243.04 + temp));
        return 243.04 * a / (17.625 - a);
    }

    private String getHumidityDescription(double dewPoint) {
        for (int i = 0; i < DEW_POINTS.size(); i++) {
            if (dewPoint < DEW_POINTS.get(i)) {
                return DEW_POINT_COMFORT_LEVELS.get(i);
            }
        }
        return DEW_POINT_COMFORT_LEVELS.get(DEW_POINT_COMFORT_LEVELS.size()-1);
    }

    private String getPressureDescription(double pressure) {
        for (int i = 0; i < PRESSURES.size(); i++) {
            if (pressure < PRESSURES.get(i)) {
                return PRESSURE_LEVELS.get(i);
            }
        }
        return PRESSURE_LEVELS.get(PRESSURE_LEVELS.size()-1);
    }

    // Reference: https://stackoverflow.com/questions/2808535/round-a-double-to-2-decimal-places
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private void setWeatherIcon(String iconName, String weather) {
        // Weather icons are created by Roman Danylyk - Flaticon
        String uri = "drawable/i" + iconName;
        if (weather.contains("rain and snow") || weather.contains("sleet")) {
            uri.concat("_sleet");
        } else if (weather.equals("mist") || weather.equals("smoke") || weather.equals("haze")) {
            uri.concat("_haze");
        }

        int imageResource = getResources().getIdentifier(uri, null, getPackageName());
        ImageView weatherIcon = findViewById(R.id.weather_icon);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { // >= API 21
            weatherIcon.setImageDrawable(getResources().getDrawable(imageResource, getApplicationContext().getTheme()));
        } else {
            weatherIcon.setImageDrawable(getResources().getDrawable(imageResource));
        }
    }

    class weatherTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            findViewById(R.id.loader).setVisibility(View.VISIBLE);
            findViewById(R.id.detailsContainer).setVisibility(View.GONE);
            findViewById(R.id.errorText).setVisibility(View.GONE);
            findViewById(R.id.weather_icon).setVisibility(View.GONE);
        }

        protected String doInBackground(String... args) {
            String response = HttpRequest.excuteGet("https://api.openweathermap.org/data/2.5/weather?lat=" + LAT + "&lon=" + LON + "&units=metric&appid=" + API);
            return response;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObj = new JSONObject(result);
                JSONObject main = jsonObj.getJSONObject("main");
                JSONObject sys = jsonObj.getJSONObject("sys");
                JSONObject wind = jsonObj.getJSONObject("wind");
                JSONObject weather = jsonObj.getJSONArray("weather").getJSONObject(0);

                Long updatedAt = jsonObj.getLong("dt");
                String updatedAtText = "Updated at: " + new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(new Date(updatedAt * 1000));
                // Only display the integer part of the temperature
                String temp = main.getString("temp").split("\\.")[0] + "°";
                String tempMin = "L: " + main.getString("temp_min").split("\\.")[0] + "°";
                String tempMax = "H: " + main.getString("temp_max").split("\\.")[0] + "°";

                // Check if the sensor has read humidity and pressure
                float storedPressure = getPressureFromSharedPreferences();
                float storedHumidity = getHumidityFromSharedPreferences();

                // If the sensor stores data, use the stored data, otherwise use the data obtained by the api
                String pressure = (storedPressure != -1) ? String.valueOf(storedPressure) : main.getString("pressure");
                String humidity = (storedHumidity != -1) ? String.valueOf(storedHumidity) : main.getString("humidity");

                Long sunrise = sys.getLong("sunrise");
                Long sunset = sys.getLong("sunset");
                // Only need 1 decimal
                String windSpeed = String.format("%.1f", wind.getDouble("speed"));
                String weatherDescription = weather.getString("description");

                String address = jsonObj.getString("name") + ", " + sys.getString("country");

                String windDescription = getWindDescription(wind.getLong("speed"));

                double dewPoint = calculateDewPoint(main.getDouble("temp"),
                        Double.parseDouble(humidity));
                String dewPointString = "Dew point: " + round(dewPoint, 1) + "°";
                String humidityDescription = getHumidityDescription(dewPoint);

                String pressureDescription = getPressureDescription(Double.parseDouble(pressure));

                String weatherIconName = weather.getString("icon");

                addressTxt.setText(address);
                updated_atTxt.setText(updatedAtText);
                statusTxt.setText(weatherDescription);
                tempTxt.setText(temp);
                temp_minTxt.setText(tempMin);
                temp_maxTxt.setText(tempMax);
                sunriseTxt.setText(new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(sunrise * 1000)));
                sunsetTxt.setText(new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(sunset * 1000)));
                windTxt.setText(windSpeed);
                wind_descriptionTxt.setText(windDescription);
                pressureTxt.setText(pressure.split("\\.")[0]);
                pressure_descriptionTxt.setText(pressureDescription);
                humidityTxt.setText(humidity + "%");
                dew_pointTxt.setText(dewPointString);
                humidity_descriptionTxt.setText(humidityDescription);

                long curr = jsonObj.getLong("dt");
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date(curr * 1000));
                int month = calendar.get(Calendar.MONTH) + 1;

                if (sunrise > sunset) {
                    sunrise_titleTxt.setText(" SUNRISE");
                    sunset_titleTxt.setText("Sunset:");
                } else {
                    sunrise_titleTxt.setText(" SUNSET");
                    sunset_titleTxt.setText("Sunrise:");
                }

                setWeatherIcon(weatherIconName, weatherDescription);

                findViewById(R.id.loader).setVisibility(View.GONE);
                findViewById(R.id.detailsContainer).setVisibility(View.VISIBLE);
                findViewById(R.id.weather_icon).setVisibility(View.VISIBLE);

                SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences("WeatherInfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("main", jsonObj.getJSONArray("weather").getJSONObject(0).getString("main"));
                editor.putString("temp", temp);
                editor.putString("windSpeed", wind.getString("speed"));
                editor.putInt("month", month);
                editor.apply();

            } catch (JSONException e) {
                findViewById(R.id.loader).setVisibility(View.GONE);
                findViewById(R.id.errorText).setVisibility(View.VISIBLE);
            }

        }
    }
}