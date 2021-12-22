package es.udc.psi14.lab01trabazo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class SecondActiv extends ActionBarActivity {

    private final String TAG = "LCA_TAG";
    
    public void onSecondButton(View view) {
        Log.d(TAG, "SecondActivity: onToSecondActiv");
        Intent intent = new Intent(this, SecondActiv.class);
        startActivity(intent);
    }

    public void onKillTwo(View view) {
        finish();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "SecondActivity: onRestart");
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "SecondActivity: onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "SecondActivity: onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "SecondActivity: onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "SecondActivity: onDestroy");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "SecondActivity: onStart");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_second, menu);
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
}
