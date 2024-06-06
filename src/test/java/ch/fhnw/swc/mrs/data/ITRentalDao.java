package ch.fhnw.swc.mrs.data;

import ch.fhnw.swc.mrs.model.Movie;
import ch.fhnw.swc.mrs.model.Rental;
import ch.fhnw.swc.mrs.model.User;
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
public class ITRentalDao extends AbstractITDao {

    private RentalDAO daoRental;

    private MovieDAO daoMovie;

    private UserDAO daoUser;

    private Rental rental1, rental2, rental3, rental4;

    private Movie juno, matrix, rambo, hello;

    private User donald, dagobert, mickey;

    private Source src;

    private Properties props = new Properties();

    ITRentalDao() {
        props.setProperty("url", "jdbc:hsqldb:mem:mrs");
        props.setProperty("user", "sa");
        props.setProperty("password", "");
        props.setProperty("jdbc.driver", "org.hsqldb.jdbcDriver");
    }

    @BeforeEach
    void setUp() throws Exception {
        daoRental = new RentalDAO(getEMF().createEntityManager());
        daoMovie = new MovieDAO(getEMF().createEntityManager());
        daoUser = new UserDAO(getEMF().createEntityManager());

        donald = new User("Duck", "Donald", LocalDate.of(2013, 01, 13));
        dagobert = new User("Duck", "Dagobert", LocalDate.of(1945, 9, 9));
        mickey = new User("Mouse", "Mickey", LocalDate.of(1935, 11, 3));

        juno = new Movie("Titanic", LocalDate.of(2007, 12, 23), 0);
        matrix = new Movie("Matrix", LocalDate.of(1997, 3,  11), 12);
        rambo = new Movie("Rambo", LocalDate.of(2008, 1, 25), 14);
        hello = new Movie("Hello", LocalDate.of(2010, 1, 25), 0);

        daoUser.saveOrUpdate(donald);
        daoUser.saveOrUpdate(dagobert);
        daoUser.saveOrUpdate(mickey);

        daoMovie.saveOrUpdate(juno);
        daoMovie.saveOrUpdate(matrix);
        daoMovie.saveOrUpdate(rambo);
        daoMovie.saveOrUpdate(hello);

        rental1 = new Rental(donald, juno, LocalDate.of(2020, 1, 1));
        rental2 = new Rental(dagobert, matrix, LocalDate.of(2020, 2, 1));
        rental3 = new Rental(mickey, rambo, LocalDate.of(2020, 3, 1));

        daoRental.save(rental1);
        daoRental.save(rental2);
        daoRental.save(rental3);

        rental4 = new Rental(donald, hello, LocalDate.of(2020, 4, 1));
        daoRental.save(rental4);

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
        Table table = new Table(src, "rentals");
        assertThat(table).hasNumberOfRows(4);

        assertThat(table).column("userid")
                .value().isEqualTo(rental1.getUser().getUserid())
                .value().isEqualTo(rental2.getUser().getUserid())
                .value().isEqualTo(rental3.getUser().getUserid())
                .value().isEqualTo(rental4.getUser().getUserid());

        assertThat(table).column(("movieid"))
                .value().isEqualTo(rental1.getMovie().getMovieid())
                .value().isEqualTo(rental2.getMovie().getMovieid())
                .value().isEqualTo(rental3.getMovie().getMovieid())
                .value().isEqualTo(rental4.getMovie().getMovieid());

        assertThat(table).column("rentaldate")
                .value().isEqualTo(rental1.getRentalDate())
                .value().isEqualTo(rental2.getRentalDate())
                .value().isEqualTo(rental3.getRentalDate())
                .value().isEqualTo(rental4.getRentalDate());

        output(table).toConsole();
    }

    @Test
    void testDeleteNoneExisting() throws Exception {

        User user = new User("Muster", "Max", LocalDate.of(1997,  1, 20));
        Movie movie = new Movie("Hello", LocalDate.now(), 12);
        Rental rental = new Rental(user, movie, LocalDate.now());

        Changes changes = new Changes(src);
        changes.setStartPointNow();
        daoRental.delete(rental);
        changes.setEndPointNow();

        assertThat(changes).hasNumberOfChanges(0);
    }

    @Test
    void testDelete() {
        List<Rental> rentals = daoRental.getAll();
        assertEquals(4, rentals.size());

        Changes changes = new Changes(src);

        changes.setStartPointNow();
        daoRental.delete(rentals.get(0));
        changes.setEndPointNow();

        assertThat(changes).hasNumberOfChanges(2);

        assertThat(changes).change()
                .changeOfDeletionOnTable("RENTALS")
                .rowAtStartPoint().value("rentalid").isEqualTo(rental1.getRentalId())
                .rowAtEndPoint().doesNotExist();
    }

    @Test
    void testGetById() {
        List<Rental> rentals = daoRental.getAll();
        assertEquals(4, rentals.size());

        Rental rental = rentals.get(0);
        long id = rental.getRentalId();

        Rental r = daoRental.getById(id);
        assertEquals(r, rental);
    }

    @Test
    void testGetByName() {
        List<Rental> rentals = daoRental.getAll();
        assertEquals(4, rentals.size());

        User donald = rentals.get(0).getUser();
        User dagobert = rentals.get(1).getUser();

        long donaldId = donald.getUserid();
        long dagobertId = dagobert.getUserid();

        List<Rental> rentalsByDonald = daoRental.getRentalsByUser(donaldId);
        List<Rental> rentalsByDagobert = daoRental.getRentalsByUser(dagobertId);

        assertEquals(2, rentalsByDonald.size());
        assertEquals(1, rentalsByDagobert.size());
    }
}
