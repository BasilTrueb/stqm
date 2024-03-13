package ch.fhnw.swc.mrs.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PriceCategoryTest {

    private PriceCategory pc;

    private class TestCategory extends PriceCategory {

        @Override
        public double getCharge(int daysRented) {
            return 0.0;
        }
    }

    /**
     * make sure the categories are loaded before any test is executed
     * @throws Exception 
     */
    @BeforeAll
    public static void loadCategories() throws Exception {
        PriceCategoryLoader.load();
    }
    
    @BeforeEach
    public void setUp() {
        pc = new TestCategory();
    }

    @Test
    public void testGetFrequentRenterPoints() throws Exception {
        
        assertEquals(0, pc.getFrequentRenterPoints(-6));
        assertEquals(0, pc.getFrequentRenterPoints(0));
        assertEquals(1, pc.getFrequentRenterPoints(1));
        assertEquals(1, pc.getFrequentRenterPoints(2));
        assertEquals(1, pc.getFrequentRenterPoints(4000));
    }

    @Test
    public void testGetPriceCategoryFromId() {

        PriceCategory pc = PriceCategory.getPriceCategoryFromId("Regular");
        assertEquals("Regular", pc.toString());

        pc = PriceCategory.getPriceCategoryFromId("NoneExisting");
        assertNull(pc);

    }

}
