package es.udc.psi14.apachetest;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndContentImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndEntryImpl;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndFeedImpl;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedOutput;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/* Two independent steps:
 * 1. Create RSS entry with available data
 * 2. Send that RSS entry to the configured URL
 */

public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    final static String TAG = "SimpleHTTP";
    final static String BANNER = "[MainActivity] ";

    final static String DEV_SERVER_IP = "192.168.1.134";
    final static String POST_FEED_URL_START = "http://";
    final static String POST_FEED_URL_END = "/scripts/read_input.py";

    Button bt_send_data;
    TextView tv_device_id;
    EditText et_server_ip;

    String deviceId;
    String serverIp;

    void initViews () {
        bt_send_data = (Button) findViewById(R.id.bt_send_data);
        tv_device_id = (TextView) findViewById(R.id.tv_device_id);
        et_server_ip = (EditText) findViewById(R.id.et_server_ip);

        bt_send_data.setOnClickListener(this);
    }

    private String getDeviceId () {
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();

    }

    private String createFeed(String deviceId) {

        Log.d(TAG, BANNER + "createFeed");

        //java.text.DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        java.text.DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
        String feedType = "rss_2.0";

        String feedTitle = deviceId; // feedTitle is IMEI device
        String feedLink = "http://rome.dev.java.net";
        String feedDescr = "This feed has been created using ROME (Java syndication utilities";

        String entryTitle = "ROME v1.0";
        String entryLink = "http://wiki.java.net/bin/view/Javawsxml/Rome01";
        String entryDate = dateFormat.format(new Date());

        // Entry description data
        String descrType = "text/plain";
        String descrValue = "Initial release of ROME";

        // Creating feed
        SyndFeed feed = new SyndFeedImpl();
        feed.setFeedType(feedType);
        feed.setTitle(feedTitle);
        feed.setLink(feedLink);
        feed.setDescription(feedDescr);

        // Entry datatypes
        List entries = new ArrayList();
        SyndEntry entry = new SyndEntryImpl();
        SyndContent description;

        // Creating entry
        entry.setTitle(entryTitle);
        entry.setLink(entryLink);
        try {
            entry.setPublishedDate(dateFormat.parse(entryDate));
        } catch (ParseException pe) {
            Log.d(TAG, BANNER + "createFeed: " + pe.getMessage());
        }
        description = new SyndContentImpl();
        description.setType(descrType);
        description.setValue(descrValue);
        entry.setDescription(description);

        // Adding entries to list, then to feed
        entries.add(entry);
        feed.setEntries(entries);

        // Obtaining feed XML as String
        SyndFeedOutput output = new SyndFeedOutput();
        String outFeed = "";
        try {
            outFeed = output.outputString(feed);
        } catch (FeedException fe) {
            Log.d(TAG, BANNER + "createFeed: " + fe.getMessage());
        }

        return outFeed;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        Log.d(TAG, BANNER + "onCreate: views set up");

        // Avoid showing keyboard on activity start.
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        et_server_ip.setText(DEV_SERVER_IP);

        // Get and show device ID
        deviceId = getDeviceId();
        tv_device_id.setText(getString(R.string.tv_device_id) + deviceId);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

        if (b.getId() == bt_send_data.getId()) {
            new POST_Job().execute(createFeed(deviceId), et_server_ip.getText().toString());
            Log.d(TAG, BANNER + "onClick: executed POST_Job task");
        }
    }

    private class POST_Job extends AsyncTask<String, Void, String> {
        final static String BANNER = "[POST_Job] ";

        @Override
        protected String doInBackground(String... params) {
            Log.d(TAG, BANNER + "POST_Job");

            String requestBody = params[0];
            byte[] postData = requestBody.getBytes(Charset.forName("UTF-8"));
            int postDataLength = postData.length;

            String urlStr = POST_FEED_URL_START + params[1] + POST_FEED_URL_END;

            URL url;
            try {
                url = new URL(urlStr);
                HttpURLConnection cox= (HttpURLConnection) url.openConnection();
                cox.setDoOutput(true);
                cox.setDoInput(true);
                cox.setInstanceFollowRedirects(false);
                cox.setRequestMethod("POST");
                cox.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                cox.setRequestProperty("charset", "utf-8");
                cox.setRequestProperty("Content-Length", Integer.toString(postDataLength));
                cox.setUseCaches(false);
                Log.d(TAG, BANNER + "doInBackground: Sending data to : " + urlStr);
                DataOutputStream wr = new DataOutputStream(cox.getOutputStream());
                wr.write(postData);
                int responseCode = cox.getResponseCode();
                Log.d(TAG, BANNER + "doInBackground: responseCode: " + responseCode);
                wr.flush();
                wr.close();
            } catch (MalformedURLException murle) {
                Log.d(TAG, BANNER + "doInBackground " + murle.getMessage());
            } catch (IOException ioe) {
                Log.d(TAG, BANNER + "doInBackground " + ioe.getMessage());
            }

            return "";
        }
    }

}
