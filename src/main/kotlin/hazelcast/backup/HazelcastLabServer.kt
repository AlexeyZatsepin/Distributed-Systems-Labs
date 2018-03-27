package hazelcast.backup

import com.hazelcast.config.Config
import com.hazelcast.core.Hazelcast
import com.hazelcast.core.IMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import com.hazelcast.config.MapConfig


fun main(args: Array<String>) {
    val cfg = Config()

    val network = cfg.networkConfig
    network.setPort(5701).isPortAutoIncrement = true

    val join = network.join
    join.multicastConfig.isEnabled = false
    join.tcpIpConfig
            .setEnabled(true)
            .addMember("192.168.43.128")
            .addMember("192.168.43.230")
            .addMember("192.168.43.195")
            .addMember("192.168.43.27")

    val mapCfg = MapConfig()
    val mapName = "customers"
    mapCfg.name = mapName
    mapCfg.backupCount = 4

    cfg.addMapConfig(mapCfg)

    val instance = Hazelcast.newHazelcastInstance(cfg)

    val map: IMap<String, String> = instance.getMap(mapName)


    for (i in 0 .. 9999) {
        map.put("Alexey" + i,"Alexey")
    }
    val ex = Executors.newSingleThreadScheduledExecutor()
    ex.scheduleAtFixedRate({
        System.out.println("Server Map Size:" + map.size)
    }, 0, 5, TimeUnit.SECONDS)
}
