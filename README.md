# Logger Agent

A Java agent that demonstrates how to use Byte Buddy to manipulate the bytecode of another Java application.
It intercepts log4j logs of the application and prints them to the console.

## How to run

1. Clone the repository.

```shell
git clone https://github.com/adamrozycki00/logger-agent.git
```

2. Change directory to the root of the repository:

```shell
cd logger-agent
```

3. Build the agent:

```shell
./mvnw clean package
```

4. Locate the agent jar `target/logger-agent-1.0-SNAPSHOT.jar` (a fat jar) and note its full path.
5. Run the jar of another application that uses log4j, together with the agent:

```shell
java -javaagent:<agent-jar-full-path> -jar <application-jar>
```

6. Logs intercepted by the agent will be printed to the console.

## Architecture Decision Record

1. I chose Byte Buddy as the bytecode manipulation library because it has a fairly good
   documentation: https://bytebuddy.net/#/.
2. To intercept log4j logs, I used an agent-based approach as it does not require any changes to, or knowledge of, the
   application source code that is subject to instrumentation.
3. I chose to intercept the `logIfEnabled` method of the `Logger` class, as it is the method that is called by all
   regular logging methods in log4j, e.g. `info`, `error`, `warn`. By doing this, there is no need to intercept
   different logging methods but just one.
4. When it comes to intercepting and logging the information about the argument passed to the logging method, returned
   by its `toString` method, I decided to combine it together with the instrumentation of logging method rather than by
   using a separate agent for this purpose. The reasons for this are:
    1. Intercepting the `toString` method of all objects has a very significant performance overhead.
    2. As the `toString` method can be called in different contexts, retrieving information about the caller is not
       straightforward.

## Performance impact

I tested a sample application both with and without the agent to assess the performance impact, using IntelliJ Profiler
to measure running time.

* Running time with the agent: approx. 1300 ms
* running time without the agent: approx. 750 ms

The overhead of approximately 550 ms is largely attributable to the transform method of the Byte Buddy AgentBuilder.
This method increases the time taken to create the Logger by 415 ms, as shown
in [profile.png](src/test/resources/profile.png). Overall, the impact of the agent on performance is moderate,
particularly because the overhead primarily occurs during the initial phase of the application.

## Sources

While developing the solution, I referred to the following sources:

1. Byte Buddy documentation: https://bytebuddy.net/#/
2. Byte Buddy GitHub repository: https://github.com/raphw/byte-buddy
3. Byte Buddy javadoc: https://javadoc.io/doc/net.bytebuddy/byte-buddy/latest/index.html
4. Maven Repository: https://mvnrepository.com/
5. ChatGPT-4, for brainstorming ideas as well as proofreading plain English text.
6. GitHub Copilot, for generating straightforward parts of the code.
