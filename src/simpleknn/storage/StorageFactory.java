package simpleknn.storage;

public class StorageFactory {

    public Storage getStorage (
            String dbType, String dbConnectionString, String dbEndpoint, String dbUser, String dbPasswd) {

        if (dbType == null)
            return null;
        else if (dbType.equalsIgnoreCase("mysql"))
            return new StorageSQL();
        else if (dbType.equalsIgnoreCase("sqlite"))
            return new StorageSQLite(dbConnectionString, dbEndpoint);
        else
            return null;
    }
}
