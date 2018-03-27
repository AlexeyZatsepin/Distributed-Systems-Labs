import java.io.Serializable

class Value : Serializable {
    var serialVersionUID: Long = 100
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