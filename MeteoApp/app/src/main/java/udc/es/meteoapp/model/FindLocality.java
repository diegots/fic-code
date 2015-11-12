package udc.es.meteoapp.model;

import android.net.http.AndroidHttpClient;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class FindLocality extends Thread {

    String TAG = "MeteoApp";

    Handler handler;
    String locality_name;
    String apiKey;
    String queryURL;

    public FindLocality(String threadName, Handler handler, String locality_name, String apiKey) {
        super(threadName);
        this.handler = handler;
        this.locality_name = locality_name;
        this.apiKey = apiKey;
    }

    private String buildQueryURL (String locality_name) {
        // Remove whites
        StringBuilder newLocalityName = new StringBuilder(locality_name.trim());
        int whiteIndex = newLocalityName.indexOf(" ");
        while (whiteIndex != -1) {
            newLocalityName.deleteCharAt(whiteIndex);
            newLocalityName.insert(whiteIndex, "%20");
            whiteIndex = newLocalityName.indexOf(" ");
        }

        Log.d(TAG, "FindLocalityHandler - buildQueryURL:" + newLocalityName);

        String url =
                new StringBuilder("http://servizos.meteogalicia.es/apiv3/findPlaces?location=")
                        .append(newLocalityName)
                        .append("&API_KEY=")
                        .append(apiKey).toString();

        Log.d(TAG, "FindLocalityHandler - buildQueryURL:" + url);

        return url;
    }

    private class JSONResponseHandler implements ResponseHandler<List<String>> {

        @Override
        public List<String> handleResponse(HttpResponse httpResponse) throws IOException {
            Log.d(TAG, "handleResponse");

            List<String> result = new ArrayList<>();

            String JSONResp = new BasicResponseHandler().handleResponse(httpResponse);
            //Log.d(TAG, "JSONResponseHandler: " + JSONResp);
            Log.d(TAG, "JSONResponseHandler: status code: " + httpResponse.getStatusLine().getStatusCode());

            JSONTokener jt = new JSONTokener(JSONResp);
            //Log.d(TAG, "JSONResponseHandler: " + jt.toString());

            JSONObject object;
            JSONArray jsonArray = new JSONArray();

            try {
                while (jt.more()) {
                    object = (JSONObject) jt.nextValue();

                    if(object.has("features"))
                        jsonArray = object.getJSONArray("features");
                    for (int i = 0; i<jsonArray.length(); i++) {
                        JSONObject tmp = (JSONObject) jsonArray.get(i);
                        Log.d(TAG, "JSONResponseHandler: " + tmp.toString());

                        if(tmp.has("properties")) {
                            JSONObject jsonObject = tmp.getJSONObject("properties");
                            String id = jsonObject.getString("id");
                            String municipality = jsonObject.getString("municipality");
                            String name = jsonObject.getString("name");
                            String province = jsonObject.getString("province");
                            String type = jsonObject.getString("type");

                            Log.d(TAG, "JSONResponseHandler: "
                                    + "id: " + id
                                    + " municipality:" + municipality
                                    + " name:" + name
                                    + " province:" + province
                                    + " type:" + type);
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return result;
        }
    }

    @Override
    public void run() {
        super.run();
        queryURL = buildQueryURL(locality_name);
        Log.d(TAG, "FindLocality: run - find: '" + locality_name + "' API id");

        List<String> result = new ArrayList<>();
        AndroidHttpClient client = AndroidHttpClient.newInstance("");
        HttpGet request = new HttpGet(queryURL);

        List<String> ls = new ArrayList<>();
        JSONResponseHandler respHandler = new JSONResponseHandler();

        try {
            ls = client.execute(request, respHandler);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class FindLocalityHandler extends Handler {

    String TAG = "MeteoApp";

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        Log.d(TAG, "FindLocalityHandler: handleMessage");

    }
}
