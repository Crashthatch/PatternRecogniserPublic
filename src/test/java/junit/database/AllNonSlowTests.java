package junit.database;

import org.junit.extensions.cpsuite.ClasspathSuite;
import org.junit.extensions.cpsuite.ClasspathSuite.ClassnameFilters;
import org.junit.runner.RunWith;

@RunWith(ClasspathSuite.class)
@ClassnameFilters({"junit.database.*", "!junit.database.processors.broken.*", "!junit.database.choosenextrow.broken.*", "!junit.database.relationships.*", "!junit.database.inout.endtoend.broken.*", "!junit.database.inout.endtoend.slow.*"})
public class AllNonSlowTests {
    // Junit tests
}