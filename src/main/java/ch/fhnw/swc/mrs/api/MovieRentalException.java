package ch.fhnw.swc.mrs.api;

import java.io.Serializable;

/**
 * Class MovieRentalException represents a unchecked exception which will be thrown if an error
 * occurs using a method of this package.
 * 
 * @author Juerg Luthiger
 */
public class MovieRentalException extends RuntimeException implements Serializable {
    /**
     * Serial number.
     */
    private static final long serialVersionUID = -2166850969786176646L;

    /**
     * Creates a new MovieMgmtException with given message.
     * 
     * @param s Description of what caused this exception.
     */
    public MovieRentalException(String s) {
        super(s);
    }
    
    /**
     * Creates a new MovieRental Exception with a message and a root cause.
     * @param s Description of what caused this exception.
     * @param e the root exception causing this exception.
     */
    public MovieRentalException(String s, Exception e) {
        super(s, e);
    }
}
