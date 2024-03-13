package ch.fhnw.swc.mrs.data;

import java.time.LocalDate;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

@SuppressWarnings("rawtypes")
public class IsBeforeDate extends BaseMatcher {

    private LocalDate dateToCheck = LocalDate.now();

    public void describeTo(Description description) {
        description.appendText("current date is after " + dateToCheck);
    }

    protected IsBeforeDate(LocalDate dateToCheck) {
        this.dateToCheck = dateToCheck;
    };

    public static Matcher isBefore(LocalDate dateToCheck) {
        return new IsBeforeDate(dateToCheck);
    }

    @Override
    public boolean matches(Object d) {
        return ((LocalDate) d).isBefore(dateToCheck);
    }
}
