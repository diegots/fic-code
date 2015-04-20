package es.udc.psi14.lab08trabazo;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.util.ArrayList;


public class PostTask extends AsyncTask<String, String, String> {

    private final static String banner = "[PostTask] ";

    @Override
    protected String doInBackground(String... params) {

        String username = params[0];
        String passwd = params[1];
        String ip = params[2];
        String result = "";
        Log.d(NetActiv.TAG, banner + "Params: " + username + ", " + passwd + ", " + ip);

        class MyResponseHandler implements ResponseHandler<String> {
            private final static String banner = "[MyResponseHandler] ";

            @Override
            public String handleResponse(HttpResponse response) {
                InputStream content;
                String result = "";

                try {
                    content = response.getEntity().getContent();
                    byte[] buffer = new byte[1024];
                    int numRead = 0;
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();

                    while ((numRead=content.read(buffer)) != -1)
                        stream.write(buffer, 0, numRead);

                    content.close();
                    result = new String(stream.toByteArray());
                    return result;
                } catch (IOException ioe) {
                    Log.d(NetActiv.TAG, banner + ioe.getMessage());
                }
                return result;
            }
        }

        // Build POST parameters
        ArrayList<BasicNameValuePair> par = new ArrayList<>();
        par.add(new BasicNameValuePair("username", username));
        par.add(new BasicNameValuePair("password", passwd));

        try {

            // Build POST query
            HttpEntity postEntity = new UrlEncodedFormEntity(par, HTTP.UTF_8);
            HttpPost request = new HttpPost("http://" + ip + "/query.php");
            request.setEntity(postEntity);

            HttpClient client = new DefaultHttpClient();
            MyResponseHandler myResponseHandler = new MyResponseHandler();

            result = client.execute(request, myResponseHandler);

        } catch (UnsupportedEncodingException uee) {
            Log.d(NetActiv.TAG, banner + uee.getMessage());
        } catch (ClientProtocolException cpe) {
            Log.d(NetActiv.TAG, banner + cpe.getMessage());
        } catch (IOException ioe) {
            Log.d(NetActiv.TAG, banner + ioe.getMessage());
        }

        return result;
    }
}
