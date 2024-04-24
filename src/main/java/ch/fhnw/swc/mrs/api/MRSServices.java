package ch.fhnw.swc.mrs.api;

import java.time.LocalDate;
import java.util.Collection;

import ch.fhnw.swc.mrs.model.Movie;
import ch.fhnw.swc.mrs.model.Rental;
import ch.fhnw.swc.mrs.model.User;

/**
 * The service interface for all services that the MRS offers.
 */
public interface MRSServices {
    /**
     * Create a new movie.
     * 
     * @param aTitle Title of the movie. Must not be null nor empty.
     * @param aReleaseDate Date when this movie was released. Must not be null.
     * @param anAgeRating How old a user must be at least to be allowed to rent this Movie. A value
     *            between [0, 18].
     * @return a Movie object initialized with given data.
     * @throws IllegalArgumentException in case, any of the parameters are null or title is empty.
     */
    Movie createMovie(String aTitle, LocalDate aReleaseDate, int anAgeRating);

    /**
     * Retrieve all Movies.
     * 
     * @return all Movies.
     */
    Collection<Movie> getAllMovies();

    /**
     * get all rented or available Movies.
     * 
     * @param rented whether the available or the rented Movies shall be retrieved.
     * @return all Movies that are either rented or not (depending on parameter rented)
     */
    Collection<Movie> getAllMovies(boolean rented);

    /**
     * @param id the identification of the Movie to retrieve.
     * @return get Movie by its ID,
     * 
     *         <pre>
     *         null
     *         </pre>
     * 
     *         if no movie found with given id.
     */
    Movie getMovieById(long id);

    /**
     * Update Movie with new data.
     * 
     * @param movie contains the new data.
     * @return whether the update operation was successful.
     */
    boolean updateMovie(Movie movie);

    /**
     * Delete Movie.
     * 
     * @param movieid Movie to delete.
     * @return whether the delete operation was successful.
     */
    boolean deleteMovie(long movieid);

    /**
     * Retrieve all Users.
     * 
     * @return all Users.
     */
    Collection<User> getAllUsers();

    /**
     * @param id the identification of the User to retrieve.
     * @return get User by its ID.
     */
    User getUserById(long id);

    /**
     * @param name retrieve first user found with given name.
     * @return User or null if not found.
     */
    User getUserByName(String name);

    /**
     * Create a new user with the given name information.
     * 
     * @param aName the user's family name.
     * @param aFirstName the user's first name.
     * @param aBirthdate the user's date of birth.
     * @return a User object initialized with given data.
     * @throws IllegalArgumentException The name must neither be <code>null</code>.
     * @throws MovieRentalException If the name is empty ("") or longer than 40 characters.
     */
    User createUser(String aName, String aFirstName, LocalDate aBirthdate);

    /**
     * Update User with new data.
     * 
     * @param u contains the new data.
     * @return whether the update operation was successful.
     */
    boolean updateUser(User u);

    /**
     * Delete User.
     * 
     * @param userid user to delete.
     * @return whether the delete operation was successful.
     */
    boolean deleteUser(long userid);

    /**
     * Retrieve all Rentals.
     * 
     * @return all Rentals.
     */
    Collection<Rental> getAllRentals();

    /**
     * Create a new Rental.
     * 
     * @param userId the id of the user who is renting a movie.
     * @param movieId the id of the rented movie.
     * @param rentalDate date the rental starts.
     * @return the created Rental object.
     */
    Rental createRental(long userId, long movieId, LocalDate rentalDate);

    /**
     * Delete the rental with the specified rental ID.
     * 
     * @param rentalid rental to delete.
     * @return whether the deletion was successful.
     */
    boolean deleteRental(long rentalid);

    /**
     * Creates database.
     */
    void createDB();

    /**
     * Removes database.
     */
    void removeDB();
}
