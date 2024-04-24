package ch.fhnw.swc.mrs.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import ch.fhnw.swc.mrs.api.MovieRentalException;

@DisplayName("Test Lab5 task 3 - 5")
public class AgeRatingTest {

    /******************************************************************
     * Tests for age rating of Movies
     *****************************************************************/
    private static final String TITLE = "aTitle";
    private static final LocalDate TODAY = LocalDate.now();

    private static final String NAME = "aName";
    private static final String FIRSTNAME = "aFistName";

    @DisplayName("Ctor with ageRating, simple approach")
    @Test
    void testCtorWithAgeRating() {

        Movie m;
        m = new Movie(TITLE, TODAY, Movie.MIN_AGE_RATING_AGE);
        assertEquals(Movie.MIN_AGE_RATING_AGE, m.getAgeRating());

        m = new Movie(TITLE, TODAY, Movie.MIN_AGE_RATING_AGE + 1);
        assertEquals(Movie.MIN_AGE_RATING_AGE + 1, m.getAgeRating());

        m = new Movie(TITLE, TODAY, Movie.MAX_AGE_RATING_AGE - 1);
        assertEquals(Movie.MAX_AGE_RATING_AGE - 1, m.getAgeRating());

        m = new Movie(TITLE, TODAY, Movie.MAX_AGE_RATING_AGE);
        assertEquals(Movie.MAX_AGE_RATING_AGE, m.getAgeRating());

        m = new Movie(TITLE, TODAY, (Movie.MAX_AGE_RATING_AGE - Movie.MIN_AGE_RATING_AGE) / 2);
        assertEquals((Movie.MAX_AGE_RATING_AGE - Movie.MIN_AGE_RATING_AGE) / 2, m.getAgeRating());
    }

    @DisplayName("Ctor with ageRating, using array")
    @Test
    void testCtorWithAgeRatingArray() {
        int[] legalAgeRatings = new int[] {
            Movie.MIN_AGE_RATING_AGE, Movie.MIN_AGE_RATING_AGE + 1,
            (Movie.MIN_AGE_RATING_AGE + Movie.MAX_AGE_RATING_AGE) / 2, Movie.MAX_AGE_RATING_AGE - 1,
            Movie.MAX_AGE_RATING_AGE };

        for (int agerating : legalAgeRatings) {
            Movie m = new Movie(TITLE, TODAY, agerating);
            assertEquals(agerating, m.getAgeRating());
        }
    }

    @DisplayName("Ctor with ageRating, using parameterized test")
    @ParameterizedTest
    @ValueSource(ints = { Movie.MIN_AGE_RATING_AGE, Movie.MIN_AGE_RATING_AGE + 1,
                          (Movie.MIN_AGE_RATING_AGE + Movie.MAX_AGE_RATING_AGE) / 2, Movie.MAX_AGE_RATING_AGE - 1,
                          Movie.MAX_AGE_RATING_AGE })
    void testCtorWithAgeRatingParameterized(int ageRating) {
        Movie m = new Movie(TITLE, TODAY, ageRating);
        assertEquals(ageRating, m.getAgeRating());
    }

    @DisplayName("Ctor with illegal ageRatings")
    @ParameterizedTest
    @ValueSource(ints = { Integer.MIN_VALUE, Movie.MIN_AGE_RATING_AGE - 1, Movie.MIN_AGE_RATING_AGE - 400,
                          Movie.MAX_AGE_RATING_AGE + 1, Movie.MAX_AGE_RATING_AGE + 5000, Integer.MAX_VALUE })
    void testCtorWithIllegalAgeRating(final int ageRating) {
        assertThrows(IllegalArgumentException.class, () -> new Movie(TITLE, TODAY, ageRating));
    }

    @DisplayName("Test User constructor with legal parameter birthdate")
    @Test
    public void testUserCtorWithLegalBirthdates() {
        LocalDate[] legalBirthdates = new LocalDate[] {
            null, TODAY, TODAY.minusDays(1), TODAY.minusYears(4),
            TODAY.minusYears(User.MAX_USER_AGE / 2), TODAY.minusYears(User.MAX_USER_AGE).plusDays(1),
            TODAY.minusYears(User.MAX_USER_AGE) };

        // since the birthdate may not have been tested yet in the User class, it is
        // tested here by calling the get method
        for (LocalDate d : legalBirthdates) {
            User u = new User(NAME, FIRSTNAME, d);
            if (d != null) {
                assertEquals(d, u.getBirthdate());
            } else {
                // in case of null, we cannot test with now == u.getBirthdate since a few
                // milliseconds may pass between the constructor call and the now call
                // so we test equality or now is after
                assertTrue(TODAY.isEqual(u.getBirthdate()) || TODAY.isAfter(u.getBirthdate()));
            }
        }
    }

    @DisplayName("Test User constructor with illegal parameter birthdate")
    @Test
    public void testUserCtorWithIllegalBirthdate() {

        // illegal birthdates:
        // in the future: now+1,
        // too old: -120ys-1d and greater
        LocalDate[] illegalBirthdates = new LocalDate[] {
                TODAY.plusDays(1), TODAY.plusYears(34),
                TODAY.minusYears(User.MAX_USER_AGE).minusDays(1), TODAY.minusYears(User.MAX_USER_AGE).minusYears(10) };

        for (LocalDate d : illegalBirthdates) {
            Throwable t = assertThrows(IllegalArgumentException.class, () -> new User(NAME, FIRSTNAME, d));
            assertEquals(User.EXC_ILLEGAL_BIRTHDATE, t.getMessage());
        }
    }

    /**
     * Demonstrates the usage of parameterized tests for bulk data testing. Since birthdate is
     * reference type, we use the MethodSource concept. MethodSource does not allow null value, so
     * we use the NullSource annotation to test also null value.
     */
    @DisplayName("Alternative constructor test using parameterized test with method source")
    @ParameterizedTest
    @NullSource // this is necessary, because Arguments does not allow null values
    @MethodSource("getLegalBirthdates")
    void testUserCtorWithLegalBirthdates(LocalDate birthdate) {

        User u = new User(NAME, FIRSTNAME, birthdate);
        if (birthdate != null) {
            assertEquals(birthdate, u.getBirthdate());
        } else {
            // in case of null, we cannot test with now == u.getBirthdate since a few
            // milliseconds may pass between the constructor call and the now call
            // so we test equality or now is after
            assertTrue(TODAY.isEqual(u.getBirthdate()) || TODAY.isAfter(u.getBirthdate()));
        }
    }

    private static Stream<Arguments> getLegalBirthdates() {
        return Stream.of(Arguments.of(TODAY), Arguments.of(TODAY.minusDays(1)), Arguments.of(TODAY.minusYears(4)),
                Arguments.of(TODAY.minusYears(User.MAX_USER_AGE / 2)),
                Arguments.of(TODAY.minusYears(User.MAX_USER_AGE).plusDays(1)),
                Arguments.of(TODAY.minusYears(User.MAX_USER_AGE)));
    }

    @DisplayName("Alternative: Test User constructor with legal birthdate parameter")
    @ParameterizedTest
    @NullSource // add test with null value, sind Arguments does not allow null values
    @ArgumentsSource(LegalBirthdateProvider.class)
    void testUserCtorWithLegalBirthdate(LocalDate d) {
        User u = new User(NAME, FIRSTNAME, d);
        if (d != null) {
            assertEquals(d, u.getBirthdate());
        } else {
            // in case of null, we cannot test with now == u.getBirthdate since a few
            // milliseconds may pass between the constructor call and the now call
            // so we test equality or now is after
            assertTrue(TODAY.isEqual(u.getBirthdate()) || TODAY.isAfter(u.getBirthdate()));
        }
    }

    private static class LegalBirthdateProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
            return Stream
                    .of(TODAY, TODAY.minusDays(1), TODAY.minusYears(4), TODAY.minusYears(User.MAX_USER_AGE / 2),
                            TODAY.minusYears(User.MAX_USER_AGE).plusDays(1), TODAY.minusYears(User.MAX_USER_AGE))
                    .map(Arguments::of);
        }
    }

    @DisplayName("Alternative: Test User constructor with illegal birthdate parameter")
    @ParameterizedTest
    @ArgumentsSource(IllegalBirthdateProvider.class)
    void testUserCtorWithIlegalBirthdate(LocalDate d) {
        Throwable t = assertThrows(IllegalArgumentException.class, () -> new User(NAME, FIRSTNAME, d));
        assertEquals(User.EXC_ILLEGAL_BIRTHDATE, t.getMessage());
    }

    private static class IllegalBirthdateProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
            return Stream.of(TODAY.plusDays(1), TODAY.plusYears(34), TODAY.minusYears(User.MAX_USER_AGE).minusDays(1),
                    TODAY.minusYears(User.MAX_USER_AGE).minusYears(10)).map(Arguments::of);
        }
    }

    /********************************************************
     * Here start the Rental tests
     *******************************************************/

    @DisplayName("new born user renting movie with age rating 0")
    @Test
    public void testRentalCtor1() {
        Movie m = new Movie(TITLE, TODAY, 0);
        User u = new User(NAME, FIRSTNAME, TODAY);

        new Rental(u, m, TODAY);
    }

    @DisplayName("new born user renting a movie with age rating 5")
    @Test
    public void testRentalCtor2() {
        Movie m = new Movie(TITLE, TODAY, 5);
        User u = new User(NAME, FIRSTNAME, TODAY);

        Throwable t = assertThrows(MovieRentalException.class, () -> new Rental(u, m, TODAY));
        assertEquals(Rental.EXC_UNDER_AGE, t.getMessage());
    }

    @DisplayName("18 year old renting adult movie")
    @Test
    public void testRentalCtor3() {
        Movie m = new Movie(TITLE, TODAY, 18);
        User u = new User(NAME, FIRSTNAME, TODAY.minusYears(18));

        new Rental(u, m, TODAY);
    }

    @DisplayName("17 year old renting adult movie")
    @Test
    public void testRentalCtor4() {
        Movie m = new Movie(TITLE, TODAY, 18);
        User u = new User(NAME, FIRSTNAME, TODAY.minusYears(18).plusDays(1));

        Throwable t = assertThrows(MovieRentalException.class, () -> new Rental(u, m, TODAY));
        assertEquals(Rental.EXC_UNDER_AGE, t.getMessage());
    }

}
