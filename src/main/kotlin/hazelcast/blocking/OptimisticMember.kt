package hazelcast.blocking

import com.hazelcast.config.Config
import Value
import com.hazelcast.core.Hazelcast
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

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
    val hz = Hazelcast.newHazelcastInstance(cfg)
    val map = hz.getMap<String, Int>("map")
    val key = "1"
    map.putIfAbsent(key, 0)
    println("Starting")
    for (k in 0..999) {
        if (k % 10 == 0) println("At: " + k)
        while (true) {
            val oldValue = map[key]
            var newValue = oldValue!!
            Thread.sleep(1)
            newValue++
//            map.replace(key,oldValue,newValue)
            if (map.replace(key,oldValue,newValue))
                break
//        }
    }
    }
    println("Finished! Result = " + map[key])
    val ex = Executors.newSingleThreadScheduledExecutor()
    ex.scheduleAtFixedRate({
        System.out.println("Result: " + map[key])
    }, 0, 5, TimeUnit.SECONDS)
}