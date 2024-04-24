package ch.fhnw.swc.mrs.view;

import java.util.Collection;

import javax.swing.table.DefaultTableModel;
import ch.fhnw.swc.mrs.model.Rental;

/**
 * Controller class for Rentals.
 */
public class RentalController extends AbstractController {
    private RentalTab view;

    protected void setView(RentalTab view) {
        this.view = view;
    }

    @Override
    public void reload() {
        Collection<Rental> rentals = getBackend().getAllRentals();
        DefaultTableModel tm = (DefaultTableModel) view.table.getModel();

        tm.setRowCount(0);
        for (Rental r : rentals) {
            tm.addRow(fillInMovieArrayElement(r));
        }
    }

    private Object[] fillInMovieArrayElement(Rental rental) {
        Object[] result = new Object[6];
        result[0] = rental.getRentalId();
        result[1] = RentMovieTab.SDF.format(rental.getRentalDate());
        result[2] = rental.getRentalDays();
        result[3] = rental.getMovie().getTitle();
        result[4] = rental.getRentalFee();
        return result;
    }

    protected void handleTableClicked() {
        if (view.table.getRowCount() > 0) {
            view.deleteButton.setEnabled(true);
        } else {
            view.deleteButton.setEnabled(false);
        }
    }

    protected void handleDelete() {
        view.deleteButton.setEnabled(false);

        int row = view.table.getSelectedRow();
        DefaultTableModel tm = (DefaultTableModel) view.table.getModel();
        long id = (long) tm.getValueAt(row, 0);
        if (getBackend().deleteRental(id)) {
            tm.removeRow(row);
            view.table.getSelectionModel().clearSelection();
        }    
    }

}
