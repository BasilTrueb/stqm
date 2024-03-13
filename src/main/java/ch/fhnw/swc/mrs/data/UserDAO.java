package ch.fhnw.swc.mrs.data;

import java.util.List;
import java.util.UUID;

import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;

import ch.fhnw.swc.mrs.model.User;

/**
 * Data Access Object that provides access to the underlying database. Use this DAO to access User
 * related data.
 */
public class UserDAO {

    /** SQL statement to delete user. */
    private static final String DELETE_SQL = "DELETE FROM users WHERE userid = :userid";
    /** SQL statement to create user. */
    private static final String INSERT_SQL = "INSERT INTO users ( userid, firstname, name, birthdate )"
            + " VALUES ( :userid, :firstname, :name, :birthdate )";
    /** SQL statement to update user. */
    private static final String UPDATE_SQL = "UPDATE users SET firstname = :firstname, name = :name, "
            + "birthdate = :birthdate WHERE userid = :userid";
    /** SQL statement to get user by userid. */
    public static final String GET_BY_ID_SQL = "SELECT userid, firstname, name, "
            + "birthdate FROM users WHERE userid = :userid";
    /** SQL statement to get user by name. */
    private static final String GET_BY_NAME_SQL = "SELECT userid, firstname, name, birthdate "
            + "FROM users WHERE name = :name";
    /** SQL statement to get all users. */
    private static final String GET_ALL_SQL = "SELECT userid, firstname, name, birthdate FROM users";

    private Sql2o sql2o;

    /**
     * 
     * @param sql2o sql to dao param
     */
    public UserDAO(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    /**
     * Retrieve a user by his/her identification.
     * 
     * @param userid the unique identification of the user object to retrieve.
     * @return the user with the given identification or <code>null</code> if none found.
     */
    public User getById(UUID userid) {
        try (Connection conn = sql2o.open()) {
            Query q = conn.createQuery(GET_BY_ID_SQL);
            q.addParameter("userid", userid);
            User result = q.executeAndFetchFirst(User.class);

            return result;
        }
    }

    /**
     * Retrieve all users stored in this system.
     * 
     * @return a list of all users.
     */
    public List<User> getAll() {
        try (Connection conn = sql2o.open()) {
            return conn.createQuery(GET_ALL_SQL).executeAndFetch(User.class);
        }
    }

    /**
     * Persist a User object. Use this method either when storing a new User object or for updating
     * an existing one.
     * 
     * @param user the object to persist.
     */
    public void saveOrUpdate(User user) {
        UUID userid = user.getUserid();
        String sql = UPDATE_SQL;

        if (userid == null) {
            user.setUserid(UUID.randomUUID());
            sql = INSERT_SQL;
        }
        try (Connection conn = sql2o.open()) {
            conn.createQuery(sql).addParameter("userid", user.getUserid())
                    .addParameter("firstname", user.getFirstName()).addParameter("name", user.getName())
                    .addParameter("birthdate", user.getBirthdate()).executeUpdate();
        }
    }

    /**
     * Remove a user from the database. After this operation the user does not exist any more in the
     * database. Make sure to dispose the object too!
     * 
     * @param userid the User to remove.
     */
    public void delete(UUID userid) {
        try (Connection conn = sql2o.open()) {
            conn.createQuery(DELETE_SQL).addParameter("userid", userid).executeUpdate();
        }
    }

    /**
     * Retrieve a user by his/her name. Use the family name to retrieve a list of all users with
     * that name. Note this method does not support wildcards!
     * 
     * @param name the family name of the users to retrieve.
     * @return a list of users with the given name.
     */
    public List<User> getByName(String name) {
        try (Connection conn = sql2o.open()) {
            return conn.createQuery(GET_BY_NAME_SQL).addParameter("name", name).executeAndFetch(User.class);
        }
    };
}
