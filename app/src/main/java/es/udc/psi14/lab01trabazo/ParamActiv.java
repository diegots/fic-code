package es.udc.psi14.lab01trabazo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;



public class ParamActiv extends ActionBarActivity {

    private Intent sender;
    private Integer counter;
    private EditText editText;
    private final String TAG = "LCA_TAG";
    private final Integer REQUEST_CODE = 10;

    public void onActivityResult(int requestCode,int resultCode,Intent data) {

        Log.d(TAG, "ParamActivity: onActivityResult()");

        if (requestCode==REQUEST_CODE && resultCode==RESULT_OK) {
            // Use the same key to recover the returned value
            counter = data.getIntExtra("count", counter);
        }
        //Toast.makeText(this, "onActivityResult, count: " + counter, Toast.LENGTH_SHORT).show();
        editText.setText("" + counter);
    }

    public void onToParamActiv(View view) {
        Log.d(TAG, "ParamActivity: onToParamActiv()");

        if (sender != null && sender.hasExtra("count")) {
            counter = Integer.valueOf(editText.getText().toString());
            Intent intent = new Intent(this, ParamActiv.class);
            intent.putExtra("count", counter.intValue());
            startActivityForResult(intent, REQUEST_CODE);
        }
        finish();
    }

    public void onOK(View view) {
        Log.d(TAG, "ParamActivity: onOK()");

        if (sender != null && sender.hasExtra("count")) {
            // Enviamos los datos recogidos (actividad explícita)
            counter = Integer.valueOf(editText.getText().toString());
            Intent data = new Intent();
            data.putExtra("count", counter.intValue());
            setResult(RESULT_OK, data);
            finish();
        } else {
            // Actividad lanzada como implícita por el sistema
            finish();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "ParamActivity: onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_param);

        editText = (EditText) findViewById(R.id.textEditText);

        sender = getIntent();

        if (sender != null && sender.hasExtra("count")) {
            // Actividad lanzada desde FirstActiv como explícita
            counter = sender.getIntExtra("count", 0);
            editText.setText("" + counter);
        } else {
            // Actividad lanzada como implícita por el sistema
            String data = sender.getDataString();
            editText.setText(data);
            Toast.makeText(this, data, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_param, menu);
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
