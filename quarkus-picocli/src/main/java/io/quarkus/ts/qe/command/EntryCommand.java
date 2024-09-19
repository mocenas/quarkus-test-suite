package io.quarkus.ts.qe.command;

import io.quarkus.logging.Log;
import io.quarkus.picocli.runtime.annotations.TopCommand;
import io.quarkus.runtime.Quarkus;
import io.quarkus.ts.qe.services.RestService;

import picocli.CommandLine;

@TopCommand
@CommandLine.Command(name = "customized-command", mixinStandardHelpOptions = true, subcommands = { HelloCommand.class,
        AgeCommand.class })
public class EntryCommand implements Runnable {
    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec;

    @Override
    public void run() {
        Log.info("Running command with name: " + spec.name());
        RestService.setConfiguredResponse("Default Entry Command");
        Quarkus.waitForExit();
    }
}
