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

    public PlacesFragment() {
    }

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
            Log.d(TAG, "handleMessage: localitiesBudle");

            Bundle localitiesBudle = msg.getData();


            String name = localitiesBudle.get("name").toString();
            String province = localitiesBudle.get("province").toString();
            String municipality = localitiesBudle.get("municipality").toString();

            Log.d(TAG, "handleMessage: localitiesBudle " + localitiesBudle.get("id"));
            Log.d(TAG, "handleMessage: localitiesBudle " + localitiesBudle.get("municipality"));
            Log.d(TAG, "handleMessage: localitiesBudle " + localitiesBudle.get("name"));
            Log.d(TAG, "handleMessage: localitiesBudle " + localitiesBudle.get("province"));
            Log.d(TAG, "handleMessage: localitiesBudle " + localitiesBudle.get("type"));


            Log.d(TAG, "handleMessage: antes de iguales ");

            boolean iguales = checkLocality(name, municipality, province, mItem);
            Log.d(TAG, "handleMessage: despues de iguales " + iguales);

            if (iguales) {
                mItem.locality_municipality = municipality;
                ((TextView) rootView.findViewById(R.id.place_municipality)).setText(mItem.locality_municipality);
            }

        }

        public Boolean checkLocality(String name, String municipality, String province, PlacesContent.PlaceItem place) {

            boolean n = place.locality_name.equalsIgnoreCase(name);
            Log.d(TAG, "handleMessage: place name"+ place.locality_name +" name "+ name);
            Log.d(TAG, "handleMessage: name iguales " + n);

            boolean m = place.locality_municipality.equalsIgnoreCase(municipality);
            Log.d(TAG, "handleMessage: place municipality"+ place.locality_municipality +" municipality "+ municipality);
            Log.d(TAG, "handleMessage: municipality iguales " + m);

            boolean p = place.locality_province.equalsIgnoreCase(province);
            Log.d(TAG, "handleMessage: place province"+ place.locality_province +" province "+ province);
            Log.d(TAG, "handleMessage: province iguales " + p);

            return (m & n & p);
        }
    }
}