package ch.fhnw.swc.mrs.data;

import java.util.List;
import java.util.function.Consumer;

import ch.fhnw.swc.mrs.api.MovieRentalException;
import ch.fhnw.swc.mrs.model.Movie;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

/**
 * Provides CRUD operations for Movie objects to and from the database.
 */
public class MovieDAO {
    /** Query to get all movies. */
    private static final String GET_ALL_MOVIES = "SELECT m FROM Movie m";
    /** Query to get movies by title. */
    private static final String GET_MOVIE_BY_TITLE = "SELECT m FROM Movie m WHERE m.title like :title";
    /** Query to get rented movies. */
    private static final String GET_RENTED_MOVIES = "SELECT m FROM Movie m WHERE m.rented = :rented";

    private EntityManager em;

    /**
     * Create a data access object (DAO) for movie related database queries.
     * 
     * @param em the EntityManager to use with this DAO.
     */
    public MovieDAO(EntityManager em) {
        this.em = em;
    }

    /**
     * Retrieve a movie by its identification.
     * 
     * @param movieid the unique identification of the movie object to retrieve.
     * @return the movie with the given identification or <code>null</code> if none found.
     */
    public Movie getById(long movieid) {
        return em.find(Movie.class, movieid);
    }

    /**
     * Retrieve all movies stored in this system.
     * 
     * @return a list of all movies.
     */
    public List<Movie> getAll() {
        TypedQuery<Movie> query = em.createQuery(GET_ALL_MOVIES, Movie.class);
        List<Movie> result = query.getResultList();
        return result;
    }

    /**
     * Get movies according to their rented status.
     * 
     * @param rented if the movies shall be rented or not.
     * @return movies that fulfill the rented status.
     */
    public List<Movie> getAll(boolean rented) {
        TypedQuery<Movie> query = em.createQuery(GET_RENTED_MOVIES, Movie.class).setParameter("rented", rented);
        List<Movie> result = query.getResultList();
        return result;
    }

    /**
     * Get movies according to their title.
     * 
     * @param title the title of the movie.
     * @return movies that match the title.
     */
    public List<Movie> getByTitle(String title) {
        TypedQuery<Movie> query = em.createQuery(GET_MOVIE_BY_TITLE, Movie.class).setParameter("title", title);
        List<Movie> result = query.getResultList();
        return result;
    }

    /**
     * Persist a Movie object. Use this method either when storing a new Movie object or for
     * updating an existing one.
     * 
     * @param movie the object to persist.
     */
    public void saveOrUpdate(Movie movie) {
        executeInsideTransaction(em -> {
            if (movie.getMovieid() != 0) {
                em.merge(movie);
            } else {
                em.persist(movie);
            }
        });
    }

    /**
     * Remove a movie from the database. After this operation the movie does not exist any more in
     * the database. Make sure to dispose the object too!
     * 
     * @param movie the Movie to remove.
     */
    public void delete(Movie movie) {
        executeInsideTransaction(em -> em.remove(em.merge(movie)));
    }

    private void executeInsideTransaction(Consumer<EntityManager> action) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            action.accept(em);
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            throw new MovieRentalException("DB operation failed", e);
        }
    }

}
