package udc.es.meteoapp.model;


public class Model {

    FindLocality findLocality;
    FindLocalityHandler findLocalityHandler;

    public Model() {

    }

    public void findLocality(String localityName) {
        findLocalityHandler = new FindLocalityHandler(); // TODO consider 'this' as argument
        findLocality =
            new FindLocality("findLocality", findLocalityHandler, localityName, PlacesContent.apiKey);
        findLocality.start();
    }


}
