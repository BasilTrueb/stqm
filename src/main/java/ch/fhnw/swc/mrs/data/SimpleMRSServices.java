package ch.fhnw.swc.mrs.data;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import ch.fhnw.swc.mrs.api.MRSServices;
import ch.fhnw.swc.mrs.model.Movie;
import ch.fhnw.swc.mrs.model.Rental;
import ch.fhnw.swc.mrs.model.User;

/**
 * A simple implementation of the MRS Services.
 */
public class SimpleMRSServices implements MRSServices {

    private static long nextid = 100;

    private Map<Long, Movie> movies = new HashMap<>();
    private Map<Long, User> users = new HashMap<>();
    private Map<Long, Rental> rentals = new HashMap<>();

    @Override
    public Movie createMovie(String aTitle, LocalDate aReleaseDate, int anAgeRating) {
        try {
            Movie m = new Movie(aTitle, aReleaseDate, anAgeRating);
            long id = nextid++;
            setId(m, id);
            movies.put(id, m);
            return m;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Collection<Movie> getAllMovies() {
        return movies.values();
    }

    @Override
    public Collection<Movie> getAllMovies(boolean rented) {
        Collection<Movie> result = new ArrayList<>();
        for (Movie m : movies.values()) {
            if (rented == m.isRented()) {
                result.add(m);
            }
        }
        return result;
    }

    @Override
    public Movie getMovieById(long id) {
        return movies.get(id);
    }

    @Override
    public boolean updateMovie(Movie movie) {
        movies.put(movie.getMovieid(), movie);
        return true;
    }

    @Override
    public boolean deleteMovie(long movieid) {
        return movies.remove(movieid) != null;
    }

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @Override
    public User getUserById(long id) {
        return users.get(id);
    }

    @Override
    public User getUserByName(String name) {
        for (User u : users.values()) {
            if (u.getName().equals(name)) {
                return u;
            }
        }
        return null;
    }

    @Override
    public User createUser(String aName, String aFirstName, LocalDate aBirthdate) {
        try {
            User u = new User(aName, aFirstName, aBirthdate);
            long id = nextid++;
            setId(u, id);
            users.put(id, u);
            return u;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean updateUser(User u) {
        User user = u;
        users.put(user.getUserid(), user);
        return true;
    }

    @Override
    public boolean deleteUser(long userid) {
        return users.remove(userid) != null;
    }

    @Override
    public Collection<Rental> getAllRentals() {
        return rentals.values();
    }

    @Override
    public Rental createRental(long userId, long movieId, LocalDate rentalDate) {
        User u = users.get(userId);
        Movie m = movies.get(movieId);

        if (u != null && m != null && !m.isRented() && !rentalDate.isAfter(LocalDate.now())) {
            try {
                Rental r = new Rental(u, m, rentalDate);
                long id = nextid++;
                setId(r, id);
                rentals.put(id, r);
                return r;
            } catch (Exception e) {
                return null;
            }
        }
        return null;   
    }
    
    @Override
    public boolean deleteRental(long rentalid) {
        Rental r = rentals.get(rentalid);
        r.getMovie().setRented(false);
        boolean result = r.getUser().removeRental(r);
        rentals.remove(rentalid);
        return result;
    }
    
    /**
     * Initialize the "server component".
     */
    @Override
    public void createDB() {
        readMovies();
        readUsers();
        readRentals();
    }

    @Override
    public void removeDB() {
        movies.clear();
        users.clear();
        rentals.clear();
    }

    private void readMovies() {
        InputStream instream = getClass().getResourceAsStream("/data/movies.csv");
        try (Reader in = new InputStreamReader(instream, StandardCharsets.UTF_8)) {
            
            CSVFormat format = initCSVFormat(MovieHeaders.class);

            Iterable<CSVRecord> movieList = format.parse(in);            
            
            for (CSVRecord m : movieList) {
                long id = Long.parseLong(m.get(MovieHeaders.ID));
                String title = m.get(MovieHeaders.Title);
                LocalDate releaseDate = LocalDate.parse(m.get(MovieHeaders.ReleaseDate));
                int ageRating = Integer.parseInt(m.get(MovieHeaders.AgeRating));
                Movie movie = new Movie(title, releaseDate, ageRating);
                setId(movie, id);
                movies.put(id, movie);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readUsers() {
        InputStream instream = getClass().getResourceAsStream("/data/users.csv");
        try (Reader in = new InputStreamReader(instream, StandardCharsets.UTF_8)) {
            
            CSVFormat format = initCSVFormat(UserHeaders.class);

            Iterable<CSVRecord> usersList = format.parse(in);            
            
            for (CSVRecord u : usersList) {
                long id = Long.parseLong(u.get(UserHeaders.ID));
                String surname = u.get(UserHeaders.Surname);
                String firstname = u.get(UserHeaders.FirstName);
                LocalDate birthdate = LocalDate.parse(u.get(UserHeaders.Birthdate));
                User user = new User(surname, firstname, birthdate);
                setId(user, id);
                users.put(id, user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readRentals() {
        InputStream instream = getClass().getResourceAsStream("/data/rentals.csv");
        try (Reader in = new InputStreamReader(instream, StandardCharsets.UTF_8)) {
            
            CSVFormat format = initCSVFormat(RentalHeaders.class);

            Iterable<CSVRecord> rentalsList = format.parse(in);            
            
            for (CSVRecord r : rentalsList) {
                long id = Long.parseLong(r.get(RentalHeaders.ID));
                LocalDate rentaldate = LocalDate.parse(r.get(RentalHeaders.RentalDate));
                long userId = Long.parseLong(r.get(RentalHeaders.UserID));
                long movieId = Long.parseLong(r.get(RentalHeaders.MovieID));
                User u = users.get(userId);
                Movie m = movies.get(movieId);
                Rental rental = new Rental(u, m, rentaldate);
                setId(rental, id);
                rentals.put(id, rental);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * init the Excel Formatter to read the data from the cvs file 
     * @param headerClass the specification of the heder column provided as a class
     * @return
     */
    private CSVFormat initCSVFormat(Class<? extends Enum<?>> headerClass) {
        CSVFormat format = CSVFormat.Builder.create(CSVFormat.EXCEL)
                .setHeader(headerClass)
                .setDelimiter(';')
                .setSkipHeaderRecord(true)
                .build();
        return format;
    }
    
    enum MovieHeaders {
        ID, Title, ReleaseDate, AgeRating
    }

    enum UserHeaders {
        ID, Surname, FirstName, Birthdate
    }

    enum RentalHeaders {
        ID, RentalDate, UserID, MovieID
    }

    private void setId(Object o, long id) throws Exception {
        Field f = o.getClass().getDeclaredField("id");
        f.setAccessible(true);
        f.setLong(o, id);
    }

}
