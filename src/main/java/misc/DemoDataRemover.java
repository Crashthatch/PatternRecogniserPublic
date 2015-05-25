package misc;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Tom on 25/10/2014.
 */
public class DemoDataRemover {

    public static void main(String[] args) {

        //Create the hashes of all the directories already in "TestData" (ie. those we already know about / have test cases for).
        File[] knownDirectories = new File("testdata").listFiles();

        HashMap<String, String> knownHashes = new HashMap<>();
        knownHashes.put("8b9020921f914c69c9970f96a12768cbd7560a4d", "extract date with extra space"); //"extract date" demo with an extra space following the 3rd training example.
        knownHashes.put("cb4f4698eb461d97c0e4e3fa8d9243197524ef9d", "json to sql inserts crlf");

        for( File directory : knownDirectories){
            if( directory.isDirectory() ) {

                try {
                    StringBuilder directoryContentsAppended = new StringBuilder();
                    directoryContentsAppended.append(FileUtils.readFileToString(new File(directory.getPath()+"/trainIn.dat")));
                    directoryContentsAppended.append(FileUtils.readFileToString(new File(directory.getPath()+"/trainOut.dat")));
                    directoryContentsAppended.append(FileUtils.readFileToString(new File(directory.getPath()+"/applyIn.dat")));
                    String directoryHash = DigestUtils.sha1Hex(directoryContentsAppended.toString());

                    knownHashes.put(directoryHash, directory.getName());
                } catch (IOException e) {

                }
            }
        }

        System.out.println(knownHashes.size()+" directories found that we know about & have hashed.");

        //Create the hashes of all the directories in the
        File[] directories = new File("testdata/dumped from server").listFiles();
        int newFilesCount = 0;
        for( File directory : directories){
            assert(directory.isDirectory());

            StringBuilder directoryContentsAppended = new StringBuilder();
            try {
                directoryContentsAppended.append(FileUtils.readFileToString(new File(directory.getPath()+"/trainIn.dat")));
                directoryContentsAppended.append(FileUtils.readFileToString(new File(directory.getPath()+"/trainOut.dat")));
                directoryContentsAppended.append(FileUtils.readFileToString(new File(directory.getPath()+"/applyIn.dat")));
            } catch (IOException e) {
                e.printStackTrace();
            }


            String directoryHash = DigestUtils.sha1Hex(directoryContentsAppended.toString());

            if( knownHashes.get(directoryHash) == null) {
                newFilesCount++;
                System.out.println("Previously unseen directory: "+directory.getName() + ": " + directoryHash);
            }
            else{
                System.out.println("Deleting directory "+directory.getName()+" because it matches '"+knownHashes.get(directoryHash)+"'");
                try {
                    FileUtils.deleteDirectory(directory);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println(newFilesCount+" previously unseen directories found.");
    }
}
