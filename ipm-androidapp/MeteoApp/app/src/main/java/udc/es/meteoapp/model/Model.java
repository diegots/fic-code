package udc.es.meteoapp.model;


import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;

public class Model {

    String TAG = "MeteoApp";

    Handler handler;
    Handler retrieveForecastHandler;

    public Model(Handler handler, Handler retrieveForecastHandler) {
        this.handler = handler;
        this.retrieveForecastHandler = retrieveForecastHandler;
    }

    public void retrieveForecast(String locality_api_id, String locality_id) {
        Log.d(TAG, "Model: retrieveForecast");
        RetrieveForecast retrieveForecast = new RetrieveForecast(
            "retrieveForecast",
            retrieveForecastHandler,
            locality_api_id,
            PlacesContent.apiKey,
            locality_id);
        retrieveForecast.start();
    }

    public void findLocality(Bundle item) {

        String name = item.getString("name");
        String id = item.getString("id");
        Log.d(TAG, "Model: findLocality: name: " + name + " id: " + id);

        FindLocality findLocality =
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
        b.putString("type", pp.locality_type);

        b.putString("precipitation_amount_units", pp.details.precipitation_amount_units);
        b.putString("precipitation_amount_value", pp.details.precipitation_amount_value);

        b.putString("sky_state_url", pp.details.sky_state_url);
        b.putString("sky_state_value", pp.details.sky_state_value);

        b.putString("temperature_units", pp.details.temperature_units);
        b.putString("temperature_value", pp.details.temperature_value);

        b.putString("wind_module_units", pp.details.wind_module_units);
        b.putString("wind_direction_units", pp.details.wind_direction_units);
        b.putString("wind_direction_value", pp.details.wind_direction_value);
        b.putString("wind_module_value", pp.details.wind_module_value);
        b.putString("wind_direction_iconURL", pp.details.wind_direction_iconURL);

        b.putString("snow_level_units", pp.details.snow_level_units);
        b.putString("snow_level_value", pp.details.snow_level_value);

        b.putString("relative_humidity_units", pp.details.relative_humidity_units);
        b.putString("relative_humidity_value", pp.details.relative_humidity_value);

        b.putString("cloud_area_fraction_units", pp.details.cloud_area_fraction_units);
        b.putString("cloud_area_fraction_value", pp.details.cloud_area_fraction_value);

        b.putString("air_pressure_at_sea_level_units", pp.details.air_pressure_at_sea_level_units);
        b.putString("air_pressure_at_sea_level_value", pp.details.air_pressure_at_sea_level_value);

        b.putString("significative_wave_height_units", pp.details.significative_wave_height_units);
        b.putString("significative_wave_height_value", pp.details.significative_wave_height_value);

        b.putString("relative_peak_period_units", pp.details.relative_peak_period_units);
        b.putString("relative_peak_period_value", pp.details.relative_peak_period_value);

        b.putString("mean_wave_direction_units", pp.details.mean_wave_direction_units);
        b.putString("mean_wave_direction_value", pp.details.mean_wave_direction_value);
        b.putString("mean_wave_direction_url", pp.details.mean_wave_direction_url);

        b.putString("sea_water_temperature_units", pp.details.sea_water_temperature_units);
        b.putString("sea_water_temperature_value", pp.details.sea_water_temperature_value);

        b.putString("sea_water_temperature_units", pp.details.sea_water_salinity_units);
        b.putString("sea_water_temperature_value", pp.details.sea_water_salinity_value);

        b.putString("sky_state_string", pp.details.sky_state_string);
        b.putString("wave_direction_string", pp.details.wave_direction_string);
        b.putString("wind_direction_string", pp.details.wind_direction_string);

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
                Log.d(TAG, "Model: parseLocalityData: FOUND " + name);

                // Save data internally
                PlacesContent.PlaceItem pp = PlacesContent.ITEM_MAP.get(locality_name);
                pp.locality_municipality = municipality;
                pp.locality_province = province;
                pp.locality_type = type;
                pp.locality_api_id = id;

                b.putString("municipality", municipality);
                b.putString("province", province);
                b.putString("type", type);
                b.putString("id", id);
                b.putString("name", name);

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
