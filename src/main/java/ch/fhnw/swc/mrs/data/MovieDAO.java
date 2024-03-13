package ch.fhnw.swc.mrs.data;

import java.util.List;
import java.util.UUID;

import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;

import ch.fhnw.swc.mrs.model.Movie;

/**
 * Provides CRUD operations for Movie objects to and from the database.
 */
public class MovieDAO {
    /** SQL statement to delete movie. */
    private static final String DELETE_SQL = "DELETE FROM movies WHERE movieid = :movieid ";
    /** SQL statement to create movie. */
    private static final String INSERT_SQL = "INSERT INTO movies (movieid, title, rented, "
            + "releasedate, pricecategory, agerating) "
            + "  VALUES (:movieid, :title, :rented, :releasedate, :pricecategory, :agerating) ";
    /** SQL statement to update movie. */
    private static final String UPDATE_SQL = "UPDATE movies " + "SET title = :title, rented = :rented, "
            + "releasedate = :releasedate, pricecategory = :pricecategory , agerating = :agerating"
            + "  WHERE movieid = :movieid";
    /** select clause of queries. */
    private static final String SELECT_CLAUSE = "SELECT movieid, title, rented, releasedate, pricecategory, agerating "
            + "  FROM movies ";
    /** SQL statement to get movie by movieid. */
    public static final String GET_BY_ID_SQL = SELECT_CLAUSE + " WHERE movieid = :movieid";
    /** SQL statement to get movie by name. */
    private static final String GET_BY_TITLE_SQL = SELECT_CLAUSE + " WHERE title = :title";
    /** SQL statement to get all movies. */
    private static final String GET_ALL_SQL = SELECT_CLAUSE;
    /** SQL statement to get all movies of a given rented status. */
    private static final String GET_ALL_RENTED_SQL = SELECT_CLAUSE + " WHERE rented = :rented";

    private Sql2o sql2o;

    /**
     * 
     * @param sql2o the sql to dao param
     */
    public MovieDAO(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    /**
     * Retrieve a movie by its identification.
     * 
     * @param movieid the unique identification of the movie object to retrieve.
     * @return the movie with the given identification or <code>null</code> if none found.
     */
    public Movie getById(UUID movieid) {
        try (Connection conn = sql2o.open()) {
            Query q = conn.createQuery(GET_BY_ID_SQL);
            q = q.addParameter("movieid", movieid);
            Movie m = q.executeAndFetchFirst(Movie.class);
            return m;
        }
    }

    /**
     * Retrieve all movies stored in this system.
     * 
     * @return a list of all movies.
     */
    public List<Movie> getAll() {
        try (Connection conn = sql2o.open()) {
            return conn.createQuery(GET_ALL_SQL).executeAndFetch(Movie.class);
        }
    }

    /**
     * Get movies according to their rented status.
     * 
     * @param rented if the movies shall be rented or not.
     * @return movies that fulfill the rented status.
     */
    public List<Movie> getAll(boolean rented) {
        try (Connection conn = sql2o.open()) {
            return conn.createQuery(GET_ALL_RENTED_SQL).addParameter("rented", rented).executeAndFetch(Movie.class);
        }

    }

    /**
     * Get movies according to their title.
     * 
     * @param title the title of the movie.
     * @return movies that match the title.
     */
    public List<Movie> getByTitle(String title) {
        try (Connection conn = sql2o.open()) {
            return conn.createQuery(GET_BY_TITLE_SQL).addParameter("title", title).executeAndFetch(Movie.class);
        }
    }

    /**
     * Persist a Movie object. Use this method either when storing a new Movie object or for
     * updating an existing one.
     * 
     * @param m the object to persist.
     */
    public void saveOrUpdate(Movie m) {
        UUID movieid = m.getMovieid();
        String sql = UPDATE_SQL;

        if (movieid == null) {
            m.setMovieid(UUID.randomUUID());
            sql = INSERT_SQL;
        }
        try (Connection conn = sql2o.open()) {
            conn.createQuery(sql).addParameter("movieid", m.getMovieid()).addParameter("title", m.getTitle())
                    .addParameter("rented", m.isRented()).addParameter("releasedate", m.getReleaseDate())
                    .addParameter("pricecategory", m.getPriceCategory().toString())
                    .addParameter("agerating", m.getAgeRating()).executeUpdate();
        }
    }

    /**
     * Remove a movie from the database. After this operation the movie does not exist any more in
     * the database. Make sure to dispose the object too!
     * 
     * @param movieid the Movie to remove.
     */
    public void delete(UUID movieid) {
        try (Connection conn = sql2o.open()) {
            conn.createQuery(DELETE_SQL).addParameter("movieid", movieid).executeUpdate();
        }
    }

}
