package knightinc;

import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import knightinc.core.Person;

public class bookerApplication extends Application<bookerConfiguration> {

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
    }

}
