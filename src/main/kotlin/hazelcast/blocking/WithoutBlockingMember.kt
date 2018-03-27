package hazelcast.blocking

import com.hazelcast.config.Config
import com.hazelcast.core.Hazelcast


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
    map.put(key, 0)
    println("Starting")
    for (k in 0..999) {
        if (k % 10 == 0) println("At: " + k)
        val value = map[key]
        Thread.sleep(10)
        value!!.inc()
        map.put(key, value)
    }
    println("Finished! Result = " + map[key])
}
