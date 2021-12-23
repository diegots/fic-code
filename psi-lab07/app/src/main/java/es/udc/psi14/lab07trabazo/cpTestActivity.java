package es.udc.psi14.lab07trabazo;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.UserDictionary;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;


public class cpTestActivity extends ActionBarActivity implements View.OnClickListener {

    Button cp_but_insert;
    Button cp_but_delete;
    Button cp_but_edit;
    Button cp_but_search;
    EditText cp_et_word;
    ListView cp_lv;


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Log.d(dbTestActivity.TAG, "[cpTestActivity] onContextItemSelected");
        AdapterView.AdapterContextMenuInfo info;

        if (item.getItemId() == R.id.cp_context_menu_delete) {
            info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            int delItems = delete_word((int) info.id);
            Toast.makeText(this, "Deleted: " + delItems, Toast.LENGTH_SHORT).show();

            // Updating Listview
            Cursor mCursor = buscar_palabra(cp_et_word.getText().toString());
            update_list(mCursor);
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.cp_context_menu, menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cp_test);

        cp_but_search = (Button) findViewById(R.id.cp_but_search);
        cp_but_search.setOnClickListener(this);
        cp_et_word = (EditText) findViewById(R.id.cp_et_word);
        cp_lv = (ListView) findViewById(R.id.cp_lv);
        cp_lv.setOnCreateContextMenuListener(this);

        cp_but_insert = (Button) findViewById(R.id.cp_but_insert);
        cp_but_insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!"".equals(cp_et_word.getText().toString())) {
                    ContentValues mNewValues = new ContentValues();
                    mNewValues.put(UserDictionary.Words.WORD, cp_et_word.getText().toString());
                    mNewValues.put(UserDictionary.Words.LOCALE, "es");
                    mNewValues.put(UserDictionary.Words.APP_ID, "es.udc.psi14.lab07trabazo");
                    mNewValues.put(UserDictionary.Words.FREQUENCY, "1");

                    Uri mNewUri = getContentResolver().insert(
                            UserDictionary.Words.CONTENT_URI, mNewValues);
                    Toast.makeText(cpTestActivity.this, "New URI: " + mNewUri.toString(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        cp_but_delete = (Button) findViewById(R.id.cp_but_delete);
        cp_but_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!"".equals(cp_et_word.getText().toString())) {
                    String select = UserDictionary.Words.WORD + " LIKE ? ";
                    String[ ] selectArgs = {"%" + cp_et_word.getText().toString() + "%"};

                    int numUpdateValues = getContentResolver().delete(
                            UserDictionary.Words.CONTENT_URI, select, selectArgs);
                    Toast.makeText(cpTestActivity.this, "Updated values: " + numUpdateValues,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        cp_but_edit = (Button) findViewById(R.id.cp_but_edit);
        cp_but_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!"".equals(cp_et_word.getText().toString())) {
                    String select = UserDictionary.Words.WORD + " LIKE ? ";
                    String[ ] selectArgs = {"%" + cp_et_word.getText().toString() + "%"};
                    ContentValues mNewValues = new ContentValues();
                    mNewValues.putNull(UserDictionary.Words.LOCALE);
                    int numUpdateContacts = getContentResolver().update(
                            UserDictionary.Words.CONTENT_URI,
                            mNewValues, select, selectArgs);
                    Toast.makeText(cpTestActivity.this, "Updated contacts: " + numUpdateContacts,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cp_test, menu);
        return true;
    }

    @Override
    public void onClick(View v) {

        if (!"".equals(cp_et_word.getText().toString())) {
            Cursor mCursor = buscar_palabra(cp_et_word.getText().toString());
            update_list(mCursor);
        }
    }

    private void update_list(Cursor cursorNotas) {
        Log.d(dbTestActivity.TAG, "[cpTestActivity] update_list");
        Log.d(dbTestActivity.TAG, "[cpTestActivity] found results: " + cursorNotas.getCount());
        Log.d(dbTestActivity.TAG, "[cpTestActivity] number of columns: " + cursorNotas.getColumnCount());
        String [] stringsS = cursorNotas.getColumnNames();
        Log.d(dbTestActivity.TAG, "[cpTestActivity] Column[0]: " + stringsS[0]);
        Log.d(dbTestActivity.TAG, "[cpTestActivity] Column[1]: " + stringsS[1]);
        while (cursorNotas.moveToNext()) {
            Log.d(dbTestActivity.TAG, "[cpTestActivity] Column[1]: " + cursorNotas.getString(1));
        }

        String[] strings = {
            UserDictionary.Words.WORD,   // Contract class constant for the word column name
            UserDictionary.Words.LOCALE  // Contract class constant for the locale column name
        };

        int[] ints = new int[] {R.id.dictWord, R.id.locale};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                R.layout.wordlistrow,
                cursorNotas,
                strings,
                ints,
                0);

        cp_lv.setAdapter(adapter);
    }

    public Cursor buscar_palabra(String fileName) {

        Log.d(dbTestActivity.TAG, "[cpTestActivity] Looking up: " + fileName);

        // A "projection" defines the columns that will be returned for each row
        String[] mProjection = {
            UserDictionary.Words._ID,
            UserDictionary.Words.WORD,   // Contract class constant for the word column name
            UserDictionary.Words.LOCALE  // Contract class constant for the locale column name
        };

        // Constructs a selection clause that matches the word that the user entered.
        String mSelectionClause = UserDictionary.Words.WORD + " like ?";

        // Initializes an array to contain selection arguments
        String[] mSelectionArgs = {""};
        // Moves the user's input string to the selection arguments.
        mSelectionArgs[0] = "%" + fileName + "%";

        //String mSortOrder = UserDictionary.Words.WORD;
        String mSortOrder = UserDictionary.Words.WORD;

        Cursor mCursor = getContentResolver().query(
                UserDictionary.Words.CONTENT_URI,   // The content URI of the words table
                mProjection,                        // The columns to return for each row
                mSelectionClause,                    // Selection criteria
                mSelectionArgs,                     // Selection criteria
                mSortOrder);                        // The sort order for the returned rows

        if (null == mCursor)
            Toast.makeText(this, "Wrong query", Toast.LENGTH_SHORT).show();
        else if (mCursor.getCount() < 1)
            Toast.makeText(this, "No results", Toast.LENGTH_SHORT).show();

        return mCursor;

    }

    public int delete_word(int word_id) {
        // Defines selection criteria for the rows you want to delete
        String mSelectionClause = UserDictionary.Words._ID + " = ?";
        String[] mSelectionArgs = {String.valueOf(word_id)};

        // Defines a variable to contain the number of rows deleted
        int mRowsDeleted = 0;

        // Deletes the words that match the selection criteria
        mRowsDeleted = getContentResolver().delete(
                UserDictionary.Words.CONTENT_URI,   // the user dictionary content URI
                mSelectionClause,                    // the column to select on
                mSelectionArgs                      // the value to compare to
        );

        return mRowsDeleted;
    }

}
