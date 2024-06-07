package ch.fhnw.swc.mrs.fixture;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Wrapper class to control the date format.
 * Unfortunately Fit does not use standard Java-Bean conventions. Instead it
 * users reflection to access (private) attributes of a class. Therefore
 * the suppressions for the "unused" warning. Furthermore
 * Fit lacks support for LocalDate although LocalDate supports the parse
 * method that Fit expects to be able to call.
 *
 * This wrapper class is a workaround for these shortcomings. It is also
 * a simple way to control the date formatting in Fit tables.
 */
public class ATUser {
    @SuppressWarnings("unused")
    private String name;
    @SuppressWarnings("unused")
    private String firstname;
    @SuppressWarnings("unused")
    private String birthdate;
    public ATUser(String aName, String aFirstname, LocalDate aBirthdate) {
        name = aName;
        firstname = aFirstname;
        birthdate = aBirthdate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
