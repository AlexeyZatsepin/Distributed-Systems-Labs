package hazelcast

import com.hazelcast.client.HazelcastClient
import com.hazelcast.client.config.ClientConfig
import com.hazelcast.config.Config
import com.hazelcast.config.MapConfig
import com.hazelcast.core.IMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


fun main(args: Array<String>) {
    val client = HazelcastClient.newHazelcastClient()
    val map: IMap<Int, String> = client.getMap("customers")

    for (i in 1..1000)  {
        map.put(System.currentTimeMillis().toInt(), "value" + i + System.currentTimeMillis())
        Thread.sleep(10)
    }
    val ex = Executors.newSingleThreadScheduledExecutor()
    ex.scheduleAtFixedRate({
        System.out.println("Client"+ Thread.currentThread().id +" Map Size:" + map.size)
    }, 0, 2, TimeUnit.SECONDS)

}
