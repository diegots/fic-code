package udc.es.meteoapp;


import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import udc.es.meteoapp.modelo.PlacesContent;

public class PlacesFragment extends Fragment {

    String TAG = "MeteoApp";
    public static final String ARG_LOCALITY_ID = "locality_id";
    private PlacesContent.PlaceItem mItem;

    public PlacesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "PlacesFragment: onCreate");

        if(getArguments().containsKey(ARG_LOCALITY_ID)) {
            mItem = PlacesContent.ITEM_MAP.get(getArguments().getString(ARG_LOCALITY_ID));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "PlacesFragment: onCreateView");

        View rootView = inflater.inflate(R.layout.fragment_places_detail, container, false);

        if (mItem != null)
            ((TextView) rootView.findViewById(R.id.place_detail)).setText(mItem.content);

        return rootView;
    }
}

//package udc.es.meteoapp;
//
//
//import android.os.Bundle;
//import android.app.Fragment;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//
///**
// * A simple {@link Fragment} subclass.
// */
//public class PlacesFragment extends Fragment { //implements FragmentManager.OnBackStackChangedListener {
//
//    String TAG = "MeteoApp";
//    String locality;
//
//    public PlacesFragment() {
//        // Required empty public constructor
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        Log.d(TAG, "PlacesFragment: onDestroyView()");
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        Log.d(TAG, "PlacesFragment: onCreateView()");
//        locality = getArguments().getString("locality");
//
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_places_detail, container, false);
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//
//        TextView tv_locality_name = (TextView) getActivity().findViewById(R.locality_id.tv_locality_name);
//        tv_locality_name.setText(locality);
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        Log.d(TAG, "PlacesFragment: onDestroy()");
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        Log.d(TAG, "PlacesFragment: onStop()");
//    }
//}