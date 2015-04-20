package es.udc.psi14.lab08trabazo;

import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

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

public class JsonTask extends AsyncTask<String, Void, List<String>> {

    @Override
    protected List<String> doInBackground(String... params) {
        AndroidHttpClient client = AndroidHttpClient.newInstance("");
        HttpGet request = new HttpGet(params[0]);
        JSONResponseHandler respHandler = new JSONResponseHandler();

        try {
            return client.execute(request, respHandler);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<String> strings) { //ListActivity or ListFragment
        super.onPostExecute(strings);
//        setListAdapter(new ArrayAdapter<String>(this, R.layout, strings));
    }
}

class JSONResponseHandler implements ResponseHandler<List<String>> {

    public List<String> handleResponse(HttpResponse resp) throws IOException {

        List<String> result = new ArrayList<>();
        String JSONResp=new BasicResponseHandler().handleResponse(resp);

        try {
            JSONObject object =
                    ((JSONObject) new JSONTokener(JSONResp).nextValue()).getJSONObject("posiciones");
            JSONArray posiciones = object.getJSONArray("posicion");
            for (int i = 0; i < posiciones.length(); i++) {
                JSONObject tmp = (JSONObject) posiciones.get(i);
                result.add(
                    "idlinea:" + tmp.get("idlinea") +
                    " idautobus:" + tmp.get("idautobus") +
                    " idparada:" + tmp.getString("idparada") +
                    " horaactualizacion:" + tmp.get("horaactualizacion")
                );
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;

    }
}