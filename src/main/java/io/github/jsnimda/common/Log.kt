package io.github.jsnimda.common

import io.github.jsnimda.common.Log.LogLevel.*
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

object Log {
  private const val id = "inventoryprofiles"
  val innerLogger: Logger = LogManager.getLogger(id)

  var shouldDebug: () -> Boolean = { false }
  var shouldTrace: () -> Boolean = { false }

  fun error(message: String) = innerLogger.error("[$id] $message").also { onLog(ERROR, message) }
  fun warn(message: String) = innerLogger.warn("[$id] $message").also { onLog(WARN, message) }
  fun info(message: String) = innerLogger.info("[$id] $message").also { onLog(INFO, message) }
//  fun printDebug(message: String) = innerLogger.debug("[$id] $message")

  fun debug(message: String) = debug { message }
  fun debug(message: () -> String) {
    val messageString = message()
    if (shouldDebug()) {
      innerLogger.info("[$id/DEBUG] $messageString").also { onLog(DEBUG, messageString) }
    }
  }

  // for trace
  private var indent = 0
  fun indent() {
    indent++
  }

  fun unindent() {
    indent--
  }

  fun clearIndent() {
    indent = 0
  }

  fun trace(message: String) = trace { message }
  fun trace(message: () -> String) {
    val messageString = " ".repeat(indent * 4) + message()
    if (shouldTrace()) {
      innerLogger.info("[$id/TRACE] $messageString").also { onLog(TRACE, messageString) }
    }
  }

  data class LogMessage(val level: LogLevel, val message: String)

  enum class LogLevel : Comparable<LogLevel> {
    TRACE,
    DEBUG,
    INFO,
    WARN,
    ERROR,
    ;
  }

  private val logListener = mutableSetOf<(LogMessage) -> Unit>()

  private fun onLog(level: LogLevel, message: String) {
    val logMessage = LogMessage(level, message)
    logListener.forEach { it(logMessage) }
  }

  fun addLogListener(listener: (LogMessage) -> Unit) {
    logListener.add(listener)
  }

  fun removeLogListener(listener: (LogMessage) -> Unit) {
    logListener.remove(listener)
  }

  inline fun withLogListener(noinline listener: (LogMessage) -> Unit, block: () -> Unit) {
    addLogListener(listener)
    block()
    removeLogListener(listener)
  }

  inline fun withLogListener(level: LogLevel, noinline listener: (LogMessage) -> Unit, block: () -> Unit) {
    withLogListener({ logMessage ->
      if (logMessage.level >= level) {
        listener(logMessage)
      }
    }, block)
  }

}
