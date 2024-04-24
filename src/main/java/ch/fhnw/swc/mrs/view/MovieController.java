package ch.fhnw.swc.mrs.view;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collection;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import ch.fhnw.swc.mrs.model.Movie;

/**
 * Java FX controller class for Movies.
 */
public class MovieController extends AbstractController {
    private Movie editing = null; // currently no movie is being edited.
    private MovieTab view;

    protected void setView(MovieTab view) {
        this.view = view;
    }

    @Override
    public void reload() {
        Collection<Movie> movies = getBackend().getAllMovies();
        DefaultTableModel tm = (DefaultTableModel) view.movieTable.getModel();

        tm.setRowCount(0);
        for (Movie m : movies) {
            tm.addRow(fillInMovieArrayElement(m));
        }

    }

    private Object[] fillInMovieArrayElement(Movie movie) {
        Object[] result = new Object[5];
        result[0] = movie.getMovieid();
        result[1] = movie.getTitle();
        result[2] = RentMovieTab.SDF.format(movie.getReleaseDate());
        result[3] = movie.isRented();
        result[4] = movie.getAgeRating();
        return result;
    }

    private void showMovieDetails(Movie movie) {
        if (movie != null) {
            // fill the labels with info from the MovieVM object
            view.title.setText(movie.getTitle());
            view.releaseDate.setText(movie.getReleaseDate().format(MovieTab.SDF));
            view.ageRating.setSelectedItem(movie.getAgeRating());
        } else {
            // clear the content and set default values
            view.title.setText("");
            view.releaseDate.setText("");
            view.ageRating.setSelectedIndex(-1);
        }
    }

    protected void handleTableClicked(boolean tableContainsMovies) {
        if (tableContainsMovies) {
            view.deleteMovie.setEnabled(true);
            view.editMovie.setEnabled(true);
            view.newMovie.setEnabled(true);
            view.saveMovie.setEnabled(false);
            view.cancel.setEnabled(false);

            view.title.setEnabled(false);
            view.title.setText("");
            view.releaseDate.setEnabled(false);
            view.releaseDate.setText("");
        } else {
            handleCancel();
        }
    }

    protected void handleCancel() {
        view.cancel.setEnabled(false);
        view.newMovie.setEnabled(true);
        view.editMovie.setEnabled(false);
        view.deleteMovie.setEnabled(false);
        view.saveMovie.setEnabled(false);
        showMovieDetails(null);
        view.title.setEditable(false);
        view.releaseDate.setEditable(false);
        editing = null;
    }

    protected void handleNew() {
        view.cancel.setEnabled(true);
        view.newMovie.setEnabled(false);
        view.editMovie.setEnabled(false);
        view.deleteMovie.setEnabled(false);
        view.saveMovie.setEnabled(true);
        showMovieDetails(null);
        view.title.setEditable(true);
        view.releaseDate.setEditable(true);
        view.title.requestFocus();
        editing = null;
    }

    protected void handleSave() {
        String relDateText = view.releaseDate.getText();
        LocalDate relDate = text2Date(relDateText);
        if (editing == null) {
            Movie m = getBackend().createMovie(view.title.getText(), relDate,
                    (int) view.ageRating.getSelectedItem());
            DefaultTableModel tm = (DefaultTableModel) view.movieTable.getModel();
            tm.addRow(fillInMovieArrayElement(m));
        } else {
            editing.setTitle(view.title.getText());
            editing.setReleaseDate(relDate);
            editing.setAgeRating((int) view.ageRating.getSelectedItem());
            getBackend().updateMovie(editing);
        }
        handleCancel();
    }

    private LocalDate text2Date(String relDateText) {
        LocalDate date = LocalDate.now();
        try {
            date = LocalDate.parse(relDateText, MovieTab.SDF);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(null, "Illegal Date format. Please use yyyy-mm-dd");
        }
        return date;
    }

    protected void handleEdit() {
        view.cancel.setEnabled(true);
        view.newMovie.setEnabled(false);
        view.editMovie.setEnabled(false);
        view.deleteMovie.setEnabled(false);
        view.saveMovie.setEnabled(true);

        int row = view.movieTable.getSelectedRow();
        DefaultTableModel tm = (DefaultTableModel) view.movieTable.getModel();
        long id = (long) tm.getValueAt(row, 0);
        editing = getBackend().getMovieById(id);

        view.title.setText(editing.getTitle());
        view.releaseDate.setText(MovieTab.SDF.format(editing.getReleaseDate()));

        view.ageRating.setEnabled(true);
        view.releaseDate.setEnabled(true);

        view.title.requestFocus();
    }

    protected void handleDelete() {
        view.cancel.setEnabled(false);
        view.newMovie.setEnabled(true);
        view.editMovie.setEnabled(false);
        view.deleteMovie.setEnabled(false);
        view.saveMovie.setEnabled(false);
        int row = view.movieTable.getSelectedRow();
        DefaultTableModel tm = (DefaultTableModel) view.movieTable.getModel();
        long id = (long) tm.getValueAt(row, 0);

        if (getBackend().deleteMovie(id)) {
            tm.removeRow(row);
            view.movieTable.clearSelection();
        }
        showMovieDetails(null);
        view.title.setEditable(false);
        view.releaseDate.setEditable(false);
        editing = null;
    }

}
