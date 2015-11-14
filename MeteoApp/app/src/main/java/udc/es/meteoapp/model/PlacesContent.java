package udc.es.meteoapp.model;


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

    private static String[] localities = {
            "Vigo", "A Coruña", "Ourense", "Lugo", "Santiago de Compostela", "Pontevedra", "Ferrol",
            "O Narón", "Vilagarcía", "Oleiros", "Carballo", "Arteixo", "Praia das Catedrais", "Redondela",
            "Culleredo", "Ponteareas", "Cangas", "Marín", "Cambre", "A Estrada", "Lalín", "Moaña",
            "Monforte de Lemos", "Boiro", "O Porriño", "Praia Carnota", "Sanxenxo", "Tui", "A Peroxa",
            "Celanova", "Praia de Sada", "Vilalba", "Verín", "Noia", "A Fonsagrada",
            "Gondomar", "Foz", "Manzaneda", "Vigo de Sarria", "Fene", "Cambados", "Betanzos", "Ordes",
            "Bueu", "Baiona", "Rianxo", "A Laracha"
    };

    private static String[] municipality = {
            "VIGO", "CORUÑA (A)", "OURENSE", "LUGO", "SANTIAGO DE COMPOSTELA", "PONTEVEDRA", "FERROL",
            "PONTES DE GARCÍA RODRÍGUEZ (AS)","VILAGARCÍA DE AROUSA", "OLEIROS", "CARBALLO", "ARTEIXO",
            "RIBADEO", "REDONDELA", "CULLEREDO", "PONTEAREAS", "CANGAS", "MARÍN", "CAMBRE", "GONDOMAR", "LALÍN", "MOAÑA",
            "MONFORTE DE LEMOS", "PORTO DO SON", "PORRIÑO (O)", "CARNOTA", "SANXENXO", "TUI", "PEROXA (A)", "CELANOVA", "SADA",
            "VILALBA", "VERÍN", "NOIA", "A FONSAGRADA", "GONDOMAR", "FOZ", "MANZANEDA", "SARRIA", "FENE", "CAMBADOS",
            "BETANZOS", "ORDES", "BUEU", "BAIONA", "RIANXO", "LARACHA (A)"

    };

    private static String[] province = {"Pontevedra", "A Coruña", "Ourense", "Lugo", "A Coruña", "Pontevedra",
            "A Coruña", "A Coruña", "Pontevedra", "A Coruña", "A Coruña", "A Coruña", "Lugo", "Pontevedra",
            "A Coruña", "Pontevedra", "Pontevedra", "Pontevedra", "A Coruña", "Pontevedra", "Pontevedra",
            "Pontevedra", "Lugo", "A Coruña", "Pontevedra", "A Coruña", "Pontevedra", "Pontevedra", "Ourense",
            "Ourense", "A Coruña", "Lugo", "Ourense", "A Coruña", "Lugo", "Pontevedra", "Lugo", "Ourense", "Lugo",
            "A Coruña", "Pontevedra", "A Coruña", "A Coruña", "Pontevedra", "Pontevedra", "A Coruña", "A Coruña"
    };


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
        public String locality_api_id;
        Details details;

        public PlaceItem(String locality_id, String locality_name, String locality_municipality, String locality_province) {
            this.locality_municipality = locality_municipality;
            this.locality_province = locality_province;
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
