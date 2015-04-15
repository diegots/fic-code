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

    NetActiv netActiv;
    private Context context;
    private final static String banner = "[SocketTask] ";

    public SocketTask(Context context) {
        super();
        netActiv = (NetActiv) context;
    }

    @Override
    protected String doInBackground(String[] params) {

        Socket clientSocket = null;
        PrintWriter out;
        BufferedReader in = null;

        // Get params
        String hostname = params[0];
        int portNumber = Integer.parseInt(params[1]);
        String message = params[2];

        StringBuffer bufferData = new StringBuffer();
        String rawData;

        try {
            Log.d(NetActiv.TAG, banner + "doInBackground: Opening socket to: " + hostname + ":" + portNumber);
            clientSocket = new Socket(hostname, portNumber);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));

            Log.d(NetActiv.TAG, banner + "doInBackground: sending '" + message + "'");
            out.println(message);
            Thread.sleep((long) 200);
            boolean inReady = in.ready();
            Log.d(NetActiv.TAG, banner + "doInBackground: in.ready: " + Boolean.toString(inReady));
            while (inReady && (rawData = in.readLine()) != null) {
                Log.d(NetActiv.TAG, banner + "doInBackground: while rawdata: " + rawData);
                publishProgress(rawData);
                bufferData.append(rawData + "\n");
                Thread.sleep((long) 200);
                inReady = in.ready();
                Log.d(NetActiv.TAG, banner + "doInBackground: while in.ready: " + Boolean.toString(inReady));
            }
        } catch (IOException ioe) {
            Log.d(NetActiv.TAG, banner + "doInBackground: " + ioe.getMessage());
            return ioe.getMessage();
        } catch (InterruptedException ie) {
            Log.d(NetActiv.TAG, banner + "doInBackground: " + ie.getMessage());
        } finally {
            try {
                if (in != null)
                    in.close();
                if (clientSocket != null)
                    clientSocket.close();
            } catch (IOException ioe) {
                Log.d(NetActiv.TAG, banner + "doInBackground: " + ioe.getMessage());
            }

        }
        Log.d(NetActiv.TAG, banner + "doInBackground finishing");
        return bufferData.toString();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        Log.d("LCA_TAG", banner + "onProgressUpdate Given value: " + values[0]);

        netActiv.tv_line.setText(values[0]);
    }
}
