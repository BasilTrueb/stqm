package ch.fhnw.swc.mrs.data;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.sql.DataSource;

import org.postgresql.ds.PGSimpleDataSource;
import org.sql2o.Sql2o;
import org.sql2o.converters.Converter;
import org.sql2o.converters.UUIDConverter;
import org.sql2o.quirks.PostgresQuirks;

import ch.fhnw.swc.mrs.api.MRSServices;
import ch.fhnw.swc.mrs.model.Movie;
import ch.fhnw.swc.mrs.model.PriceCategory;
import ch.fhnw.swc.mrs.model.Rental;
import ch.fhnw.swc.mrs.model.User;
import ch.fhnw.swc.mrs.util.LocalDateConverter;
import ch.fhnw.swc.mrs.util.PriceCategoryConverter;

/**
 * A MRSServices facade for PostresqlDB access
 * 
 */
public class DbMRSServices implements MRSServices {

    private PGSimpleDataSource datasource;
    private Sql2o sql2o;

    /**
     * A MRSServices facade for PostresqlDB is initialized according to the passed config.
     * 
     * @param pathToConfig the classpath to the database configuration file
     * @throws IOException if config file not found
     */
    public DbMRSServices(String pathToConfig) throws IOException {

        Properties config = readDbConfig(pathToConfig);

        String userName = config.getProperty("user");
        String password = config.getProperty("pwd");
        String url = config.getProperty("url");
        datasource = new PGSimpleDataSource();
        datasource.setUrl(url);
        datasource.setUser(userName);
        datasource.setPassword(password);

        // Important: do not forget to register special data types
        @SuppressWarnings("rawtypes")
        Map<Class, Converter> converters = new HashMap<>();
        converters.put(UUID.class, new UUIDConverter());
        converters.put(LocalDate.class, new LocalDateConverter());
        converters.put(PriceCategory.class, new PriceCategoryConverter());

        sql2o = new Sql2o(datasource, new PostgresQuirks(converters));
    }

    /**
     * Retrieve the data source used in this service.
     * @return the data source of this service.
     */
    public DataSource getDataSource() {
        return datasource;
    }

    /**
     * note: getResourceAsStream works with classpath
     * https://stackoverflow.com/questions/18053059/getresourceasstream-is-returning-null-properties-file-is-not-loading
     * 
     * @param configFile
     * @return
     * @throws IOException
     */
    private Properties readDbConfig(String configFile) throws IOException {
        Properties prop = new Properties();
        InputStream input = getClass().getResourceAsStream(configFile);
        prop.load(input);
        return prop;
    }

    private MovieDAO getMovieDAO() {
        return new MovieDAO(sql2o);
    }

    private UserDAO getUserDAO() {
        return new UserDAO(sql2o);
    }

    private RentalDAO getRentalDAO() {
        return new RentalDAO(sql2o);
    }

    @Override
    public Movie createMovie(String aTitle, LocalDate aReleaseDate, String aPriceCategory, int anAgeRating) {
        try {
            PriceCategory pc = PriceCategory.getPriceCategoryFromId(aPriceCategory);
            Movie m = new Movie(aTitle, aReleaseDate, pc, anAgeRating);
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
    public Movie getMovieById(UUID id) {
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
    public boolean deleteMovie(UUID id) {
        try {
            getMovieDAO().delete(id);
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
    public User getUserById(UUID id) {
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
    public boolean deleteUser(UUID id) {
        try {
            getUserDAO().delete(id);
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
    public Rental createRental(UUID userId, UUID movieId, LocalDate d) {
        // TO-DO: transaction is missing
        User u = getUserDAO().getById(userId);
        Movie m = getMovieDAO().getById(movieId);

        if (u != null && m != null && !m.isRented() && !d.isAfter(LocalDate.now())) {
            UUID rentalId = getRentalDAO().create(userId, movieId, d);
            m.setRented(true);
            getMovieDAO().saveOrUpdate(m);
            return getRentalDAO().getById(rentalId);
        }
        return null;
    }

    @Override
    public boolean deleteRental(UUID id) {
        RentalDAO rdao = getRentalDAO();
        Rental r = rdao.getById(id);
        Movie m = r.getMovie();
        m.setRented(false);
        getMovieDAO().saveOrUpdate(m);
        rdao.delete(id);
        return r != null;
    }

    @Override
    public void createDB() {
        try (Connection conn = datasource.getConnection()) {
            Statement statement = conn.createStatement();
            statement.execute(CREATE_MOVIES_TABLE);
            statement.execute(CREATE_USERS_TABLE);
            statement.execute(CREATE_REANTALS_TABLE);
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    @Override
    public void removeDB() {
        try (Connection conn = datasource.getConnection()) {
            Statement statement = conn.createStatement();
            statement.execute(DROP_RENTALS_TABLE);
            statement.execute(DROP_MOVIES_TABLE);
            statement.execute(DROP_USERS_TABLE);
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    private static final String CREATE_MOVIES_TABLE = "CREATE TABLE IF NOT EXISTS movies ( " + "MovieId uuid NOT NULL, "
            + "Title text NOT NULL, " + "Rented boolean NOT NULL, " + "ReleaseDate date NOT NULL, "
            + "PriceCategory text NOT NULL, " + "AgeRating integer NOT NULL, "
            + "CONSTRAINT movies_pkey PRIMARY KEY (MovieId)" + ");";
    private static final String CREATE_USERS_TABLE = "CREATE TABLE IF NOT EXISTS users ( " + "UserId uuid NOT NULL, "
            + "Name text NOT NULL, " + "FirstName text NOT NULL, " + "Birthdate date NOT NULL, "
            + "CONSTRAINT users_pkey PRIMARY KEY (UserId) " + ");";
    private static final String CREATE_REANTALS_TABLE = "CREATE TABLE IF NOT EXISTS rentals ( "
            + "RentalId uuid NOT NULL, " + "MovieId uuid NOT NULL, " + "UserId uuid NOT NULL, "
            + "RentalDate date NOT NULL, " + "CONSTRAINT rentals_pkey PRIMARY KEY (RentalId), "
            + "CONSTRAINT NoDuplicateRentals UNIQUE (MovieId, UserId), " + "CONSTRAINT movieFK FOREIGN KEY (MovieId) "
            + "    REFERENCES movies (MovieId) MATCH SIMPLE " + "    ON UPDATE NO ACTION " + "    ON DELETE NO ACTION, "
            + "CONSTRAINT userFK FOREIGN KEY (UserId) " + "    REFERENCES users (UserId) MATCH SIMPLE "
            + "    ON UPDATE NO ACTION " + "    ON DELETE NO ACTION " + ");";
    private static final String DROP_MOVIES_TABLE = "DROP TABLE movies";
    private static final String DROP_USERS_TABLE = "DROP TABLE users";
    private static final String DROP_RENTALS_TABLE = "DROP TABLE rentals";
}
