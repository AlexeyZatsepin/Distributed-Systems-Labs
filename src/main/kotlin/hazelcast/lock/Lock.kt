package hazelcast.lock

import com.hazelcast.config.Config
import com.hazelcast.core.Hazelcast
import java.util.*
import com.hazelcast.config.LockConfig
import com.hazelcast.config.QuorumConfig
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import com.hazelcast.config.QuorumListenerConfig


fun main(args: Array<String>) {
    val cfg = Config()

    val quorumConfig = QuorumConfig()
    quorumConfig.name = "quorum-name"
    quorumConfig.isEnabled = true
    quorumConfig.size = 3
    val listenerConfig = QuorumListenerConfig()
    listenerConfig.setImplementation { quorumEvent ->
        if (quorumEvent.isPresent) {
            println("quorum is present")
        } else {
            println("quorum is lost")
        }
    }
    quorumConfig.addListenerConfig(listenerConfig)
    cfg.addQuorumConfig(quorumConfig)

    val lockConfig = LockConfig()
    lockConfig.setName("myLock").quorumName = "quorum-name"
    cfg.addLockConfig(lockConfig)

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


    Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate({
        val quorumService = hazelcastInstance.quorumService
        val quorum = quorumService.getQuorum("quorumRuleWithTwoMembers")
        val quorumPresence = quorum.isPresent
        println("Quorum now :$quorumPresence")
    }, 1, 1, TimeUnit.SECONDS)

    while (true) {
        try {
            lock.lock()
            println("locked")
            println("Input: ")
            val sc = Scanner(System.`in`)
            val i = sc.nextLine()
            println("processed $i")
            sc.close()
        } finally {
            lock.unlock()
            println("unlocked")
        }
    }
}