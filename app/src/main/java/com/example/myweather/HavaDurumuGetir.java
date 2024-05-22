package com.example.myweather;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;

public class HavaDurumuGetir extends AsyncTask<String, Void, JSONObject> {

    private WeakReference<MainActivity> activityRef;

    HavaDurumuGetir(MainActivity activity) {
        activityRef = new WeakReference<>(activity);
    }

    @Override
    protected JSONObject doInBackground(String... strings) {
        String cityName = "Istanbul,TR";
        if (strings != null && strings.length > 0 && !strings[0].isEmpty()) {
            cityName = strings[0];
        }

        String apiKey = "a2faf6511b79b5423659aa1054142554";
        String urlString = String.format("https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s",
                URLEncoder.encode(cityName, StandardCharsets.UTF_8), apiKey);

        StringBuilder sb = new StringBuilder();
        URL url;
        try {
            url = new URL(urlString);

            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.connect();

            InputStream in = con.getInputStream();
            InputStreamReader reader = new InputStreamReader(in);

            int data = reader.read();
            while (data != -1) {
                sb.append((char) data);
                data = reader.read();
            }

            return new JSONObject(sb.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        super.onPostExecute(jsonObject);
        MainActivity activity = activityRef.get();
        if (activity != null) {
            if (jsonObject != null) {
                activity.handleWeatherData(jsonObject);
            } else {
                activity.handleWeatherDataError();
            }
        }
    }
}
