package udc.es.meteoapp.modelo;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class PlacesContent {

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
        "Narón", "Vilagarcía de Arousa", "Oleiros", "Carballo", "Arteixo", "Ames", "Redondela",
        "Culleredo", "Ribeira", "Cangas", "Marín", "Cambre", "Estrada", "Lalín", "Moaña",
        "Monforte de Lemos", "Boiro", "Porriño", "Teo", "Sanxenxo", "Tui", "Poio", "Viveiro", "Mos",
        "Sada", "Vilalba", "Verín", "Noia", "Carballiño", "Gondomar", "Barco de Valdeorras",
        "Tomiño", "Sarria", "Fene", "Cambados", "Betanzos", "Ordes", "Bueu", "Baiona", "Rianxo",
        "Laracha"
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
     * A dummy item representing a piece of content.
     */
    public static class PlaceItem {
        public String locality_id;
        public String content;

        public PlaceItem(String locality_id, String content) {
            this.locality_id = locality_id;
            this.content = content;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
