package ch.fhnw.swc.mrs;

import ch.fhnw.swc.mrs.data.DbMRSServices;
import ch.fhnw.swc.mrs.model.Movie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SystemTest {
    private DbMRSServices backend;

    @BeforeEach
    public void setup() {
        backend = new DbMRSServices("MRS.Test");

    }

    @AfterEach
    public void cleanup() {
        // Clean up the database after each test
    }

    @Test
    public void testCreateMovie() {
        Movie movie = backend.createMovie("Test Movie", LocalDate.now(), 12);
        assertNotNull(movie, "Expected movie to be created");
    }

    @Test
    public void testGetAllMovies() {
        backend.createMovie("Test Movie 1", LocalDate.now(), 12);
        backend.createMovie("Test Movie 2", LocalDate.now(), 15);
        List<Movie> movies = backend.getAllMovies();
        assertEquals(2, movies.size(), "Expected two movies to be retrieved");
    }

    @Test
    public void testGetMovieById() {
        Movie movie = backend.createMovie("Test Movie", LocalDate.now(), 12);
        Movie retrievedMovie = backend.getMovieById(movie.getMovieid());
        assertEquals(movie, retrievedMovie, "Expected retrieved movie to match created movie");
    }

    @Test
    public void testUpdateMovie() {
        Movie movie = backend.createMovie("Test Movie", LocalDate.now(), 12);
        movie.setTitle("Updated Title");
        boolean result = backend.updateMovie(movie);
        assertTrue(result, "Expected movie to be updated");
        Movie updatedMovie = backend.getMovieById(movie.getMovieid());
        assertEquals("Updated Title", updatedMovie.getTitle(), "Expected movie title to be updated");
    }

    @Test
    public void testDeleteMovie() {
        Movie movie = backend.createMovie("Test Movie", LocalDate.now(), 12);
        boolean result = backend.deleteMovie(movie.getMovieid());
        assertTrue(result, "Expected movie to be deleted");
        Movie deletedMovie = backend.getMovieById(movie.getMovieid());
        assertNull(deletedMovie, "Expected movie to be null after deletion");
    }
}
