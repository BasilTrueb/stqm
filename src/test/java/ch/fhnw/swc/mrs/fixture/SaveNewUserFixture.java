package ch.fhnw.swc.mrs.fixture;

import ch.fhnw.swc.mrs.data.SimpleMRSServices;
import fit.ActionFixture;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class SaveNewUserFixture extends ActionFixture {

    private SimpleMRSServices mrsServices = new SimpleMRSServices();

    private String surname;
    private String firstname;
    private LocalDate birthdate;

    public SaveNewUserFixture() throws Exception {

        // create connection to service layer
        mrsServices.createDB();
    }

    public void surname(String surname) {
        this.surname = surname;
    }

    public void firstName(String firstname) {
        this.firstname = firstname;
    }

    public void birthdate(String birthdate) {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        this.birthdate = LocalDate.parse(birthdate, dtf);
    }

    public void save() {
        mrsServices.createUser(surname, firstname, birthdate);
    }
    /**
     * Counts the entries in the table.
     * @return how many entries are saved
     */
    public int countUsers() {
        return mrsServices.getAllUsers().size();
    }

    public boolean userExists() {
        return mrsServices.getAllUsers().stream()
                .anyMatch(u -> (u.getFirstName() + " " + u.getName()).equals("Livio Jäckle"));
    }
}

