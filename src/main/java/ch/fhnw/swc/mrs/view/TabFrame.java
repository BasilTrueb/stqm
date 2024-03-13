package ch.fhnw.swc.mrs.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ch.fhnw.swc.mrs.api.MRSServices;
import ch.fhnw.swc.mrs.view.about.AboutDialog;

public class TabFrame extends JFrame {

    /** Tab index for <I>Rent Movie</I> tab. */
    protected static final int RENT_MOVIE_TAB_INDEX = 0;
    /** Tab index for <I>Movies</I> tab. */
    protected static final int MOVIES_TAB_INDEX = 1;
    /** Tab index for <I>Users</I> tab. */
    protected static final int USERS_TAB_INDEX = 2;
    /** Tab index for <I>Rentals</I> tab. */
    protected static final int RENTALS_TAB_INDEX = 3;

    private JTabbedPane mainPane;
    private JMenuBar menuBar;
    private MainController mainController;

    /** Error message when attempting to set look and feel. */
    public static final String LOOK_AND_FEEL_ERR_MSG = "Error attempting to set look and feel of the system";

    /**
     * Main Frame.
     * 
     * @param mainController the main controller managing the tabs.
     */
    public TabFrame(MainController mainController) {
        this.mainController = mainController;
        setLookAndFeel();
        initComponents();
    }

    /**
     * Set the apps look and feel from the systems.
     */
    private void setLookAndFeel() {
        try {
            String ui = UIManager.getSystemLookAndFeelClassName();
            UIManager.setLookAndFeel(ui);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, LOOK_AND_FEEL_ERR_MSG + ":\n" + e.getLocalizedMessage());
        }
    }

    private void initComponents() {
        mainPane = new JTabbedPane();
        menuBar = new JMenuBar();

        initMenu();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Movie Rental System");
        setName("mainFrame");

        mainPane.setName("mainPane");
        mainPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent evt) {
                mainPaneStateChanged(evt);
            }
        });

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(mainPane,
                GroupLayout.DEFAULT_SIZE, 532, Short.MAX_VALUE));
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(mainPane,
                GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE));

        pack();
    }

    private void addRentMoviePanel(MRSServices backend) {
        RentMovieController rmc = new RentMovieController();
        rmc.setBackend(backend);
        RentMovieTab rmTab = new RentMovieTab(rmc);
        mainController.setRentMovieController(rmc);
        mainPane.insertTab("Rent Movie", null, rmTab, "Rent Movie", RENT_MOVIE_TAB_INDEX);
    }

    private void addMoviePanel(MRSServices backend) {
        MovieController mc = new MovieController();
        mc.setBackend(backend);
        MovieTab mTab = new MovieTab(mc);
        mainController.setMovieController(mc);
        mainPane.insertTab("Movies", null, mTab, "Movies", MOVIES_TAB_INDEX);
    }

    private void addRentalsPanel(MRSServices backend) {
        RentalController rc = new RentalController();
        rc.setBackend(backend);
        RentalTab rTab = new RentalTab(rc);
        mainController.setRentalController(rc);
        mainPane.insertTab("Rentals", null, rTab, "Rentals", RENTALS_TAB_INDEX);
    }

    private void addUsersPanel(MRSServices backend) {
        UserController uc = new UserController();
        uc.setBackend(backend);
        UserTab uTab = new UserTab(uc);
        mainController.setUserController(uc);
        mainPane.insertTab("Clients", null, uTab, "Clients", USERS_TAB_INDEX);
    }

    /**
     * Set the list of movies in the table.
     * 
     * @param backend the backend to use in the tabs' controllers.
     */
    public void initTabs(MRSServices backend) {
        addRentMoviePanel(backend);
        addMoviePanel(backend);
        addUsersPanel(backend);
        addRentalsPanel(backend);

        mainController.handleTabChange(RENT_MOVIE_TAB_INDEX);
    }

    /**
     * Initialize the app's menu.
     */
    private void initMenu() {

        JMenu menu = new JMenu("Options");
        menuBar.add(menu);

        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        menu.add(exitMenuItem);
        menu.add(new JSeparator());

        JMenuItem aboutMenuItem = new JMenuItem("About...");
        aboutMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });
        menu.add(aboutMenuItem);

        setJMenuBar(menuBar);
    }

    /**
     * @param evt none.
     */
    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        System.exit(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * @param evt none.
     */
    private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        AboutDialog about = new AboutDialog();
        about.setVisible(true);
    }

    /**
     * @param evt none.
     */
    protected void mainPaneStateChanged(ChangeEvent evt) {
        mainController.handleTabChange(mainPane.getSelectedIndex());
    }

}
