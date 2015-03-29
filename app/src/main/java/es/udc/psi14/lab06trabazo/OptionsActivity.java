package es.udc.psi14.lab06trabazo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
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
        Log.d(DataTestActivity.TAG, "[OptionsActivity] onCreate()");

        listView = (ListView) findViewById(android.R.id.list);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.options);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

//    This activity is already a preferences panel. onCreateOptionsMenu() and onOptionsItemSelected()
//    are not needed.
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_options, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    private void showStorageSummary() {
        String storageTypeCodeStr = sharedPreferences.getString(DataTestActivity.KEY_STORAGE_TYPE, "");
        ListPreference storageTypePref = (ListPreference) findPreference(DataTestActivity.KEY_STORAGE_TYPE);
        if ("".equals(storageTypeCodeStr)) {
            Log.d(DataTestActivity.TAG, "[OptionsActivity] onResume() storageTypeCode empty");
            return;
        } else
            Log.d(DataTestActivity.TAG, "[OptionsActivity] onResume() storageTypeCode: " + storageTypeCodeStr);
        if (storageTypePref == null)
            Log.d(DataTestActivity.TAG, "[OptionsActivity] onResume() storageTypePref null");

        int storageTypeCode = Integer.parseInt(storageTypeCodeStr);
        CharSequence[] cs = storageTypePref.getEntries();
        storageTypePref.setSummary(cs[storageTypeCode]);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(DataTestActivity.TAG, "[OptionsActivity] onResume()");

        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        if(sharedPreferences == null) {
            Log.d(DataTestActivity.TAG, "[OptionsActivity] onResume() sharedPreferences is null");
            return;
        }

        // Set summary for new file name preference pane.
        String file_name = getString(R.string.file_name);
        String file_name_value = sharedPreferences.getString(DataTestActivity.KEY_FN, file_name);
        Preference file_name_pref = findPreference(DataTestActivity.KEY_FN);
        file_name_pref.setSummary(file_name_value);

        // Set storage type preference
        showStorageSummary();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(DataTestActivity.TAG, "[OptionsActivity] onSharedPreferenceChanged() KEY: " + key);

        if (key.equals(DataTestActivity.KEY_FN)) {
            Preference preference = findPreference(key);
            preference.setSummary(sharedPreferences.getString(key, "")); // show actual value
        } else if(key.equals(DataTestActivity.KEY_STORAGE_TYPE)) {
            // Set storage type preference
            showStorageSummary();

        } else
            Log.d(DataTestActivity.TAG, "[OptionsActivity] onSharedPreferenceChanged() key not known: " + key);
    }
}
