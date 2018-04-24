package hazelcast.generator

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
    val idGen = hz.getIdGenerator( "newId" )
    while (true) {
        val id = idGen.newId()
        println("Id: $id")
        Thread.sleep( 1000 )
    }
}