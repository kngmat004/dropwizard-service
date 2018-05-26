package knightinc;

import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import knightinc.core.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class bookerApplication extends Application<bookerConfiguration> {
    private final static Logger logger = LoggerFactory.getLogger(bookerApplication.class);

    public static void main(final String[] args) throws Exception {
        new bookerApplication().run(args);
    }

    @Override
    public String getName() {
        return "booker";
    }

    private final HibernateBundle<bookerConfiguration> hibernate = new HibernateBundle<bookerConfiguration>(Person.class) {
        @Override
        public DataSourceFactory getDataSourceFactory(bookerConfiguration configuration) {
            return configuration.getDatabaseAppDataSourceFactory();
        }
    };

    @Override
    public void initialize(final Bootstrap<bookerConfiguration> bootstrap) {
        bootstrap.addBundle(hibernate);
        bootstrap.addBundle(new MigrationsBundle<bookerConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(bookerConfiguration configuration) {
                return configuration.getDatabaseAppDataSourceFactory();
            }
        });
    }

    @Override
    public void run(final bookerConfiguration configuration,
                    final Environment environment) {
        logger.info("Application start...");
        try {
            createDatabaseIfNotExists(configuration);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }

    }

    private void createDatabaseIfNotExists(final bookerConfiguration configuration) throws ClassNotFoundException, SQLException {

        databaseConfig databaseConfig = configuration.getDatabaseConfig();

        String databaseName = databaseConfig.getName();
        String port = databaseConfig.getPort();

        String user = configuration.getDatabaseAppDataSourceFactory().getUser();
        String password = configuration.getDatabaseAppDataSourceFactory().getPassword();

        String connectionString = String.format("jdbc:postgresql://localhost:%s/", port);
        logger.info("Create database if not exists...{}", databaseName);
        logger.info(connectionString);

        Class.forName("org.postgresql.Driver");
        Connection connection = null;
        connection = DriverManager.getConnection(connectionString,user, password);

        ResultSet executeQuery = connection.createStatement().executeQuery("SELECT datname FROM pg_database;");

        List<String> tables = new ArrayList<>();

        while(executeQuery.next()) {
            tables.add(executeQuery.getString(1));
        }

        if(!tables.contains(databaseName.toLowerCase())) {
            logger.info(databaseName + " does not exist... creating");
            connection.createStatement().execute(String.format("Create database %s;", databaseName));
        } else {
            logger.info(databaseName + " already exists");
        }
        connection.close();
    }

}
