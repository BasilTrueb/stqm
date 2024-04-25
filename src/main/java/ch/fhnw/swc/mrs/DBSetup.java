package ch.fhnw.swc.mrs;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.postgresql.ds.PGSimpleDataSource;

public final class DBSetup {

    public static void main(String[] args) {
        DBSetup setup = new DBSetup();

        if (args.length != 1) {
            System.out.println("exactly one argument needed");
            System.out.println("Usage: DBSetup [create|createfill|drop]");
        } else {
            setup.setup(args[0]);
        }
    }

    private void setup(String option) {
        // IMPROVE: Ugly! Never store credentials in source code
        String userName = "postgres";
        String password = "1234";
        String url = "jdbc:postgresql:stqm";

        PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setUrl(url);
        ds.setUser(userName);
        ds.setPassword(password);

        switch (option.toUpperCase()) {
        case "CREATE":
            createDB(ds);
            break;
        case "CREATEFILL":
            createDB(ds);
            fillDB(ds);
            break;
        case "DROP":
            removeDB(ds);
            break;
        default:
            System.out.println("Usage: DBSetup [create|createfill|drop]");
        }
    }

    private void createDB(PGSimpleDataSource datasource) {
        try (Connection conn = datasource.getConnection()) {
            Statement statement = conn.createStatement();
            statement.execute(CREATE_MOVIES_TABLE);
            statement.execute(CREATE_USERS_TABLE);
            statement.execute(CREATE_REANTALS_TABLE);
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    private void removeDB(PGSimpleDataSource datasource) {
        try (Connection conn = datasource.getConnection()) {
            Statement statement = conn.createStatement();
            statement.execute(DROP_RENTALS_TABLE);
            statement.execute(DROP_MOVIES_TABLE);
            statement.execute(DROP_USERS_TABLE);
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    private void fillDB(PGSimpleDataSource datasource) {
        fillMovies(datasource);
        fillUsers(datasource);
        fillRentals(datasource);
    }

    private void fillMovies(PGSimpleDataSource datasource) {
        InputStream instream = getClass().getResourceAsStream("/data/movies.csv");
        try (Reader in = new InputStreamReader(instream, StandardCharsets.UTF_8)) {

            CSVFormat format = initCSVFormat(MovieHeaders.class);

            Iterable<CSVRecord> movieList = format.parse(in);

            Connection con = datasource.getConnection();
            con.setAutoCommit(false);

            for (CSVRecord m : movieList) {
                long id = -Long.parseLong(m.get(MovieHeaders.ID));
                String title = m.get(MovieHeaders.Title);
                LocalDate releaseDate = LocalDate.parse(m.get(MovieHeaders.ReleaseDate));
                int ageRating = Integer.parseInt(m.get(MovieHeaders.AgeRating));

                try (PreparedStatement insert = con.prepareStatement(INSERT_MOVIE)) {
                    insert.setLong(1, id);
                    insert.setString(2, title);
                    insert.setBoolean(3, false);
                    insert.setDate(4, Date.valueOf(releaseDate));
                    insert.setInt(5, ageRating);
                    insert.executeUpdate();
                    con.commit();
                } catch (SQLException e) {
                    e.printStackTrace();
                    if (con != null) {
                        try {
                            System.err.print("Transaction is being rolled back");
                            con.rollback();
                        } catch (SQLException excep) {
                            excep.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fillUsers(PGSimpleDataSource datasource) {
        InputStream instream = getClass().getResourceAsStream("/data/users.csv");
        try (Reader in = new InputStreamReader(instream, StandardCharsets.UTF_8)) {

            CSVFormat format = initCSVFormat(UserHeaders.class);

            Iterable<CSVRecord> usersList = format.parse(in);

            Connection con = datasource.getConnection();
            con.setAutoCommit(false);

            for (CSVRecord u : usersList) {
                long id = -Long.parseLong(u.get(UserHeaders.ID));
                String surname = u.get(UserHeaders.Surname);
                String firstname = u.get(UserHeaders.FirstName);
                LocalDate birthdate = LocalDate.parse(u.get(UserHeaders.Birthdate));

                try (PreparedStatement insert = con.prepareStatement(INSERT_USER)) {
                    insert.setLong(1, id);
                    insert.setString(2, surname);
                    insert.setString(3, firstname);
                    insert.setDate(4, Date.valueOf(birthdate));
                    insert.executeUpdate();
                    con.commit();
                } catch (SQLException e) {
                    e.printStackTrace();
                    if (con != null) {
                        try {
                            System.err.print("Transaction is being rolled back");
                            con.rollback();
                        } catch (SQLException excep) {
                            excep.printStackTrace();
                        }
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fillRentals(PGSimpleDataSource datasource) {
        InputStream instream = getClass().getResourceAsStream("/data/rentals.csv");
        try (Reader in = new InputStreamReader(instream, StandardCharsets.UTF_8)) {

            CSVFormat format = initCSVFormat(RentalHeaders.class);

            Iterable<CSVRecord> rentalsList = format.parse(in);

            Connection con = datasource.getConnection();
            con.setAutoCommit(false);

            for (CSVRecord r : rentalsList) {
                long id = -Long.parseLong(r.get(RentalHeaders.ID));
                LocalDate rentaldate = LocalDate.parse(r.get(RentalHeaders.RentalDate));
                long userId = -Long.parseLong(r.get(RentalHeaders.UserID));
                long movieId = -Long.parseLong(r.get(RentalHeaders.MovieID));
                try (PreparedStatement insert = con.prepareStatement(INSERT_RENTAL)) {
                    insert.setLong(1, id);
                    insert.setLong(2, movieId);
                    insert.setLong(3, userId);
                    insert.setDate(4, Date.valueOf(rentaldate));
                    insert.executeUpdate();
                    con.commit();
                } catch (SQLException e) {
                    e.printStackTrace();
                    if (con != null) {
                        try {
                            System.err.print("Transaction is being rolled back");
                            con.rollback();
                        } catch (SQLException excep) {
                            excep.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * init the Excel Formatter to read the data from the cvs file
     * 
     * @param headerClass the specification of the heder column provided as a class
     * @return
     */
    private CSVFormat initCSVFormat(Class<? extends Enum<?>> headerClass) {
        CSVFormat format = CSVFormat.Builder.create(CSVFormat.EXCEL).setHeader(headerClass).setDelimiter(';')
                .setSkipHeaderRecord(true).build();
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

    private static final String CREATE_MOVIES_TABLE = "CREATE TABLE IF NOT EXISTS movies ( "
            + "MovieId integer NOT NULL, " 
            + "Title text NOT NULL, " 
            + "Rented boolean NOT NULL, "
            + "ReleaseDate date NOT NULL, " 
            + "AgeRating integer NOT NULL, "
            + "CONSTRAINT movies_pkey PRIMARY KEY (MovieId)" + ");";
    private static final String CREATE_USERS_TABLE = "CREATE TABLE IF NOT EXISTS users ( " 
            + "UserId integer NOT NULL, "
            + "Name text NOT NULL, " 
            + "FirstName text NOT NULL, " 
            + "Birthdate date NOT NULL, "
            + "CONSTRAINT users_pkey PRIMARY KEY (UserId) " + ");";
    private static final String CREATE_REANTALS_TABLE = "CREATE TABLE IF NOT EXISTS rentals ( "
            + "RentalId integer NOT NULL, " 
            + "MovieId integer NOT NULL, " 
            + "UserId integer NOT NULL, "
            + "RentalDate date NOT NULL, " 
            + "CONSTRAINT rentals_pkey PRIMARY KEY (RentalId), "
            + "CONSTRAINT NoDuplicateRentals UNIQUE (MovieId, UserId), " 
            + "CONSTRAINT movieFK FOREIGN KEY (MovieId) "
            + "    REFERENCES movies (MovieId) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION, "
            + "CONSTRAINT userFK FOREIGN KEY (UserId) REFERENCES users (UserId) MATCH SIMPLE "
            + "    ON UPDATE NO ACTION ON DELETE NO ACTION " + ");";

    private static final String DROP_MOVIES_TABLE = "DROP TABLE movies";
    private static final String DROP_USERS_TABLE = "DROP TABLE users";
    private static final String DROP_RENTALS_TABLE = "DROP TABLE rentals";

    private static final String INSERT_MOVIE = "INSERT INTO movies (MovieId, Title, Rented, ReleaseDate, AgeRating) "
            + "VALUES (?, ?, ?, ?, ?)";
    private static final String INSERT_USER = "INSERT INTO users (UserId, Name, FirstName, Birthdate) "
            + "VALUES (?, ?, ?, ?)";
    private static final String INSERT_RENTAL = "INSERT INTO rentals (RentalId, MovieId, UserId, RentalDate) "
            + "VALUES (?, ?, ?, ?)";
}
