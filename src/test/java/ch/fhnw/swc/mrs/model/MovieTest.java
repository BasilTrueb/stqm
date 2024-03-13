package ch.fhnw.swc.mrs.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Unit tests for class Movie.
 */
public class MovieTest {
    private LocalDate today;

    // Expected exception messages.
    private static final String PC_MSG = "price category must not be null";
    private static final String TITLE_MSG = "Title must not be null nor emtpy";
    private static final String RD_MSG = "Release date must not be null";

    // PriceCategories used in tests.
    private static final PriceCategory REGULAR = RegularPriceCategory.getInstance();
    private static final PriceCategory CHILDREN = ChildrenPriceCategory.getInstance();

    @BeforeEach
    public void setup() {
        today = LocalDate.now();
    }

    @DisplayName("Test hashCode")
    @Test
    public void testHashCode() throws InterruptedException {
        UUID xid = UUID.randomUUID();
        UUID yzid = UUID.randomUUID();
        Movie x = new Movie("Untitled", today, REGULAR, 0);
        x.setMovieid(xid);
        Movie y = new Movie("A", today, REGULAR, 0);
        y.setMovieid(yzid);
        Movie z = new Movie("A", today, REGULAR, 0);
        z.setMovieid(yzid);

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

        final Movie m = new Movie("A", today, REGULAR, 0); // get a new Movie
        m.setMovieid(yzid);
        m.setPriceCategory(ChildrenPriceCategory.getInstance());
        assertEquals(h, m.hashCode());

        assertThrows(IllegalStateException.class, () -> m.setMovieid(UUID.randomUUID()));
    }

    @DisplayName("Create a Movie with protected ctor")
    @Test
    public void testMovie() {
        Movie m = new Movie("Untitled", today, REGULAR, 0);
        assertNotNull(m.getPriceCategory());
        assertNotNull(m.getReleaseDate());
        assertNotNull(m.getTitle());
        assertFalse(m.isRented());
        assertEquals(0, m.getAgeRating());
    }

    @DisplayName("Test public ctor of Movie with assertAll")
    @Test
    public void testMovieUsingGroupedFeature() {
        Movie m = new Movie("Untitled", today, REGULAR, 0);

        assertAll("verify movie", () -> assertNotNull(m.getPriceCategory()), () -> assertNotNull(m.getReleaseDate()),
                () -> assertEquals(0, m.getAgeRating()), () -> assertNotNull(m.getTitle()),
                () -> assertFalse(m.isRented()));
    }

    @DisplayName("Create a Movie with public ctor")
    @Test
    public void testMovieStringDatePriceCategory() throws InterruptedException {
        LocalDate anotherDay = LocalDate.of(1969, 7, 19);
        Movie m = new Movie("A", anotherDay, REGULAR, 0);
        assertEquals("A", m.getTitle());
        assertEquals(RegularPriceCategory.class, m.getPriceCategory().getClass());
        assertEquals(anotherDay, m.getReleaseDate());
        assertFalse(m.isRented());
    }

    /**
     * Demo parameterized testing with Junit5
     * 
     * @param title
     * @param priceCategory
     * @param date
     * @param ageRating
     * @throws InterruptedException
     */
    @DisplayName("Create Movie with multiple different valid parameters")
    @ParameterizedTest
    @MethodSource("movieValidParamsProvider")
    public void testMovieWithMultipleValidInputParams(String title, PriceCategory priceCategory, LocalDate date,
            int ageRating) throws InterruptedException {

        Movie m = new Movie(title, date, priceCategory, ageRating);
        assertEquals(title, m.getTitle());
        assertEquals(priceCategory, m.getPriceCategory());
        assertEquals(date, m.getReleaseDate());
        assertEquals(ageRating, m.getAgeRating());
        assertFalse(m.isRented());
    }

    static Stream<Arguments> movieValidParamsProvider() {

        return Stream.of(arguments("a", ChildrenPriceCategory.getInstance(), LocalDate.of(1969, 7, 19), 0),
                arguments("b", NewReleasePriceCategory.getInstance(), LocalDate.of(1969, 7, 19), 18),
                arguments("longtitle", NewReleasePriceCategory.getInstance(), LocalDate.of(2000, 7, 19), 9));
    }

    @DisplayName("Try to instantiate Movie with no or empty title")
    @Test
    public void testExceptionOnMissingTitle() {
        Throwable e = assertThrows(IllegalArgumentException.class, () -> new Movie(null, today, REGULAR, 0));
        assertEquals(TITLE_MSG, e.getMessage());

        e = assertThrows(IllegalArgumentException.class, () -> new Movie("", today, REGULAR, 0));
        assertEquals(TITLE_MSG, e.getMessage());
    }

    @DisplayName("Try to instantiate Movie with no price category")
    @Test
    public void testExceptionOnMissingPriceCategory() {
        Throwable e = assertThrows(IllegalArgumentException.class, () -> new Movie("A", today, null, 0));
        assertEquals(PC_MSG, e.getMessage());
    }

    @DisplayName("Try to instantiate Movie with no release date")
    @Test
    public void testExceptionOnMissingReleaseDate() {
        Throwable e = assertThrows(IllegalArgumentException.class, () -> new Movie("A", null, REGULAR, 0));
        assertEquals(RD_MSG, e.getMessage());
    }

    @DisplayName("Try to re-set id")
    @Test
    public void testIdReset() {
        Movie m = new Movie("Untitled", today, REGULAR, 0);

        assertNull(m.getMovieid());

        UUID xid = UUID.randomUUID();
        UUID yid = UUID.randomUUID();
        m.setMovieid(xid);
        assertEquals(xid, m.getMovieid());

        Exception e = assertThrows(IllegalStateException.class, () -> m.setMovieid(yid));
        assertEquals(Movie.EXC_ID_FIXED, e.getMessage());
    }

    @DisplayName("Try ctor with null args")
    @Test
    public void testExceptionMovieStringDatePriceCategory() {
        Throwable e = assertThrows(IllegalArgumentException.class, () -> new Movie(null, today, REGULAR, 0));
        assertEquals(TITLE_MSG, e.getMessage());
        e = assertThrows(IllegalArgumentException.class, () -> new Movie("A", null, REGULAR, 0));
        assertEquals(RD_MSG, e.getMessage());
        e = assertThrows(IllegalArgumentException.class, () -> new Movie("A", today, null, 0));
        assertEquals(PC_MSG, e.getMessage());
    }

    @DisplayName("Compare with same object")
    @Test
    public void testEqualsIdentity() {
        Movie m = new Movie("Untitled", today, REGULAR, 0);
        // 1. test on identity
        assertTrue(m.equals(m));
    }

    @DisplayName("Compare with null")
    @Test
    public void testEqualsNull() {
        Movie m = new Movie("Untitled", today, REGULAR, 0);
        // 1. test on identity
        assertFalse(m.equals(null));
    }

    @SuppressWarnings("unlikely-arg-type")
    @DisplayName("Compare Movie with non-Movie object")
    @Test
    public void testEqualsNonMovie() {
        Movie m = new Movie("Untitled", today, REGULAR, 0);
        assertFalse(m.equals("Hallo"));
    }

    @DisplayName("Compare Movie objects that differ in id")
    @Test
    public void testEqualsId() {
        UUID xid = UUID.randomUUID();
        UUID yid = UUID.randomUUID();

        Movie m1 = new Movie("Titanic", today, REGULAR, 0);
        Movie m2 = new Movie("Titanic", today, REGULAR, 0);
        m1.setMovieid(xid);
        m2.setMovieid(xid);
        assertTrue(m1.equals(m2));
        assertTrue(m2.equals(m1));
        m2 = new Movie("Titanic", today, REGULAR, 0);
        m2.setMovieid(yid);
        assertFalse(m1.equals(m2));
        assertFalse(m2.equals(m1));
    }

    @DisplayName("Compare Movie objects that differ in their titles")
    @Test
    public void testEqualsTitleDate() {
        UUID xid = UUID.randomUUID();
        Movie m1 = new Movie("Star Wars", today, REGULAR, 0);
        Movie m2 = new Movie("Star Trek", today, REGULAR, 0);
        m1.setMovieid(xid);
        m2.setMovieid(xid);
        assertFalse(m1.equals(m2));
        assertFalse(m2.equals(m1));
    }

    @DisplayName("Compare Movie objects that differ in their release dates")
    @Test
    public void testEqualsReleaseDate() {
        UUID xid = UUID.randomUUID();
        Movie m1 = new Movie("Titanic", today.minusDays(1), REGULAR, 0);
        Movie m2 = new Movie("Titanic", today, REGULAR, 0);
        m1.setMovieid(xid);
        m2.setMovieid(xid);
        assertFalse(m1.equals(m2));
        assertFalse(m2.equals(m1));
    }

    @DisplayName("Compare Movie objects that differ in their price categories")
    @Test
    public void testEqualsPriceCategory() {
        UUID xid = UUID.randomUUID();
        Movie m1 = new Movie("Titanic", today, REGULAR, 0);
        Movie m2 = new Movie("Titanic", today, CHILDREN, 0);
        m1.setMovieid(xid);
        m2.setMovieid(xid);
        assertFalse(m1.equals(m2));
        assertFalse(m2.equals(m1));
    }

    @DisplayName("Compare Movie objects that differ in their age ratings")
    @Test
    public void testEqualsAgeRating() {
        UUID xid = UUID.randomUUID();
        Movie m1 = new Movie("Titanic", today, REGULAR, 6);
        Movie m2 = new Movie("Titanic", today, REGULAR, 12);
        m1.setMovieid(xid);
        m2.setMovieid(xid);
        assertFalse(m1.equals(m2));
        assertFalse(m2.equals(m1));
    }

    @Test
    public void testSetTitle() {
        Movie m = new Movie("Untitled", today, REGULAR, 0);
        m.setTitle("Hallo");
        assertEquals("Hallo", m.getTitle());

        assertThrows(IllegalArgumentException.class, () -> m.setTitle(null));

    }

    @Test
    public void testSetReleaseDate() {
        Movie m = new Movie("Untitled", today, REGULAR, 0);
        m.setReleaseDate(today);
        assertEquals(today, m.getReleaseDate());
        assertThrows(IllegalArgumentException.class, () -> m.setReleaseDate(null));
    }
}
