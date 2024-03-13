package ch.fhnw.swc.mrs.view;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * Implementation of the tab "Rental"
 */
public class RentalTab extends JPanel {

    JButton deleteButton = new JButton("Delete");
    JTable table = new JTable();

    private static final String[] HEADERS = new String[] { "Rental ID", "Rental Date", "Rental Days", "Title",
            "Rental Fee" };
    private static final Class<?>[] TYPES = new Class[] { Long.class, String.class, Integer.class, String.class,
            Double.class };

    private RentalController controller;

    /**
     * Create and initialize the content of the "Rentals" tab.
     * 
     * @param controller the controller that manages this view.
     */
    public RentalTab(RentalController controller) {
        this.setName("Rentals");
        this.controller = controller;
        controller.setView(this);
        createUIElements();
        setLayout();
    }

    private void createUIElements() {
        deleteButton.setText("Delete");
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                controller.handleDelete();
            }
        });

        DefaultTableModel tm = new DefaultTableModel() {
            public Class<?> getColumnClass(int col) {
                return TYPES[col];
            }
        };
        tm.setColumnIdentifiers(HEADERS);
        table.setModel(tm);

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                controller.handleTableClicked();
            }
        });
    }

    private void setLayout() {

        GroupLayout getRentalsDialogLayout = new GroupLayout(this);
        JScrollPane scrollPane = new JScrollPane(table);

        setLayout(getRentalsDialogLayout);
        getRentalsDialogLayout.setHorizontalGroup(getRentalsDialogLayout
                .createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(GroupLayout.Alignment.TRAILING,
                        getRentalsDialogLayout.createSequentialGroup().addContainerGap().addContainerGap()));
        getRentalsDialogLayout.setVerticalGroup(getRentalsDialogLayout
                .createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(GroupLayout.Alignment.TRAILING,
                        getRentalsDialogLayout.createSequentialGroup().addContainerGap()));

        GroupLayout rentalsCRUDPanelLayout = new GroupLayout(this);

        this.setLayout(rentalsCRUDPanelLayout);

        rentalsCRUDPanelLayout
                .setHorizontalGroup(rentalsCRUDPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 527, Short.MAX_VALUE)
                        .addGroup(GroupLayout.Alignment.TRAILING, rentalsCRUDPanelLayout.createSequentialGroup()
                                .addContainerGap(446, Short.MAX_VALUE).addComponent(deleteButton).addContainerGap()));

        rentalsCRUDPanelLayout
                .setVerticalGroup(
                        rentalsCRUDPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(GroupLayout.Alignment.TRAILING, rentalsCRUDPanelLayout.createSequentialGroup()
                                        .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 444, Short.MAX_VALUE)
                                        .addGap(18, 18, 18).addComponent(deleteButton).addContainerGap()));

    }
}
