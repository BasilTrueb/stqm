package ch.fhnw.swc.mrs;

import ch.fhnw.swc.mrs.api.MRSServices;
import ch.fhnw.swc.mrs.controller.MovieController;
import ch.fhnw.swc.mrs.controller.UserController;
import ch.fhnw.swc.mrs.data.SimpleMRSServices;
import ch.fhnw.swc.mrs.util.Filters;
import static spark.Spark.port;
import static spark.Spark.staticFiles;
import static spark.Spark.exception;
import static spark.Spark.awaitInitialization;

/**
 * The main class to start the application as a web server.
 */
public final class Application {

    /**
     * The Application main entry point.
     * 
     * @param args are ignored.
     * @throws Exception whenever something goes wrong.
     */
    public static void main(String[] args) throws Exception {
        // Instantiate dependencies
        MRSServices backend = new SimpleMRSServices();
        backend.createDB();

        // Configure Spark
        exception(Exception.class, (e, req, res) -> e.printStackTrace());
        // let spark find a free port
        port(0);
        staticFiles.location("/public");
        staticFiles.expireTime(600L);

        // Set up before-filters (called before each get/post)
        Filters.beforeGetPost();

        // Set up routes
        MovieController.init(backend);
        UserController.init(backend);
        // get("*", ViewUtil.notFound);

        // Set up after-filters (called after each get/post)
        Filters.afterGetPost();

        awaitInitialization(); // IMPORTANT: Wait for spark server to be initialized

    }

    /**
     * @return returns the port used by spark server.
     */
    public static int getPort() {
        return port();
    }

    /**
     * Stops spark server.
     */
    public static void stop() {
        spark.Spark.stop();
    }

    private Application() {
    }
}
