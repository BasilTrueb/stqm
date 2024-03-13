package ch.fhnw.swc.mrs.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.table.DefaultTableModel;

public class UserTab extends JPanel {

    JButton saveButton = new JButton("Save");
    JButton deleteButton = new JButton("Delete");
    JButton editButton = new JButton("Edit");
    JButton cancelButton = new JButton("Cancel");
    JButton newButton = new JButton("New...");
    JTextField surnameTF = new JTextField();
    JTextField firstnameTF = new JTextField();
    JTextField birthdateTF = new JTextField();

    JTable table = new JTable();

    private UserController controller;

    protected static final DateTimeFormatter SDF = DateTimeFormatter.ISO_DATE;
    private static final String[] HEADERS = new String[] { "User ID", "Surname", "First name", "Date of Birth" };
    private static final Class<?>[] TYPES = new Class[] { Integer.class, String.class, String.class, String.class };

    /**
     * Create and initialize the content of the "User" tab.
     * 
     * @param controller the controller that manages this view.
     */
    public UserTab(UserController controller) {
        this.setName("Users");
        this.controller = controller;
        controller.setView(this);
        createUIElements();
        setLayout();
    }

    private void createUIElements() {
        controller.handleCancel();
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                controller.handleSave();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                controller.handleDelete();
            }
        });

        editButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                controller.handleEdit();
            }
        });

        newButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                controller.handleNew();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                controller.handleCancel();
            }
        });

        DefaultTableModel tm = new DefaultTableModel() {
            public Class<?> getColumnClass(int colIndex) {
                return TYPES[colIndex];
            }
        };
        tm.setColumnIdentifiers(HEADERS);
        table.setModel(tm);
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                controller.handleTableClicked(table.getRowCount() > 0);
            }
        });
    }

    private void setLayout() {
        JLabel firstNameUsersLabel = new JLabel("First Name:");
        JLabel surNameUsersLabel = new JLabel("Surname:");
        JLabel birthdateLabel = new JLabel("Date of Birth:");

        GroupLayout layout = new GroupLayout(this);
        JScrollPane scrollPane = new JScrollPane(table);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(GroupLayout.Alignment.TRAILING,
                        layout.createSequentialGroup().addContainerGap(149, Short.MAX_VALUE).addComponent(cancelButton)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(newButton)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(editButton)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(deleteButton)
                                .addGap(18, 18, 18).addComponent(saveButton).addContainerGap())
                .addGroup(GroupLayout.Alignment.TRAILING,
                        layout.createSequentialGroup().addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(firstNameUsersLabel).addComponent(surNameUsersLabel)
                                        .addComponent(birthdateLabel))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(surnameTF, GroupLayout.Alignment.LEADING,
                                                GroupLayout.DEFAULT_SIZE, 282, Short.MAX_VALUE)
                                        .addComponent(firstnameTF, GroupLayout.Alignment.LEADING,
                                                GroupLayout.DEFAULT_SIZE, 282, Short.MAX_VALUE)
                                        .addComponent(birthdateTF, GroupLayout.Alignment.LEADING,
                                                GroupLayout.DEFAULT_SIZE, 282, Short.MAX_VALUE))
                                .addGap(53, 53, 53).addContainerGap())
                .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 527, Short.MAX_VALUE));

        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 372, Short.MAX_VALUE).addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(surNameUsersLabel).addComponent(surnameTF, GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(firstNameUsersLabel).addComponent(firstnameTF, GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(birthdateLabel).addComponent(birthdateTF, GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(saveButton)
                                .addComponent(deleteButton).addComponent(editButton).addComponent(newButton)
                                .addComponent(cancelButton))
                        .addContainerGap()));
    }
}
