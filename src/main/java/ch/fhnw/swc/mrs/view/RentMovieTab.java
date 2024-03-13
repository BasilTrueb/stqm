package ch.fhnw.swc.mrs.view;

import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.table.DefaultTableModel;

/**
 * Implementation of the tab "Rent a Movie".
 */
public class RentMovieTab extends JPanel {

    JTextField birthdate = new JTextField();
    JTextField rentalDate = new JTextField();
    JTextField firstname = new JTextField();
    JTextField surname = new JTextField();
    JTextField id = new JTextField();

    JButton getUser = new JButton("get User");
    JButton save = new JButton("Save");
    JButton clearAll = new JButton("Clear All");

    JCheckBox newUser = new JCheckBox("new User");

    JTable movieTable = new JTable();

    private RentMovieController controller;

    protected static final DateTimeFormatter SDF = DateTimeFormatter.ISO_DATE;
    private static final String[] HEADERS = new String[] { "Movie ID", "Title", "Release Date", "Price Category" };
    private static final Class<?>[] TYPES = new Class[] { Long.class, String.class, Object.class, Object.class };

    /**
     * Create and initialize the content of the "Rent a Movie" tab.
     * 
     * @param controller the controller that manages this view.
     */
    public RentMovieTab(RentMovieController controller) {
        this.setName("Rent Movie");
        this.controller = controller;
        controller.setView(this);
        createUIElements();
        setLayout();
    }

    private void createUIElements() {
        String today = LocalDate.now().format(SDF);
        birthdate.setToolTipText("Please enter the customers birthdate.");
        birthdate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                birthdateTextFieldKeyPressed(evt);
            }
        });

        rentalDate.setText(today);
        rentalDate.setToolTipText("Rental date defaults to today's date.");

        firstname.setToolTipText("Please enter customer's first name.");
        firstname.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                firstNameTextFieldKeyPressed(evt);
            }
        });

        surname.setToolTipText("Please enter customer's surname.");
        surname.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                surNameTextFieldKeyPressed(evt);
            }
        });

        id.setToolTipText("Enter customer id to find a customer.");
        id.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                userIdForTextFieldKeyPressed(evt);
            }
        });

        getUser.setToolTipText("Retrieves a known customer from the database.");
        getUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                controller.handleGetUser();
            }
        });

        save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                controller.handleSave();
            }
        });

        clearAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                controller.handleClearAll();
            }
        });

        newUser.setToolTipText("Check if the user is not stored or find in the database.");
        newUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                controller.handleNewUser();
            }
        });

        DefaultTableModel tm = new DefaultTableModel() {
            public Class<?> getColumnClass(int columnIndex) {
                return TYPES[columnIndex];
            }
        };
        tm.setColumnIdentifiers(HEADERS);
        movieTable.setModel(tm);
    }

    private void setLayout() {
        JLabel surNameLabel = new JLabel("Surname:");
        JLabel firstNameLabel = new JLabel("First name:");
        JLabel rentalDateLabel = new JLabel("Rental date:");
        JLabel birthdateLabel = new JLabel("Date of birth:");
        JLabel userIdLabel = new JLabel("User ID:");

        GroupLayout mainPanelLayout = new GroupLayout(this);
        JScrollPane movieScrollPane = new JScrollPane(movieTable);
        setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(mainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
                GroupLayout.Alignment.TRAILING,
                mainPanelLayout.createSequentialGroup().addGap(175, 175, 175).addComponent(clearAll)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGap(18, 18, 18).addComponent(save)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addContainerGap())
                .addGroup(mainPanelLayout.createSequentialGroup().addContainerGap()
                        .addGroup(mainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(birthdateLabel).addComponent(rentalDateLabel).addComponent(firstNameLabel)
                                .addComponent(surNameLabel))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(mainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                .addGroup(mainPanelLayout.createSequentialGroup()
                                        .addComponent(surname, GroupLayout.PREFERRED_SIZE, 80,
                                                GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18).addComponent(userIdLabel)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(id, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE))
                                .addComponent(firstname).addComponent(rentalDate).addComponent(birthdate))
                        .addGap(68, 68, 68).addComponent(newUser)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(getUser)
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addComponent(movieScrollPane, GroupLayout.DEFAULT_SIZE, 571, Short.MAX_VALUE));

        mainPanelLayout
                .setVerticalGroup(
                        mainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                                        .addComponent(movieScrollPane, GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE)
                                        .addGap(18, 18, 18)
                                        .addGroup(mainPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(surNameLabel)
                                                .addComponent(surname, GroupLayout.PREFERRED_SIZE,
                                                        GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                .addComponent(userIdLabel)
                                                .addComponent(id, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                                        GroupLayout.PREFERRED_SIZE)
                                                .addComponent(getUser).addComponent(newUser))
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(mainPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(firstNameLabel).addComponent(firstname,
                                                        GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                                        GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(mainPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(rentalDateLabel)
                                                .addComponent(rentalDate, GroupLayout.PREFERRED_SIZE,
                                                        GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(mainPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(birthdateLabel).addComponent(birthdate,
                                                        GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                                        GroupLayout.PREFERRED_SIZE))
                                        .addGap(18, 18, 18)
                                        .addGroup(mainPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(save).addComponent(clearAll))
                                        .addContainerGap()));
    }

    private void birthdateTextFieldKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            controller.handleSave();
        } else if (evt.getKeyCode() == KeyEvent.VK_TAB) {
            save.requestFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
            controller.handleClearAll();
        }
    }

    private void surNameTextFieldKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_ENTER && getUser.isEnabled()) {
            birthdate.requestFocus();
            controller.handleGetUser();
        } else if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
            controller.handleClearAll();
        }
    }

    private void firstNameTextFieldKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
            controller.handleClearAll();
        } else if (evt.getKeyCode() == KeyEvent.VK_TAB) {
            birthdate.requestFocus();
        }
    }

    private void userIdForTextFieldKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
            controller.handleClearAll();
        } else if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            controller.handleGetUser();
            birthdate.requestFocus();
        }
    }

}
