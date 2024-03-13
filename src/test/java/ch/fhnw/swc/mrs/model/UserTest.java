package ch.fhnw.swc.mrs.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import ch.fhnw.swc.mrs.api.MovieRentalException;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class UserTest {
    private static final String NAME = "name";
    private static final String FIRSTNAME = "first name";
    private static final String EMPTYSTRING = "";

    private static final String LONG_TEXT = "This is a very long name which is over fourty characters long "
            + "and thus should result in an exception";

    @Test
    public void testUser() {
        User u = new User(NAME, FIRSTNAME, LocalDate.now());

        assertAll("verify user constructor", () -> {
            assertNotNull(u, "u must not be null");
            // check if name and first name were stored correctly
            assertAll("verify user properties", () -> assertEquals(NAME, u.getName()),
                    () -> assertEquals(FIRSTNAME, u.getFirstName()));
        }, () -> {
            // check if there exists a rental list
            List<Rental> rentals = u.getRentals();
            assertNotNull(rentals, "rentals list should be empty, not null");
            assertEquals(0, rentals.size());
        }

        );

    }

    @DisplayName("Create User with multiple different valid parameters")
    @ParameterizedTest
    @MethodSource("userValidParamsProvider")
    public void testUserWithMultipleValidInputParams(String name, String firstname, LocalDate birthdate)
            throws InterruptedException {

        User u = new User(name, firstname, birthdate);

        assertAll("verify user constructor", () -> {
            assertNotNull(u, "u must not be null");

            assertAll("verify user properties", () -> assertThat(u.getName(), is(equalTo(name))),
                    () -> assertThat(u.getFirstName(), is(equalTo(firstname))));
        }, () -> {
            // check if there exists a rental list
            List<Rental> rentals = u.getRentals();
            assertThat(rentals, is(notNullValue()));
            assertThat(rentals.size(), is(equalTo(0)));
        }

        );

    }

    static Stream<Arguments> userValidParamsProvider() {

        return Stream.of(arguments("a", "b", LocalDate.of(1969, 7, 19), 0),
                arguments("aaaaa", "bbbbb", LocalDate.of(1969, 7, 19), 18),
                arguments("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb",
                        LocalDate.of(1969, 7, 19), 18));
    }

    /**
     * Test if correct exceptions are thrown in constructor.
     */
    @Test
    public void testUserExceptions() {
        Throwable e = assertThrows(IllegalArgumentException.class, () -> new User(null, FIRSTNAME, LocalDate.now()));
        assertEquals(User.EXC_MISSING_NAME, e.getMessage());

        e = assertThrows(IllegalArgumentException.class, () -> new User(NAME, null, LocalDate.now()));
        assertEquals(User.EXC_MISSING_NAME, e.getMessage());

        e = assertThrows(MovieRentalException.class, () -> new User(EMPTYSTRING, FIRSTNAME, LocalDate.now()));
        assertEquals(User.EXC_ILLEGAL_NAME, e.getMessage());

        e = assertThrows(MovieRentalException.class, () -> new User(NAME, EMPTYSTRING, LocalDate.now()));
        assertEquals(User.EXC_ILLEGAL_NAME, e.getMessage());
    }

    @Test
    public void testSetterGetterId() {
        User u = new User(NAME, FIRSTNAME, LocalDate.now());
        UUID uid = UUID.randomUUID();
        u.setUserid(uid);
        assertEquals(uid, u.getUserid());
        assertThrows(IllegalStateException.class, () -> u.setUserid(UUID.randomUUID()));
    }

    /**
     * Test method for {@link ch.fhnw.edu.rental.model.User#getRentals()} . Test method for
     * {@link ch.fhnw.edu.rental.model.User#setRentals()}.
     */
    @Test
    public void testSetterGetterRentals() {
        List<Rental> list = new LinkedList<Rental>();
        User u = new User(NAME, FIRSTNAME, LocalDate.now());
        u.setRentals(list);
        assertEquals(list, u.getRentals());
        u.setRentals(null);
        assertEquals(0, u.getRentals().size(), "must return an empty list");
    }

    /**
     * Test method for {@link ch.fhnw.edu.rental.model.User#getName()}. Test method for
     * {@link ch.fhnw.edu.rental.model.User#setName()}.
     */
    @Test
    public void testSetterGetterName() {
        User u = new User(NAME, FIRSTNAME, LocalDate.now());

        Throwable e = assertThrows(IllegalArgumentException.class, () -> u.setName(null));
        assertEquals(User.EXC_MISSING_NAME, e.getMessage());

        e = assertThrows(MovieRentalException.class, () -> u.setName(""));
        assertEquals(User.EXC_ILLEGAL_NAME, e.getMessage());

        e = assertThrows(MovieRentalException.class, () -> u.setName(LONG_TEXT));
        assertEquals(User.EXC_ILLEGAL_NAME, e.getMessage());

        u.setName("Bla");
        assertEquals("Bla", u.getName());
    }

    /**
     * Test method for {@link ch.fhnw.edu.rental.model.User#setName(java.lang.String)}. Test method
     * for {@link ch.fhnw.edu.rental.model.User#getFirstName()}.
     */
    @Test
    public void testSetterGetterFirstName() {
        User u = new User(NAME, FIRSTNAME, LocalDate.now());

        Throwable e = assertThrows(IllegalArgumentException.class, () -> u.setFirstName(null));
        assertEquals(User.EXC_MISSING_NAME, e.getMessage());

        e = assertThrows(MovieRentalException.class, () -> u.setFirstName(""));
        assertEquals(User.EXC_ILLEGAL_NAME, e.getMessage());

        e = assertThrows(MovieRentalException.class, () -> u.setFirstName(LONG_TEXT));
        assertEquals(User.EXC_ILLEGAL_NAME, e.getMessage());

        u.setFirstName("Bla");
        assertEquals("Bla", u.getFirstName());
    }

    /**
     * Test method for {@link ch.fhnw.edu.rental.model.User#getCharge()}.
     */
    @Test
    public void testGetCharge() {
        LocalDate today = LocalDate.now();
        double delta = 1e-6;
        User u = new User(NAME, FIRSTNAME, LocalDate.now());
        // a newly created user has no rentals and no charge.
        double charge = u.getCharge();
        assertEquals(0.0d, charge, delta);

        PriceCategory regular = RegularPriceCategory.getInstance();

        // first check regular movie
        Movie mov = new Movie("A", today, regular, 0);
        Rental r = new Rental(u, mov, today);
        charge = r.getRentalFee();
        assertEquals(charge, u.getCharge(), delta);

        // now add another two regular movies
        mov = new Movie("B", today, regular, 0);
        r = new Rental(u, mov, today);
        charge += r.getRentalFee();
        mov = new Movie("C", today, regular, 0);
        r = new Rental(u, mov, today);
        charge += r.getRentalFee();
        assertEquals(charge, u.getCharge(), delta);
    }

    @Test
    public void testEquals() throws Exception {
        User u1 = new User(NAME, FIRSTNAME, LocalDate.now());
        User u2 = new User(NAME, FIRSTNAME, LocalDate.now());
        UUID xid = UUID.randomUUID();
        UUID yid = UUID.randomUUID();
        u1.setUserid(xid);
        u2.setUserid(xid);
        assertTrue(u1.equals(u1));
        assertFalse(u1.equals(NAME));
        assertTrue(u1.equals(u2));
        assertTrue(u2.equals(u1));
        u2 = new User(NAME, FIRSTNAME, LocalDate.now());
        u2.setUserid(yid);
        assertFalse(u1.equals(u2));
        assertFalse(u2.equals(u1));
        u1 = new User(NAME, FIRSTNAME, LocalDate.now());
        u1.setUserid(yid);
        assertTrue(u1.equals(u2));
        u1.setName("Meier");
        assertFalse(u1.equals(u2));
        assertFalse(u2.equals(u1));
        u2.setName("Meier");
        assertTrue(u1.equals(u2));
        u1.setFirstName("Hans");
        assertFalse(u1.equals(u2));
        assertFalse(u2.equals(u1));
        u2.setFirstName("Hans");
        assertTrue(u1.equals(u2));
    }

    @Test
    public void testHashCode() throws Exception {
        // dummy user objects
        User x = new User(NAME, FIRSTNAME, LocalDate.now());
        User y = new User(NAME, FIRSTNAME, LocalDate.now());

        UUID xid = UUID.randomUUID();
        assertEquals(x.hashCode(), y.hashCode());
        x.setUserid(xid);
        assertTrue(x.hashCode() != y.hashCode());
        y.setUserid(xid);
        assertEquals(x.hashCode(), y.hashCode());
    }

}
