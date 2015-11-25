package udc.es.meteoapp.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RetrieveForecast extends Thread {

    String TAG = "MeteoApp";

    String locality_id;
    String locality_api_id;
    String apiKey;
    Handler handler;

    RetrieveForecast(
        String threadName,
        Handler handler,
        String locality_api_id,
        String apiKey,
        String locality_id) {

        super(threadName);
        this.handler = handler;
        this.locality_api_id = locality_api_id;
        this.apiKey = apiKey;
        this.locality_id = locality_id;
    }

    private String buildQueryURL () {
        String vars =
            "sky_state"                 + "," +
            "temperature"               + "," +
            "wind"                      + "," +
            "precipitation_amount"      + "," +
            "relative_humidity"         + "," +
            "cloud_area_fraction"       + "," +
            "snow_level"                + "," +
            "air_pressure_at_sea_level" + "," +
            "significative_wave_height" + "," +
            "relative_peak_period"      + "," +
            "mean_wave_direction"       + "," +
            "sea_water_temperature"     + "," +
            "sea_water_salinity";

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); // Format any date like this

        long date = Calendar.getInstance().getTimeInMillis();
        String startTime = format.format(new Date(date));

        // date is not currently used but needed to get a n-hour forecast, that is, just assign
        // values to delay variable accordingly
        long delay = 1;
        date += 60*60*1000 * delay;
        String endTime = format.format(new Date(date));
        //Log.d(TAG, "RetrieveForecast: buildQueryURL: startTime: " + endTime);

        String url =
            new StringBuilder("http://servizos.meteogalicia.es/apiv3/getNumericForecastInfo?locationIds=")
                .append(locality_api_id)
                .append("&variables=").append(vars)
                .append("&startTime=").append(startTime)
                .append("&endTime=").append(endTime)
                .append("&API_KEY=").append(apiKey)
                .toString();
        Log.d(TAG, "RetrieveForecast: buildQueryURL: " + url);

        return url;
    }

    @Override
    public void run() {
        super.run();
        Log.d(TAG, "RetrieveForecast: run");

        Bundle forecast = null;

        PlacesContent.PlaceItem pp = PlacesContent.ITEM_MAP.get(locality_id);
        String timeInstant = pp.details.timeInstant;

        if (!"".equals(timeInstant)) { // There is stored data

            if (isForecastOld(timeInstant)) // Stored data is old, do the query
               forecast = retrieveForecast(forecast);

            else { // Stored data is fresh enough, skip query
                forecast = new Bundle();
                forecast.putString("locality_id", locality_id);
            }
        } else // There is no stored data, do the qurery
            forecast = retrieveForecast(forecast);

        // Prepare data to be sent back
        Message message = new Message();
        message.setData(forecast);

        // Send data to main thread
        handler.sendMessage(message);
    }

    private boolean isForecastOld(String timeInstant) {
        Log.d(TAG, "RetrieveForecast: isForecastOld");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZ");
        Date date = null;
        try {
            date = sdf.parse(timeInstant);
        } catch (ParseException e) {
            Log.d(TAG, "RetrieveForecast: isForecastOld: " + e.getMessage());
        }

        // long forecastLimit = 1000*60*60 // one hour forecast
        long forecastLimit = 1000*60*1; // one minute forecast

        long currentDate = Calendar.getInstance().getTimeInMillis();
        long forecastTime = date.getTime();
        Log.d(TAG, "RetrieveForecast: isForecastOld: forecastTime : " + timeInstant);
        Log.d(TAG, "RetrieveForecast: isForecastOld: forecastTime : " + forecastTime);
        Log.d(TAG, "RetrieveForecast: isForecastOld: currentDate  : " + currentDate);
        Log.d(TAG, "RetrieveForecast: isForecastOld: difference   : " + (currentDate - forecastTime));
        Log.d(TAG, "RetrieveForecast: isForecastOld: forecastLimit: " + forecastLimit);

        return (currentDate - forecastTime) > forecastLimit;
    }

    private Bundle retrieveForecast(Bundle forecast) {
        Log.d(TAG, "RetrieveForecast: retrieveForecast");
        String queryURL = buildQueryURL();

        AndroidHttpClient client = AndroidHttpClient.newInstance("");
        HttpGet request = new HttpGet(queryURL);
        try {
            // Connect to API and execute query
            HttpResponse response = client.execute(request);

            // Parse received data
            JSONRetrieveForecasHandler jsonHandler = new JSONRetrieveForecasHandler();

            try {
                forecast = jsonHandler.getForecast(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Add locality internal ID to data
            if (forecast != null)
                forecast.putString("locality_id", locality_id);
            else
                throw new IOException("RetrieveForecast: forecast has no data");

//            URL url = new URL(forecast.getString("wind_direction_iconURL"));
//            Bitmap wind_direction_bitmap = BitmapFactory.decodeStream((InputStream) url.getContent());
//            String wind_direction_string = Utils.encodeTobase64(wind_direction_bitmap);
//            forecast.putString("wind_direction_string", wind_direction_string);
//
//            URL url_wave = new URL(forecast.getString("mean_wave_direction_url"));
//            Bitmap wave_direction_bitmap = BitmapFactory.decodeStream((InputStream) url_wave.getContent());
//            String wave_direction_string =  Utils.encodeTobase64(wave_direction_bitmap);
//            forecast.putString("wave_direction_string", wave_direction_string);

            URL url_sky = new URL(forecast.getString("sky_state_url"));
            Bitmap sky_state_bitmap = BitmapFactory.decodeStream((InputStream) url_sky.getContent());
            String sky_state_string =  Utils.encodeTobase64(sky_state_bitmap);
            forecast.putString("sky_state_string", sky_state_string);

            saveForecast(forecast);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            client.close();
        }

        return forecast;
    }

    public void saveForecast(Bundle received_data) {
        Log.d(TAG, "RetrieveForecast: saveForecast");

        String locality_id = received_data.getString("locality_id");
        Log.d(TAG, "RetrieveForecast: saveForecast: locality_id: " + locality_id);

        PlacesContent.PlaceItem pp = PlacesContent.ITEM_MAP.get(locality_id);

//        String wind_direction_string = received_data.getString("wind_direction_string");
//        pp.details.wind_direction_string = wind_direction_string;
//
//        String wave_direction_string = received_data.getString("wave_direction_string");
//        pp.details.wave_direction_string = wave_direction_string;

        String sky_state_string = received_data.getString("sky_state_string");
        pp.details.sky_state_string = sky_state_string;

        String timeInstant = received_data.getString("timeInstant");
        pp.details.timeInstant = timeInstant;

        String precipitation_amount_units = received_data.getString("precipitation_amount_units");
        String precipitation_amount_value = received_data.getString("precipitation_amount_value");
        pp.details.precipitation_amount_units = precipitation_amount_units;
        pp.details.precipitation_amount_value = precipitation_amount_value;

        String sky_state_url = received_data.getString("sky_state_url");
        String sky_state_value = received_data.getString("sky_state_value");
        pp.details.sky_state_url = sky_state_url;
        pp.details.sky_state_value = sky_state_value;

        String temperature_units = received_data.getString("temperature_units");
        String temperature_value = received_data.getString("temperature_value");
        pp.details.temperature_units = temperature_units;
        pp.details.temperature_value = temperature_value;

        String wind_module_units = received_data.getString("wind_module_units");
        String wind_direction_units = received_data.getString("wind_direction_units");
        String wind_direction_value = received_data.getString("wind_direction_value");
        String wind_module_value = received_data.getString("wind_module_value");
        String wind_direction_iconURL = received_data.getString("wind_direction_iconURL");
        pp.details.wind_module_units = wind_module_units;
        pp.details.wind_direction_units = wind_direction_units;
        pp.details.wind_direction_value = wind_direction_value;
        pp.details.wind_module_value = wind_module_value;
        pp.details.wind_direction_iconURL = wind_direction_iconURL;

        String snow_level_units = received_data.getString("snow_level_units");
        String snow_level_value = received_data.getString("snow_level_value");
        pp.details.snow_level_units = snow_level_units;
        pp.details.snow_level_value = snow_level_value;

        String relative_humidity_units = received_data.getString("relative_humidity_units");
        String relative_humidity_value = received_data.getString("relative_humidity_value");
        pp.details.relative_humidity_units = relative_humidity_units;
        pp.details.relative_humidity_value = relative_humidity_value;

        String cloud_area_fraction_units = received_data.getString("cloud_area_fraction_units");
        String cloud_area_fraction_value = received_data.getString("cloud_area_fraction_value");
        pp.details.cloud_area_fraction_units = cloud_area_fraction_units;
        pp.details.cloud_area_fraction_value = cloud_area_fraction_value;

        String air_pressure_at_sea_level_units = received_data.getString("air_pressure_at_sea_level_units");
        String air_pressure_at_sea_level_value = received_data.getString("air_pressure_at_sea_level_value");
        pp.details.air_pressure_at_sea_level_units = air_pressure_at_sea_level_units;
        pp.details.air_pressure_at_sea_level_value = air_pressure_at_sea_level_value;

        String significative_wave_height_units = received_data.getString("significative_wave_height_units");
        String significative_wave_height_value = received_data.getString("significative_wave_height_value");
        pp.details.significative_wave_height_units = significative_wave_height_units;
        pp.details.significative_wave_height_value = significative_wave_height_value;

        String relative_peak_period_units = received_data.getString("relative_peak_period_units");
        String relative_peak_period_value = received_data.getString("relative_peak_period_value");
        pp.details.relative_peak_period_units = relative_peak_period_units;
        pp.details.relative_peak_period_value = relative_peak_period_value;

        String mean_wave_direction_units = received_data.getString("mean_wave_direction_units");
        String mean_wave_direction_value = received_data.getString("mean_wave_direction_value");
        String mean_wave_direction_url = received_data.getString("mean_wave_direction_url");
        pp.details.mean_wave_direction_units = mean_wave_direction_units;
        pp.details.mean_wave_direction_value = mean_wave_direction_value;
        pp.details.mean_wave_direction_url = mean_wave_direction_url;

        String sea_water_temperature_units = received_data.getString("sea_water_temperature_units");
        String sea_water_temperature_value = received_data.getString("sea_water_temperature_value");
        pp.details.sea_water_temperature_units = sea_water_temperature_units;
        pp.details.sea_water_temperature_value = sea_water_temperature_value;

        String sea_water_salinity_units = received_data.getString("sea_water_salinity_units");
        String sea_water_salinity_value = received_data.getString("sea_water_salinity_value");
        pp.details.sea_water_salinity_units = sea_water_salinity_units;
        pp.details.sea_water_salinity_value = sea_water_salinity_value;


    }

    private class JSONRetrieveForecasHandler {

        public Bundle getForecast(HttpResponse httpResponse) throws IOException, JSONException {
            Log.d(TAG, "JSONRetrieveForecasHandler: getForecast");

            // Bundle to store JSON parsed result
            Bundle result = new Bundle();

            String JSONResp = new BasicResponseHandler().handleResponse(httpResponse);
            Log.d(TAG, "JSONRetrieveForecasHandler: getForecast: " + JSONResp);
            Log.d(TAG, "JSONResponseHandler: getForecast: status code: " + httpResponse.getStatusLine()
                    .getStatusCode());

            JSONTokener jt = new JSONTokener(JSONResp);
            //Log.d(TAG, "JSONResponseHandler: getForecast: " + jt.toString());

            JSONObject jsonObj;
            try {

                while (jt.more()) { // Loop JSONTokener
                    jsonObj = (JSONObject) jt.nextValue();

                    JSONArray features = jsonObj.getJSONArray("features");
                    if (features.length() == 0) // "features" was not found
                        continue;

                    jsonObj = features.getJSONObject(0);
                    JSONObject properties = jsonObj.getJSONObject("properties");

                    JSONArray days = properties.getJSONArray("days");
                    jsonObj = days.getJSONObject(0);

                    JSONObject timePeriod = jsonObj.getJSONObject("timePeriod");
                    JSONObject begin = timePeriod.getJSONObject("begin");
                    result.putString("timeInstant", begin.getString("timeInstant"));

                    JSONArray variables = jsonObj.getJSONArray("variables");
                    //Log.d(TAG, "JSONResponseHandler: getForecast: variables: " + variables.length());
                    for (int i = 0; i<variables.length(); i++) {
                        JSONObject var = (JSONObject) variables.get(i);
                        String name = var.getString("name");
                        Log.d(TAG, "JSONResponseHandler: getForecast: variables names: " + name);

                        if ("precipitation_amount".equals(name)) {
                            result.putString("precipitation_amount", name);
                            getPrecipitation(var, result);
                            Log.d(TAG, "JSONResponseHandler: getForecast: PRECIPITATION_AMOUNT");

                        } else if ("sky_state".equals(name)) {
                            result.putString("sky_state", name);
                            getSkyState(var, result);
                            Log.d(TAG, "JSONResponseHandler: getForecast: SKY_STATE");

                        } else if ("temperature".equals(name)) {
                            result.putString("temperature", name);
                            getTemperature(var, result);
                            Log.d(TAG, "JSONResponseHandler: getForecast: TEMP");

                        } else if ("wind".equals(name)) {
                            result.putString("wind", name);
                            getWind(var, result);
                            Log.d(TAG, "JSONResponseHandler: getForecast: WIND");

                        } else if ("snow_level".equals(name)) {
                            result.putString("snow_level", name);
                            getSnowLevel(var, result);
                            Log.d(TAG,"JSONResponseHandler: getForecast: snow_level");

                        } else if("relative_humidity".equals(name)) {
                            result.putString("relative_humidity", name);
                            getRelative_humidity(var, result);
                            Log.d(TAG,"JSONResponseHandler: getForecast: relative_humidity");

                        } else if("cloud_area_fraction".equals(name)) {
                            result.putString("cloud_area_fraction", name);
                            getCloud_area_fraction(var, result);
                            Log.d(TAG,"JSONResponseHandler: getForecast: cloud_area_fraction");

                        } else if("air_pressure_at_sea_level".equals(name)) {
                            result.putString("air_pressure_at_sea_level", name);
                            getAir_pressure_at_sea_level(var, result);

                        } else if("sea_water_temperature".equals(name)) {
                            result.putString("sea_water_temperature", name);
                            getSea_water_temperature(var, result);

                        } else if("significative_wave_height".equals(name)) {
                            result.putString("significative_wave_height", name);
                            getSignificative_wave_height(var, result);

                        } else if("mean_wave_direction".equals(name)) {
                            result.putString("mean_wave_direction", name);
                            getMean_wave_direction(var,result);

                        } else if("relative_peak_period".equals(name)) {
                            result.putString("relative_peak_period", name);
                            getRelative_peak_period(var, result);

                        } else if("sea_water_salinity".equals(name)) {
                            result.putString("sea_water_salinity", name);
                            getSea_water_salinity(var, result);
                        }

                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return result;
        }
    }

    /* General variables */
    private void getPrecipitation (JSONObject jsonObject, Bundle bundle) throws JSONException {
        Log.d(TAG, "JSONResponseHandler: getPrecipitation");
        bundle.putString("precipitation_amount_units", jsonObject.getString("units"));

        JSONArray jsonArray = jsonObject.getJSONArray("values");
        JSONObject j = jsonArray.getJSONObject(0);

        bundle.putString("precipitation_amount_value", j.getString("value"));
    }

    private void getSkyState(JSONObject jsonObject, Bundle bundle) throws JSONException {
        Log.d(TAG, "JSONResponseHandler: getSkyState");

        JSONArray jsonArray = jsonObject.getJSONArray("values");
        JSONObject j = jsonArray.getJSONObject(0);

        bundle.putString("sky_state_url", j.getString("iconURL"));
        bundle.putString("sky_state_value", j.getString("value"));

        Log.d(TAG, "JSONResponseHandler: getSkyState sky_state_url " + j.getString("iconURL"));
    }

    private void getTemperature(JSONObject jsonObject, Bundle bundle) throws JSONException {
        Log.d(TAG, "JSONResponseHandler: getTemperature");
        bundle.putString("temperature_units", jsonObject.getString("units"));
        JSONArray jsonArray = jsonObject.getJSONArray("values");
        JSONObject j = jsonArray.getJSONObject(0);

        bundle.putString("temperature_value", j.getString("value"));
    }

    private void getWind(JSONObject jsonObject, Bundle bundle)  throws JSONException {
        Log.d(TAG, "JSONResponseHandler: getWind");
        bundle.putString("wind_module_units", jsonObject.getString("moduleUnits"));
        bundle.putString("wind_direction_units", jsonObject.getString("directionUnits"));


        JSONArray jsonArray = jsonObject.getJSONArray("values");
        JSONObject j = jsonArray.getJSONObject(0);

        bundle.putString("wind_direction_value", j.getString("directionValue"));
        bundle.putString("wind_module_value", j.getString("moduleValue"));
        bundle.putString("wind_direction_iconURL", j.getString("iconURL"));

    }

    private void getSnowLevel(JSONObject jsonObject, Bundle bundle) throws JSONException{
        Log.d(TAG, "JSONResponseHandler: getSnow_level");
        bundle.putString("snow_level_units", jsonObject.getString("units"));

        JSONArray jsonArray = jsonObject.getJSONArray("values");
        JSONObject j = jsonArray.getJSONObject(0);
        bundle.putString("snow_level_value", j.getString("value"));

    }

    // TODO next function is UNTESTED. snow precipitation UNITS are NOT saved
    private void getSnow_precipitation(JSONObject jsonObject, Bundle bundle) throws JSONException{
        Log.d(TAG, "JSONResponseHandler: getSnow_precipitation");

        JSONArray jsonArray = jsonObject.getJSONArray("values");
        JSONObject j = jsonArray.getJSONObject(0);

        bundle.putString("snow_precipitation_value", j.getString("value"));
    }

    private void getRelative_humidity(JSONObject jsonObject, Bundle bundle) throws JSONException{
        Log.d(TAG, "JSONResponseHandler: getRelative_humidity");
        bundle.putString("relative_humidity_units", jsonObject.getString("units"));
        JSONArray jsonArray = jsonObject.getJSONArray("values");
        JSONObject j = jsonArray.getJSONObject(0);

        bundle.putString("relative_humidity_value", j.getString("value"));
    }

    private void getCloud_area_fraction(JSONObject jsonObject, Bundle bundle) throws JSONException{
        Log.d(TAG, "JSONResponseHandler: getCloud_area_fraction ");
        bundle.putString("cloud_area_fraction_units", jsonObject.getString("units"));

        JSONArray jsonArray = jsonObject.getJSONArray("values");
        JSONObject j = jsonArray.getJSONObject(0);

        bundle.putString("cloud_area_fraction_value", j.getString("value"));

        Log.d(TAG, "JSONResponseHandler: getCloud_area_fraction units: "
                + jsonObject.getString("units"));
        Log.d(TAG, "JSONResponseHandler: getCloud_area_fraction value: "
                + j.getString("value"));
    }

    /* Beach variables */
    private void getAir_pressure_at_sea_level(JSONObject jsonObject, Bundle bundle) throws JSONException {
        Log.d(TAG, "JSONResponseHandler: getAir_pressure_at_sea_level");
        bundle.putString("air_pressure_at_sea_level_units", jsonObject.getString("units"));
        JSONArray jsonArray = jsonObject.getJSONArray("values");
        JSONObject j = jsonArray.getJSONObject(0);
        bundle.putString("air_pressure_at_sea_level_value", j.getString("value"));
    }

    private void getSignificative_wave_height(JSONObject jsonObject, Bundle bundle) throws JSONException{
        Log.d(TAG, "JSONResponseHandler: getSignificative_wave_height");
        bundle.putString("significative_wave_height_units", jsonObject.getString("units"));

        JSONArray jsonArray = jsonObject.getJSONArray("values");
        JSONObject j = jsonArray.getJSONObject(0);

        bundle.putString("significative_wave_height_value", j.getString("value"));
    }

    private void getRelative_peak_period(JSONObject jsonObject, Bundle bundle) throws JSONException{
        Log.d(TAG, "JSONResponseHandler: getRelative_peak_period");
        bundle.putString("relative_peak_period_units", jsonObject.getString("units"));

        JSONArray jsonArray = jsonObject.getJSONArray("values");
        JSONObject j = jsonArray.getJSONObject(0);

        bundle.putString("relative_peak_period_value", j.getString("value"));
    }

    private void getMean_wave_direction(JSONObject jsonObject, Bundle bundle) throws JSONException{
        Log.d(TAG, "JSONResponseHandler: getMean_wave_direction");
        bundle.putString("mean_wave_direction_units", jsonObject.getString("units"));

        JSONArray jsonArray = jsonObject.getJSONArray("values");
        JSONObject j = jsonArray.getJSONObject(0);

        bundle.putString("mean_wave_direction_value", j.getString("value"));
        bundle.putString("mean_wave_direction_url", j.getString("iconURL"));
    }

    private void getSea_water_temperature(JSONObject jsonObject, Bundle bundle) throws JSONException{
        Log.d(TAG, "JSONResponseHandler: getSea_water_temperature");
        bundle.putString("sea_water_temperature_units", jsonObject.getString("units"));

        JSONArray jsonArray = jsonObject.getJSONArray("values");
        JSONObject j = jsonArray.getJSONObject(0);

        bundle.putString("sea_water_temperature_value", j.getString("value"));
    }

    private void getSea_water_salinity(JSONObject jsonObject, Bundle bundle) throws JSONException{
        Log.d(TAG, "JSONResponseHandler: getSea_water_salinity");
        bundle.putString("sea_water_salinity_units", jsonObject.getString("units"));

        JSONArray jsonArray = jsonObject.getJSONArray("values");
        JSONObject j = jsonArray.getJSONObject(0);

        bundle.putString("sea_water_salinity_value", j.getString("value"));
    }
}
