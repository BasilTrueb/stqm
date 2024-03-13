package ch.fhnw.swc.mrs.util;

/**
 * Contains constants for http status codes used in MRS.
 */
public final class StatusCodes {
    private StatusCodes() {
    }

    /** successful request */
    public static final int OK = 200;
    /** request fulfilled, resutling in creation of a new resource. */
    public static final int CREATED = 201;
    /** The server successfully processed the request, and is not returning any content. */
    public static final int NO_CONTENT = 204;
    /** The server cannot or will not process the request due to an apparent client error. */
    public static final int BAD_REQUEST = 400;
    /** The requested resource could not be found. */
    public static final int NOT_FOUND = 404;
}
