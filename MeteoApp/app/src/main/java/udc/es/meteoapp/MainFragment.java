package udc.es.meteoapp;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import android.os.Handler;

import udc.es.meteoapp.model.Model;
import udc.es.meteoapp.model.PlacesContent;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnItemSelectedListener}
 * interface.
 */
public class MainFragment extends ListFragment {

    String TAG = "MeteoApp";
    Handler handler;
    Model model;

    private OnItemSelectedListener itemSelectedListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MainFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "MainFragment: onStart");

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "MainFragment: onCreate");
        handler = new Handler();
        model = new Model(handler);

        // TODO: Change Adapter to display your locality_name
        setListAdapter(new ArrayAdapter<PlacesContent.PlaceItem>(getActivity(),
            android.R.layout.simple_list_item_1, android.R.id.text1, PlacesContent.ITEMS));


    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(TAG, "MainFragment: onAttach");

        try {
            itemSelectedListener = (OnItemSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "MainFragment: onDetach");

        // onDetach list item listener is disabled.
        itemSelectedListener = null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Log.d(TAG, "MainFragment: onListItemClick");

        if (null != itemSelectedListener) {

            String locality_id = PlacesContent.ITEMS.get(position).locality_id;
            String locality_name = PlacesContent.ITEMS.get(position).locality_name;
            Log.d(TAG, "MainFragment: onListItemClick - locality_id: " + locality_id);
            model.findLocality(locality_name);

            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            itemSelectedListener.onItemSelected(locality_id);
        }
    }

    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // TODO
    }

    public interface OnItemSelectedListener {
        public void onItemSelected(String id);
    }

}

class getLocalityHandler extends Handler{
    String TAG = "MeteoApp";

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        Log.d(TAG, "handleMessage: localitiesBudle");

        Bundle localitiesBudle = msg.getData();
        Log.d(TAG, "handleMessage: localitiesBudle" + localitiesBudle.get("id"));
        String [] data = {};

    }
}
