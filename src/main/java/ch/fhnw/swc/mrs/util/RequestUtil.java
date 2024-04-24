package ch.fhnw.swc.mrs.util;

import spark.Request;

/**
 * Utility class supporting the parsing of http requests.
 */
public final class RequestUtil {

    /**
     * Extract the parameter /:id from the request.
     * 
     * @param request the request to extract the id parameter from.
     * @return the id or -1 if there was none or an illegal one.
     */
    public static long getParamId(Request request) {
        String param = request.params("id");
        return Long.parseLong(param);
    }

    /**
     * Extract the parameter ?name from the request.
     * 
     * @param request the request to get the attribute from.
     * @return the value of the name parameter.
     */
    public static String getParamName(Request request) {
        return request.queryParams("name");
    }

    /**
     * Extract the parameter ?rented from the request.
     * 
     * @param request the request to get the attribute from.
     * @return the rented attributes value.
     */
    public static String getParamRented(Request request) {
        return request.queryParams("rented");
    }

    /**
     * Retrieve the locale from the requests session.
     * 
     * @param request to work on.
     * @return the locale.
     */
    public static String getSessionLocale(Request request) {
        return request.session().attribute("locale");
    }

    /**
     * Determine whether the client accepts html.
     * 
     * @param request the request to work on.
     * @return whether html is an acceptable format.
     */
    public static boolean clientAcceptsHtml(Request request) {
        String accept = request.headers("Accept");
        return accept != null && accept.contains("text/html");
    }

    /**
     * Determine whether the client accepts json.
     * 
     * @param request the request to work on.
     * @return whether json is an acceptable format.
     */
    public static boolean clientAcceptsJson(Request request) {
        String accept = request.headers("Accept");
        return accept != null && accept.contains("application/json");
    }

    // prevent instantiation
    private RequestUtil() {
    }
}
