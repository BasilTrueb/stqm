package ch.fhnw.swc.mrs.view;

import javax.swing.JFrame;

import ch.fhnw.swc.mrs.api.MRSServices;

/** Controller responsible for overall application. */
public class MainController {

    private RentMovieController rentMovieController;
    private MovieController movieController;
    private RentalController rentalController;
    private UserController userController;

    protected void setRentMovieController(RentMovieController rmc) {
        rentMovieController = rmc;
    }

    protected void setMovieController(MovieController mc) {
        movieController = mc;
    }

    protected void setRentalController(RentalController rc) {
        rentalController = rc;
    }

    protected void setUserController(UserController uc) {
        userController = uc;
    }

    /**
     * Handle change of main tab.
     * 
     * @param index new tab to display.
     */
    public void handleTabChange(int index) {
        // refresh data in tables when it is selected
        switch (index) {
        case TabFrame.RENT_MOVIE_TAB_INDEX:
            rentMovieController.reload();
            break;
        case TabFrame.MOVIES_TAB_INDEX:
            movieController.reload();
            break;
        case TabFrame.RENTALS_TAB_INDEX:
            rentalController.reload();
            break;
        case TabFrame.USERS_TAB_INDEX:
            userController.reload();
            break;

        default:
        }
    }

    /**
     * Set the list of movies in the table.
     * 
     * @param backend the backend to use in the tabs' controllers.
     * @return the main frame to display.
     */
    public JFrame init(MRSServices backend) {
        TabFrame tabs = new TabFrame(this);
        tabs.initTabs(backend);
        rentMovieController.setParent(tabs);
        movieController.setParent(tabs);
        rentalController.setParent(tabs);
        // preload first tab, as it will not receive a selection change event.
        rentMovieController.reload();
        return tabs;
    }

}
