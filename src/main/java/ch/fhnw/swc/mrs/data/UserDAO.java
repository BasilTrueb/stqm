package ch.fhnw.swc.mrs.data;

import java.util.List;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.fhnw.swc.mrs.api.MovieRentalException;
import ch.fhnw.swc.mrs.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

/**
 * Data Access Object that provides access to the underlying database. Use this DAO to access User
 * related data.
 */
public class UserDAO {

    /** Query to get all users. */
    private static final String GET_ALL_USERS = "SELECT u FROM User u";
    /** Query to get user by name. */
    private static final String GET_USER_BY_NAME = "SELECT u FROM User u WHERE u.name = :name";
    
    /** Exception message when user could not be deleted. */
    private static final String EXC_DELETE_FAILED = "User could not be deleted - maybe it didn't exist in the database";
    /** Exception message when user could not be stored. */
    private static final String EXC_STORE_FAILED = "User could not be saved or updated";
    
    private EntityManager em;
    
    /** Logger used to produce logs. */
    private static Logger log = LogManager.getLogger(UserDAO.class);

    /**
     * Create a data access object (DAO) for user related database queries.
     * @param em EntityManager to use with this DAO.
     */
    public UserDAO(EntityManager em) {
        this.em = em;
    }

    /**
     * Retrieve all users stored in this system.
     * 
     * @return a list of all users.
     */
    public List<User> getAll() {
        TypedQuery<User> query = em.createQuery(GET_ALL_USERS, User.class);
        List<User> result = query.getResultList();
        return result;
    }

    /**
     * Retrieve a user by his/her identification.
     * 
     * @param userid the unique identification of the user object to retrieve.
     * @return the user with the given identification or <code>null</code> if none found.
     */
    public User getById(long userid) {
        return em.find(User.class, userid);
    }

    /**
     * Retrieve a user by his/her name. Use the family name to retrieve a list of all users with
     * that name. Note this method does not support wildcards!
     * 
     * @param name the family name of the users to retrieve.
     * @return a list of users with the given name.
     */
    public List<User> getByName(String name) {
        TypedQuery<User> query = em.createQuery(GET_USER_BY_NAME, User.class).setParameter("name", name);
        List<User> result = query.getResultList();
        return result;
    }
    
    /**
     * Persist a User object. Use this method either when storing a new User object or for updating
     * an existing one.
     * 
     * @param user the object to persist.
     */
    public void saveOrUpdate(User user) {
        executeInsideTransaction(em -> {
            if (user.getUserid() != 0) {
                em.merge(user);
            } else {
                em.persist(user);
            }
        }, EXC_STORE_FAILED);
    }

    /**
     * Remove a user from the database. After this operation the user does not exist any more in the
     * database. Make sure to dispose the object too!
     * 
     * @param user the User to remove.
     */
    public void delete(User user) {
        try {
            executeInsideTransaction(em -> {
                User u = em.merge(user);
                em.remove(u);
            }, EXC_DELETE_FAILED);
        } catch (MovieRentalException mre) {
            log.warn(EXC_DELETE_FAILED, mre);
        }
    }

    private void executeInsideTransaction(Consumer<EntityManager> action, String message) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            action.accept(em);
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            throw new MovieRentalException(message, e);
        }
    }

}
