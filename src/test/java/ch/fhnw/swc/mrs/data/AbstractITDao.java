package ch.fhnw.swc.mrs.data;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;


public class AbstractITDao {

    private static EntityManagerFactory emf;
    
    protected AbstractITDao() { }

    @BeforeAll
    static void initialize() throws Exception {
        emf = Persistence.createEntityManagerFactory("MRS.Test");
    }
    
    @AfterAll
    static void closeDB() throws Exception {
        emf.close();
    }
        
    protected static EntityManagerFactory getEMF() {
        return emf;
    }
    

}
