package org.example.agent.interceptor.logger;

import org.example.agent.LoggerAgent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Provides an internal logger for the agent.
 */
public class LoggerProvider {

  private static final String LOGGER_NAME = LoggerAgent.class.getSimpleName();
  private static final String LOG_FORMAT = "%s [%s] %s - %s%n";
  private static final Logger logger = Logger.getLogger(LOGGER_NAME);

  static {
    setupLogger();
  }

  public static Logger getLogger() {
    return logger;
  }

  private static void setupLogger() {
    var consoleHandler = new ConsoleHandler();
    consoleHandler.setFormatter(new CustomFormatter());
    logger.addHandler(consoleHandler);
    logger.setUseParentHandlers(false);  // Prevent output to other handlers
  }

  private static class CustomFormatter extends Formatter {
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat(DATE_FORMAT);

    @Override
    public String format(LogRecord record) {
      return LOG_FORMAT.formatted(
          DATE_FORMATTER.format(new Date(record.getMillis())),
          record.getLoggerName(),
          record.getLevel().getLocalizedName(),
          formatMessage(record));
    }
  }
}
