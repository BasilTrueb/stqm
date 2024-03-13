package ch.fhnw.swc.mrs.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ch.fhnw.swc.mrs.api.MovieRentalException;

/**
 * Unit tests for the Rental class.
 */
@DisplayName("Tests for class Rental")
public class RentalTest {

    private User mickey, donald;
    private Movie theKid, goldrush;
    private PriceCategory pc;
    private LocalDate today;

    private UUID mickeyid;
    private UUID donaldid;
    private UUID kidid;
    private UUID goldid;

    /**
     * Creates legal User and Movie objects and sets the reference time stamp to now.
     */
    @BeforeEach
    public void setUp() {
        mickeyid = UUID.randomUUID();
        donaldid = UUID.randomUUID();
        kidid = UUID.randomUUID();
        goldid = UUID.randomUUID();

        today = LocalDate.now();
        pc = RegularPriceCategory.getInstance();
        mickey = new User("Mouse", "Mickey", today);
        mickey.setUserid(mickeyid);
        donald = new User("Duck", "Donald", today);
        donald.setUserid(donaldid);
        theKid = new Movie("The Kid", today, pc, 0);
        theKid.setMovieid(kidid);
        goldrush = new Movie("Goldrush", today, pc, 0);
        goldrush.setMovieid(goldid);
    }

    @DisplayName("Does Rental object get initialized correctly with constructor?")
    @Test
    public void testRentalCtorWithRentalDate() {
        Rental r = new Rental(mickey, theKid, today.minusDays(6));
        doAssertionsForTestRental(kidid, r);
    }

    private void doAssertionsForTestRental(UUID expected, Rental r) {
        // is the rental registered with the user?
        assertTrue(mickey.getRentals().contains(r));
        // has the movie's rented state been set to rented?
        assertTrue(theKid.isRented());
        // is the number of rental days set correctly?
        assertEquals(6, r.getRentalDays());
        // has the rental date been set?
        assertNotNull(r.getRentalDate());
        // do we get the objects that we set?
        assertEquals(mickey, r.getUser());
        assertEquals(theKid, r.getMovie());
    }

    @DisplayName("Throws exception when ctor is called with null User ?")
    @Test
    public void testRentalCtorWithNullUser() {
        Throwable t = assertThrows(NullPointerException.class, () -> new Rental(null, theKid, today));
        assertEquals(Rental.EXC_USER_NULL, t.getMessage());
    }

    @DisplayName("Throws exception when ctor is called with null Movie?")
    @Test
    public void testRentalCtorWithNullMovie() {
        Throwable t = assertThrows(MovieRentalException.class, () -> new Rental(mickey, null, today));
        assertEquals(Rental.EXC_MOVIE_NOT_RENTALBE, t.getMessage());
    }

    @DisplayName("Throws exception when ctor is called with null Date?")
    @Test
    public void testRentalCtorWithNullDate() {
        Throwable t = assertThrows(IllegalArgumentException.class, () -> new Rental(mickey, theKid, null));
        assertEquals(Rental.EXC_RENTAL_DATE_IN_FUTURE, t.getMessage());
    }

    @DisplayName("Throws exception when ctor is called with rental date in the future?")
    @Test
    public void testRentalCtorWithFutureDate() {
        Throwable t = assertThrows(IllegalArgumentException.class, () -> new Rental(mickey, theKid, today.plusDays(1)));
        assertEquals(Rental.EXC_RENTAL_DATE_IN_FUTURE, t.getMessage());
    }

    @DisplayName("Throws exception when user exceeds max. no. of movie rentals?")
    @Test
    public void testRentMoreThanAllowedNumberOfMovies() {
        for (int i = 0; i < User.MAX_RENTABLE_MOVIES; i++) {
            Movie m = new Movie("Movie" + String.valueOf(i), today, pc, 0); // generate enumerated
                                                                            // Movies
            new Rental(mickey, m, today);
        }
        Throwable t = assertThrows(MovieRentalException.class, () -> {
            Movie m = new Movie("MovieInExcess", today, pc, 0);
            new Rental(mickey, m, today);
        });
        assertEquals(Rental.EXC_TOO_MANY_MOVIES_RENTED, t.getMessage());
    }

    @DisplayName("Is rental duration calculated correctly?")
    @Test
    public void testCalcDaysOfRental() {
        LocalDate rentalDate = LocalDate.now().minusDays(6);
        Rental r = new Rental(mickey, theKid, rentalDate);

        int days = r.getRentalDays();
        assertEquals(6, days);
    }

    @DisplayName("Do Id getter and setter work correctly and prevent setting id once it is set?")
    @Test
    public void testSetterGetterId() {
        Rental r = new Rental(mickey, theKid, today);
        UUID xid = UUID.randomUUID();
        UUID yid = UUID.randomUUID();
        r.setRentalId(xid);
        assertEquals(xid, r.getRentalId());

        // setting id a 2nd time
        assertThrows(IllegalStateException.class, () -> r.setRentalId(yid));
        assertEquals(xid, r.getRentalId());
    }

    @DisplayName("Do Movie getter and setter work correctly and prevent setting null Movie?")
    @Test
    public void testSetterGetterMovie() {
        Rental r = new Rental(mickey, theKid, today);
        r.setMovie(goldrush);
        assertEquals(goldrush, r.getMovie());

        Throwable t = assertThrows(MovieRentalException.class, () -> r.setMovie(null));
        assertEquals(Rental.EXC_MOVIE_NOT_RENTALBE, t.getMessage());
        assertEquals(goldrush, r.getMovie());
    }

    @DisplayName("Do User getter and setter work correctly and prevent setting null User?")
    @Test
    public void testSetterGetterUser() {
        Rental r = new Rental(mickey, theKid, today);
        r.setUser(donald);
        assertEquals(donald, r.getUser());

        Throwable t = assertThrows(NullPointerException.class, () -> r.setUser(null));
        assertEquals(Rental.EXC_USER_NULL, t.getMessage());
        assertEquals(donald, r.getUser());
    }

    @DisplayName("Is hash code calculated correctly?")
    @Test
    public void testHashCode() {
        Rental x = new Rental(mickey, theKid, today);
        theKid.setRented(false);
        Rental y = new Rental(mickey, theKid, today);

        UUID xid = UUID.randomUUID();
        x.setRentalId(xid);
        y.setRentalId(xid);
        assertEquals(x.hashCode(), y.hashCode());

        x.setMovie(goldrush);
        assertTrue(x.hashCode() != y.hashCode());
        y.setMovie(goldrush);
        assertEquals(x.hashCode(), y.hashCode());

        x.setUser(donald);
        assertTrue(x.hashCode() != y.hashCode());
        y.setUser(donald);
        assertEquals(x.hashCode(), y.hashCode());
    }
}
