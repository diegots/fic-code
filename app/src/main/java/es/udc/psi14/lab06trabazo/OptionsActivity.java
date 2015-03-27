package es.udc.psi14.lab06trabazo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;


public class OptionsActivity extends PreferenceActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    ListView listView;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        listView = (ListView) findViewById(android.R.id.list);
        Log.d(DataTestActivity.TAG, "[OptionsActivity] onCreate()");

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.options);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_options, menu);
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
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(DataTestActivity.TAG, "[OptionsActivity] onResume()");

        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        if(sharedPreferences == null)
            Log.d(DataTestActivity.TAG, "[OptionsActivity] onResume() sharedPreferences is null");
        else if ("".equals(sharedPreferences.getString(DataTestActivity.KEY_FN, getString(R.string.file_name))))
            Log.d(DataTestActivity.TAG, "[OptionsActivity] onResume() string is null");
        else {
            String file_name = getString(R.string.file_name);
            String file_name_value = sharedPreferences.getString(DataTestActivity.KEY_FN, file_name);
            Preference preference = findPreference(DataTestActivity.KEY_FN);

            if (preference == null)
                Log.d(DataTestActivity.TAG, "[OptionsActivity] onResume() preference is null");
            else
                preference.setSummary(file_name_value);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(DataTestActivity.TAG, "[OptionsActivity] onSharedPreferenceChanged()");

        if (key.equals(DataTestActivity.KEY_FN)) {
            Preference preference = findPreference(key);
            preference.setSummary(sharedPreferences.getString(key, "")); // show actual value
        } else
            Log.d(DataTestActivity.TAG, "[OptionsActivity] onSharedPreferenceChanged() key is not DataTestActivity.KEY_FN: " + key);
    }
}
