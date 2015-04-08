package es.udc.psi14.lab07trabazo;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueva_nota);

        et_nombre = (EditText) findViewById(R.id.et_nombre);
        et_apellido = (EditText) findViewById(R.id.et_apellido);
        et_materia = (EditText) findViewById(R.id.et_materia);
        et_mencion = (EditText) findViewById(R.id.et_mencion);
        et_nota = (EditText) findViewById(R.id.et_nota);
        bt_add = (Button) findViewById(R.id.bt_add);
        bt_add.setOnClickListener(this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_nueva_nota, menu);
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

        if (b.getId() == R.id.bt_add) {
            NotasDataBaseHelper ndbh = new NotasDataBaseHelper(this);
            String nombre = et_nombre.getText().toString();
            String apellido = et_apellido.getText().toString();
            String materia = et_materia.getText().toString();
            boolean mencion = Boolean.parseBoolean(et_mencion.getText().toString());
            float nota = Float.parseFloat(et_nota.getText().toString());
            Notas notas = new Notas();
            notas.setNombre(nombre);
            notas.setApellido(apellido);
            notas.setMateria(materia);
            notas.setMencion(mencion);
            notas.setNota(nota);
            long code = ndbh.insertNota(notas);
            if (code!=-1) Toast.makeText(this, "Nuevo dato", Toast.LENGTH_LONG).show();
            finish();
        }

    }
}
