package VDF

class VDFKey(private val Value: String) {
    override fun toString(): String {
        return Value
    }

    fun toBoolean(): Boolean {
        return if (Value == null || Value == "0") false else true
    }

    fun toInt(): Int {
        return Value.toInt()
    }

    fun toLong(): Long {
        return Value.toLong()
    }
}