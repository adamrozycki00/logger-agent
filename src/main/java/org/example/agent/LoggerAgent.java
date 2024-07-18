package org.example.agent;

import static net.bytebuddy.matcher.ElementMatchers.named;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import org.example.agent.interceptor.Log4JInterceptor;

import java.lang.instrument.Instrumentation;

public class LoggerAgent {

  private static final String LOGGER_CLASS_TO_INTERCEPT = "org.apache.logging.log4j.core.Logger";
  private static final String LOGGER_METHOD_TO_INTERCEPT = "logIfEnabled";

  @SuppressWarnings("unused")
  public static void premain(String arguments, Instrumentation instrumentation) {
    loggerAgent().installOn(instrumentation);
  }

  private static AgentBuilder.Identified.Extendable loggerAgent() {
    return new AgentBuilder.Default()
        .type(named(LOGGER_CLASS_TO_INTERCEPT))
        .transform((builder, _1, _2, _3, _4) ->
            builder
                .method(named(LOGGER_METHOD_TO_INTERCEPT))
                .intercept(Advice.to(Log4JInterceptor.class)));
  }
}
