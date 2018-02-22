package simpleknn;

import simpleknn.config.ToolConfig;
import simpleknn.exceptions.ArgumentsException;
import simpleknn.recommender.SimpleUserBasedKnn;
import simpleknn.recommender.SimpleUserBasedKnnImpl;

import java.util.List;

public class Controller {

    public final static String TAG = "-> ";
    public final static String configFileName = "simple_recommender.conf";

    private String dbType;
    private String dbConnectionString;
    private String dbEndpoint;
    private String dbUser;
    private String dbPasswd;

    private ToolConfig toolConfig;
    SimpleUserBasedKnn simpleUserBasedKnn;

    public Controller(String [] args) {
        toolConfig = new ToolConfig(this, args);
    }

    public void setDatabaseData (
            String dbType, String dbConnectionString, String dbEndpoint, String dbUser, String dbPasswd ) {

        this.dbType = dbType;
        this.dbConnectionString = dbConnectionString;
        this.dbEndpoint = dbEndpoint;
        this.dbUser = dbUser;
        this.dbPasswd = dbPasswd;
    }

    public String getDbType() {
        return dbType;
    }

    public String getDbConnectionString() {
        return dbConnectionString;
    }

    public String getDbEndpoint() {
        return dbEndpoint;
    }

    public String getDbUser() {
        return dbUser;
    }

    public String getDbPasswd() {
        return dbPasswd;
    }

    public void batchProcess (String dataset, int numberRecs, int neighborSize) {
    }

    public void userProcess (String dataset, int user, int numberRecs, int neighborSize) {

        simpleUserBasedKnn = new SimpleUserBasedKnnImpl(this, dataset);

        List<Integer> recommendations = simpleUserBasedKnn.
                recommendedItems(user, numberRecs, neighborSize);
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
