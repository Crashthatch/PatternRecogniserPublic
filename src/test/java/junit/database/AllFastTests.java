package junit.database;

import org.junit.extensions.cpsuite.ClasspathSuite;
import org.junit.extensions.cpsuite.ClasspathSuite.ClassnameFilters;
import org.junit.runner.RunWith;

@RunWith(ClasspathSuite.class)
@ClassnameFilters({"junit.database.*", "!junit.database.processors.broken.*", "!junit.database.choosenextrow.*", "!junit.database.relationships.*", "!junit.database.inout.endtoend.*", "!junit.database.processors.TestHttpGet"})
public class AllFastTests {
    // Junit tests
}