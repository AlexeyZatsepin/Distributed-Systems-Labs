package hazelcast.transactions

import com.hazelcast.config.Config
import com.hazelcast.transaction.TransactionOptions
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
    val hazelcastInstance = Hazelcast.newHazelcastInstance(cfg)

    val options = TransactionOptions()
            .setTransactionType(TransactionOptions.TransactionType.TWO_PHASE)

    val context = hazelcastInstance.newTransactionContext(options)

    context.beginTransaction()

    val map = context.getMap<String, String>("mymap")
    val set = context.getSet<String>("myset")

    try {
        map.put("Alexey2", "commit2")
        set.add("Alexey2")
//        throw Throwable()
        context.commitTransaction()
        println("commited all")
    } catch (t: Throwable) {
        context.rollbackTransaction()
        println("rollback")
    }

    val ex = Executors.newSingleThreadScheduledExecutor()
    ex.scheduleAtFixedRate({
        println("Client " + Thread.currentThread().id + " Map Size:" + map.size())
    }, 0, 2, TimeUnit.SECONDS)
}