package hazelcast.topic

import MyEvent
import com.hazelcast.config.Config
import com.hazelcast.core.Hazelcast
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


private val messageExecutor = Executors.newSingleThreadExecutor()

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
    val topic = hazelcastInstance.getTopic<Any>("default")
    topic.addMessageListener({
            val myEvent = it.messageObject as MyEvent
            println("Message received = " + myEvent.toString())
            if (myEvent.isHeavyweight()) {
                messageExecutor.execute({ doHeavyweightStuff(myEvent)})
            }
    })
    topic.publish(MyEvent("Alexey"))

    val ex = Executors.newSingleThreadScheduledExecutor()
    ex.scheduleAtFixedRate({
        topic.publish(MyEvent("Alexey"))
    }, 0, 5, TimeUnit.SECONDS)
}

fun doHeavyweightStuff(myEvent: MyEvent) {
    println("ok")
    Thread.sleep(1000)
}
