# Logger Agent

A Java agent that demonstrates how to use Byte Buddy to manipulate the bytecode of another Java application.

## How to run

1. Clone the repository.
```shell
git clone https://github.com/adamrozycki00/logger-agent.git
```
2. Change directory to the root of the repository:
```shell
cd logger-agent
```
3. Run the following command to build the agent:
```shell
./mvnw clean package
```
4. Find the agent jar `target/logger-agent-1.0-SNAPSHOT.jar` and store its full path.
5. Run the jar of another application that uses log4j, together with the agent:
```shell
java -javaagent:<agent-jar-full-path> -jar <application-jar>
```
6. Logs intercepted by the agent will be printed to the console.
