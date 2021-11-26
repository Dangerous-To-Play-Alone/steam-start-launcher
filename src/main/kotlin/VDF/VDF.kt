package VDF

import VDF.VDFBaseElement
import VDF.VDF
import VDF.VDFElement
import java.io.*
import java.util.ArrayList
import kotlin.Throws
import kotlin.jvm.JvmOverloads

class VDF : VDFBaseElement {
    constructor() : super() {}
    constructor(file: String?) : this(File(file)) {}
    constructor(file: File) : this(fileToLine(file)) {}
    constructor(VDFLine: Array<String?>) {
        var ElementName: String? = null
        var Element: VDFElement? = null
        for (i in VDFLine.indices) {
            val Line = VDFLine[i]!!.trim { it <= ' ' }.split("\t\t").toTypedArray()
            if (Line[0].startsWith("\"") && Line[0].endsWith("\"")) {
                if (Line.size == 2) {
                    if (Element == null) this.addKey(removeQuote(Line[0]), removeQuote(Line[1])) else Element.addKey(
                        removeQuote(
                            Line[0]
                        ), removeQuote(Line[1])
                    )
                } else if (Line.size == 1) {
                    ElementName = removeQuote(Line[0])
                }
            } else if (Line[0].contains("{")) {
                Element = if (Element == null) {
                    this.addParent(ElementName!!, null)
                } else {
                    Element.addParent(ElementName!!, Element)
                }
            } else if (Line[0].contains("}")) {
                Element = if (Element!!.base == null) null else Element.base
            }
        }
    }

    @Throws(IOException::class)
    fun Save(file: String?) {
        this.Save(File(file))
    }

    @JvmOverloads
    @Throws(IOException::class)
    fun Save(file: File? = Companion.file) {
        val BufferWriter = BufferedWriter(FileWriter(file))
        BufferWriter.write(this.toString())
        BufferWriter.close()
    }

    companion object {
        var file: File? = null

        @Throws(IOException::class)
        private fun fileToLine(file: File): Array<String?> {
            Companion.file = file
            val BufferLine = ArrayList<String?>()
            val BufferReader = BufferedReader(FileReader(file))
            var LineRead: String? = null
            while (BufferReader.readLine().also { LineRead = it } != null) {
                BufferLine.add(LineRead)
            }
            BufferReader.close()
            val VDFLine = arrayOfNulls<String>(BufferLine.size)
            return BufferLine.toArray(VDFLine)
        }

        private fun removeQuote(str: String): String {
            return str.replace("\"", "")
        }
    }
}