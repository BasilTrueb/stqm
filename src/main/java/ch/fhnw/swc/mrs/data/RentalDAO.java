package ch.fhnw.swc.mrs.data;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.sql2o.Connection;
import org.sql2o.Sql2o;

import ch.fhnw.swc.mrs.model.Movie;
import ch.fhnw.swc.mrs.model.Rental;
import ch.fhnw.swc.mrs.model.User;

/**
 * The Rental data object model class.
 * 
 */
public class RentalDAO {

    /** SQL statement to delete rental. */
    private static final String DELETE_SQL = "DELETE FROM rentals WHERE rentalid = :rentalid";
    /** SQL statement to create rental. */
    private static final String INSERT_SQL = "INSERT INTO rentals ( rentalid, movieid, userid, rentaldate )"
            + "  VALUES ( :rentalid, :movieid, :userid, :rentaldate )";
    /** select clause of queries. */
    private static final String SELECT_CLAUSE = "SELECT rentalid, movieid, userid, rentaldate FROM rentals ";
    /** SQL statement to get rental by id. */
    private static final String GET_BY_ID_SQL = SELECT_CLAUSE + " WHERE rentalid = :rentalid";
    /** SQL statement to get all rentals from a user. */
    private static final String GET_BY_USER_SQL = SELECT_CLAUSE + " WHERE userid = :userid";
    /** SQL statement to get all rentals. */
    private static final String GET_ALL_SQL = SELECT_CLAUSE;

    private Sql2o sql2o;

    /**
     * 
     * @param sql2o the sql to dao param
     */
    public RentalDAO(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    /**
     * Retrieve a rental by its identification.
     * 
     * @param id the unique identification of the rental object to retrieve.
     * @return the rental with the given identification or <code>null</code> if none found.
     */
    public Rental getById(UUID id) {
        Rntl r = null;
        try (Connection conn = sql2o.open()) {
            r = conn.createQuery(GET_BY_ID_SQL).addParameter("rentalid", id).executeAndFetchFirst(Rntl.class);
        }
        return materializeRental(r);
    }

    /**
     * Retrieve all rentals stored in this system.
     * 
     * @return a list of all rentals.
     */
    public List<Rental> getAll() {
        List<Rntl> list = new LinkedList<>();
        List<Rental> result = new LinkedList<>();
        try (Connection conn = sql2o.open()) {
            list = conn.createQuery(GET_ALL_SQL).executeAndFetch(Rntl.class);
        }
        for (Rntl r : list) {
            result.add(materializeRental(r));
        }
        return result;
    }

    /**
     * Retrieve all rentals of a user.
     * 
     * @param userid to retrieve rentals from.
     * @return all rentals of this user, possibly empty list.
     */
    public List<Rental> getRentalsByUser(UUID userid) {
        List<Rntl> list = new LinkedList<>();
        List<Rental> result = new LinkedList<>();
        try (Connection conn = sql2o.open()) {
            list = conn.createQuery(GET_BY_USER_SQL).addParameter("userid", userid).executeAndFetch(Rntl.class);
        }
        for (Rntl r : list) {
            result.add(materializeRental(r));
        }
        return result;
    }

    private Rental materializeRental(Rntl r) {
        try (Connection conn = sql2o.open()) {
            User u = conn.createQuery(UserDAO.GET_BY_ID_SQL).addParameter("userid", r.userid)
                    .executeAndFetchFirst(User.class);
            Movie m = conn.createQuery(MovieDAO.GET_BY_ID_SQL).addParameter("movieid", r.movieid)
                    .executeAndFetchFirst(Movie.class);
            Rental result = new Rental(u, m, r.rentaldate);
            result.setRentalId(r.rentalid);
            return result;
        }
    }

    /**
     * 
     * @param userid unique user id
     * @param movieid unique movie id
     * @param rentalDate date of rental
     * @return UUID of new rental
     */
    public UUID create(UUID userid, UUID movieid, LocalDate rentalDate) {
        UUID newRentalId = UUID.randomUUID();
        try (Connection conn = sql2o.open()) {
            conn.createQuery(INSERT_SQL).addParameter("rentalid", newRentalId).addParameter("movieid", movieid)
                    .addParameter("userid", userid).addParameter("rentaldate", rentalDate).executeUpdate();

            return newRentalId;
        }
    }

    /**
     * Remove a rental from the database.
     * 
     * @param id the Rental to remove.
     */
    public void delete(UUID id) {
        try (Connection conn = sql2o.open()) {
            conn.createQuery(DELETE_SQL).addParameter("rentalid", id).executeUpdate();
        }
    }

    private static class Rntl {
        private UUID rentalid, movieid, userid;
        private LocalDate rentaldate;
    }
}
