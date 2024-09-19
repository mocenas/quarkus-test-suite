package io.quarkus.ts.qe.command;

import jakarta.inject.Inject;

import io.quarkus.logging.Log;
import io.quarkus.runtime.Quarkus;
import io.quarkus.ts.qe.services.HelloService;
import io.quarkus.ts.qe.services.RestService;

import picocli.CommandLine;

@CommandLine.Command(name = "greeting", mixinStandardHelpOptions = true)
public class HelloCommand implements Runnable {
    @CommandLine.Option(names = { "-n", "--name" }, description = "Who will we greet?", defaultValue = "World")
    String name;
    @Inject
    HelloService helloService;

    public HelloCommand() {
    }

    public HelloCommand(HelloService helloService) {
        this.helloService = helloService;
    }

    @Override
    public void run() {
        Log.info("Executing HelloCommand with name: " + name);
        RestService.setConfiguredResponse(helloService.greet(name));
        Quarkus.waitForExit();
    }

}
