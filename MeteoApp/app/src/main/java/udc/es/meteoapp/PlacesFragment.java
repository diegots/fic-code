package udc.es.meteoapp;


import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import udc.es.meteoapp.model.PlacesContent;

public class PlacesFragment extends Fragment {

    String TAG = "MeteoApp";
    public static final String ARG_LOCALITY_ID = "locality_id";
    private PlacesContent.PlaceItem mItem;

    public PlacesFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "PlacesFragment: onCreate");

        if (getArguments().containsKey(ARG_LOCALITY_ID))
            mItem = PlacesContent.ITEM_MAP.get(getArguments().getString(ARG_LOCALITY_ID));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "PlacesFragment: onCreateView");

        View rootView = inflater.inflate(R.layout.fragment_places_detail, container, false);

        if (mItem != null)
            ((TextView) rootView.findViewById(R.id.place_detail)).setText(mItem.locality_name);

        return rootView;
    }
}