package hazelcast.replication

import com.hazelcast.config.Config
import com.hazelcast.config.InMemoryFormat
import com.hazelcast.core.Hazelcast
import com.hazelcast.core.EntryEvent
import com.hazelcast.core.EntryListener
import com.hazelcast.core.MapEvent


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
    val replicatedMapConfig = cfg.getReplicatedMapConfig("customers")
    replicatedMapConfig.inMemoryFormat = InMemoryFormat.BINARY
    val hazelcastInstance = Hazelcast.newHazelcastInstance(cfg)

    val data = hazelcastInstance.getReplicatedMap<String, String>("customers")
    data.addEntryListener(object : EntryListener<String, String> {
        override fun mapEvicted(event: MapEvent?) {
            println("Map evicted: $event")
        }

        override fun mapCleared(event: MapEvent?) {
            println("Map cleared: $event")
        }

        override fun entryAdded(event: EntryEvent<String, String>) {
            println("Entry added: $event")
        }

        override fun entryUpdated(event: EntryEvent<String, String>) {
            println("Entry updated: $event")
        }

        override fun entryRemoved(event: EntryEvent<String, String>) {
            println("Entry removed: $event")
        }

        override fun entryEvicted(event: EntryEvent<String, String>) {
            // Currently not supported, will never fire
        }
    })

    data["Alexey"] = "v1" // add event
    data["Alexey"] = "v2" // add event
}