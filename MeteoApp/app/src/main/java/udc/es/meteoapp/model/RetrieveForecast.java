package udc.es.meteoapp.model;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class RetrieveForecast extends Thread {

    String TAG = "MeteoApp";

    String locality_api_id;
    Handler handler;

    RetrieveForecast(String threadName, Handler handler, String locality_api_id) {
        this.handler = handler;
        this.locality_api_id = locality_api_id;
    }

    @Override
    public void run() {
        super.run();
        Log.d(TAG, "RetrieveForecast: run");

        Bundle retrievedForecast = new Bundle();
        Message message = new Message();

        message.setData(retrievedForecast);
        handler.sendMessage(message);
    }
}
