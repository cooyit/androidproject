package com.example.myweather;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    TextView tv;
    EditText et;
    ImageView weatherIcon;
    TextView prayerTimesTv;

    private HavaDurumuGetir weatherTask;
    private NamazVakitleriGetir namazTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = findViewById(R.id.textView);
        et = findViewById(R.id.editTextText);
        weatherIcon = findViewById(R.id.imageViewWeatherIcon);
        prayerTimesTv = findViewById(R.id.textViewPrayerTimes);
    }

    public void Getir(View v) {
        String cityName = et.getText().toString().trim();
        if (cityName.isEmpty() || cityName.equals("Şehir İsmi")) {
            cityName = "Istanbul";
        }

        Log.d(TAG, "Fetching data for city: " + cityName);

        if (weatherTask != null) {
            weatherTask.cancel(true); // Cancel the previous task if it's running
        }
        weatherTask = new HavaDurumuGetir(this);
        weatherTask.execute(cityName);

        if (namazTask != null) {
            namazTask.cancel(true);
        }
        namazTask = new NamazVakitleriGetir(this);
        namazTask.execute(cityName);
    }

    public void handleWeatherData(JSONObject jsonObject) {
        Log.d(TAG, "Weather data received: " + jsonObject.toString());

        try {
            JSONObject main = jsonObject.getJSONObject("main");
            double temp = main.getDouble("temp") - 273.15; // Kelvin'den Celsius'a dönüştürme

            String formattedTemp = String.format("%.1f", temp);
            String cityName = jsonObject.getString("name");
            String countryCode = jsonObject.getJSONObject("sys").getString("country");

            JSONObject weather = jsonObject.getJSONArray("weather").getJSONObject(0);
            String description = weather.getString("description");
            String weatherText = cityName + ", " + countryCode + "\n" + formattedTemp + " °C\n" + description;

            tv.setText(weatherText);
            String iconCode = weather.getString("icon");
            String iconUrl = "https://openweathermap.org/img/w/" + iconCode + ".png";
            Glide.with(this).load(iconUrl).into(weatherIcon);

        } catch (JSONException e) {
            Log.e(TAG, "Error parsing weather data", e);
            e.printStackTrace();
        }
    }

    public void handleWeatherDataError() {
        tv.setText("Hava durumu bilgisi alınamadı.");
    }

    public void handlePrayerTimesData(JSONObject jsonObject) {
        Log.d(TAG, "Prayer times data received: " + jsonObject.toString());

        try {
            String city = jsonObject.getString("query");
            JSONArray items = jsonObject.getJSONArray("items");
            JSONObject today = items.getJSONObject(0);

            String prayerTimesText = "Şehir: " + city + "\n"
                    + "İmsak: " + today.getString("fajr") + "\n"
                    + "Güneş: " + today.getString("shurooq") + "\n"
                    + "Öğle: " + today.getString("dhuhr") + "\n"
                    + "İkindi: " + today.getString("asr") + "\n"
                    + "Akşam: " + today.getString("maghrib") + "\n"
                    + "Yatsı: " + today.getString("isha");

            prayerTimesTv.setText(prayerTimesText);

        } catch (JSONException e) {
            Log.e(TAG, "Error parsing prayer times data", e);
            e.printStackTrace();
        }
    }

    public void handlePrayerTimesDataError() {
        prayerTimesTv.setText("Namaz vakitleri bilgisi alınamadı.");
    }
}
