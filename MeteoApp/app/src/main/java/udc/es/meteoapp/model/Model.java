package udc.es.meteoapp.model;


import android.graphics.AvoidXfermode;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;

import udc.es.meteoapp.PlacesFragment;

public class Model {

    String TAG = "MeteoApp";

    FindLocality findLocality;
    Handler handler;

    public Model(Handler handler) {
        this.handler = handler;

    }

    public void findLocality(Bundle item) {

        String name = item.getString("name");
        String id = item.getString("id");
        Log.d(TAG, "Model: findLocality: name: " + name + " id: " + id);
        findLocality =
            new FindLocality("findLocality", handler, id, name, PlacesContent.apiKey);
        findLocality.start();
    }

    public Bundle getPlaceItem(String id){

        PlacesContent.PlaceItem pp = PlacesContent.ITEM_MAP.get(id);

        Bundle b = new Bundle();
        b.putString("id", pp.locality_id);
        b.putString("name", pp.locality_name);
        b.putString("province", pp.locality_province);
        b.putString("municipality", pp.locality_municipality);
        b.putString("type", pp.locality_municipality);
        // TODO locality type is missing
        return  b;
    }

    public Bundle parseLocalityData(Bundle received_data) {

        ArrayList<Bundle> c = received_data.getParcelableArrayList("received_data");
        String locality_name = received_data.getString("locality_id");
        Log.d(TAG, "Model: parseLocalityData: " + locality_name + " received items: " + c.size());
        String id;
        String name;
        String province;
        String municipality;
        String type;

        Bundle b = new Bundle();

        for (Bundle item : c) {
            id = item.get("id").toString();
            name = item.get("name").toString();
            province = item.get("province").toString();
            municipality = item.get("municipality").toString();
            type = item.get("type").toString();

            Log.d(TAG, "Model: parseLocalityData: "
                    + "id: " + id
                    + " municipality:" + municipality
                    + " name:" + name
                    + " province:" + province
                    + " type:" + type);

            PlacesContent.PlaceItem mItem = PlacesContent.ITEM_MAP.get(locality_name);
            if (mItem == null)
                Log.d(TAG, "Model: parseLocalityData: NULL mItem");
            else
                Log.d(TAG, "Model: parseLocalityData: " + mItem.locality_name);

            boolean found_locality_id = checkLocality(name, municipality, province, mItem);

            if (found_locality_id) {
                Log.d(TAG, "Model: parseLocalityData: FOUND");
                b.putString("id", id);
                b.putString("name", name);
                b.putString("province", province);
                b.putString("municipality", municipality);
                b.putString("type", type);
                return b;
            }
        }
        throw new IllegalArgumentException("Given locality has no API ID");
    }

    private Boolean checkLocality(String name, String municipality, String province,
                                  PlacesContent.PlaceItem place) {


        boolean n = place.locality_name.equalsIgnoreCase(name);

        Log.d(TAG, "GetLocalityHandler: checkLocality: STORED: " + place.locality_name + " RECEIVED: "
                + name);
        Log.d(TAG, "GetLocalityHandler: checkLocality: names are " + n);



        boolean m = place.locality_municipality.equalsIgnoreCase(municipality);
        Log.d(TAG, "GetLocalityHandler: checkLocality: STORED: "
                + place.locality_municipality + " RECEIVED: " + municipality);
        Log.d(TAG, "GetLocalityHandler: checkLocality: municipalities are " + m);

        boolean p = place.locality_province.equalsIgnoreCase(province);
        Log.d(TAG, "GetLocalityHandler: checkLocality: STORED: " + place.locality_province
                + " RECEIVED " + province);
        Log.d(TAG, "GetLocalityHandler: checkLocality: provinces are " + p);

        Log.d(TAG, "GetLocalityHandler: checkLocality: RESULT " + ((m && n) && p));
        return ((m && n) && p);
    }


}
