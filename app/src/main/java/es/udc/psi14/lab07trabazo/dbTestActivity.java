package es.udc.psi14.lab07trabazo;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;


public class dbTestActivity extends ActionBarActivity {

    final static String TAG = "LCA_TAG";
    ListView lv;
    TextView tv;
    NotasDataBaseHelper notasdb;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info;

        switch (item.getItemId()) {

            // Delete selected item
            case R.id.context_menu_borrar:
                info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                notasdb.deleteNota((int) info.id);
                actualizar_lista(notasdb.getNotas()); // actualiza la listView
                Log.d(TAG, "[dbTestActivity] onContextItemSelected: deleted item  with id: "
                    + info.id);
                return true;

            // Edit selected item
            case R.id.context_menu_editar:
                info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                Intent intent = new Intent(this, NuevaNota.class);
                intent.putExtra(NotasDataBaseHelper.COL_ID, (int) info.id);
                startActivity(intent);
                Log.d(TAG, "[dbTestActivity] onContextItemSelected: edit item with id: "
                    + info.id);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "[dbTestActivity] onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db_test);

        tv = (TextView) findViewById(R.id.tv);
        lv = (ListView) findViewById(R.id.lv);
        lv.setOnCreateContextMenuListener(this);

        notasdb = new NotasDataBaseHelper(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_db_test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            Log.d(TAG, "[dbTestActivity] onOptionsItemSelected: action add");
            startActivity(new Intent(this, NuevaNota.class));
            return true;
        } else if (id == R.id.action_update) {
            Log.d(TAG, "[dbTestActivity] onOptionsItemSelected: action update");
            actualizar_lista(notasdb.getNotas());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void actualizar_lista(Cursor cursorNotas) {
        Log.d(TAG, "[dbTestActivity] actualizar_lista");
        tv.setText(getString(R.string.msg) + " " + String.valueOf(cursorNotas.getCount()));

        String[] strings = new String[] {
            NotasDataBaseHelper.COL_NOMBRE,
            NotasDataBaseHelper.COL_APELLIDO,
            NotasDataBaseHelper.COL_MATERIA,
            NotasDataBaseHelper.COL_MENCION,
            NotasDataBaseHelper.COL_NOTA
        };

        int[] ints = new int[] {
            R.id.nombre,
            R.id.apellido,
            R.id.materia,
            R.id.mención,
            R.id.nota
        };

        SimpleCursorAdapter adapter2 = new SimpleCursorAdapter(
            this,
            R.layout.list_row,
            cursorNotas,
            strings,
            ints,
            0);

        lv.setAdapter(adapter2);
    }
}
