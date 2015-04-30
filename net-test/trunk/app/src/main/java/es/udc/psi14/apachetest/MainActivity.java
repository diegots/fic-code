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
import android.widget.Button;
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
import java.util.List;

/* Two independent steps:
 * 1. Create RSS entry with available data
 * 2. Send that RSS entry to the configured URL
 */

public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    final static String TAG = "SimpleHTTP";
    private final static String banner = "[MainActivity] ";

    Button testButton01;
    TextView tv_device_id;

    String deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Init Views
        testButton01 = (Button) findViewById(R.id.testButton01);
        tv_device_id = (TextView) findViewById(R.id.tv_device_id);

        // Set View listeners
        testButton01.setOnClickListener(this);

        // Get and show device ID
        deviceId = getDeviceId();
        tv_device_id.setText(deviceId);

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

        if (b.getId() == testButton01.getId()) {
            new POST_Job().execute();
        }
    }

    private class POST_Job extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            Log.d(TAG, banner + "POST_Job");
            //String requestBody = createFeed();
            //String requestBody = "This is the body\n";
            String requestBody = createFeed();
            byte[] postData = requestBody.getBytes(Charset.forName("UTF-8"));
            int postDataLength = postData.length;
            String request = "http://192.168.0.6/scripts/read_input.py";
            URL url;
            try {
                url = new URL(request);
                HttpURLConnection cox= (HttpURLConnection) url.openConnection();
                cox.setDoOutput(true);
                cox.setDoInput(true);
                cox.setInstanceFollowRedirects(false);
                cox.setRequestMethod("POST");
                cox.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
                cox.setRequestProperty( "charset", "utf-8");
                cox.setRequestProperty( "Content-Length", Integer.toString(postDataLength));
                cox.setUseCaches(false);
                DataOutputStream wr = new DataOutputStream(cox.getOutputStream());
                wr.write(postData);
                int responseCode = cox.getResponseCode();
                Log.d(TAG, banner + "POST_Job: responseCode: " + responseCode);
                wr.flush();
                wr.close();
            } catch (MalformedURLException murle) {
                Log.d(TAG, banner + "POST_Job: URL exception" + murle.getMessage());
            } catch (IOException ioe) {
                Log.d(TAG, banner + "POST_Job: IO exception" + ioe.getMessage());
            }

            return "";
        }
    }

    private String createFeed() {
        Log.d(TAG, banner + "createFeed start");
        java.text.DateFormat DATE_PARSER = new SimpleDateFormat("yyyy-MM-dd");
        String feedType = "rss_2.0";

        SyndFeed feed = new SyndFeedImpl();
        feed.setFeedType(feedType);

        feed.setTitle("Sample Feed (created with ROME)");
        feed.setLink("http://rome.dev.java.net");
        feed.setDescription("This feed has been created using ROME (Java syndication utilities");

        List entries = new ArrayList();
        SyndEntry entry;
        SyndContent description;

        entry = new SyndEntryImpl();
        entry.setTitle("ROME v1.0");
        entry.setLink("http://wiki.java.net/bin/view/Javawsxml/Rome01");
        try {
            entry.setPublishedDate(DATE_PARSER.parse("2004-06-08"));
        } catch (ParseException pe) {
            Log.d(TAG, banner + "createFeed: " + pe.getMessage());
        }
        description = new SyndContentImpl();
        description.setType("text/plain");
        description.setValue("Initial release of ROME");
        entry.setDescription(description);
        entries.add(entry);

        entry = new SyndEntryImpl();
        entry.setTitle("ROME v2.0");
        entry.setLink("http://wiki.java.net/bin/view/Javawsxml/Rome02");
        try {
            entry.setPublishedDate(DATE_PARSER.parse("2004-06-16"));
        } catch (ParseException pe) {
            Log.d(TAG, banner + "createFeed: " + pe.getMessage());
        }
        description = new SyndContentImpl();
        description.setType("text/plain");
        description.setValue("Bug fixes, minor API changes and some new features");
        entry.setDescription(description);
        entries.add(entry);

        feed.setEntries(entries);
        SyndFeedOutput output = new SyndFeedOutput();
        String outFeed = "";
        try {
            outFeed = output.outputString(feed);
        } catch (FeedException fe) {
            Log.d(TAG, banner + "createFeed: " + fe.getMessage());
        }
        return outFeed;
    }

    private String getDeviceId () {
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();

    }
}
