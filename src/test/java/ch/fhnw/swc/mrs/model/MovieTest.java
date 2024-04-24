package ch.fhnw.swc.mrs.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.stream.Stream;

/**
 * Unit tests for class Movie.
 */
public class MovieTest {
    private LocalDate today;

    // Expected exception messages.
    private static final String TITLE_MSG = "Title must not be null nor emtpy";
    private static final String RD_MSG = "Release date must not be null";

    @BeforeEach
    public void setup() {
        today = LocalDate.now();
    }

    @DisplayName("Test hashCode")
    @Test
    public void testHashCode() throws InterruptedException {
        Movie x = new Movie("Untitled", today, 0);
        Movie y = new Movie("A", today, 0);
        Movie z = new Movie("A", today, 0);

        // do we get consistently the same result?
        int h = x.hashCode();
        assertEquals(h, x.hashCode());
        h = y.hashCode();
        assertEquals(h, y.hashCode());

        // do we get the same result from two equal objects?
        h = y.hashCode();
        assertEquals(h, z.hashCode());

        // still the same hashcode after changing rented state?
        z.setRented(true);
        assertEquals(h, z.hashCode());

        final Movie m = new Movie("A", today, 0); // get a new Movie
        assertEquals(h, m.hashCode());
    }

    @DisplayName("Create a Movie with protected ctor")
    @Test
    public void testMovie() {
        Movie m = new Movie("Untitled", today, 0);
        assertNotNull(m.getReleaseDate());
        assertNotNull(m.getTitle());
        assertFalse(m.isRented());
        assertEquals(0, m.getAgeRating());
    }

    @DisplayName("Test public ctor of Movie with assertAll")
    @Test
    public void testMovieUsingGroupedFeature() {
        Movie m = new Movie("Untitled", today, 0);

        assertAll("verify movie", () -> assertNotNull(m.getReleaseDate()),
                () -> assertEquals(0, m.getAgeRating()), () -> assertNotNull(m.getTitle()),
                () -> assertFalse(m.isRented()));
    }

    @DisplayName("Create a Movie with public ctor")
    @Test
    public void testMovieStringDatePriceCategory() throws InterruptedException {
        LocalDate anotherDay = LocalDate.of(1969, 7, 19);
        Movie m = new Movie("A", anotherDay, 0);
        assertEquals("A", m.getTitle());
        assertEquals(anotherDay, m.getReleaseDate());
        assertFalse(m.isRented());
    }

    /**
     * Demo parameterized testing with Junit5
     * 
     * @param title
     * @param date
     * @param ageRating
     * @throws InterruptedException
     */
    @DisplayName("Create Movie with multiple different valid parameters")
    @ParameterizedTest
    @MethodSource("movieValidParamsProvider")
    public void testMovieWithMultipleValidInputParams(String title, LocalDate date,
            int ageRating) throws InterruptedException {

        Movie m = new Movie(title, date, ageRating);
        assertEquals(title, m.getTitle());
        assertEquals(date, m.getReleaseDate());
        assertEquals(ageRating, m.getAgeRating());
        assertFalse(m.isRented());
    }

    static Stream<Arguments> movieValidParamsProvider() {

        return Stream.of(arguments("a", LocalDate.of(1969, 7, 19), 0),
                arguments("b", LocalDate.of(1969, 7, 19), 18),
                arguments("longtitle", LocalDate.of(2000, 7, 19), 9));
    }

    @DisplayName("Try to instantiate Movie with no or empty title")
    @Test
    public void testExceptionOnMissingTitle() {
        Throwable e = assertThrows(IllegalArgumentException.class, () -> new Movie(null, today, 0));
        assertEquals(TITLE_MSG, e.getMessage());

        e = assertThrows(IllegalArgumentException.class, () -> new Movie("", today, 0));
        assertEquals(TITLE_MSG, e.getMessage());
    }

    @DisplayName("Try to instantiate Movie with no release date")
    @Test
    public void testExceptionOnMissingReleaseDate() {
        Throwable e = assertThrows(IllegalArgumentException.class, () -> new Movie("A", null, 0));
        assertEquals(RD_MSG, e.getMessage());
    }

    @DisplayName("Try ctor with null args")
    @Test
    public void testExceptionMovieStringDate() {
        Throwable e = assertThrows(IllegalArgumentException.class, () -> new Movie(null, today, 0));
        assertEquals(TITLE_MSG, e.getMessage());
        e = assertThrows(IllegalArgumentException.class, () -> new Movie("A", null, 0));
        assertEquals(RD_MSG, e.getMessage());
    }

    @DisplayName("Compare with same object")
    @Test
    public void testEqualsIdentity() {
        Movie m = new Movie("Untitled", today, 0);
        // 1. test on identity
        assertTrue(m.equals(m));
    }

    @DisplayName("Compare with null")
    @Test
    public void testEqualsNull() {
        Movie m = new Movie("Untitled", today, 0);
        // 1. test on identity
        assertFalse(m.equals(null));
    }

    @SuppressWarnings("unlikely-arg-type")
    @DisplayName("Compare Movie with non-Movie object")
    @Test
    public void testEqualsNonMovie() {
        Movie m = new Movie("Untitled", today, 0);
        assertFalse(m.equals("Hallo"));
    }

    @DisplayName("Compare Movie objects that differ in id")
    @Test
    public void testEqualsId() 
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Movie m1 = new Movie("Titanic", today, 0);
        Movie m2 = new Movie("Titanic", today, 0);
        assertTrue(m1.equals(m2));
        assertTrue(m2.equals(m1));

        /* change id */
        m2 = new Movie("Titanic", today, 0);
        Field field = Movie.class.getDeclaredField("id");
        field.setAccessible(true);
        field.set(m2, 5);
        assertFalse(m1.equals(m2));
        assertFalse(m2.equals(m1));
    }

    @DisplayName("Compare Movie objects that differ in their titles")
    @Test
    public void testEqualsTitleDate() {
        Movie m1 = new Movie("Star Wars", today, 0);
        Movie m2 = new Movie("Star Trek", today, 0);
        assertFalse(m1.equals(m2));
        assertFalse(m2.equals(m1));
    }

    @DisplayName("Compare Movie objects that differ in their release dates")
    @Test
    public void testEqualsReleaseDate() {
        Movie m1 = new Movie("Titanic", today.minusDays(1), 0);
        Movie m2 = new Movie("Titanic", today, 0);
        assertFalse(m1.equals(m2));
        assertFalse(m2.equals(m1));
    }

    @DisplayName("Compare Movie objects that differ in their age ratings")
    @Test
    public void testEqualsAgeRating() {
        Movie m1 = new Movie("Titanic", today, 6);
        Movie m2 = new Movie("Titanic", today, 12);
        assertFalse(m1.equals(m2));
        assertFalse(m2.equals(m1));
    }

    @Test
    public void testSetTitle() {
        Movie m = new Movie("Untitled", today, 0);
        m.setTitle("Hallo");
        assertEquals("Hallo", m.getTitle());

        assertThrows(IllegalArgumentException.class, () -> m.setTitle(null));

    }

    @Test
    public void testSetReleaseDate() {
        Movie m = new Movie("Untitled", today, 0);
        m.setReleaseDate(today);
        assertEquals(today, m.getReleaseDate());
        assertThrows(IllegalArgumentException.class, () -> m.setReleaseDate(null));
    }
}
