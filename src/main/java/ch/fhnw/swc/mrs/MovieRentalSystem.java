package ch.fhnw.swc.mrs;

import ch.fhnw.swc.mrs.data.DbMRSServices;
import ch.fhnw.swc.mrs.view.MainController;

/**
 * Main class of the Movie Rental System App.
 */
public class MovieRentalSystem {

    private DbMRSServices backend = new DbMRSServices();

    /**
     * start initialization of the application.
     */
    public void start() {
        backend.createDB();
        MainController controller = new MainController();
        controller.init(backend).setVisible(true);
    }

    /**
     * The main method to start the app.
     * 
     * @param args currently ignored.
     * @throws Exception whenever something goes wrong.
     */
    public static void main(String[] args) throws Exception {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MovieRentalSystem().start();
            }
        });
    }


}
