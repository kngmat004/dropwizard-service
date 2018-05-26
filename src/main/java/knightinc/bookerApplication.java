package knightinc;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class bookerApplication extends Application<bookerConfiguration> {

    public static void main(final String[] args) throws Exception {
        new bookerApplication().run(args);
    }

    @Override
    public String getName() {
        return "booker";
    }

    @Override
    public void initialize(final Bootstrap<bookerConfiguration> bootstrap) {
        // TODO: application initialization
    }

    @Override
    public void run(final bookerConfiguration configuration,
                    final Environment environment) {
        // TODO: implement application
    }

}
