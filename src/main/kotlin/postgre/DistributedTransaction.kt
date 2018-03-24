package postgre

import org.postgresql.xa.PGXADataSource
import java.sql.SQLException
import java.sql.Connection
import java.sql.Statement
import java.util.*
import javax.transaction.xa.XAException
import javax.transaction.xa.XAResource
import javax.transaction.xa.XAResource.TMSUCCESS
import javax.transaction.xa.XAResource.TMNOFLAGS
import javax.sql.XAConnection
import javax.transaction.xa.Xid

val USER = "a.zatsepin"
val PASSWORD = "alexzatsepin"

data class XidEx(val format: Int, val gtrid: ByteArray, val bqual: ByteArray) : Xid {
    override fun getBranchQualifier(): ByteArray {
        return bqual
    }

    override fun getGlobalTransactionId(): ByteArray {
        return gtrid
    }

    override fun getFormatId(): Int {
        return format
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as XidEx
        if (format != other.format) return false
        if (!Arrays.equals(gtrid, other.gtrid)) return false
        if (!Arrays.equals(bqual, other.bqual)) return false
        return true
    }

    override fun hashCode(): Int {
        var result = format
        result = 31 * result + Arrays.hashCode(gtrid)
        result = 31 * result + Arrays.hashCode(bqual)
        return result
    }
}

fun getDatabase(dbname:String): PGXADataSource {
    val xaDataSourceFly = PGXADataSource()
    xaDataSourceFly.databaseName = dbname
    xaDataSourceFly.user = USER
    xaDataSourceFly.password = PASSWORD
    xaDataSourceFly.logLevel = 2 // log level DEBUG = 2, INFO = 1
    return xaDataSourceFly
}

fun main(args: Array<String>) {
    val flyConnXA: XAConnection?
    val flyConn: Connection?
    val flyXAResource: XAResource?
    val flyStatement: Statement?
    val hotelConnXA: XAConnection?
    val hotelConn: Connection?
    val hotelXAResource: XAResource?
    val hotelStatement: Statement?
    val accountConnXA: XAConnection?
    val accountConn: Connection?
    val accountXAResource: XAResource?
    val accountStatement: Statement?

    val xidFly = XidEx(100, byteArrayOf(0x01), byteArrayOf(0x02))
    val xidHotel = XidEx(100, byteArrayOf(0x01), byteArrayOf(0x12))
    val xidAccount = XidEx(100, byteArrayOf(0x01), byteArrayOf(0x22))

    try {
        flyConnXA = getDatabase("flydb").xaConnection
        flyConn = flyConnXA!!.connection
        flyXAResource = flyConnXA.xaResource
        flyStatement = flyConn!!.createStatement()

        hotelConnXA = getDatabase("hoteldb").xaConnection
        hotelConn = hotelConnXA!!.connection
        hotelXAResource = hotelConnXA.xaResource
        hotelStatement = hotelConn!!.createStatement()

        accountConnXA = getDatabase("accountdb").xaConnection
        accountConn = accountConnXA!!.connection
        accountXAResource = accountConnXA.xaResource
        accountStatement = accountConn!!.createStatement()
    } catch (e: SQLException) {
        e.printStackTrace()
        return
    }

    try {
        flyXAResource!!.start(xidFly, TMNOFLAGS)
        hotelXAResource!!.start(xidHotel, TMNOFLAGS)
        accountXAResource!!.start(xidAccount, TMNOFLAGS)

        flyStatement!!.execute("INSERT INTO flight_booking VALUES (5,'1','1','1','1',CURRENT_TIMESTAMP)")
        hotelStatement!!.execute("INSERT INTO hotel_booking VALUES (5,'1','1',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP)")
        accountStatement!!.execute("UPDATE accounts SET AMOUNT = AMOUNT-30 WHERE ID=0")

        flyXAResource.end(xidFly, TMSUCCESS)
        hotelXAResource.end(xidHotel, TMSUCCESS)
        accountXAResource.end(xidAccount, TMSUCCESS)

        val ret1 = flyXAResource.prepare(xidFly)
        val ret2 = hotelXAResource.prepare(xidHotel)
        val ret3 = accountXAResource.prepare(xidAccount)
        if ((ret1 == XAResource.XA_OK) and (ret2 == XAResource.XA_OK) and (ret3 == XAResource.XA_OK)) {
            flyXAResource.commit(xidFly, false)
            hotelXAResource.commit(xidHotel, false)
            accountXAResource.commit(xidAccount, false)
        } else {
            flyXAResource.rollback(xidFly)
            hotelXAResource.rollback(xidHotel)
            accountXAResource.rollback(xidAccount)
        }
    } catch (exception: SQLException) {
        exception.printStackTrace()
    } catch (exception: XAException) {
        exception.printStackTrace()
    } finally {
        try {
            flyStatement!!.close()
            flyConn.close()
            flyConnXA.close()

            hotelStatement!!.close()
            hotelConn.close()
            hotelConnXA.close()

            accountStatement!!.close()
            accountConn.close()
            accountConnXA.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        }

    }
}