package udc.es.meteoapp;


import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import udc.es.meteoapp.model.Model;
import udc.es.meteoapp.model.PlacesContent;

public class PlacesFragment extends Fragment {

    String TAG = "MeteoApp";
    public static final String ARG_LOCALITY_ID = "locality_name";
    private PlacesContent.PlaceItem mItem;

    View rootView;

    Handler handler;
    Model model;

    public PlacesFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "PlacesFragment: onCreate");

        handler = new GetLocalityHandler();
        model = new Model(handler);

        if (getArguments().containsKey(ARG_LOCALITY_ID)) {
            mItem = PlacesContent.ITEM_MAP.get(getArguments().getString(ARG_LOCALITY_ID));
            model.findLocality(mItem.locality_name);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "PlacesFragment: onCreateView");

        rootView = inflater.inflate(R.layout.fragment_places_detail, container, false);

        if (mItem != null)
            ((TextView) rootView.findViewById(R.id.place_name)).setText(mItem.locality_name);

        return rootView;
    }

    class GetLocalityHandler extends Handler {
        String TAG = "MeteoApp";

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "handleMessage: localitiesBudle");

            Bundle localitiesBudle = msg.getData();
            Log.d(TAG, "handleMessage: localitiesBudle " + localitiesBudle.get("id"));
            Log.d(TAG, "handleMessage: localitiesBudle " + localitiesBudle.get("municipality"));
            Log.d(TAG, "handleMessage: localitiesBudle " + localitiesBudle.get("name"));
            Log.d(TAG, "handleMessage: localitiesBudle " + localitiesBudle.get("province"));
            Log.d(TAG, "handleMessage: localitiesBudle " + localitiesBudle.get("type"));

            mItem.locality_municipality = (String) localitiesBudle.get("municipality");
            ((TextView) rootView.findViewById(R.id.place_municipality)).setText(mItem.locality_municipality);

        }
    }
}