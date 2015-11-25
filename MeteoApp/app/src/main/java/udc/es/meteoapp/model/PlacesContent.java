package udc.es.meteoapp.model;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Helper class for providing sample locality_name for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class PlacesContent {
    String TAG = "MeteoApp";

    final static String apiKey =
            "NZ3eChGun9GfjW2N3LLV41OrIncNd18Tzi8tZ8AjKm0517Afua3LNYq8cWNS2sm3";

    /**
     * An array of sample (dummy) items.
     */
    public static List<PlaceItem> ITEMS = new ArrayList<PlaceItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static Map<String, PlaceItem> ITEM_MAP = new HashMap<String, PlaceItem>();


    private static String[] localities = {"A Coruña", "A Estrada", "A Fonsagrada", "A Laracha", "A Peroxa",
            "Arteixo", "Baiona", "Betanzos", "Boiro", "Bueu", "Cambados", "Cambre", "Cangas", "Carballo",
            "Celanova", "Culleredo", "Fene", "Ferrol", "Foz", "Gondomar", "Lalín", "Lugo", "Manzaneda", "Marín",
            "Moaña", "Monforte de Lemos", "Noia", "O Narón", "O Porriño", "Oleiros", "Ordes", "Ourense", "Ponteareas",
            "Pontevedra", "Praia Carnota", "Praia das Catedrais", "Praia de Sada", "Redondela", "Rianxo",
            "Santiago de Compostela", "Sanxenxo", "Tui", "Verín", "Vigo", "Vigo de Sarria", "Vilagarcía", "Vilalba"};


    private static String[] municipality = {"CORUÑA (A)", "GONDOMAR", "A FONSAGRADA", "LARACHA (A)", "PEROXA (A)", "ARTEIXO", "BAIONA", "BETANZOS", "PORTO DO SON", "BUEU", "CAMBADOS", "CAMBRE", "CANGAS", "CARBALLO",
            "CELANOVA", "CULLEREDO", "FENE", "FERROL", "FOZ", "GONDOMAR", "LALÍN", "LUGO", "MANZANEDA",
            "MARÍN", "MOAÑA", "MONFORTE DE LEMOS", "NOIA", "PONTES DE GARCÍA RODRÍGUEZ (AS)", "PORRIÑO (O)", "OLEIROS", "ORDES",
            "OURENSE", "PONTEAREAS", "PONTEVEDRA","CARNOTA", "RIBADEO", "SADA", "REDONDELA", "RIANXO", "SANTIAGO DE COMPOSTELA",
            "SANXENXO", "TUI", "VERÍN", "VIGO", "SARRIA", "VILAGARCÍA DE AROUSA", "VILALBA"};

    private static String[] province = {"A Coruña", "Pontevedra", "Lugo", "A Coruña", "Ourense", "A Coruña", "Pontevedra",
            "A Coruña", "A Coruña", "Pontevedra", "Pontevedra", "A Coruña", "Pontevedra", "A Coruña", "Ourense", "A Coruña",
            "A Coruña", "A Coruña", "Lugo", "Pontevedra", "Pontevedra", "Lugo", "Ourense", "Pontevedra", "Pontevedra","Lugo",
            "A Coruña","A Coruña", "Pontevedra", "A Coruña", "A Coruña", "Ourense", "Pontevedra", "Pontevedra", "A Coruña",
            "Lugo", "A Coruña", "Pontevedra", "A Coruña","A Coruña", "Pontevedra", "Pontevedra", "Ourense", "Pontevedra",
            "Lugo", "Pontevedra","Lugo"};

    static {

        for (int i = 0; i < localities.length; i++)
            addItem(new PlaceItem((i + 1) + "", localities[i], municipality[i], province[i]));


    }

    private static void addItem(PlaceItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.locality_id, item);
    }

    /**
     * An item representing a locality.
     */
    public static class PlaceItem {
        public String locality_id;
        public String locality_name;
        public String locality_municipality;
        public String locality_province;
        public String locality_type;
        public String locality_api_id;
        Details details;

        public PlaceItem(String locality_id, String locality_name, String locality_municipality, String locality_province) {
            this.locality_municipality = locality_municipality;
            this.locality_province = locality_province;
            this.locality_id = locality_id;
            this.locality_name = locality_name;
            this.locality_api_id = "";
            this.details = new Details();
        }

        @Override
        public String toString() {
            return locality_name;
        }

    }

    public static class Details {
        public Details() {

            timeInstant = "";

            precipitation_amount_units = "";
            precipitation_amount_value = "";

            sky_state_url = "";
            sky_state_value = "";

            temperature_units = "";
            temperature_value = "";

            wind_module_units = "";
            wind_direction_units = "";
            wind_direction_value = "";
            wind_module_value = "";
            wind_direction_iconURL = "";

            snow_level_units = "";
            snow_level_value = "";

            relative_humidity_units = "";
            relative_humidity_value = "";

            cloud_area_fraction_units = "";
            cloud_area_fraction_value = "";

            air_pressure_at_sea_level_units = "";
            air_pressure_at_sea_level_value = "";

            significative_wave_height_units = "";
            significative_wave_height_value = "";

            relative_peak_period_units = "";
            relative_peak_period_value = "";

            mean_wave_direction_units = "";
            mean_wave_direction_value = "";
            mean_wave_direction_url = "";

            sea_water_temperature_units = "";
            sea_water_temperature_value = "";

            sea_water_salinity_units = "";
            sea_water_salinity_value = "";

            wind_direction_string = "";
            wave_direction_string = "";
            sky_state_string = "";

        }

        public String timeInstant;

        public String wind_direction_string;
        public String wave_direction_string;
        public String sky_state_string;

        public String precipitation_amount_units;
        public String precipitation_amount_value;

        public String sky_state_url;
        public String sky_state_value;

        public String temperature_units;
        public String temperature_value;

        public String wind_module_units;
        public String wind_direction_units;
        public String wind_direction_value;
        public String wind_module_value;
        public String wind_direction_iconURL;

        public String snow_level_units;
        public String snow_level_value;

        public String relative_humidity_units;
        public String relative_humidity_value;

        public String cloud_area_fraction_units;
        public String cloud_area_fraction_value;

        public String air_pressure_at_sea_level_units;
        public String air_pressure_at_sea_level_value;

        public String significative_wave_height_units;
        public String significative_wave_height_value;

        public String relative_peak_period_units;
        public String relative_peak_period_value;

        public String mean_wave_direction_units;
        public String mean_wave_direction_value;
        public String mean_wave_direction_url;

        public String sea_water_temperature_units;
        public String sea_water_temperature_value;

        public String sea_water_salinity_units;
        public String sea_water_salinity_value;
    }
}
