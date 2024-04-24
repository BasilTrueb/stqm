package ch.fhnw.swc.mrs.data;

import static org.assertj.db.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;
import java.util.Properties;
import javax.sql.DataSource;

import org.assertj.db.type.Changes;
import org.assertj.db.type.Source;
import org.assertj.db.type.Table;
import org.assertj.db.type.Table.Order;
import org.hsqldb.jdbc.JDBCDataSourceFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import ch.fhnw.swc.mrs.model.User;

@Tag("integration")
public class ITUserDao extends AbstractITDao {

    /** Class under test: UserDAO. */
    private UserDAO dao;
    
    private User donald, dagobert, mickey;

    private Source src;
    private Properties props = new Properties();
    
    ITUserDao() {
        props.setProperty("url", "jdbc:hsqldb:mem:mrs");
        props.setProperty("user", "sa");
        props.setProperty("password", "");
        props.setProperty("jdbc.driver", "org.hsqldb.jdbcDriver");
    }
    
    @BeforeEach
    void setUp() throws Exception {
        dao = new UserDAO(getEMF().createEntityManager());
        donald = new User("Duck", "Donald", LocalDate.of(2013, 01, 13));
        dagobert = new User("Duck", "Dagobert", LocalDate.of(1945, 9, 9));
        mickey = new User("Mouse", "Mickey", LocalDate.of(1935, 11, 3));
        
        dao.saveOrUpdate(donald);
        dao.saveOrUpdate(dagobert);
        dao.saveOrUpdate(mickey);
        
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
        Table table = new Table(src, "users", new Order[] {Order.asc("birthdate")});
        assertThat(table).hasNumberOfRows(3);
        
        assertThat(table).column("name")
            .value().isEqualTo("Mouse")
            .value().isEqualTo("Duck")
            .value().isEqualTo("Duck");
        
        assertThat(table).column("firstname")
            .value().isEqualTo("Mickey")
            .value().isEqualTo("Dagobert")
            .value().isEqualTo("Donald");
        
        assertThat(table).column("birthdate")
            .value().isEqualTo(LocalDate.of(1935, 11, 3))
            .value().isEqualTo(LocalDate.of(1945, 9, 9))
            .value().isEqualTo(LocalDate.of(2013, 01, 13));
    }

    @Test
    public void testDeleteNonexisting() throws Exception {
        User user = new User("Denzler", "Christoph", LocalDate.now());

        Changes changes = new Changes(src);
        changes.setStartPointNow();
        dao.delete(user);
        changes.setEndPointNow();
        
        assertThat(changes).hasNumberOfChanges(0);
    }

    /**
     * Delete an existing user.
     * 
     * @throws Exception when anything goes wrong.
     */
    @Test
    public void testDelete() throws Exception {
        List<User> users = dao.getByName("Mouse");
        assertEquals(1, users.size());
        
        Changes changes = new Changes(src);
        changes.setStartPointNow();
        dao.delete(users.get(0));
        changes.setEndPointNow();
        
        assertThat(changes).hasNumberOfChanges(1);
        assertThat(changes).change()
            .changeOfDeletionOnTable("USERS")
            .rowAtStartPoint().value("name").isEqualTo("Mouse")
            .rowAtEndPoint().doesNotExist();
    }

    @Test
    public void testGetByName() throws Exception {
        List<User> ducks = dao.getByName("Duck"); 
        assertEquals(2, ducks.size());
        
        for (User d: ducks) {
            assertEquals("Duck", d.getName());
            assertTrue("Dagobert".equals(d.getFirstName()) || "Donald".equals(d.getFirstName()));
        }
    }

    @Test
    public void testGetById() throws Exception {
        List<User> users = dao.getByName("Mouse");
        assertEquals(1, users.size());
        
        User mickey = users.get(0);
        long id = mickey.getUserid();
        
        User u = dao.getById(id);
        assertEquals(u, mickey); // ensures that attributes of Mickey are set correctly.
    }

    /**
     * See if we get all users.
     * 
     * @throws Exception when anything goes wrong.
     */
    @Test
    public void testGetAll() throws Exception {
        List<User> users = dao.getAll(); 
        assertEquals(3, users.size());
    }

    @Test
    public void testUpdate() throws Exception {
        List<User> users = dao.getByName("Mouse");
        assertEquals(1, users.size());
        User mickey = users.get(0);
        
        mickey.setName("Gufus");
        mickey.setFirstName("Goofy");
        mickey.setBirthdate(LocalDate.of(1934, 12, 31));
        
        Changes changes = new Changes(src);
        changes.setStartPointNow();
        dao.saveOrUpdate(mickey);
        changes.setEndPointNow();
        
        assertThat(changes).hasNumberOfChanges(1);
        assertThat(changes).change()
            .changeOfModificationOnTable("USERS")
            .rowAtStartPoint().value("name").isEqualTo("Mouse")
            .rowAtEndPoint().value("name").isEqualTo("Gufus")
            .rowAtStartPoint().value("firstname").isEqualTo("Mickey")
            .rowAtEndPoint().value("firstname").isEqualTo("Goofy")
            .rowAtStartPoint().value("birthdate").isEqualTo(LocalDate.of(1935, 11, 3))
            .rowAtEndPoint().value("birthdate").isEqualTo(LocalDate.of(1934, 12, 31));
    }

}
