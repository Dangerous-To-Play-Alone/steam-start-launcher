package VDF

import VDF.VDFElement
import java.util.HashMap
import VDF.VDFKey
import kotlin.jvm.JvmOverloads
import VDF.VDFBaseElement

open class VDFBaseElement {
    private val MapParent: MutableMap<String, VDFElement> = HashMap()
    private val MapKey: MutableMap<String?, String> = HashMap()
    fun addKey(Name: String?, Value: Boolean) {
        this.addKey(Name, if (Value) "1" else "0")
    }

    fun addKey(Name: String?, Value: Int) {
        this.addKey(Name, Value.toString())
    }

    fun addKey(Name: String?, Value: Long) {
        this.addKey(Name, Value.toString())
    }

    fun addKey(Name: String?, Value: String) {
        MapKey[Name] = Value
    }

    fun getKey(Name: String): VDFKey {
        return VDFKey(MapKey[Name]!!)
    }

    val keys: Array<String?>
        get() {
            val Keys = arrayOfNulls<String>(MapKey.size)
            var i = 0
            for (Key in MapKey.keys) {
                Keys[i] = Key
                i++
            }
            return Keys
        }

    @JvmOverloads
    fun addParent(Name: String, Parent: VDFElement? = null): VDFElement {
        val Element = VDFElement(Name, Parent)
        MapParent[Name] = Element
        return Element
    }

    fun getParent(Name: String): VDFElement? {
        return MapParent[Name]
    }

    val parents: Array<VDFElement?>
        get() {
            val Parents = arrayOfNulls<VDFElement>(MapParent.size)
            var i = 0
            for (Key in MapParent.keys) {
                Parents[i] = MapParent[Key]
                i++
            }
            return Parents
        }

    protected fun toString(tabNumber: Int): String {
        var OutputVDF = keysToString(this, tabNumber)
        OutputVDF = OutputVDF + parentsToString(this, tabNumber)
        return OutputVDF
    }

    override fun toString(): String {
        return this.toString(0)
    }

    companion object {
        private fun generateTabs(number: Int): String {
            var Tabs = ""
            for (t in 0 until number) {
                Tabs = Tabs + "\t"
            }
            return Tabs
        }

        private fun keysToString(Element: VDFBaseElement, tabNumber: Int): String {
            var OutputKeys = ""
            for (i in Element.keys.indices) {
                val Key = Element.keys[i]
                OutputKeys = """$OutputKeys${generateTabs(tabNumber)}"$Key"		"${Element.getKey(Key!!)}"
"""
            }
            return OutputKeys
        }

        private fun parentsToString(Element: VDFBaseElement, tabNumber: Int): String {
            var OutputParents = ""
            for (i in Element.parents.indices) {
                val Parent: VDFElement? = Element.parents[i]
                OutputParents = """
                    $OutputParents${generateTabs(tabNumber)}"${Parent!!.name}"
                    ${generateTabs(tabNumber)}{${generateTabs(tabNumber)}
                    ${Parent.toString(tabNumber + 1)}${generateTabs(tabNumber)}}
                    
                    """.trimIndent()
            }
            return OutputParents
        }
    }
}