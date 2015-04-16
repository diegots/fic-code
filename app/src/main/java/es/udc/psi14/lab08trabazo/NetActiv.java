package es.udc.psi14.lab08trabazo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.concurrent.ExecutionException;


public class NetActiv extends ActionBarActivity implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener, View.OnLongClickListener {

    final static String TAG = "LCA_TAG";
    private final static String banner = "[NetActiv] ";

    final static String HARDCODED_ECHO_MESSAGE = "This is the echo message";
    final static String HARDCODED_ECHO_HOSTNAME = "localhost";
    final static String HARDCODED_ECHO_PORT_NUMBER = "12321";
    final static String HARDCODED_HTTP_MESSAGE = "GET /inicio.html";
    final static String HARDCODED_HTTP_HOSTNAME = "gac.udc.es";
    final static String HARDCODED_HTTP_PORT_NUMBER = "80";

    Button but_back;
    Button but_forward;
    Button but_clear;
    Button but_reload;
    Button but_get;
    Button but_send;
    Button but_cancel;
    ToggleButton but_msg_type;
    EditText et_host;
    EditText et_port;
    EditText et_msg;
    TextView tv_line;
    TextView tv_display;
    WebView webView;
    String hostname;
    String portNumber;
    String message;
    LinearLayout ll_web_view;

    private class Callback extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading (
                WebView view, String url) { return false; }
    }

    private void onGet (View view) {
        String url = et_host.getText().toString();
        if (!url.startsWith("http://www.")) url = "http://www." + url;
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webView.setWebViewClient(new Callback());
        webView.loadUrl(url);
    }

    private void but_send_f () {

        et_host.setText(hostname);
        et_port.setText(portNumber);
        et_msg.setText(message);

        SocketTask socketTask = new SocketTask(this);
        try {
            String result = socketTask.execute(hostname, portNumber, message).get().toString();
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
        et_host.setText("");
        et_port.setText("");
        et_msg.setText("");
    }

    /* Show or hides webView when a long click is performed to tv_display */
    private void showHideBrowser() {
        if (ll_web_view.getVisibility() == View.VISIBLE) {
            Log.d(TAG, banner + "showHideWView: hiding webView");
            ll_web_view.setVisibility(View.INVISIBLE);
        }
        else {
            Log.d(TAG, banner + "showHideWView: unhiding webView");
            ll_web_view.setVisibility(View.VISIBLE);
        }
    }

    void setDefaultBehaviour (boolean isChecked) {
        Log.d(TAG, banner + "setDefaultBehaviour");
        hostname = et_host.getText().toString();
        portNumber = et_port.getText().toString();
        message = et_msg.getText().toString();

        // HTTP message
        if (isChecked && "".equals(hostname))
            hostname = HARDCODED_HTTP_HOSTNAME;
        if (isChecked && "".equals(portNumber))
            portNumber = HARDCODED_HTTP_PORT_NUMBER;
        if (isChecked && "".equals(message))
            message = HARDCODED_HTTP_MESSAGE;

        // ECHO message
        if (!isChecked && "".equals(hostname))
            hostname = HARDCODED_ECHO_HOSTNAME;
        if (!isChecked && "".equals(portNumber))
            portNumber = HARDCODED_ECHO_PORT_NUMBER;
        if (!isChecked && "".equals(message))
            message = HARDCODED_ECHO_MESSAGE;

        Log.d(TAG, banner + "setDefaultBehaviour message: " + message);
    }

    void initViews () {
        but_send = (Button) findViewById(R.id.but_send);
        but_cancel = (Button) findViewById(R.id.but_cancel);
        et_host = (EditText) findViewById(R.id.et_host);
        et_port = (EditText) findViewById(R.id.et_port);
        et_msg = (EditText) findViewById(R.id.et_msg);
        tv_display = (TextView) findViewById(R.id.tv_display);
        tv_line = (TextView) findViewById(R.id.tv_line);
        webView = (WebView) findViewById(R.id.web_view);
        but_msg_type = (ToggleButton) findViewById(R.id.but_msg_type);
        ll_web_view = (LinearLayout) findViewById(R.id.ll_web_view);
        but_back = (Button) findViewById(R.id.but_back);
        but_forward = (Button) findViewById(R.id.but_forward);
        but_clear = (Button) findViewById(R.id.but_clear);
        but_reload = (Button) findViewById(R.id.but_reload);
        but_get = (Button) findViewById(R.id.but_get);

        but_send.setOnClickListener(this);
        but_cancel.setOnClickListener(this);
        but_msg_type.setOnCheckedChangeListener(this);
        but_back.setOnClickListener(this);
        but_forward.setOnClickListener(this);
        but_clear.setOnClickListener(this);
        but_reload.setOnClickListener(this);
        but_get.setOnClickListener(this);
        tv_display.setOnLongClickListener(this);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net);
        Log.d(TAG, banner + "onCreate");

        // Avoid showing keyboard on activity start.
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        initViews();

        setDefaultBehaviour(but_msg_type.isChecked());

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

        if (id == R.id.action_post_activ) {
            startActivity(new Intent(this, PostActiv.class));
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        Button b = (Button) v;
        switch (b.getId()) {
            case R.id.but_cancel:
                Log.d(TAG, banner + "onClick: cancel button");
                but_cancel_f();
                break;
            case R.id.but_send:
                Log.d(TAG, banner + "onClick: send button");
                but_send_f();
                break;
            case R.id.but_back:
                Log.d(TAG, banner + "onClick: back button");
                webView.goBack();
                break;
            case R.id.but_forward:
                Log.d(TAG, banner + "onClick: forward button");
                webView.goForward();
                break;
            case R.id.but_clear:
                Log.d(TAG, banner + "onClick: clear button");
                webView.clearHistory();
                break;
            case R.id.but_reload:
                Log.d(TAG, banner + "onClick: reload button");
                webView.reload();
                break;
            case R.id.but_get:
                Log.d(TAG, banner + "onClick: get button");
                onGet(null);
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {

        TextView textView = (TextView) v;
        if (textView.getId() == R.id.tv_display) {
            Log.d(TAG, banner + "onLongClick: calling showHideWView");
            showHideBrowser();
            return true;
        }

        return false;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(TAG, banner + "onCheckedChanged");
        setDefaultBehaviour(isChecked);
    }
}
