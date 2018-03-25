package hazelcast.transactions

import com.hazelcast.transaction.TransactionOptions
import com.hazelcast.core.Hazelcast


fun main(args: Array<String>) {
    val hazelcastInstance = Hazelcast.newHazelcastInstance()

    val options = TransactionOptions()
            .setTransactionType(TransactionOptions.TransactionType.ONE_PHASE)

    val context = hazelcastInstance.newTransactionContext(options)

    context.beginTransaction()

    val queue = context.getQueue<Any>("queue")
    val map = context.getMap<Any, Any>("map")
    val set = context.getSet<Any>("set")

    try {
        val obj = queue.poll()
        map.put("1", "value1")
        set.add("value")
        context.commitTransaction()
        println("commited all")
    } catch (t: Throwable) {
        context.rollbackTransaction()
    }

}