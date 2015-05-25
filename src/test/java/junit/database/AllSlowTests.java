package junit.database;

import org.junit.extensions.cpsuite.ClasspathSuite;
import org.junit.extensions.cpsuite.ClasspathSuite.ClassnameFilters;
import org.junit.runner.RunWith;

@RunWith(ClasspathSuite.class)
@ClassnameFilters({"junit.database.inout.endtoend.slow.*"})
public class AllSlowTests {
    // Junit tests
}