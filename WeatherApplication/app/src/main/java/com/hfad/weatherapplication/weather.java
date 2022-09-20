package com.hfad.weatherapplication;

import org.json.JSONException;
import org.json.JSONObject;

public class weather {


    private static String temp;
    private static String icon;
    private static String city;
    private static String typeOfWeather;
    private int condition;

    public static weather fromJson(JSONObject jsonObject) {

        try{

            weather w = new weather();
            w.city=jsonObject.getString("name");
            w.condition = jsonObject.getJSONArray("weather").getJSONObject(0).getInt("id");
            w.typeOfWeather = jsonObject.getJSONArray("weather").getJSONObject(0).getString("main");
            w.icon = updateWeatherIcon(w.condition);
            double tempResult = jsonObject.getJSONObject("main").getDouble("temp")-273.15;
            int roundedValue = (int) Math.rint(tempResult);
            w.temp = Integer.toString(roundedValue);
            return w;


        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }


    }



    private static String updateWeatherIcon(int condition) {
        if (condition >= 0 && condition<=300) {
            return "thunderstorm";
        }
        else if (condition >= 300 && condition<=500) {
            return "lightrain";
        }
        else if (condition >= 500 && condition<=600) {
            return "shower";
        }
        else if (condition >= 701 && condition<=771) {
            return "fog";
        }
        else if (condition >= 772 && condition<=800) {
            return "overcast";
        }
        else if (condition==800) {
            return "sunny";
        }
        else if (condition >= 801 && condition<=804) {
            return "cloudy";
        }

        return "app does not support your weather";

    }

    public static String getTemp() {
        return temp + "⁰C";
    }

    public static String getIcon() {
        return icon;
    }

    public static String getCity() {
        return city;
    }

    public static String getTypeOfWeather() {
        return typeOfWeather;
    }
}
