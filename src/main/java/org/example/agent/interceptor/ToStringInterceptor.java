package org.example.agent.interceptor;

import static net.bytebuddy.asm.Advice.OnMethodExit;
import static net.bytebuddy.asm.Advice.Return;

import org.example.agent.interceptor.logger.LoggerProvider;

import java.util.logging.Logger;

public class ToStringInterceptor {

  public static final Logger LOGGER = LoggerProvider.getLogger();
  public static final String LOGGING_TMPL = "Intercepted toString: %s %s";
  public static final String TO_STRING_INFO_TMPL = "%s => %s";
  public static final int CALLER_STACK_INDEX = 1;

  @OnMethodExit
  @SuppressWarnings("unused")
  public static void exit(@Return(readOnly = false) String returnValue) {
    var stackTrace = Thread.currentThread().getStackTrace();
    var toStringCaller = stackTrace[CALLER_STACK_INDEX];
    var callLocation = Log4JInterceptor.getCallLocation(toStringCaller);
    var argInfo = getToStringInfo(toStringCaller, returnValue);
    LOGGER.info(LOGGING_TMPL.formatted(callLocation, argInfo));
  }

  public static String getToStringInfo(StackTraceElement e, String originalToString) {
    String toStringArg = e.getClassName() + "." + e.getMethodName();
    return TO_STRING_INFO_TMPL.formatted(toStringArg, originalToString);
  }
}
