package ch.fhnw.swc.mrs.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RegularPriceCategoryTest {
    private static final double TOLERANCE = 1.0e-10;

    private RegularPriceCategory rpc;

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
        rpc = RegularPriceCategory.getInstance();
    }

    @Test
    public void testGetCharge() {
        assertEquals(0.0d, rpc.getCharge(-3), TOLERANCE);
        assertEquals(0.0d, rpc.getCharge(0), TOLERANCE);
        assertEquals(2.0d, rpc.getCharge(1), TOLERANCE);
        assertEquals(2.0d, rpc.getCharge(2), TOLERANCE);
        assertEquals(3.5d, rpc.getCharge(3), TOLERANCE);
        assertEquals(5.0d, rpc.getCharge(4), TOLERANCE);
        assertEquals(32.0d, rpc.getCharge(22), TOLERANCE);
    }

    @Test
    public void testToString() {
        assertEquals("Regular", rpc.toString());
    }

}
