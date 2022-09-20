package com.hfad.weatherapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    final String api_key = "5e819f82ac5c7e58f1c9a746a5ee4363";
    final String weather_url = "https://api.openweathermap.org/data/2.5/weather";

    final long min_time = 5000;
    final float min_dist = 1000;
    final int request_code = 101;

    String Location_Provider = LocationManager.GPS_PROVIDER;

    TextView NameOfCity, weatherState, temperature;
    ImageView weatherIcon;

    RelativeLayout cityFinder;

    LocationManager mLocationManager;
    LocationListener mLocationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weatherState = findViewById(R.id.weatherCondition);
        temperature = findViewById(R.id.temperature);
        weatherIcon = findViewById(R.id.weatherIcon);
        cityFinder = findViewById(R.id.cityFinder);
        NameOfCity = findViewById(R.id.cityName);

        cityFinder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, cityFinder.class);
                startActivity(intent);
            }
        });
    }


   /* @Override
    protected void onResume() {
        super.onResume();
        getWeatherOfCurrentLocation();
    } */

    @Override
    protected void onResume() {
        super.onResume();
        Intent n_intent=getIntent();
        String city = n_intent.getStringExtra("City");
        if (city!=null) {
            getWeatherForNewCity(city);
        }
        else {
            getWeatherOfCurrentLocation();
        }
    }

    private void getWeatherForNewCity(String city) {

        RequestParams params = new RequestParams();
        params.put("q", city);
        params.put("app id", api_key);
        letsdoSomeNetworking(params);


    }

    private void getWeatherOfCurrentLocation() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                String Latitude = String.valueOf(location.getLatitude());
                String Longitude = String.valueOf(location.getLongitude());

                RequestParams params = new RequestParams();
                params.put("Lat", Latitude);
                params.put("Long", Longitude);
                params.put("app id", api_key);
                letsdoSomeNetworking(params);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                // not able to get the location

            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, request_code);
            return;
        }
        mLocationManager.requestLocationUpdates(Location_Provider, min_time, min_dist, mLocationListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == request_code){
            if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(MainActivity.this, "Location granted", Toast.LENGTH_SHORT).show();
                getWeatherOfCurrentLocation();
            }
            else {
                //user did not approve the permission
            }
        }
    }

    public void letsdoSomeNetworking(RequestParams params) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(weather_url, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers,  JSONObject response) {
                Toast.makeText(MainActivity.this, "All necessary data successfully captured", Toast.LENGTH_SHORT).show();
                weather w = weather.fromJson(response);
                updateUI(w);

               // super.onSuccess(statusCode, headers,  Response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,  Throwable throwable, JSONObject errorResponse) {
               // super.onFailure(statusCode, headers,  throwable, errorResponse);
            }
        });
    }

    private void updateUI(weather myweather) {
        temperature.setText(weather.getTemp());
        NameOfCity.setText(weather.getCity());
        weatherState.setText(weather.getTypeOfWeather());
        int resourceID = getResources().getIdentifier(weather.getIcon(),"drawable",getPackageName());
        weatherIcon.setImageResource(resourceID);


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(mLocationListener);
        }
    }
}