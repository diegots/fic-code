package es.udc.psi14.lab07trabazo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;


public class dbTestActivity extends ActionBarActivity implements DialogInterface.OnClickListener {

    final static String PREF_TAG = "PREF_TAG";
    final static String TAG = "LCA_TAG";
    final static String KEY_CHOSEN_COLUMN_ID = "KEY_CHOSEN_COLUMN_ID";
    final static String KEY_CHOSEN_COLUMN = "KEY_CHOSEN_COLUMN";

    ListView lv;
    TextView tv;
    NotasDataBaseHelper notasdb;
    CharSequence [] allColumnNames;
    int chosenColumnId;
    String chosenColumn;
    SharedPreferences sharedPreferences;

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
                update_list(notasdb.getNotas()); // actualiza la listView
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
        allColumnNames = notasdb.getAllColumnNames();

        // Recover preference values (AlertDialog values)
        sharedPreferences = getSharedPreferences(PREF_TAG, Context.MODE_MULTI_PROCESS);
        chosenColumnId = sharedPreferences.getInt(KEY_CHOSEN_COLUMN_ID, 0);
        chosenColumn = sharedPreferences.getString(KEY_CHOSEN_COLUMN, "_id");

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

        if (id == R.id.action_add) {
            Log.d(TAG, "[dbTestActivity] onOptionsItemSelected: action add");
            startActivity(new Intent(this, NuevaNota.class));
            return true;
        } else if (id == R.id.action_update) {
            Log.d(TAG, "[dbTestActivity] onOptionsItemSelected: action update");
            update_list(notasdb.getNotas());
            return true;
        } else if (id == R.id.action_sort) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder
                .setTitle("Pick column and order")
                .setSingleChoiceItems(allColumnNames, chosenColumnId, this);

            builder.setPositiveButton("Ascending", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Log.d(TAG, "[dbTestActivity] onOptionsItemSelected: order ASC by " + chosenColumn);
                    Cursor cursor =
                        notasdb.getNotasOrdered(chosenColumn + " ASC");
                    update_list(cursor);
                }
            });
            builder.setNegativeButton("Descending", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Log.d(TAG, "[dbTestActivity] onOptionsItemSelected: order ASC by " + chosenColumn);
                    Cursor cursor =
                            notasdb.getNotasOrdered(chosenColumn + " DESC");
                    update_list(cursor);
                }
            });
            builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AlertDialog alertDialog = (AlertDialog) dialog;
                    alertDialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();

        } else if (id == R.id.action_filter) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Filter criteria:");
            builder.setPositiveButton("Sort", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AlertDialog alertDialog = (AlertDialog) dialog;
                    EditText editText = (EditText) alertDialog.getWindow().findViewById(R.id.alert_edit_text);

                    Cursor cursor =
                            notasdb.getNotas(chosenColumn, editText.getText().toString());
                    update_list(cursor);

                }
            });
            builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AlertDialog alertDialog = (AlertDialog) dialog;
                    alertDialog.dismiss();
                }
            });
            //builder.setSingleChoiceItems(allColumnNames, chosenColumnId, this);


            AlertDialog alert = builder.create();
            ViewGroup viewGroup = new LinearLayout(this);
            View view = alert.getLayoutInflater().inflate(R.layout.alert_dialog, viewGroup);

            ListView listView = (ListView) view.findViewById(R.id.alert_list_view);
            ArrayAdapter<CharSequence> arrayAdapter = new ArrayAdapter<>
                (this,android.R.layout.select_dialog_singlechoice, allColumnNames);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    final String item = (String) parent.getItemAtPosition(position);
                    Log.d(TAG, "onItemClick: selected_item: " + item);

                    dialogOnClick(position, item);
                }
            });

            listView.setAdapter(arrayAdapter);
            alert.setView(view);
            listView.setSelection(chosenColumnId);
            alert.show();
        } else if (id == R.id.cpTest) {
            startActivity(new Intent(this, cpTestActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    private void update_list(Cursor cursorNotas) {
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

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
            this,
            R.layout.list_row,
            cursorNotas,
            strings,
            ints,
            0);

        lv.setAdapter(adapter);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        Log.d(TAG, "onClick");
        AlertDialog alertDialog = (AlertDialog) dialog;
        alertDialog.getButton(which);
        Log.d(TAG, "onClick: But: " + which);

        dialogOnClick(which, allColumnNames[which].toString());
    }

    private void dialogOnClick(int cColumnId, String cColumn) {

        chosenColumnId = cColumnId;
        chosenColumn = cColumn;

        // Saving preferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_CHOSEN_COLUMN, chosenColumn);
        editor.putInt(KEY_CHOSEN_COLUMN_ID, chosenColumnId);
        editor.commit();
    }
}
