package database.worker;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import database.AttRelationshipGraph;
import database.RelationshipFinderInputOutput;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.io.*;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The worker listens for jobs from a Redis Resque queue, and uses InputOutputConverter to find a transformer, then apply the same transformation to a new file.
 * File transfer is done via S3 (the client should save files there, and pass the folder id via redis, then this worker will pick up the files and do the processing).
 * The worker really just makes RelationshipFinderInputOutput.processAndGetBestTree scalable using a queue. You don't need it unless you need to do lots of processing simultaneously. Just call RelationshipFinderInputOutput.processAndGetBestTree directly with your local files.
 *
 * Created by Tom on 15/05/2014.
 */

public class learnAndApplyInputOutput implements Runnable{
    String uploadId;
    Logger logger;

    public learnAndApplyInputOutput(Map jobPayload){
        System.out.println("Constructor of learnAndApplyInputOutput called with payload: "+jobPayload);
        logger = LoggerFactory.getLogger(this.getClass());
        logger.info("\"Constructor of learnAndApplyInputOutput called with payload: {} ", jobPayload);
        uploadId = ""+jobPayload.get("uploadId");
    }

    public void run(){
        System.out.println("Starting to run learnAndApplyInputOutput...");



        //Download the training data from S3.
        //Could have a processor that downloads directly from S3 instead of loading from a file so that this "controller" process never has to load the full data? Pushes some of the overhead to building the processing tree etc.
        //Could also have a different worker that takes text snippets directly from the queue and passes them to RelationshipFinder.processAndGetBestTree so uploading / downloading to S3 is avoided completely.
        try {
            //String configAsString = FileUtils.readFileToString(new File("config.json"));
            JSONObject config = QueueWorker.jsonConfig;
            String awsBucket = config.get("awsBucket").toString();

            Jedis jedis = new Jedis((String) config.get("redisHost"), ((Long)config.get("redisPort")).intValue());
            jedis.auth((String)config.get("redisPassword"));
            jedis.hset("pr-"+uploadId, "status", "Started processing.");
            JedisReporter statusReporter = new JedisReporter(jedis, "pr-"+uploadId);
            statusReporter.reportStatus("Started processing...");

            //Download data from S3.
            BasicAWSCredentials credentials = new BasicAWSCredentials((String)config.get("AWSAccessKey"), (String)config.get("AWSSecretAccessKey"));
            AmazonS3 s3 = new AmazonS3Client(credentials);

            S3Object trainIn = s3.getObject(new GetObjectRequest(awsBucket, uploadId + "/trainIn.dat"));
            File trainInFile = new File("tmp/"+ uploadId +"/trainIn.dat");
            trainInFile.getParentFile().mkdirs();
            IOUtils.copy(trainIn.getObjectContent(), new FileOutputStream(trainInFile));

            S3Object trainOut = s3.getObject(new GetObjectRequest(awsBucket, uploadId + "/trainOut.dat"));
            File trainOutFile = new File("tmp/"+ uploadId +"/trainOut.dat");
            IOUtils.copy(trainOut.getObjectContent(), new FileOutputStream(trainOutFile) );

            S3Object applyIn = s3.getObject(new GetObjectRequest(awsBucket, uploadId + "/applyIn.dat"));
            File applyInFile = new File("tmp/"+ uploadId +"/applyIn.dat");
            IOUtils.copy(applyIn.getObjectContent(), new FileOutputStream(applyInFile) );

            statusReporter.reportStatus("Got Data. Processing...");




            //Actually do the predicting.
            AttRelationshipGraph tree;

            if((Boolean)config.get("production")) {
                //Do not create meta-model (faster).
                tree = RelationshipFinderInputOutput.processAndGetBestTree("tmp/" + uploadId + "/trainIn.dat", "tmp/" + uploadId + "/trainOut.dat", statusReporter, null);
            }
            else{
                tree = RelationshipFinderInputOutput.processAndGetBestTree("tmp/" + uploadId + "/trainIn.dat", "tmp/" + uploadId + "/trainOut.dat", statusReporter);
            }

            if( tree.getFinalOutputAtts().size() == 0 ) {
                statusReporter.reportStatus("Failed to find a converter from training-input-sample to target.");
                jedis.hset("pr-" + uploadId, "predictions", "0");
            }
            else{
                statusReporter.reportStatus("Trained Converter. Applying same conversion to entire file...");
                Collection<String> predictions = RelationshipFinderInputOutput.applyTree("tmp/" + uploadId + "/applyIn.dat", tree);
                statusReporter.reportStatus("Made Predictions.");

                //Save predictions to S3.
                Set<String> uniquePredictions = new HashSet<>(predictions);
                int predictionIdx = 0;
                for (String prediction : uniquePredictions) {
                    predictionIdx++;
                    InputStream predictionStream = new ByteArrayInputStream(prediction.getBytes("UTF-8"));
                    PutObjectRequest putRequest = new PutObjectRequest(awsBucket, uploadId + "/applyOut-" + predictionIdx + ".dat", predictionStream, new ObjectMetadata());
                    putRequest.setCannedAcl(CannedAccessControlList.PublicRead);
                    s3.putObject(putRequest);
                }
                if (uniquePredictions.size() > 0) {
                    statusReporter.reportStatus("Finished predictions.");
                } else {
                    statusReporter.reportStatus("Failed to create any predictions.");
                }
                jedis.hset("pr-" + uploadId, "predictions", "" + uniquePredictions.size());
            }

            jedis.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
