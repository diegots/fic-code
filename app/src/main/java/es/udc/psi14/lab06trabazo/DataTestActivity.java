package es.udc.psi14.lab06trabazo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;

/* 1.e) Data is kept as an xml file in PREF_TAG.xml. It's name is defined in PREF_TAG String.
 * Full path is: /data/data/es.udc.psi14.lab06trabazo/shared_prefs/PREF_TAG.xml
 * Of course, it is editable from Android Device Emulator and also by any user with root level
 * access to the system. On the other side, other apps can't access it.
 *
 * 3.f) Cannot be edited externally if has MODE_PRIVATE set. Once the app is uninstalled, all
 * internal data is wiped.
 *
 * 4.d) File is in: /storage/sdcard/Android/data/es.udc.psi14.lab06trabazo/files/hello_file.txt
 * Can be edited by anyone and is kept on uninstallation.
 */

public class DataTestActivity extends ActionBarActivity {

    final static String TAG = "LCA_TAG";
    final static String PREF_TAG = "PREF_TAG";
    final static String KEY_HELLO_MSG = "KEY_HELLO_MSG";
    final static String FILENAME = "hello_file.txt";

    TextView tv_text;
    EditText et_text;
    Spinner spinner;
    SharedPreferences sharedPreferences;

    Button but_hello;
    Button but_write_int;
    Button but_read_int;
    Button but_write_ext;
    Button but_read_ext;
    Button but_write;
    Button but_read;

    private void init() {
        tv_text = (TextView) findViewById(R.id.tv_text);
        et_text = (EditText) findViewById(R.id.et_text);
        but_hello = (Button) findViewById(R.id.but_hello);
        but_write_int = (Button) findViewById(R.id.but_write_int);
        spinner = (Spinner) findViewById(R.id.spinner);
        but_read_int = (Button) findViewById(R.id.but_read_int);
        but_write_ext = (Button) findViewById(R.id.but_write_ext);
        but_read_ext = (Button) findViewById(R.id.but_read_ext);
        but_read = (Button) findViewById(R.id.but_read);
        but_write = (Button) findViewById(R.id.but_write);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_test);
        init();

        final SharedPreferences preferences = getSharedPreferences(PREF_TAG, Context.MODE_MULTI_PROCESS);
        String myData = preferences.getString(KEY_HELLO_MSG, "No value");

        PreferenceManager.setDefaultValues(this, R.xml.options, false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        tv_text.setText(myData);

        but_hello.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!"".equals(et_text.getText().toString())) {
                    Log.d(TAG, "[DataTestActivity] but_hello onClick(), et_text not empty -> saving prefs");
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(KEY_HELLO_MSG, et_text.getText().toString());
                    editor.commit();
                    et_text.setText("");
                } else
                    Log.d(TAG, "[DataTestActivity] but_write_int onClick(), et_text empty -> doing nothing");
            }
        });

        but_write_int.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String et_text_contents = et_text.getText().toString();
                if (!"".equals(et_text_contents)) {
                    Log.d(TAG, "[DataTestActivity] but_write_int onClick(), et_text not empty -> saving file");

                    FileOutputStream fos;
                    try {
                        fos = openFileOutput(FILENAME, Context.MODE_APPEND);
                        fos.write((et_text_contents + "\n").getBytes());
                        fos.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    et_text.setText("");
                } else
                    Log.d(TAG, "[DataTestActivity] but_write_int onClick(), et_text empty -> doing nothing");
            }
        });

        but_read_int.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Log.d(TAG, "[DataTestActivity] but_read_int onClick()");
                    ArrayList<String> arrayList = new ArrayList<String>();
                    String line;
                    FileInputStream fis = openFileInput(FILENAME);
                    InputStreamReader in = new InputStreamReader(fis);
                    BufferedReader br = new BufferedReader(in);

                    while ((line = br.readLine()) != null) { // read text line by line
                        arrayList.add(line + "\n"); // append in a variable
                    }

                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                            (DataTestActivity.this, android.R.layout.simple_list_item_1, arrayList);
                    spinner.setAdapter(arrayAdapter);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        but_write_ext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "[DataTestActivity] but_write_ext onClick()");
                String et_text_contents = et_text.getText().toString();
                if (!"".equals(et_text_contents)) {
                    String stateExternalStorage = Environment.getExternalStorageState();
                    if (Environment.MEDIA_MOUNTED.equals(stateExternalStorage)) {
                        // availability
                        try {
                            // Create path pointing to the root of the external storage
                            // (where application have permissions to save files)
                            File file = new File(getExternalFilesDir(null), FILENAME);
                            OutputStream os = new FileOutputStream(file, true);
                            os.write(et_text.getText().toString().getBytes());
                            // write data from textFile
                            os.close();
                            // close file
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                et_text.setText("");
            }
        });

        but_read_ext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "[DataTestActivity] but_read_ext onClick()");
                try {
                    ArrayList<String> arrayList = new ArrayList<String>();
                    String line;
                    File file = new File(getExternalFilesDir(null), FILENAME);
                    FileInputStream fis = new FileInputStream(file);
                    InputStreamReader in = new InputStreamReader(fis);
                    BufferedReader br = new BufferedReader(in);

                    while ((line = br.readLine()) != null) { // read text line by line
                        arrayList.add(line + "\n"); // append in a variable
                    }

                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                            (DataTestActivity.this, android.R.layout.simple_list_item_1, arrayList);
                    spinner.setAdapter(arrayAdapter);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        but_read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        but_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "[DataTestActivity] onCreateOptionsMenu() - creating menu");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_data_test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "[DataTestActivity] onOptionsItemSelected()");

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Log.d(TAG, "[DataTestActivity] settings menu detected -> launch OptionsActivity");
            startActivity(new Intent(this, OptionsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
