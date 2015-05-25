package misc;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Created by Tom on 25/10/2014.
 */
public class UnusedTestDataFinder {

    static class FileHandler extends SimpleFileVisitor<Path> {

        private String needle;
        public boolean found = false;

        FileHandler(String needle){
            this.needle = needle;
        }
        // Print information about
        // each type of file.
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {
            try{
                Scanner scanner = new Scanner(file);
                if (scanner.findWithinHorizon(Pattern.quote(needle), 0) != null) {
                    found = true;
                }
                scanner.close();
            }
            catch( IOException e ){
                e.printStackTrace();
            }

            return FileVisitResult.CONTINUE;
        }

        /*// Print each directory visited.
        @Override
        public FileVisitResult postVisitDirectory(Path dir,
                                                  IOException exc) {
            System.out.format("Directory: %s%n", dir);
            return FileVisitResult.CONTINUE;
        }

        // If there is some error accessing
        // the file, let the user know.
        // If you don't override this method
        // and an error occurs, an IOException
        // is thrown.
        @Override
        public FileVisitResult visitFileFailed(Path file,
                                               IOException exc) {
            System.err.println(exc);
            return FileVisitResult.CONTINUE;
        }*/
    }

    public static void main(String[] args) {
        File[] testDataFolders = new File("testdata").listFiles();

        int foundFiles = 0;
        int unfoundFiles = 0;

        for( File testDataFolder : testDataFolders ){
            if( !testDataFolder.isDirectory() ){
                continue;
            }

            //Search through all the tests sources looking for the string filename.
            try{
                FileHandler fh = new FileHandler(testDataFolder.getName());
                Files.walkFileTree(Paths.get("src/test/java/junit"), fh);

                if( !fh.found ){
                    unfoundFiles++;
                    System.out.println(testDataFolder.getName());
                }
                else{
                    foundFiles++;
                }
            }
            catch( IOException e ){
                e.printStackTrace();
            }
        }

        System.out.println();
        System.out.println(unfoundFiles+" folders that are NOT used in tests and "+foundFiles+" that are.");
    }
}
