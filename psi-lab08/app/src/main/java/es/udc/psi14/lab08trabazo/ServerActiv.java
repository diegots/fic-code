package es.udc.psi14.lab08trabazo;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ServerActiv extends ActionBarActivity implements View.OnClickListener,
        AdapterView.OnItemClickListener {

    private final static String banner = "[ServerActiv] ";

    final static String KEY_CLIENT_THREAD_IP = "KEY_CLIENT_THREAD_IP";
    final static String KEY_CLIENT_THREAD_PORT = "KEY_CLIENT_THREAD_PORT";
    final static String KEY_CLIENT_THREAD_LINE = "KEY_CLIENT_THREAD_LINE";
    final static String KEY_CLIENT_THREAD_ENDLINE = "KEY_CLIENT_THREAD_ENDLINE";
    final static int HARDCODED_SERVER_PORT = 10500;

    Button server_activ_but_listen;
    Button server_activ_but_close;
    EditText server_activ_et_port;
    TextView server_activ_tv_ip;
    TextView server_activ_tv_client;
    TextView server_activ_tv_display;
    ScrollView server_activ_scrollView;
    ListView server_activ_list_view;

    int port;
    ServerThread serverThread;
    Handler serverActivHandler;
    HashMap<String, List<String>> clientsData;
    ArrayAdapter<String> clientsArrayAdapter;

    void setDefaultPort () {

        port = HARDCODED_SERVER_PORT;
        server_activ_et_port.setText(HARDCODED_SERVER_PORT + "");
        Log.d(NetActiv.TAG, banner + "setDefaultPort: " + HARDCODED_SERVER_PORT);
    }

    void but_close_f () {

        if (serverThread != null) {
            serverThread.terminate(port);
            server_activ_tv_display.setText("");
            server_activ_tv_ip.setText("");
            clientsData.clear();
            clientsArrayAdapter.clear();
            serverThread = null;
            Log.d(NetActiv.TAG, banner + "but_close_f: Echo server finished");

        } else {
            Log.d(NetActiv.TAG, banner + "but_close_f: No Echo server running!");
            Toast.makeText(this, "No Echo server running!", Toast.LENGTH_SHORT).show();
        }
    }

    void but_listen_f () {

        if ("".equals(server_activ_et_port.getText().toString())) {
            Log.d(NetActiv.TAG, banner + "but_listen_f: setting default port value");
            setDefaultPort();
        }

        Log.d(NetActiv.TAG, banner + "but_listen_f: starting Echo Server on port " + port);
        if (serverThread != null) {
            Toast.makeText(this, "Echo server already running!", Toast.LENGTH_SHORT).show();
            Log.d(NetActiv.TAG, banner + "but_listen_f: echo server already running!");
        } else {
            serverThread = new ServerThread("serverThread", port, serverActivHandler);
            serverThread.start();
            Log.d(NetActiv.TAG, banner + "but_listen_f: starting Echo Server on port " + port);
        }
    }

    public String getIpAddr() { // permissions INTERNET & ACCESS_WIFI_STATE
        WifiManager wifiManager=(WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();
        return String.format("%d.%d.%d.%d", (ip & 0xff), (ip >> 8 & 0xff),
                (ip >> 16 & 0xff), (ip >> 24 & 0xff));
    }

    public String getIpAddr2() {

        Enumeration<NetworkInterface> en;

        try {


            for (en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {

                NetworkInterface intf = en.nextElement();

                for (Enumeration<InetAddress> eIpAddr = intf.getInetAddresses();
                     eIpAddr.hasMoreElements();) {

                    InetAddress inet = eIpAddr.nextElement();
                    if (!inet.isLoopbackAddress() && !inet.isLinkLocalAddress()) {
                        Log.d(NetActiv.TAG, banner + "getIpAddr2: setting ip: " + inet.getHostAddress());
                        return inet.getHostAddress();
                    }

                }
            }
        } catch (SocketException se) {
            Log.d(NetActiv.TAG, banner + "getIpAddr2: couldn't get IP address!");
        }
        return "0.0.0.0";
    }

    void initViews () {
        server_activ_but_listen = (Button) findViewById(R.id.server_activ_but_listen);
        server_activ_but_close = (Button) findViewById(R.id.server_activ_but_close);
        server_activ_et_port = (EditText) findViewById(R.id.server_activ_et_port);
        server_activ_tv_ip = (TextView) findViewById(R.id.server_activ_tv_ip);
        server_activ_tv_client = (TextView) findViewById(R.id.server_activ_tv_client);
        server_activ_tv_display = (TextView) findViewById(R.id.server_activ_tv_display);
        server_activ_scrollView = (ScrollView) findViewById(R.id.server_activ_scrollView);
        server_activ_list_view = (ListView) findViewById(R.id.server_activ_list_view);

        server_activ_but_listen.setOnClickListener(this);
        server_activ_but_close.setOnClickListener(this);
        server_activ_list_view.setOnItemClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        initViews();
        //server_activ_tv_ip.setText(getIpAddr());
        server_activ_tv_ip.setText(getIpAddr2());

        // Avoid showing keyboard on activity start.
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        serverActivHandler = new ServerActivHander(this);
        clientsData = new HashMap<>();
        clientsArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_server, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        Button b = (Button) v;

        switch (b.getId()) {
            case R.id.server_activ_but_listen:
                Log.d(NetActiv.TAG, banner + "onClick: listen button");
                but_listen_f();
                break;

            case R.id.server_activ_but_close:
                Log.d(NetActiv.TAG, banner + "onClick: close button");
                but_close_f();
                break;
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        final String item = (String) parent.getItemAtPosition(position);
        Log.d(NetActiv.TAG, banner + "onItemClick: listView item selected: " + item);

        List<String> l = clientsData.get(item);
        StringBuilder b = new StringBuilder();
        Iterator<String> il = l.iterator();
        while (il.hasNext())
            b.append(il.next());

        server_activ_tv_display.setText(b.toString());
        server_activ_scrollView.post(new Runnable() {
            public void run() {
                server_activ_scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

    }
}

class ServerActivHander extends Handler {

    private final static String banner = "[ServerActivHander] ";
    ServerActiv serverActiv;

    ServerActivHander (Context context) {
        serverActiv = (ServerActiv) context;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);

        Bundle b = msg.getData();
        String [] data = {};
        String id;
        String receivedLine;

        if (b.getStringArray(ServerActiv.KEY_CLIENT_THREAD_IP) != null)
            data = b.getStringArray(ServerActiv.KEY_CLIENT_THREAD_IP);
        else if (b.getStringArray(ServerActiv.KEY_CLIENT_THREAD_PORT) != null)
            data = b.getStringArray(ServerActiv.KEY_CLIENT_THREAD_PORT);
        else if (b.getStringArray(ServerActiv.KEY_CLIENT_THREAD_LINE) != null)
            data = b.getStringArray(ServerActiv.KEY_CLIENT_THREAD_LINE);
        else if (b.getStringArray(ServerActiv.KEY_CLIENT_THREAD_ENDLINE) != null)
            data = b.getStringArray(ServerActiv.KEY_CLIENT_THREAD_ENDLINE);

        id = data[0];
        receivedLine = data[1];

        insertData(id, receivedLine);
        updateClientsListView();

        Log.d(NetActiv.TAG, banner + "handleMessage: " + id + " " + receivedLine);

    }

    void updateClientsListView () {
        Set<String> s = serverActiv.clientsData.keySet();
        serverActiv.clientsArrayAdapter.clear();
        serverActiv.clientsArrayAdapter.addAll(s);
        serverActiv.server_activ_list_view.setAdapter(serverActiv.clientsArrayAdapter);
    }

    private void insertData (String id, String receivedLine) {
        if (serverActiv.clientsData.containsKey(id)) {
            // Key already in HashMap
            List<String> l = serverActiv.clientsData.get(id);
            l.add(receivedLine + "\n");

        } else {
            // Key not found
            List<String> l = new ArrayList<>();
            l.add(receivedLine + "\n");
            serverActiv.clientsData.put(id, l);
        }
    }
}

class ServerThread extends Thread {
    private final static String banner = "[ServerThread] ";

    int port;
    EchoServer echoServer;
    Handler handler;

    public ServerThread(String name, int port, Handler handler) {
        super (name);
        this.port = port;
        this.handler = handler;
    }

    public void run() {
        Log.d(NetActiv.TAG, banner + "run: start EchoServer");
        echoServer = new EchoServer(handler);
        echoServer.start(port);

    }

    public void terminate(int port) {
        Log.d(NetActiv.TAG, banner + "terminate");
        echoServer.terminate();
    }

}