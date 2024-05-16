package ch.fhnw.swc.mrs.data;

import java.util.List;
import java.util.function.Consumer;

import ch.fhnw.swc.mrs.api.MovieRentalException;
import ch.fhnw.swc.mrs.model.Rental;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

/**
 * The Rental data object model class.
 * 
 */
public class RentalDAO {
    
    /** Message when attempting to update rentals. */
    public static final String EXC_UPDATE_NOT_ALLOWED = "Rentals cannot be updated.";
    
    /** Query to get all Rentals. */
    private static final String GET_ALL_RENTALS = "SELECT r FROM Rental r";
    /** Query to get rental by user. */
    private static final String GET_RENTAL_BY_USER = "SELECT r FROM Rental r WHERE r.user.id = :userid";

    private EntityManager em;
    
    /**
     * Create a data access object (DAO) for rental related database queries.
     * @param em EntityManager to use with this DAO.
     */
    public RentalDAO(EntityManager em) {
        this.em = em;
    }

    /**
     * Retrieve all rentals stored in this system.
     * 
     * @return a list of all rentals.
     */
    public List<Rental> getAll() {
        TypedQuery<Rental> query = em.createQuery(GET_ALL_RENTALS, Rental.class);
        List<Rental> result = query.getResultList();
        return result;
    }

    /**
     * Retrieve a rental by its identification.
     * 
     * @param rentalid the unique identification of the rental object to retrieve.
     * @return the rental with the given identification or <code>null</code> if none found.
     */
    public Rental getById(long rentalid) {
        return em.find(Rental.class, rentalid);
    }

    /**
     * Retrieve all rentals of a user.
     * 
     * @param userid to retrieve rentals from.
     * @return all rentals of this user, possibly empty list.
     */
    public List<Rental> getRentalsByUser(long userid) {
        TypedQuery<Rental> query = em.createQuery(GET_RENTAL_BY_USER, Rental.class).setParameter("userid", userid);
        List<Rental> result = query.getResultList();
        return result;
    }

    /**
     * Persist a Rental object. This method will only create a new rental to the database.
     * It is NOT POSSIBLE to update Rentals (by intention).
     * @param rental the object to persist.
     */
    public void save(Rental rental) {
        if (rental.getRentalId() != 0) {
            throw new MovieRentalException(EXC_UPDATE_NOT_ALLOWED);
        }
        executeInsideTransaction(em -> {
            em.persist(rental);
            em.merge(rental.getMovie());
            em.merge(rental.getUser());
        });
    }

    /**
     * Remove a rental from the database.
     * 
     * @param rental the Rental to remove.
     */
    public void delete(Rental rental) {
        executeInsideTransaction(em -> {
            Rental rentalId = em.find(Rental.class, rental.getRentalId());
            if (rentalId != null) {
                var user = rental.getUser();
                var movie = rental.getMovie();
                user.removeRental(rental);
                movie.setRented(false);
                em.merge(user);
                em.merge(movie);
                em.remove(em.merge(rental));
            }
        });
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
