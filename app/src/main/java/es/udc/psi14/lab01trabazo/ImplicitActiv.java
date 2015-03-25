package es.udc.psi14.lab01trabazo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;


public class ImplicitActiv extends ActionBarActivity {

    private static final Integer REQUEST_IMAGE_CAPTURE = 1;

    public void onSms(View view) {

            String smsBody = "Cuerpo del mensaje sms";
            String smsNumber = "667843018";

            EditText eTextBody = (EditText) findViewById(R.id.eTextBody);
            EditText eTextNumber= (EditText) findViewById(R.id.eTextNumber);

            smsBody = eTextBody.getText().toString();
            smsNumber = eTextNumber.getText().toString();

            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("sms:" + smsNumber));
            intent.putExtra("sms_body", smsBody);

            if (intent.resolveActivity(getPackageManager()) != null)
                startActivity(intent);
        }

    public void onCall(View view) {

            String phoneNumber = "667843018";

            EditText eTextNumber = (EditText) findViewById(R.id.eTextPhone);
            phoneNumber = eTextNumber.getText().toString();

            Intent intent = new Intent(Intent.ACTION_CALL,
                    Uri.parse("tel:" + phoneNumber));
            startActivity(intent);
        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==REQUEST_IMAGE_CAPTURE && resultCode==RESULT_OK) {
            Bitmap thumbnail = data.getParcelableExtra("data");
        }
    }

    public void onCamera(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (intent.resolveActivity(getPackageManager()) != null)
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }

    public void onMap(View view) {

            String mapLat = "47.6";
            String mapLong = "-122.3";

            EditText eTextLat = (EditText) findViewById(R.id.eTextLat);
            EditText eTextLong = (EditText) findViewById(R.id.eTextLong);

            mapLat = eTextLat.getText().toString();
            mapLong = eTextLong.getText().toString();

            Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("geo:" + mapLat + "," + mapLong));
            if (intent.resolveActivity(getPackageManager()) != null)
                startActivity(intent);
        }

    public void onWeb(View view) {

        String url = "http://www.google.com";

        EditText eTextUrl = (EditText) findViewById(R.id.eTextUrl);
        url = eTextUrl.getText().toString();

        Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(url));
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_implicit);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_implicit, menu);
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
