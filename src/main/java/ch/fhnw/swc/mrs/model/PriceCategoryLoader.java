package ch.fhnw.swc.mrs.model;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Loads price category classes specified by a list in a config file. Put a text file named
 * pricecategories.config in the data folder. Provide fully qualified class names of price
 * categories to load. One class per line.
 */
public class PriceCategoryLoader {

    /**
     * Invokes the loading of price category classes.
     * 
     * @throws Exception when reading the config file fails.
     */
    public static void load() throws Exception {

        new PriceCategoryLoader().loadPriceCategories();
    }

    private void loadPriceCategories() throws Exception {

        InputStream input = getClass().getResourceAsStream("/data/pricecategories.config");

        BufferedReader r = new BufferedReader(new InputStreamReader(input));

        // reads each line
        String l;
        while ((l = r.readLine()) != null) {
            try {
                Class.forName(l);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

}
