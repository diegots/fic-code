package es.udc.psi14.lab08trabazo;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class SocketTask extends AsyncTask<String, String, String> {

    private Context context;

    public SocketTask(Context context) {
        super();
        this.context = context;
    }

    @Override
    protected String doInBackground(String[] params) {

        Socket clientSocket;

        // Get params
        String hostname = (String) params[0];
        int portNumber = Integer.parseInt((String) params[1]);
        String message = (String) params[2];

        StringBuffer bufferData = new StringBuffer();
        String rawData;

        try {
            Log.d(NetActiv.TAG, "Opening socket to: " + hostname + ":" + portNumber);
            clientSocket = new Socket(hostname, portNumber);
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));

            out.println(message);
            while (in.ready() && (rawData = in.readLine()) != null) {

                bufferData.append(rawData);
                publishProgress(rawData);


            }
            clientSocket.close();

        } catch (IOException ioe) {
            Log.d(NetActiv.TAG, "IOException: " + ioe.getMessage());
        }

        return bufferData.toString();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        Log.d("LCA_TAG", "[SocketTask] - onProgressUpdate: " + values[0]);
        NetActiv netActiv = (NetActiv) context;
        netActiv.tv_display.setText(values[0]);
    }
}
