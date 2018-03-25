package hazelcast.replication

import com.hazelcast.config.Config
import com.hazelcast.config.InMemoryFormat
import com.hazelcast.core.Hazelcast
import com.hazelcast.core.EntryEvent
import com.hazelcast.core.EntryListener
import com.hazelcast.core.MapEvent


fun main(args: Array<String>) {
    val config = Config()
    val replicatedMapConfig = config.getReplicatedMapConfig("replica")
    replicatedMapConfig.inMemoryFormat = InMemoryFormat.BINARY
    val hazelcastInstance = Hazelcast.newHazelcastInstance()

    val data = hazelcastInstance.getReplicatedMap<String, String>("replica")
    data.addEntryListener(object : EntryListener<String, String> {
        override fun mapEvicted(event: MapEvent?) {
            println("Map evicted: " + event)
        }

        override fun mapCleared(event: MapEvent?) {
            println("Map cleared: " + event)
        }

        override fun entryAdded(event: EntryEvent<String, String>) {
            println("Entry added: " + event)
        }

        override fun entryUpdated(event: EntryEvent<String, String>) {
            println("Entry updated: " + event)
        }

        override fun entryRemoved(event: EntryEvent<String, String>) {
            println("Entry removed: " + event)
        }

        override fun entryEvicted(event: EntryEvent<String, String>) {
            // Currently not supported, will never fire
        }
    })

    data.put("1", "1") // add event
    data.put("1", "2") // update event
    data.remove("1")

}