import com.hazelcast.core.Hazelcast
import com.hazelcast.core.IMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


fun main(args: Array<String>) {
    val instance = Hazelcast.newHazelcastInstance()

    val map: IMap<Int, String> = instance.getMap("customers")

    val ex = Executors.newSingleThreadScheduledExecutor()
    ex.scheduleAtFixedRate({
        System.out.println("Server Map Size:" + map.size)
    }, 0, 10, TimeUnit.SECONDS)
}
