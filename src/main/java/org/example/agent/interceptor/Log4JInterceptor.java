package org.example.agent.interceptor;

import static java.util.Arrays.stream;
import static net.bytebuddy.asm.Advice.AllArguments;
import static net.bytebuddy.asm.Advice.OnMethodEnter;

import org.apache.logging.log4j.Level;
import org.example.agent.interceptor.logger.LoggerProvider;

import java.util.logging.Logger;

/**
 * Intercepts calls to the {@link org.apache.logging.log4j.spi.AbstractLogger#logIfEnabled} method,
 * which is directly called by regular logging methods such as {@link org.apache.logging.log4j.Logger#info}.
 * The interceptor uses an internal logger to log details about the caller of the Log4j logging method.
 */
public class Log4JInterceptor {

  public static final Logger LOGGER = LoggerProvider.getLogger();
  public static final String LOGGING_TMPL = "Intercepted logging: %s %s %s";
  public static final String CALL_LOCATION_TMPL = "%s:%d";
  public static final String ARG_INFO_TMPL = "arg: %s.toString => %s";
  public static final String NO_ARG_INFO = "(no arg detected)";
  public static final int LOGGER_STACK_INDEX = 2;
  public static final int CALLER_STACK_INDEX = 3;
  public static final int LOGGING_LEVEL_ARG_INDEX = 1;
  public static final int LOGGED_ARG_INDEX = 3;

  @OnMethodEnter
  @SuppressWarnings("unused")
  public static void enter(@AllArguments Object[] args) {
    var stackTrace = Thread.currentThread().getStackTrace();

    if (!detectedLoggingMethod(stackTrace)) {
      return;
    }

    var caller = stackTrace[CALLER_STACK_INDEX];
    var callLocation = getCallLocation(caller);
    var loggingLevel = (Level) args[LOGGING_LEVEL_ARG_INDEX];
    String argInfo = extractLoggedArgInfo(args);

    LOGGER.info(LOGGING_TMPL.formatted(callLocation, loggingLevel, argInfo));
  }

  public static String getCallLocation(StackTraceElement caller) {
    String fileName = caller.getFileName();
    int lineNum = caller.getLineNumber();
    return CALL_LOCATION_TMPL.formatted(fileName, lineNum);
  }

  public static boolean detectedLoggingMethod(StackTraceElement[] stackTrace) {
    if (stackTrace.length < LOGGER_STACK_INDEX) {
      return false;
    }

    var loggingCall = stackTrace[LOGGER_STACK_INDEX];
    String methodName = loggingCall.getMethodName();

    return isLoggingMethod(methodName);
  }

  public static boolean isLoggingMethod(String methodName) {
    return stream(Level.values())
        .map(Level::name)
        .anyMatch(levelName -> levelName.equalsIgnoreCase(methodName));
  }

  /**
   * Accepts arguments passed to the {@link org.apache.logging.log4j.spi.AbstractLogger#logIfEnabled} method
   * and guesses the argument coming from the {@link org.apache.logging.log4j.Logger}'s logging method.
   */
  public static String extractLoggedArgInfo(Object[] args) {
    if (args.length < LOGGED_ARG_INDEX) {
      return NO_ARG_INFO;
    }

    Object loggedArg = args[LOGGED_ARG_INDEX];
    String argClassName = loggedArg.getClass().getSimpleName();
    String argToString = loggedArg.toString();
    return ARG_INFO_TMPL.formatted(argClassName, argToString);
  }
}
