package knightinc;

import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.db.DataSourceFactory;

import javax.validation.Valid;
import javax.validation.constraints.*;

public class BookerConfiguration extends Configuration {

    @Valid
    @NotNull
    @JsonProperty("databaseConfig")
    private DatabaseConfig databaseConfig;

    @Valid
    @NotNull
    @JsonProperty("database")
    private DataSourceFactory database = new DataSourceFactory();


    public DatabaseConfig getDatabaseConfig() {
        return databaseConfig;
    }

    public DataSourceFactory getDatabaseAppDataSourceFactory() {
        return database;
    }
}
