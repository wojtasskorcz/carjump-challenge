package me.carjump.fetcher.services

sealed trait LogLevel

object LogLevels {
  case object Info extends LogLevel
  case object Error extends LogLevel
}

trait Logger {
  def log(lvl: LogLevel, msg: String)
}

object Logger extends Logger {
  var loggingBackend: Logger = _

  override def log(lvl: LogLevel, msg: String): Unit = loggingBackend.log(lvl, msg)
}

class PrintlnLogger extends Logger {
  override def log(lvl: LogLevel, msg: String): Unit = msg.split('\n').foreach(msgPart => println(s"[$lvl] $msgPart"))
}
