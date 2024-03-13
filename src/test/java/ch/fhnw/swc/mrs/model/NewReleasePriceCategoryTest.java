package ch.fhnw.swc.mrs.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NewReleasePriceCategoryTest {

    private static final double TOLERANCE = 1.0e-10;

    private NewReleasePriceCategory nrpc;

    /**
     * make sure the categories are loaded before any test is executed
     * @throws Exception 
     */
    @BeforeAll
    public static void loadCategories() throws Exception {
        PriceCategoryLoader.load();
    }
    
    @BeforeEach
    public void setUp() throws Exception {
        nrpc = NewReleasePriceCategory.getInstance();
    }

    @Test
    public void testGetCharge() {
        assertEquals(0.0d, nrpc.getCharge(-5), TOLERANCE);
        assertEquals(0.0d, nrpc.getCharge(0), TOLERANCE);
        assertEquals(3.0d, nrpc.getCharge(1), TOLERANCE);
        assertEquals(6.0d, nrpc.getCharge(2), TOLERANCE);
        assertEquals(66.0d, nrpc.getCharge(22), TOLERANCE);
    }

    @Test
    public void testGetFrequentRenterPoints() {
        assertEquals(0, nrpc.getFrequentRenterPoints(-3));
        assertEquals(0, nrpc.getFrequentRenterPoints(0));
        assertEquals(1, nrpc.getFrequentRenterPoints(1));
        assertEquals(2, nrpc.getFrequentRenterPoints(2));
        assertEquals(2, nrpc.getFrequentRenterPoints(50));
    }

    @Test
    public void testToString() {
        assertEquals("New Release", nrpc.toString());
    }

}
