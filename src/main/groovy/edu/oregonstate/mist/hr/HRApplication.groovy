package edu.oregonstate.mist.hr

import edu.oregonstate.mist.api.Application
import edu.oregonstate.mist.hr.db.HRDAO
import edu.oregonstate.mist.hr.db.HRMockDAO
import edu.oregonstate.mist.hr.resources.HRResource
import io.dropwizard.jdbi.DBIFactory
import io.dropwizard.setup.Environment
import org.skife.jdbi.v2.DBI

/**
 * Main application class.
 */
class HRApplication extends Application<HRConfiguration> {
    /**
     * Parses command-line arguments and runs the application.
     *
     * @param configuration
     * @param environment
     */
    @Override
    public void run(HRConfiguration configuration, Environment environment) {
        this.setup(configuration, environment)
        HRDAO hrDAO = getHRDAO(configuration, environment)

        environment.jersey().register(new HRResource(hrDAO))
        // @todo: register healthcheck
    }

    private HRDAO getHRDAO(HRConfiguration configuration,
                            Environment environment) {
        HRDAO hrDAO
        if (configuration.useTestDAO) {
            //@todo: change this to a config value
            hrDAO = new HRMockDAO(1000)
        } else {
            DBIFactory factory = new DBIFactory()
            DBI jdbi = factory.build(environment, configuration.getDatabase(), "jdbi")
            hrDAO = (HRDAO) jdbi.onDemand(HRDAO.class)
        }

        hrDAO
    }

    /**
     * Instantiates the application class with command-line arguments.
     *
     * @param arguments
     * @throws Exception
     */
    public static void main(String[] arguments) throws Exception {
        new HRApplication().run(arguments)
    }
}
