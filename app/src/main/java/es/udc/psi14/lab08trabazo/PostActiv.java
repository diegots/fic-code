package es.udc.psi14.lab08trabazo;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class PostActiv extends ActionBarActivity implements View.OnClickListener {

    EditText et_user_name;
    EditText et_passwd;
    EditText et_ip;
    TextView tv_result;
    Button post_activ_but_send;

    void initViews () {
        et_user_name = (EditText) findViewById(R.id.et_user_name);
        et_passwd = (EditText) findViewById(R.id.et_passwd);
        et_ip = (EditText) findViewById(R.id.et_ip);
        tv_result = (TextView) findViewById(R.id.tv_result);
        post_activ_but_send = (Button) findViewById(R.id.post_activ_but_send);

        post_activ_but_send.setOnClickListener(this);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        initViews();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_post, menu);
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

        if (b.getId() == R.id.post_activ_but_send) {
            // DO something
        }
    }
}
