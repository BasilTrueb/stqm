package ch.fhnw.swc.mrs.data;

import java.time.LocalDate;
import java.util.List;
import ch.fhnw.swc.mrs.api.MRSServices;
import ch.fhnw.swc.mrs.model.Movie;
import ch.fhnw.swc.mrs.model.Rental;
import ch.fhnw.swc.mrs.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * A MRSServices facade for PostresqlDB access
 * 
 */
public class DbMRSServices implements MRSServices {

    private EntityManagerFactory emf;
    private EntityManager em;

    /**
     * A MRSServices facade for PostresqlDB is initialized according to the passed config.
     */
    public DbMRSServices(String persistenceUnitName) {
        emf = Persistence.createEntityManagerFactory(persistenceUnitName);
        em = emf.createEntityManager();
    }

    /**
     * A MRSServices facade for PostresqlDB is initialized according to the passed config.
     */
    public DbMRSServices() {        
        emf = Persistence.createEntityManagerFactory("MRS.Production");
        em = emf.createEntityManager();
    }

    private MovieDAO getMovieDAO() {
        return new MovieDAO(em);
    }

    private UserDAO getUserDAO() {
        return new UserDAO(em);
    }

    private RentalDAO getRentalDAO() {
        return new RentalDAO(em);
    }

    @Override
    public Movie createMovie(String aTitle, LocalDate aReleaseDate, int anAgeRating) {
        try {
            Movie m = new Movie(aTitle, aReleaseDate, anAgeRating);
            getMovieDAO().saveOrUpdate(m);
            return m;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Movie> getAllMovies() {
        return getMovieDAO().getAll();
    }

    @Override
    public List<Movie> getAllMovies(boolean rented) {
        return getMovieDAO().getAll(rented);
    }

    @Override
    public Movie getMovieById(long id) {
        return getMovieDAO().getById(id);
    }

    @Override
    public boolean updateMovie(Movie movie) {
        try {
            getMovieDAO().saveOrUpdate(movie);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteMovie(long id) {
        try {
            Movie m = getMovieDAO().getById(id);
            getMovieDAO().delete(m);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<User> getAllUsers() {
        return getUserDAO().getAll();
    }

    @Override
    public User getUserById(long id) {
        return getUserDAO().getById(id);
    }

    @Override
    public User getUserByName(String name) {
        List<User> users = getUserDAO().getByName(name);
        return users.size() == 0 ? null : users.get(0);
    }

    @Override
    public User createUser(String aName, String aFirstName, LocalDate aBirthdate) {
        try {
            User u = new User(aName, aFirstName, aBirthdate);
            getUserDAO().saveOrUpdate(u);
            return u;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean updateUser(User user) {
        try {
            getUserDAO().saveOrUpdate(user);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteUser(long id) {
        try {
            User u = getUserDAO().getById(id);
            getUserDAO().delete(u);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Rental> getAllRentals() {
        return getRentalDAO().getAll();
    }

    @Override
    public Rental createRental(long userId, long movieId, LocalDate d) {
        // TO-DO: transaction is missing
        User u = getUserDAO().getById(userId);
        Movie m = getMovieDAO().getById(movieId);

        if (u != null && m != null && !m.isRented() && !d.isAfter(LocalDate.now())) {
            Rental r = new Rental(u, m, d);
            getRentalDAO().save(r);
            m.setRented(true);
            getMovieDAO().saveOrUpdate(m);
            return r;
        }
        return null;
    }

    @Override
    public boolean deleteRental(long id) {
        RentalDAO rdao = getRentalDAO();
        Rental r = rdao.getById(id);
        Movie m = r.getMovie();
        m.setRented(false);
        getMovieDAO().saveOrUpdate(m);
        rdao.delete(r);
        return r != null;
    }

    @Override
    public void createDB() { }

    @Override
    public void removeDB() { }

    public void clearDB() {
        em.clear();
    }
}
