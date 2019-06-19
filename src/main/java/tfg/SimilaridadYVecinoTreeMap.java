package tfg;

import java.util.Comparator;
import java.util.TreeMap;

public class SimilaridadYVecinoTreeMap extends TreeMap<SimilaridadYVecino, Integer> {

    SimilaridadYVecinoTreeMap() {
        super(new Comparator<SimilaridadYVecino>() {
            @Override
            public int compare(SimilaridadYVecino a1, SimilaridadYVecino a2) {

                if (a1.getSimilarity() < a2.getSimilarity()) {
                    return 1;

                } else if (a1.getSimilarity() == a2.getSimilarity()) {
                    if (a1.getUserId() < a2.getUserId()) {
                        return 1;
                    } else if (a1.getUserId() == a2.getUserId()) {
                        return 0;
                    } else {
                        return -1;
                    }

                } else {
                    return -1;
                }
            }
        });
    }


    void put(SimilaridadYVecino similaridadYVecino) {
        put(similaridadYVecino, null);
    }
}
