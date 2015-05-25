package database.worker;

import com.rapidminer.RapidMiner;
import com.rapidminer.tools.LogService;
import net.greghaines.jesque.Config;
import net.greghaines.jesque.ConfigBuilder;
import net.greghaines.jesque.utils.JesqueUtils;
import net.greghaines.jesque.worker.MapBasedJobFactory;
import net.greghaines.jesque.worker.Worker;
import net.greghaines.jesque.worker.WorkerImpl;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class QueueWorker {
    public static JSONObject jsonConfig;

    public static void main( String[] args ){
        System.out.println("Running QueueWorker.main() using config file:" + args[0]);

        Logger logger = LoggerFactory.getLogger("QueueWorker");
        System.out.println("Starting worker.");
        logger.info("Starting worker...");

        //Init RapidMiner so it doesn't get initialized (slow) on the first job received from queue.
        LogService rmlogger = LogService.getGlobal();
        RapidMiner.setExecutionMode(RapidMiner.ExecutionMode.COMMAND_LINE);
        rmlogger.setVerbosityLevel(LogService.ERROR);
        RapidMiner.init();

        try {
            String configAsString = FileUtils.readFileToString(new File(args[0]));
            jsonConfig = (JSONObject) JSONValue.parse(configAsString);

            Config redisConfig = new ConfigBuilder().withHost( (String) jsonConfig.get("redisHost")).withPort(((Long)jsonConfig.get("redisPort")).intValue()).withPassword( (String) jsonConfig.get("redisPassword")).build();

            Map<String, Class<?>> actionToClassMap = new HashMap<>();
            actionToClassMap.put("learnAndApplyInputOutput", learnAndApplyInputOutput.class);

            Worker worker = new WorkerImpl(redisConfig, Arrays.asList("learnAndApplyInputOutput"), new MapBasedJobFactory(JesqueUtils.map(JesqueUtils.entry("learnAndApplyInputOutput", learnAndApplyInputOutput.class))));
            //new MapBasedJobFactory(actionToClassMap) );
            final Thread workerThread = new Thread(worker);
            workerThread.start();
        }
        catch( IOException e ){
            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }

}
