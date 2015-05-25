package database.worker;

import redis.clients.jedis.Jedis;

/**
 * Created by Tom on 25/09/2014.
 */
public class JedisReporter extends StatusReporter {
    private Jedis jedis;
    private String redisKey;

    public JedisReporter(Jedis jedis, String redisKey){
        this.jedis = jedis;
        this.redisKey = redisKey;
    }

    @Override
    public void reportStatus(String message) {
        jedis.hset(redisKey, "status", message);
    }
}
