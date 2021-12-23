package simpleknn;

import simpleknn.config.ToolConfig;
import simpleknn.exceptions.ArgumentsException;
import simpleknn.recommender.SimpleUserBasedKnn;
import simpleknn.recommender.SimpleUserBasedKnnImpl;
import simpleknn.storage.Storage;
import simpleknn.storage.StorageFactory;

import java.util.List;

public class Controller {

    public final static String TAG = "-> ";
    public final static String configFileName = "simple_recommender.conf";

    private Storage storage;
    private ToolConfig toolConfig;
    private SimpleUserBasedKnn simpleUserBasedKnn;

    public Controller(String [] args) {
        toolConfig = new ToolConfig(this, args);
    }

    public void setDatabaseData (
            String dbType, String dbConnectionString, String dbEndpoint, String dbUser, String dbPasswd ) {

        storage = new StorageFactory().getStorage(dbType, dbConnectionString, dbEndpoint, dbUser, dbPasswd);
        storage.createTables (); // Prepare storage if not created
    }

    public void batchProcess (String dataset, int numberRecs, int neighborSize) {

        // TODO compute all users recommendations
    }

    public void userProcess (String dataset, int user, int numberRecs, int neighborSize) {

        simpleUserBasedKnn = new SimpleUserBasedKnnImpl(this, dataset, storage);

        List<Integer> recommendations = simpleUserBasedKnn.
                recommendedItems(user, numberRecs, neighborSize);

        for (Integer i: recommendations)
            System.out.println("Recommendation " + i);
    }

    public void run () {
        try {
            toolConfig.processRunningConfig();
            toolConfig.runConfig();
        } catch (ArgumentsException e) {
            System.err.println(TAG + "[Stopping] " + e.getReason());
        }
    }

}
