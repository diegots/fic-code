package udc.es.meteoapp.model;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateUtils;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

        date += 60*60*1000;
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

        Bundle retrievedForecast = new Bundle();
        Message message = new Message();

        String url = buildQueryURL();

        message.setData(retrievedForecast);
        handler.sendMessage(message);
    }
}
