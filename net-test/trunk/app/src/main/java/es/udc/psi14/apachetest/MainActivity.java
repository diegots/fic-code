package es.udc.psi14.apachetest;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/* Two independent steps:
 * 1. Create RSS entry with available data
 * 2. Send that RSS entry to the configured URL
 */

public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    final static String TAG = "SimpleHTTP";
    Button testButton01;
    String data_field_1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        data_field_1 = "generated field data 1";
        testButton01 = (Button) findViewById(R.id.testButton01);
        testButton01.setOnClickListener(this);

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
            //createFeed(); // TODO This function ends with an exception
        }
    }

    private class POST_Job extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            Log.d(TAG, "POST_Job");
            //String requestBody = createFeed();
            String requestBody = "This is the body\n";
            byte[] postData = requestBody.getBytes(Charset.forName("UTF-8"));
            int postDataLength = postData.length;
            String request = "http://192.168.0.2/scripts/read_input.py";
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
                Log.d(TAG, "POST_Job: responseCode: " + responseCode);
                wr.flush();
                wr.close();
            } catch (MalformedURLException murle) {
                Log.d(TAG, "POST_Job: URL exception" + murle.getMessage());
            } catch (IOException ioe) {
                Log.d(TAG, "POST_Job: IO exception" + ioe.getMessage());
            }

            return "";
        }
    }

//    GET request example. Not needed, POST is a better way
//    private class GET_Job extends AsyncTask<String, Void, String> {
//        @Override
//        protected String doInBackground(String... params) {
//            String url = "http://192.168.1.132/";
//            final String USER_AGENT = "Mozilla/5.0";
//
//            try {
//                URL obj = new URL(url);
//                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
//                con.setRequestMethod("GET");
//                con.setRequestProperty("User-Agent", USER_AGENT);
//
//                int responseCode = con.getResponseCode();
//
//                Log.d(TAG, "\nSending 'GET' request to URL : " + url);
//                Log.d(TAG, "Response Code : " + responseCode);
//
//                BufferedReader in = new BufferedReader(
//                        new InputStreamReader(con.getInputStream()));
//                String inputLine;
//                StringBuffer response = new StringBuffer();
//                while ((inputLine = in.readLine()) != null) {
//                    response.append(inputLine);
//                }
//                in.close();
//                Log.d(TAG, response.toString());
//
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return "";
//        }
//    }

    // TODO Test this function.
    private String createFeed() {
        Log.d(TAG, "createFeed start");
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
            Log.d(TAG, "createFeed: " + pe.getMessage());
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
            Log.d(TAG, "createFeed: " + pe.getMessage());
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
            Log.d(TAG, "createFeed: " + fe.getMessage());
        }
        return outFeed;
    }

}
