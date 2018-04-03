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

    val queueConfig = cfg.getQueueConfig("default")
    queueConfig.setName("MyQueueConfig")
            .setBackupCount(1)
            .setMaxSize(10)
            .setStatisticsEnabled(true).quorumName = "quorum-name"

    cfg.addQueueConfig(queueConfig)

    val hz = Hazelcast.newHazelcastInstance(cfg)
    val queue = hz.getQueue<Int>("queue")
    while (true) {
        val item = queue.take()
        println("Consumed: $item")
        if (item == -1) {
            queue.put(-1)
            break
        }
        Thread.sleep(5000)
    }
    println("Consumer Finished!")
}