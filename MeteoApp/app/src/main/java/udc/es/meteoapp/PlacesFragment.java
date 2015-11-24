package udc.es.meteoapp;


import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ProgressDialog;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import udc.es.meteoapp.model.Model;
import udc.es.meteoapp.model.PlacesContent;

import static android.graphics.BitmapFactory.*;
import static udc.es.meteoapp.R.id.image;
import static udc.es.meteoapp.R.id.img_place_temperature;

public class PlacesFragment extends Fragment {

    String TAG = "MeteoApp";
    public static final String ARG_LOCALITY_ID = "locality_name";
    private PlacesContent.PlaceItem mItem;
    ProgressDialog pDialog;

    View rootView;

    Handler handler;
    Handler retrieveForecastHandler;
    Model model;

    public PlacesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "PlacesFragment: onCreate");

        handler = new GetLocalityHandler();
        retrieveForecastHandler = new RetrieveForecastHandler();
        model = new Model(handler, retrieveForecastHandler);

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
            Log.d(TAG, "GetLocalityHandlerHMIA: handleMessage");

            Bundle bundle = new Bundle();

            try {
                bundle = model.parseLocalityData(msg.getData());

                Log.d(TAG, "GetLocalityHandler: handleMessage: valid data: "
                        + bundle.getString("id") + ", "
                        + bundle.getString("municipality"));
            } catch (IllegalArgumentException e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            ((TextView) rootView.findViewById(R.id.place_name)).
                    setText(bundle.getString("name"));
            ((TextView) rootView.findViewById(R.id.place_municipality)).
                    setText(bundle.getString("municipality"));
            ((TextView) rootView.findViewById(R.id.place_province)).
                    setText(bundle.getString("province").toUpperCase());
            ((TextView) rootView.findViewById(R.id.place_type)).
                    setText(bundle.getString("type"));

            // Now ID is known, so forecast can be retrieved
            model.retrieveForecast(bundle.getString("id"));
        }
    }

    class RetrieveForecastHandler extends Handler {
        @Override

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "RetrieveForecastHandler: handleMessage");

            Bundle bundle = msg.getData();
            Log.d(TAG, "RetrieveForecastHandler: handleMessage: sky_state_icon "
                    + bundle.getString("sky_state_url"));

            fillInLocality(bundle);
            fillISeaPlaces(bundle);

        }


        public void fillInLocality(Bundle bundle) {

            /* sky_state*/
            String sky_state = bundle.getString("sky_state_value");
            String url = bundle.getString("sky_state_url");
            loadSky_state(sky_state, url);

            /* precipitation_amount*/
            ((TextView) rootView.findViewById(R.id.place_precipitation_amount)).
                    setText(bundle.getString("precipitation_amount_value")
                            + " " + bundle.getString("precipitation_amount_units"));

            /* temperature*/
            ((TextView) rootView.findViewById(R.id.place_temperature)).
                    setText(bundle.getString("temperature_value"));
            /* Unit temperature*/
            ((TextView) rootView.findViewById(R.id.tempUnit)).
                    setText(bundle.getString("temperature_units"));
            /* wind*/
            ((TextView) rootView.findViewById(R.id.place_wind)).
                    setText(bundle.getString("wind_module_value") +
                            " " + bundle.getString("wind_module_units"));
            /*wind image */
            loadWindImage(bundle.getString("wind_direction_iconURL"));
            /*wind direction */
            ((TextView) rootView.findViewById(R.id.place_wind_direction)).
                    setText(bundle.getString("wind_direction_value") +
                            " " + bundle.getString("wind_direction_units"));
            /* humidity*/
            ((TextView) rootView.findViewById(R.id.place_relative_humidity)).
                    setText(bundle.getString("relative_humidity_value") +
                            " " + bundle.getString("relative_humidity_units"));
            /* clound area*/
            ((TextView) rootView.findViewById(R.id.place_cloud_area_fraction)).
                    setText(bundle.getString("cloud_area_fraction_value") +
                            " " + bundle.getString("cloud_area_fraction_units"));

            /*snow*/
            ((TextView) rootView.findViewById(R.id.place_snow_level)).
                    setText(bundle.getString("snow_level_value") + " " +
                            " " + bundle.getString("snow_level_units"));

        }

        public void fillISeaPlaces(Bundle bundle) {

            /*significative_wave_height*/
            String significative_wave_height_value = bundle.getString("significative_wave_height_value");
            Log.d(TAG, "fillISeaPlaces: handleMessage: significative_wave_height_value "
                    + significative_wave_height_value);

            if (significative_wave_height_value != null) {
                TextView text = (TextView) rootView.findViewById(R.id.sea_significative_wave_height);
                ImageView image = (ImageView) rootView.findViewById(R.id.sea_significative_wave_height_icon);
                TextView tag = (TextView) rootView.findViewById(R.id.sea_significative_wave_height_tag);

                image.setVisibility(View.VISIBLE);
                tag.setVisibility(View.VISIBLE);
                text.setVisibility(View.VISIBLE);
                text.setText(significative_wave_height_value + " " +
                        " " + bundle.getString("significative_wave_height_units"));
            }


            /*mean_wave_direction*/
            String mean_wave_direction_value = bundle.getString("mean_wave_direction_value");
            Log.d(TAG, "fillISeaPlaces: handleMessage: mean_wave_direction_value "
                    + mean_wave_direction_value);

            String url = bundle.getString("mean_wave_direction_url");

            if (mean_wave_direction_value != null) {

                ImageView image = (ImageView) rootView.findViewById(R.id.sea_mean_wave_direction_icon);
                TextView text = (TextView) rootView.findViewById(R.id.sea_mean_wave_direction);
                TextView tag = (TextView) rootView.findViewById(R.id.sea_mean_wave_direction_tag);


                tag.setVisibility(View.VISIBLE);
                text.setVisibility(View.VISIBLE);
                image.setVisibility(View.VISIBLE);
                new LoadImageAsyncTask(image).execute(url);
                text.setText(mean_wave_direction_value + " " +
                        " " + bundle.getString("significative_wave_height_units"));
            }


            /*sea_water_temperature*/
            String sea_water_temperature_value = bundle.getString("sea_water_temperature_value");
            Log.d(TAG, "fillISeaPlaces: handleMessage: sea_water_temperature "
                    + sea_water_temperature_value);

            if (sea_water_temperature_value != null) {

                ImageView image = (ImageView) rootView.findViewById(R.id.sea_water_temperature_icon);
                TextView text = (TextView) rootView.findViewById(R.id.sea_water_temperature);
                TextView tag = (TextView) rootView.findViewById(R.id.sea_water_temperature_tag);


                tag.setVisibility(View.VISIBLE);
                text.setVisibility(View.VISIBLE);
                image.setVisibility(View.VISIBLE);
                text.setText(sea_water_temperature_value + " " +
                        " " + bundle.getString("sea_water_temperature_units"));

            }

            /*sea_water_salinity */
            String sea_water_salinity_value = bundle.getString("sea_water_salinity_value");
            Log.d(TAG, "fillISeaPlaces: handleMessage: sea_water_salinity "
                    + sea_water_salinity_value);

            if (sea_water_salinity_value != null) {

                TextView text = (TextView) rootView.findViewById(R.id.sea_water_salinity);
                TextView tag = (TextView) rootView.findViewById(R.id.sea_water_salinity_tag);

                tag.setVisibility(View.VISIBLE);
                text.setVisibility(View.VISIBLE);
                text.setText(sea_water_salinity_value + " " +
                        " " + bundle.getString("sea_water_salinity_units"));

            }


        }

        public void loadSky_state(String sky_state, String url) {
            /*State */
            ((TextView) rootView.findViewById(R.id.sky_state)).
                    setText(sky_state);
            /*Load image state */
            ImageView image = (ImageView) rootView.findViewById(img_place_temperature);
            new LoadImageAsyncTask(image).execute(url);


        }

        public void loadWindImage(String url) {
            /*Load wind image */
            ImageView image = (ImageView) rootView.findViewById(R.id.place_wind_direction_icon);
            new LoadImageAsyncTask(image).execute(url);


        }

    }

    private class LoadImageAsyncTask extends AsyncTask<String, String, Bitmap> {
        private final static String TAG = "LoadImage";
        private ImageView imageView;

        public LoadImageAsyncTask(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            try {
                URL url = new URL(params[0]);
                bitmap = BitmapFactory.decodeStream((InputStream) url.getContent());

            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }
    }

}