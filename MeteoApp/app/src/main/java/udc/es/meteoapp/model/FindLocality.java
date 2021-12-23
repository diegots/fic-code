package udc.es.meteoapp.model;

import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;

class FindLocality extends Thread {

    String TAG = "MeteoApp";

    Handler handler;
    String locality_name;
    String locality_id;
    String apiKey;

    public FindLocality(String threadName, Handler handler, String locality_id, String locality_name,
                        String apiKey) {
        super(threadName);
        this.handler = handler;
        this.locality_name = locality_name;
        this.locality_id = locality_id;
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
        //Log.d(TAG, "FindLocalityHandler - buildQueryURL:" + newLocalityName);

        String url =
                new StringBuilder("http://servizos.meteogalicia.es/apiv3/findPlaces?location=")
                        .append(newLocalityName)
                        .append("&API_KEY=")
                        .append(apiKey).toString();
        //Log.d(TAG, "FindLocalityHandler - buildQueryURL:" + url);

        return url;
    }

    private class JSONResponseHandler{

        public ArrayList<Bundle> getLocalities(HttpResponse httpResponse) throws IOException {
            Log.d(TAG, "JSONResponseHandler: getLocalities");

            ArrayList<Bundle> result = new ArrayList<>();

            String JSONResp = new BasicResponseHandler().handleResponse(httpResponse);
            //Log.d(TAG, "JSONResponseHandler: " + JSONResp);
            Log.d(TAG, "JSONResponseHandler: status code: " + httpResponse.getStatusLine()
                .getStatusCode());

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
                        //Log.d(TAG, "JSONResponseHandler: " + tmp.toString());

                        if(tmp.has("properties")) {
                            JSONObject jsonObject = tmp.getJSONObject("properties");
                            String id = jsonObject.getString("id");
                            String municipality = jsonObject.getString("municipality");
                            String name = jsonObject.getString("name");
                            String province = jsonObject.getString("province");
                            String type = jsonObject.getString("type");

                            Bundle bundle = new Bundle();
                            bundle.putString("id", id);
                            bundle.putString("municipality",municipality);
                            bundle.putString("name", name);
                            bundle.putString("province",province);
                            bundle.putString("type",type);

                            result.add(bundle);
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
        Log.d(TAG, "FindLocality: run: looking for: '" + locality_name + "'");

        String queryURL = buildQueryURL(locality_name);

        // Store locality_id, which is the internal id used to identify the PlaceItem
        Bundle bundle = new Bundle();
        bundle.putString("locality_id", locality_id);

        ArrayList<Bundle> list = null;

        // The first time app is run or if data is not present
        if (PlacesContent.ITEM_MAP.get(locality_id) != null &&
            PlacesContent.ITEM_MAP.get(locality_id).locality_api_id.equals("")) {

            list = doQueryAPI(queryURL);
        } else {

            list = new ArrayList<>();
            Bundle localityBundle = new Bundle();

            PlacesContent.PlaceItem pp = PlacesContent.ITEM_MAP.get(locality_id);

            localityBundle.putString("id", pp.locality_api_id);
            localityBundle.putString("municipality",pp.locality_municipality);
            localityBundle.putString("name", pp.locality_name);
            localityBundle.putString("province", pp.locality_province);
            localityBundle.putString("type", pp.locality_type);
            list.add(localityBundle);

        }

        // Prepare data to be sent back
        bundle.putParcelableArrayList("received_data", list);
        Message message = new Message();
        message.setData(bundle);

        // Send data to main thread
        handler.sendMessage(message);

    }

    private ArrayList<Bundle> doQueryAPI(String queryURL) {
        Log.d(TAG, "FindLocality: doQueryAPI");

        // Do the query in the API
        AndroidHttpClient client = AndroidHttpClient.newInstance("");
        HttpGet request = new HttpGet(queryURL);

        ArrayList<Bundle> list = new ArrayList<>();
        try {
            // Connect to API and execute query
            HttpResponse response = client.execute(request);

            // Parse received data
            JSONResponseHandler json = new JSONResponseHandler();
            list = json.getLocalities(response);
            //Log.d(TAG, "FindLocality: run: Found: '" + list.get(0).get("id") + "' ID");

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Explanation on why use filally:
            // http://javabasico.osmosislatina.com/curso/progavanza2/trycatch.htm
            client.close();
        }

        return list;
    }
}



