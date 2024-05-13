package ch.fhnw.swc.mrs.data;

import ch.fhnw.swc.mrs.model.Movie;
import org.assertj.db.type.Changes;
import org.assertj.db.type.Source;
import org.assertj.db.type.Table;
import org.hsqldb.jdbc.JDBCDataSourceFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;
import java.util.Properties;

import static org.assertj.db.api.Assertions.assertThat;
import static org.assertj.db.output.Outputs.output;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("integration")
public class ITMovieDao extends AbstractITDao {

    private MovieDAO dao;

    private Movie juno, matrix, rambo;

    private Source src;

    private Properties props = new Properties();

    ITMovieDao() {
        props.setProperty("url", "jdbc:hsqldb:mem:mrs");
        props.setProperty("user", "sa");
        props.setProperty("password", "");
        props.setProperty("jdbc.driver", "org.hsqldb.jdbcDriver");
    }

    @BeforeEach
    void setUp() throws Exception {
        dao = new MovieDAO(getEMF().createEntityManager());
        juno = new Movie("Titanic", LocalDate.of(2007,12,23),0);
        matrix = new Movie("Matrix", LocalDate.of(1997,3,11), 12);
        rambo = new Movie("Rambo", LocalDate.of(2008,1,25), 14);

        dao.saveOrUpdate(juno);
        dao.saveOrUpdate(matrix);
        dao.saveOrUpdate(rambo);

        src = new Source("jdbc:hsqldb:mem:mrs", "sa", "");
    }

    @AfterEach
    void tearDown() throws Exception {
        DataSource ds = JDBCDataSourceFactory.createDataSource(props);
        Connection conn = ds.getConnection();
        conn.createStatement().executeUpdate("delete from rentals");
        conn.createStatement().executeUpdate("delete from users");
        conn.createStatement().executeUpdate("delete from movies");
        conn.close();
    }

    @Test
    void testInsert() throws Exception {
        Table table = new Table(src, "movies", new Table.Order[]{Table.Order.asc("title")});
        assertThat(table).hasNumberOfRows(3);

        assertThat(table).column("title")
                .value().isEqualTo("Matrix")
                .value().isEqualTo("Rambo")
                .value().isEqualTo("Titanic");

        assertThat(table).column("releasedate")
                .value().isEqualTo(LocalDate.of(1997,3,11))
                .value().isEqualTo(LocalDate.of(2008,1,25))
                .value().isEqualTo(LocalDate.of(2007,12,23));

        assertThat(table).column("agerating")
                .value().isEqualTo(12)
                .value().isEqualTo(14)
                .value().isEqualTo(0);

        output(table).toConsole();
    }

    @Test
    void testDeleteNoneExisting() throws Exception {
        Movie movie = new Movie("Hello", LocalDate.now(),12);

        Changes changes = new Changes(src);
        changes.setStartPointNow();
        dao.delete(movie);
        changes.setEndPointNow();

        assertThat(changes).hasNumberOfChanges(0);
    }

    @Test
    void testDelete() throws Exception {
        List<Movie> movies = dao.getByTitle("Matrix");

        Changes changes = new Changes(src);

        changes.setStartPointNow();
        dao.delete(movies.get(0));
        changes.setEndPointNow();

        assertThat(changes).hasNumberOfChanges(1);
        assertThat(changes).change()
                .changeOfDeletionOnTable("MOVIES")
                .rowAtStartPoint().value("title").isEqualTo("Matrix")
                .rowAtEndPoint().doesNotExist();
    }

    @Test
    void testGetByName() throws Exception {
        List<Movie> movies = dao.getByTitle("Matrix");
        assertEquals("Matrix", movies.getFirst().getTitle());
    }

    @Test
    void testGetById() throws Exception {
        List<Movie> movies = dao.getByTitle("Matrix");
        assertEquals(1, movies.size());

        Movie movie = dao.getById(movies.getFirst().getMovieid());
        assertEquals(movie, movies.getFirst());
    }

    @Test
    void testAll() throws Exception {
        List<Movie> movies = dao.getAll();
        assertEquals(3, movies.size());
    }

    @Test
    void testUpdate() {

        List<Movie> movies = dao.getByTitle("Matrix");
        assertEquals(1, movies.size());

        Movie matrix = movies.getFirst();

        matrix.setTitle("Matrix 3");
        matrix.setReleaseDate(LocalDate.of(2020, 3, 3));
        matrix.setAgeRating(16);


        Changes changes = new Changes(src);
        changes.setStartPointNow();
        dao.saveOrUpdate(matrix);
        changes.setEndPointNow();

        assertThat(changes).hasNumberOfChanges(1);

        assertThat(changes).change()
                .changeOfModificationOnTable("MOVIES")
                .rowAtStartPoint().value("title").isEqualTo("Matrix")
                .rowAtEndPoint().value("title").isEqualTo("Matrix 3")
                .rowAtStartPoint().value("releasedate").isEqualTo(LocalDate.of(1997,3,11))  // Check initial release date
                .rowAtEndPoint().value("releasedate").isEqualTo(LocalDate.of(2020,3,3))
                .rowAtStartPoint().value("agerating").isEqualTo(12)
                .rowAtEndPoint().value("agerating").isEqualTo(16);
    }
}

