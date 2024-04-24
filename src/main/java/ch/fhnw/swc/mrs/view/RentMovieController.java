package ch.fhnw.swc.mrs.view;

import java.time.LocalDate;
import java.util.Collection;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import ch.fhnw.swc.mrs.model.Movie;
import ch.fhnw.swc.mrs.model.User;

/** Controller class for rent movies tab. */
public class RentMovieController extends AbstractController {
    private User found = null;
    private RentMovieTab view;

    protected void setView(RentMovieTab view) {
        this.view = view;
    }

    @Override
    public void reload() {
        Collection<Movie> movies = getBackend().getAllMovies(false);
        DefaultTableModel tm = (DefaultTableModel) view.movieTable.getModel();

        tm.setRowCount(0);
        for (Movie m : movies) {
            tm.addRow(fillInMovieArrayElement(m));
        }

        clearAllFields();
        setReadyEnabling();

        view.newUser.setSelected(false);
    }

    private Object[] fillInMovieArrayElement(Movie movie) {
        Object[] result = new Object[3];
        result[0] = movie.getMovieid();
        result[1] = movie.getTitle();
        result[2] = RentMovieTab.SDF.format(movie.getReleaseDate());
        return result;
    }

    private void setReadyEnabling() {
        view.surname.setEditable(true);
        view.firstname.setEditable(false);
        view.birthdate.setEditable(false);
        view.rentalDate.setEditable(false);

        view.newUser.setEnabled(true);
        view.getUser.setEnabled(true);
        view.save.setEnabled(false);
        view.clearAll.setEnabled(false);
    }

    private void setNewUserEnabling() {
        view.surname.setEditable(true);
        view.firstname.setEditable(true);
        view.birthdate.setEditable(true);

        view.rentalDate.setEnabled(true);
        view.birthdate.setEnabled(true);
        view.newUser.setEnabled(true);
        view.getUser.setEnabled(false);
        view.save.setEnabled(true);
        view.clearAll.setEnabled(true);
    }

    private void clearAllFields() {
        String today = LocalDate.now().format(RentMovieTab.SDF);
        view.surname.setText(null);
        view.firstname.setText(null);
        view.rentalDate.setText(today);
        view.birthdate.setText(null);
    }

    private void setFoundUserState(User currUser) {
        String today = LocalDate.now().format(RentMovieTab.SDF);
        view.surname.setText(currUser.getName());
        view.firstname.setText(currUser.getFirstName());
        view.birthdate.setText(currUser.getBirthdate().format(RentMovieTab.SDF));
        view.rentalDate.setText(today);

        view.surname.setEditable(false);
        view.firstname.setEditable(false);
        view.newUser.setEnabled(false);
        view.save.setEnabled(true);
        view.clearAll.setEnabled(true);
    }

    void handleClearAll() {
        view.newUser.setSelected(false);
        setReadyEnabling();
        clearAllFields();
    }

    protected void handleNewUser() {
        clearAllFields();
        if (view.newUser.isSelected()) {
            setNewUserEnabling();
        } else {
            setReadyEnabling();
        }
        view.surname.requestFocus();
    }

    protected void handleGetUser() {
        found = null;
        String username = view.surname.getText();
        found = getBackend().getUserByName(username);
        if (found != null) {
            setFoundUserState(found);
        } else {
            JOptionPane.showMessageDialog(null, "No user found with given id or surname.");
            view.surname.requestFocus();
        }
    }

    protected void handleSave() {
        // get data from input fields
        String surName = view.surname.getText().trim();
        String firstName = view.firstname.getText().trim();
        String birthDate = view.birthdate.getText().trim();
        long userId = 1L;

        try {
            userId = ((view.newUser.isSelected()) ? createUser(surName, firstName, birthDate) : found).getUserid();

            // save rental in list
            int rowselection = view.movieTable.getSelectedRow();
            if (rowselection < 0) {
                JOptionPane.showMessageDialog(null, "Please select a movie to rent from the list.");
                return;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Names must not be empty or illegal date of birth");
            handleClearAll();
            return;
        }

        try {
            Object movieObjId = view.movieTable.getValueAt(view.movieTable.getSelectedRow(), 0);
            String movieStrId = movieObjId.toString();
            long movieId = Long.parseLong(movieStrId);
            getBackend().createRental(userId, movieId, LocalDate.now());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        getParent().mainPaneStateChanged(null);

        // reset application
        view.movieTable.requestFocus();

        handleClearAll();
    }

    private User createUser(String fn, String sn, String bd) {
        LocalDate dob = LocalDate.parse(bd, RentMovieTab.SDF);
        return getBackend().createUser(sn, fn, dob);
    }

}
