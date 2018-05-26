package knightinc;

import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import knightinc.core.Person;
import knightinc.dao.PersonDAO;
import knightinc.resources.PersonResource;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookerApplication extends Application<BookerConfiguration> {
    private final static Logger logger = LoggerFactory.getLogger(BookerApplication.class);

    public static void main(final String[] args) throws Exception {
        new BookerApplication().run(args);
    }

    @Override
    public String getName() {
        return "booker";
    }

    private final HibernateBundle<BookerConfiguration> hibernate = new HibernateBundle<BookerConfiguration>(Person.class) {
        @Override
        public DataSourceFactory getDataSourceFactory(BookerConfiguration configuration) {
            return configuration.getDatabaseAppDataSourceFactory();
        }
    };

    @Override
    public void initialize(final Bootstrap<BookerConfiguration> bootstrap) {
        bootstrap.addBundle(hibernate);
        bootstrap.addBundle(new MigrationsBundle<BookerConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(BookerConfiguration configuration) {
                return configuration.getDatabaseAppDataSourceFactory();
            }
        });
    }

    @Override
    public void run(final BookerConfiguration configuration,
                    final Environment environment) throws LiquibaseException, SQLException, ClassNotFoundException {
        logger.warn("Application start...");

        // if anything fails here we do not want the application to continue.
        createDatabaseIfNotExists(configuration);

        runMigrations(configuration);

        registerResources(environment);
    }

    private Connection openConnection(final BookerConfiguration configuration) throws SQLException, ClassNotFoundException {
        DatabaseConfig DatabaseConfig = configuration.getDatabaseConfig();
        String databaseName = DatabaseConfig.getName();
        String port = DatabaseConfig.getPort();

        String user = configuration.getDatabaseAppDataSourceFactory().getUser();
        String password = configuration.getDatabaseAppDataSourceFactory().getPassword();

        String connectionString = String.format("jdbc:postgresql://localhost:%s/%s", port, databaseName);
        logger.info(connectionString);

        Class.forName("org.postgresql.Driver");
        Connection connection = null;
        connection = DriverManager.getConnection(connectionString, user, password);

        return connection;
    }

    private void createDatabaseIfNotExists(final BookerConfiguration configuration) throws ClassNotFoundException, SQLException {
        DatabaseConfig DatabaseConfig = configuration.getDatabaseConfig();

        String databaseName = DatabaseConfig.getName();
        String port = DatabaseConfig.getPort();

        String user = configuration.getDatabaseAppDataSourceFactory().getUser();
        String password = configuration.getDatabaseAppDataSourceFactory().getPassword();

        String connectionString = String.format("jdbc:postgresql://localhost:%s/", port);
        logger.info("Create database if not exists...{}", databaseName);
        logger.info(connectionString);

        Class.forName("org.postgresql.Driver");
        Connection connection = null;
        connection = DriverManager.getConnection(connectionString, user, password);

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


    private void runMigrations(final BookerConfiguration configuration) throws LiquibaseException, SQLException, ClassNotFoundException {
        logger.info("Running Liquibase migrations...");

        Connection connection = openConnection(configuration);

        Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));

        Liquibase liquibase = new liquibase.Liquibase("db/migrations.xml", new ClassLoaderResourceAccessor(), database);

        liquibase.update(new Contexts(), new LabelExpression());
    }


    private void registerResources(final Environment environment) {
        logger.info("Registering resources...");
        final PersonDAO personDAO = new PersonDAO(hibernate.getSessionFactory());

        final PersonResource personResource = new PersonResource(personDAO);

        environment.jersey().register(personResource);
    }

}
