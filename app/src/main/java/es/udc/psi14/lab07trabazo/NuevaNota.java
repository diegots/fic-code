package es.udc.psi14.lab07trabazo;

import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class NuevaNota extends ActionBarActivity implements View.OnClickListener{

    EditText et_nombre;
    EditText et_apellido;
    EditText et_materia;
    EditText et_mencion;
    EditText et_nota;
    Button bt_add;
    Button bt_edit;
    long dataId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueva_nota);

        // Define activity elements
        et_nombre = (EditText) findViewById(R.id.et_nombre);
        et_apellido = (EditText) findViewById(R.id.et_apellido);
        et_materia = (EditText) findViewById(R.id.et_materia);
        et_mencion = (EditText) findViewById(R.id.et_mencion);
        et_nota = (EditText) findViewById(R.id.et_nota);
        bt_add = (Button) findViewById(R.id.bt_add);
        bt_add.setOnClickListener(this);
        bt_edit = (Button) findViewById(R.id.bt_edit);
        bt_edit.setOnClickListener(this);

        if (getIntent().getExtras() != null) { // check if not null
            Log.d(dbTestActivity.TAG, "[NuevaNota] onCreate: getIntent returns != null");
            Bundle extra = getIntent().getExtras();
            int id = extra.getInt(NotasDataBaseHelper.COL_ID, 0);
            Log.d(dbTestActivity.TAG, "[NuevaNota] onCreate: editing db entry with id: " + id);
            NotasDataBaseHelper notasdb = new NotasDataBaseHelper(this);
            Cursor cursorNotas = notasdb.getNotas(NotasDataBaseHelper.COL_ID, String.valueOf(id));

            Log.d(dbTestActivity.TAG, "[NuevaNota] onCreate: cursor column's number: "
                + cursorNotas.getColumnCount());
            Log.d(dbTestActivity.TAG, "[NuevaNota] onCreate: cursor data elements:  "
                + cursorNotas.getCount());

            if (cursorNotas.getCount() != 1) {
                Log.d(dbTestActivity.TAG, "[NuevaNota] onCreate: wrong number of elements recovered: "
                    + cursorNotas.getCount());
            } else {
                cursorNotas.moveToFirst();

                // Store data id, needed if updating
                int colId = cursorNotas.getColumnIndex(NotasDataBaseHelper.COL_ID);
                dataId = cursorNotas.getLong(colId);
                Log.d(dbTestActivity.TAG, "[NuevaNota] onCreate: storing data id: " + dataId);

                // Fill EditTexts with recovered data
                int colNombreId = cursorNotas.getColumnIndex(NotasDataBaseHelper.COL_NOMBRE);
                et_nombre.setText(cursorNotas.getString(colNombreId));

                colNombreId = cursorNotas.getColumnIndex(NotasDataBaseHelper.COL_APELLIDO);
                et_apellido.setText(cursorNotas.getString(colNombreId));

                colNombreId = cursorNotas.getColumnIndex(NotasDataBaseHelper.COL_MATERIA);
                et_materia.setText(cursorNotas.getString(colNombreId));

                colNombreId = cursorNotas.getColumnIndex(NotasDataBaseHelper.COL_MENCION);
                et_mencion.setText(cursorNotas.getString(colNombreId));

                colNombreId = cursorNotas.getColumnIndex(NotasDataBaseHelper.COL_NOTA);
                et_nota.setText(cursorNotas.getString(colNombreId));

                bt_edit.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // There is no menu on this activity
        //getMenuInflater().inflate(R.menu.menu_nueva_nota, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        Button b = (Button) v;

        if (b.getId() == R.id.bt_add)
            updateOrCreate(R.id.bt_add);

        else if (b.getId() == R.id.bt_edit)
            updateOrCreate(R.id.bt_edit);

        finish();
    }

    private void updateOrCreate(int actionId) {
        NotasDataBaseHelper notasdb = new NotasDataBaseHelper(this);
        Notas notas = new Notas();

        // TODO check that all fields are not empty
        String nombre = et_nombre.getText().toString();
        String apellido = et_apellido.getText().toString();
        String materia = et_materia.getText().toString();
        String mencion = et_mencion.getText().toString();
        float nota = Float.parseFloat(et_nota.getText().toString());

        notas.setNombre(nombre);
        notas.setApellido(apellido);
        notas.setMateria(materia);
        notas.setMencion(mencion);
        notas.setNota(nota);

        if (actionId == R.id.bt_add) {
            long code = notasdb.insertNota(notas);
            if (code != -1)
                Toast.makeText(this, "Nuevo dato, Id: " + code, Toast.LENGTH_LONG).show();
        } else if (actionId == R.id.bt_edit) {
            // Sets id on updating
            notas.setId(dataId);

            notasdb.updateNota(notas);
            Toast.makeText(this, "Dato " + dataId  + " cambiado", Toast.LENGTH_LONG).show();
        }
    }
}
