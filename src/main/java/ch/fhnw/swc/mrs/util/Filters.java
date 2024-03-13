package ch.fhnw.swc.mrs.util;

import spark.Request;
import spark.Response;

import static spark.Spark.after;

import spark.Filter;

/**
 * Helper class to filter responses before and after a REST call. 
 *
 */
public final class Filters {

    /**
     * Enable GZIP for all responses.
     */
    private static Filter addGzipHeader = (Request request, Response response) -> {
        response.header("Content-Type", "application/json");
        response.header("Content-Encoding", "gzip");
    };

    /**
     * Set up before-filters (called before each get/post)
     */
    public static void beforeGetPost() {
        // before("*", Filters.addTrailingSlashes);
    }

    /**
     * Set up after-filters (called after each get/post)
     */
    public static void afterGetPost() {
        after("*", Filters.addGzipHeader);
    }

    private Filters() {
    }

}
