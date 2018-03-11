import org.postgresql.xa.PGXADataSource
import java.sql.SQLException
import java.sql.Connection
import java.sql.Statement
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
}


fun getFlyDatabase(): PGXADataSource {
    val xaDataSourceFly = PGXADataSource()
    xaDataSourceFly.databaseName = "flydb"
    xaDataSourceFly.user = USER
    xaDataSourceFly.password = PASSWORD
    xaDataSourceFly.logLevel = 2 // log level DEBUG = 2, INFO = 1
    return xaDataSourceFly
}

fun getHotelDatabase(): PGXADataSource {
    val xaDataSourceFly = PGXADataSource()
    xaDataSourceFly.databaseName = "hoteldb"
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

    val xidFly = XidEx(100, byteArrayOf(0x01), byteArrayOf(0x02))
    val xidHotel = XidEx(100, byteArrayOf(0x01), byteArrayOf(0x22))

    try {
        flyConnXA = getFlyDatabase().xaConnection
        flyConn = flyConnXA!!.connection
        flyXAResource = flyConnXA.xaResource
        flyStatement = flyConn!!.createStatement()

        hotelConnXA = getHotelDatabase().xaConnection
        hotelConn = hotelConnXA!!.connection
        hotelXAResource = hotelConnXA.xaResource
        hotelStatement = hotelConn!!.createStatement()
    } catch (e: SQLException) {
        e.printStackTrace()
        return
    }

    try {
        flyXAResource!!.start(xidFly, TMNOFLAGS)
        hotelXAResource!!.start(xidHotel, TMNOFLAGS)

        flyStatement!!.execute("INSERT INTO flight_booking VALUES (9,'1','1','1','1',CURRENT_TIMESTAMP)")
        hotelStatement!!.execute("INSERT INTO hotel_booking VALUES (9,'1','1',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP)")

        flyXAResource.end(xidFly, TMSUCCESS)
        hotelXAResource.end(xidHotel, TMSUCCESS)

        val ret1 = flyXAResource.prepare(xidFly)
        val ret2 = hotelXAResource.prepare(xidHotel)
        if ((ret1 == XAResource.XA_OK) and (ret2 == XAResource.XA_OK)) {
            flyXAResource.commit(xidFly, false)
            hotelXAResource.commit(xidHotel, false)
        } else {
            flyXAResource.rollback(xidFly)
            hotelXAResource.rollback(xidHotel)
        }
    } catch (exception: SQLException) {
        exception.printStackTrace()
        //handle by hand
        //for master

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

        } catch (e: SQLException) {
            e.printStackTrace()
        }

    }
}