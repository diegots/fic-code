package udc.es.meteoapp.model;

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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class RetrieveForecast extends Thread {

    String TAG = "MeteoApp";

    String locality_api_id;
    String apiKey;
    Handler handler;

    RetrieveForecast(String threadName, Handler handler, String locality_api_id, String apiKey) {
        super(threadName);
        this.handler = handler;
        this.locality_api_id = locality_api_id;
        this.apiKey = apiKey;
    }

    private String buildQueryURL () {
        String vars = "sky_state,temperature,wind,precipitation_amount,relative_humidity," +
                "cloud_area_fraction,snow_level"+ ",air_pressure_at_sea_level," +
                "significative_wave_height,relative_peak_period,mean_wave_direction," +
                "sea_water_temperature,sea_water_salinity";

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

        String queryURL = buildQueryURL();

        AndroidHttpClient client = AndroidHttpClient.newInstance("");
        HttpGet request = new HttpGet(queryURL);

        try {
            // Connect to API and execute query
            HttpResponse response = client.execute(request);

            // Parse received data
            JSONRetrieveForecasHandler jsonHandler = new JSONRetrieveForecasHandler();
            Bundle forecast = null;
            try {
                forecast = jsonHandler.getForecast(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Prepare data to be sent back
            Message message = new Message();
            message.setData(forecast);

            // Send data to main thread
            handler.sendMessage(message);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            client.close();
        }
    }

    private class JSONRetrieveForecasHandler {

        public Bundle getForecast(HttpResponse httpResponse) throws IOException, JSONException {
            Log.d(TAG, "JSONRetrieveForecasHandler: getForecast");

            // ArrayList to store JSON parsed result
            //ArrayList<Bundle> result = new ArrayList<>();
            Bundle result = new Bundle();

            String JSONResp = new BasicResponseHandler().handleResponse(httpResponse);
            //Log.d(TAG, "JSONRetrieveForecasHandler: getForecast: " + JSONResp);
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

                    JSONArray variables = jsonObj.getJSONArray("variables");
                    //Log.d(TAG, "JSONResponseHandler: getForecast: variables: " + variables.length());
                    for (int i = 0; i<variables.length(); i++) {
                        JSONObject var = (JSONObject) variables.get(i);
                        String name = var.getString("name");
                        Log.d(TAG, "JSONResponseHandler: getForecast: variables names: " + name);

                        if ("precipitation_amount".equals(name)) {
                            result.putString("precipitation_amount", name);
                            getPrecipitation(var, result);

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

                        }else if ("snow_level".equals(name)){
                            result.putString("snow_level", name);
                            getSnowLevel(var, result);
                            Log.d(TAG,"JSONResponseHandler: getForecast: snow_level");

                        } else if("relative_humidity".equals(name)){
                            result.putString("relative_humidity", name);
                            getRelative_humidity(var, result);
                            Log.d(TAG,"JSONResponseHandler: getForecast: relative_humidity");

                        }else if("cloud_area_fraction".equals(name)){
                            result.putString("cloud_area_fraction", name);
                            getCloud_area_fraction(var, result);
                            Log.d(TAG,"JSONResponseHandler: getForecast: cloud_area_fraction");
                        }else if("air_pressure_at_sea_level".equals(name)){
                            result.putString("air_pressure_at_sea_level", name);
                            getAir_pressure_at_sea_level(var, result);
                        }else if("sea_water_temperature".equals(name)){
                            result.putString("sea_water_temperature", name);
                            getSea_water_temperature(var, result);
                        }else if("significative_wave_height".equals(name)){
                            result.putString("significative_wave_height", name);
                            getSignificative_wave_height(var, result);
                        }else if("mean_wave_direction".equals(name)){
                            result.putString("mean_wave_direction", name);
                            getMean_wave_direction(var,result);
                        }else if("relative_peak_period".equals(name)){
                            result.putString("relative_peak_period", name);
                            getRelative_peak_period(var, result);
                        }else if("sea_water_salinity".equals(name)){
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
        Log.d(TAG, "JSONResponseHandler: getSkyState sky_state_url " + j.getString("iconURL"));

        bundle.putString("sky_state_value", j.getString("value"));
        bundle.putString("sky_state_value", j.getString("value"));

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

    private void  getSnowLevel(JSONObject jsonObject, Bundle bundle) throws JSONException{
        Log.d(TAG, "JSONResponseHandler: getSnow_level");
        bundle.putString("snow_level_units", jsonObject.getString("units"));

        JSONArray jsonArray = jsonObject.getJSONArray("values");
        JSONObject j = jsonArray.getJSONObject(0);
        bundle.putString("snow_level_value", j.getString("value"));

    }

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
        Log.d(TAG, "JSONResponseHandler: getCloud_area_fraction UNITS " + jsonObject.getString("units"));


        JSONArray jsonArray = jsonObject.getJSONArray("values");
        JSONObject j = jsonArray.getJSONObject(0);

        bundle.putString("cloud_area_fraction_value", j.getString("value"));
    }

    /*Beach */
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
