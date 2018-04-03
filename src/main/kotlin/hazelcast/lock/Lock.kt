package hazelcast.lock

import com.hazelcast.config.Config
import com.hazelcast.core.Hazelcast
import java.util.*


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
    val hazelcastInstance = Hazelcast.newHazelcastInstance(cfg)
    val lock = hazelcastInstance.getLock("myLock")
    while (true) {
        lock.lock()
        try {
            println("Input: ")
            val sc = Scanner(System.`in`)
            val i = sc.nextInt()
            println(i)
            sc.close()
        } finally {
            lock.unlock()
        }
    }
}