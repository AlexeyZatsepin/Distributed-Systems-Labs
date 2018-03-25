package hazelcast.blocking

import com.hazelcast.core.Hazelcast


fun main(args: Array<String>) {
    val hz = Hazelcast.newHazelcastInstance()
    val map = hz.getMap<String, Value>("map")
    val key = "1"
    map.put(key, Value())
    println("Starting")
    for (k in 0..999) {
        if (k % 10 == 0) println("At: " + k)
        val value = map[key]
        Thread.sleep(10)
        value!!.amount++
        map.put(key, value)
    }
    println("Finished! Result = " + map[key]!!.amount)
}
