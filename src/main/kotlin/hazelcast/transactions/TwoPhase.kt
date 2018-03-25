package hazelcast.transactions

import com.hazelcast.client.HazelcastClient
import javax.transaction.xa.XAResource


fun main(args: Array<String>) {
    var client = HazelcastClient.newHazelcastClient()

    var tm = // TODO
    tm.setTransactionTimeout(60)
    tm.begin()


    var xaResource = client.xaResource
    var transaction = tm.getTransaction()
    transaction.enlistResource(xaResource)

    try{
        var context = xaResource.transactionContext
        var map = context.getMap<String,String>("map")
        map.put("key","value")
        val queue=context.getQueue<String>("queue")
        queue.offer("item")

        transaction.delistResource(xaResource, XAResource.TMSUCCESS)
        tm.commit()
    }catch(e:Throwable){
        e.printStackTrace()
        transaction.delistResource(xaResource,XAResource.TMFAIL)
        tm.rollback()
    }

}