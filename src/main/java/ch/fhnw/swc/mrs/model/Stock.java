package ch.fhnw.swc.mrs.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import ch.fhnw.swc.mrs.api.MovieRentalException;

/**
 * Manages the stock of videos of the rental shop.
 */
public class Stock {

    /** The stock of videos. */
    private HashMap<String, Integer> stock = new HashMap<String, Integer>();

    /** low stock listeners. */
    private List<LowStockListener> listeners = new LinkedList<LowStockListener>();

    /**
     * Add a movie to the stock.
     * 
     * @param movie the movie to add to the stock.
     * @return the number of items of this movie in stock after this operation.
     * @throws NullPointerException if the argument is <code>null</code>
     */
    public int addToStock(Movie movie) {
        Integer i = stock.get(movie.getTitle());
        int inStock = (i == null) ? 0 : i;
        stock.put(movie.getTitle(), ++inStock);
        return inStock;
    }

    /**
     * Removes a movie from the stock.
     * 
     * @param movie the movie to remove from the stock.
     * @return the number of items of this movie in stock after this operation.
     */
    public int removeFromStock(Movie movie) {
        String title = movie.getTitle();
        Integer i = stock.get(title);
        int inStock = (i == null) ? 0 : i;
        if (inStock <= 0) {
            throw new MovieRentalException("no video in stock");
        }
        stock.put(title, --inStock);
        notifyListeners(movie, inStock);
        return inStock;
    }

    /**
     * Notify all LowStockListeners with a threshold of c or below that movie m is low in stock.
     * 
     * @param m movie to notify
     * @param c threshold for notification
     */
    private void notifyListeners(Movie m, int c) {
        for (LowStockListener l : listeners) {
            if (l.getThreshold() >= c) {
                l.stockLow(m, c);
            }
        }
    }

    /**
     * @param title the movie title to get the stock count.
     * @return the number copies of the movie still in stock.
     */
    public int getInStock(String title) {
        Integer i = stock.get(title);
        return (i == null) ? 0 : i;
    }

    /**
     * Add a stock listener.
     * 
     * @param l listener
     */
    public void addLowStockListener(LowStockListener l) {
        if (l != null && !listeners.contains(l)) {
            listeners.add(l);
        }
    }

    /**
     * Remove a stock listener.
     * 
     * @param l listener
     */
    public void removeLowStockListener(LowStockListener l) {
        listeners.remove(l);
    }

}
