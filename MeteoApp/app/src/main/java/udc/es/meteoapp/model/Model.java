package udc.es.meteoapp.model;


import android.os.Handler;

public class Model {

    FindLocality findLocality;
//    FindLocalityHandler findLocalityHandler;
    Handler handler;

    public Model(Handler handler) {
        this.handler = handler;

    }

    public void findLocality(String localityName) {
        //findLocalityHandler = new FindLocalityHandler(); // TODO consider 'this' as argument
        findLocality =
            new FindLocality("findLocality", handler, localityName, PlacesContent.apiKey);
        findLocality.start();
    }


}
