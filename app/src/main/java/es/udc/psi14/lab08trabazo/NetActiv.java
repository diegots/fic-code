package es.udc.psi14.lab08trabazo;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.ExecutionException;


public class NetActiv extends ActionBarActivity implements View.OnClickListener {

    final static String TAG = "LCA_TAG";
    final static String HARDCODED_MESSAGE = "Test message";
    final static String HARDCODED_HOSTNAME = "192.168.0.7";
    final static String HARDCODED_PORT_NUMBER = "12321";
    Button but_send;
    Button but_cancel;
    EditText et_host;
    EditText et_port;
    EditText et_msg;
    TextView tv_display;

    public void updateDisplay(String data) {
        tv_display.setText(data);
    }

    private void but_send_f () {

        // Get hostname
        String hostname = et_host.getText().toString();
        if ("".equals(hostname)) {
            hostname = HARDCODED_HOSTNAME;
            et_host.setText(HARDCODED_HOSTNAME);
        }

        // Get port number
        String portNumber = et_port.getText().toString();
        if ("".equals(portNumber)) {
            portNumber = HARDCODED_PORT_NUMBER;
            et_port.setText(HARDCODED_PORT_NUMBER);
        }

        // Get message
        String message = et_msg.getText().toString();
        if ("".equals(message)) {
            message = HARDCODED_MESSAGE;
            et_msg.setText(HARDCODED_MESSAGE);
        }

        String result;

        SocketTask socketTask = new SocketTask(this);
        try {
            result = socketTask.execute(hostname, portNumber, message).get().toString();
            tv_display.setText(result);
        } catch (ExecutionException ee) {
            Log.d(NetActiv.TAG, "IOException: " + ee.getMessage());
        } catch (InterruptedException ie) {
            Log.d(NetActiv.TAG, "IOException: " + ie.getMessage());
        }





    }

    private void but_cancel_f () {
        // Clear tv_display
        tv_display.setText("");
    }

    @Override
    public void onClick(View v) {
        Button b = (Button) v;
        switch (b.getId()) {
            case R.id.but_cancel:
                but_cancel_f();
                break;
            case R.id.but_send:
                but_send_f();
                break;
        }
    }

    void init () {
        but_send = (Button) findViewById(R.id.but_send);
        but_cancel = (Button) findViewById(R.id.but_cancel);
        et_host = (EditText) findViewById(R.id.et_host);
        et_port = (EditText) findViewById(R.id.et_port);
        et_msg = (EditText) findViewById(R.id.et_msg);
        tv_display = (TextView) findViewById(R.id.tv_display);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net);

        init();
        but_send.setOnClickListener(this);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_net, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}
