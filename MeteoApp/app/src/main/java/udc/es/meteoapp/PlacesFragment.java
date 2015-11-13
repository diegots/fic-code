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
import android.widget.Toast;

import java.util.List;

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

            Log.d(TAG, "PlacesFragment: onCreate: locality_id: " + getArguments().getString(ARG_LOCALITY_ID));

            Bundle b = model.getPlaceItem(getArguments().getString(ARG_LOCALITY_ID));
            model.findLocality(b);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "PlacesFragment: onCreateView");

        rootView = inflater.inflate(R.layout.fragment_places_detail, container, false);

        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.place_name)).setText(mItem.locality_name);

        }

        return rootView;
    }

    class GetLocalityHandler extends Handler {
        String TAG = "MeteoApp";

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "GetLocalityHandler: handleMessage");

            Bundle bundle = new Bundle();

            try {
                bundle = model.parseLocalityData(msg.getData());

                Log.d(TAG, "GetLocalityHandler: handleMessage: valid data: "
                        + bundle.getString("id")
                        + bundle.getString("municipality"));
            } catch (IllegalArgumentException e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            ((TextView) rootView.findViewById(R.id.place_name)).
                    setText(bundle.getString("name"));
            ((TextView) rootView.findViewById(R.id.place_municipality)).
                    setText(bundle.getString("municipality"));
            ((TextView) rootView.findViewById(R.id.place_province)).
                    setText(bundle.getString("province"));
            ((TextView) rootView.findViewById(R.id.place_type)).
                    setText(bundle.getString("type"));

        }
    }
}