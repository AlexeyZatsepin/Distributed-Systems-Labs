package hazelcast.blocking

import java.io.Serializable
import com.hazelcast.core.Hazelcast

fun main(args: Array<String>) {
    val hz = Hazelcast.newHazelcastInstance()
    val map = hz.getMap<String, Value>("map")
    val key = "1"
    map.put(key, Value())
    println("Starting")
    for (k in 0..999) {
        if (k % 10 == 0) println("At: " + k)
        while (true) {
            val oldValue = map[key]
            val newValue = Value(oldValue)
            Thread.sleep(10)
            newValue.amount++
            if (map.replace(key,oldValue,newValue))
                break
        }
    }
    println("Finished! Result = " + map[key]!!.amount)
}

internal class Value : Serializable {
    var amount: Int = 0

    constructor()

    constructor(that: Value?) {
        this.amount = that!!.amount
    }

    override fun equals(o: Any?): Boolean {
        if (o === this) return true
        if (o !is Value) return false
        val that = o as Value?
        return that!!.amount == this.amount
    }

    override fun hashCode(): Int {
        return amount
    }
}