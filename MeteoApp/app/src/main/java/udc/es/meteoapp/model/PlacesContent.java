package udc.es.meteoapp.model;

import java.util.ArrayList;
import java.util.Arrays;
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

    private static String[] localities = {
        "Vigo", "A Coruña", "Ourense", "Lugo", "Santiago de Compostela", "Pontevedra", "Ferrol",
        "Narón", "Vilagarcía", "Oleiros", "Carballo", "Arteixo", "Ames", "Redondela",
        "Culleredo", "Ribeira", "Cangas", "Marín", "Cambre", "Estrada", "Lalín", "Moaña",
        "Monforte de Lemos", "Boiro", "Porriño", "Teo", "Sanxenxo", "Tui", "Poio", "Viveiro", "Mos",
        "Sada", "Vilalba", "Verín", "Noia", "Carballiño", "Gondomar", "Barco de Valdeorras",
        "Tomiño", "Sarria", "Fene", "Cambados", "Betanzos", "Ordes", "Bueu", "Baiona", "Rianxo",
        "Laracha"
    };

    private static String[] municipality = {
        "VIGO", "CORUÑA (A)", "OURENSE", "LUGO", "SANTIAGO DE COMPOSTELA", "PONTEVEDRA", "FERROL",
        "PONTES DE GARCÍA RODRIGUEZ (AS)", "VILAGARCÍA DE AROUSA"
    };

    private static String[] province = {
        "Pontevedra", "A Coruña", "Ourense", "Lugo", "A Coruña", "Pontevedra", "A Coruña",
        "A Coruña", "Pontevedra"
    };


    static {
        Arrays.sort(localities);
        for(int i = 0; i<localities.length; i++)
            addItem(new PlaceItem((i+1)+"", localities[i]));
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
        String api_id;
        Details details;

        public PlaceItem(String locality_id, String locality_name) {
            this.locality_id = locality_id;
            this.locality_name = locality_name;
        }

        @Override
        public String toString() {
            return locality_name;
        }

    }

    public static class Details {
        public String sky_state;
        public String temperature;
        public String wind;
        public String precipitation_amount;
        public String relative_humidity;
        public String cloud_area_fraction;
        public String air_pressure_at_sea_level;
        public String snow_level;
        public String sea_water_temperature;
        public String significative_wave_height;
        public String mean_wave_direction;
        public String relative_peak_period;
        public String sea_water_salinity;

    }
}
