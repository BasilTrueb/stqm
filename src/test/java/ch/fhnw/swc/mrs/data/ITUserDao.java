package ch.fhnw.swc.mrs.data;

import static ch.fhnw.swc.mrs.data.IsBeforeDate.isBefore;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.sql.DataSource;

import org.dbunit.Assertion;
import org.dbunit.VerifyTableDefinition;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DefaultTable;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.NoSuchColumnException;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2o;
import org.sql2o.converters.Converter;
import org.sql2o.converters.UUIDConverter;
import org.sql2o.quirks.PostgresQuirks;

import com.opentable.db.postgres.embedded.EmbeddedPostgres;

import ch.fhnw.swc.mrs.model.PriceCategoryLoader;
import ch.fhnw.swc.mrs.model.User;
import ch.fhnw.swc.mrs.util.LocalDateConverter;

@Tag("integration")
public class ITUserDao {

    /** Class under test: UserDAO. */
    private UserDAO dao;
    private Connection connection;
    private DataSource ds;
    private static EmbeddedPostgres pg;
    private static boolean firstTest = true;

    private static VerifyTableDefinition vtd;
    @SuppressWarnings("rawtypes")
    private static Map<Class, Converter> converters = new HashMap<>();

    private static final String COUNT_SQL = "SELECT COUNT(*) FROM users";

    // the connection string to the database
    private Sql2o sql2o;

    @BeforeAll
    public static void startPostgresql() throws Exception {
        pg = EmbeddedPostgres.start();
        PriceCategoryLoader.load();
        vtd = new VerifyTableDefinition("users", new String[] {"userid"});
        converters.put(UUID.class, new UUIDConverter());
        converters.put(LocalDate.class, new LocalDateConverter());
    }

    /**
     * Initialize a DBUnit DatabaseTester object to use in tests.
     * 
     * @throws Exception whenever something goes wrong.
     */
    @BeforeEach
    public void setUp() throws Exception {
        ds = pg.getPostgresDatabase();
        connection = ds.getConnection();

        // count no. of rows before deletion
        if (!firstTest) {
            dropDB(connection);
        }
        createDB(connection);

        sql2o = new Sql2o(ds, new PostgresQuirks(converters));
        dao = new UserDAO(sql2o);
    }

    @AfterEach
    public void tearDown() throws Exception {
        dropDB(connection);
    }

    @Test
    public void testDeleteNonexistingWithoutDbUnit() throws Exception {
        insertData(connection);

        Statement s = connection.createStatement();
        ResultSet r = s.executeQuery(COUNT_SQL);
        r.next();
        int rows = r.getInt(1);
        assertEquals(3, rows);

        // Delete non-existing record
        User user = new User("Denzler", "Christoph", LocalDate.now());
        UUID uid = UUID.randomUUID();
        user.setUserid(uid);
        dao.delete(uid);

        r = s.executeQuery(COUNT_SQL);
        r.next();
        rows = r.getInt(1);
        assertEquals(3, rows);
    }

    @Test
    public void testDeleteWithoutDbUnit() throws Exception {
        insertData(connection);

        Statement s = connection.createStatement();
        ResultSet r = s.executeQuery(COUNT_SQL);
        r.next();
        int rows = r.getInt(1);
        assertEquals(3, rows);

        // delete existing record
        UUID did = UUID.fromString("20000000-0000-0000-0000-000000000001");
        User user = new User("Duck", "Donald", LocalDate.of(2013, 1, 13));
        user.setUserid(did);
        dao.delete(did);

        r = s.executeQuery(COUNT_SQL);
        r.next();
        rows = r.getInt(1);
        assertEquals(2, rows);
    }

    @Test
    public void testDeleteNonexisting() throws Exception {
        MRSPrepAndExpectedTestCase tc = new MRSPrepAndExpectedTestCase(ds, vtd);
        tc.runtest("UserDaoTestData.xml", "UserDaoTestData.xml", () -> {
            // Delete non-existing record
            User user = new User("Denzler", "Christoph", LocalDate.now());
            UUID uid = UUID.randomUUID();
            user.setUserid(uid);
            dao.delete(uid);

            return null;
        });
    }

    /**
     * Delete an existing user.
     * 
     * @throws Exception when anything goes wrong.
     */
    @Test
    public void testDelete() throws Exception {
        MRSPrepAndExpectedTestCase tc = new MRSPrepAndExpectedTestCase(ds, vtd);
        tc.runtest("UserDaoTestData.xml", "UserDaoDeleteResult.xml", () -> {
            // delete existing record
            UUID did = UUID.fromString("20000000-0000-0000-0000-000000000001");
            User user = new User("Duck", "Donald", LocalDate.of(2013, 1, 13));
            user.setUserid(did);
            dao.delete(did);

            return null;
        });
    }

    @Test
    public void testGetById() throws Exception {
        UUID uid = UUID.fromString("20000000-0000-0000-0000-000000000002");
        MRSPrepAndExpectedTestCase tc = new MRSPrepAndExpectedTestCase(ds, vtd);
        Object o = tc.runtest("UserDaoTestData.xml", "UserDaoTestData.xml", () -> {
            return dao.getById(uid); // Get by id
        });

        // verify the read data
        if (o instanceof User) {
            User user = (User) o;
            assertEquals("Micky", user.getFirstName());
            assertEquals("Mouse", user.getName());
            assertEquals(uid, user.getUserid());
        } else {
            fail("didn't receive a user object");
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetByName() throws Exception {
        MRSPrepAndExpectedTestCase tc = new MRSPrepAndExpectedTestCase(ds, vtd);
        Object o = tc.runtest("UserDaoTestData.xml", "UserDaoTestData.xml", () -> {
            // Get by name
            return dao.getByName("Duck");
        });
        if (o instanceof List<?>) {
            List<User> userlist = (List<User>) o;
            assertEquals(2, userlist.size());

            ITable actualTable = convertToTable(userlist);

            InputStream stream = this.getClass().getResourceAsStream("UserDaoGetByNameResult.xml");
            IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(stream);
            ITable expectedTable = expectedDataSet.getTable("USERS");

            Assertion.assertEquals(expectedTable, actualTable);
        }
    }

    /**
     * See if we get all users.
     * 
     * @throws Exception when anything goes wrong.
     */
    @Test
    public void testGetAll() throws Exception {
        MRSPrepAndExpectedTestCase tc = new MRSPrepAndExpectedTestCase(ds, vtd);
        @SuppressWarnings("unchecked")
        List<User> userlist = (List<User>) tc.runtest("UserDaoTestData.xml", "UserDaoTestData.xml", () -> {
            return dao.getAll();
        });

        ITable actualTable = convertToTable(userlist);

        InputStream stream = this.getClass().getResourceAsStream("UserDaoTestData.xml");
        IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(stream);
        ITable expectedTable = expectedDataSet.getTable("USERS");

        Assertion.assertEquals(expectedTable, actualTable);
    }

    @Test
    public void testSave() throws Exception {
        MRSPrepAndExpectedTestCase tc = new MRSPrepAndExpectedTestCase(ds, vtd);
        tc.runtest("UserDaoTestData.xml", "UserDaoInsertResult.xml", () -> {
            // insert new user
            User goofy = new User("Goofy", "Goofus", LocalDate.of(1936, 10, 12));
            dao.saveOrUpdate(goofy);

            return null;
        });
    }

    @Test
    public void testUpdate() throws Exception {
        MRSPrepAndExpectedTestCase tc = new MRSPrepAndExpectedTestCase(ds, vtd);

        tc.runtest("UserDaoTestData.xml", "UserDaoUpdateResult.xml", () -> {
            // update existing user
            UUID did = UUID.fromString("20000000-0000-0000-0000-000000000001");
            User daisy = new User("Duck", "Daisy", LocalDate.of(2013, 01, 13));
            daisy.setUserid(did);
            daisy.setFirstName("Daisy");
            dao.saveOrUpdate(daisy);
            return null;
        });
    }

    private void createDB(Connection c) throws SQLException {
        Statement s = c.createStatement();
        s.execute("CREATE TABLE public.users\n" + "(\n" + "    \"userid\" uuid NOT NULL,\n"
                + "    \"name\" text COLLATE pg_catalog.\"default\" NOT NULL,\n"
                + "    \"firstname\" text COLLATE pg_catalog.\"default\" NOT NULL,\n"
                + "    \"birthdate\" date NOT NULL,\n" + "    CONSTRAINT users_pkey PRIMARY KEY (\"userid\")\n" + ")");
    }

    private void dropDB(Connection c) throws SQLException {
        Statement s = c.createStatement();
        s.execute("DROP TABLE public.users");
    }

    private void insertData(Connection c) throws SQLException {
        Statement s = c.createStatement();
        s.execute("INSERT INTO public.users (userid, firstname, name, birthdate)"
                + "VALUES('20000000-0000-0000-0000-000000000001', 'Donald', 'Duck', '2013-01-13')");
        s.execute("INSERT INTO public.users (userid, firstname, name, birthdate)"
                + "VALUES('20000000-0000-0000-0000-000000000002', 'Micky', 'Mouse', '1935-11-03')");
        s.execute("INSERT INTO public.users (userid, firstname, name, birthdate)"
                + "VALUES('20000000-0000-0000-0000-000000000009', 'Donald', 'Duck', '1945-09-09')");
    }

    @SuppressWarnings("deprecation")
    private ITable convertToTable(List<User> userlist) throws Exception {
        ITableMetaData meta = new TableMetaData();
        DefaultTable t = new DefaultTable(meta);
        int row = 0;
        for (User u : userlist) {
            t.addRow();
            LocalDate d = u.getBirthdate();
            t.setValue(row, "userid", u.getUserid());
            t.setValue(row, "name", u.getName());
            t.setValue(row, "firstname", u.getFirstName());
            t.setValue(row, "birthdate", new Date(d.getYear() - 1900, d.getMonthValue() - 1, d.getDayOfMonth()));
            row++;
        }
        return t;
    }

    private static final class TableMetaData implements ITableMetaData {

        private List<Column> cols = new ArrayList<>();

        TableMetaData() {
            cols.add(new Column("userid", DataType.UNKNOWN));
            cols.add(new Column("name", DataType.VARCHAR));
            cols.add(new Column("firstname", DataType.VARCHAR));
            cols.add(new Column("birthdate", DataType.DATE));
        }

        @Override
        public int getColumnIndex(String colname) throws DataSetException {
            int index = 0;
            for (Column c : cols) {
                if (c.getColumnName().equals(colname.toLowerCase())) {
                    return index;
                }
                index++;
            }
            throw new NoSuchColumnException(getTableName(), colname);
        }

        @Override
        public Column[] getColumns() throws DataSetException {
            return cols.toArray(new Column[4]);
        }

        @Override
        public Column[] getPrimaryKeys() throws DataSetException {
            Column[] cols = new Column[1];
            cols[0] = new Column("userid", DataType.UNKNOWN);
            return cols;
        }

        @Override
        public String getTableName() {
            return "users";
        }
    }

    @SuppressWarnings("unchecked")
    @DisplayName("test that all rad users from DB are not null")
    @Test
    public void testVerifyAllUsersAreNotNull() throws Exception {
        MRSPrepAndExpectedTestCase tc = new MRSPrepAndExpectedTestCase(ds, vtd);
        List<User> userList = (List<User>) tc.runtest("UserDaoTestData.xml", "UserDaoTestData.xml", () -> {
            return dao.getAll();
        });

        assertThat(userList, not(hasItems(nullValue())));

    }

    @SuppressWarnings("unchecked")
    @DisplayName("test that all read users have valid birthdate")
    @Test
    public void testAllUsersHaveValidBirthdate() throws Exception {
        MRSPrepAndExpectedTestCase tc = new MRSPrepAndExpectedTestCase(ds, vtd);
        List<User> userList = (List<User>) tc.runtest("UserDaoTestData.xml", "UserDaoTestData.xml", () -> {
            return dao.getAll();
        });

        for (User u : userList) {
            assertThat(u.getBirthdate(), allOf(notNullValue(), isBefore(LocalDate.now())));
        }

    }

}
