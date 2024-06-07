package ch.fhnw.swc.mrs.fixture;

import ch.fhnw.swc.mrs.data.SimpleMRSServices;

/**
 * Helper class to allow opening db only once in an application.
 *
 */
public final class MRSServicesFactory {
    private static SimpleMRSServices mrsServices = new SimpleMRSServices();

    // make constructor private
    private MRSServicesFactory() {
    }

    static {
        mrsServices.createDB();
    }

    static SimpleMRSServices getInstance() {
        return mrsServices;
    }
}
