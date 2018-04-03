package hazelcast.queue

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
    val queue = hz.getQueue<Int>("queue")
    for (k in 1..99) {
        queue.put(k)
        println("Producing: $k")
        Thread.sleep(1000)
    }
    queue.put(-1)
    println("Producer Finished!")
}