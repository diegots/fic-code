package es.udc.psi14.lab08trabazo;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.concurrent.ExecutionException;

public class PostActiv extends ActionBarActivity implements View.OnClickListener {

    private final static String banner = "[PostActiv] ";

    final static String HARDCODED_HTTP_USERNAME = "Diego";
    final static String HARDCODED_HTTP_PASSWORD = "Trabazo";
    final static String HARDCODED_HTTP_IP = "192.168.0.3";

    EditText et_user_name;
    EditText et_passwd;
    EditText et_ip;
    TextView tv_result;
    Button post_activ_but_send;
    Button post_activ_but_json;
    Button post_activ_but_xml;

    String username;
    String passwd;
    String ip;

    void setDefaultBehaviour () {
        Log.d(NetActiv.TAG, banner + "setDefaultBehaviour");

        username = et_user_name.getText().toString();
        passwd = et_passwd.getText().toString();
        ip = et_ip.getText().toString();

        // HTTP POST parameters
        if ("".equals(username))
            username = HARDCODED_HTTP_USERNAME;
        if ("".equals(passwd))
            passwd = HARDCODED_HTTP_PASSWORD;
        if ("".equals(ip))
            ip = HARDCODED_HTTP_IP;
    }

    void but_xml_f () {
        String xmlUrl = "http://datos.gijon.es/doc/transporte/busgijontr.xml";
        XmlTask xmlTask = new XmlTask();

        try {
            Log.d(NetActiv.TAG, banner + "but_xml_f");
            String result = xmlTask.execute(xmlUrl).get().toString();
            tv_result.setText(result);
        } catch (ExecutionException ee) {
            Log.d(NetActiv.TAG, banner + ee.getMessage());
        } catch (InterruptedException ie) {
            Log.d(NetActiv.TAG, banner + ie.getMessage());
        }
    }

    void but_json_f () {
        String jsonUrl = "http://datos.gijon.es/doc/transporte/busgijontr.json";
        JsonTask jsonTask = new JsonTask();

        try {
            Log.d(NetActiv.TAG, banner + "but_json_f");
            String result = jsonTask.execute(jsonUrl).get().toString();
            tv_result.setText(result);
        } catch (ExecutionException ee) {
            Log.d(NetActiv.TAG, banner + ee.getMessage());
        } catch (InterruptedException ie) {
            Log.d(NetActiv.TAG, banner + ie.getMessage());
        }
    }

    // Handle send POST request
    void but_send_f () {

        et_user_name.setText(username);
        et_passwd.setText(passwd);
        et_ip.setText(ip);

        PostTask postTask = new PostTask();
        try {
            Log.d(NetActiv.TAG, banner + "sending username: " + username + ", passwd: "
                + passwd + ", ip: " + ip);
            String result = postTask.execute(username, passwd, ip).get().toString();
            tv_result.setText(result);
        } catch (ExecutionException ee) {
            Log.d(NetActiv.TAG, banner + ee.getMessage());
        } catch (InterruptedException ie) {
            Log.d(NetActiv.TAG, banner + ie.getMessage());
        }
    }

    void initViews () {
        et_user_name = (EditText) findViewById(R.id.et_user_name);
        et_passwd = (EditText) findViewById(R.id.et_passwd);
        et_ip = (EditText) findViewById(R.id.et_ip);
        tv_result = (TextView) findViewById(R.id.tv_result);
        post_activ_but_send = (Button) findViewById(R.id.post_activ_but_send);
        post_activ_but_json = (Button) findViewById(R.id.post_activ_but_json);
        post_activ_but_xml = (Button) findViewById(R.id.post_activ_but_xml);

        post_activ_but_send.setOnClickListener(this);
        post_activ_but_json.setOnClickListener(this);
        post_activ_but_xml.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        initViews();
        setDefaultBehaviour();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_post, menu);
        return true;
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

            case R.id.post_activ_but_send:
                Log.d(NetActiv.TAG, banner + "onClick: send button");
                but_send_f();
                break;
            case R.id.post_activ_but_json:
                Log.d(NetActiv.TAG, banner + "onClick: json button");
                but_json_f ();
                break;
            case R.id.post_activ_but_xml:
                Log.d(NetActiv.TAG, banner + "onClick: xml button");
                but_xml_f ();
                break;
        }
    }
}
