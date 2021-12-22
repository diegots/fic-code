package es.udc.psi14.lab00_lcatrabazo;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class FirstActiv extends ActionBarActivity {

    private Integer count = 0;
    private final String TAG = "LCA_TAG";
    private Button killButton;
    private Button countButton;
    private TextView textView;
    private String textViewString;

    public void onKill(View view) {
        Log.d(TAG, "FirstActivity: onKill");
        finish();
    }

    public void onCount(View view) {
        Log.d(TAG, "FirstActivity: onCount");
        count++;

        textView.setText(textViewString + count);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_first);

        Log.d(TAG, "FirstActivity: onCreate");

        killButton = (Button) findViewById(R.id.but_kill);
        countButton = (Button) findViewById(R.id.but_count);
        textView = (TextView) findViewById(R.id.vistaTexto);

        // getInt devuelve 0 si la clave no ha sido encontrada
        if (savedInstanceState != null)
            count = savedInstanceState.getInt("count");

        // Se guarda la etiqueta original del textView antes de modificarla
        textViewString = textView.getText().toString();
        textView.setText(textViewString + count);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_first, menu);
        return true;
    }

    @Override
    protected void onRestart() {

        super.onRestart();
        Log.d(TAG, "FirstActivity: onRestart");

        // Alternativa a Log, mensajes en forma de pequeños popups:
        // http://developer.android.com/guide/topics/ui/notifiers/toasts.html
        // Toast.makeText(this, "onPause()", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "FirstActivity: onResume");
    }

    // In general onSaveInstanceState(Bundle) is used to save per-instance state in the activity
    // and this method is used to store global persistent data (in content providers, files, etc.)
    // http://developer.android.com/reference/android/app/Activity.html#onPause%28%29
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("count", count);
}

    // No existen datos persistentes para guardar
    @Override
    protected void onPause() {

        super.onPause();
        Log.d(TAG, "FirstActivity: onPause");
}

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "FirstActivity: onStop");
        //Toast.makeText(this, "onPause()", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "FirstActivity: onDestroy");
        //Toast.makeText(this, "onPause()", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "FirstActivity: onStart");
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
    public boolean onTouchEvent(MotionEvent event) {
        // El eje de coodenadas se corresponde con la esquina superior izquierda.

        int action = event.getAction();
        switch (action) {

            // A pressed gesture has started, the motion contains the initial starting location.
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "Accion DOWN (started): "
                        + event.getAxisValue(MotionEvent.AXIS_X) + ", "
                        + event.getAxisValue(MotionEvent.AXIS_Y) );
                return true;
            // A change has happened during a press gesture (between ACTION_DOWN and ACTION_UP).
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "Accion MOVE (change): "
                        + event.getAxisValue(MotionEvent.AXIS_X) + ", "
                        + event.getAxisValue(MotionEvent.AXIS_Y) );
                return true;
            // A pressed gesture has finished.
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "Accion UP (finished): "
                        + event.getAxisValue(MotionEvent.AXIS_X) + ", "
                        + event.getAxisValue(MotionEvent.AXIS_Y) );
                return true;
            default:
                return super.onTouchEvent(event); // Accion por defecto para las acciones no recogidas
        }
    }


}
