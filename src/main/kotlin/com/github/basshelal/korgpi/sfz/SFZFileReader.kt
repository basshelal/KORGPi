package com.github.basshelal.korgpi.sfz

import java.io.FileReader

class SFZFileReader(val filePath: String) {

    val reader = FileReader(filePath)

    val lines = mutableListOf<String>()

    val headers = mutableListOf<SFZHeader>()

    init {
        lines.clear()
        lines += reader.readLines().filterNot { it.isComment() || it.isEmpty() || it.isBlank() }
        var currentHeader: SFZHeader? = null
        lines.forEach { line: String ->
            // New header
            if (line.startsWith("<")) {
                // old header has finished
                currentHeader?.also {
                    headers.add(it)
                    currentHeader = null
                }
                val headerName = line.removeSurrounding(prefix = "<", suffix = ">")
                currentHeader = SFZHeader.from(headerName)
            } else {
                // Some opcode
                currentHeader?.also {
                    val opcodeKey = line.substringBefore("=")
                    val opcodeValue = line.substringAfter("=")
                    it.opcodes.add(SFZOpcode.from(opcodeKey, opcodeValue))
                }
                // TODO: 13/01/2021 Handle this correctly
                //  Some hanging opcode without a header such as #define
            }

        }
    }

    private fun String.isComment(): Boolean = this.startsWith("//")

}