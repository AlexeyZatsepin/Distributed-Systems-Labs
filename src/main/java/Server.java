import com.hazelcast.config.*;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Server {
    public static void main(String[] args) {
        Config config = new Config();
        NetworkConfig network = config.getNetworkConfig();
        network.setPort(5701).setPortCount(20);
        network.setPortAutoIncrement(true);
        JoinConfig join = network.getJoin();
        join.getMulticastConfig().setEnabled(false);
        join.getTcpIpConfig()
                .addMember("machine1")
                .addMember("localhost").setEnabled(true);
        MapConfig mapCfg = new MapConfig();
        mapCfg.setName("customers");
        mapCfg.setBackupCount(0);

        config.addMapConfig(mapCfg);

        HazelcastInstance instance = Hazelcast.newHazelcastInstance(config);

        IMap<Integer, String> map = instance.getMap("customers");

        ScheduledExecutorService ex = Executors.newSingleThreadScheduledExecutor();
        ex.scheduleAtFixedRate(() -> System.out.println("Server Map Size:" + map.size()), 0, 5, TimeUnit.SECONDS);
    }
}
