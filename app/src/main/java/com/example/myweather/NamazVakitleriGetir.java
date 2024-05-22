package com.example.myweather;

import android.os.AsyncTask;
import org.json.JSONArray;
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

public class NamazVakitleriGetir extends AsyncTask<String, Void, JSONObject> {

    private WeakReference<MainActivity> activityRef;

    NamazVakitleriGetir(MainActivity activity) {
        activityRef = new WeakReference<>(activity);
    }

    @Override
    protected JSONObject doInBackground(String... strings) {
        String city = "Istanbul";
        if (strings != null && strings.length > 0 && !strings[0].isEmpty()) {
            city = strings[0];
        }

        String url = String.format("https://muslimsalat.com/%s.json?key=8d264a462086fc77e48eb2d020b59246",
                URLEncoder.encode(city, StandardCharsets.UTF_8));

        StringBuilder sb = new StringBuilder();
        URL uri;
        try {
            uri = new URL(url);
            HttpsURLConnection con = (HttpsURLConnection) uri.openConnection();
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
                activity.handlePrayerTimesData(jsonObject);
            } else {
                activity.handlePrayerTimesDataError();
            }
        }
    }
}
