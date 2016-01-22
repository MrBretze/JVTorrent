package fr.bretzel.jvtorrent.log

import com.jfoenix.controls.JFXTextArea
import org.apache.log4j.Level
import org.apache.log4j.Logger
import java.io.IOException
import java.io.OutputStream
import java.util.*

/**
 * Created by MrBretzle on 22/01/2016.
 */

class LogOutputStream : OutputStream {

    private var logger: Logger = Logger.getLogger("xdfgfdgqzerf")

    private var level: Level = Level.INFO

    private final var DEFAULT_BUFFER_LENGHT: Int = 2048

    private var hasBeenClosed: Boolean = false

    private var buf: ByteArray = ByteArray(2048)

    private var count: Int = 0

    private var currentBufLenght = 0

    private var oneByte: ByteArray = ByteArray(0)

    constructor(logger: Logger) {
        this.logger = logger
        this.level = logger.level

        currentBufLenght = DEFAULT_BUFFER_LENGHT
        buf = ByteArray(currentBufLenght)
        count = 0
        oneByte = ByteArray(1)
    }

    override fun write(b: Int) {
        if (hasBeenClosed) {
            throw IOException("The stream has been closed.")
        }

        if (b == 0) {
            return
        }

        if (count == currentBufLenght) {
            val newBugLength: Int = currentBufLenght + DEFAULT_BUFFER_LENGHT
            val newBuf: ByteArray = ByteArray(newBugLength)
            System.arraycopy(buf, 0, newBuf, 0, currentBufLenght)
            buf = newBuf
            currentBufLenght = newBugLength
        }

        buf[count] = b.toByte()
        count++
    }

    override fun flush() {
        if (count == 0) {
            return
        }

        val bytes: ByteArray = ByteArray(count)
        System.arraycopy(buf, 0, bytes, 0, count)
        val str: String = String(bytes)
        logger.log(level, str)
        count = 0
    }

    override fun close() {
        flush()
        hasBeenClosed = true
    }

    class Appender: Runnable {

        private var area: JFXTextArea? = null

        private var lengths: LinkedList<Int> = LinkedList()

        private var values: ArrayList<String> = ArrayList()

        private var curLength: Int = 0

        private var clear: Boolean = false

        private var queue: Boolean = true

        constructor(area: JFXTextArea) {
            this.area = area
            lengths = LinkedList<Int>()
            values = ArrayList<String>()

            curLength = 0
            clear = false
            queue = true
        }

        override fun run() {

        }
    }
}