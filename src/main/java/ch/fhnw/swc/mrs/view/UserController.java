package ch.fhnw.swc.mrs.view;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.UUID;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import ch.fhnw.swc.mrs.model.User;

/**
 * Java FX controller class for Users.
 */
public class UserController extends AbstractController {

    private User editing = null; // currently no user is being edited.
    private UserTab view;

    protected void setView(UserTab view) {
        this.view = view;
    }

    @Override
    public void reload() {
        Collection<User> users = getBackend().getAllUsers();
        DefaultTableModel tm = (DefaultTableModel) view.table.getModel();

        tm.setRowCount(0);
        for (User u : users) {
            tm.addRow(fillInUserArrayElement(u));
        }
    }

    private Object[] fillInUserArrayElement(User u) {
        Object[] result = new Object[4];
        result[0] = u.getUserid();
        result[1] = u.getName();
        result[2] = u.getFirstName();
        result[3] = u.getBirthdate().format(UserTab.SDF);
        return result;
    }

    protected void handleTableClicked(boolean tableContainsUsers) {
        if (tableContainsUsers) {
            view.deleteButton.setEnabled(true);
            view.editButton.setEnabled(true);
            view.newButton.setEnabled(true);
            view.saveButton.setEnabled(false);
            view.cancelButton.setEnabled(false);

            view.surnameTF.setEnabled(false);
            view.surnameTF.setText("");
            view.firstnameTF.setEnabled(false);
            view.firstnameTF.setText("");
            view.birthdateTF.setEnabled(false);
            view.birthdateTF.setText("");
        } else {
            handleCancel();
        }
    }

    private void showUserDetails(User user) {
        if (user != null) {
            // fill the labels with info from the Movie object
            view.surnameTF.setText(user.getName());
            view.firstnameTF.setText(user.getFirstName());
            view.birthdateTF.setText(user.getBirthdate().format(UserTab.SDF));
        } else {
            // clear the content and set default values
            view.surnameTF.setText("");
            view.firstnameTF.setText("");
            view.birthdateTF.setText("");
        }
    }

    protected void handleCancel() {
        view.cancelButton.setEnabled(false);
        view.newButton.setEnabled(true);
        view.editButton.setEnabled(false);
        view.deleteButton.setEnabled(false);
        view.saveButton.setEnabled(false);
        showUserDetails(null);
        view.surnameTF.setEditable(false);
        view.firstnameTF.setEditable(false);
        view.birthdateTF.setEditable(false);
        editing = null;
    }

    protected void handleNew() {
        view.cancelButton.setEnabled(true);
        view.newButton.setEnabled(false);
        view.editButton.setEnabled(false);
        view.deleteButton.setEnabled(false);
        view.saveButton.setEnabled(true);
        showUserDetails(null);
        view.surnameTF.setEditable(true);
        view.firstnameTF.setEditable(true);
        view.birthdateTF.setEditable(true);
        view.surnameTF.requestFocus();
        editing = null;
    }

    protected void handleSave() {
        String birthdateText = view.birthdateTF.getText();
        LocalDate birthdate = text2Date(birthdateText);
        if (editing == null) {
            User u = getBackend().createUser(view.surnameTF.getText(), view.firstnameTF.getText(), birthdate);
            DefaultTableModel tm = (DefaultTableModel) view.table.getModel();
            tm.addRow(fillInUserArrayElement(u));
        } else {
            editing.setName(view.surnameTF.getText());
            editing.setFirstName(view.firstnameTF.getText());
            editing.setBirthdate(birthdate);
            getBackend().updateUser(editing);
        }
        handleCancel();
    }

    private LocalDate text2Date(String relDateText) {
        LocalDate date = LocalDate.now();
        try {
            date = LocalDate.parse(relDateText, UserTab.SDF);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(null, "Illegal Date format. Please use yyyy-mm-dd");
        }
        return date;
    }

    protected void handleEdit() {
        view.cancelButton.setEnabled(true);
        view.newButton.setEnabled(false);
        view.editButton.setEnabled(false);
        view.deleteButton.setEnabled(false);
        view.saveButton.setEnabled(true);

        int row = view.table.getSelectedRow();
        DefaultTableModel tm = (DefaultTableModel) view.table.getModel();
        UUID id = UUID.fromString((String) tm.getValueAt(row, 0));
        editing = getBackend().getUserById(id);

        view.surnameTF.setEditable(true);
        view.firstnameTF.setEditable(true);
        view.birthdateTF.setEditable(true);
        view.surnameTF.requestFocus();
    }

    protected void handleDelete() {
        view.cancelButton.setEnabled(true);
        view.newButton.setEnabled(false);
        view.editButton.setEnabled(true);
        view.deleteButton.setEnabled(true);
        view.saveButton.setEnabled(true);

        int row = view.table.getSelectedRow();
        DefaultTableModel tm = (DefaultTableModel) view.table.getModel();
        UUID id = UUID.fromString((String) tm.getValueAt(row, 0));

        if (getBackend().deleteUser(id)) {
            tm.removeRow(row);
            view.table.clearSelection();
        }
        showUserDetails(null);
        view.surnameTF.setEditable(false);
        view.firstnameTF.setEditable(false);
        view.birthdateTF.setEditable(false);
        editing = null;
    }

}
