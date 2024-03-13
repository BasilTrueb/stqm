package ch.fhnw.swc.mrs.data;

import javax.sql.DataSource;

import org.dbunit.DataSourceDatabaseTester;
import org.dbunit.DefaultPrepAndExpectedTestCase;
import org.dbunit.PrepAndExpectedTestCaseSteps;
import org.dbunit.VerifyTableDefinition;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.ext.postgresql.PostgresqlDataTypeFactory;
import org.dbunit.util.fileloader.FlatXmlDataFileLoader;
import org.junit.Ignore;

// an IntelliJ issue: IntelliJ tries to execute this as a "vintage" (Junit3) test, and warns that no tests are
// implemented
// we have to use @ignore, since the class is derived from TestCase (a Junit3 concept) 
@Ignore
public class MRSPrepAndExpectedTestCase extends DefaultPrepAndExpectedTestCase {
    private VerifyTableDefinition vtd;

    public Object runtest(String prepDataFile, String expectedDataFile, PrepAndExpectedTestCaseSteps steps)
            throws Exception {

        final String[] prepDataFiles = {"/ch/fhnw/swc/mrs/data/" + prepDataFile}; // define prep
                                                                                    // file as set
        final String[] expectedDataFiles = {"/ch/fhnw/swc/mrs/data/" + expectedDataFile}; // define
                                                                                            // expected
                                                                                            // file
                                                                                            // as
                                                                                            // set
        final VerifyTableDefinition[] tables = {vtd}; // define tables to verify as set
        return runTest(tables, prepDataFiles, expectedDataFiles, steps); // run the test
    }

    public MRSPrepAndExpectedTestCase(DataSource ds, VerifyTableDefinition aVtd) {
        super(new FlatXmlDataFileLoader(), new DataSourceDatabaseTester(ds));
        this.vtd = aVtd;
    }

    @Override
    protected void setUpDatabaseConfig(final DatabaseConfig config) {
        config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new PostgresqlDataTypeFactory());
    }
}
