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
        String vars = "sky_state,temperature,wind,precipitation_amount";

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); // Format any date like this

        long date = Calendar.getInstance().getTimeInMillis();
        String startTime = format.format(new Date(date));
        //Log.d(TAG, "RetrieveForecast: buildQueryURL: startTime: " + startTime);

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
            Bundle forecast;
            try {
                forecast = jsonHandler.getForecast(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }
//
//            // Prepare data to be sent back
//            Bundle retrievedForecast = new Bundle();
//            retrievedForecast.putParcelableArrayList("received_data", forecast);
            Message message = new Message();
//            message.setData(retrievedForecast);

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

                    for (int i = 0; i<variables.length(); i++) {
                        jsonObj = (JSONObject) variables.get(i);
                        String name = jsonObj.getString("name");

                        if ("precipitation_amount".equals(name)) {
                            result = precipitation(jsonObj, result);
                        } else if ("sky_state".equals(name)) {
                            Log.d(TAG, "JSONResponseHandler: getForecast: SKY_STATE");
                        } else if ("temperature".equals(name)) {
                            Log.d(TAG, "JSONResponseHandler: getForecast: TEMP");
                        } else if ("wind".equals(name)) {
                            Log.d(TAG, "JSONResponseHandler: getForecast: WIND");
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return result;
        }
    }

    private Bundle precipitation (JSONObject jsonObject, Bundle bundle) throws JSONException {
        JSONArray jsonArray = jsonObject.getJSONArray("values");
        JSONObject j = jsonArray.getJSONObject(0);

        bundle.putString("precipitation_amount", "precipitation_amount");
        bundle.putString("precipitation_amount_value", j.getString("value"));
        return bundle;
    }
}
