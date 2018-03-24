package hazelcast

import com.hazelcast.config.Config
import com.hazelcast.core.Hazelcast
import com.hazelcast.core.IMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import com.hazelcast.config.MapConfig


fun main(args: Array<String>) {
    val config = Config()
    val network = config.networkConfig
    network.setPort(5701).portCount = 20
    network.isPortAutoIncrement = true
    val join = network.join
    join.multicastConfig.isEnabled = false
    join.tcpIpConfig
            .addMember("machine1")
            .addMember("localhost").isEnabled = true

    val mapCfg = MapConfig()
    mapCfg.name = "customers"
    mapCfg.backupCount = 0

    config.addMapConfig(mapCfg)

    val instance = Hazelcast.newHazelcastInstance(config)

    val map: IMap<Int, String> = instance.getMap("customers")

    val ex = Executors.newSingleThreadScheduledExecutor()
    ex.scheduleAtFixedRate({
        System.out.println("Server Map Size:" + map.size)
    }, 0, 5, TimeUnit.SECONDS)
}
