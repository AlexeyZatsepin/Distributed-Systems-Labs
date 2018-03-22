import com.hazelcast.config.Config
import com.hazelcast.core.Hazelcast
import com.hazelcast.core.IMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import com.hazelcast.config.MapConfig

fun main(args: Array<String>) {
    val cfg = Config()

    val mapCfg = MapConfig()
    mapCfg.name = "customers"
    mapCfg.backupCount = 0

    cfg.addMapConfig(mapCfg)

    val instance = Hazelcast.newHazelcastInstance(cfg)

    val map: IMap<Int, String> = instance.getMap("customers")

    val ex = Executors.newSingleThreadScheduledExecutor()
    ex.scheduleAtFixedRate({
        System.out.println("Server Map Size:" + map.size)
    }, 0, 5, TimeUnit.SECONDS)
}
